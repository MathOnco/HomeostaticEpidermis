library(ggplot2)

EDist <- function(x, y, x1, y2, rangeRlamb, rangeMean){
  distance <- sqrt((x/rangeRlamb-x1/rangeRlamb)^2+(y/rangeMean-y2/rangeMean)^2)
  return(distance)
}

MinRange <- function(df){
  colNam <- colnames(df)
  colNam <- as.list(colNam)
  for (i in names(df)){
    print(colNam[[i]])
    print(mean(df[[i]])-sd(df[[i]]))
    print(mean(df[[i]])+sd(df[[i]]))
  }
}

## Checking over parameter space of the Second 8 parameters##
## After correcting the model ##
df <- read.csv("~/Desktop/Darryl_collab/Framework/Homeostatic_Epidermis/ParamFile_Iteration8.txt", sep = "\t", header = FALSE)
df <- na.omit(df)
colnames(df) <- c("PSF", "EGF_CONS","APOPEGF","DEATHPROB","MOVE","DIVLOCPROB","rlambda","mean")
dists <- data.frame(matrix(NA, nrow=0, ncol=1))
colnames(dists) <- c("Dist")
for(i in 1:length(df$mean)){
  dist <- data.frame(Dist=EDist(df$rlambda[i], df$mean[i], 0.16, 25, max(df$rlambda)-min(df$rlambda), max(df$mean)-min(df$mean)))
  dists <- rbind(dists,dist)
}
dfDist <- cbind(df, dists)
colnames(dfDist) <- c("PSF", "EGF_CONS","APOPEGF","DEATHPROB","MOVE","DIVLOCPROB","rlambda","mean","E.Dist")

d=data.frame(x1=c(0.14), x2=c(0.18), y1=c(25), y2=c(30), Region=c('Target'), r=c(1))

ggplot(dfDist, aes(rlambda, mean, colour=E.Dist)) + geom_point() + xlab("rλ (Weekly)") + ylab("Mean Keratinocyte Age (Weekly)") + 
  geom_rect(data=d, mapping=aes(xmin=x1, xmax=x2, ymin=y1, ymax=y2, fill=Region), alpha=0.5, inherit.aes = FALSE) + ggtitle("Parameter Sweeping Iteration 6") +
  geom_vline(xintercept=c(0.14, 0.18), linetype="dotted") + geom_hline(yintercept=c(25, 30), linetype="dotted") + scale_colour_gradient(low = "blue", high = "black")

setParams <- subset(dfDist, dfDist$E.Dist <= 0.25)
ggplot(setParams, aes(rlambda, mean, colour=E.Dist)) + geom_point() + xlab("rλ (Weekly)") + ylab("Mean Keratinocyte Age (Weekly)") + 
  geom_rect(data=d, mapping=aes(xmin=x1, xmax=x2, ymin=y1, ymax=y2, fill=Region), alpha=0.5, inherit.aes = FALSE) + ggtitle("Parameter Sweeping Iteration 6") +
  geom_vline(xintercept=c(0.14, 0.18), linetype="dotted") + geom_hline(yintercept=c(25, 30), linetype="dotted") + scale_colour_gradient(low = "blue", high = "black")
MinRange(setParams)


df <- read.csv("~/Desktop/Darryl_collab/Framework/Homeostatic_Epidermis/ParamFile_Iteration9.txt", sep = "\t", header = FALSE)
df <- na.omit(df)
colnames(df) <- c("PSF", "EGF_CONS","APOPEGF","DEATHPROB","MOVE","DIVLOCPROB","rlambda","mean")
dists <- data.frame(matrix(NA, nrow=0, ncol=1))
colnames(dists) <- c("Dist")
for(i in 1:length(df$mean)){
  dist <- data.frame(Dist=EDist(df$rlambda[i], df$mean[i], 0.16, 25, max(df$rlambda)-min(df$rlambda), max(df$mean)-min(df$mean)))
  dists <- rbind(dists,dist)
}
dfDist <- cbind(df, dists)
colnames(dfDist) <- c("PSF", "EGF_CONS","APOPEGF","DEATHPROB","MOVE","DIVLOCPROB","rlambda","mean","E.Dist")

d=data.frame(x1=c(0.14), x2=c(0.18), y1=c(25), y2=c(30), Region=c('Target'), r=c(1))

ggplot(dfDist, aes(rlambda, mean, colour=E.Dist)) + geom_point() + xlab("rλ (Weekly)") + ylab("Mean Keratinocyte Age (Weekly)") + 
  geom_rect(data=d, mapping=aes(xmin=x1, xmax=x2, ymin=y1, ymax=y2, fill=Region), alpha=0.5, inherit.aes = FALSE) + ggtitle("Parameter Sweeping Iteration 6") +
  geom_vline(xintercept=c(0.14, 0.18), linetype="dotted") + geom_hline(yintercept=c(25, 30), linetype="dotted") + scale_colour_gradient(low = "blue", high = "black")

setParams <- subset(dfDist, dfDist$rlambda >= 0.14 & dfDist$rlambda <= 0.18)
setParams <- subset(setParams, setParams$mean >= 25 & setParams$mean <= 30)
ggplot(setParams, aes(rlambda, mean)) + geom_point() + xlab("rλ (Weekly)") + ylab("Mean Keratinocyte Age (Weekly)") + 
  geom_rect(data=d, mapping=aes(xmin=x1, xmax=x2, ymin=y1, ymax=y2, fill=Region), alpha=0.5, inherit.aes = FALSE) + ggtitle("Parameter Sweeping Iteration 6") +
  geom_vline(xintercept=c(0.14, 0.18), linetype="dotted") + geom_hline(yintercept=c(25, 30), linetype="dotted") + scale_colour_gradient(low = "blue", high = "black")
MinRange(setParams)





df <- read.csv("~/Desktop/Darryl_collab/Framework/Homeostatic_Epidermis/ParamFile_Iteration10.txt", sep = "\t", header = FALSE)
df <- na.omit(df)
colnames(df) <- c("PSF", "EGF_CONS","APOPEGF","DEATHPROB","MOVE","DIVLOCPROB","rlambda","mean")
dists <- data.frame(matrix(NA, nrow=0, ncol=1))
colnames(dists) <- c("Dist")
for(i in 1:length(df$mean)){
  dist <- data.frame(Dist=EDist(df$rlambda[i], df$mean[i], 0.16, 25, max(df$rlambda)-min(df$rlambda), max(df$mean)-min(df$mean)))
  dists <- rbind(dists,dist)
}
dfDist <- cbind(df, dists)
colnames(dfDist) <- c("PSF", "EGF_CONS","APOPEGF","DEATHPROB","MOVE","DIVLOCPROB","rlambda","mean","E.Dist")

d=data.frame(x1=c(0.14), x2=c(0.18), y1=c(25), y2=c(30), Region=c('Target'), r=c(1))

ggplot(dfDist, aes(rlambda, mean, colour=E.Dist)) + geom_point() + xlab("rλ (Weekly)") + ylab("Mean Keratinocyte Age (Weekly)") + 
  geom_rect(data=d, mapping=aes(xmin=x1, xmax=x2, ymin=y1, ymax=y2, fill=Region), alpha=0.5, inherit.aes = FALSE) + ggtitle("Parameter Sweeping Iteration 6") +
  geom_vline(xintercept=c(0.14, 0.18), linetype="dotted") + geom_hline(yintercept=c(25, 30), linetype="dotted") + scale_colour_gradient(low = "blue", high = "black")

dfDist <- subset(dfDist, dfDist$mean < 100)
plot(dfDist)
setParams <- subset(dfDist, dfDist$rlambda >= 0.14 & dfDist$rlambda <= 0.18)
setParams <- subset(setParams, setParams$mean >= 25 & setParams$mean <= 30)
ggplot(setParams, aes(rlambda, mean)) + geom_point() + xlab("rλ (Weekly)") + ylab("Mean Keratinocyte Age (Weekly)") + 
  geom_rect(data=d, mapping=aes(xmin=x1, xmax=x2, ymin=y1, ymax=y2, fill=Region), alpha=0.5, inherit.aes = FALSE) + ggtitle("Parameter Sweeping Iteration 6") +
  geom_vline(xintercept=c(0.14, 0.18), linetype="dotted") + geom_hline(yintercept=c(25, 30), linetype="dotted") + scale_colour_gradient(low = "blue", high = "black")
MinRange(setParams)
