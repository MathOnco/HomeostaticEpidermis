# Title     : Analyze Fixation Results
# Objective : Measures the response of inducing a NOTCH Mutation with variable fixation probabilies
# Created by: schencro
# Created on: 10/19/17

library(ggplot2)
library(gridExtra)
library(dplyr)

# Prep dataframes
cleanData <- function(df){
  colnames(df) <- c("Replicate" ,"Prob","FixationTime" , "CellAge" , "TissueHeight" , "MeanRLambda", "EndTick", "TotalPop", "Frequency")
  df$CellAge <- 0
  df$FixationTime <- as.numeric(df$FixationTime)
  df <- na.omit(df)
  return(df)
}

#downsamples to 200 fixation events
sampleFixations <- function(df){
  sampled <- data.frame(matrix(NA, nrow=0, ncol=length(colnames(df))))
  colnames(sampled) <- colnames(df)
  for(i in 1:length(unique(df$Prob))){
    tmp <- subset(df, df$Prob==unique(df$Prob)[i])
    for(k in 1:length(tmp$Frequency)){
      if(tmp$Frequency[k]==1.00){
        out <- tmp[k,]
        sampled <- rbind(sampled,out)
      }
    }
  }
  return(sampled)
}

groupFixers <- function(cleanFix, lin){
  out <- data.frame(matrix(0,nrow=0,ncol=length(colnames(cleanFix))+1))
  colnames(out) <- c(colnames(cleanFix),"Group")
  cleanFix$Prob
  for(i in 1:length(unique(cleanFix$Prob))){
    tmp <- subset(cleanFix, cleanFix$Prob==unique(cleanFix$Prob)[i])
    lin %>% filter(x == unique(cleanFix$Prob)[i]) -> val
    for(k in 1:length(tmp$FixationTime)){
      if(tmp[k,]$FixationTime >= val$y){
        tmpout <- cbind(tmp[k,], data.frame(group="Late"))
      } else{
        tmpout <- cbind(tmp[k,], data.frame(group="Early"))
      }
      out <- rbind(out, tmpout)
    }
  }
  return(out)
}

#setwd("~/IdeaProjects/Epidermis_Project_Final/")
setwd("/Users/schencro/Desktop/Darryl_collab/Framework/Homeostatic_Epidermis/FixationTesting/")
# Use these results to get a consistent number of total fixation events to compare
fixations <- read.csv("Results/MostRunSuccesses.txt", header = F, sep="\t", na.strings = c("na","NaN"))
fixations <- cleanData(fixations)
cleanFix <- sampleFixations(fixations)
# Use these results to get the Fixation Events bar plot
fixdf <- read.csv("Results/500Replicates.txt", header = F, sep="\t", na.strings = c("na","NaN"))
colnames(fixdf) <- c("Replicate" ,"Prob","FixationTime" , "CellAge" , "TissueHeight" , "MeanRLambda", "EndTick", "TotalPop", "Frequency")
fixdf$CellAge <- 0
fixdf$FixationTime <- as.numeric(fixdf$FixationTime)
fixPlot <- cleanData(fixdf)

passed <- data.frame(matrix(NA, nrow=0, ncol=5))
colnames(passed) <- c("Prob","Passed","MeanFixation","StdFixation","Nums")
for(i in 1:length(unique(fixdf$Prob))){
    tmp <- subset(fixdf, fixdf$Prob==unique(fixdf$Prob)[i])
    probs <- unique(fixdf$Prob)[i]
    freqFix <- length(na.omit(tmp$FixationTime))/length(tmp$Prob)
    fixTime <- mean(na.omit(tmp$FixationTime))
    fixTimeSTD <- sd(na.omit(tmp$FixationTime))
    totals <- length(na.omit(tmp$FixationTime))
    outDF <- data.frame(Prob=probs,
                        Passed=freqFix,
                        MeanFixation=fixTime,
                        StdFixation=fixTimeSTD,
                        Nums=totals
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
    #geom_text(aes(label = Runs, y = Passed), vjust = 0) +
    ylab("Frequency") + xlab("fitness") + ggtitle("Fixation Events") +
    scale_y_continuous(limits = c(0.0,0.5)) + 
    theme_minimal()
    #theme(axis.title.x = element_blank(), axis.text.x = element_blank())

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


#--Additional Runs to obtain meaningful results.
ggplot(fixations, aes(x=FixationTime, y=TissueHeight, colour=as.factor(Prob))) +
         geom_point()

#--Gets the means and stdev information from the data--#
passed <- data.frame(matrix(NA, nrow=0, ncol=5))
colnames(passed) <- c("Prob","Passed","MeanFixation","StdFixation","Nums")
for(i in 1:length(unique(cleanFix$Prob))){
  tmp <- subset(cleanFix, cleanFix$Prob==unique(cleanFix$Prob)[i])
  probs <- unique(cleanFix$Prob)[i]
  freqFix <- length(na.omit(tmp$FixationTime))/length(tmp$Prob)
  fixTime <- mean(na.omit(tmp$FixationTime))
  fixTimeSTD <- sd(na.omit(tmp$FixationTime))
  totals <- length(na.omit(tmp$FixationTime))
  outDF <- data.frame(Prob=probs,
                      Passed=freqFix,
                      MeanFixation=fixTime,
                      StdFixation=fixTimeSTD,
                      Nums=totals
  )
  passed <- rbind(passed, outDF)
}
#---Grouping analysis to get the two groups (Late/Early Fixers)---#
x = unique(as.numeric(cleanFix$Prob))
x = sort(x[x>0.1])
y = c(300,310,350,400,500,600,700,775,850)
#nums <- seq(0.0,1.0,0.1)
fit <- lm(y~x)
y = sapply(sort(unique(as.numeric(cleanFix$Prob))), FUN=function(x) fit$coefficients[2]*x+fit$coefficients[1])
lin = data.frame(x=sort(unique(as.numeric(cleanFix$Prob))),y=y)
plot(lin)

plotFixers <- groupFixers(cleanFix, lin)

p1 <- ggplot() +
  #geom_violin(data=cleanFix, aes(x=Prob,y=FixationTime, group=Prob)) +
  geom_line (data=lin, aes(x=x,y=y), alpha=0.2, inherit.aes=F) +
  geom_point(data=plotFixers, aes(x=Prob,y=FixationTime, colour=group, group=group), size=0.9, inherit.aes = F) +
  annotate("text", as.numeric(passed$Prob), 1750, label=paste("n=",passed$Nums, sep="")) +
  #geom_point(data=passed, aes(x=as.factor(Prob),y=MeanFixation), color="red") +
  #geom_errorbar(data=passed, aes(x=as.factor(Prob),ymin=MeanFixation-StdFixation, ymax=MeanFixation+StdFixation), color="red",width=.01, position=pd, inherit.aes = F) +
  ylab("Time to Fixation (days)") + xlab("fp (fzero = 1-fp)") + theme_minimal() +
  scale_x_continuous(breaks=unique(cleanFix$Prob)) +
  scale_colour_manual(values=c(rgb(114,20,41,maxColorValue = 255),rgb(18,45,84,maxColorValue = 255)))

p3 <- ggplot() + 
  geom_point(data=plotFixers, aes(x=Prob, y=TissueHeight, color=group, group=group), position=position_dodge(0.02), size=0.9, inherit.aes = F) + theme_minimal() +
  annotate("rect", xmin = -0.05, xmax = 1.05, ymin = 0, ymax = 11, alpha = .1) +
  annotate("rect", xmin = -0.05, xmax = 1.05, ymin = 13, ymax = 14, alpha = .1) +
  annotate("rect", xmin = -0.05, xmax = 1.05, ymin = 11, ymax = 13, alpha = .1, fill="green") +
  #geom_hline(yintercept = 13, size=0.2) +
  #geom_hline(yintercept = 11, size=0.2) + 
  annotate("text", as.numeric(passed$Prob), 13, label=paste("n=",passed$Nums, sep="")) +
  annotate("text", 0.1, 12, label = "Homeostasis") + 
  scale_y_continuous(limits=c(0.0,14), breaks=c(0,2,4,6,8,10,12,14), expand = c(0,0)) +
  ylab("Epidermis Height") + xlab("fp (fzero = 1-fp)") +
  scale_x_continuous(breaks=unique(cleanFix$Prob), expand=c(0,0)) +
  scale_colour_manual(values=c(rgb(114,20,41,maxColorValue = 255),rgb(18,45,84,maxColorValue = 255)))

p5 <- ggplot() + geom_point(data=plotFixers, aes(x=Prob, y=MeanRLambda, color=group, group=group), position=position_dodge(0.02), size=0.9, inherit.aes = F) + theme_minimal() +
  annotate("rect", xmin = -0.05, xmax = 1.05, ymin = .0, ymax = .020, alpha = .1) +
  annotate("rect", xmin = -0.05, xmax = 1.05, ymin = .026, ymax = .03, alpha = .1) +
  annotate("rect", xmin = -0.05, xmax = 1.05, ymin = .02, ymax = .026, alpha = .1, fill="green") +
  #geom_hline(yintercept = 13, size=0.2) +
  #geom_hline(yintercept = 11, size=0.2) + 
  #annotate("text", 1.5, 0.0, label = "Homeostasis") + theme(legend.position="none") +
  scale_y_continuous(limits=c(0.0,0.03), expand = c(0,0)) + 
  annotate("text", 0.05, .024, label = "Homeostasis") +
  annotate("text", as.numeric(passed$Prob), 0.027, label=paste("n=",passed$Nums, sep="")) +
  ylab("Basal Loss/Replacement Rate") + xlab("fp (fzero = 1-fp)") +
  scale_x_continuous(breaks=unique(cleanFix$Prob), expand=c(0,0)) +
  scale_colour_manual(values=c(rgb(114,20,41,maxColorValue = 255),rgb(18,45,84,maxColorValue = 255)))


vaf <- read.csv("/Users/schencro/Desktop/Darryl_collab/Model_Data_Analysis/HomeostaticDataAnalysis/OutputTables/Selection/3D/ProcessedData.threeDNOTCHTest.10242017.10xDim.100yrs.replicate_1.csv", header=T)

depth=500
for(i in 1:length(vaf$VAF)){
  simSeq <- data.frame(matrix(NA,ncol = 1, nrow=length(vaf$VAF)))
  colnames(simSeq) <- c("SimVAF")
  for(k in 1:length(vaf$VAF)){
    fi <- rbinom(size=depth,p=vaf$VAF[k],n=1)
    VAFs <- fi/depth
    simSeq[k,1] <- VAFs
  }
}

plot(density(simSeq$SimVAF[simSeq$SimVAF>0.005]))

newInfo = data.frame(vaf=simSeq$SimVAF[simSeq$SimVAF>0.005])

plot(density(newInfo$vaf))

ggplot() +
  stat_density(data=newInfo, aes(vaf), alpha = 0.1, fill="red", colour="red")+
  scale_x_continuous(limits=c(-0.15,0.45)) +
  geom_line(aes(x=seq(-0.15,0.45,0.01), y=0), colour="white") +
  ylab("Density") + xlab("VAF") + theme_bw(base_size = 12) + ggtitle("Model & Patient VAF Densities")



