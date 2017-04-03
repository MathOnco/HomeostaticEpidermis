package Epidermis_Model;
import java.util.ArrayList;

/**
 * Created by schencro on 3/25/17.
 */

public class EpidermisCellGenome {
    static final int genomelength = 73;
    static final double humangenome = 3200000000.0;
    static final long[] genelengths=new long[]{3191618082l, 59823,108204,8731,24252,50898,42645,45841,71006,22907,753772,190752,20074,13370,98250,7382,35596,155066,115248,24908,137920,26125,28662,8630,1162911,76558,20134,98997,123939,56708,115638,15561,11270,419393,220963,3309,20553,338011,16686,45675,59146,41142,38777,112449,33587,31229,127078,32403,8901,12431,145748,20439,86187,519254,42201,158722,49677,105338,1117166,59211,41598,14392,15015,84511,88892,54829,16034,2512,201692,114698,13018,107620,29653};
    static final double[] geneproportion=new double[]{0.997380650625, 1.86946875e-05, 3.381375e-05, 2.7284375e-06, 7.57875e-06, 1.5905625e-05, 1.33265625e-05, 1.43253125e-05, 2.2189375e-05, 7.1584375e-06, 0.00023555375, 5.961e-05, 6.273125e-06, 4.178125e-06, 3.0703125e-05, 2.306875e-06, 1.112375e-05, 4.8458125e-05, 3.6015e-05, 7.78375e-06, 4.31e-05, 8.1640625e-06, 8.956875e-06, 2.696875e-06, 0.0003634096875, 2.3924375e-05, 6.291875e-06, 3.09365625e-05, 3.87309375e-05, 1.772125e-05, 3.6136875e-05, 4.8628125e-06, 3.521875e-06, 0.0001310603125, 6.90509375e-05, 1.0340625e-06, 6.4228125e-06, 0.0001056284375, 5.214375e-06, 1.42734375e-05, 1.8483125e-05, 1.2856875e-05, 1.21178125e-05, 3.51403125e-05, 1.04959375e-05, 9.7590625e-06, 3.9711875e-05, 1.01259375e-05, 2.7815625e-06, 3.8846875e-06, 4.554625e-05, 6.3871875e-06, 2.69334375e-05, 0.000162266875, 1.31878125e-05, 4.9600625e-05, 1.55240625e-05, 3.2918125e-05, 0.000349114375, 1.85034375e-05, 1.2999375e-05, 4.4975e-06, 4.6921875e-06, 2.64096875e-05, 2.777875e-05, 1.71340625e-05, 5.010625e-06, 7.85e-07, 6.302875e-05, 3.5843125e-05, 4.068125e-06, 3.363125e-05, 9.2665625e-06};

    // Creating the genome Arraylist with 75 ArrayLists within it.
    int [] mut_counts;
    ArrayList<String>[] mut_pos; //waiting for constructor
    EpidermisCellGenome(){ //Constructor to set up ArrayLists for genes
        mut_pos=(ArrayList[])new ArrayList[genomelength];
        mut_counts=new int[genomelength];
        for(int i=0;i<genomelength;i++){
            mut_pos[i]=new ArrayList<>();
        }
    }

    // Add values to mut_pos, function
    void mut_pos_setter(int geneindex, String pos){
        if(geneindex != 0) {

            mut_pos[geneindex].add(pos);
        }
        mut_counts[geneindex]++;
    }

    // Pull values from mut_pos, function
    String mut_pos_getter(int geneindex, int pos_index){
        return mut_pos[geneindex].get(pos_index);
    }

}
