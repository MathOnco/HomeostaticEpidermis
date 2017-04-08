package Epidermis_Model;
import AgentFramework.*;
import AgentFramework.Gui.*;

import java.awt.*;
import java.text.DecimalFormat;

/**
 * Created by schencro on 3/24/17.
 */

//Holds Constants for rest of model
class EpidermisConst{
    static final int xSize=200; // keratinocyte modal cell size = 15µm (Proc. Natl. Acad. Sci. USA Vol.82,pp.5390-5394,August1985; YANN BARRANDON and HOWARD GREEN) == volume == 1766.25µm^3
    // (Sampled area = 1mm-2mm^2); Sampled volume = 4.4*10^8µm^3; Total cells needed for 2mm^2 area with depth of 140µm= 249115cells (xSize = 12456, ySize = 20);
    // For 1mm^2 area with depth of 140µm = 62279cells (xSize = 3114, ySize = 20);
    // Takes forever to reach even a year. Cutting the smallest biopsy into a quarter (1/4) = 15570cells (xSize = 1038, ySize = 20)
    static final int ySize=20;

    static final int KERATINOCYTE = 0; //setting types into a binary 0 or 1
    static final int DIVIDE = 2; // Attribute if cell is dividing
    static final int STATIONARY = 3; // Attribute if cell is stationary
    static final int MOVING = 4; //Attribute if cell is moving

    static final int years=10; // time in years.
    static final int RecordTime=years*365;
    static final int ModelTime=years*365 + 10; // Time in days + 10 days after time for recording! e.g. 65 years = 23725

    static final int VisUpdate = 7; // Timestep interval to update Division and Death, etc.

    static final boolean GuiOn = false; // use for visualization
    static final boolean JarFile = false; // Set to true if running from command line as jar file
    static final boolean RecordParents = true; // use when you want parents information
    static final boolean RecordLineages = true; // use when you want
    static final boolean RecordPopSizes = true; // Use to record clone population sizes
    static final boolean RecordGenomes = true; // Use to record clone genomes
    static final boolean get_r_lambda = true; // use when you want the r_lambda value
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
        Sets up Data Files if on cluster or if ran locally
         */
        if(EpidermisConst.JarFile){
            String ParentFile = args[0];
            String LineageFile = args[1];
            String PopSizes = args[2];
            String MutationFile = args[3];
        } else {
            String ParentFile = System.getProperty("user.dir") + "/TestOutputs/ParentFile.csv";
            String LineageFile = System.getProperty("user.dir") + "/TestOutputs/LineageFile.csv";
            String PopSizes = System.getProperty("user.dir") + "/TestOutputs/PopSizes.csv";
            String MutationFile = System.getProperty("user.dir") + "/TestOutputs/MutationFile.csv";
        }

        /*
        Initialization
         */
        final EpidermisGrid Epidermis = new EpidermisGrid(EpidermisConst.xSize, EpidermisConst.ySize); // Initializes and sets up the program for running
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
        EpidermisCellVis CellDraw = null;


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
            YearLab = LabelGuiSet("Age: ", 1, 1);
            MainGUI.AddCol(YearLab, 0);
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

        TickRateTimer tickIt = new TickRateTimer();
        while(Epidermis.GetTick() < EpidermisConst.ModelTime){

            tickIt.TickPause(0); // Adjusting a frame rate

            // Main Running of the steps within the model
            Epidermis.RunStep();

            /*
            All Injuries Occuring Here!
             */
//            if(Epidermis.GetTick()%1000==0){
//                Epidermis.inflict_wound();
//            }

            /*
            rLambda Value calculations, output, and recording
             */
            if (EpidermisConst.get_r_lambda) {
                if (Epidermis.GetTick() % 7f == 0) {
                    float temp_r_lambda = Epidermis.r_lambda_weekly / 7.0f;
                    String r_lambda_out = String.valueOf(temp_r_lambda);
                    //writer2.Write(r_lambda_out + '\n'); // writes out r_lambda value
                    //System.out.println(Epidermis.r_lambda_weekly / 7.0f);
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

            /*
            All Model Data Recording Is Below This line
             */
            if(EpidermisConst.GuiOn == false && EpidermisConst.RecordGenomes==true){

            }

        }
    }
}
