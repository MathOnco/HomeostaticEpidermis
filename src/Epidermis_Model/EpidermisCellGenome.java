package Epidermis_Model;
import AgentFramework.FileIO;
import AgentFramework.GenomeInfo;
import AgentFramework.Utils;
import cern.jet.random.Poisson;
import com.sun.org.apache.bcel.internal.generic.PUSH;

import java.util.ArrayList;
import java.util.Random;

import static AgentFramework.Utils.GetHSBtoRGB;
import static Epidermis_Model.EpidermisCell.RNEngine;

/**
 * Created by schencro on 3/25/17.
 */


public class EpidermisCellGenome extends GenomeInfo <EpidermisCellGenome> {
    /*
    New Information To Keep Inside the Model!!!!! Official Information
     */
    private static final String BaseIndexFile= System.getProperty("user.dir") + "/src/Epidermis_Model/Global_Info/BaseIndexes.csv";
    static final int GenomeComponents = 71;
    static final double HumanGenome = 3200000000.0;
    static final String[] GeneNames = new String[]{"Genome","ADAM29","ADAMTS18","AJUBA","AKT1","AKT2","APOB","ARID1A","ARID2","AURKA","BAI3","BRAF","CASP8","CCND1","CDH1","CDKN2A","CR2","CREBBP","CUL3","DICER1","EGFR","EPHA2","ERBB2","ERBB3","ERBB4","EZH2","FAT1","FAT4","FBXW7","FGFR1","FGFR2","FGFR3","FLG2","GRIN2A","GRM3","HRAS","IRF6","KCNH5","KEAP1","KRAS","MET","MUC17","NF1","NFE2L2","NOTCH1","NOTCH2","NOTCH3","NRAS","NSD1","PCED1B","PIK3CA","PLCB1","PPP1R3A","PREX2","PTCH1","PTEN","PTPRT","RB1","RBM10","SALL1","SCN11A","SCN1A","SETD2","SMAD4","SMO","SOX2","SPHKAP","SUFU","TP53","TP63","TRIOBP"};
    static final int dumb = GeneNames.length;
    static final long[] GeneLengths = new long[]{3191618082l,2463l,3666l,1617l,1443l,1444l,13692l,6205l,5506l,1212l,4569l,2301l,1440l,888l,2649l,514l,3279l,7329l,2307l,5767l,3633l,2931l,3695l,4023l,3927l,2239l,13767l,14944l,2124l,2616l,2614l,2443l,7176l,4391l,2781l,633l,1404l,1976l,1875l,687l,4185l,13482l,8520l,1771l,7668l,7416l,6966l,570l,7351l,1299l,3207l,3750l,3369l,4821l,4337l,1212l,4383l,2787l,2951l,3985l,5376l,6026l,7695l,1659l,2364l,954l,5103l,1455l,1289l,2041l,7098};
    //static final double[] ExpectedMuts = new double[]{1.02E+01,1.2298960944e-05,1.96790865336e-05,5.17789144098534e-06,4.0831751376e-06,3.4550775719999997e-06,5.31298384596e-05,1.09091587995e-05,1.4262587681399999e-05,5.799936312e-06,3.26067900354e-05,7.5642604596e-06,3.1009560480000003e-06,2.632816548e-06,4.9328483301e-06,1.4758253784000002e-06,1.01311941753e-05,1.31902673427e-05,7.5599847855e-06,1.7638435243800002e-05,1.5155579382299998e-05,8.232701246999999e-06,8.4182405355e-06,1.0923286209299999e-05,2.01405160341e-05,7.383735465300001e-06,5.52827183922e-05,7.44672207456e-05,4.5076216176e-06,6.3409807368e-06,6.0913895615999996e-06,6.023301272100001e-06,2.7098910352799996e-05,1.43892038115e-05,1.96441633269e-05,1.9557932031000003e-06,4.7323285776e-06,8.1609406632e-06,4.252267125e-06,2.5443069732000003e-06,1.1842056792e-05,4.2777651231e-05,1.9588205052e-05,4.5322769646000005e-06,2.17774750284e-05,2.8159527204e-05,2.39869062126e-05,1.577318022e-06,2.11065999156e-05,4.15960481251698e-06,1.3912263288900002e-05,1.5799708125e-05,1.98371928474e-05,3.12956873424e-05,9.8357773446e-06,2.3451916392e-06,2.41207004817e-05,7.1323861662e-06,7.7055865731e-06,1.6665454107e-05,1.33106416128e-05,2.2693447177199998e-05,1.28883485745e-05,5.3329515560999995e-06,5.4104442479999995e-06,4.0392948618e-06,2.79768591711e-05,2.5580702745e-06,2.9903398857e-06,6.2211890403e-06,1.60235412246e-05};
    static final double[] ExpectedMuts = new double[]{3.5,1.2298960944e-05,1.96790865336e-05,5.17789144098534e-06,4.0831751376e-06,3.4550775719999997e-06,5.31298384596e-05,1.09091587995e-05,1.4262587681399999e-05,5.799936312e-06,3.26067900354e-05,7.5642604596e-06,3.1009560480000003e-06,2.632816548e-06,4.9328483301e-06,1.4758253784000002e-06,1.01311941753e-05,1.31902673427e-05,7.5599847855e-06,1.7638435243800002e-05,1.5155579382299998e-05,8.232701246999999e-06,8.4182405355e-06,1.0923286209299999e-05,2.01405160341e-05,7.383735465300001e-06,5.52827183922e-05,7.44672207456e-05,4.5076216176e-06,6.3409807368e-06,6.0913895615999996e-06,6.023301272100001e-06,2.7098910352799996e-05,1.43892038115e-05,1.96441633269e-05,1.9557932031000003e-06,4.7323285776e-06,8.1609406632e-06,4.252267125e-06,2.5443069732000003e-06,1.1842056792e-05,4.2777651231e-05,1.9588205052e-05,4.5322769646000005e-06,2.17774750284e-05,2.8159527204e-05,2.39869062126e-05,1.577318022e-06,2.11065999156e-05,4.15960481251698e-06,1.3912263288900002e-05,1.5799708125e-05,1.98371928474e-05,3.12956873424e-05,9.8357773446e-06,2.3451916392e-06,2.41207004817e-05,7.1323861662e-06,7.7055865731e-06,1.6665454107e-05,1.33106416128e-05,2.2693447177199998e-05,1.28883485745e-05,5.3329515560999995e-06,5.4104442479999995e-06,4.0392948618e-06,2.79768591711e-05,2.5580702745e-06,2.9903398857e-06,6.2211890403e-06,1.60235412246e-05};
    private static final double[] BaseMutProb = new double[]{1.0/6,3/6.,1.0/6,1.0/6};
    private static final long[][][] BaseIndex = ParseBaseIndexes();
    private static final String[] Base = new String[]{"A","C","G","T"};
    private static final boolean QuickMut = false;
    private static final double QuickMutRate = 0.1;
    static final Random RN=new Random();
    EpidermisGrid theGrid;
    String PrivateGenome;
    float h;
    float s;
    float v;

    /*
    End New Information To Keep Inside the Model!!!!!
     */

    EpidermisCellGenome(float h, float s, float v, String PrivateGenome, EpidermisGrid theGrid) {
        this.h = h;
        this.s = s;
        this.v = v;
        this.PrivateGenome = PrivateGenome;
        this.theGrid = theGrid;
    }

    @Override
    public EpidermisCellGenome _RunPossibleMutation() {
        StringBuilder MutsObtained = new StringBuilder();
        if (QuickMut == false) {
            for (int j = 0; j < ExpectedMuts.length; j++) {
                if (j != 0) {
                    Poisson poisson_dist = new Poisson(ExpectedMuts[j], RNEngine); // Setup the Poisson distributions for each gene.
                    int mutations = poisson_dist.nextInt(); // Gets how many mutations will occur for each gene
                    for (int hits = 0; hits < mutations; hits++) {
                        int MutatedBaseKind = Utils.RandomVariable(BaseMutProb, RN);
                        long mutIndex = BaseIndex[j - 1][MutatedBaseKind][RN.nextInt(BaseIndex[j - 1][MutatedBaseKind].length)];
                        String MutOut = "";
                        if (j == ExpectedMuts.length - 1) {
                            MutOut = theGrid.GetTick() + "." + j + "." + Base[MutatedBaseKind] + "." + mutIndex;
                        } else {
                            MutOut = theGrid.GetTick() + "." + j + "." + Base[MutatedBaseKind] + "." + mutIndex + ",";
                        }
                        MutsObtained.append(MutOut);
                    }
                }
                //            else {
                //                if(EpidermisConst.GuiOn == true) {
                //                    Poisson poisson_dist = new Poisson(ExpectedMuts[j], RNEngine); // Setup the Poisson distributions for each gene.
                //                    int mutations = poisson_dist.nextInt(); // Gets how many mutations will occur for the Genome
                //                    for (int hits = 0; hits < mutations; hits++) {
                //                        long mutIndex = RN.nextLong();
                //                        String MutOut = "";
                //                        if(j==ExpectedMuts.length-1){
                //                            MutOut = j + "." + ".N." + "." + mutIndex;
                //                        } else {
                //                            MutOut = j + "." + ".N." + "." + mutIndex + ",";
                //                        }
                //                        MutsObtained.append(MutOut);
                //                    }
                //                }
                //            }}


            }
            String PrivGenome = MutsObtained.toString();
            if (PrivGenome.length() > 0) {
                if (h == 0f && s == 0f && v == 1f) {
                    return new EpidermisCellGenome(RN.nextFloat(), 1f , 0.75f, PrivGenome, theGrid);
                } else {
                    return new EpidermisCellGenome(h, RN.nextFloat()*0.3f+0.6f, RN.nextFloat()*0.55f+0.3f, PrivGenome, theGrid);
                }
            } else {
                return null; // If No Mutation Occurs
            }
        } else {
            if(RN.nextDouble()<QuickMutRate){
                String EmptyGenome = "";
                if (h == 0f && s == 0f && v == 1f) {
                    return new EpidermisCellGenome(RN.nextFloat(), 1f , 0.75f, EmptyGenome, theGrid);
                } else {
                    return new EpidermisCellGenome(h, RN.nextFloat()*0.3f+0.6f, RN.nextFloat()*0.55f+0.3f, EmptyGenome, theGrid);
                }
            } else {
                return null;
            }
        }
    }

    @Override
    public String GenomeInfoStr() {
        return PrivateGenome;
    }

    // Parses Base Mutation Information
    private static long[][][] ParseBaseIndexes(){
        FileIO reader = new FileIO(BaseIndexFile, "r");
        ArrayList<long[]> data = new ArrayList<> (reader.ReadLongDelimit(","));
        long[][][] BaseIndexes = new long[data.size()/4][4][];
        for (int i = 0; i < data.size(); i+=4) {
            BaseIndexes[i/4][0] = data.get(i);
            BaseIndexes[i/4][1] = data.get(i+1);
            BaseIndexes[i/4][2] = data.get(i+2);
            BaseIndexes[i/4][3] = data.get(i+3);
        }
        return BaseIndexes;
    }

    // Parses Base Mutation Function Information
//    public String long[][] ParseMutationInfo(){
//        FileIO reader = new FileIO(BaseIndexFile, "h");
//        ArrayList<long[]> data = new ArrayList<>(reader.ReadBinString(",")));
//    }
}
