library(ggplot2)

EDist <- function(x, y, x1, y2, rangeRlamb, rangeMean){
  distance <- sqrt((x/rangeRlamb-x1/rangeRlamb)^2+(y/rangeMean-y2/rangeMean)^2)
  return(distance)
}

## Checking over parameter space of the first Five parameters##
df <- read.csv("~/IdeaProjects/Epidermis_Project_Final/ParamFile_Iteration1.csv", sep = ",", header = FALSE)
colnames(df) <- c("PSF", "EGF_CONS","APOPEGF","DEATHPROB","MOVE","rlambda","mean")
dists <- data.frame(matrix(NA, nrow=0, ncol=1))
colnames(dists) <- c("Dist")
for(i in 1:length(df$mean)){
  dist <- data.frame(Dist=EDist(df$rlambda[i], df$mean[i], 0.16, 25, max(df$rlambda)-min(df$rlambda), max(df$mean)-min(df$mean)))
  dists <- rbind(dists,dist)
}
dfDist <- cbind(df, dists)
colnames(dfDist) <- c("PSF", "EGF_CONS","APOPEGF","DEATHPROB","MOVE","rlambda","mean","E.Dist")
d=data.frame(x1=c(0.14), x2=c(0.18), y1=c(25), y2=c(30), Region=c('Target'), r=c(1))

ggplot(dfDist, aes(rlambda, mean, colour=E.Dist)) + geom_point() + xlab("rλ (Weekly)") + ylab("Mean Keratinocyte Age (Weekly)") + 
  geom_rect(data=d, mapping=aes(xmin=x1, xmax=x2, ymin=y1, ymax=y2, fill=Region), alpha=0.5, inherit.aes = FALSE) + ggtitle("Parameter Sweeping Iteration 1") +
  geom_vline(xintercept=c(0.14, 0.18), linetype="dotted") + geom_hline(yintercept=c(25, 30), linetype="dotted") + scale_colour_gradient(low = "blue", high = "black")

## Looking for proper variables for the mean Keratinocyte Age ##
properAge <- subset(df, (df$mean >= 25) & (df$mean <=30))
mean(properAge$EGF_CONS)
sd(properAge$EGF_CONS)
mean(properAge$APOPEGF)
sd(properAge$APOPEGF)
mean(properAge$DEATHPROB)
sd(properAge$DEATHPROB)
mean(properAge$MOVE)
sd(properAge$MOVE)
mean(properAge$PSF)
sd(properAge$PSF)

# Sweeping over these
# Division location
# DEATHPROB
# PSF

## Checking over parameter space of the Second 3 parameters##
df <- read.csv("~/IdeaProjects/Epidermis_Project_Final/ParamFile_Iteration2.csv", sep = ",", header = FALSE)
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
  geom_rect(data=d, mapping=aes(xmin=x1, xmax=x2, ymin=y1, ymax=y2, fill=Region), alpha=0.5, inherit.aes = FALSE) + ggtitle("Parameter Sweeping Iteration 2") +
  geom_vline(xintercept=c(0.14, 0.18), linetype="dotted") + geom_hline(yintercept=c(25, 30), linetype="dotted") + scale_colour_gradient(low = "blue", high = "black")




