package Epidermis_Model;

import AgentFramework.*;
import AgentFramework.Gui.GuiVis;

import static AgentFramework.Utils.GetHSBtoRGB;
import static Epidermis_Model.EpidermisConst.*;
import static java.awt.Color.HSBtoRGB;

import java.io.FileWriter;
import java.util.Random;

/**
 * Created by schencro on 3/31/17.
 */


// Grid specific parameters
class EpidermisGrid extends Grid3unstackable<EpidermisCell> {
    final Random RN=new Random();
    static final int[] divHoodBasal={1,0,0, -1,0,0, 0,0,1, 0,0,-1, 0,1,0}; // Coordinate set for two beside and one above and two front and back [x,y,z,x,y,z...]
    static final int[] moveHood={1,0,0, -1,0,0, 0,0,1, 0,0,-1, 0,-1,0};
    static final int[] inBounds= new int[5];
    static final double EGF_DIFFUSION_RATE=0.08; //keratinocyte growth factor
    static final double DECAY_RATE=0.001; //chemical decay rate of growth factors
    static final double SOURCE_EGF=1; //constant level at basement
    static final int AIR_HEIGHT=15; //air, keratinocyte death! (threshold level for placement of keratinocytes essentially)
    static final int CHEMICAL_STEPS=100; // number of times diffusion is looped every tick
    static double[][][][] ImageArray = new double[EpidermisConst.ySize][EpidermisConst.xSize][EpidermisConst.zSize][4];
    static GenomeInfo[][] StateChange = new GenomeInfo[EpidermisConst.xSize][EpidermisConst.zSize]; // Measuring World Volatility
    static double[] Volatility = new double[RecordTime];
    boolean running;
    float r_lambda_weekly = 0;
    int xDim;
    int yDim;
    int zDim;
    long popSum=0;
    int[] MeanProlif = new int[EpidermisConst.xSize * EpidermisConst.ySize * EpidermisConst.zSize];
    int[] MeanDeath = new int[EpidermisConst.xSize * EpidermisConst.ySize * EpidermisConst.zSize];
    GenomeTracker<EpidermisCellGenome> GenomeStore;
    GridDiff3 EGF;

    public EpidermisGrid(int x, int y, int z) {
        super(x,y,z,EpidermisCell.class);
        running = false;
        xDim = x;
        yDim = y;
        zDim = z;
        EGF = new GridDiff3(x, y, z);
        GenomeStore = new GenomeTracker<>(new EpidermisCellGenome(0f,0f,1f,"", this), true, true);
        PlaceCells();
        GetState(StateChange);
    }

    public void PlaceCells() {
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < AIR_HEIGHT; y++) {
                for (int z = 0; z < xDim; z++) {
                    if (GetAgent(x, y, z) == null) {
                        EpidermisCell c = NewAgent(x, y, z);
                        c.init(KERATINOCYTE, GenomeStore.NewProgenitor()); // Initializes cell types; Uniform Start
                    }
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
        popSum+=Pop();
        CleanShuffInc(RN); // Special Sauce
        GetState(StateChange);
    }

    public void DrawChemicals(GuiVis chemVis, boolean egf, boolean bfgf) {
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                    if (egf) {
                        chemVis.SetColorHeat(x, y, EGF.GetCurr(x, y, zDim/2) / SOURCE_EGF, "rgb");
                }
            }
        }
    }

    public void ActivityHeatMap(GuiVis heatVis, EpidermisGrid Epidermis, EpidermisCellVis CellDraw, int[] MeanLife, String heatColor) {
        for(int i=0; i<MeanLife.length; i++){
            if(MeanLife[i]!=0) {
                heatVis.SetColorHeat(ItoX(i), ItoY(i), MeanLife[i] / (float)EpidermisConst.VisUpdate, heatColor);
            } else {
                heatVis.SetColor(ItoX(i),ItoY(i), 0.0f, 0.0f, 0.0f);
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

    public float GetOldestCell(EpidermisGrid Epidermis){
        float Age = 0;
        int aliveCells = 0;
        for (EpidermisCell c: this) {
            if(c!=null){
                Age += c.Age();
                aliveCells += 1;
            }
        }
        return Age/aliveCells;
    }

    public void DrawCellActivity(GuiVis vis, EpidermisGrid Epidermis, EpidermisCellVis CellDraw) {
        long time = System.currentTimeMillis();
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                    EpidermisCell c = Epidermis.GetAgent(x, y, zDim/2);
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
                EpidermisCell c = Epidermis.GetAgent(x, y, zDim/2);
                if (c != null) {
                    CellDraw.DrawCellonGridPop(vis, c);
                } else {
                    CellDraw.DrawEmptyCell(vis, x, y);
                }
            }
        }
    }

    public void DrawCellPopsBottom(GuiVis vis, EpidermisGrid Epidermis, EpidermisCellVis CellDraw){
        for(int x=0; x < xDim; x++) {
            for(int z=0; z<zDim; z++) {
                EpidermisCell c = Epidermis.GetAgent(x, 0, z);
                if (c != null){
                    CellDraw.DrawCellonGridPopZ(vis, c);
                } else {
                    CellDraw.DrawEmptyCell(vis, x, z);
                }
            }
        }
    }

    public void DrawCellPopsBottomActivity(GuiVis vis, EpidermisGrid Epidermis, EpidermisCellVis CellDraw) {
        long time = System.currentTimeMillis();
        for (int x = 0; x < xDim; x++) {
            for (int z = 0; z < zDim; z++) {
                EpidermisCell c = Epidermis.GetAgent(x, 0, z);
                if (c != null) {
                    CellDraw.DrawCellonGrid3D(vis, c);
                } else {
                    CellDraw.DrawEmptyCell(vis, x, z);
                }
            }
        }
    }

    public void BuildMathematicaArray(){
        for(int i=0; i < (EpidermisConst.ySize*EpidermisConst.xSize*EpidermisConst.zSize);i++){
            EpidermisCell c = GetAgent(i);
            if (c != null){
                if(c.myGenome.h ==1.0){
                    ImageArray[ItoY(i)][ItoX(i)][ItoZ(i)][0] = 1.0;
                    ImageArray[ItoY(i)][ItoX(i)][ItoZ(i)][1] = 1.0;
                    ImageArray[ItoY(i)][ItoX(i)][ItoZ(i)][2] = 1.0;
                    ImageArray[ItoY(i)][ItoX(i)][ItoZ(i)][3] = 0.1;
                } else {
                    ImageArray[ItoY(i)][ItoX(i)][ItoZ(i)][0] = GetHSBtoRGB(c.myGenome.h,c.myGenome.s,c.myGenome.v)[0];
                    ImageArray[ItoY(i)][ItoX(i)][ItoZ(i)][1] = GetHSBtoRGB(c.myGenome.h,c.myGenome.s,c.myGenome.v)[1];
                    ImageArray[ItoY(i)][ItoX(i)][ItoZ(i)][2] = GetHSBtoRGB(c.myGenome.h,c.myGenome.s,c.myGenome.v)[2];
                    ImageArray[ItoY(i)][ItoX(i)][ItoZ(i)][3] = 0.80;
                }
            } else {
                ImageArray[ItoY(i)][ItoX(i)][ItoZ(i)][0] = 0.0;
                ImageArray[ItoY(i)][ItoX(i)][ItoZ(i)][1] = 0.0;
                ImageArray[ItoY(i)][ItoX(i)][ItoZ(i)][2] = 0.0;
                ImageArray[ItoY(i)][ItoX(i)][ItoZ(i)][3] = 0.0;
            }
        }
    }

    // Inflicting a wound to simulate wound repair...
    public void inflict_wound(int size){
        for(int i=0; i < (EpidermisConst.ySize*EpidermisConst.xSize*EpidermisConst.zSize);i++) {
            EpidermisCell c = GetAgent(i);
            if(c!=null){
                continue;
            }
        }
    }

    public boolean checkWoundHeal(int AvgHeight){
        if(GetAgent(xDim/2, 0, zDim/2)!=null){
            return true;
        } else {
            return false;
        }
    }

    public void GetCellPositions(FileIO PositionOut){
        for(int i=0; i < (EpidermisConst.ySize*EpidermisConst.xSize*EpidermisConst.zSize);i++) {
            EpidermisCell c = GetAgent(i);
            if(c!=null){
                String OutString = ItoX(i) + "," + ItoY(i) + "," + ItoZ(i) + "," + c.myGenome.IDGetter() + "\n";
                PositionOut.Write(OutString);
            }
        }
    }

    public void GetState(GenomeInfo[][] StateArray){
        int StateChanges = 0;
        for(int x=0; x<EpidermisConst.xSize; x++){
            for (int z = 0; z < EpidermisConst.zSize; z++) {
                EpidermisCell c = GetAgent(x,0,z);
                if(c!=null){
                    if(c.myGenome != StateArray[x][z]){
                        StateChanges++;
                        StateArray[x][z] = c.myGenome;
                    }
                }
            }
        }
        if((StateChanges *1.0)/(EpidermisConst.xSize*EpidermisConst.zSize)!=0.0){
            Volatility[GetTick()] = (StateChanges *1.0)/(EpidermisConst.xSize*EpidermisConst.zSize);
        }
    }

    public void WriteStateChange(FileIO StateChange){
        for (int i = 0; i < Volatility.length; i++) {
            String outLine = i + "," + Volatility[i] + "\n";
            StateChange.Write(outLine);
        }
    }

    public void ChemicalLoop(){
        //DIFFUSION
        EGF.Diffuse(EGF_DIFFUSION_RATE,false,0,true, false, true);
        //CELL CONSUMPTION
        for (EpidermisCell c: this) {
                EGF.AddNext(c.Xsq(),c.Ysq(),c.Zsq(), c.KERATINO_EGF_CONSPUMPTION*EGF.GetCurr(c.Xsq(), c.Ysq(), c.Zsq()));
        }

        //DECAY RATE
        for(int i=0;i<EGF.length;i++){
            EGF.SetNext(ItoX(i),ItoY(i),ItoZ(i), EGF.GetNext(ItoX(i), ItoY(i), ItoZ(i))*(1.0-DECAY_RATE));
        }

        //SOURCE ADDITION
        for(int x=0;x<xDim;x++) {
            for(int z=0;z<zDim;z++) {
                EGF.SetNext(x, 0, z, SOURCE_EGF);
            }
        }

        //SWAP CURRENT FOR NEXT
        EGF.SwapNextCurr();
    }

    public void GetEGFVal(){
        StringBuilder EGFCons = new StringBuilder();
        for (int y=0; y < yDim; y++) {
            String out = String.valueOf(EGF.GetCurr(xDim/2, y, zDim/2)) + "\t";
            EGFCons.append(out);
        }
        System.out.println(EGFCons.toString());
    }
}
