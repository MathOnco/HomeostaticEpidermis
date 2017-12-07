# Title     : Visualize 3D Simulations
# Objective : Create Paper worthy Cell figures
# Created by: schencro
# Created on: 12/6/17

library(rgl)

# RGL XYZ correspond to vertices...such that x1,y1,z1 are split by x=c(),y=(),z=()
# in this way it's a very R fashion. Can do complex shapes with this method. x y or z
# is not an individual vertices. For triangles this means a length(vector)/3 must equal 0.

xDim=50
zDim=xDim
yDim=20

SQtoX <- function(i){ return( floor(i/(yDim*zDim))+1 )  }
SQtoY <- function(i){ return( floor((i/zDim)%%yDim)+1 )  }
SQtoZ <- function(i){ return( (i%%zDim)+1 ) }

initializeRGL <- function(theta=28,phi=15,myColor="white"){
    par3d(windowRect = c(0, 0, 800, 800))
    view3d(theta,phi)
    rgl.bg(color=myColor)
}

drawSquare <- function{

}

plotBox <- function(){
x = c(0,0,0,xDim+4,0,0,0, xDim+4,0, 0,xDim+4,xDim+4,0,xDim+4,0,0,xDim+4,xDim+4)
y = c(0,20,0,0,0,0,20,20,20,20,0,0,0,0,0,20)
z = c(0,0,0,0,0,xDim+4,0,0,0,xDim+4,xDim+4,0,xDim+4,xDim+4,xDim+4,xDim+4,0,0)
rgl.lines(x=x,y=y,z=z, color="black")
#box
x = c(0,xDim+4,0,0,xDim+4,xDim+4,0,0,xDim+4,0,xDim+4,xDim+4,0,0,0,0,0,0)
y = c(0,0,0,0,0,0,yDim,0,yDim,0,yDim,0,0,yDim,0,yDim,yDim)
z = c(zDim+4,0,0,zDim+4,0,xDim+4,0,0,0,0,0,0,xDim+4,xDim+4,0,xDim+4,0,0)
triangles3d(x=x,y=y,z=z,color="black")
}

drawSubsection <- function(){
x = c(xDim/2,xDim/2,xDim+2,xDim/2,xDim+2,xDim+2,xDim/2,xDim/2,xDim+2,xDim/2,xDim+2,xDim+2)
y = c(0,yDim-2,yDim-2,0,0,yDim-2,0,yDim-2,yDim-2,0,0,yDim-2)
z = c(rep(xDim/2,length(y/2)),rep(xDim+1.5,length(y/2)))
triangles3d(x=x, y=y, z=z, color="red", alpha=0.3)
}

placeCells <- function(theData, myAlpha=1.0){
rgl.spheres(
x=SQtoX(theData$i),
z=SQtoZ(theData$i),
y=SQtoY(theData$i),
radius=0.8,
color=hsv(theData$h,theData$s,theData$v),
alpha=myAlpha)
}

visualizeCells <- function(theData=data.frame(x=rnorm(100),y=rnorm(100),z=rnorm(100),r=rep(1,100),g=rep(1,100),b=rep(1,100),alpha=rep(0.8,100))){
whiteCells <- subset(theData,theData$h==0.0)
otherCells <- subset(theData,theData$h!=0.0)
placeCells(whiteCells, rep(0.4,length(theData$i)))
placeCells(otherCells, otherCells$alpha)
plotBox()
drawSubsection() # Use this if you are visualizing pieces taken away
}

drawCells <- function(theData=data.frame(x=rnorm(100),y=rnorm(100),z=rnorm(100),r=rep(1,100),g=rep(1,100),b=rep(1,100),alpha=rep(0.8,100))){
initializeRGL()
visualizeCells(theData)
}

positions <- read.table("~/Desktop/ImageData3DStep1.txt",header = F)
colnames(positions)<-c("i","h","s","v","alpha")

drawCells(positions)

rgl.snapshot( "~/Desktop/3D.2yrs.100xDim.section.png", fmt = "png", top = TRUE )
rgl.close()

