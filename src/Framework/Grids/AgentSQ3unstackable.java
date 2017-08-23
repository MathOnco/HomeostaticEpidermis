package Framework.Grids;

import java.util.ArrayList;

/**
 * extend the AgentSQ3 class if you want agents that exist on a 3D discrete lattice
 * with the possibility of stacking multiple agents on the same grid square
 * @param <T> the extended Grid3 class that the agents will live in
 * Created by rafael on 11/18/16.
 */
public class AgentSQ3unstackable<T extends Grid3> extends AgentBaseSpatial<T>{
    int xSq;
    int ySq;
    int zSq;

    void Setup(int xSq,int ySq,int zSq){
        this.xSq=xSq;
        this.ySq=ySq;
        this.zSq=zSq;
        this.iSq=myGrid.I(xSq,ySq,zSq);
        AddSQ(iSq);
    }
    void Setup(double xPos,double yPos,double zPos){
        Setup((int)xPos,(int)yPos,(int)zPos);
    }

    @Override
    void Setup(int i) {
        this.iSq=i;
        this.xSq=myGrid.ItoX(i);
        this.ySq=myGrid.ItoY(i);
        this.zSq=myGrid.ItoZ(i);
        AddSQ(iSq);
    }

    @Override
    void Setup(int x, int y) {
        throw new IllegalStateException("shouldn't be adding 3D agent to 2D grid");
    }

    public void SwapPosition(AgentBaseSpatial other){
        if(!alive||!other.alive){
            throw new RuntimeException("attempting to move dead agent");
        }
        if(other.myGrid!=myGrid){
            throw new IllegalStateException("can't swap positions between agents on different grids!");
        }
        int iNew=other.Isq();
        int iNewOther=Isq();
        other.RemSQ();
        this.RemSQ();
        other.MoveI(iNewOther);
        this.MoveI(iNew);
    }

    /**
     * Moves the agent to the specified coordinates
     */
    public void MoveSQ(int x, int y, int z){
        //moves agent discretely
        if(!alive){
            throw new RuntimeException("attempting to move dead agent");
        }
        int iNewPos=myGrid.I(x,y,z);
        RemSQ();
        this.xSq=x;
        this.ySq=y;
        this.zSq=z;
        this.iSq=iNewPos;
        AddSQ(iNewPos);
    }
    void AddSQ(int i){
        if(myGrid.grid[iSq]!=null){
            throw new RuntimeException("Adding multiple unstackable agents to the same square!");
        }
        myGrid.grid[iSq]=this;
    }
    void RemSQ(){
        myGrid.grid[iSq]=null;
    }

    /**
     * gets the xDim coordinate of the square that the agent occupies
     */
    public int Xsq(){
        return xSq;
    }

    /**
     * gets the yDim coordinate of the square that the agent occupies
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
     * gets the xDim coordinate of the agent
     */
    public double Xpt(){
        return xSq+0.5;
    }

    /**
     * gets the yDim coordinate of the agent
     */
    public double Ypt(){
        return ySq+0.5;
    }

    /**
     * gets the z coordinate of the agent
     */
    public double Zpt(){ return zSq+0.5;}
    /**
     * deletes the agent
     */
    public void Dispose(){
        //kills agent
        if(!alive){
            throw new RuntimeException("attempting to dispose already dead agent");
        }
        RemSQ();
        myGrid.agents.RemoveAgent(this);
    }

    @Override
    void GetAllOnSquare(ArrayList<AgentBaseSpatial> putHere) {
        putHere.add(this);
    }

    @Override
    public void MoveI(int iNext) {
        RemSQ();
        xSq=myGrid.ItoX(iNext);
        ySq=myGrid.ItoY(iNext);
        zSq=myGrid.ItoZ(iNext);
        iSq=iNext;
        AddSQ(iNext);
    }

    @Override
    void Setup(double i) {

    }

    @Override
    void Setup(double x, double y) {

    }
}
