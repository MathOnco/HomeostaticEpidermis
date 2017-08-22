package Epidermis_Model;

import Epidermis_Model.ParamSweeper.ParamSweeper;
import Framework.Gui.GuiGridVis;
import Framework.Gui.GuiLabel;
import Framework.Gui.GuiWindow;
import Framework.Tools.FileIO;
import Framework.Tools.*;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import static Epidermis_Model.EpidermisCellGenome.RN;


/**
 * Created by schencro on 3/24/17.
 */

//Holds Constants for rest of model
class EpidermisConst{
    static int xSize=20; // keratinocyte modal cell size = 15µm (Proc. Natl. Acad. Sci. USA Vol.82,pp.5390-5394,August1985; YANN BARRANDON and HOWARD GREEN) == volume == 1766.25µm^3
    // (Sampled area = 1mm-2mm^2); Sampled volume = 4.4*10^8µm^3; Total cells needed for 2mm^2 area with depth of 140µm= 249115cells (xSize = 12456, ySize = 20);
    // For 1mm^2 area with depth of 140µm = 62279cells (xSize = 3114, ySize = 20);
    // Takes forever to reach even a year. Cutting the smallest biopsy into a quarter (1/4) = 15570cells (xSize = 1038, ySize = 20)
    static final int ySize=20;
    static int zSize=xSize;

    static final int KERATINOCYTE = 0; //setting types into a binary 0 or 1
    static final int DIVIDE = 2; // Attribute if cell is dividing
    static final int STATIONARY = 3; // Attribute if cell is stationary
    static final int MOVING = 4; //Attribute if cell is moving

    static int years=2; // time in years.
    static int RecordTime=years*365;
    static int ModelTime=years*365 + 10; // Time in days + 10 days after time for recording! e.v. 65 years = 23725

    static final int VisUpdate = 7; // Timestep interval to update Division and Death, etc.

    static final boolean GuiOn = false; // use for visualization, set to false for jar file / multiple runs
    static final boolean JarFile = false; // Set to true if running from command line as jar file!!!!!!!!
    static final boolean RecordParents = false; // use when you want parents information
    static final boolean RecordLineages = false; // use when you want
    static final boolean RecordPopSizes = false; // Use to record clone population sizes
    static final boolean get_r_lambda = false; // use when you want the r_lambda value for the visualization
    static final boolean writeValues = false; // use this when you want the data to be saved!
    static final boolean sliceOnly = false; // use this when you want slice of the 3D model data to be output!!!!!!!!!!!!!!
    static final boolean SliceAndFull = false; // use this when you want a slice out of the 3D model and the full data of the modeled cells!!!!
    static final boolean GetImageData = false; // Use for 3D data for visualization
}

public class Epidermis_Main {

//    static GuiLabel LabelGuiSet(String text, int compX, int compY) {
//        GuiLabel ret= new GuiLabel(text, compX, compY);
//        ret.setOpaque(true);
//        ret.setForeground(Color.white);
//        ret.setBackground(Color.black);
//        return ret;
//    }

    public static void main (String[] args){
///*
//Initialization
// */
//        GuiVis ActivityVis = null;
//        GuiVis EGFVis = null;
//        GuiVis DivVis = null;
//        GuiVis DivLayerVis = null;
//        GuiVis DeathVis = null;
//        GuiVis DeathLayerVis = null;
//        GuiVis ClonalVis = null;
//        GuiLabel YearLab = null;
//        GuiLabel rLambda_Label = null;
//        GuiLabel OldestCell = null;
//        GuiLabel HealLab = null;
//        GuiLabel HeightLab = null;
//        EpidermisCellVis CellDraw = null;
//        String ParentFile = System.getProperty("user.dir") + "/TestOutput/ParentFile.csv";
//        String PopSizes = System.getProperty("user.dir") + "/TestOutput/PopSizes.csv";
//        String MutationFile = System.getProperty("user.dir") + "/TestOutput/MutationFile.csv";
//        String r_lambda_file = System.getProperty("user.dir") + "/TestOutput/R_Lambda_Values.csv";
//        String PositionFile = System.getProperty("user.dir") + "/TestOutput/CellPositions.csv";
///*
//Sets up Data Files if on cluster or if ran locally
// */
//        if(EpidermisConst.JarFile){
//            ParentFile = args[0];
//            PopSizes = args[1];
//            MutationFile = args[2];
//            r_lambda_file = args[3];
//            EpidermisConst.xSize = Integer.parseInt(args[4]);
//            int Time = Integer.parseInt(args[5]);
//            EpidermisConst.years = Time;
//            EpidermisConst.ModelTime = Time * 365 + 10;
//            EpidermisConst.RecordTime = Time * 365;
//            PositionFile = args[6];
//        }
//        if(EpidermisConst.GuiOn == false){
//            System.out.println("xSize: " + EpidermisConst.xSize);
//            System.out.println("Years: " + EpidermisConst.years);
//        }

        FileIO FileParams = new FileIO("GridParams3D_Round1.txt", "w");
        ParamSweeper PS = new ParamSweeper(FileParams, (double[] runThatShit)->{
            EpidermisGrid Epidermis = new EpidermisGrid(EpidermisConst.xSize, EpidermisConst.ySize, EpidermisConst.zSize, runThatShit); // Initializes and sets up the program for running
            String OutRL = "";
            String outMean = "";
            String outAge2 = "";
            String outOldest = "";
            int r_lambda_index = 0;
            int woundTick = 0;
            boolean Healed = true;
            ArrayList<Double> MeanHeight = new ArrayList<>();
            double avgHeight=0;
            int tickSum=0;

            while(Epidermis.GetTick() < EpidermisConst.ModelTime){
                //TODO FIGURE OUT THE INNER WORKINGS OF THIS TO BE ABLE TO RUN A TON OF PARAMETER SWEEPS

                Epidermis.RunStep();
                int healTick=0;

//                if(!Healed && Epidermis.GetTick() > 913 && woundHealWriteValue.size() < 11){
//                    Healed = Epidermis.checkWoundHeal((int)avgHeight);
//                    healTick=Epidermis.GetTick();
//                    if(Healed){
//                        woundHealWriteValue.add(healTick-woundTick);
//                    }
//                }
//
//                if(Healed && Epidermis.GetTick() > 913 && Epidermis.GetTick()-healTick>10 && woundHealWriteValue.size() < 11){
//                    Epidermis.inflict_wound();
//                    woundTick=Epidermis.GetTick();
//                    Healed = false;
//                }
//                if(Epidermis.GetTick()==912){
//                    avgHeight=Epidermis.GetMeanCellHeight();
//                }

                MeanHeight.add(Epidermis.GetMeanCellHeight());
//                Epidermis.TrackAge.SetMeanAge();

                if (Epidermis.GetTick() % 7f == 0) {
                    Epidermis.Turnover.GetBasalRate("Death", 7); //Required to Record the rate of interest every 7 days
                }

                /*
                Breaks While loop if the epidermis dissolves
                 */
                if(Epidermis.GetPop()==0){
                    OutRL = "NaN";
                    outMean = "NaN";
                    break;
                }

                if(EpidermisConst.RecordTime==Epidermis.GetTick()){
                    /*
                    Records the loss replacement rate for whole time
                     */
                    double r_lamb_print = 0;
                    int index = 0;
                    for (int i = 0; i < Epidermis.Turnover.GetOutputArray().length; i++) {
                        if(Epidermis.Turnover.GetOutputArray()[i]!=0){
                            r_lamb_print += Epidermis.Turnover.GetOutputArray()[i];
                            index++;
                        }
                    }
                    OutRL = "" + r_lamb_print/index;
                    if(r_lamb_print/r_lambda_index==0.0){
                        EpidermisConst.ModelTime = Epidermis.GetTick()+1;
                        OutRL = "NaN";
                    }

                    for (int i = 0; i < MeanHeight.size(); i++) {
                        avgHeight += MeanHeight.get(i);
                    }
                    avgHeight = avgHeight/MeanHeight.size();

//                    outMean = Epidermis.TrackAge.GetMeanAge() + "";

//                    outAge2 = Epidermis.GetAges() + "";

                    outOldest = Epidermis.GetOldestCell() + "";
                }

            }
            System.out.println("Run Complete...");
//            System.out.println(OutRL + "\t" + outMean + "\t" + avgHeight + "\t"+ "NoHealData");
            //String why=Utils.ArrToString(runThatShit, "\t");
            return Utils.ArrToString(runThatShit, "\t") + OutRL + "\t" + outOldest + "\t" + avgHeight + "\n";
        });

        //*range+min
        PS.AddParam((Random RN)->{ // PSF
            //return RN.nextDouble()*1.0+0.0; //Iteration 1
//            return RN.nextDouble()*0.2+0.0; //Iteration 1,2,4
//            return RN.nextDouble()*0.5+0.0; //Iteration 5
//            return RN.nextDouble()*0.9+0.1; //Iteration 6
//            return RN.nextDouble()*0.3+0.000001; //Iteration 7, 8
//            return RN.nextDouble()*0.04+0.01; //Iteration 9
//            return RN.nextDouble()*0.005+0.025; //Iteration 10, 11, 12
//            return RN.nextDouble()*0.99=+0.01; // Iteration 14, 15
//            return RN.nextDouble()*0.3+0.0001; // Iteration 16
//            return RN.nextDouble()*0.109530591233847+0.00498618090048364;
            return RN.nextDouble()*0.1+0.0000000;
        });
        PS.AddParam((Random RN)->{ // KerEGFConsumption
//            return RN.nextDouble()*-1.0-0.0; //Iteration 1,2,4,5,7, 8, 9, 10, 11, 12
//            return RN.nextDouble()*-0.99-0.01;
//            return RN.nextDouble()*-0.99-0.01; // Iteration 14, 15
//            return RN.nextDouble()*-0.3-0.0001; // Iteration 16
//            return RN.nextDouble()*-0.197507253235264-0.745503338640579;
            return RN.nextDouble()*-0.1-0.000001;
        });
        PS.AddParam((Random RN)->{ // ApopEGF
            //return RN.nextDouble()*1.0+0.0; //Iteration 1,2,4, 5,7
//            return RN.nextDouble()*0.5+0.1; //Iteration 8, 9, 10, 11, 12
//            return RN.nextDouble()*0.99+0.01; // Iteration 14
//            return RN.nextDouble()*0.4+0.001; // Iteration 15
//            return RN.nextDouble()*0.830918923846992+0.102204526933265;
            return RN.nextDouble()*0.5+0.0;
        });
        PS.AddParam((Random RN)->{ // DeathProb
//            return RN.nextDouble()*1.0+0.0; //Iteration 1
//            return RN.nextDouble()*0.2+0.0; //Iteration 1,2,4, 5,7, 8, 9, 10, 11, 12
//            return RN.nextDouble()*0.99+0.01; // Iteration 14
//            return RN.nextDouble()*0.144684003908747+0.044313748498699;
            return RN.nextDouble()*0.1+0.000001;
        });
        PS.AddParam((Random RN)->{ // MoveProb
//            return RN.nextDouble()*1.0+0.0; //Iteration 1,2,4, 5,7, 8, 9, 10, 11
//            return RN.nextDouble()*0.1+0.9; //Iteration 12
//            return RN.nextDouble()*0.99+0.01; // Iteration 14
//            return RN.nextDouble()*0.037713587735326+0.961687208092696;
            return RN.nextDouble()*1.0+0.0;
        });
        PS.AddParam((Random RN)->{ // DIVLOCPROB
//            return RN.nextDouble()*1.0+0.0; //Iteration 1,2,4, 5,7
//            return RN.nextDouble()*0.599+0.001; //Iteration 8, 9, 10
//            return RN.nextDouble()*0.15+0.25; //Iteration 11, 12
//            return RN.nextDouble()*0.99+0.01; // Iteration 14
//            return RN.nextDouble()*0.6+0.4; // Iteration 16
//            return RN.nextDouble()*0.901925240758804+0.0760929638878588;
            return RN.nextDouble()*0.9+0.3;
        });
        PS.AddParam((RandomRN)->{ // EGF_DIFFUSION_RATE
//            return RN.nextDouble()*1.0+0.0; //Iteration 1,2,4
//            return RN.nextDouble()*0.3+0.0; //Iteration 3
//            return RN.nextDouble()*0.05+0.2; //Iteration 4, 5
//            return RN.nextDouble()*0.2399+0.0001; //Iteration 6,7, 8, 9, 10, 11, 12, 13,14
//            return RN.nextDouble()*0.19+0.01; //Iteration 15
//            return RN.nextDouble()*0.0529218211260551+0.196223766505206;
            return RN.nextDouble()*0.1+0.75;
        });
        PS.AddParam((RandomRN)->{ // Decay Rate
//            return RN.nextDouble()*0.5+0.0; //Iteration 1,2,4
//            return RN.nextDouble()*0.1+0.2; //Iteration 5,7, 8, 9, 10, 11, 12
//            return RN.nextDouble()*0.99+0.01; // Iteration 14
//            return RN.nextDouble()*0.20+0.001; // Iteration 15
//            return RN.nextDouble()*0.267497092075744+0.00127358998181254;
            return RN.nextDouble()*0.0015+0.0005;
        });

        PS.Sweep(1, 1);

        FileParams.Close();
//        while(Epidermis.GetTick() < EpidermisConst.ModelTime){
//
//            tickIt.TickPause(0); // Adjusting a frame rate
//
//            // Main Running of the steps within the model
//            Epidermis.RunStep();
//
//            /*
//            All Injuries Occuring Here!
//             */
////            if(Epidermis.GetTick()%1000==0){
////                Epidermis.inflict_wound();
////            }
//
//            /*
//            rLambda and meanCell Age Value calculations, output, and recording
//             */
//            if (EpidermisConst.get_r_lambda) {
//                if (Epidermis.GetTick() % 7f == 0) {
//                    r_lambda_WriteValue.add(r_lambda_index, Epidermis.r_lambda_weekly/EpidermisConst.xSize/7f);
//                    r_lambda_index += 1;
//                    meanCellAgeWriteValue.add(meanCellAgeIndex, Epidermis.GetOldestCell(Epidermis));
//                    meanCellAgeIndex += 1;
//                    if(rLambda_Label!=null){rLambda_Label.setText("Mean rLambda (per week): " + new DecimalFormat("#.000").format(Epidermis.r_lambda_weekly/EpidermisConst.xSize/7f));}
//                    EpidermisCell.loss_count_basal=0;
//                    Epidermis.r_lambda_weekly = 0;
//                } else {
//                    Epidermis.r_lambda_weekly += ((float) EpidermisCell.loss_count_basal);
//                }
//            }
//
//            /*
//            Output Time Options
//             */
////            if(ActivityVis==null){
////                if(Epidermis.GetTick()%365==0){
////                    System.out.println(new DecimalFormat("#.0").format((Epidermis.GetTick() / 365f)));
////                }
////            }
//
//            /*
//            All Visualization Components are here
//             */
//            if(ActivityVis!=null){YearLab.setText("Age (yrs.): " + new DecimalFormat("#.00").format((Epidermis.GetTick() / 365f)));}
//            if(DivVis!=null&Epidermis.GetTick()%EpidermisConst.VisUpdate==0){Epidermis.ActivityHeatMap(DivVis, Epidermis, CellDraw, Epidermis.MeanProlif, "gbr");}
//            if(DivLayerVis!=null&Epidermis.GetTick()%EpidermisConst.VisUpdate==0){Epidermis.LayerVis(DivLayerVis, Epidermis, CellDraw, Epidermis.MeanProlif, "gbr");}
//            if(DeathVis!=null&Epidermis.GetTick()%EpidermisConst.VisUpdate==0){Epidermis.ActivityHeatMap(DeathVis, Epidermis, CellDraw, Epidermis.MeanDeath, "rbg");}
//            if(DeathLayerVis!=null&Epidermis.GetTick()%EpidermisConst.VisUpdate==0){Epidermis.LayerVis(DeathLayerVis, Epidermis, CellDraw, Epidermis.MeanDeath, "rbg");}
//            if(ClonalVis!=null){Epidermis.DrawCellPops(ClonalVis, Epidermis, CellDraw);}
//            if(OldestCell!=null){OldestCell.setText("Mean cell age (days): " + new DecimalFormat("#.00").format(Epidermis.GetOldestCell(Epidermis)));}
//            if(ActivityVis!=null){Epidermis.DrawCellActivity(ActivityVis, Epidermis, CellDraw);}
//            if(EGFVis!=null){Epidermis.DrawChemicals(EGFVis, true, false);}
//
//            Epidermis.GenomeStore.RecordClonePops();
//
//            /*
//            All Model Data Recording Is Below This line
//             */
////            if(EpidermisConst.RecordParents==true && EpidermisConst.RecordTime==Epidermis.GetTick()){
////                FileIO ParentOut = new FileIO(ParentFile, "w");
////                Epidermis.GenomeStore.WriteParentIDs(ParentOut, "\n");
////                ParentOut.Close();
////                System.out.println("Parents written to file.");
////            }
////            if(EpidermisConst.RecordLineages==true && EpidermisConst.RecordTime==Epidermis.GetTick()){
////                FileIO MutsOut = new FileIO(MutationFile, "w");
////                Epidermis.GenomeStore.WriteAllLineageInfoLiving(MutsOut, ",", "\n");
////                MutsOut.Close();
////                System.out.println("Lineage genomes written to file.");
////            }
////            if(EpidermisConst.RecordPopSizes==true && EpidermisConst.RecordTime==Epidermis.GetTick()){
////                FileIO PopSizeOut = new FileIO(PopSizes, "w");
////                //Epidermis.GenomeStore.RecordClonePops();
////                Epidermis.GenomeStore.WriteClonePops(PopSizeOut, ",", "\n");
////                PopSizeOut.Close();
////                System.out.println("Population sizes written to file.");
////            }
//            if(EpidermisConst.get_r_lambda == true && EpidermisConst.RecordTime==Epidermis.GetTick()){
////                FileIO RLambdaWriter = new FileIO(r_lambda_file, "w");
//                float r_lamb_print = 0;
//                for (int i = 0; i < r_lambda_WriteValue.size(); i++) {
//                    r_lamb_print += r_lambda_WriteValue.get(i);
////                    String out = r_lambda_WriteValue.get(i).toString();
////                    RLambdaWriter.Write(out);
//                }
////                RLambdaWriter.Close();
//                System.out.println("Mean weekly rLambda: " + new DecimalFormat("#.000").format(r_lamb_print/r_lambda_index) + "\n");
//            }
//            if(EpidermisConst.get_r_lambda == true && EpidermisConst.RecordTime==Epidermis.GetTick()) {
//                float MeanWeekPrint = 0;
//                for (int i = 0; i < r_lambda_WriteValue.size(); i++) {
//                    MeanWeekPrint += meanCellAgeWriteValue.get(i);
//                }
//                System.out.println("Mean weekly cell age: " + new DecimalFormat("#.000").format(MeanWeekPrint/meanCellAgeWriteValue.size()) + "\n");
//            }
//        }
//        Utils.PrintMemoryUsage();
    }
}

