library(vegan)
library(ggplot2)
library(gridExtra)
library(reshape2)
library(directlabels)

gradient <- function(x, target, bins){
  if(x >= target - bins & x < target + bins){y = 0}
  else if(x >= target - bins*2 & x < target-bins){ y =-1}
  else if(x >= target + bins & x < target + bins*2){ y = +1}
  else{y=2}
  return(y)
}

ApplyColor <- function(x, target=0, S.deviation=1, colors=c('grey','blue','red','blue','grey'), n=100){
  rbPal <- colorRampPalette(colors)
  adjust <- sort(rnorm(100,mean=target,sd=S.deviation))
  out <- rbPal(n)[as.numeric(cut(x,breaks = adjust))]
  out <- sapply(out, function(x) if(is.na(x)){return("#B6B6C0")}else{return(x)})
  return(out)
}

PlotRDA <- function(ccaData, testDF, ColorOption, labelText){
  arrowData <- data.frame(summary(ccaData)$biplot)
  arrowDataLabs <- rownames(arrowData)
  response <- data.frame(summary(ccaData)$species)
  p1 <- ggplot() + geom_point(data=testDF, aes(x=RDA1, y=RDA2), inherit.aes = F, col=ColorOption) +
    geom_vline(xintercept=0, linetype="dotted") + geom_hline(yintercept=0, linetype="dotted") + theme_minimal() + xlab("RDA1") + ylab("RDA2") +
    geom_segment(data=arrowData, aes(x=0, y=0, xend=RDA1, yend=RDA2), arrow=arrow(length=unit(0.2,"cm")), alpha=0.75, color="red") +
    geom_text(data=arrowData, aes(x=RDA1, y=RDA2, label=rownames(arrowData)), size = 3, vjust=0, color="red") + ggtitle(paste(labelText, sep="")) +
    geom_text(label=rownames(response), aes(x=response$RDA1, y=response$RDA2), size=3, color="black")
  return(p1)
}

# Function takes in the ccadata analysis and a formula
BuildOrdination <- function(formula, ModelParams, rdaData, testDF, p=10000, ColorOption="black", ColLow="red",ColHigh="darkred"){
  VectorStats <- envfit(formula, data = ModelParams, permutations=c(p))
  print(VectorStats)
  myR2 <- paste("r-squared: ", round(VectorStats$vectors$r[1],4), sep="")
  myPval <- paste("P-Val: ", round(VectorStats$vectors$pvals[1],4), sep = "")
  outText <- paste(myR2, myPval, sep="\n")
  OrdiSurface <- ordisurf(formula, data = ModelParams, plot = FALSE)

  surface <- OrdiSurface$grid
  xdf <- surface[[1]]
  ydf <- surface[[2]]
  zdf <- as.matrix(surface[[3]])
  colnames(zdf) <- ydf
  rownames(zdf) <- xdf
  outDF <- melt(zdf)
  colnames(outDF) <- c("x","y","z")

  arrowData <- data.frame(summary(rdaData)$biplot)
  arrowDataLabs <- rownames(arrowData)
  response <- data.frame(summary(rdaData)$species)

  p1 <- ggplot() +
    geom_vline(xintercept=0, linetype="dotted") + geom_hline(yintercept=0, linetype="dotted") +
    geom_point(data=testDF, aes(RDA1,RDA2), color=ColorOption, inherit.aes = F) +
    geom_contour(data=outDF, aes(x=x,y=y,z=z, colour=..level..), show.legend=F) + scale_colour_gradient(low = ColLow, high = ColHigh) +
    geom_segment(data=arrowData, aes(x=0, y=0, xend=RDA1, yend=RDA2), arrow=arrow(length=unit(0.2,"cm")), alpha=0.75, color="black") +
    #geom_text(data=arrowData, aes(x=RDA1, y=RDA2, label=rownames(arrowData)), size = 3, vjust=0, color="black") + ggtitle(paste("", sep="")) +
    #geom_text(label=rownames(response), aes(x=response$RDA1, y=response$RDA2), size=3, color="black") +
    theme_minimal() + ggtitle(Reduce(paste, deparse(formula)))

  p1 <- direct.label(p1,"bottom.pieces")
  scales <- layer_scales(p1)
  x <- scales$x$range$range[2]
  y <- scales$y$range$range[2]

  p1 <- p1 + annotate("text", x = x-0.5, y = y-0.5, label = outText)
  return(p1)
}

OrdiPlot <- function(df){
  dfOutcome <- df[9:11]
  dfOutcome$height <- as.numeric(dfOutcome$height)
  dfOutcome$rlambda <- as.numeric(dfOutcome$rlambda)
  dfOutcome$mean <- as.numeric(dfOutcome$mean)
  #dfOutcome$heal <- as.numeric(lapply(dfOutcome$heal, function(x) round(x,0)))
  dfInput <- df[1:8]
  #dfInput <- log(df[,1:6]+1)

  rdaData <- rda(dfOutcome ~ PSF+EGF_CONS+APOPEGF+DEATHPROB+MOVE+DIVLOCPROB+EGF_DIFFUSION_RATE+EGFDecayRate, data=dfInput, scale=TRUE)
  p <- plot(rdaData)
  testDF <- as.data.frame(p$sites)
  atts <- attributes(p$biplot)
  xR2Vals <- as.data.frame(p$sites)
  myFact = atts$arrow.mul
  print(myFact)
  #xLoc <- max(xR2Vals$CCA1) + 2
  xLoc <- 2.2

  testDF <- cbind(testDF, data.frame(rlambda=dfOutcome$rlambda, rlambdaSat=ApplyColor(dfOutcome$rlambda, 0.02404, 0.5)))
  testDF <- cbind(testDF, data.frame(mean=dfOutcome$mean, meanSat=ApplyColor(dfOutcome$mean, 29,2)))
  testDF <- cbind(testDF, data.frame(mean=dfOutcome$height, HeightSat=ApplyColor(dfOutcome$height, 14,2)))

  #plot.new()
  #par(mfrow=c(3,4))
  #plot(ccaData, type="n", axes = TRUE, frame.plot=TRUE, cex=2/3, main="RDA: Height")#, ylim = c(-2,2), xlim=c(-3,3))
  #points(testDF$RDA1, testDF$RDA2, col=testDF$HeightSat, cex=2/5)
  #text(ccaData, display = "bp", cex=2/3, axis.bp=FALSE, col="blue")
  #legend(-3,2,unique(testDF$height),col=1:length(testDF$height),pch=1)

  p1 <- PlotRDA(rdaData, testDF, testDF$HeightSat, "Height")

  #plot(ccaData, type="n", axes = TRUE, frame.plot=TRUE, cex=2/3, main="RDA: Age")#, ylim = c(-2,2), xlim=c(-3,3))
  #points(testDF$RDA1, testDF$RDA2, col=testDF$AgeSat, cex=2/5)
  #text(ccaData, display = "bp", cex=2/3, axis.bp=FALSE, col="blue")
  #legend(-3,2,unique(testDF$height),col=1:length(testDF$height),pch=1)
  p2 <- PlotRDA(rdaData, testDF, testDF$meanSat, "Age")

  #plot(ccaData, type="n", axes = TRUE, frame.plot=TRUE, cex=2/3, main="RDA: rlambda")#, ylim = c(-2,2), xlim=c(-3,3))
  #points(testDF$RDA1, testDF$RDA2, col=testDF$rlambdaSat, cex=2/5)
  #text(ccaData, display = "bp", cex=2/3, axis.bp=FALSE, col="blue")
  #legend(-3,2,unique(testDF$height),col=1:length(testDF$height),pch=1)
  p3 <- PlotRDA(rdaData, testDF, testDF$rlambdaSat, "rlambda")

  grob_responses=arrangeGrob(p1, p2, p3, ncol=3, nrow=1)

  #plot(ccaData, type="n", axes = TRUE, frame.plot=TRUE, cex=2/3, main="RDA: Heal Time", ylim = c(-2,2), xlim=c(-3,3))
  #points(testDF$RDA1, testDF$RDA2, col=testDF$healSat, cex=2/5)
  #text(ccaData, display = "bp", cex=2/3, axis.bp=FALSE, col="blue")
  #legend(-3,2,unique(testDF$height),col=1:length(testDF$height),pch=1)


  p4 <- BuildOrdination(rdaData~PSF, dfInput, rdaData, testDF, 10000, "lightgrey")
  p5 <- BuildOrdination(rdaData~APOPEGF, dfInput, rdaData, testDF, 10000, "lightgrey")
  p6 <- BuildOrdination(rdaData~EGF_CONS, dfInput, rdaData, testDF,10000, "lightgrey")
  p7 <- BuildOrdination(rdaData~MOVE, dfInput, rdaData, testDF,10000, "lightgrey")
  p8 <- BuildOrdination(rdaData~DIVLOCPROB, dfInput, rdaData, testDF,10000, "lightgrey")
  p9 <- BuildOrdination(rdaData~DEATHPROB, dfInput, rdaData, testDF,10000, "lightgrey")
  p10 <- BuildOrdination(rdaData~EGF_DIFFUSION_RATE, dfInput, rdaData, testDF, 10000, "lightgrey")
  p11 <- BuildOrdination(rdaData~EGFDecayRate, dfInput, rdaData, testDF,10000, "lightgrey")

  grob_params=arrangeGrob(p4, p5, p6, p7, p8, p9, p10, p11, ncol=3, nrow=3)

  # plot.new()
  # par(mfrow=c(2,4))
  # VectorStats <- envfit(ccaData ~ PSF, data = dfInput, permutations=c(10000))
  # print(VectorStats)
  # myR2 <- paste("r2 = ", round(VectorStats$vectors$r[1],4), sep="")
  # myPval <- paste("p-value = ", round(VectorStats$vectors$pvals[1],4), sep = "")
  # outText <- paste(myR2, myPval, sep="\n")
  # OrdiSurface <- ordisurf(ccaData ~ PSF, data = dfInput, plot = FALSE)
  # plot(ccaData, type="n", axes=FALSE, frame.plot=TRUE, cex=2/3, xlab="RDA1", ylab="RDA2", main="PSF")
  # plot(OrdiSurface, add=TRUE, col="red")
  # text(ccaData, display="species")
  # text(ccaData, display = "bp", cex=2/3, axis.bp=FALSE, col="blue", select = c("PSF"), arrow.mul = myFact)
  # text(xLoc,0, labels = c(outText))
  # rm(myR2, myPval, VectorStats)
  #
  # VectorStats <- envfit(ccaData ~ APOPEGF, data = dfInput, permutations=c(1000))
  # print(VectorStats)
  # myR2 <- paste("r2 = ", round(VectorStats$vectors$r[1],4), sep="")
  # myPval <- paste("p-value = ", round(VectorStats$vectors$pvals[1],4), sep = "")
  # outText <- paste(myR2, myPval, sep="\n")
  # OrdiSurface <- ordisurf(ccaData ~ APOPEGF, data = dfInput, plot = FALSE)
  # plot(ccaData, type="n", axes=FALSE, frame.plot=TRUE, cex=2/3, xlab="RDA1", ylab="RDA2", main="APOPEGF")
  # plot(OrdiSurface, add=TRUE, col="red")
  # text(ccaData, display="species")
  # text(ccaData, display = "bp", cex=2/3, axis.bp=FALSE, col="blue", select = c("APOPEGF"), arrow.mul = myFact)
  # text(xLoc,0, labels = c(outText))
  # rm(myR2, myPval, VectorStats)
  #
  # VectorStats <- envfit(ccaData ~ EGF_CONS, data = dfInput, permutations=c(1000))
  # print(VectorStats)
  # myR2 <- paste("r2 = ", round(VectorStats$vectors$r[1],4), sep="")
  # myPval <- paste("p-value = ", round(VectorStats$vectors$pvals[1],4), sep = "")
  # outText <- paste(myR2, myPval, sep="\n")
  # OrdiSurface <- ordisurf(ccaData ~ EGF_CONS, data = dfInput, plot = FALSE)
  # plot(ccaData, type="n", axes=FALSE, frame.plot=TRUE, cex=2/3, xlab="RDA1", ylab="RDA2", main="EGF CONSUMPTION")
  # plot(OrdiSurface, add=TRUE, col="red")
  # text(ccaData, display="species")
  # text(ccaData, display = "bp", cex=2/3, axis.bp=FALSE, col="blue", select = c("EGF_CONS"), arrow.mul = myFact)
  # text(xLoc,0, labels = c(outText))
  # rm(myR2, myPval, VectorStats)
  #
  # VectorStats <- envfit(ccaData ~ MOVE, data = dfInput, permutations=c(1000))
  # print(VectorStats)
  # myR2 <- paste("r2 = ", round(VectorStats$vectors$r[1],4), sep="")
  # myPval <- paste("p-value = ", round(VectorStats$vectors$pvals[1],4), sep = "")
  # outText <- paste(myR2, myPval, sep="\n")
  # OrdiSurface <- ordisurf(ccaData ~ MOVE, data = dfInput, plot = FALSE)
  # plot(ccaData, type="n", axes=FALSE, frame.plot=TRUE, cex=2/3, xlab="RDA1", ylab="RDA2", main="MOVE")
  # plot(OrdiSurface, add=TRUE, col="red")
  # text(ccaData, display="species")
  # text(ccaData, display = "bp", cex=2/3, axis.bp=FALSE, col="blue", select = c("MOVE"), arrow.mul = myFact)
  # text(xLoc,0, labels = c(outText))
  # rm(myR2, myPval, VectorStats)
  #
  # VectorStats <- envfit(ccaData ~ DIVLOCPROB, data = dfInput, permutations=c(1000))
  # print(VectorStats)
  # myR2 <- paste("r2 = ", round(VectorStats$vectors$r[1],4), sep="")
  # myPval <- paste("p-value = ", round(VectorStats$vectors$pvals[1],4), sep = "")
  # outText <- paste(myR2, myPval, sep="\n")
  # OrdiSurface <- ordisurf(ccaData ~ DIVLOCPROB, data = dfInput, plot = FALSE)
  # plot(ccaData, type="n", axes=FALSE, frame.plot=TRUE, cex=2/3, xlab="RDA1", ylab="RDA2", main="DIVLOCPROB")
  # plot(OrdiSurface, add=TRUE, col="red")
  # text(ccaData, display="species")
  # text(ccaData, display = "bp", cex=2/3, axis.bp=FALSE, col="blue", select = c("DIVLOCPROB"), arrow.mul = myFact)
  # text(xLoc,0, labels = c(outText))
  # rm(myR2, myPval, VectorStats)
  #
  # VectorStats <- envfit(ccaData ~ DEATHPROB, data = dfInput, permutations=c(1000))
  # print(VectorStats)
  # myR2 <- paste("r2 = ", round(VectorStats$vectors$r[1],4), sep="")
  # myPval <- paste("p-value = ", round(VectorStats$vectors$pvals[1],4), sep = "")
  # outText <- paste(myR2, myPval, sep="\n")
  # OrdiSurface <- ordisurf(ccaData ~ DEATHPROB, data = dfInput, plot = FALSE)
  # plot(ccaData, type="n", axes=FALSE, frame.plot=TRUE, cex=2/3, xlab="RDA1", ylab="RDA2", main="DEATHPROB")
  # plot(OrdiSurface, add=TRUE, col="red")
  # text(ccaData, display="species")
  # text(ccaData, display = "bp", cex=2/3, axis.bp=FALSE, col="blue", select = c("DEATHPROB"), arrow.mul = myFact)
  # text(xLoc,0, labels = c(outText))
  # rm(myR2, myPval, VectorStats)
  #
  # VectorStats <- envfit(ccaData ~ EGF_DIFFUSION_RATE, data = dfInput, permutations=c(1000))
  # print(VectorStats)
  # myR2 <- paste("r2 = ", round(VectorStats$vectors$r[1],4), sep="")
  # myPval <- paste("p-value = ", round(VectorStats$vectors$pvals[1],4), sep = "")
  # outText <- paste(myR2, myPval, sep="\n")
  # OrdiSurface <- ordisurf(ccaData ~ EGF_DIFFUSION_RATE, data = dfInput, plot = FALSE)
  # plot(ccaData, type="n", axes=FALSE, frame.plot=TRUE, cex=2/3, xlab="RDA1", ylab="RDA2", main="EGFDiffusionRate")
  # plot(OrdiSurface, add=TRUE, col="red")
  # text(ccaData, display="species")
  # text(ccaData, display = "bp", cex=2/3, axis.bp=FALSE, col="blue", select = c("EGF_DIFFUSION_RATE"), arrow.mul = myFact)
  # text(xLoc,0, labels = c(outText))
  # rm(myR2, myPval, VectorStats)
  #
  # VectorStats <- envfit(ccaData ~ EGFDecayRate, data = dfInput, permutations=c(1000))
  # print(VectorStats)
  # myR2 <- paste("r2 = ", round(VectorStats$vectors$r[1],4), sep="")
  # myPval <- paste("p-value = ", round(VectorStats$vectors$pvals[1],4), sep = "")
  # outText <- paste(myR2, myPval, sep="\n")
  # OrdiSurface <- ordisurf(ccaData ~ EGFDecayRate, data = dfInput, plot = FALSE)
  # plot(ccaData, type="n", axes=FALSE, frame.plot=TRUE, cex=2/3, xlab="RDA1", ylab="RDA2", main="EGF Decay Rate")
  # plot(OrdiSurface, add=TRUE, col="red")
  # text(ccaData, display="species")
  # text(ccaData, display = "bp", cex=2/3, axis.bp=FALSE, col="blue", select = c("EGFDecayRate"), arrow.mul = myFact)
  # text(xLoc,0, labels = c(outText))
  # rm(myR2, myPval, VectorStats)
  #return(grob_responses)
  return(list(grob_responses,grob_params))
}

PrepDF <- function(df){
  df <- na.omit(df)
  df$V2 <- as.numeric(lapply(df$V2, function(x) x*-1))
  colnames(df) <- c("PSF", "EGF_CONS","APOPEGF","DEATHPROB","MOVE","DIVLOCPROB","EGF_DIFFUSION_RATE","EGFDecayRate","rlambda","mean", "height")
  return(df)
}

#setwd("~/IdeaProjects/Epidermis_Project_Final/")
setwd("~/Desktop/Darryl_collab/Framework/Homeostatic_Epidermis/")
iteration <- 11
inputFile <- paste("GridParams_Round",iteration,".txt",sep="")
ResponsePlot <- paste("Iteration",iteration,"_Responses.png",sep="")
SurfacePlots <- paste("Iteration",iteration,".png",sep="")
df <- read.csv(inputFile, sep = "\t", header = FALSE, na.strings = "NaN")
cleanDF <- PrepDF(df)
summary(cleanDF)
g <- OrdiPlot(cleanDF) # Use this to get Ordination Plots and CCA plots
do.call("grid.arrange", g[1])
ggsave(ResponsePlot, do.call("grid.arrange", g[1]), width=10,height=5,dpi=300,units = "in")
do.call("grid.arrange", g[2])
ggsave(SurfacePlots, do.call("grid.arrange", g[2]), width=10,height=12,dpi=300,units="in")

boxplot(cleanDF[1:8])


