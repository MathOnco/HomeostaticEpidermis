package Epidermis_Model;

import AgentFramework.Grid3;

import java.util.ArrayList;

/**
 * Created by schencro on 8/10/17.
 */
public class FixationGetter {
    private double Frequency;
    private String Mutation;
    EpidermisGrid theGrid;
    ArrayList<String> MutList;

    public FixationGetter(EpidermisGrid theGrid, ArrayList<String> muts) {
        this.theGrid = theGrid;
        this.MutList = muts;
        CheckFixation();
    }

    // TODO Figure out why this isn't working?!?!?!?!?!?!?!?!?!?!?!
    private void CheckFixation(){
        int MutCells = 0;
        int CellCount = 0;
        for (int g = 0; g < MutList.size(); g++) {
            MutCells = 0;
            CellCount = 0;
            for (int i = 0; i < (EpidermisConst.ySize * EpidermisConst.xSize * EpidermisConst.zSize); i++) {
                EpidermisCell c = theGrid.GetAgent(i);
                if (c != null) {
                    if(c.myGenome.GenomeInfoStr().contains(MutList.get(g))){
                        MutCells++;
                    }
                    CellCount++;
                }
            }
            if(MutCells*1.0/CellCount==1.0){
                Frequency = MutCells*1.0/CellCount;
                Mutation = MutList.get(g);
            } else {
                Mutation = null;
            }
        }
    }

    public double GetFreq(){ return Frequency; }

    public String GetMut(){ return Mutation; }

    public int MutTime(){
        String[] MutParts = Mutation.split(".");
        return Integer.parseInt(MutParts[1]);
    }
}
