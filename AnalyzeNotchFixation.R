library(ggplot2)
library(gridExtra)

setwd("~/IdeaProjects/Epidermis_Project_Final/")
fixdf <- read.csv("NOTCHReplicateRuns.txt", header = T, sep="\t", na.strings = c("na"))
fixPlot <- na.omit(fixdf)
passed <- data.frame(matrix(NA, nrow=0, ncol=4))
colnames(passed) <- c("Prob","Passed","MeanFixation","StdFixation")
for(i in 1:length(unique(fixdf$Prob))){
  tmp <- subset(fixdf, fixdf$Prob==unique(fixdf$Prob)[i])
  outDF <- data.frame(Prob=unique(fixdf$Prob)[i],
                      Passed=length(na.omit(tmp$FixTime))/200, 
                      MeanFixation=mean(na.omit(tmp$FixTime)),
                      StdFixation=sd(na.omit(tmp$FixTime)))
  passed <- rbind(passed, outDF)
}

pd <- position_dodge(0.0)
p1 <- ggplot(data=passed, aes(x=Prob,y=MeanFixation)) + geom_point(color="red") +
        geom_errorbar(aes(ymin=MeanFixation-StdFixation, ymax=MeanFixation+StdFixation), color="red",width=.01, position=pd) +
        geom_point(data=fixPlot, aes(x=Prob,y=FixTime), alpha=0.4, inherit.aes = F) +
        ylab("Time to Fixation") + xlab("fitness") + ggtitle("Time to fixation (200 replicates)")

p2 <- ggplot(data=passed, aes(x=Prob,y=Passed)) + geom_bar(stat="identity", fill="red") +
      ylab("Frequency of Fixation") + xlab("fitness") + ggtitle("Fixation Events (200 replicates)")

lay <- rbind(c(1),
             c(2),
             c(2),
             c(2))
g <- grid.arrange(p2, p1)
