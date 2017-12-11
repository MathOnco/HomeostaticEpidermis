# Title     : Visualize 3D Simulations
# Objective : Create Paper worthy Cell figures
# Created by: schencro
# Created on: 12/6/17

library(rgl)

# RGL XYZ correspond to vertices...such that x1,y1,z1 are split by x=c(),y=(),z=()
# in this way it's a very R fashion. Can do complex shapes with this method. x y or z
# is not an individual vertices. For triangles this means a length(vector)/3 must equal 0.

SQtoX <- function(i){ return( floor(i/(yDim*zDim))+1 )  }
SQtoY <- function(i){ return( floor((i/zDim)%%yDim)+1 )  }
SQtoZ <- function(i){ return( (i%%zDim)+1 ) }

SubsectHelper <- function(i, section=1){
    # Section corresponds to right chunk out (1), left chunk out (2), or half out (3).
    if(section==1){
        if( (SQtoX(i)<xDim/2 || SQtoZ(i)<xDim/2)){
            return(TRUE)
        } else {
            return(FALSE)
        }
    } else if(section==2){
        if( (SQtoX(i)>xDim/2 || SQtoZ(i)<xDim/2)){
            return(TRUE)
        } else {
            return(FALSE)
        }
    } else if(section==3){
        if( SQtoZ(i)<zDim/2 ){
            return(TRUE)
        } else {
            return(FALSE)
        }
    }
}

Subsect <- function(df, section=1){
    dfList <- sapply(df$i, FUN = SubsectHelper, section=section)
    return(df[dfList,])
}

GetData <- function(fileName){
    df <- read.csv(fileName, header=F, sep="\t")
    colnames(df)<-c("i","h","s","v","alpha")
    whiteCells <- subset(df,df$h==0.0)
    otherCells <- subset(df,df$h!=0.0)
    whiteCells$alpha = rep(0.1,length(whiteCells$alpha))
    otherCells$alpha = otherCells$alpha-0.2
    df <- rbind(whiteCells,otherCells)
    return(df)
}

initializeRGL <- function(theta=0,phi=20,myColor="white"){
    par3d(windowRect = c(0, 0, 1600, 800))
    view3d(theta,phi)
    rgl.bg(color=myColor)
}

placeCells <- function(theData=data.frame(), adjustx=0, adjustz=0, adjusty = 0, myAlpha=1.0, radius=0.8){
    x=SQtoX(theData$i) + adjustx
    z=SQtoZ(theData$i) + adjustz
    y=SQtoY(theData$i) + adjusty
    rgl.spheres(x=x,z=z,y=y,radius=radius,color=hsv(theData$h,theData$s,theData$v),alpha=myAlpha)
}

BigBoxPlot <- function(){
    l = (xDim*3+xDim/4*2+5)
    x = c( 0 , 0 , 0 , 0 , 0 , 0, 0 , (xDim*3+xDim/4*2+5) , 0 , (xDim*3+xDim/4*2+5) , 0 ,
    (xDim*3+xDim/4*2+5) , (xDim*3+xDim/4*2+5) , (xDim*3+xDim/4*2+5) , (xDim*3+xDim/4*2+5) , (xDim*3+xDim/4*2+5) , 0 , 0 , (xDim*3+xDim/4*2+5)
    , (xDim*3+xDim/4*2+5) , (xDim*3+xDim/4*2+5) , (xDim*3+xDim/4*2+5))
    y = c( -50 , yDim+5 , -50 , -50 , yDim+5 , yDim+5 , -50 , -50 , yDim+5 , yDim+5 , -50 , -50 , -50 , -50 , yDim+5 , yDim+5 , -50 , yDim+5 , -50 , yDim+5 , -50 , yDim+5)
    z = c( 0 , 0 , 0 , zDim+5 , 0 , zDim+5 , 0 , 0 , 0 , 0 , zDim+5, zDim+5 , 0 , zDim+5 , 0 , zDim+5 , zDim+5, zDim+5 , zDim+5 , zDim+5 , 0 , 0)
    rgl.lines(x=x,y=y,z=z, color="black")
    #box
    x = c(0,l,l, 0,0,l, 0,0,l, 0,l,l, 0,0,0, 0,0,0, l,l,l, l,l,l)
    y = c(-50,-50,yDim+5, -50,yDim+5,yDim+5, -50,-50,-50, -50,-50,-50, -50,-50,yDim+5, -50,yDim+5,yDim+5, -50,-50,yDim+5, -50,yDim+5,yDim+5)
    z = c(0,0,0, 0,0,0, 0,zDim+5,zDim+5, 0,0,zDim+5, 0,zDim+5,zDim+5, 0,0,zDim+5, 0,zDim+5,zDim+5, 0,0,zDim+5)
    triangles3d(x=x,y=y,z=z,color="black")

    x = c(l/3,l/3,l/3, l/3,l/3,l/3, l/3*2,l/3*2,l/3*2, l/3*2,l/3*2,l/3*2)
    y = c(-50,yDim+5,yDim+5, -50,-50,yDim+5, -50,yDim+5,yDim+5, -50,-50,yDim+5)
    z = c(0,0,zDim/2, 0,zDim/2,zDim/2, 0,0,zDim/2, 0,zDim/2,zDim/2)
    triangles3d(x=x,y=y,z=z,color="red", alpha=1)
}


#====Main====#
xDim=100
yDim=20
zDim=xDim

t25 <- GetData("~/Desktop/100xDim.25yrs.txt")
t25cut <- Subsect(t25,section=1)
t50 <- GetData("~/Desktop/100xDim.50yrs.txt")
t50cut <- Subsect(t50,section=3)
t75 <- GetData("~/Desktop/100xDim.75yrs.txt")
t75cut <- Subsect(t75,section=2)

#drawCells
initializeRGL()

placeCells(t25, myAlpha=t25$alpha)
placeCells(t25cut, myAlpha=t25cut$alpha, adjusty=-50)

placeCells(t50, myAlpha=t50$alpha, adjustx=xDim+xDim/4)
placeCells(t50cut, myAlpha=t50cut$alpha, adjusty=-50, adjustx=xDim+xDim/4)

placeCells(t75, myAlpha=t75$alpha, adjustx=xDim*2+(xDim/4)*2)
placeCells(t75cut, myAlpha=t75cut$alpha, adjusty=-50, adjustx=xDim*2+(xDim/4)*2)

view3d(zoom=0.6)

BigBoxPlot()

rgl.snapshot( "~/Desktop/3D.progression.png", fmt = "png", top = TRUE )
rgl.clear()
rgl.close()
