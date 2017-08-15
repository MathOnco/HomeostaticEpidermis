package AgentFramework;

import AgentFramework.Interfaces.Sortable;

import java.awt.*;
import java.lang.reflect.Array;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

/**
 * A collection of helpful static utility functions
 * recommended import: import static Utils.*
 * Created by rafael on 10/11/16.
 */
public final class Utils {

    /**
     * Samples a gaussian with the provided mean and standard deviation
     * @param mean the mean of the gaussian
     * @param stdDev the standard deviation of the gaussian
     * @param rn the random number generator that will be used
     * @return a single value sampled from the defined Gaussian
     */
    public static double Gaussian(double mean,double stdDev,Random rn){
        return rn.nextGaussian()*stdDev+mean;
    }

    /**
     * Returns the coordinates defining the Von Neumann neighborhood centered on (0,0)
     * @param includeOrigin defines whether to include the origin (0,0)
     * @return coordinates returned as an array of the form [x,y,x,y...]
     */
    public static int[] VonNeumannHood(boolean includeOrigin){
        if(includeOrigin) {
            return new int[]{0,0,1, 0, -1, 0, 0, 1, 0, -1};
        }
        else {
            return new int[]{1, 0, -1, 0, 0, 1, 0, -1};
        }
    }

    /**
     * Returns the coordinates defining the Moore neighborhood centered on (0,0)
     * @param includeOrigin defines whether to include the origin (0,0)
     * @return coordinates returned as an array of the form [x,y,x,y,...]
     */
    public static int[] MooreHood(boolean includeOrigin){
        if(includeOrigin) {
            return new int[]{0,0,1, 1, 1, 0, 1, -1, 0, -1, -1, -1, -1, 0, -1, 1, 0, 1};
        }
        else{
            return new int[]{1, 1, 1, 0, 1, -1, 0, -1, -1, -1, -1, 0, -1, 1, 0, 1};
        }
    }

    /**
     * Returns the coordinates defining the Hexagonal neighborhood for even y coordinates centered on (0,0)
     * @param includeOrigin defines whether to include the origin (0,0)
     * @return coordinates returned as an array of the form [x,y,x,y,...]
     */
    public static int[] HexEvenHood(boolean includeOrigin) {
        if (includeOrigin) {
            return new int[]{0,0,1, 1, 1, 0, 1, -1, 0, -1, -1, 0, 0, 1};
        }
        else{
            return new int[]{1, 1, 1, 0, 1, -1, 0, -1, -1, 0, 0, 1};
        }
    }

    /**
     * Returns the coordinates defining the Hexagonal neighborhood for odd y coordinates centered on (0,0)
     * @param includeOrigin defines whether to include the origin (0,0)
     * @return coordinates returned as an array of the form [x,y,x,y,...]
     */
    public static int[] HexOddHood(boolean includeOrigin) {
        if (includeOrigin) {
            return new int[]{0,0,1, 0, 0, -1, -1, -1, -1, 0, -1, 1, 0, 1};
        }
        else {
            return new int[]{1, 0, 0, -1, -1, -1, -1, 0, -1, 1, 0, 1};
        }
    }

    /**
     * Returns the coordinates defining the Von Neumann neighborhood centered on (0,0,0)
     * @param includeOrigin defines whether to include the origin (0,0,0)
     * @return coordinates returned as an array of the form [x,y,z,x,y,z,...]
     */
    public int[] VonNeumannHood3D(boolean includeOrigin) {
        if (includeOrigin) {
            return new int[]{0, 0, 0, 1, 0, 0, -1, 0, 0, 0, 1, 0, 0, -1, 0, 0, 0, 1, 0, 0, -1};
        }
        else {
            return new int[]{1, 0, 0, -1, 0, 0, 0, 1, 0, 0, -1, 0, 0, 0, 1, 0, 0, -1};
        }
    }

    //OTHER COORDINATE FUNCTIONS

    /**
     * Returns an array of all squares touching a line between the positions provided
     * @param x0 the x coordinate of the starting position
     * @param y0 the y coordinate of the starting position
     * @param x1 the x coordinate of the ending position
     * @param y1 the y coordinate of the ending position
     * @return coordinates return as an array of the form [x,y,x,y,...]
     */
    public static int[] SquaresAlongLine(double x0,double y0, double x1,double y1) {
        double dx = Math.abs(x1 - x0);
        double dy = Math.abs(y1 - y0);

        int x = (int)(Math.floor(x0));
        int y = (int)(Math.floor(y0));

        int n = 1;
        int x_inc, y_inc;
        double error;

        if (dx == 0)
        {
            x_inc = 0;
            error = Double.MAX_VALUE;
        }
        else if (x1 > x0)
        {
            x_inc = 1;
            n += (int)(Math.floor(x1)) - x;
            error = (Math.floor(x0) + 1 - x0) * dy;
        }
        else
        {
            x_inc = -1;
            n += x - (int)(Math.floor(x1));
            error = (x0 - Math.floor(x0)) * dy;
        }

        if (dy == 0)
        {
            y_inc = 0;
            error -= Double.MAX_VALUE;
        }
        else if (y1 > y0)
        {
            y_inc = 1;
            n += (int)(Math.floor(y1)) - y;
            error -= (Math.floor(y0) + 1 - y0) * dx;
        }
        else
        {
            y_inc = -1;
            n += y - (int)(Math.floor(y1));
            error -= (y0 - Math.floor(y0)) * dx;
        }

        int[] writeHere=new int[n^2];
        int Count=0;
        for (; n > 0; --n)
        {
            writeHere[Count*2]=(int)Math.floor(x);
            writeHere[Count*2]=(int)Math.floor(y);
            Count++;

            if (error > 0)
            {
                y += y_inc;
                error -= dx;
            }
            else
            {
                x += x_inc;
                error += dy;
            }
        }
        return writeHere;
    }

    /**
     * Writes to the array argument all squares touching a line between the positions provided
     * @param writeHere the array to write the coordinates into
     * @param x0 the x coordinate of the starting position
     * @param y0 the y coordinate of the starting position
     * @param x1 the x coordinate of the ending position
     * @param y1 the y coordinate of the ending position
     * @return the number of touching squares
     */
    public static int SquaresAlongLine(int[] writeHere,double x0,double y0, double x1,double y1){
        double dx = Math.abs(x1 - x0);
        double dy = Math.abs(y1 - y0);

        int x = (int)(Math.floor(x0));
        int y = (int)(Math.floor(y0));

        int n = 1;
        int x_inc, y_inc;
        double error;

        if (dx == 0)
        {
            x_inc = 0;
            error = Double.MAX_VALUE;
        }
        else if (x1 > x0)
        {
            x_inc = 1;
            n += (int)(Math.floor(x1)) - x;
            error = (Math.floor(x0) + 1 - x0) * dy;
        }
        else
        {
            x_inc = -1;
            n += x - (int)(Math.floor(x1));
            error = (x0 - Math.floor(x0)) * dy;
        }

        if (dy == 0)
        {
            y_inc = 0;
            error -= Double.MAX_VALUE;
        }
        else if (y1 > y0)
        {
            y_inc = 1;
            n += (int)(Math.floor(y1)) - y;
            error -= (Math.floor(y0) + 1 - y0) * dx;
        }
        else
        {
            y_inc = -1;
            n += y - (int)(Math.floor(y1));
            error -= (y0 - Math.floor(y0)) * dx;
        }

        int Count=0;
        for (; n > 0; --n)
        {
            writeHere[Count*2]=(int)Math.floor(x);
            writeHere[Count*2]=(int)Math.floor(y);
            Count++;

            if (error > 0)
            {
                y += y_inc;
                error -= dx;
            }
            else
            {
                x += x_inc;
                error += dy;
            }
        }
        return Count;
    }


    /**
     * Returns the coordinates of all squares whose centers lie within a circle of the provided radius, centered on (0,0)
     * @param includeOrigin defines whether to include the origin (0,0)
     * @param radius the radius of the circle
     * @return coordinates returned as an array of the form [x,y,x,y,...]
     */
    static public int[] CircleCentered(boolean includeOrigin,double radius){
        double distSq=radius*radius;
        int min=(int)Math.floor(-radius);
        int max=(int)Math.ceil(radius);
        int[] retLong=new int[((max+1-min)*(max+1-min))*2];
        int ct=0;
        if(includeOrigin){
            ct++;
            retLong[0]=0;
            retLong[1]=0;
        }
        for(int x=min;x<=max;x++){
            for(int y=min;y<=max;y++){
                if(Utils.DistSq2D(0,0,x,y)<=distSq){
                    if(x==0&&y==0){
                        continue;
                    }
                    retLong[ct*2]=x;
                    retLong[ct*2+1]=y;
                    ct++;
                }
            }
        }
        int[] ret=new int[ct*2];
        System.arraycopy(retLong, 0, ret, 0, ret.length);
        return ret;
    }

    /**
     * Returns the coordinates of all squares whose centers lie within a rectangle of the provided radius, centered on (0,0)
     * @param includeOrigin defines whether to include the origin (0,0)
     * @param radX the radius of the rectangle in the x direction
     * @param radY the radius of the rectangle in the y direction
     * @return coordinates returned as an array of the form [x,y,x,y,...]
     */
    static public int[] RectCentered(boolean includeOrigin,int radX,int radY){
        //returns a square with a center location at 0,0
        int[] dataIn;
        int nCoord;
        if(includeOrigin) {
            dataIn=new int[(radX * 2 + 1) * (radY * 2 + 1)*2];
            dataIn[0]=0;
            dataIn[1]=0;
            nCoord=1;
        }
        else{
            dataIn=new int[(radX * 2 + 1) * (radY * 2 + 1)*2-1];
            nCoord=0;
        }
        for(int x=-radX;x<=radX;x++){
            for(int y=-radY;y<=radY;y++){
                if(x==0&&y==0){
                    continue;
                }
                dataIn[nCoord*2]=x;
                dataIn[nCoord*2+1]=y;
                nCoord++;
            }
        }
        return dataIn;
    }

    //MATH FUNCTIONS

    /**
     * Samples a discrete random variable from the probabilities provided
     * @param probs an array of probabilities. should sum to 1
     * @param rn the random number generator to be used
     * @return the index of the probability bin that was sampled
     */
    public static int RandomVariable(double[]probs,Random rn){
        double rand=rn.nextDouble();
        for(int i=0;i<probs.length;i++){
            rand-=probs[i];
            if(rand<0){
                return i;
            }
        }
        return -1;
    }

    /**
     * sets the values in the array such that they sum to 1
     * @param vals an array of values
     */
    public static void SumTo1(double[] vals){
        double tot=0;
        for(int i=0;i<vals.length;i++){
            tot+=vals[i];
        }
        for(int i=0;i<vals.length;i++){
            vals[i]=vals[i]/tot;
        }
    }

    /**
     * uses the Michaelis Menten equation to compute the reaction rate for a given substrate concentration
     * @param conc concentration of the reaction limiting substrate
     * @param maxRate reaction rate given maximum concentration
     * @param halfRateConc substrate concentration at which the reaction rate is 1/2 the maximum
     * @return the reaction rate at the given substrate concentration
     */
    public static double MichaelisMenten(final double conc,final double maxRate,final double halfRateConc){
        if(conc>0) {
            return -(maxRate * conc) / (halfRateConc + conc);
        }
        return 0;
    }

    /**
     * Shuffles an array of integers
     * @param arr array to be shuffled
     * @param lenToShuffle number of elements from array that shuffling can swap
     * @param Count number of elements that will be shuffled, should not exceed lenToShuffle
     * @param rn the random number generator to be used
     */
    public static void Shuffle(int[] arr, int lenToShuffle, int Count, Random rn){
        for(int i=0;i<Count;i++){
            int iSwap=rn.nextInt(lenToShuffle-i)+i;
            int swap=arr[iSwap];
            arr[iSwap]=arr[i];
            arr[i]=swap;
        }
    }

    /**
     * Shuffles an array of doubles
     * @param arr array to be shuffled
     * @param lenToShuffle number of elements from array that shuffling can swap
     * @param Count number of elements that will be shuffled, should not exceed lenToShuffle
     * @param rn the random number generator to be used
     */
    public static void Shuffle(double[] arr,int lenToShuffle,int Count,Random rn){
        for(int i=0;i<Count;i++){
            int iSwap=rn.nextInt(lenToShuffle-i)+i;
            double swap=arr[iSwap];
            arr[iSwap]=arr[i];
            arr[i]=swap;
        }
    }

    /**
     * Shuffles an array of objects
     * @param arr array to be shuffled
     * @param lenToShuffle number of elements from array that shuffling can swap
     * @param Count number of elements that will be shuffled, should not exceed lenToShuffle
     * @param rn the random number generator to be used
     */
    public static void Shuffle(Object[] arr,int lenToShuffle,int Count,Random rn){
        for(int i=0;i<Count;i++){
            int iSwap=rn.nextInt(lenToShuffle-i)+i;
            Object swap=arr[iSwap];
            arr[iSwap]=arr[i];
            arr[i]=swap;
        }
    }

    /**
     * returns the distance squared between the two position provided in 2D
     * @param x1 the x coordinate of the first position
     * @param y1 the y coordinate of the first position
     * @param x2 the x coordinate of the second position
     * @param y2 the y coordinate of the second position
     * @return the distance squared between the first and second position
     */
    public static double DistSq2D(double x1, double y1, double x2, double y2){
        double xDist=x2-x1,yDist=y2-y1;
        return xDist*xDist+yDist*yDist;
    }

    /**
     * returns the distance squared between the two position provided in any number of dimensions
     * @param p1 the coordinates of the first position
     * @param p2 the coordinates of the second position
     * @return the distance squared between the first and second position
     */
    public static double DistSqND(double[]p1,double[]p2){
        double sum=0;
        for(int i=0;i<p1.length;i++){
            double diff=p1[i]-p2[i];
            sum+=diff*diff;
        }
        return sum;
    }

    /**
     * returns the mean value of the provided array
     */
    static public double Mean(double[] a){
        double tot=0;
        for(int i=0;i<a.length;i++){
            tot+=a[i];
        }
        return tot/a.length;
    }

    /**
     * returns the original value bounded by min and max inclusive
     */
    public static double BoundVal(double val, double min, double max){ return val<min?min:(val>max?max:val); }

    /**
     * returns the original value bounded by min and max inclusive
     */
    public static int BoundValI(int val, int min, int max){ return val<min?min:(val>max?max:val);}

    /**
     * returns the original value bounded by min and max inclusive
     */
    public static float BoundValF(float val, double min, double max){ return (float) (val<min?min:(val>max?max:val));}

    /**
     * returns where the value is from min to max as a number from 0 to 1
     */
    public static double ScaleVal(double val,double min,double max){
        return (val-min)/(max-min);
    }

    /**
     * returns value with wraparound between 0 and max
     */
    public static int ModWrap(int val,int max) {
        return val<0?max+val%max:val%max;
    }

    /**
     * converts proton concentration to pH
     */
    public static double ConvertHToPh(double h){
        return -Math.log10(h)+3;
    }

    /**
     * converts pH to proton concentration
     */
    public static double ConvertPhToH(double ph){
        return Math.pow(10,3.0-ph);
    }

    /**
     adjusts probability that an event will occur in 1 unit of time to the probability that the event will occur in timeFraction duration
     * @param prob probability that an event occurs in 1 unit of time
     * @param timeFraction duration to over which event may occur
     * @return the probability that the event will occur in timeFraction
     */
    public static double ProbScale(double prob, double timeFraction){
        return 1.0f-(Math.pow(1.0-prob,1.0/timeFraction));

    }

    //LIST FUNCTIONS

    /**
     * Returns a new array that is the first array with the second concatenated to the end of it
     * @param <T> the type of the input and output arrays
     */
    public static <T> T[] Concat(T[] first,T[] second){
        int firstLen=first.length;
        int secondLen=second.length;
        T[] ret= (T[]) Array.newInstance(first.getClass().getComponentType(),firstLen+secondLen);
        System.arraycopy(first,0,ret,0,firstLen);
        System.arraycopy(second,0,ret,firstLen,firstLen+secondLen);
        return ret;
    }

    /**
     * Returns a new array that is the first array with the appendMe object appended to the end of it
     * @param <T> the type of the inputs and output array
     */
    public static <T> T[] Append(T[] arr, T appendMe){
        int firstLen=arr.length;
        T[] ret= (T[]) Array.newInstance(arr.getClass().getComponentType(),firstLen+1);
        System.arraycopy(arr,0,ret,0,firstLen);
        ret[firstLen]=appendMe;
        return ret;
    }

    /**
     * Generates a list of sequential integers starting at 0, and then shuffles them
     * @param nEntries the length of the list to be generated
     * @param CountRandom the number of elements that should be shuffled
     * @param rn the random number generator to be used
     * @return returns the array of indices after being shuffled
     */
    public static int[] RandomIndices(int nEntries,int CountRandom,Random rn){
        int indices[]=new int[nEntries];
        for(int i=0;i<nEntries;i++){
            indices[i]=i;
        }
        Shuffle(indices,indices.length,CountRandom,rn);
        return indices;
    }

    /**
     * Fills out with random doubles between min and max inclusive
     * @param out the array the random doubles should be written to. the length of the input array defines the number of doubles to be generated
     * @param rn the random number generator to be used
     */
    public static void RandomDS(double[] out,double min,double max,Random rn){
        for(int i=0;i<out.length;i++){
            out[i]=rn.nextDouble()*(max-min)+min;
        }
    }

    /**
     * Fills out with random integers between min (inclusive) and max (exclusive)
     * @param out the array the random doubles should be written to. the length of the input array defines the number of doubles to be generated
     * @param rn the random number generator to be used
     */
    public static void RandomIS(int[] out,int min,int max,Random rn){
        for(int i=0;i<out.length;i++) {
            out[i]=rn.nextInt(max-min)+min;
        }
    }

    /**
     * runs a fully connected neural network layer
     * @param neurons array of all neurons in the network
     * @param weights array of weights in the fully connected layer
     * @param iFromStart index of the start of the input to the fully connected layer
     * @param iFromEnd index of the end of the input to the fully connected layer
     * @param iToStart index of the start of the output of the fully connected layer
     * @param iToEnd index of the end of the output of the fully connected layer
     * @param iWeightStart index of the start of the weights for this layer out of the weights array
     */
    public static void NNfullyConnectedLayer(double[] neurons,double[] weights,int iFromStart,int iFromEnd,int iToStart,int iToEnd,int iWeightStart){
        int iWeight=iWeightStart;
        for(int iFrom=iFromStart;iFrom<iFromEnd;iFrom++){
            for(int iTo=iToStart;iTo<iToEnd;iTo++){
                neurons[iTo]+=neurons[iFrom]*weights[iWeight];
                iWeight++;
            }
        }
    }

    /**
     * set all neurons between iStart and iEnd with the given value
     */
    public static void NNset(double[] neurons,int iStart,int iEnd,double val) {
        Arrays.fill(neurons, iStart, iEnd, val);
    }

    //UTILITIES

    /**
     * returns a timestamp of the form "yyyy_MM_dd_HH_mm_ss" as a string
     */
    static public String TimeStamp(){
        return new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());

    }

    /**
     * gets the current working directory as a string
     */
    static public String PWD(){
        return Paths.get("").toAbsolutePath().toString()+"/";
        //return System.getProperty("user.dir");
    }

    /**
     * Runs quicksort on an object that implements Sortable
     * @param sortMe the object to be sorted
     * @param greatestToLeast if true, sorting will be form greatest to least, otherwise will be least to greatest
     */
    public static <T extends Sortable> void QuickSort(T sortMe,boolean greatestToLeast){
        SortHelper(sortMe,0,sortMe.Length()-1,greatestToLeast);
    }
    static <T extends Sortable> void SortHelper(T sortMe,int lo,int hi,boolean greatestToLeast){
        if(lo<hi){
            int p=Partition(sortMe,lo,hi,greatestToLeast);
            SortHelper(sortMe,lo,p-1,greatestToLeast);
            SortHelper(sortMe,p+1,hi,greatestToLeast);
        }
    }
    static <T extends Sortable> int Partition(T sortMe,int lo,int hi,boolean greatestToLeast){
        if(greatestToLeast) {
            for (int j = lo; j < hi; j++) {
                if (sortMe.Compare(hi, j) <= 0) {
                    sortMe.Swap(lo, j);
                    lo++;
                }
            }
            sortMe.Swap(lo, hi);
            return lo;
        }
        else{
            for (int j = lo; j < hi; j++) {
                if (sortMe.Compare(hi, j) >= 0) {
                    sortMe.Swap(lo, j);
                    lo++;
                }
            }
            sortMe.Swap(lo, hi);
            return lo;
        }
    }
    /**
     * runs the diffusion equation in 2 dimensions
     * @param inGrid an array of values holding the starting state of the diffusible
     * @param outGrid an array into which the result of diffusion will be written
     * @param xDim x dimenison of the inGrid and outGrid
     * @param yDim y dimension of the inGrid and outGrid
     * @param diffRate diffusion rate for the diffusion equaition
     * @param boundaryCond defines whether a constant boundary condition value should diffuse in from the boundaries
     * @param boundaryValue only impacts diffusion if boundaryCond is true, sets the boundary condition value
     * @param wrapX whether to wrap around diffusion over the left and right boundaries
     */
    public static void Diffusion(double []inGrid,double[]outGrid,int xDim, int yDim,double diffRate, boolean boundaryCond,double boundaryValue,boolean wrapX, boolean wrapY){
        int x, y, z;
        double valSum;
        for (x = 0; x < xDim; x++) {
            for (y = 0; y < yDim; y++) {
                //4 squares to check
                valSum = 0;
                if (InDim(xDim, x + 1)) {
                    valSum += inGrid[(x + 1) * yDim + (y)];
                } else if (boundaryCond) {
                    valSum += boundaryValue;
                } else if (wrapX) {
                    valSum += inGrid[(0) * yDim + (y)];
                } else {
                    valSum += inGrid[(x - 1) * yDim + (y)];
                }

                if (InDim(xDim, x - 1)) {
                    valSum += inGrid[(x - 1) * yDim + (y)];
                } else if (boundaryCond) {
                    valSum += boundaryValue;
                } else if (wrapX) {
                    valSum += inGrid[(xDim - 1) * yDim + (y)];
                } else {
                    valSum += inGrid[(x + 1) * yDim + (y)];
                }

                if (InDim(yDim, y + 1)) {
                    valSum += inGrid[(x) * yDim + (y + 1)];
                } else if (boundaryCond) {
                    valSum += boundaryValue;
                } else if (wrapY) {
                    valSum += inGrid[(x) * yDim + (0)];
                } else {
                    valSum += inGrid[(x) * yDim + (y - 1)];
                }

                if (InDim(yDim, y - 1)) {
                    valSum += inGrid[(x) * yDim + (y - 1)];
                } else if (boundaryCond) {
                    valSum += boundaryValue;
                } else if (wrapY) {
                    valSum += inGrid[(x) * yDim + (yDim - 1)];
                } else {
                    valSum += inGrid[(x) * yDim + (y + 1)];
                }
                int i = x * yDim + y;
                outGrid[i] = inGrid[i] + diffRate * (-inGrid[i] * 4 + valSum);
            }
        }
    }
    //boolean in3(int compX,int compY,int zDim,int x,int y,int z){
    //    return x>=0&&x<compX&&y>=0&&y<compY&&z>=0&&z<zDim;
    //}

    /**
     * returns whether the input value is between 0 and the dimension value
     */
    static boolean InDim(int Dim, int Val){
        return Val>=0&&Val<Dim;
    }


    /**
     * runs the diffusion equation in 3 dimensions
     * @param inGrid an array of values holding the starting state of the diffusible
     * @param outGrid an array into which the result of diffusion will be written
     * @param xDim x dimenison of the inGrid and outGrid
     * @param yDim y dimension of the inGrid and outGrid
     * @param zDim z dimension of the inGrid and outGrid
     * @param diffRate diffusion rate for the diffusion equation
     * @param boundaryCond defines whether a constant boundary condition value should diffuse in from the boundaries
     * @param boundaryValue only impacts diffusion if boundaryCond is true, sets the boundary condition value
     * @param wrapX whether to wrap around diffusion over the left and right and front and back boundaries
     */
    public static void Diffusion3(final double[] inGrid, final double[] outGrid, final int xDim, final int yDim, final int zDim, final double
            diffRate, final boolean boundaryCond, final double boundaryValue, final boolean wrapX,final boolean wrapY,final boolean wrapZ) {
        int x, y, z;
        double valSum;
        for (x = 0; x < xDim; x++) {
            for (y = 0; y < yDim; y++) {
                for (z = 0; z < zDim; z++) {
                    //6 squares to check
                    valSum = 0;
                    if (InDim(xDim, x + 1)) {
                        valSum += inGrid[(x + 1) * yDim * zDim + (y) * zDim + (z)];
                    }  else if (boundaryCond) {
                        valSum += boundaryValue;
                    }else if (wrapX) {
                        valSum += inGrid[(0) * yDim * zDim + (y) * zDim + (z)];
                    } else{
                        valSum+=inGrid[(x-1)*yDim*zDim+(y)*zDim+(z)];
                    }

                    if (InDim(xDim, x - 1)) {
                        valSum += inGrid[(x - 1) * yDim * zDim + (y) * zDim + (z)];
                    }  else if (boundaryCond) {
                        valSum += boundaryValue;
                    }else if (wrapX) {
                        valSum += inGrid[(xDim - 1) * yDim * zDim + (y) * zDim + (z)];
                    } else{
                        valSum += inGrid[(x + 1) * yDim * zDim + (y) * zDim + (z)];
                    }

                    if (InDim(yDim, y + 1)) {
                        valSum += inGrid[(x) * yDim * zDim + (y + 1) * zDim + (z)];
                    } else if (boundaryCond) {
                        valSum += boundaryValue;
                    } else if(wrapY){
                        valSum += inGrid[(x) * yDim * zDim + (0) * zDim + (z)];
                    } else{
                        valSum += inGrid[(x) * yDim * zDim + (y-1) * zDim + (z)];
                    }

                    if (InDim(yDim, y - 1)) {
                        valSum += inGrid[(x) * yDim * zDim + (y - 1) * zDim + (z)];
                    } else if (boundaryCond) {
                        valSum += boundaryValue;
                    } else if(wrapY){
                        valSum += inGrid[(x) * yDim * zDim + (yDim-1) * zDim + (z)];
                    } else{
                        valSum += inGrid[(x) * yDim * zDim + (y+1) * zDim + (z)];
                    }

                    if (InDim(zDim, z + 1)) {
                        valSum += inGrid[(x) * yDim * zDim + (y) * zDim + (z+1)];
                    } else if (boundaryCond) {
                        valSum += boundaryValue;
                    } else if(wrapZ){
                        valSum += inGrid[(x) * yDim * zDim + (y) * zDim + (0)];
                    } else{
                        valSum += inGrid[(x) * yDim * zDim + (y) * zDim + (z-1)];
                    }

                    if (InDim(zDim, z - 1)) {
                        valSum += inGrid[(x) * yDim * zDim + (y) * zDim + (z-1)];
                    } else if (boundaryCond) {
                        valSum += boundaryValue;
                    } else if(wrapZ){
                        valSum += inGrid[(x) * yDim * zDim + (y) * zDim + (zDim-1)];
                    } else{
                        valSum += inGrid[(x) * yDim * zDim + (y) * zDim + (z+1)];
                    }
                    int i = x * yDim * zDim + y * zDim + z;
                    outGrid[i] = inGrid[i] + diffRate * (-inGrid[i] * 6 + valSum);
                }
            }
        }
    }

    /**
     * prints information about the memory usage and max memory allocated for the program
     */
    public static void PrintMemoryUsage(){
        int mb=1024*1024;
        Runtime rt=Runtime.getRuntime();
        System.out.println("Used Memory: "+(rt.totalMemory()-rt.freeMemory())/mb+" mb");
        System.out.println("Free Momory: "+rt.freeMemory()/mb+" mb");
        System.out.println("Total Memory: "+rt.totalMemory()/mb+" mb");
        System.out.println("Max  Memory: "+rt.maxMemory()/mb+" mb");
    }

    /**
     * prints an array
     * @param arr array to be printed
     * @param delim the delimiter used to separate entries
     * @param <T> the type of the data entries in the array
     */
    public static <T>String PrintArr(T[] arr,String delim){
        StringBuilder sb=new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]+delim);
        }
        return sb.toString();
    }
    /**
     * prints an array
     * @param arr array to be printed
     * @param delim the delimiter used to separate entries
     */
    public static String PrintArr(double[] arr,String delim){
        StringBuilder sb=new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]+delim);
        }
        return sb.toString();
    }
    /**
     * prints an array
     * @param arr array to be printed
     * @param delim the delimiter used to separate entries
     */
    public static String PrintArr(int[] arr,String delim){
        StringBuilder sb=new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]+delim);
        }
        return sb.toString();
    }

    /**
     * Converts HSV values to RGB values
     * hue: 0->1 (Picks Color)
     * saturation: 0->1
     * brightness: 0->1
     **/
    public static float[] GetHSBtoRGB(float hue, float saturation, float brightness) {
        float scale = 255f;
        int rgb = Color.HSBtoRGB(hue, saturation, brightness);
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        float[] RGBreturn = {r/scale,g/scale,b/scale};
        return RGBreturn;
    }

}