package Epidermis_Model;

/**
 * Created by schencro on 8/15/17.
 */
public class AgeTracker {
    private int[] Ages;
    EpidermisGrid theGrid;

    public AgeTracker(EpidermisGrid theGrid, int x, int y){
        this.theGrid = theGrid;
        this.Ages = new int[x*y];
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
        return tmpAge/index;
    }

}
