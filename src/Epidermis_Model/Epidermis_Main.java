package Epidermis_Model;
import AgentFramework.*;
import AgentFramework.Gui.*;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by schencro on 3/24/17.
 */

//Holds Constants for rest of model
class EpidermisConst{
    static int xSize=150; // keratinocyte modal cell size = 15µm (Proc. Natl. Acad. Sci. USA Vol.82,pp.5390-5394,August1985; YANN BARRANDON and HOWARD GREEN) == volume == 1766.25µm^3
    // (Sampled area = 1mm-2mm^2); Sampled volume = 4.4*10^8µm^3; Total cells needed for 2mm^2 area with depth of 140µm= 249115cells (xSize = 12456, ySize = 20);
    // For 1mm^2 area with depth of 140µm = 62279cells (xSize = 3114, ySize = 20);
    // Takes forever to reach even a year. Cutting the smallest biopsy into a quarter (1/4) = 15570cells (xSize = 1038, ySize = 20)
    static final int ySize=20;

    static final int KERATINOCYTE = 0; //setting types into a binary 0 or 1
    static final int DIVIDE = 2; // Attribute if cell is dividing
    static final int STATIONARY = 3; // Attribute if cell is stationary
    static final int MOVING = 4; //Attribute if cell is moving

    static int years=50; // time in years.
    static int RecordTime=years*365;
    static int ModelTime=years*365 + 10; // Time in days + 10 days after time for recording! e.g. 65 years = 23725

    static final int VisUpdate = 7; // Timestep interval to update Division and Death, etc.

    static final boolean GuiOn = false; // use for visualization
    static final boolean JarFile = true; // Set to true if running from command line as jar file
    static final boolean RecordAllPopSizes = false; // use to record all clone populations
    static final boolean TrackAll = false; // Use this if you want to record mutations outside the genes of interest.
    static final boolean RecordParents = true; // use when you want parents information
    static final boolean RecordLineages = true; // use when you want
    static final boolean RecordPopSizes = true; // Use to record clone population sizes
    static final boolean get_r_lambda = true; // use when you want the r_lambda value
    static final boolean writeValues = true; // Use when you want to write the output
}

public class Epidermis_Main {

    static GuiLabel LabelGuiSet(String text, int compX, int compY) {
        GuiLabel ret= new GuiLabel(text, compX, compY);
        ret.setOpaque(true);
        ret.setForeground(Color.white);
        ret.setBackground(Color.black);
        return ret;
    }

    public static void main (String[] args){
        /*
        Initialization
         */
        GuiVis ActivityVis = null;
        GuiVis EGFVis = null;
        GuiVis DivVis = null;
        GuiVis DivLayerVis = null;
        GuiVis DeathVis = null;
        GuiVis DeathLayerVis = null;
        GuiVis ClonalVis = null;
        GuiLabel YearLab = null;
        GuiLabel rLambda_Label = null;
        GuiLabel OldestCell = null;
        GuiLabel HealLab = null;
        GuiLabel HeightLab = null;
        EpidermisCellVis CellDraw = null;
        ArrayList<Float> r_lambda_WriteValue = new ArrayList();
        int r_lambda_index = 0;
        ArrayList<Float> meanCellAge = new ArrayList();
        int meanCellAgeIndex = 0;
        String ParentFile = System.getProperty("user.dir") + "/TestOutput/ParentFile.csv";
        String PopSizes = System.getProperty("user.dir") + "/TestOutput/PopSizes.csv";
        String MutationFile = System.getProperty("user.dir") + "/TestOutput/MutationFile.csv";
        String r_lambda_file = System.getProperty("user.dir") + "/TestOutput/R_Lambda_Values.csv";
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
            Gui MainGUI = new Gui("Homeostatic Epidermis Model", true);
            MainGUI.panel.setOpaque(true);
            MainGUI.panel.setBackground(Color.black);
            ClonalVis = new GuiVis(EpidermisConst.xSize*5, EpidermisConst.ySize*5, 1, 2, 1);
            DivVis = new GuiVis(EpidermisConst.xSize, EpidermisConst.ySize, 3, 1, 1);
            DivLayerVis = new GuiVis(EpidermisConst.xSize, EpidermisConst.ySize, 3, 1, 1);
            DeathVis = new GuiVis(EpidermisConst.xSize, EpidermisConst.ySize, 3, 1, 1);
            DeathLayerVis = new GuiVis(EpidermisConst.xSize, EpidermisConst.ySize, 3, 1, 1);
            ActivityVis = new GuiVis(EpidermisConst.xSize * 5, EpidermisConst.ySize * 5, 1, 2, 1); // Main Epidermis visualization window
            EGFVis = new GuiVis(EpidermisConst.xSize, EpidermisConst.ySize, 5, 2, 1);
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

            tickIt.TickPause(0); // Adjusting a frame rate

            // Main Running of the steps within the model
            Epidermis.RunStep();

            /*
            All Injuries Occuring Here!
             */
//            int healTick=0;
//
//            if(Healed && Epidermis.GetTick()%365==0){
//                Epidermis.inflict_wound();
//                woundTick=Epidermis.GetTick();
//                Healed = false;
//            }
//
//            if(!Healed && Epidermis.GetTick()%365!=0) {
//                Healed = Epidermis.checkWoundHeal((int) avgHeight);
//                healTick = Epidermis.GetTick();
//                if (Healed && HealLab != null) {
//                    if (HealLab != null) {
//                        HealLab.setText("Heal Time (Days): " + new DecimalFormat("#.0").format((healTick - woundTick)));
//                    }
//                }
//            }


            /*
            rLambda Value calculations, output, and recording
             */
            if (EpidermisConst.get_r_lambda) {
                if (Epidermis.GetTick() % 7f == 0) {
                    r_lambda_WriteValue.add(r_lambda_index, Epidermis.r_lambda_weekly/EpidermisConst.xSize/7f);
                    r_lambda_index += 1;
                    meanCellAge.add(meanCellAgeIndex, Epidermis.GetOldestCell(Epidermis));
                    meanCellAgeIndex += 1;
                    if(rLambda_Label!=null){rLambda_Label.setText("Mean rLambda (per week): " + new DecimalFormat("#.000").format(Epidermis.r_lambda_weekly/EpidermisConst.xSize/7f));}
                    EpidermisCell.loss_count_basal=0;
                    Epidermis.r_lambda_weekly = 0;
                } else {
                    Epidermis.r_lambda_weekly += ((float) EpidermisCell.loss_count_basal);
                }
            }

            /*
            Output Time Options
             */
            if(ActivityVis==null){
                if(Epidermis.GetTick()%365==0){
                    System.out.println(new DecimalFormat("#.0").format((Epidermis.GetTick() / 365f)));
                }
            }

            /*
            All Visualization Components are here
             */
            if(ActivityVis!=null){YearLab.setText("Age (yrs.): " + new DecimalFormat("#.00").format((Epidermis.GetTick() / 365f)));}
            if(DivVis!=null&Epidermis.GetTick()%EpidermisConst.VisUpdate==0){Epidermis.ActivityHeatMap(DivVis, Epidermis, CellDraw, Epidermis.MeanProlif, "gbr");}
            if(DivLayerVis!=null&Epidermis.GetTick()%EpidermisConst.VisUpdate==0){Epidermis.LayerVis(DivLayerVis, Epidermis, CellDraw, Epidermis.MeanProlif, "gbr");}
            if(DeathVis!=null&Epidermis.GetTick()%EpidermisConst.VisUpdate==0){Epidermis.ActivityHeatMap(DeathVis, Epidermis, CellDraw, Epidermis.MeanDeath, "rbg");}
            if(DeathLayerVis!=null&Epidermis.GetTick()%EpidermisConst.VisUpdate==0){Epidermis.LayerVis(DeathLayerVis, Epidermis, CellDraw, Epidermis.MeanDeath, "rbg");}
            if(ClonalVis!=null){Epidermis.DrawCellPops(ClonalVis, Epidermis, CellDraw);}
            if(OldestCell!=null){OldestCell.setText("Mean cell age (days): " + new DecimalFormat("#.00").format(Epidermis.GetOldestCell(Epidermis)));}
            if(ActivityVis!=null){Epidermis.DrawCellActivity(ActivityVis, Epidermis, CellDraw);}
            if(EGFVis!=null){Epidermis.DrawChemicals(EGFVis, true, false);}
            if(Epidermis.GetTick()==26){
                avgHeight=(Epidermis.popSum*1.0/Epidermis.GetTick())/Epidermis.xDim;
                if(HeightLab!=null){HeightLab.setText("Height: " + new DecimalFormat("#.00").format(avgHeight));}
            }

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
                    for (int i = 0; i < r_lambda_WriteValue.size(); i++) {
                        r_lamb_print += r_lambda_WriteValue.get(i);
                        String out = r_lambda_WriteValue.get(i).toString();
                        RLambdaWriter.Write(out + "\n");
                    }
                    RLambdaWriter.Close();
                    System.out.println("Mean weekly rLambda: " + new DecimalFormat("#.000").format(r_lamb_print / r_lambda_index) + "\n");
                }
                if (EpidermisConst.get_r_lambda == true && EpidermisConst.RecordTime == Epidermis.GetTick()) {
                    float MeanWeekPrint = 0;
                    for (int i = 0; i < meanCellAge.size(); i++) {
                        MeanWeekPrint += meanCellAge.get(i);
                    }
                    System.out.println("Mean weekly rLambda: " + new DecimalFormat("#.000").format(MeanWeekPrint / meanCellAgeIndex) + "\n");
                }
            }
        }
        Utils.PrintMemoryUsage();
    }
}
