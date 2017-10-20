# Title     : Analyze Fixation Results
# Objective : Measures the response of inducing a NOTCH Mutation with variable fixation probabilies
# Created by: schencro
# Created on: 10/19/17

library(ggplot2)
library(gridExtra)

#setwd("~/IdeaProjects/Epidermis_Project_Final/")
setwd("/Users/schencro/Desktop/Darryl_collab/Framework/Homeostatic_Epidermis/FixationTesting/")
fixdf <- read.csv("Results/500Replicates.txt", header = F, sep="\t", na.strings = c("na","NaN"))
colnames(fixdf) <- c("Replicate" , "Prob","FixationTime" , "CellAge" , "TissueHeight" , "MeanRLambda", "EndTick", "TotalPop", "Frequency")
fixdf$CellAge <- 0
fixdf$FixationTime <- as.numeric(fixdf$FixationTime)
fixPlot <- na.omit(fixdf)
passed <- data.frame(matrix(NA, nrow=0, ncol=5))
colnames(passed) <- c("Prob","Passed","MeanFixation","StdFixation","Nums")
for(i in 1:length(unique(fixdf$Prob))){
    tmp <- subset(fixdf, fixdf$Prob==unique(fixdf$Prob)[i])
    outDF <- data.frame(Prob=unique(fixdf$Prob)[i],
                        Passed=length(na.omit(tmp$FixationTime))/length(tmp$Prob),
                        MeanFixation=mean(na.omit(tmp$FixationTime)),
                        StdFixation=sd(na.omit(tmp$FixationTime)),
                        Nums=length(na.omit(tmp$FixationTime))
                        )
    passed <- rbind(passed, outDF)
}

passed$Runs <- rep(500, length(unique(passed$Prob)))
#passed$Runs <- c("200","200","200","200","200","200","200","500","500","1000","1000")

passed$Prob <- as.factor(passed$Prob)
fixPlot$Prob <- as.factor(fixPlot$Prob)
pd <- position_dodge(0.9)
p1 <- ggplot() +
    geom_boxplot(data=fixPlot, aes(x=Prob,y=FixationTime, group=Prob)) +
    #geom_point(data=fixPlot, aes(x=Prob,y=FixationTime, group=Prob), alpha=0.4, inherit.aes = F) +
    geom_point(data=passed, aes(x=Prob,y=MeanFixation), color="red") +
    geom_errorbar(data=passed, aes(x=Prob,ymin=MeanFixation-StdFixation, ymax=MeanFixation+StdFixation), color="red",width=.01, position=pd, inherit.aes = F) +
    ylab("Time to Fixation (days)") + xlab("fitness") + theme_minimal()

p2 <- ggplot(data=passed, aes(x=Prob,y=Passed)) +
    geom_bar(stat="identity", fill="red") +
    geom_text(aes(label = Runs, y = Passed + 0.001), position = position_dodge(0.9), vjust = 0) +
    ylab("Frequency") + xlab("") + ggtitle("Fixation Events") +
    scale_y_continuous(limits = c(0.0,0.5)) + theme_minimal() +
    theme(axis.title.x = element_blank(), axis.text.x = element_blank())

grid.arrange(p2, p1, ncol = 1, heights = c(1, 2))

p3 <- ggplot() + geom_line(data=fixPlot, aes(x=Prob, y=TissueHeight, color="red"), inherit.aes = F) + theme_minimal() +
    annotate("rect", xmin = 0, xmax = 12, ymin = 0, ymax = 11, alpha = .1) +
    annotate("rect", xmin = 0, xmax = 12, ymin = 13, ymax = 14, alpha = .1) +
    annotate("rect", xmin = 0, xmax = 12, ymin = 11, ymax = 13, alpha = .1, fill="green") +
    #geom_hline(yintercept = 13, size=0.2) +
    #geom_hline(yintercept = 11, size=0.2) + 
    annotate("text", 1.5, 12, label = "Homeostasis") + theme(legend.position="none") +
    scale_y_continuous(limits=c(0.0,14), breaks=c(0,2,4,6,8,10,12,14), expand = c(0,0)) +
    ylab("Epidermis Height") + xlab("fitness")

p4 <- ggplot() + geom_line(data=fixPlot, aes(x=Prob, y=CellAge, color="red"), inherit.aes = F) + theme_minimal() +
    annotate("rect", xmin = 0, xmax = 12, ymin = 0, ymax = 28, alpha = .1) +
    annotate("rect", xmin = 0, xmax = 12, ymin = 32, ymax = 1200, alpha = .1) +
    annotate("rect", xmin = 0, xmax = 12, ymin = 28, ymax = 32, alpha = .1, fill="green") +
    scale_y_continuous(limits=c(0.0,1200), expand = c(0,0)) + theme(legend.position="none") +
    annotate("text", 1.5, 28, label = "Homeostasis") +
    ylab("Mean Cell Age") + xlab("fitness")

p5 <- ggplot() + geom_line(data=fixPlot, aes(x=Prob, y=MeanRLambda, color="red")) + theme_minimal() +
    annotate("rect", xmin = 0, xmax = 12, ymin = .0, ymax = .020, alpha = .1) +
    annotate("rect", xmin = 0, xmax = 12, ymin = .026, ymax = .03, alpha = .1) +
    annotate("rect", xmin = 0, xmax = 12, ymin = .02, ymax = .026, alpha = .1, fill="green") +
    #geom_hline(yintercept = 13, size=0.2) +
    #geom_hline(yintercept = 11, size=0.2) + 
    annotate("text", 1.5, 12, label = "Homeostasis") + theme(legend.position="none") +
    scale_y_continuous(limits=c(0.0,0.03), expand = c(0,0)) + theme(legend.position="none") +
    annotate("text", 1, .024, label = "Homeostasis") +
    ylab("Basal Loss/Replacement Rate") + xlab("fitness")

grid.arrange(p3, p5, ncol = 1, heights = c(1, 1))

