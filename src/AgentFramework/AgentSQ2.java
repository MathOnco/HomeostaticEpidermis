package AgentFramework;

/**
 * extend the AgentSQ2 class if you want agents that exist on a 2D discrete lattice
 * without the possibility of stacking multiple agents on the same grid square
 * @param <T> the extended Grid2 class that the agents will live in
 * Created by rafael on 11/18/16.
 */

public class AgentSQ2<T extends Grid2> extends AgentBase <T>{
    int xSq;
    int ySq;
    int iSq;
    void Setup(int i){
        xSq=myGrid.ItoX(i);
        ySq=myGrid.ItoX(i);
        iSq=i;
        myGrid.AddAgentToSquare(this,i);
    }
    void Setup(int xSq,int ySq){
        this.xSq=xSq;
        this.ySq=ySq;
        iSq=myGrid.SQtoI(xSq,ySq);
        myGrid.AddAgentToSquare(this,iSq);
    }

    /**
     * Moves the agent to the square with the specified index
     */
    public void Move(int i){
        //moves agent discretely
        this.xSq=myGrid.ItoX(i);
        this.ySq=myGrid.ItoY(i);
        myGrid.RemAgentFromSquare(this,iSq);
        myGrid.AddAgentToSquare(this,i);
        this.iSq=i;
    }

    /**
     * Moves the agent to the square at the specified coordinates
     */
    public void Move(int x, int y){
        int iNewPos=myGrid.SQtoI(x,y);
        myGrid.RemAgentFromSquare(this,iSq);
        myGrid.AddAgentToSquare(this,iNewPos);
        this.xSq=x;
        this.ySq=y;
        this.iSq=iNewPos;
    }

    /**
     * Gets the x coordinate of the square that the agent occupies
     */
    public int Xsq(){
        return xSq;
    }
    /**
     * Gets the y coordinate of the square that the agent occupies
     */
    public int Ysq(){
        return ySq;
    }
    /**
     * Gets the x coordinate agent
     */
    public double Xpt(){
        return xSq+0.5;
    }
    /**
     * Gets the y coordinate agent
     */
    public double Ypt(){
        return ySq+0.5;
    }
    /**
     * Gets the index of the square that the agent occupies
     */
    public int Isq(){return iSq;}

    /**
     * Deletes the agent
     */
    public void Dispose(){
        myGrid.RemoveAgent(this,myGrid.SQtoI(xSq,ySq));
    }
}
