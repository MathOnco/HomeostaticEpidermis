library(ggplot2)
library(vegan)

###### Functions! #####
OrdRange <- function(q){
  max <- q*2
  min <- q/2
  print(min)
  print(max-min)
}

gradient <- function(x, target, bins){
  if(x >= target - bins & x < target + bins){y = 0}
  else if(x >= target - bins*2 & x < target-bins){ y =-1}
  else if(x >= target + bins & x < target + bins*2){ y = +1}
  else{y=2}
  return(y)
}

getColored <- function(dfInput, dfOutcome, testDF){
  outputDF <- as.data.frame(matrix(nrow=length(dfInput[,1]), ncol=4))
  colnames(outputDF) <- c("HeightSat", "AgeSat", "rlambdaSat", "healSat")
  
  for( i in 1:length(dfInput$PSF)){
    dfOutcomeTest <- dfOutcome[i,]
    if(gradient(dfOutcomeTest$rlambda, 0.16, 0.04)==0){outputDF[i,3]="red"}
      else if(gradient(dfOutcomeTest$rlambda, 0.16, 0.04)==-1 || gradient(dfOutcomeTest$rlambda, 0.16, 0.04)==+1){outputDF[i,3]="royalblue4"}
      else{outputDF[i,3]="grey"}
    if(gradient(dfOutcomeTest$mean, 28, 4)==0){outputDF[i,2]="red"}
      else if(gradient(dfOutcomeTest$mean, 28, 4)==-1 || gradient(dfOutcomeTest$mean, 28, 4)==+1){outputDF[i,2]="royalblue4"}
      else{outputDF[i,2]="grey"}
    if(gradient(dfOutcomeTest$height, 14, 2)==0){outputDF[i,1]="red"}
      else if(gradient(dfOutcomeTest$height, 14, 2)==-1 || gradient(dfOutcomeTest$height, 14, 2)==+1){outputDF[i,1]="royalblue4"}
      else{outputDF[i,2]="grey"}
    if(gradient(dfOutcomeTest$heal, 2, 8)==0){outputDF[i,4]="red"}
      else if(gradient(dfOutcomeTest$heal, 2, 8)==-1 || gradient(dfOutcomeTest$heal, 2, 8)==+1){outputDF[i,4]="royalblue4"}
      else{outputDF[i,4]="grey"}
  }
  dfOutcomeVariables <- cbind(dfOutcome, outputDF)
  testDF <- cbind(testDF, dfOutcomeVariables)
  return(testDF)
}

OrdiPlot <- function(df){
  dfOutcome <- df[7:10]
  dfOutcome$height <- as.numeric(lapply(dfOutcome$height, function(x) round(x,0)))
  dfOutcome$rlambda <- as.numeric(lapply(dfOutcome$rlambda, function(x) round(x,2)))
  dfOutcome$mean <- as.numeric(lapply(dfOutcome$mean, function(x) round(x,0)))
  dfOutcome$heal <- as.numeric(lapply(dfOutcome$heal, function(x) round(x,0)))
  dfInput <- df[1:6]
  #dfInput <- log(df[,1:6]+1)
  
  ccaData <- rda(dfOutcome ~ PSF+EGF_CONS+APOPEGF+DEATHPROB+MOVE+DIVLOCPROB, data=dfInput, scale=TRUE)
  p <- plot(ccaData)
  testDF <- as.data.frame(p$sites)
  atts <- attributes(p$biplot)
  xR2Vals <- as.data.frame(p$sites)
  myFact = atts$arrow.mul
  print(myFact)
  #xLoc <- max(xR2Vals$CCA1) + 2
  xLoc <- 2.2
  
  testDF <- getColored(dfInput, dfOutcome, testDF)
  
  plot.new()
  par(mfrow=c(3,4))
  #plot(ccaData, type="n", axes = TRUE, frame.plot=TRUE, cex=2/3, main="Canonical Correspondence Analysis")
  #points(ccaData, display = "sites", cex=1/10)
  #text(ccaData, display = "bp", cex=2/3, axis.bp=FALSE, col="blue")
  #plot(ccaData, type="n", axes = TRUE, frame.plot=TRUE, cex=2/3, main="Canonical Correspondence Analysis", ylim = c(-2,2), xlim=c(-3,3))
  #points(ccaData, display = "sites", cex=1/3)
  #text(ccaData, display = "bp", cex=2/3, axis.bp=FALSE, col="blue")
  plot(ccaData, type="n", axes = TRUE, frame.plot=TRUE, cex=2/3, main="RDA: Height", ylim = c(-2,2), xlim=c(-3,3))
  points(testDF$RDA1, testDF$RDA2, col=testDF$HeightSat, cex=2/5)
  text(ccaData, display = "bp", cex=2/3, axis.bp=FALSE, col="blue")
  #legend(-3,2,unique(testDF$height),col=1:length(testDF$height),pch=1)
  
  plot(ccaData, type="n", axes = TRUE, frame.plot=TRUE, cex=2/3, main="RDA: Age", ylim = c(-2,2), xlim=c(-3,3))
  points(testDF$RDA1, testDF$RDA2, col=testDF$AgeSat, cex=2/5)
  text(ccaData, display = "bp", cex=2/3, axis.bp=FALSE, col="blue")
  #legend(-3,2,unique(testDF$height),col=1:length(testDF$height),pch=1)
  
  plot(ccaData, type="n", axes = TRUE, frame.plot=TRUE, cex=2/3, main="RDA: rlambda", ylim = c(-2,2), xlim=c(-3,3))
  points(testDF$RDA1, testDF$RDA2, col=testDF$rlambdaSat, cex=2/5)
  text(ccaData, display = "bp", cex=2/3, axis.bp=FALSE, col="blue")
  #legend(-3,2,unique(testDF$height),col=1:length(testDF$height),pch=1)
  
  plot(ccaData, type="n", axes = TRUE, frame.plot=TRUE, cex=2/3, main="RDA: Heal Time", ylim = c(-2,2), xlim=c(-3,3))
  points(testDF$RDA1, testDF$RDA2, col=testDF$healSat, cex=2/5)
  text(ccaData, display = "bp", cex=2/3, axis.bp=FALSE, col="blue")
  #legend(-3,2,unique(testDF$height),col=1:length(testDF$height),pch=1)
  
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
  
  return(ccaData)
}

EDist <- function(x, y, z, q, x1, y1, z1, q1){
  distance <- sqrt((x-x1)^2+(y/100-y1/100)^2+(z/100-z1/100)^2+(q/10-q1/10)^2)
  return(distance)
}

MinRange <- function(df){
  colNam <- colnames(df)
  for (i in colNam){
    print(i)
    print(mean(df[[i]])-sd(df[[i]]))
    print(mean(df[[i]])+sd(df[[i]]))
  }
}

circle <- function(center,diameter = 1, npoints = 100){
  r = diameter / 2
  tt <- seq(0,2*pi,length.out = npoints)
  xx <- center[1] + r * cos(tt)
  yy <- center[2] + r * 100 *sin(tt)
  return(data.frame(x = xx, y = yy))
}

PlotRun <- function(df){
  df <- na.omit(df)
  df$V2 <- as.numeric(lapply(df$V2, function(x) x*-1))
  colnames(df) <- c("PSF", "EGF_CONS","APOPEGF","DEATHPROB","MOVE","DIVLOCPROB","rlambda","mean", "height", "heal")
  dists <- data.frame(matrix(NA, nrow=0, ncol=1))
  colnames(dists) <- c("Dist")
  for(i in 1:length(df$mean)){
    dist <- data.frame(Dist=EDist(df$rlambda[i], df$mean[i], df$height[i], df$heal[i], 0.16, 28, 14, 3))
    dists <- rbind(dists,dist)
  }
  dfDist <- cbind(df, dists)
  colnames(dfDist) <- c("PSF", "EGF_CONS","APOPEGF","DEATHPROB","MOVE","DIVLOCPROB","rlambda","mean", "height", "heal", "E.Dist")
  
  #d=data.frame(x1=c(0.14), x2=c(0.18), y1=c(25), y2=c(30), Region=c('Target'), r=c(1))
  
  d=circle(c(0.16,28), .06)
  
  p <- ggplot(dfDist, aes(rlambda, mean, colour=E.Dist)) + geom_point() + xlab("rλ (Weekly)") + ylab("Mean Keratinocyte Age (Weekly)") + 
    geom_path(data=d, mapping=aes(x,y), alpha=0.5, inherit.aes = FALSE) + ggtitle("Parameter Sweeping Iteration 6") +
    geom_vline(xintercept=c(0.13, 0.19), linetype="dotted") + geom_hline(yintercept=c(25, 31), linetype="dotted") + scale_colour_gradient(low = "blue", high = "black")
  print(p)
  return(dfDist)  
}

PlotClosest <- function(dfDist){
  dfDist <- subset(dfDist, dfDist$E.Dist <= 0.1)
  #d=data.frame(x1=c(0.14), x2=c(0.18), y1=c(25), y2=c(30), Region=c('Target'), r=c(1))
  d=circle(c(0.16,28), .06)
  p <- ggplot(dfDist, aes(rlambda, mean, colour=E.Dist)) + geom_point() + xlab("rλ (Weekly)") + ylab("Mean Keratinocyte Age (Weekly)") + 
    geom_path(data=d, mapping=aes(x,y), alpha=0.5, inherit.aes = FALSE) + ggtitle("Parameter Sweeping Iteration 6") +
    geom_vline(xintercept=c(0.13, 0.19), linetype="dotted") + geom_hline(yintercept=c(25, 31), linetype="dotted") + scale_colour_gradient(low = "blue", high = "black")
  print(subset(dfDist, dfDist$E.Dist == min(dfDist$E.Dist)))
  print(p)
  setParams <- subset(dfDist, dfDist$rlambda >= 0.13 & dfDist$rlambda <= 0.19)
  setParams <- subset(setParams, setParams$mean >= 25 & setParams$mean <= 31)
  return(setParams)
}

textLab <- function(df){
  input <- paste("Parameters Range/Mean/SD:")
  a <- paste("PSF: ", round(min(df$PSF), 3), "-", round(max(df$PSF),3), " / ",round(mean(df$PSF),3), " / ", round(sd(df$PSF),3), sep=(""))
  b <- paste("APOPEGF: ", round(min(df$APOPEGF), 3), "-", round(max(df$APOPEGF),3), " / ",round(mean(df$APOPEGF),3), " / ", round(sd(df$APOPEGF),3), sep=(""))
  c <- paste("EGFCons.: ", round(min(df$EGF_CONS), 3), "-", round(max(df$EGF_CONS),3), " / ",round(mean(df$EGF_CONS),3), " / ", round(sd(df$EGF_CONS),3), sep=(""))
  d <- paste("MoveProb: ", round(min(df$MOVE), 3), "-", round(max(df$MOVE),3), " / ",round(mean(df$MOVE),3), " / ", round(sd(df$MOVE),3), sep=(""))
  e <- paste("DivLocProb: ", round(min(df$DIVLOCPROB), 3), "-", round(max(df$DIVLOCPROB),3), " / ",round(mean(df$DIVLOCPROB),3), " / ", round(sd(df$DIVLOCPROB),3), sep=(""))
  f <- paste("DeathProb: ", round(min(df$DEATHPROB), 3), "-", round(max(df$DEATHPROB),3), " / ",round(mean(df$DEATHPROB),3), " / ", round(sd(df$DEATHPROB),3), sep=(""))
  output <- paste("\nOutputs Range/Min.E.Dist:")
  edist <- subset(df, df$E.Dist == min(df$E.Dist))
  g <- paste("Cell Age: ", round(min(df$mean), 2), "-", round(max(df$mean), 2), " / ", round(edist$mean, 2), sep="")
  h <- paste("rLambda: ", round(min(df$rlambda), 2), "-", round(max(df$rlambda), 2), " / ", round(edist$rlambda, 2), sep="")
  i <- paste("Height: ", round(min(df$height), 2), "-", round(max(df$height), 2), " / ", round(edist$height, 2), sep="")
  j <- paste("Heal (days): ", round(min(df$heal), 2), "-", round(max(df$heal), 2), " / ", round(edist$heal, 2), sep="")
  return(paste(input, a, b, c, d, e, f, output, g, h, i, j, sep="\n"))
}

###### END Functions! #####

#Paramaterization (Round 5 is actually Round 1)
df <- read.csv("~/IdeaProjects/Epidermis_Project_Final/ParamSweep_Ordination_Round15.txt", sep = "\t", header = FALSE)
df <- read.csv("~/IdeaProjects/Epidermis_Project_Final/All_Parameters_Constant_Round16.txt", sep = "\t", header = FALSE)
DistDF <- PlotRun(df)
print(subset(DistDF, DistDF$E.Dist==min(DistDF$E.Dist)))
ccaData <- OrdiPlot(DistDF) # Use this to get Ordination Plots and CCA plots
summary(ccaData)
plot(DistDF$PSF, DistDF$E.Dist, xlab = "PSF", ylab = "Eucladian Dist.", main="Best Parameter", cex=2/5)
plot(1, type="n", xlab="", ylab="", xlim=c(-10, 10), ylim=c(-10, 10), axes=FALSE, frame.plot = FALSE)
text(0,0, labels = c(textLab(DistDF)))

filtered <- subset(DistDF, DistDF$rlambda >= 0.155 & DistDF$rlambda <= 0.175) # Yields Maximum value from Round15.txt file!
filtered <- subset(filtered, filtered$mean >= 26 & filtered$mean <= 30) # Yields Maximum value from Round15.txt file!
filtered <- subset(filtered, filtered$height >= 10) # Yields Maximum value from Round15.txt file!
VALUES_FOR_MODEL <- subset(filtered, filtered$height==max(filtered$height))
min(filtered$DIVLOCPROB)
max(filtered$DIVLOCPROB)-min(filtered$DIVLOCPROB)
