package AgentFramework;
import java.util.Arrays;

/**
 * GridDiff3 class facilitates 2D diffusion with two arrays of doubles called fields
 * the intended usage is that during a diffusion step, the current values will be read, and the next values will be written to
 * after updates, SwapNextCurr is called to set the next field as the current field.
 */
abstract public class GridDiff3 extends GridBase {
    final public int xDim;
    final public int yDim;
    final public int zDim;
    final public int length;
    public double[] field;
    public double[] swap;
    public GridDiff3(int xDim, int yDim, int zDim){
        this.xDim =xDim;
        this.yDim =yDim;
        this.zDim =zDim;
        length=xDim*yDim*zDim;
        field=new double[this.xDim * this.yDim * this.zDim];
        swap=new double[this.xDim * this.yDim * this.zDim];
    }

    /**
     * gets the current field value at the specified coordinates
     */
    public double SQgetCurr(int x,int y,int z) {
        return field[x*yDim*zDim+y*zDim+z];
    }

    /**
     * sets the current field value at the specified coordinates
     */
    public void SQsetCurr(int x,int y,int z,double val){
        field[x*yDim*zDim+y*zDim+z]=val;
    }

    /**
     * adds to the current field value at the specified coordinates
     */
    public void SQaddCurr(int x,int y,int z,double val){
        field[x*yDim*zDim+y*zDim+z]+=val;
    }

    /**
     * gets to the current field value at the specified coordinates
     */
    public double SQgetNext(int x,int y,int z){
        return swap[x*yDim*zDim+y*zDim+z];
    }

    /**
     * gets to the current field value at the specified coordinates
     */
    public void SQsetNext(int x,int y,int z,double val){
        swap[x*yDim*zDim+y*zDim+z]=val;
    }

    /**
     * adds to the next field value at the specified index
     */
    public void SQaddNext(int x,int y,int z,double val){
        swap[x*yDim*zDim+y*zDim+z]+=val;
    }

    /**
     * copies the current field into the next field
     */
    public void NextCopyCurr(){
        System.arraycopy(field,0,swap,0,field.length);
    }

    /**
     * swaps the next and current field
     */
    public void SwapNextCurr(){
        double[] temp=field;
        field=swap;
        temp=field;
    }

    /**
     * Runs diffusion on the current field, putting the results into the next field
     * @param diffRate rate of diffusion
     * @param boundaryCond whether a boundary condition value will diffuse in from the field boundaries
     * @param boundaryValue only applies when boundaryCond is true, the boundary condition value
     * @param WrapXZ whether to wrap the field over the left and right and top and bottom
     */
    public void Diffuse(double diffRate,boolean boundaryCond,double boundaryValue,boolean WrapXZ){
        Utils.Diffusion3(field,swap,xDim,yDim,zDim,diffRate,boundaryCond,boundaryValue,WrapXZ);
        double[] temp=field;
    }

    /**
     * returns the maximum difference between the current field and the next field
     * @param scaled divides the differences by the current field value (unstable at low concentrations)
     */
    public double MaxDiff(boolean scaled){
        double maxDiff=0;
        if(!scaled){
            for(int i=0;i<field.length;i++){
                maxDiff=Math.max(maxDiff,Math.abs(field[i]-swap[i]));
            }
        }else{
            for(int i=0;i<field.length;i++){
                maxDiff=Math.max(maxDiff,Math.abs((field[i]-swap[i])/field[i]));
            }
        }
        return maxDiff;
    }

    /**
     * sets all squares in current the field to the specified value
     */
    public void SetAllCurr(double val){
        Arrays.fill(field,val);
    }

    /**
     * sets all squares in the next field to the specified value
     */
    public void SetAllNext(double val){
        Arrays.fill(swap,val);
    }

    /**
     * gets the average value of all squares in the current field
     */
    public double AvgCurr(){
        double tot=0;
        for(int i=0;i<length;i++){
            tot+=field[i];
        }
        return tot/length;
    }

    /**
     * gets the average value of all squares in the next field
     */
    public double AvgNext(){
        double tot=0;
        for(int i=0;i<length;i++){
            tot+=swap[i];
        }
        return tot/length;
    }
}
