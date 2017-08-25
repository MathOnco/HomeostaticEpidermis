library(ggplot2)
library(gridExtra)

GetLine <- function(df){

  return(fit)
}

#BenchMarking
twoD <- c(67,0.780,100,1.061,200,2.100,300,3.148,1000,10.997,2000,49.485,3000,99.625,5000,271.884,8000,644.676,10000,980.436)
twoDF <- as.data.frame(matrix(NA,nrow=0,ncol=2))
colnames(twoDF) <- c("Size","Time")
for(i in seq(1,length(twoD),by=2)){
  print(i)
  tmp <- data.frame(Size=twoD[i], Time=twoD[i+1]/(365*2))
  twoDF <- rbind(twoDF,tmp)
}

fit <- lm(Time~Size,twoDF)
line <- function(x){
  t <- 73*365
  y <- 0.0001262*x-0.0916710
  return(y*t / 60 / 60)
}

test <- sapply(twoDF$Size, FUN="line")

dfRuns <- data.frame(Size=twoDF$Size, Time=sapply(twoDF$Size, FUN="line"), Age="65 year sim")
dfRuns2 <- data.frame(Size=twoDF$Size, Time=sapply(twoDF$Size, FUN="line"), Age="55 year sim")
dfRuns3 <- data.frame(Size=twoDF$Size, Time=sapply(twoDF$Size, FUN="line"), Age="58 year sim")
dfRuns4 <- data.frame(Size=twoDF$Size, Time=sapply(twoDF$Size, FUN="line"), Age="73 year sim")
dfRuns <- rbind(dfRuns,dfRuns2,dfRuns3,dfRuns4,dfRuns)

p1 <- ggplot(twoDF, aes(x=Size, y=Time)) + geom_point() +
  geom_abline(intercept=-0.0916710, slope=0.0001262) +
  ggtitle("2D Benchmarking") + xlab("xDim") + ylab("Time per Tick (s)") + ylim(0.0,1.5)
p1


p2 <- ggplot(dfRuns, aes(x=Size, y=Time, colour=Age)) + geom_line() + ggtitle("2D estimated hours per simulation") + xlab("xDim") + ylab("Hours")
p2

p3 <- arrangeGrob(p1, p2, ncol=1, nrow=2)
do.call("grid.arrange", p3)

