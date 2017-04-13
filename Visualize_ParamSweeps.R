library(ggplot2)

###### Functions! #####

EDist <- function(x, y, x1, y2, rangeRlamb){
  distance <- sqrt((x-x1)^2+(y/100-y2/100)^2)
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

dat <- circleFun(c(1,-1),2.3,npoints = 100)
#geom_path will do open circles, geom_polygon will do filled circles
ggplot(dat,aes(x,y)) + geom_path()

PlotRun <- function(df){
  df <- na.omit(df)
  colnames(df) <- c("PSF", "EGF_CONS","APOPEGF","DEATHPROB","MOVE","DIVLOCPROB","rlambda","mean")
  dists <- data.frame(matrix(NA, nrow=0, ncol=1))
  colnames(dists) <- c("Dist")
  for(i in 1:length(df$mean)){
    dist <- data.frame(Dist=EDist(df$rlambda[i], df$mean[i], 0.16, 28))
    dists <- rbind(dists,dist)
  }
  dfDist <- cbind(df, dists)
  colnames(dfDist) <- c("PSF", "EGF_CONS","APOPEGF","DEATHPROB","MOVE","DIVLOCPROB","rlambda","mean","E.Dist")
  
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

#Iteration8-10
dfAll <- read.csv("~/IdeaProjects/Epidermis_Project_Final/ParamFile_Iterations8_9_10_11.txt", sep = "\t", header = FALSE)
DistdfAll <- PlotRun(dfAll)
plot(DistdfAll)
DistdfAllParams <- PlotClosest(DistdfAll)
plot(DistdfAllParams)
MinRange(DistdfAllParams)

#Iteration13
df15 <- read.csv("~/IdeaProjects/Epidermis_Project_Final/ParamFile_Iteration15_DIVLOCPROB_PSF_DEATHPROB_EGFCONS_const.txt", sep = "\t", header = FALSE)
Distdf15 <- PlotRun(df15)
plot(Distdf15)
Distdf15Params <- PlotClosest(Distdf15)
MinRange(Distdf15Params)

#Iteration14
df15 <- read.csv("~/IdeaProjects/Epidermis_Project_Final/ParamFile_Iteration15_DIVLOCPROB_PSF_DEATHPROB_EGFCONS_const.txt", sep = "\t", header = FALSE)
Distdf15 <- PlotRun(df15)
plot(Distdf15)
Distdf15Params <- PlotClosest(Distdf15)
MinRange(Distdf15Params)

#Iteration15
df15 <- read.csv("~/IdeaProjects/Epidermis_Project_Final/ParamFile_Iteration15_DIVLOCPROB_PSF_DEATHPROB_EGFCONS_const.txt", sep = "\t", header = FALSE)
Distdf15 <- PlotRun(df15)
plot(Distdf15)
Distdf15Params <- PlotClosest(Distdf15)
MinRange(Distdf15Params)

#Iteration16
df16 <- read.csv("~/IdeaProjects/Epidermis_Project_Final/ParamFile_Iteration16_DIVLOCPROB_PSF_DEATHPROB_EGFCONS_MOVEPROB_const.txt", sep = "\t", header = FALSE)
Distdf16 <- PlotRun(df16)
plot(Distdf16)
Distdf16Params <- PlotClosest(Distdf16)
MinRange(Distdf16)











