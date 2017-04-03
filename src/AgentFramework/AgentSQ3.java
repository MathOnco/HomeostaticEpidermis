package AgentFramework;

/**
 * extend the AgentSQ3 class if you want agents that exist on a 3D discrete lattice
 * with the possibility of stacking multiple agents on the same grid square
 * @param <T> the extended Grid3 class that the agents will live in
 * Created by rafael on 11/18/16.
 */
public abstract class AgentSQ3<T extends Grid3> extends AgentBase<T>{
    int xSq;
    int ySq;
    int zSq;
    AgentSQ3 nextSq;
    AgentSQ3 prevSq;
    void Setup(int xSq,int ySq,int zSq){
        this.xSq=xSq;
        this.ySq=ySq;
        this.zSq=zSq;
        myGrid.AddAgentToSquare(this,myGrid.SQtoI(xSq,ySq,zSq));
    }
    void Setup(double xPos,double yPos,double zPos){
        Setup((int)xPos,(int)yPos,(int)zPos);
    }

    /**
     * Moves the agent to the specified coordinates
     */
    public void Move(int x, int y, int z){
        //moves agent discretely
        int iPrevPos=myGrid.SQtoI(xSq,ySq,zSq);
        int iNewPos=myGrid.SQtoI(x,y,z);
        myGrid.RemAgentFromSquare(this,iPrevPos);
        myGrid.AddAgentToSquare(this,iNewPos);
        this.xSq=x;
        this.ySq=y;
        this.zSq=z;
    }

    /**
     * Moves the agent to the specified coordinates
     */
    public void Move(double x, double y, double z){
        Move((int)x,(int)y,(int)z);
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
     * gets the z coordinate of the square that the agent occupies
     */
    public int Zsq(){
        return zSq;
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
     * gets the z coordinate of the agent
     */
    public double Zpt(){ return zSq+0.5;}
    /**
     * gets the index of the square that the agent occupies
     */
    public int Isq(){return myGrid.SQtoI(xSq,ySq,zSq);}

    /**
     * deletes the agent
     */
    public void Dispose(){
        //kills agent
        myGrid.RemoveAgent(this,myGrid.SQtoI(xSq,ySq,zSq));
    }
    //addCoords
}
