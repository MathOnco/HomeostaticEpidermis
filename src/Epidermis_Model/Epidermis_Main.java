package Epidermis_Model;
import AgentFramework.*;
import AgentFramework.Gui.*;
import sun.applet.Main;

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
    static final int xSize=500; // keratinocyte modal cell size = 15µm (Proc. Natl. Acad. Sci. USA Vol.82,pp.5390-5394,August1985; YANN BARRANDON and HOWARD GREEN) == volume == 1766.25µm^3
    // (Sampled area = 1mm-2mm^2); Sampled volume = 4.4*10^8µm^3; Total cells needed for 2mm^2 area with depth of 140µm= 249115cells (xSize = 12456, ySize = 20);
    // For 1mm^2 area with depth of 140µm = 62279cells (xSize = 3114, ySize = 20);
    // Takes forever to reach even a year. Cutting the smallest biopsy into a quarter (1/4) = 15570cells (xSize = 1038, ySize = 20)
    static final int ySize=20;

    static final int KERATINOCYTE = 0; //setting types into a binary 0 or 1
    static final int MELANOCYTE = 1; //same as above (1)
    static final int DIVIDE = 0; // Attribute if cell is dividing
    static final int STATIONARY = 1; // Attribute if cell is stationary
    static final int MOVING = 2; //Attribute if cell is moving

    static final int years=23725; // time in years.
    static final int RecordTime=years*365;
    static final int ModelTime=years*365 + 10; // Time in days + 10 days after time for recording! e.g. 65 years = 23725

    static final boolean FileOn = false; // use when writing information for mueller plots
    static final boolean GuiOn = true; // use for visualization
    static final boolean genome_info = false; // use when you want genome information
    static final boolean get_r_lambda = false; // use when you want the r_lambda value
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
        GuiVis MainVis = null;
        GuiVis EGFVis = null;
        GuiVis BFGFVis = null;
        GuiVis DivVis = null;
        GuiLabel YearLab = null;
        EpidermisCellVis CellDraw = null;
        // Sets up GUI

        if(EpidermisConst.GuiOn) {
            CellDraw = new EpidermisCellVis();
            Gui MainGUI = new Gui("Homeostatic Epidermis Model", true);
            MainGUI.panel.setOpaque(true);
            MainGUI.panel.setBackground(Color.black);

            DivVis = new GuiVis(EpidermisConst.xSize, EpidermisConst.ySize, 5, 1, 1);
            MainVis = new GuiVis(EpidermisConst.xSize * 5, EpidermisConst.ySize * 5, 1, 1, 1); // Main Epidermis visualization window
            EGFVis = new GuiVis(EpidermisConst.xSize, EpidermisConst.ySize, 5, 1, 1);
            BFGFVis = new GuiVis(EpidermisConst.xSize, EpidermisConst.ySize, 5, 1, 1);
            YearLab = LabelGuiSet("Age: ", 1, 1);
            MainGUI.AddCol(YearLab, 0);
            MainGUI.AddCol(LabelGuiSet("Division", 1, 1), 0);
            MainGUI.AddCol(DivVis, 0);
            MainGUI.AddCol(LabelGuiSet("Epidermis", 1, 1), 0);
            MainGUI.AddCol(MainVis, 0); // Main Epidermis visualization window
            MainGUI.AddCol(LabelGuiSet("EGF", 1, 1), 0);
            MainGUI.AddCol(EGFVis, 0);
            MainGUI.AddCol(LabelGuiSet("bFGF", 1, 1), 0);
            MainGUI.AddCol(BFGFVis, 0);

            MainGUI.RunGui();
        }

        TickRateTimer tickIt = new TickRateTimer();
        while(Epidermis.GetTick() < EpidermisConst.ModelTime){

//            tickIt.TickPause(100); // Adjusting a frame rate

            // Main Running of the steps within the model
            Epidermis.RunStep();

            // Print Epidermis Time in Years

            if(Epidermis.GetTick()==100||Epidermis.GetTick()==150){
                Epidermis.inflict_wound();
            }

            if(MainVis==null){
                if(Epidermis.GetTick()%3650==0){
                    System.out.println(new DecimalFormat("#.0").format((Epidermis.GetTick() / 365f)));
                }
            }
            if(MainVis!=null){YearLab.setText("Tick: " + new DecimalFormat("#.00").format((Epidermis.GetTick() / 365f)));}
            if(MainVis!=null){Epidermis.DrawCells(MainVis, Epidermis, CellDraw);}
            if(EGFVis!=null){Epidermis.DrawChemicals(EGFVis, true, false);}
            if(BFGFVis!=null){Epidermis.DrawChemicals(BFGFVis, false, true);}



        }


//        for(int t=0; t < EpidermisConst.ModelTime; t++) {
//
//
//            EpidermisCellVis cell_vis = new EpidermisCellVis();
//            for(int x=0; x<=1399; x+=5){
//                for(int y=0; y<=29; y+=5){
//                    // Draws the cell
//                    for (int i = 0; i < cell_vis.division_vis.length; i += 2) {
//                        if(MainVis!=null){MainVis.SetColor(cell_vis.division_vis[i]+x, cell_vis.division_vis[i + 1]+y, 1f, 0f, 0f);}
//                    }
//                }
//            }
//
//            for(int x=0; x<=1399; x+=5){
//                for(int y=30; y<=59; y+=5){
//                    // Draws the cell
//                    for (int i = 0; i < cell_vis.stationary_vis.length; i += 2) {
//                        if(MainVis!=null){MainVis.SetColor(cell_vis.stationary_vis[i]+x, cell_vis.stationary_vis[i + 1]+y, 0f, 1f, 0f);}
//                    }
//                }
//            }
//            for(int x=0; x<=1399; x+=5){
//                for(int y=60; y<=99; y+=5){
//                    // Draws the cell
//                    for (int i = 0; i < cell_vis.movement_vis.length; i += 2) {
//                        if(MainVis!=null){MainVis.SetColor(cell_vis.movement_vis[i]+x, cell_vis.movement_vis[i + 1]+y, 0f, 0f, 1f);}
//                    }
//                }
//            }


    }
}
