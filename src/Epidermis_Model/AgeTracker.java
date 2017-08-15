package Epidermis_Model;

/**
 * Created by schencro on 8/15/17.
 */
public class AgeTracker {
    private int[] Ages;
    private double[] AverageAge;
    EpidermisGrid theGrid;

    public AgeTracker(EpidermisGrid theGrid, int x, int y, int ModelTime){
        this.theGrid = theGrid;
        this.Ages = new int[x*y];
        this.AverageAge = new double[ModelTime + 1];
    }

    public void SetAge(int i, int CellAge){ Ages[i] = CellAge; }

    public double GetMeanAge() {
        double tmpAge = 0;
        int index=0;
        for (int i = 0; i < Ages.length; i++) {
            if(Ages[i]!=0){
                tmpAge += Ages[i];
                index++;
            }
        }
        AverageAge[theGrid.GetTick()] = tmpAge/index;
        return tmpAge/index;
    }

}
