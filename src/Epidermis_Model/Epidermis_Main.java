package Epidermis_Model;
import AgentFramework.*;
import AgentFramework.Gui.*;
import sun.applet.Main;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static AgentFramework.Utils.*;

/**
 * Created by schencro on 3/24/17.
 */

//Holds Constants for rest of model
class EpidermisConst{
    static final int xSize=250; // keratinocyte modal cell size = 15µm (Proc. Natl. Acad. Sci. USA Vol.82,pp.5390-5394,August1985; YANN BARRANDON and HOWARD GREEN) == volume == 1766.25µm^3
    // (Sampled area = 1mm-2mm^2); Sampled volume = 4.4*10^8µm^3; Total cells needed for 2mm^2 area with depth of 140µm= 249115cells (xSize = 12456, ySize = 20);
    // For 1mm^2 area with depth of 140µm = 62279cells (xSize = 3114, ySize = 20);
    // Takes forever to reach even a year. Cutting the smallest biopsy into a quarter (1/4) = 15570cells (xSize = 1038, ySize = 20)
    static final int ySize=20;

    static final int KERATINOCYTE = 0; //setting types into a binary 0 or 1
    static final int MELANOCYTE = 1; //same as above (1)
    static final int DIVIDE = 0; // Attribute if cell is dividing
    static final int STATIONARY = 1; // Attribute if cell is stationary
    static final int MOVING = 2; //Attribute if cell is moving

    static final int years=65; // time in years.
    static final int RecordTime=years*365;
    static final int ModelTime=years*365 + 10; // Time in days + 10 days after time for recording! e.g. 65 years = 23725

    static final boolean FileOn = false; // use when writing information for mueller plots
    static final boolean GuiOn = true; // use for visualization
    static final boolean genome_info = false; // use when you want genome information
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
        final EpidermisGrid Epidermis = new EpidermisGrid(EpidermisConst.xSize, EpidermisConst.ySize); // Initializes and sets up the program for running
        GuiVis ActivityVis = null;
        GuiVis EGFVis = null;
        GuiVis BFGFVis = null;
        GuiVis DivVis = null;
        GuiVis DivLayerVis = null;
        GuiVis DeathVis = null;
        GuiVis DeathLayerVis = null;
        GuiVis ClonalVis = null;
        GuiLabel YearLab = null;
        GuiLabel rLambda_Label = null;
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
            BFGFVis = new GuiVis(EpidermisConst.xSize, EpidermisConst.ySize, 5, 2, 1);
            YearLab = LabelGuiSet("Age: ", 1, 1);
            MainGUI.AddCol(YearLab, 0);
            rLambda_Label = LabelGuiSet("rLambda: ", 1, 1);
            MainGUI.AddCol(rLambda_Label, 1);
            MainGUI.AddCol(LabelGuiSet("Population", 2, 1), 0);
            MainGUI.AddCol(ClonalVis, 0);
            MainGUI.AddCol(LabelGuiSet("Division (per week)", 1, 1), 0);
            MainGUI.AddCol(DivVis, 0);
            MainGUI.AddCol(LabelGuiSet("Division Layers (per week)", 1, 1), 1);
            MainGUI.AddCol(LabelGuiSet("Death (per week)", 1, 1), 0);
            MainGUI.AddCol(DeathVis,0);
            MainGUI.AddCol(DivLayerVis, 1);
            MainGUI.AddCol(LabelGuiSet("Death Layer (per week)", 1, 1), 1);
            MainGUI.AddCol(LabelGuiSet("Epidermis", 2, 1), 0);
            MainGUI.AddCol(ActivityVis, 0); // Main Epidermis visualization window
            MainGUI.AddCol(LabelGuiSet("EGF", 2, 1), 0);
            MainGUI.AddCol(EGFVis, 0);
            MainGUI.AddCol(LabelGuiSet("bFGF", 2, 1), 0);
            MainGUI.AddCol(BFGFVis, 0);

            MainGUI.RunGui();
        }

        TickRateTimer tickIt = new TickRateTimer();
        while(Epidermis.GetTick() < EpidermisConst.ModelTime){

            tickIt.TickPause(0); // Adjusting a frame rate

            // Main Running of the steps within the model
            Epidermis.RunStep();
            if(Epidermis.GetTick()==100){
                Epidermis.inflict_wound();
            }

            if (EpidermisConst.get_r_lambda) {
                if (Epidermis.GetTick() % 7f == 0) {
                    float temp_r_lambda = Epidermis.r_lambda_weekly / 7.0f;
                    String r_lambda_out = String.valueOf(temp_r_lambda);
                    //writer2.Write(r_lambda_out + '\n'); // writes out r_lambda value
                    //System.out.println(Epidermis.r_lambda_weekly / 7.0f);
                    if(rLambda_Label!=null){rLambda_Label.setText("Mean rLambda: " + new DecimalFormat("#.000").format(Epidermis.r_lambda_weekly/EpidermisConst.xSize/7f));}
                    EpidermisCell.loss_count_basal=0;
                    Epidermis.r_lambda_weekly = 0;
                } else {
                    Epidermis.r_lambda_weekly += ((float) EpidermisCell.loss_count_basal);
                }
            }

            if(ActivityVis==null){
                if(Epidermis.GetTick()%365==0){
                    System.out.println(new DecimalFormat("#.0").format((Epidermis.GetTick() / 365f)));
                }
            }

            // Visualization Components
            if(ActivityVis!=null){YearLab.setText("Age (yrs.): " + new DecimalFormat("#.00").format((Epidermis.GetTick() / 365f)));}
            if(DivVis!=null&Epidermis.GetTick()%7==0){Epidermis.DivisionHeatMap(DivVis, Epidermis, CellDraw);}
            if(DivLayerVis!=null&Epidermis.GetTick()%7==0){Epidermis.DivisionLayers(DivLayerVis, Epidermis, CellDraw);}
            if(ClonalVis!=null){Epidermis.DrawCellPops(ClonalVis, Epidermis, CellDraw);}
            if(ActivityVis!=null){Epidermis.DrawCellActivity(ActivityVis, Epidermis, CellDraw);}
            if(EGFVis!=null){Epidermis.DrawChemicals(EGFVis, true, false);}
            if(BFGFVis!=null){Epidermis.DrawChemicals(BFGFVis, false, true);}
        }
    }
}
