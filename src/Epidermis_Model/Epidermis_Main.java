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

    static int years=100; // time in years.
    static int RecordTime=years*365;
    static int ModelTime=years*365 + 10; // Time in days + 10 days after time for recording! e.v. 65 years = 23725

    static final int VisUpdate = 7; // Timestep interval to update Division and Death, etc.

    static final boolean JarFile = true; // Set to true if running from command line as jar file!!!!!!!!
}

public class Epidermis_Main {

    public static void main (String[] args){

        String Rep = "0";
        double FixProb = 1.0;

        /*
        Sets up Data Files if on cluster or if ran locally
         */
        if(EpidermisConst.JarFile){
            Rep = args[0];
            FixProb = Double.parseDouble(args[1]);
        }

        final EpidermisGrid Epidermis = new EpidermisGrid(EpidermisConst.xSize, EpidermisConst.ySize, EpidermisConst.zSize, FixProb); // Initializes and sets up the program for running
        Runtime rt = Runtime.getRuntime();

        int woundTick = 0;
        boolean Healed = true;
        double avgHeight=0;
        int tickSum=0;

        double EndRun = 0.0;
        boolean Done = true;
        String FixedTime = null;

        TickRateTimer tickIt = new TickRateTimer();
        while(Done) {

            // Main Running of the steps within the model
            Epidermis.RunStep();

            /*
            All Injuries Occuring Here!
             */
//            int healTick=0;
//
//            if(Healed && Epidermis.GetTick()%50==0){
//                Epidermis.inflict_wound(EpidermisConst.xSize/4);
//                woundTick=Epidermis.GetTick();
//                Healed = false;
//            }
//
//            if(!Healed && Epidermis.GetTick()%50!=0) {
//                Healed = Epidermis.checkWoundHeal((int) avgHeight);
//                healTick = Epidermis.GetTick();
//                if (Healed && HealLab != null) {
//                    if (HealLab != null) {
//                        HealLab.setText("Heal Time (Days): " + new DecimalFormat("#.0").format((healTick - woundTick)));
//                    }
//                }
//            }
            /*
            Get the Diffusion Values for examining 2D versus 3D differences
             */
//            if(Epidermis.GetTick()<=365){Epidermis.GetEGFVal();}

            /*
            Output Time Options
             */
//            if(ActivityVis==null){
//                if(Epidermis.GetTick()%365==0){
//                    System.out.println(new DecimalFormat("#.0").format((Epidermis.GetTick() / 365f)));
//                }
//            }

//            System.out.println(Epidermis.Turnover.GetBasalRate("Death",Epidermis.GetTick()));
//            if(Epidermis.GetTick()==EpidermisConst.ModelTime-1){
//            System.out.println(Epidermis.GetDivisionProportion());
//            }

            /*
            All Visualization Components are here
             */
//            if(Epidermis.GetTick()%7==0){
//                if(rLambda_Label!=null){rLambda_Label.SetText("Mean rLambda (per week): " + new DecimalFormat("#.000").format( Epidermis.Turnover.GetBasalRate("Death",7) ));}
//                if(HeightLab!=null){HeightLab.SetText("Height: " + new DecimalFormat("#.00").format(Epidermis.GetMeanCellHeight()));}
//            }
//            if(ActivityVis!=null){YearLab.SetText("Age (yrs.): " + new DecimalFormat("#.00").format((Epidermis.GetTick() / 365f)));}
//            if(DivVis!=null&Epidermis.GetTick()%EpidermisConst.VisUpdate==0){Epidermis.ActivityHeatMap(DivVis, Epidermis, CellDraw, Epidermis.MeanProlif, "gbr");}
//            if(DivLayerVis!=null&Epidermis.GetTick()%EpidermisConst.VisUpdate==0){Epidermis.LayerVis(DivLayerVis, Epidermis, CellDraw, Epidermis.MeanProlif, "gbr");}
//            if(DeathVis!=null&Epidermis.GetTick()%EpidermisConst.VisUpdate==0){Epidermis.ActivityHeatMap(DeathVis, Epidermis, CellDraw, Epidermis.MeanDeath, "rbg");}
//            if(DeathLayerVis!=null&Epidermis.GetTick()%EpidermisConst.VisUpdate==0){Epidermis.LayerVis(DeathLayerVis, Epidermis, CellDraw, Epidermis.MeanDeath, "rbg");}
//            if(ClonalVis!=null){Epidermis.DrawCellPops(ClonalVis, Epidermis, CellDraw);} // 3D Good
//            if(OldestCell!=null){OldestCell.SetText("Mean cell age: " + new DecimalFormat("#.00").format(Epidermis.GetMeanAge(Epidermis)));}
//            if(ActivityVis!=null){Epidermis.DrawCellActivity(ActivityVis, Epidermis, CellDraw);}
//            if(BottomVis!=null){Epidermis.DrawCellPopsBottom(BottomVis, Epidermis, CellDraw);}
//            if(BottomVisMove!=null){Epidermis.DrawCellPopsBottomActivity(BottomVisMove, Epidermis, CellDraw);}
//            if(EGFVis!=null){Epidermis.DrawChemicals(EGFVis, true, false);} // 3D Good


            // Use this to get the information for 3D visualizations
//            if(EpidermisConst.GetImageData && EpidermisConst.RecordTime == Epidermis.GetTick()){
//                Epidermis.BuildMathematicaArray();
//                FileIO VisOut = new FileIO(Image_file, "w");
//                String open="{\n";
//                String closer="}\n";
//                for(int y=EpidermisConst.ySize-1; y >= 0;y--){
//                    for(int x=0; x < EpidermisConst.xSize;x++){
//                        for(int z=0; z < EpidermisConst.zSize;z++){
//                            String outLine =
//                                    Epidermis.ImageArray[y][x][z][0] + "\t" + Epidermis.ImageArray[y][x][z][1] +
//                                    "\t" + Epidermis.ImageArray[y][x][z][2] + "\t" + Epidermis.ImageArray[y][x][z][3] +
//                                    "\n";
//                            VisOut.Write(outLine);
//                        }
//                    }
//                }
//
//                VisOut.Close();
//                /* Use this code snippit to get the threeD vis on mathematica
//                file=Import["VisFile(2).txt","Data"]
//                matrix = ArrayReshape[file,{19,14,14,4}]
//                Image3D[matrix, ImageSize->Large,ColorSpace->"RGB", Axes->True,Boxed->False, Method-> {"InterpolateValues" -> False},Background->Black]
//                 */
//            }

            /*
            All Model Data Recording Is Below This line
             */
//            if(EpidermisConst.writeValues==true) {
//                /*
//                This section of the code is responsible for recording the full modeled dimensions.
//                 */
//                if (EpidermisConst.RecordParents == true && EpidermisConst.RecordTime == Epidermis.GetTick()) {
//                    FileIO ParentOut = new FileIO(ParentFile, "w");
//                    Epidermis.GenomeStore.WriteParentIDs(ParentOut, "\n");
//                    ParentOut.Close();
//                    System.out.println("Parents written to file.");
//                }
//                if (EpidermisConst.RecordLineages == true && EpidermisConst.RecordTime == Epidermis.GetTick()) {
//                    FileIO MutsOut = new FileIO(MutationFile, "w");
//                    Epidermis.GenomeStore.WriteAllLineageInfoLiving(MutsOut, ",", "\n");
//                    MutsOut.Close();
//                    System.out.println("Lineage genomes written to file.");
//                }
//                if (EpidermisConst.RecordPopSizes == true && EpidermisConst.RecordTime == Epidermis.GetTick()) {
//                    FileIO PopSizeOut = new FileIO(PopSizes, "w");
//                    Epidermis.GenomeStore.RecordClonePops();
//                    Epidermis.GenomeStore.WriteClonePops(PopSizeOut, ",", "\n");
//                    PopSizeOut.Close();
//                    System.out.println("Population sizes written to file.");
//                }
//                if (EpidermisConst.get_r_lambda == true && EpidermisConst.RecordTime == Epidermis.GetTick()) {
//                    FileIO RLambdaWriter = new FileIO(r_lambda_file, "w");
//                    float r_lamb_print = 0;
//                    for (int i = 0; i < Epidermis.Turnover.GetDeathRateBasal().length ; i++) {
//                        RLambdaWriter.Write(Epidermis.Turnover.GetDeathRateBasal()[i] + "\n");
//                        r_lamb_print+=Epidermis.Turnover.GetDeathRateBasal()[i];
//                    }
//                    RLambdaWriter.Close();
//                    System.out.println("Mean weekly rLambda: " + new DecimalFormat("#.000").format(r_lamb_print / Epidermis.Turnover.GetDeathRateBasal().length) + "\n");
//                }
//                if (EpidermisConst.get_r_lambda == true && EpidermisConst.RecordTime == Epidermis.GetTick()) {
//                    float MeanWeekPrint = 0;
//                    for (int i = 0; i < meanCellAge.size(); i++) {
//                        MeanWeekPrint += meanCellAge.get(i);
//                    }
//                }
//                if (EpidermisConst.sliceOnly==true && EpidermisConst.RecordTime == Epidermis.GetTick()){
//                    FileIO PositionOut = new FileIO(PositionFile, "w");
//                    Epidermis.GetCellPositions(PositionOut);
//                    PositionOut.Close();
//                    System.out.println("Position Information Saved to File");
//                }
//            }

            if (Epidermis.GetTick() > 1) {
                Epidermis.FixInfo.CheckFixation(); // Captures frequency after runstep.
                EndRun = Epidermis.FixInfo.GetFreq(); // Retrieves the Frequency of Mut
//                    System.out.println(EndRun);
                if (EndRun == 5.0) {
                    Done = false;
                    FixedTime = "na";
//                        System.out.println(EndRun);
                } else if (EndRun == 1.0) {
                    Done = false;
                    FixedTime = (Epidermis.GetTick() - 1.0) + "";
                }
                if (Epidermis.GetTick() == EpidermisConst.RecordTime) {
                    Done = false;
                    FixedTime = "Clock"; // Ran 100 Years and didn't fix.
                }
            }

        }

        // Done with program...
        float r_lambOut = 0;
        int r_lambda_index = 0;
        for (int k = 0; k < EpidermisConst.RecordTime - 1; k++) {
            r_lambOut += Epidermis.Turnover.GetDeathRateBasal()[k];
            r_lambda_index++;
        }
        /*
         Format: Replicate , FixationTime , CellAge , TissueHeight , MeanRLambda, EndTick
         If FixedTime == nan then the mutation was lost due to drift
        */
        System.out.println(Rep + "\t" + FixProb + "\t" + FixedTime + "\t" + "NaN" + "\t" + Epidermis.GetMeanCellHeight() + "\t" + (r_lambOut / r_lambda_index) + "\t" + Epidermis.GetTick());
    }
}
