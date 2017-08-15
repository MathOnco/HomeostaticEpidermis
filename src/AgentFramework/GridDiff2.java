package AgentFramework;
import java.util.Arrays;

/**
 * GridDiff2 class facilitates 2D diffusion with two arrays of doubles called fields
 * the intended usage is that during a diffusion step, the current values will be read, and the next values will be written to
 * after updates, SwapNextCurr is called to set the next field as the current field.
 */
public class GridDiff2 extends GridBase {
    final public int xDim;
    final public int yDim;
    final public int length;
    double[] field;
    double[] swap;
    public GridDiff2(int xDim, int yDim){
        this.xDim =xDim;
        this.yDim =yDim;
        length=xDim*yDim;
        field=new double[this.xDim * this.yDim];
        swap=new double[this.xDim * this.yDim];
    }

    /**
     * gets the current field value at the specified index
     */
    public double IgetCurr(int i){return field[i];}

    /**
     * gets the current field value at the specified coordinates
     */
    public double SQgetCurr(int x, int y) { return field[x*yDim+y]; }

    /**
     * sets the current field value at the specified index
     */
    public void IsetCurr(int i,double val){field[i]=val;}

    /**
     * sets the current field value at the specified coordinates
     */
    public void SQsetCurr(int x, int y, double val){ field[x*yDim+y]=val; }

    /**
     * adds to the current field value at the specified coordinates
     */
    public void SQaddCurr(int x, int y, double val){ field[x*yDim+y]+=val; }

    /**
     * adds to the current field value at the specified index
     */
    public void IaddCurr(int i,double val){field[i]+=val;}

    /**
     * gets the next field value at the specified coordinates
     */
    public double SQgetNext(int x,int y){ return swap[x*yDim+y]; }

    /**
     * gets the next field value at the specified index
     */
    public double IgetNext(int i){return swap[i];}

    /**
     * sets the next field value at the specified coordinates
     */
    public void SQsetNext(int x,int y,double val){ swap[x*yDim+y]=val; }

    /**
     * sets the next field value at the specified index
     */
    public void IsetNext(int i,double val){swap[i]=val;}

    /**
     * sets the next field value at the specified coordinates
     */
    public void SQaddNext(int x,int y,double val){ swap[x*yDim+y]+=val; }

    /**
     * adds to the next field value at the specified index
     */
    public void IaddNext(int i,double val){swap[i]+=val;}

    /**
     * copies the current field into the next field
     */
    public void NextCopyCurr(){ System.arraycopy(field, 0, swap, 0, field.length); }

    /**
     * Bounds all values in the current field between min and max
     */
    public void BoundAllCurr(double min,double max){
        for(int i=0;i<length;i++){
            field[i]= AgentFramework.Utils.BoundVal(field[i],min,max);
        }
    }
    /**
     * Bounds all values in the next field between min and max
     */
    public void BoundAllNext(double min,double max){
        for(int i=0;i<length;i++){
            swap[i]= AgentFramework.Utils.BoundVal(swap[i],min,max);
        }
    }
    /**
     * Swaps the next and current field
     */
    public void SwapNextCurr(){
        double[]temp=field;
        field=swap;
        swap=temp;

    }

    /**
     * Runs diffusion on the current field, putting the result into the next field
     * @param diffRate rate of diffusion
     * @param boundaryCond whether a boundary condition value will diffuse in from the field boundaries
     * @param boundaryValue only applies when boundaryCond is true, the boundary condition value
     * @param wrapX whether to wrap the field over the left and right boundaries
     */
    public void Diffuse(double diffRate,boolean boundaryCond,double boundaryValue,boolean WrapX, boolean WrapY){
        Utils.Diffusion(field,swap,xDim,yDim,diffRate,boundaryCond,boundaryValue,WrapX,WrapY);
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