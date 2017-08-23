package Epidermis_Model;


import Framework.Gui.GuiGridVis;
import Framework.Gui.GuiLabel;
import Framework.Gui.GuiWindow;
import Framework.Tools.FileIO;
import Framework.Tools.*;


import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by schencro on 3/24/17.
 */

//Holds Constants for rest of model
class EpidermisConst{
    static int xSize=200; // keratinocyte modal cell size = 15µm (Proc. Natl. Acad. Sci. USA Vol.82,pp.5390-5394,August1985; YANN BARRANDON and HOWARD GREEN) == volume == 1766.25µm^3
    // (Sampled area = 1mm-2mm^2); Sampled volume = 4.4*10^8µm^3 for ; Total cells needed for 2mm^2 area with depth of 140µm= 249115cells (xSize = 12456, ySize = 20);
    // For 1mm^2 area with depth of 140µm = 62279cells (xSize = 3114, ySize = 20);
    // Takes forever to reach even a year. Cutting the smallest biopsy into a quarter (1/4) = 15570cells (xSize = 1038, ySize = 20)
    static final int ySize=20;

    static final int KERATINOCYTE = 0; //setting types into a binary 0 or 1
    static final int DIVIDE = 2; // Attribute if cell is dividing
    static final int STATIONARY = 3; // Attribute if cell is stationary
    static final int MOVING = 4; //Attribute if cell is moving

    static int years=100; // time in years.
    static int RecordTime=years*365;
    static int ModelTime=years*365 + 10; // Time in days + 10 days after time for recording! e.g. 65 years = 23725

    static final int VisUpdate = 7; // Timestep interval to update Division and Death, etc.

    static final boolean GuiOn = true; // use for visualization
    static final boolean JarFile = false; // Set to true if running from command line as jar file
    static final boolean RecordAllPopSizes = false; // use to record all clone populations
    static final boolean TrackAll = false; // Use this if you want to record mutations outside the genes of interest.
    static final boolean RecordParents = true; // use when you want parents information
    static final boolean RecordLineages = true; // use when you want
    static final boolean RecordPopSizes = true; // Use to record clone population sizes
    static final boolean get_r_lambda = true; // use when you want the r_lambda value
    static final boolean writeValues = true; // Use when you want to write the output
    static final boolean positionInfo = true; // Use to get positions
    static final boolean RecordStateChange = true;
}

public class Epidermis_Main {

    static GuiLabel LabelGuiSet(String text, int compX, int compY) {
        GuiLabel ret= new GuiLabel(text, compX, compY);
        ret.SetColor(Color.white,Color.black);
        return ret;
    }

    public static void main (String[] args){
        /*
        Initialization
         */
        GuiWindow MainGUI=null;
        GuiGridVis ActivityVis = null;
        GuiGridVis EGFVis = null;
        GuiGridVis DivVis = null;
        GuiGridVis DivLayerVis = null;
        GuiGridVis DeathVis = null;
        GuiGridVis DeathLayerVis = null;
        GuiGridVis ClonalVis = null;
        GuiLabel YearLab = null;
        GuiLabel rLambda_Label = null;
        GuiLabel OldestCell = null;
        GuiLabel HealLab = null;
        GuiLabel HeightLab = null;
        EpidermisCellVis CellDraw = null;
        String ParentFile = System.getProperty("user.dir") + "/TestOutput/ParentFile.csv";
        String PopSizes = System.getProperty("user.dir") + "/TestOutput/PopSizes.csv";
        String MutationFile = System.getProperty("user.dir") + "/TestOutput/MutationFile.csv";
        String r_lambda_file = System.getProperty("user.dir") + "/TestOutput/R_Lambda_Values.csv";
        String PositionFile = System.getProperty("user.dir") + "/TestOutput/CellPositions.csv";
        String StateChangeFile = System.getProperty("user.dir") + "/TestOutput/VolatilityMetrics.csv";
        /*
        Sets up Data Files if on cluster or if ran locally
         */
        if(EpidermisConst.JarFile){
            ParentFile = args[0];
            PopSizes = args[1];
            MutationFile = args[2];
            r_lambda_file = args[3];
            EpidermisConst.xSize = Integer.parseInt(args[4]);
            int Time = Integer.parseInt(args[5]);
            EpidermisConst.years = Time;
            EpidermisConst.ModelTime = Time * 365 + 10;
            EpidermisConst.RecordTime = Time * 365;
            PositionFile = args[6];
            StateChangeFile = args[7];
        }
        if(EpidermisConst.GuiOn == false){
            System.out.println("xSize: " + EpidermisConst.xSize);
            System.out.println("Years: " + EpidermisConst.years);
        }

        final EpidermisGrid Epidermis = new EpidermisGrid(EpidermisConst.xSize, EpidermisConst.ySize); // Initializes and sets up the program for running
        Runtime rt = Runtime.getRuntime();

        // Sets up GUI
        if(EpidermisConst.GuiOn) {
            CellDraw = new EpidermisCellVis();
            MainGUI = new GuiWindow("Homeostatic Epidermis Model", true);
            MainGUI.panel.setOpaque(true);
            MainGUI.panel.setBackground(Color.black);
            ClonalVis = new GuiGridVis(EpidermisConst.xSize*5, EpidermisConst.ySize*5, 1, 2, 1);
            DivVis = new GuiGridVis(EpidermisConst.xSize, EpidermisConst.ySize, 3, 1, 1);
            DivLayerVis = new GuiGridVis(EpidermisConst.xSize, EpidermisConst.ySize, 3, 1, 1);
            DeathVis = new GuiGridVis(EpidermisConst.xSize, EpidermisConst.ySize, 3, 1, 1);
            DeathLayerVis = new GuiGridVis(EpidermisConst.xSize, EpidermisConst.ySize, 3, 1, 1);
            ActivityVis = new GuiGridVis(EpidermisConst.xSize * 5, EpidermisConst.ySize * 5, 1, 2, 1); // Main Epidermis visualization window
            EGFVis = new GuiGridVis(EpidermisConst.xSize, EpidermisConst.ySize, 5, 2, 1);
            YearLab = LabelGuiSet("Age (Yrs.): ", 1, 1);
            MainGUI.AddCol(YearLab, 0);
            HealLab = LabelGuiSet("Heal Time (Days): ", 1, 1);
            MainGUI.AddCol(HealLab, 0);
            HeightLab = LabelGuiSet("Height: ", 1, 1);
            MainGUI.AddCol(HeightLab, 0);
            OldestCell = LabelGuiSet("Oldest Cell: ", 1, 1);
            rLambda_Label = LabelGuiSet("rLambda: ", 1, 1);
            MainGUI.AddCol(rLambda_Label, 1);
            MainGUI.AddCol(OldestCell, 1);
            MainGUI.AddCol(LabelGuiSet("Population", 2, 1), 0);
            MainGUI.AddCol(ClonalVis, 0);
            MainGUI.AddCol(LabelGuiSet("Division (per week)", 1, 1), 0);
            MainGUI.AddCol(DivVis, 0);
            MainGUI.AddCol(LabelGuiSet("Division Layers (per week)", 1, 1), 1);
            MainGUI.AddCol(LabelGuiSet("Death (per week)", 1, 1), 0);
            MainGUI.AddCol(DeathVis,0);
            MainGUI.AddCol(DivLayerVis, 1);
            MainGUI.AddCol(LabelGuiSet("Death Layer (per week)", 1, 1), 1);
            MainGUI.AddCol(DeathLayerVis, 1);
            MainGUI.AddCol(LabelGuiSet("Epidermis", 2, 1), 0);
            MainGUI.AddCol(ActivityVis, 0); // Main Epidermis visualization window
            MainGUI.AddCol(LabelGuiSet("EGF", 2, 1), 0);
            MainGUI.AddCol(EGFVis, 0);

            MainGUI.RunGui();
        }
        int woundTick = 0;
        boolean Healed = true;
        double avgHeight=0;
        int tickSum=0;

        TickRateTimer tickIt = new TickRateTimer();
        while(Epidermis.GetTick() < EpidermisConst.ModelTime){

            Epidermis.RunStep();

            /*
            All Injuries Occuring Here!
             */
//            int healTick=0;
//
//            if(Healed && Epidermis.GetTick()==735){
//                Epidermis.inflict_wound();
//                tickIt.TickPause(0); // Adjusting a frame rate
//                woundTick=Epidermis.GetTick();
//                Healed = false;
//            }
//
//            if(!Healed && Epidermis.GetTick()!=15) {
//                Healed = Epidermis.checkWoundHeal((int) avgHeight);
//                healTick = Epidermis.GetTick();
//                if (Healed && HealLab != null) {
//                    if (HealLab != null) {
//                        HealLab.setText("Heal Time (Days): " + new DecimalFormat("#.0").format((healTick - woundTick)));
//                    }
//                }
//            }

            /*
            Output the EGF Values for first year
             */
            if(Epidermis.GetTick()<=365){Epidermis.GetEGFVal();}

            /*
            Output Time Options
             */
            if(ActivityVis==null){
                if(Epidermis.GetTick()%365==0){
                    System.out.println(new DecimalFormat("#.0").format((Epidermis.GetTick() / 365f)));
                }
            }
//            System.out.println(Epidermis.TrackAge.GetMeanAge());

//            System.out.println(Epidermis.Turnover.GetBasalRate("Death",Epidermis.GetTick()));

            if(Epidermis.GetTick()==EpidermisConst.ModelTime-1){
                System.out.println(Epidermis.GetDivisionProportion());
            }
            /*
            All Visualization Components are here
             */
            if(ActivityVis!=null){YearLab.SetText("Age (yrs.): " + new DecimalFormat("#.00").format((Epidermis.GetTick() / 365f)));}
            if(DivVis!=null&Epidermis.GetTick()%EpidermisConst.VisUpdate==0){Epidermis.ActivityHeatMap(DivVis, Epidermis, CellDraw, Epidermis.MeanProlif, "gbr");}
            if(DivLayerVis!=null&Epidermis.GetTick()%EpidermisConst.VisUpdate==0){Epidermis.LayerVis(DivLayerVis, Epidermis, CellDraw, Epidermis.MeanProlif, "gbr");}
            if(DeathVis!=null&Epidermis.GetTick()%EpidermisConst.VisUpdate==0){Epidermis.ActivityHeatMap(DeathVis, Epidermis, CellDraw, Epidermis.MeanDeath, "rbg");}
            if(DeathLayerVis!=null&Epidermis.GetTick()%EpidermisConst.VisUpdate==0){Epidermis.LayerVis(DeathLayerVis, Epidermis, CellDraw, Epidermis.MeanDeath, "rbg");}
            if(ClonalVis!=null){Epidermis.DrawCellPops(ClonalVis, Epidermis, CellDraw);}
            if(OldestCell!=null){OldestCell.SetText("Mean cell age (days): " + new DecimalFormat("#.00").format( Epidermis.GetAges()));}
            if(ActivityVis!=null){Epidermis.DrawCellActivity(ActivityVis, Epidermis, CellDraw);}
            if(EGFVis!=null){Epidermis.DrawChemicals(EGFVis, true, false);}

            if(HeightLab!=null){HeightLab.SetText("Height: " + new DecimalFormat("#.00").format(Epidermis.GetMeanCellHeight()));}
//            if(Epidermis.GetTick()%7==0){
//                if(rLambda_Label!=null){rLambda_Label.setText("Mean rLambda (per week): " + new DecimalFormat("#.000").format( Epidermis.Turnover.GetBasalRate("Death",7) ));}
//            }
            if(rLambda_Label!=null){rLambda_Label.SetText("Mean rLambda (per week): " + new DecimalFormat("#.000").format(Epidermis.Turnover.GetBasalRate("Death",Epidermis.GetTick())));}

            /*
            All Model Data Recording Is Below This line
             */
            if(EpidermisConst.RecordAllPopSizes == true){
                Epidermis.GenomeStore.RecordClonePops();
            }
            if(EpidermisConst.writeValues==true) {
                if (EpidermisConst.RecordParents == true && EpidermisConst.RecordTime == Epidermis.GetTick()) {
                    FileIO ParentOut = new FileIO(ParentFile, "w");
                    Epidermis.GenomeStore.WriteParentIDs(ParentOut, "\n");
                    ParentOut.Close();
                    System.out.println("Parents written to file.");
                }
                if (EpidermisConst.RecordLineages == true && EpidermisConst.RecordTime == Epidermis.GetTick()) {
                    FileIO MutsOut = new FileIO(MutationFile, "w");
                    Epidermis.GenomeStore.WriteAllLineageInfoLiving(MutsOut, ",", "\n");
                    MutsOut.Close();
                    System.out.println("Lineage genomes written to file.");
                }
                if (EpidermisConst.RecordPopSizes == true && EpidermisConst.RecordTime == Epidermis.GetTick()) {
                    FileIO PopSizeOut = new FileIO(PopSizes, "w");
                    if (EpidermisConst.RecordAllPopSizes == false) {
                        Epidermis.GenomeStore.RecordClonePops(); // Used when you only what last time point
                    }
                    Epidermis.GenomeStore.WriteClonePops(PopSizeOut, ",", "\n");
                    PopSizeOut.Close();
                    System.out.println("Population sizes written to file.");
                }
                if (EpidermisConst.get_r_lambda == true && EpidermisConst.RecordTime == Epidermis.GetTick()) {
                    FileIO RLambdaWriter = new FileIO(r_lambda_file, "w");
                    float r_lamb_print = 0;
                    int index=0;
                    for (int i = 0; i < Epidermis.Turnover.GetDeathRateBasal().length ; i++) {
                        if(Epidermis.Turnover.GetDeathRateBasal()[i]!=0){
                            RLambdaWriter.Write(Epidermis.Turnover.GetDeathRateBasal()[i] + "\n");
                            r_lamb_print+=Epidermis.Turnover.GetDeathRateBasal()[i];
                            index++;
                        }
                    }
                    RLambdaWriter.Close();
                    System.out.println("Mean weekly rLambda: " + new DecimalFormat("#.000").format(r_lamb_print/index));
                }
                if (EpidermisConst.get_r_lambda == true && EpidermisConst.RecordTime == Epidermis.GetTick()) {
                    System.out.println("Mean Cell Age: " + new DecimalFormat("#.000").format( Epidermis.TrackAge.GetMeanAge() ) + "\n");
                }
                if (EpidermisConst.positionInfo==true && EpidermisConst.RecordTime == Epidermis.GetTick()){
                    FileIO PositionOut = new FileIO(PositionFile, "w");
                    Epidermis.GetCellPositions(PositionOut);
                    PositionOut.Close();
                    System.out.println("Position Information Saved to File");
                }
                if (EpidermisConst.RecordStateChange==true && EpidermisConst.RecordTime == Epidermis.GetTick()){
                    FileIO StateOut = new FileIO(StateChangeFile, "w");
                    Epidermis.WriteStateChange(StateOut);
                    StateOut.Close();
                    System.out.println("Volatility Information Recorded");
                }

            }
        }
        System.out.println(Utils.MemoryUsageStr());
        MainGUI.Dispose();
    }
}
