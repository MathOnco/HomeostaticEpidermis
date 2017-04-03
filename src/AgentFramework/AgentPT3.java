package AgentFramework;

/**
 * extend the AgentPT3 class if you want agents that exist on a 3D continuous lattice
 * with the possibility of stacking multiple agents on the same grid square
 * @param <T> the extended Grid3 class that the agents will live in
 * Created by rafael on 11/18/16.
 */
public abstract class AgentPT3<T extends Grid3> extends AgentSQ3<T> {
    private double xPos;
    private double yPos;
    private double zPos;
    void Setup(double xPos,double yPos,double zPos){
        this.xPos=xPos;
        this.yPos=yPos;
        this.zPos=zPos;
        myGrid.AddAgentToSquare(this,myGrid.PTtoI(xPos,yPos,zPos));
    }
    void Setup(int xPos,int yPos,int zPos){
        this.xPos=xPos+0.5;
        this.yPos=yPos+0.5;
        this.zPos=zPos+0.5;
        myGrid.AddAgentToSquare(this,myGrid.SQtoI(xPos,yPos,zPos));
    }

    /**
     * Moves the agent to the specified coordinates
     */
    public void Move(int newX, int newY,int newZ){
        int oldX=(int)xPos;
        int oldY=(int)yPos;
        int oldZ=(int)zPos;
        if(oldX!=newX||oldY!=newY||oldZ!=newZ) {
            myGrid.RemAgentFromSquare(this, myGrid.SQtoI(oldX,oldY,oldZ));
            myGrid.AddAgentToSquare(this, myGrid.SQtoI(newX, newY,newZ));
        }
        this.xPos=newX+0.5;
        this.yPos=newY+0.5;
        this.zPos=newZ+0.5;
    }

    /**
     * Moves the agent to the specified coordinates
     */
    public void Move(double newX, double newY,double newZ){
        int xIntNew=(int)newX;
        int yIntNew=(int)newY;
        int zIntNew=(int)newZ;
        int xIntOld=(int)xPos;
        int yIntOld=(int)yPos;
        int zIntOld=(int)zPos;
        if(xIntNew!=xIntOld||yIntNew!=yIntOld||zIntNew!=zIntOld) {
            myGrid.RemAgentFromSquare(this, myGrid.SQtoI(xIntOld,yIntOld,zIntOld));
            myGrid.AddAgentToSquare(this, myGrid.SQtoI(xIntNew,yIntNew,zIntNew));
        }
        xPos=newX;
        yPos=newY;
    }


    /**
     * gets the x coordinate of the agent
     */
    public double Xpt(){
        return xPos;
    }

    /**
     * gets the y coordinate of the agent
     */
    public double Ypt(){
        return yPos;
    }

    /**
     * gets the z coordinate of the agent
     */
    public double Zpt(){
        return zPos;
    }

    /**
     * gets the x coordinate of the square that the agent occupies
     */
    public int Xsq(){
        return (int)xPos;
    }

    /**
     * gets the y coordinate of the square that the agent occupies
     */
    public int Ysq(){
        return (int)yPos;
    }

    /**
     * gets the z coordinate of the square that the agent occupies
     */
    public int Zsq(){ return (int)zPos; }

    /**
     * Gets the index of the square that the agent occupies
     */
    public int Isq(){return myGrid.SQtoI((int)xPos,(int)yPos,(int)zPos);}
}
