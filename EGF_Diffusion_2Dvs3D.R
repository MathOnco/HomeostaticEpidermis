library(ggplot2)
library(reshape2)
library(scatterplot3d)
source("http://peterhaschke.com/Code/multiplot.R")

# Normal Conditions
df <- read.csv("~/Desktop/Darryl_collab/Framework/Homeostatic_Epidermis/EGFConsModels.txt", sep="\t", header = T)

dfmelt <- melt(df, id.vars = c("Tick","Model"))
colnames(dfmelt) <- c("Tick","Model","Layer","EGF_Concentration")

p1 <- with(dfmelt, scatterplot3d(Tick,Layer,EGF_Concentration, color = as.numeric(Model), pch = 19, main="Diffusion EGF Concentration Across Layers"))
dfT365 <- subset(dfmelt, dfmelt$Tick=="365")
p2 <- ggplot(dfT365, aes(as.numeric(Layer), EGF_Concentration, colour=Model)) + geom_point() + geom_line() + 
  ggtitle("EGF Concentration\nAcross Layers (T=365)") +
  scale_color_manual(values=c("DarkRed","Blue")) + guides(colour=FALSE)

#EGF_Consumption is 0.0
df <- read.csv("~/Desktop/Darryl_collab/Framework/Homeostatic_Epidermis/EGFConsModel_NullEGFCONS.txt", sep="\t", header = T)

dfmelt <- melt(df, id.vars = c("Tick","Model"))
colnames(dfmelt) <- c("Tick","Model","Layer","EGF_Concentration")

p3 <- with(dfmelt, scatterplot3d(Tick,Layer,EGF_Concentration, color = as.numeric(Model), pch = 19, main="EGF Concentration Across Layers (No Cons)"))
dfT365 <- subset(dfmelt, dfmelt$Tick=="365")
p4 <- ggplot(dfT365, aes(as.numeric(Layer), EGF_Concentration, colour=Model)) + geom_point() + geom_line() + 
  ggtitle("EGF Concentration\nAcross Layers (T=365 & No Consumption)") +
  scale_color_manual(values=c("DarkRed","Blue")) + guides(colour=FALSE)

#EGF Diffusion is different
df <- read.csv("~/Desktop/Darryl_collab/Framework/Homeostatic_Epidermis/EGFConsAdjDiffusion2D3D.txt", sep="\t", header = T)

dfmelt <- melt(df, id.vars = c("Tick","Model"))
colnames(dfmelt) <- c("Tick","Model","Layer","EGF_Concentration")

p5 <- with(dfmelt, scatterplot3d(Tick,Layer,EGF_Concentration, color = as.numeric(Model), pch = 19, main="EGF Concentration Across Layers (New Diffusion)"))
dfT365 <- subset(dfmelt, dfmelt$Tick=="365")
p6 <- ggplot(dfT365, aes(as.numeric(Layer), EGF_Concentration, colour=Model)) + geom_point() + geom_line() + 
  ggtitle("EGF Concentration\nAcross Layers (T=365 & New Diffusion)") +
  scale_color_manual(values=c("DarkRed","Blue"))

multiplot(p2, p4, p6, cols=3)




# Raffy Test
d2=t(read.csv("~/Desktop/Darryl_collab/Framework/Homeostatic_Epidermis/diffTest2.csv",header=F))
d3=t(read.csv("~/Desktop/Darryl_collab/Framework/Homeostatic_Epidermis/diffTest3.csv",header=F))

ToGGPlot=function(data,str){
  ret=data.frame(layer=seq(1,length(data)),value=data,type=str)
  return(ret)
}
df=rbind(ToGGPlot(d2,"d2"),ToGGPlot(d3,"d3"))

print(ggplot(df,aes(x=layer,y=value,color=type))+geom_point())
