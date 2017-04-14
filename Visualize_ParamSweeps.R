library(ggplot2)
library(vegan)

###### Functions! #####
OrdRange <- function(q){
  max <- q*2
  min <- q/2
  print(min)
  print(max-min)
}


EDist <- function(x, y, z, q, x1, y1, z1, q1){
  distance <- sqrt((x-x1)^2+(y/100-y1/100)^2+(z/100-z1/100)^2+(q/100-q1/100)^2)
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
  dfDist <- subset(dfDist, dfDist$E.Dist <= 0.04)
  #d=data.frame(x1=c(0.14), x2=c(0.18), y1=c(25), y2=c(30), Region=c('Target'), r=c(1))
  d=circle(c(0.16,28), .06)
  p <- ggplot(dfDist, aes(rlambda, mean, colour=E.Dist)) + geom_point() + xlab("rλ (Weekly)") + ylab("Mean Keratinocyte Age (Weekly)") + 
    geom_path(data=d, mapping=aes(x,y), alpha=0.5, inherit.aes = FALSE) + ggtitle("Parameter Sweeping Iteration 6") +
    geom_vline(xintercept=c(0.13, 0.19), linetype="dotted") + geom_hline(yintercept=c(25, 31), linetype="dotted") + scale_colour_gradient(low = "blue", high = "black")
  print(subset(dfDist, dfDist$E.Dist == min(dfDist$E.Dist)))
  print(p)
  setParams <- subset(dfDist, dfDist$rlambda >= 0.14 & dfDist$rlambda <= 0.18)
  setParams <- subset(setParams, setParams$mean >= 25 & setParams$mean <= 30)
  return(setParams)
}

###### END Functions! #####

#Param
df18 <- read.csv("~/IdeaProjects/Epidermis_Project_Final/ParamSweep_Ordination_Round1.txt", sep = "\t", header = FALSE)
Distdf18 <- PlotRun(df18)
plot(Distdf18)
Distdf18Params <- PlotClosest(Distdf18)
plot(Distdf18Params)
MinRange(Distdf18Params)


#### Canonical Correspondence analysis ####
dfOutcome <- Distdf18[7:9]
dfInput <- Distdf18[1:6]
ccaData <- cca(dfOutcome ~ PSF+EGF_CONS+APOPEGF+DEATHPROB+MOVE+DIVLOCPROB, data=dfInput)
ccaData
summary(ccaData)
VectorStats <- envfit(ccaData ~ PSF, data = dfInput, iterations=c(10000))
OrdiSurface <- ordisurf(ccaData ~ PSF, data = dfInput, plot = FALSE)
plot(ccaData)
plot(OrdiSurface, col = "red", add = TRUE)

