package Epidermis_Model;

import AgentFramework.Grid2;
import AgentFramework.GridDiff2;
import AgentFramework.Gui.Gui;
import AgentFramework.Gui.GuiVis;
import static AgentFramework.Utils.*;
import static Epidermis_Model.EpidermisConst.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by schencro on 3/31/17.
 */


// Grid specific parameters
class EpidermisGrid extends Grid2<EpidermisCell> {
    final Random RN=new Random();
    static final int[] divHoodBasal={1,0,-1,0,0,1}; // Coordinate set for two beside and one above [x,y,x,y...]
    static final int[] divHood={1,0,-1,0,0,1,0,-1}; // Coordinate set for two beside and one above and one below [x,y,x,y...]
    static final int[] inBounds= new int[4];
    static final double EGF_DIFFUSION_RATE=0.08; //keratinocyte growth factor
    static final double DECAY_RATE=0.01; //chemical decay rate of growth factors
    static final double SOURCE_EGF=1; //constant level at basement
    static final double SOURCE_BFGF=0.1; //constant level at basement
    static final int AIR_HEIGHT=15; //air, keratinocyte death! (threshold level for placement of keratinocytes essentially)
    static final int CHEMICAL_STEPS=100; // number of times diffusion is looped every tick
    boolean running;
    float r_lambda_weekly = 0;
    int xDim;
    int yDim;
    int[] MeanProlif = new int[EpidermisConst.xSize * EpidermisConst.ySize];
    int[] MeanDeath = new int[EpidermisConst.xSize * EpidermisConst.ySize];
    ArrayList<HashMap<String,Integer>> muellerList;  // timestep array[ {ClonalPopID:PopSize, ...}, {...} ]
    ArrayList<String[]> lineages;
    GridDiff2 EGF;

    public EpidermisGrid(int x, int y) {
        super(x,y,EpidermisCell.class);
        lineages=new ArrayList<String[]>();
        lineages.add(new String[]{"None"});//0th generation: parent of all cells
        lineages.add(new String[]{"0.1"});//1st generation: starting population
        muellerList=new ArrayList<HashMap<String,Integer>>();  // timestep array[ {ClonalPopID:PopSize, ...}, {...} ]
        running = false;
        xDim = x;
        yDim = y;
        EGF = new GridDiff2(x, y);
        PlaceCells();
    }

//    public void MuellerListTimestepBuilder(){
//        HashMap<String, Integer> tickInfo = new HashMap<String, Integer>();
//        SkinCell c=cells.FirstAgentAll();
//        while(c!=null){
//            if(c.myType==KERATINOCYTE) {
//                String key = c.ParentCloneID + "\t" + c.cloneID;
//                if(tickInfo.get(key)!=null) {
//                    tickInfo.put(key,tickInfo.get(key)+1);
//                }
//                else{
//                    tickInfo.put(key,1);
//                }
//            }
//            c=cells.NextAgentAll();
//        }
//        muellerList.add(tickInfo);
//    }

    public void PlaceCells() {
        EpidermisCellGenome startingGenomeVals = new EpidermisCellGenome(); // Creating genome class within each cell
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < AIR_HEIGHT; y++) {
                if (SQtoAgent(x,y) == null) {
                    EpidermisCell c = NewAgent(x, y);
                    c.init(KERATINOCYTE, 1.0f, 1.0f, 1.0f, startingGenomeVals, 0, 1, 0); // Initializes cell types; Uniform Start
                }
            }
        }
    }


    public void RunStep() {
        for (int i = 0; i < CHEMICAL_STEPS; i++) {
            ChemicalLoop();
        }
        for (EpidermisCell c: this) {
            c.CellStep();
            MeanProlif(c);
//            MeanDeath();
        }
        CleanShuffInc(RN); // Special Sauce
    }

    public void DrawChemicals(GuiVis chemVis, boolean egf, boolean bfgf) {
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                if (egf) {
                    chemVis.SetColorHeat(x, y, EGF.SQgetCurr(x, y) / SOURCE_EGF, "rgb");
                }
            }
        }
    }

    public void ActivityHeatMap(GuiVis heatVis, EpidermisGrid Epidermis, EpidermisCellVis CellDraw, int[] MeanLife, String heatColor) {
        for(int i=0; i<MeanLife.length; i++){
            if(MeanLife[i]!=0) {
                heatVis.SetColorHeat(ItoX(i), ItoY(i), MeanLife[i] / (float)EpidermisConst.VisUpdate, heatColor);
            } else {
                heatVis.SetColor(ItoX(i),ItoY(i), 0f, 0f, 0f);
            }
        }
    }

    public void LayerVis(GuiVis heatVis, EpidermisGrid Epidermis, EpidermisCellVis CellDraw, int[] MeanLife, String heatColor) {
        int[] MeanLayer = new int[EpidermisConst.ySize];
        for(int i=0; i<MeanLife.length; i++){
            int y = ItoY(i);
            MeanLayer[y] += MeanLife[i];
            MeanLife[i] = 0;
        }
        for(int y = 0; y<MeanLayer.length; y++) {
            float LayerAvg = MeanLayer[y] / (EpidermisConst.xSize * (float) EpidermisConst.VisUpdate);
                for (int x = 0; x < EpidermisConst.xSize; x++) {
                    heatVis.SetColorHeat(x, y, LayerAvg, heatColor);
                }
        }
    }

    public void MeanProlif(EpidermisCell c){
            if(c.Action == DIVIDE){
                MeanProlif[c.Isq()] += 1;
            }
    }


    public void DrawCellActivity(GuiVis vis, EpidermisGrid Epidermis, EpidermisCellVis CellDraw) {
        long time = System.currentTimeMillis();
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                EpidermisCell c = Epidermis.SQtoAgent(x, y);
                if (c != null) {
                        CellDraw.DrawCellonGrid(vis, c);
                } else {
                    CellDraw.DrawEmptyCell(vis, x, y);
                }
            }
        }
    }

    public void DrawCellPops(GuiVis vis, EpidermisGrid Epidermis, EpidermisCellVis CellDraw){
        long time = System.currentTimeMillis();
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                EpidermisCell c = Epidermis.SQtoAgent(x, y);
                if (c != null) {
                        CellDraw.DrawCellonGridPop(vis, c);
                } else {
                    CellDraw.DrawEmptyCell(vis, x, y);
                }
            }
        }
    }

//    public String ToString() {
//        String ret = "#" + cells.Tick() + "\n";
//        for (int x = 0; x < xDim; x++) {
//            for (int y = 0; y < yDim; y++) {
//                SkinCell c = cells.FirstOnSquare(x, y);
//                if (c != null) {
//                    if (c.myType == KERATINOCYTE) {
//                        ret += c.ToString() + "\n";
//
//                    }
//
//                }
//            }
//        }
//        return ret;
//    }

    // Inflicting a wound to simulate wound repair...
    public void inflict_wound(){
        for (int i = 10; i < xDim-20; i++){
            for (int k=0; k < 20; k++){
                EpidermisCell c = SQtoAgent(i,k);
                if (c != null) {
                    c.itDead();
                }
            }
        }
    }

    public void ChemicalLoop(){
        //DIFFUSION
        EGF.Diffuse(EGF_DIFFUSION_RATE,false,0,true);
        //CELL CONSUMPTION
        for (EpidermisCell c: this) {
                EGF.IaddNext(c.Isq(), c.KERATINO_EGF_CONSPUMPTION*EGF.IgetCurr(c.Isq()));
        }

        //DECAY RATE
        for(int i=0;i<EGF.length;i++){
            EGF.IsetNext(i, EGF.IgetNext(i)*(1.0-DECAY_RATE));
        }

        //SOURCE ADDITION
        for(int x=0;x<xDim;x++) {
            EGF.SQsetNext(x,0,SOURCE_EGF);
        }

        //SWAP CURRENT FOR NEXT
        EGF.SwapNextCurr();
    }
}
