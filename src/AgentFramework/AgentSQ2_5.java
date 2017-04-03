package AgentFramework;

/**
 * extend the AgentSQ2_5 class if you want agents that exist on a 2D discrete lattice
 * with the possibility of stacking multiple agents on the same grid square
 * @param <T> the extended Grid2_5 class that the agents will live in
 * Created by rafael on 11/18/16.
 */
abstract public class AgentSQ2_5<T extends Grid2_5> extends AgentBase<T>{
    int xSq;
    int ySq;
    AgentSQ2_5 nextSq;
    AgentSQ2_5 prevSq;
    void Setup(int xSq,int ySq){
        this.xSq=xSq;
        this.ySq=ySq;
        myGrid.AddAgentToSquare(this,myGrid.SQtoI(xSq,ySq));
    }
    void Setup(double xPos,double yPos){
        Setup((int)xPos,(int)yPos);
    }

    /**
     * Moves the agent to the specified square
     */
    public void Move(int x, int y){
        //moves agent discretely
        int iPrevPos=myGrid.SQtoI(xSq,ySq);
        int iNewPos=myGrid.SQtoI(x,y);
        myGrid.RemAgentFromSquare(this,iPrevPos);
        myGrid.AddAgentToSquare(this,iNewPos);
        this.xSq=x;
        this.ySq=y;
    }

    /**
     * Moves the agent to the specified square
     */
    public void Move(double x, double y){
       Move((int)x,(int)y);
    }

    /**
     * gets the x coordinate of the square that the agent occupies
     */
    public int Xsq(){
        return xSq;
    }

    /**
     * gets the y coordinate of the square that the agent occupies
     */
    public int Ysq(){
        return ySq;
    }

    /**
     * gets the x coordinate of the agent
     */
    public double Xpt(){
        return xSq+0.5;
    }

    /**
     * gets the y coordinate of the agent
     */
    public double Ypt(){
        return ySq+0.5;
    }

    /**
     * gets the index of the square that the agent occupies
     */
    public int Isq(){return myGrid.SQtoI(xSq,ySq);}

    /**
     * deletes the agent
     */
    public void Dispose(){
        //kills agent
        myGrid.RemoveAgent(this,myGrid.SQtoI(xSq,ySq));
    }
    //addCoords
}
