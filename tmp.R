library(rgl)
par3d(windowRect = c(0, 5, 400, 400))
view3d(35,30)
#rgl.open()
rgl.bg(color = "black")
df <- data.frame(matrix(NA, ncol = 7,nrow = 0))
f <- file("stdin")
open(f)
while(length(line <- readLines(f,n=1)) > 0) {
  write(line, stderr())
  if(grepl(line, "Done")==F){
    LineData <- as.data.frame(read.table(text=line, sep = "\t",header = F,colClasses = "numeric"))
    df <- rbind(df, LineData)
  } else {
    summary(df)
    try(rgl.clear(type="shapes"),silent = T)
    basal = subset(df, df$V3==0)
    basal$V1 = basal$V1+25
    df <- rbind(df, basal)
    rgl.spheres(x=df$V1, y=df$V3, z=df$V2, r = 0.5, color = rgb(df$V4,df$V5,df$V6,df$V7)) 
    rm(df)
    df <- data.frame(matrix(NA, ncol = 7,nrow = 0))
  }
}

#/Users/schencro/Desktop/Darryl_collab/Framework/Homeostatic_Epidermis
#java -jar out/artifacts/Homeostatic_Epidermis_jar/Homeostatic_Epidermis.jar | Rscript tmp.R 2>&1


