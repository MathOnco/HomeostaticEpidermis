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
    static final int[] divHood={1,0,-1,0,0,1}; // Coordinate set for two beside and one above [x,y,x,y...]
    static final int[] inBounds= new int[3];
    static final double EGF_DIFFUSION_RATE=0.08; //keratinocyte growth factor
    static final double BFGF_DIFFUSION_RATE=0.07; //melanocyte growth factor
    static final double DECAY_RATE=0.01; //chemical decay rate of growth factors
    static final double SOURCE_EGF=1; //constant level at basement
    static final double SOURCE_BFGF=0.1; //constant level at basement
    static final int AIR_HEIGHT=15; //air, keratinocyte death! (threshold level for placement of keratinocytes essentially)
    static final int DENSITY_SEARCH_SIZE=4; //Square by which 4 sides in all directions make a square, (4 is the radius), for keratinocytes.
    static final int CHEMICAL_STEPS=100; // number of times diffusion is looped every tick
    static final int INIT_MELANOCYTE_COUNT=0; // number of starting melanocytes
    boolean running;
    float r_lambda_weekly = 0;
    int xDim;
    int yDim;
    ArrayList<HashMap<String,Integer>> muellerList;  // timestep array[ {ClonalPopID:PopSize, ...}, {...} ]
    ArrayList<String[]> lineages;
    GridDiff2 EGF;
    GridDiff2 BFGF;

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
        BFGF = new GridDiff2(x, y);
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
        int[] xPositions = RandomIndices(xSize, INIT_MELANOCYTE_COUNT, RN); //Place Melanocytes
        for (int i = 0; i < INIT_MELANOCYTE_COUNT; i++) {
            EpidermisCell c = NewAgent(xPositions[i], 0);
            c.init(MELANOCYTE, 0, 0, 0, startingGenomeVals, 0, 0, 1);
        }
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
        }
        CleanShuffInc(RN); // Special Sauce
    }

    public void DrawChemicals(GuiVis chemVis, boolean egf, boolean bfgf) {
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                if (egf) {
                    chemVis.SetColorHeat(x, y, EGF.SQgetCurr(x, y) / SOURCE_EGF, "rgb");
                }
                if (bfgf) {
                    chemVis.SetColorHeat(x, y, BFGF.SQgetCurr(x, y) / SOURCE_BFGF, "bgr");
                }
            }
        }
    }

//    public void ActivityHeatMap(GuiVis heatVis, EpidermisGrid Epidermis, EpidermisCellVis CellDraw) {
//        int[] means= MeanProlif(Epidermis);
//        for(int i=0; i<means.length; i++){
//            heatVis.SetColorHeat(ItoX(i), ItoY(i), means[i], "gbr");
//        }
//    }
//
//    public int[] MeanProlif(EpidermisGrid Cells){
//        int[] means = new int[Cells.length];
//        for(int i=0; i<means.length; i++){
//            means[i]=Cells.ProWeekCount/7f;
//            int t =0;
//        }
//        return means;
//    }

    public void DrawCellActivity(GuiVis vis, EpidermisGrid Epidermis, EpidermisCellVis CellDraw) {
        long time = System.currentTimeMillis();
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                EpidermisCell c = Epidermis.SQtoAgent(x, y);
                if (c != null) {
                    if (c.myType == KERATINOCYTE) {
                        CellDraw.DrawCellonGrid(vis, c);
                    } else if (c.myType == MELANOCYTE) {
                        vis.SetColor(x, y, 0.0f, 0.0f, 1.0f);
                    }
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
                    if (c.myType == KERATINOCYTE) {
                        CellDraw.DrawCellonGridPop(vis, c);
                    } else if (c.myType == MELANOCYTE) {
                        vis.SetColor(x, y, 0.0f, 0.0f, 1.0f);
                    }
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
        for (int i = 5; i < 100; i++){
            for (int k=2; k < 20; k++){
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
        BFGF.Diffuse(BFGF_DIFFUSION_RATE,false,0,true);
        //CELL CONSUMPTION
        for (EpidermisCell c: this) {
            if(c.myType==KERATINOCYTE){
                EGF.IaddNext(c.Isq(), c.KERATINO_EGF_CONSPUMPTION*EGF.IgetCurr(c.Isq()));
            }
            else if(c.myType==MELANOCYTE){
                BFGF.IaddNext(c.Isq(), c.MELANO_BFGF_CONSUMPTION*BFGF.IgetCurr(c.Isq()));
            }
        }

        //DECAY RATE
        for(int i=0;i<EGF.length;i++){
            EGF.IsetNext(i, EGF.IgetNext(i)*(1.0-DECAY_RATE));
        }
        for(int i=0;i<BFGF.length;i++){
            BFGF.IsetNext(i, BFGF.IgetNext(i)*(1.0-DECAY_RATE));
        }

        //SOURCE ADDITION
        for(int x=0;x<xDim;x++) {
            EGF.SQsetNext(x,0,SOURCE_EGF);
            BFGF.SQsetNext(x,0,SOURCE_BFGF);
        }

        //SWAP CURRENT FOR NEXT
        EGF.SwapNextCurr();
        BFGF.SwapNextCurr();
    }
}
