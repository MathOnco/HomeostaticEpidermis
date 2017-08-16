library(vegan)
library(ggplot2)
library(gridExtra)

gradient <- function(x, target, bins){
  if(x >= target - bins & x < target + bins){y = 0}
  else if(x >= target - bins*2 & x < target-bins){ y =-1}
  else if(x >= target + bins & x < target + bins*2){ y = +1}
  else{y=2}
  return(y)
}

ApplyColor <- function(x, target){
  rbPal <- colorRampPalette(c('grey','blue','red','blue','grey'))
  adjust <- sort(rnorm(100,mean=target,sd=0.5))
  out <- rbPal(100)[as.numeric(cut(x,breaks = adjust))]
  out <- sapply(out, function(x) if(is.na(x)){return("#B6B6C0")}else{return(x)})
  return(out)
}

PlotRDA <- function(ccaData, testDF, ColorOption, labelText){
  arrowData <- data.frame(summary(ccaData)$biplot)
  arrowDataLabs <- rownames(arrowData)
  p1 <- ggplot() + geom_point(data=testDF, aes(x=RDA1, y=RDA2), inherit.aes = F, col=ColorOption) + 
    geom_vline(xintercept=0, linetype="dotted") + geom_hline(yintercept=0, linetype="dotted") + theme_minimal() + xlab("RDA1") + ylab("RDA2") +
    geom_segment(data=arrowData, aes(x=0, y=0, xend=RDA1, yend=RDA2), arrow=arrow(length=unit(0.2,"cm")), alpha=0.75, color="red") + 
    geom_text(data=arrowData, aes(x=RDA1, y=RDA2, label=rownames(arrowData)), size = 3, vjust=0, color="red") + ggtitle(paste(labelText, sep=""))
  return(p1)
}
  
OrdiPlot <- function(df){
  dfOutcome <- cleanDF[9:11]
  dfOutcome$height <- as.numeric(dfOutcome$height)
  dfOutcome$rlambda <- as.numeric(dfOutcome$rlambda)
  dfOutcome$mean <- as.numeric(dfOutcome$mean)
  #dfOutcome$heal <- as.numeric(lapply(dfOutcome$heal, function(x) round(x,0)))
  dfInput <- cleanDF[1:8]
  #dfInput <- log(df[,1:6]+1)
  
  ccaData <- rda(dfOutcome ~ PSF+EGF_CONS+APOPEGF+DEATHPROB+MOVE+DIVLOCPROB+EGF_DIFFUSION_RATE+EGFDecayRate, data=dfInput, scale=TRUE)
  p <- plot(ccaData)
  testDF <- as.data.frame(p$sites)
  atts <- attributes(p$biplot)
  xR2Vals <- as.data.frame(p$sites)
  myFact = atts$arrow.mul
  print(myFact)
  #xLoc <- max(xR2Vals$CCA1) + 2
  xLoc <- 2.2
  
  testDF <- cbind(testDF, data.frame(rlambda=dfOutcome$rlambda, rlambdaSat=ApplyColor(dfOutcome$rlambda, 0.02404)))
  testDF <- cbind(testDF, data.frame(mean=dfOutcome$rlambda, meanSat=ApplyColor(dfOutcome$mean, 29)))
  testDF <- cbind(testDF, data.frame(mean=dfOutcome$height, HeightSat=ApplyColor(dfOutcome$height, 14)))
  
  #plot.new()
  #par(mfrow=c(3,4))
  #plot(ccaData, type="n", axes = TRUE, frame.plot=TRUE, cex=2/3, main="RDA: Height")#, ylim = c(-2,2), xlim=c(-3,3))
  #points(testDF$RDA1, testDF$RDA2, col=testDF$HeightSat, cex=2/5)
  #text(ccaData, display = "bp", cex=2/3, axis.bp=FALSE, col="blue")
  #legend(-3,2,unique(testDF$height),col=1:length(testDF$height),pch=1)
  
  p1 <- PlotRDA(ccaData, testDF, testDF$HeightSat, "Height")

  #plot(ccaData, type="n", axes = TRUE, frame.plot=TRUE, cex=2/3, main="RDA: Age")#, ylim = c(-2,2), xlim=c(-3,3))
  #points(testDF$RDA1, testDF$RDA2, col=testDF$AgeSat, cex=2/5)
  #text(ccaData, display = "bp", cex=2/3, axis.bp=FALSE, col="blue")
  #legend(-3,2,unique(testDF$height),col=1:length(testDF$height),pch=1)
  p2 <- PlotRDA(ccaData, testDF, testDF$meanSat, "Age")
  
  #plot(ccaData, type="n", axes = TRUE, frame.plot=TRUE, cex=2/3, main="RDA: rlambda")#, ylim = c(-2,2), xlim=c(-3,3))
  #points(testDF$RDA1, testDF$RDA2, col=testDF$rlambdaSat, cex=2/5)
  #text(ccaData, display = "bp", cex=2/3, axis.bp=FALSE, col="blue")
  #legend(-3,2,unique(testDF$height),col=1:length(testDF$height),pch=1)
  p3 <- PlotRDA(ccaData, testDF, testDF$rlambdaSat, "rlambda")
  
  grob_all=arrangeGrob(p1, p2, p3, ncol=3)
  
  #plot(ccaData, type="n", axes = TRUE, frame.plot=TRUE, cex=2/3, main="RDA: Heal Time", ylim = c(-2,2), xlim=c(-3,3))
  #points(testDF$RDA1, testDF$RDA2, col=testDF$healSat, cex=2/5)
  #text(ccaData, display = "bp", cex=2/3, axis.bp=FALSE, col="blue")
  #legend(-3,2,unique(testDF$height),col=1:length(testDF$height),pch=1)
  
  plot.new()
  par(mfrow=c(2,4))
  VectorStats <- envfit(ccaData ~ PSF, data = dfInput, permutations=c(10000))
  print(VectorStats)
  myR2 <- paste("r2 = ", round(VectorStats$vectors$r[1],4), sep="")
  myPval <- paste("p-value = ", round(VectorStats$vectors$pvals[1],4), sep = "")
  outText <- paste(myR2, myPval, sep="\n")
  OrdiSurface <- ordisurf(ccaData ~ PSF, data = dfInput, plot = FALSE)
  plot(ccaData, type="n", axes=FALSE, frame.plot=TRUE, cex=2/3, xlab="RDA1", ylab="RDA2", main="PSF")
  plot(OrdiSurface, add=TRUE, col="red")
  text(ccaData, display="species")
  text(ccaData, display = "bp", cex=2/3, axis.bp=FALSE, col="blue", select = c("PSF"), arrow.mul = myFact)
  text(xLoc,0, labels = c(outText))
  rm(myR2, myPval, VectorStats)
  
  VectorStats <- envfit(ccaData ~ APOPEGF, data = dfInput, permutations=c(1000))
  print(VectorStats)
  myR2 <- paste("r2 = ", round(VectorStats$vectors$r[1],4), sep="")
  myPval <- paste("p-value = ", round(VectorStats$vectors$pvals[1],4), sep = "")
  outText <- paste(myR2, myPval, sep="\n")
  OrdiSurface <- ordisurf(ccaData ~ APOPEGF, data = dfInput, plot = FALSE)
  plot(ccaData, type="n", axes=FALSE, frame.plot=TRUE, cex=2/3, xlab="RDA1", ylab="RDA2", main="APOPEGF")
  plot(OrdiSurface, add=TRUE, col="red")
  text(ccaData, display="species")
  text(ccaData, display = "bp", cex=2/3, axis.bp=FALSE, col="blue", select = c("APOPEGF"), arrow.mul = myFact)
  text(xLoc,0, labels = c(outText))
  rm(myR2, myPval, VectorStats)
  
  VectorStats <- envfit(ccaData ~ EGF_CONS, data = dfInput, permutations=c(1000))
  print(VectorStats)
  myR2 <- paste("r2 = ", round(VectorStats$vectors$r[1],4), sep="")
  myPval <- paste("p-value = ", round(VectorStats$vectors$pvals[1],4), sep = "")
  outText <- paste(myR2, myPval, sep="\n")
  OrdiSurface <- ordisurf(ccaData ~ EGF_CONS, data = dfInput, plot = FALSE)
  plot(ccaData, type="n", axes=FALSE, frame.plot=TRUE, cex=2/3, xlab="RDA1", ylab="RDA2", main="EGF CONSUMPTION")
  plot(OrdiSurface, add=TRUE, col="red")
  text(ccaData, display="species")
  text(ccaData, display = "bp", cex=2/3, axis.bp=FALSE, col="blue", select = c("EGF_CONS"), arrow.mul = myFact)
  text(xLoc,0, labels = c(outText))
  rm(myR2, myPval, VectorStats)
  
  VectorStats <- envfit(ccaData ~ MOVE, data = dfInput, permutations=c(1000))
  print(VectorStats)
  myR2 <- paste("r2 = ", round(VectorStats$vectors$r[1],4), sep="")
  myPval <- paste("p-value = ", round(VectorStats$vectors$pvals[1],4), sep = "")
  outText <- paste(myR2, myPval, sep="\n")
  OrdiSurface <- ordisurf(ccaData ~ MOVE, data = dfInput, plot = FALSE)
  plot(ccaData, type="n", axes=FALSE, frame.plot=TRUE, cex=2/3, xlab="RDA1", ylab="RDA2", main="MOVE")
  plot(OrdiSurface, add=TRUE, col="red")
  text(ccaData, display="species")
  text(ccaData, display = "bp", cex=2/3, axis.bp=FALSE, col="blue", select = c("MOVE"), arrow.mul = myFact)
  text(xLoc,0, labels = c(outText))
  rm(myR2, myPval, VectorStats)
  
  VectorStats <- envfit(ccaData ~ DIVLOCPROB, data = dfInput, permutations=c(1000))
  print(VectorStats)
  myR2 <- paste("r2 = ", round(VectorStats$vectors$r[1],4), sep="")
  myPval <- paste("p-value = ", round(VectorStats$vectors$pvals[1],4), sep = "")
  outText <- paste(myR2, myPval, sep="\n")
  OrdiSurface <- ordisurf(ccaData ~ DIVLOCPROB, data = dfInput, plot = FALSE)
  plot(ccaData, type="n", axes=FALSE, frame.plot=TRUE, cex=2/3, xlab="RDA1", ylab="RDA2", main="DIVLOCPROB")
  plot(OrdiSurface, add=TRUE, col="red")
  text(ccaData, display="species")
  text(ccaData, display = "bp", cex=2/3, axis.bp=FALSE, col="blue", select = c("DIVLOCPROB"), arrow.mul = myFact)
  text(xLoc,0, labels = c(outText))
  rm(myR2, myPval, VectorStats)
  
  VectorStats <- envfit(ccaData ~ DEATHPROB, data = dfInput, permutations=c(1000))
  print(VectorStats)
  myR2 <- paste("r2 = ", round(VectorStats$vectors$r[1],4), sep="")
  myPval <- paste("p-value = ", round(VectorStats$vectors$pvals[1],4), sep = "")
  outText <- paste(myR2, myPval, sep="\n")
  OrdiSurface <- ordisurf(ccaData ~ DEATHPROB, data = dfInput, plot = FALSE)
  plot(ccaData, type="n", axes=FALSE, frame.plot=TRUE, cex=2/3, xlab="RDA1", ylab="RDA2", main="DEATHPROB")
  plot(OrdiSurface, add=TRUE, col="red")
  text(ccaData, display="species")
  text(ccaData, display = "bp", cex=2/3, axis.bp=FALSE, col="blue", select = c("DEATHPROB"), arrow.mul = myFact)
  text(xLoc,0, labels = c(outText))
  rm(myR2, myPval, VectorStats)
  
  VectorStats <- envfit(ccaData ~ EGF_DIFFUSION_RATE, data = dfInput, permutations=c(1000))
  print(VectorStats)
  myR2 <- paste("r2 = ", round(VectorStats$vectors$r[1],4), sep="")
  myPval <- paste("p-value = ", round(VectorStats$vectors$pvals[1],4), sep = "")
  outText <- paste(myR2, myPval, sep="\n")
  OrdiSurface <- ordisurf(ccaData ~ EGF_DIFFUSION_RATE, data = dfInput, plot = FALSE)
  plot(ccaData, type="n", axes=FALSE, frame.plot=TRUE, cex=2/3, xlab="RDA1", ylab="RDA2", main="EGFDiffusionRate")
  plot(OrdiSurface, add=TRUE, col="red")
  text(ccaData, display="species")
  text(ccaData, display = "bp", cex=2/3, axis.bp=FALSE, col="blue", select = c("EGF_DIFFUSION_RATE"), arrow.mul = myFact)
  text(xLoc,0, labels = c(outText))
  rm(myR2, myPval, VectorStats)
  
  VectorStats <- envfit(ccaData ~ EGFDecayRate, data = dfInput, permutations=c(1000))
  print(VectorStats)
  myR2 <- paste("r2 = ", round(VectorStats$vectors$r[1],4), sep="")
  myPval <- paste("p-value = ", round(VectorStats$vectors$pvals[1],4), sep = "")
  outText <- paste(myR2, myPval, sep="\n")
  OrdiSurface <- ordisurf(ccaData ~ EGFDecayRate, data = dfInput, plot = FALSE)
  plot(ccaData, type="n", axes=FALSE, frame.plot=TRUE, cex=2/3, xlab="RDA1", ylab="RDA2", main="EGF Decay Rate")
  plot(OrdiSurface, add=TRUE, col="red")
  text(ccaData, display="species")
  text(ccaData, display = "bp", cex=2/3, axis.bp=FALSE, col="blue", select = c("EGFDecayRate"), arrow.mul = myFact)
  text(xLoc,0, labels = c(outText))
  rm(myR2, myPval, VectorStats)
  
  return(grob_all)
}

PrepDF <- function(df){
  df <- na.omit(df)
  df$V2 <- as.numeric(lapply(df$V2, function(x) x*-1))
  colnames(df) <- c("PSF", "EGF_CONS","APOPEGF","DEATHPROB","MOVE","DIVLOCPROB","EGF_DIFFUSION_RATE","EGFDecayRate","rlambda","mean", "height")
  return(df)
}


setwd("~/IdeaProjects/Epidermis_Project_Final/")
df <- read.csv("GridParams_Round2.txt", sep = "\t", header = FALSE, na.strings = "NaN")
cleanDF <- PrepDF(df)
summary(cleanDF)
g <- OrdiPlot(cleanDF) # Use this to get Ordination Plots and CCA plots
do.call("grid.arrange", g)

boxplot(cleanDF[1:8])




