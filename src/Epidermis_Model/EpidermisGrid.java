package Epidermis_Model;


import Epidermis_Model.Genome.GenomeTracker;
import Framework.Grids.Grid2;
import Framework.Grids.GridDiff2;
import Framework.Gui.GuiGridVis;
import Framework.Tools.FileIO;

import static Epidermis_Model.EpidermisConst.*;

import java.util.Random;

/**
 * Created by schencro on 3/31/17.
 */


// Grid specific parameters
class EpidermisGrid extends Grid2<EpidermisCell> {
    final Random RN=new Random();
    static final int[] divHoodBasal={1,0,-1,0,0,1}; // Coordinate set for two beside and one above [x,y,x,y...]
    static final int[] divHood={1,0,-1,0,0,1,0,-1}; // Coordinate set for two beside and one above and one below [x,y,x,y...]
    static final int[] moveHood={1,0,-1,0,0,-1};
    final int[] inBounds= new int[4];
    double EGF_DIFFUSION_RATE=0.08; //keratinocyte growth factor
    double DECAY_RATE=0.001; //chemical decay rate of growth factors
    static final double SOURCE_EGF=1; //constant level at basement
    static final int AIR_HEIGHT=15; //air, keratinocyte death! (threshold level for placement of keratinocytes essentially)
    static final int CHEMICAL_STEPS=100; // number of times diffusion is looped every tick
    boolean running;
    int xDim;
    int yDim;
    long popSum=0;
    int[] MeanProlif = new int[EpidermisConst.xSize * EpidermisConst.ySize];
    int[] MeanDeath = new int[EpidermisConst.xSize * EpidermisConst.ySize];
    GenomeTracker<EpidermisCellGenome> GenomeStore;
    LossReplace Turnover;
    AgeTracker TrackAge;
    GridDiff2 EGF;
    double[] runParams;

    public EpidermisGrid(int x, int y, double[] customParams) {
        super(x,y,EpidermisCell.class);
        running = false;

        runParams = customParams;

        xDim = x;
        yDim = y;
        EGF = new GridDiff2(x, y, true, false);
        GenomeStore = new GenomeTracker<>(new EpidermisCellGenome(0f,0f,1f,"", this), true, true);
        Turnover = new LossReplace(this, ModelTime, 7);
        TrackAge = new AgeTracker(this, xDim, yDim, ModelTime);

        EGF_DIFFUSION_RATE = runParams[6];
        DECAY_RATE = runParams[7];
        PlaceCells();
    }

    public void PlaceCells() {
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < AIR_HEIGHT; y++) {
                if (GetAgent(x,y) == null) {
                    EpidermisCell c = NewAgentSQ(x, y);
                    c.init(KERATINOCYTE, GenomeStore.NewProgenitor()); // Initializes cell types; Uniform Start
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
        }
        popSum+=GetPop();
        CleanShuffInc(RN); // Special Sauce

        for (int i = 0; i < xDim*yDim; i++) {
            EpidermisCell c = GetAgent(i);
            if(c!=null){
                TrackAge.SetAge(i,c.Age());
            } else {
                TrackAge.SetAge(i,0);
            }
        }
        Turnover.RecordBasalRate("Death");
        Turnover.RecordBasalRate("Birth");
        Turnover.RecordTissueRate("Birth");
        Turnover.RecordTissueRate("Death");
    }

    public void DrawChemicals(GuiGridVis chemVis, boolean egf, boolean bfgf) {
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                if (egf) {
                    chemVis.SetColorHeat(x, y, EGF.GetCurr(x, y) / SOURCE_EGF, "rgb");
                }
            }
        }
    }

    public void ActivityHeatMap(GuiGridVis heatVis, EpidermisGrid Epidermis, EpidermisCellVis CellDraw, int[] MeanLife, String heatColor) {
        for(int i=0; i<MeanLife.length; i++){
            if(MeanLife[i]!=0) {
                heatVis.SetColorHeat(ItoX(i), ItoY(i), MeanLife[i] / (float)EpidermisConst.VisUpdate, heatColor);
            } else {
                heatVis.SetColor(ItoX(i),ItoY(i), 0.0f, 0.0f, 0.0f);
            }
        }
    }

    public void LayerVis(GuiGridVis heatVis, EpidermisGrid Epidermis, EpidermisCellVis CellDraw, int[] MeanLife, String heatColor) {
        int[] MeanLayer = new int[EpidermisConst.ySize];
        for(int i=0; i<MeanLife.length; i++){
            int y = ItoY(i);
            MeanLayer[y] += MeanLife[i];
            MeanLife[i] = 0;
        }
        for(int y = 0; y<MeanLayer.length; y++) {
            float LayerAvg = MeanLayer[y] / (EpidermisConst.xSize * (float) EpidermisConst.VisUpdate);
                if(LayerAvg!=0) {
                    for (int x = 0; x < EpidermisConst.xSize; x++) {
                        heatVis.SetColorHeat(x, y, LayerAvg, heatColor);
                    }
                } else {
                    for (int x = 0; x < EpidermisConst.xSize; x++) {
                        heatVis.SetColor(x, y, 0.0f, 0.0f, 0.0f);
                    }
                }
        }
    }

    public void MeanProlif(EpidermisCell c){
            if(c.Action == DIVIDE){
                MeanProlif[c.Isq()] += 1;
            }
    }

    public double GetMeanCellHeight(){
        int allColumns = 0;
        for (int x = 0; x < xDim; x++) {
                int column = 0;
                for (int y = 0; y < yDim; y++) {
                    EpidermisCell c = GetAgent(x, y);
                    if(c!=null){
                        column++;
                    }
                }
                allColumns += column;
            }
        return (allColumns*1.0)/(xDim);
    }


    public void DrawCellActivity(GuiGridVis vis, EpidermisGrid Epidermis, EpidermisCellVis CellDraw) {
        long time = System.currentTimeMillis();
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                EpidermisCell c = Epidermis.GetAgent(x, y);
                if (c != null) {
                        CellDraw.DrawCellonGrid(vis, c);
                } else {
                    CellDraw.DrawEmptyCell(vis, x, y);
                }
            }
        }
    }

    public void DrawCellPops(GuiGridVis vis, EpidermisGrid Epidermis, EpidermisCellVis CellDraw){
        long time = System.currentTimeMillis();
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                EpidermisCell c = Epidermis.GetAgent(x, y);
                if (c != null) {
                        CellDraw.DrawCellonGridPop(vis, c);
                } else {
                    CellDraw.DrawEmptyCell(vis, x, y);
                }
            }
        }
    }

    // Inflicting a wound to simulate wound repair...
    public void inflict_wound(){
        for (int i = 37; i < 37*3; i++){
            for (int k=0; k < yDim; k++){
                EpidermisCell c = GetAgent(i,k);
                if (c != null) {
                    c.itDead();
                }
            }
        }
    }

    public void GetCellPositions(FileIO PositionOut){
        for (EpidermisCell c: this) {
            if(c!=null){
                String OutString = c.Xsq() + "," + c.Ysq() + "," + c.myGenome.IDGetter() + "\n";
                PositionOut.Write(OutString);
            }
        }
    }

    public boolean checkWoundHeal(int AvgHeight){
//        int pop=0;
//        for (int i = 37; i < 37*3; i++){
//            for (int k=0; k < yDim; k++) {
//                if(SQtoAgent(i,k)!=null){
//                    pop++;
//                }
//            }
//        }
//        if(pop/(37*2) >= AvgHeight){
        if(GetAgent(xDim/2, 0)!=null){
            return true;
        } else {
            return false;
        }
    }

    public void ChemicalLoop(){
        //DIFFUSION
        EGF.Diffuse(EGF_DIFFUSION_RATE);
        //CELL CONSUMPTION
        for (EpidermisCell c: this) {
                EGF.AddNext(c.Isq(), c.KERATINO_EGF_CONSPUMPTION*EGF.GetCurr(c.Isq()));
//                EGF.IaddNext(c.Isq(), -0.05*EGF.IgetCurr(c.Isq()));

        }

        //DECAY RATE
        for(int i=0;i<EGF.length;i++){
            EGF.SetNext(i, EGF.GetNext(i)*(1.0-DECAY_RATE));
        }

        //SOURCE ADDITION
        for(int x=0;x<xDim;x++) {
            EGF.SetNext(x,0,SOURCE_EGF);
        }

        //SWAP CURRENT FOR NEXT
        EGF.SwapNextCurr();
    }

    public void GetEGFVal(){
        StringBuilder EGFCons = new StringBuilder();
        for (int y=0; y < yDim; y++) {
            String out = String.valueOf(EGF.GetCurr(xDim/2, y)) + "\t";
            EGFCons.append(out);
        }
        //System.out.println(EGFCons.toString());
    }
}
