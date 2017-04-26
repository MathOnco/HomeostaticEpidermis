s1=15
s2=1

set.seed(1)
c <- rbeta(100000, s1, s2)-0.9995


dens <- density(c)
plot.new()
plot(dens, ylim=c(0.5,13), ylab="Density", xlab="Value", main="Distribution of Fitness Effects")
x1 <- min(which(dens$x >= 0.0))
x2 <- max(which(dens$x <  0.2))

x3 <- min(which(dens$x >= -0.6))
x4 <- max(which(dens$x < -0.05))

abline(v=0.0, lty=2)
abline(v=-0.05, lty=2)
with(dens, polygon(x=c(x[c(x1,x1:x2,x2)]), y= c(0, y[x1:x2], 0), col="red"))
with(dens, polygon(x=c(x[c(x3,x3:x4,x4)]), y= c(0, y[x3:x4], 0), col="grey"))


t <- c > 0
l <- c < -0.05
myLabel <- paste("n=100,000\nP(x>0.0) = ", length(subset(t, t==TRUE))/100000, "\nP(x<-0.05) = ", length(subset(l, l==TRUE))/100000, sep="")
text(-0.5, 10, labels = myLabel)

