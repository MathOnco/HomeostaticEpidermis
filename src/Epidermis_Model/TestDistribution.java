package Epidermis_Model;

import cern.jet.random.Beta;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static Epidermis_Model.EpidermisCell.RNEngine;

/**
 * Created by schencro on 4/26/17.
 */
abstract public class TestDistribution {

    public static void main(String[] args) {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        final Beta beta_dist = new Beta(15, 1.5, RNEngine);
        ArrayList<String> testout = new ArrayList(100000);
        for (int i=0; i<=100000; i++){
            double test = beta_dist.nextDouble() - 0.99;
            max = test>max? test: max;
            min = test<min? test: min;
            testout.add(i, String.valueOf(test));
        }
        try {
            FileWriter writer = new FileWriter("/Users/schencro/Desktop/output.txt");
            for(String val: testout) {
                String out = val + "\n";
                writer.write(out);
            }
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        System.out.println(max);
        System.out.println(min);


    }

}
