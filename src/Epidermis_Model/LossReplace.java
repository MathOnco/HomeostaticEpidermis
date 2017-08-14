package Epidermis_Model;

/**
 * Created by schencro on 8/14/17.
 */

public class LossReplace {
    private int DivisionBasal = 0;
    private int DivisionTissue = 0;
    private int DeathBasal = 0;
    private int DeathTissue = 0;
    private double[] LossRateBasal;
    private double[] LossRateTissue;
    private double[] BirthRateBasal;
    private double[] BirthRateTissue;
    EpidermisGrid theGrid;

    public LossReplace(EpidermisGrid GridObj, int ModelTime, int Step){ // Initialize the LossReplacement Fetcher
        this.theGrid = GridObj;
        this.LossRateBasal = new double[ModelTime/Step+1];
        this.LossRateTissue = new double[ModelTime/Step+1];
        this.BirthRateBasal = new double[ModelTime/Step+1];
        this.BirthRateTissue = new double[ModelTime/Step+1];
    }

    public double RecordBasalRate(String Option, int Step){
        double Rate=0.0;
        if(Option=="Birth"){
            Rate=(DivisionBasal*1.0)/ (theGrid.xDim*theGrid.zDim * Step);
            BirthRateBasal[theGrid.GetTick()] = Rate;
        } else if(Option=="Death"){
            Rate=(DeathBasal*1.0)/ (theGrid.xDim*theGrid.zDim * Step);
            LossRateBasal[theGrid.GetTick()] = Rate;
        }
        ResetBasalCounters();
        return Rate;
    }

    public double RecordTissueRate(String Option, int Step){
        double Rate=0.0;
        if(Option=="Birth"){
            Rate=(DivisionTissue*1.0)/ (theGrid.xDim*theGrid.zDim * Step);
            BirthRateTissue[theGrid.GetTick()] = Rate;
        } else if(Option=="Death"){
            Rate=(DeathTissue*1.0)/ (theGrid.xDim*theGrid.zDim * Step);
            LossRateTissue[theGrid.GetTick()] = Rate;
        }
        ResetTissueCounters();
        return Rate;
    }

    public void ResetBasalCounters(){
        DivisionBasal=0;
        DeathBasal=0;
    }

    public void ResetTissueCounters(){
        DivisionTissue=0;
        DeathTissue=0;
    }

    public void RecordDivide(int y){
        if(y==0){
            DivisionBasal++;
            DivisionTissue++;
        } else {
            DivisionTissue++;
        }
    }

    public void RecordLoss(int y) {
        if (y == 0) {
            DeathBasal++;
            DeathTissue++;
        } else if (y == -1) {
            DeathBasal++; // Special case for movement out of basal only
        }else{
            DeathTissue++;
        }
    }

    public int GetBasalDivisions(){ return DivisionBasal; }

    public int GetTissueDivisions(){ return DivisionTissue; }

    public int GetBasalDeath(){ return DeathBasal; }

    public int GetTissueDeath(){ return DeathTissue; }

}
