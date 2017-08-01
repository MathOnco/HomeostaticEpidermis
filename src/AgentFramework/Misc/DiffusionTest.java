package AgentFramework.Misc;

/**
 * Created by schencro on 5/24/17.
 */


import AgentFramework.FileIO;
import AgentFramework.GridDiff2;
import AgentFramework.GridDiff3;

import java.util.Arrays;

public class DiffusionTest {
    public static void main(String[] args){
        int xDim=10;
        int yDim=100;
        int zDim=10;
        int time=100;
        FileIO outFile2=new FileIO("diffTest2.csv","w");
        FileIO outFile3=new FileIO("diffTest3.csv","w");
        double[] outAvgs2=new double[yDim];
        double[] outAvgs3=new double[yDim];
        GridDiff2 g2=new GridDiff2(xDim,yDim);
        GridDiff3 g3=new GridDiff3(xDim,yDim,zDim);
        for (int i = 0; i < time; i++) {
            //setup source and sink
            for (int x = 0; x < xDim; x++) {
                g2.SQsetCurr(x,0,1);
            }
            for (int x = 0; x < xDim; x++) {
                for (int z = 0; z < zDim; z++) {
                    g3.SetCurr(x,0,z,1);
                }
            }
            //collect results
            for (int x = 0; x <xDim; x++) {
                for (int y = 0; y < yDim; y++) {
                    outAvgs2[y]+=g2.SQgetCurr(x,y);
                }
            }
            for (int j = 0; j < yDim; j++) {
                outAvgs2[j]/=(1.0*xDim);
            }
            for (int x = 0; x <xDim; x++) {
                for (int y = 0; y < yDim; y++) {
                    for (int z = 0; z < zDim; z++) {
                        outAvgs3[y] += g3.GetCurr(x, y, z);
                    }
                }
            }
            for (int j = 0; j < yDim; j++) {
                outAvgs3[j] /= (xDim * zDim*1.0);
            }
            if(i==time-1) {
                outFile2.WriteDelimit(outAvgs2, ",");
                outFile2.Write("\n");
                outFile3.WriteDelimit(outAvgs3, ",");
                outFile3.Write("\n");
            }
            //run diffusion
            g2.Diffuse(0.25,false,0.0,true,false);
            g2.SwapNextCurr();
            g3.DiffSwapInc(0.25,false,0.0,true,false,true);
            //System.out.println(Utils.PrintArr(outAvgs2,",")+"\n");
            Arrays.fill(outAvgs2,0);
            Arrays.fill(outAvgs3,0);
        }
        outFile2.Close();
        outFile3.Close();
    }
}
