library(ggplot2)
library(gridExtra)

#setwd("~/IdeaProjects/Epidermis_Project_Final/")
setwd("/Users/schencro/Desktop/Darryl_collab/Framework/Homeostatic_Epidermis")
fixdf <- read.csv("NOTCHReplicateRuns.txt", header = T, sep="\t", na.strings = c("na"))
fixPlot <- na.omit(fixdf)
passed <- data.frame(matrix(NA, nrow=0, ncol=4))
colnames(passed) <- c("Prob","Passed","MeanFixation","StdFixation")
for(i in 1:length(unique(fixdf$Prob))){
  tmp <- subset(fixdf, fixdf$Prob==unique(fixdf$Prob)[i])
  outDF <- data.frame(Prob=unique(fixdf$Prob)[i],
                      Passed=length(na.omit(tmp$FixTime))/length(tmp$Prob), 
                      MeanFixation=mean(na.omit(tmp$FixTime)),
                      StdFixation=sd(na.omit(tmp$FixTime)))
  passed <- rbind(passed, outDF)
}

passed$Runs <- c("200","200","200","200","200","200","200","500","500","1000","1000")

passed$Prob <- as.factor(passed$Prob)
fixPlot$Prob <- as.factor(fixPlot$Prob)
pd <- position_dodge(0.9)
p1 <- ggplot() + 
        geom_violin(data=fixPlot, aes(x=Prob,y=FixTime)) +
        geom_point(data=fixPlot, aes(x=Prob,y=FixTime, group=Prob), alpha=0.4, inherit.aes = F) +
        geom_point(data=passed, aes(x=Prob,y=MeanFixation), color="red") +
        geom_errorbar(data=passed, aes(x=Prob,ymin=MeanFixation-StdFixation, ymax=MeanFixation+StdFixation), color="red",width=.01, position=pd, inherit.aes = F) +
        ylab("Time to Fixation (days)") + xlab("fitness") + theme_minimal()

p2 <- ggplot(data=passed, aes(x=Prob,y=Passed)) + 
      geom_bar(stat="identity", fill="red") +
      geom_text(aes(label = Runs, y = Passed + 0.001), position = position_dodge(0.9), vjust = 0) +
      ylab("Frequency") + xlab("") + ggtitle("Fixation Events") +
      scale_y_continuous(limits = c(0.0,0.15)) + theme_minimal() +
      theme(axis.title.x = element_blank(), axis.text.x = element_blank())

grid.arrange(p2, p1, ncol = 1, heights = c(1, 2))

p3 <- ggplot() + geom_line(data=fixPlot, aes(x=Prob, y=Height, color="red"), inherit.aes = F) + theme_minimal() + 
  annotate("rect", xmin = 0, xmax = 12, ymin = 0, ymax = 11, alpha = .1) +
  annotate("rect", xmin = 0, xmax = 12, ymin = 13, ymax = 14, alpha = .1) +
  annotate("rect", xmin = 0, xmax = 12, ymin = 11, ymax = 13, alpha = .1, fill="green") +
  geom_hline(yintercept = 13, size=0.2) +
  geom_hline(yintercept = 11, size=0.2) + annotate("text", 1.5, 12, label = "Homeostasis") + theme(legend.position="none") +
  scale_y_continuous(limits=c(0.0,14), breaks=c(0,2,4,6,8,10,12,14), expand = c(0,0)) +
  ylab("Epidermis Height") + xlab("fitness")

p4 <- ggplot() + geom_line(data=fixPlot, aes(x=Prob, y=Age, color="red"), inherit.aes = F) + theme_minimal() +
  annotate("rect", xmin = 0, xmax = 12, ymin = 0, ymax = 28, alpha = .1) +
  annotate("rect", xmin = 0, xmax = 12, ymin = 32, ymax = 1200, alpha = .1) +
  annotate("rect", xmin = 0, xmax = 12, ymin = 28, ymax = 32, alpha = .1, fill="green") +
  scale_y_continuous(limits=c(0.0,1200), expand = c(0,0)) + theme(legend.position="none") +
  annotate("text", 1.5, 28, label = "Homeostasis") +
  ylab("Mean Cell Age") + xlab("fitness")
  
p5 <- ggplot() + geom_line(data=fixPlot, aes(x=Prob, y=rLambda, color="red")) + theme_minimal() +
  annotate("rect", xmin = 0, xmax = 12, ymin = .10, ymax = .185, alpha = .1) +
  annotate("rect", xmin = 0, xmax = 12, ymin = .21, ymax = .25, alpha = .1) +
  annotate("rect", xmin = 0, xmax = 12, ymin = .185, ymax = .21, alpha = .1, fill="green") +
  scale_y_continuous(limits=c(0.10,0.25), expand = c(0,0)) + theme(legend.position="none") +
  annotate("text", 10, .19, label = "Homeostasis") +
  ylab("Basal Loss/Replacement Rate") + xlab("fitness")
  
grid.arrange(p3, p4, p5, ncol = 1, heights = c(1, 1, 1))
