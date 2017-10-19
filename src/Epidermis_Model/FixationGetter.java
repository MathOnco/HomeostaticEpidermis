package Epidermis_Model;

import java.util.ArrayList;

/**
 * Created by schencro on 10/19/17.
 */

public class FixationGetter {
    private double Frequency = 0.0;
    private String Mutation;
    EpidermisGrid theGrid;
    ArrayList<String> MutList;

    public FixationGetter(EpidermisGrid theGrid, ArrayList<String> muts) {
        this.MutList = muts;
        this.MutList.add("1.44.C.4500,");
        this.theGrid = theGrid;
        CheckFixation();
    }

    public void CheckFixation(){
        int MutCells = 0;
        int CellCount = 0;
        for (int g = 0; g < MutList.size(); g++) {
            MutCells = 0;
            CellCount = 0;
            for (int i = 0; i < (EpidermisConst.ySize * EpidermisConst.xSize * EpidermisConst.zSize); i++) {
                EpidermisCell c = theGrid.GetAgent(i);
                if (c != null) {
                    if(c.cellGenome.size()>0){
                        if(c.cellGenome.get(0).contains(MutList.get(g))){
                            MutCells++;
                        }
//                        if(c.myGenome.GenomeInfoStr().contains(MutList.get(g))){
//                            MutCells++;
//                        }
                    }
                    CellCount++;
                }
            }
            if( ((MutCells*1.0)/CellCount) >0){
                Frequency = (MutCells*1.0/CellCount);
                Mutation = MutList.get(g);
            } else {
                Mutation = null;
                Frequency = 5.0; // Kills Program, Mutation Lost
            }
        }
    }

    public double GetFreq(){ return Frequency; }

    public String GetMut(){ return Mutation; }

    /* Depracated because it always occurs at start */
    public int MutTime(){
        String[] MutParts = Mutation.split(".");
        return Integer.parseInt(MutParts[1]);
    }
}

