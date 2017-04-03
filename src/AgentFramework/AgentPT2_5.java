package AgentFramework;

/**
 * extend the AgentPT2_5 class if you want agents that exist on a 2D continuous lattice
 * with the possibility of stacking multiple agents on the same grid square
 * @param <T> the extended Grid2_5 class that the agents will live in
 * Created by rafael on 11/18/16.
 */
abstract public class AgentPT2_5<T extends Grid2_5> extends AgentSQ2_5<T> {
    private double dX;
    private double dY;
    void Setup(double xPos,double yPos){
        this.dX =xPos;
        this.dY =yPos;
        myGrid.AddAgentToSquare(this,myGrid.PTtoI(xPos,yPos));
    }
    void Setup(int xPos,int yPos){
        this.dX =xPos+0.5;
        this.dY =yPos+0.5;
        myGrid.AddAgentToSquare(this,myGrid.PTtoI(xPos,yPos));
    }

    /**
     * Moves the agent to the specified coordinates
     */
    public void Move(int newX, int newY){
        int oldX=(int) dX;
        int oldY=(int) dY;
        if(oldX!=newX||oldY!=newY) {
            myGrid.RemAgentFromSquare(this, myGrid.SQtoI(oldX,oldY));
            myGrid.AddAgentToSquare(this, myGrid.SQtoI(newX, newY));
        }
        this.dX =newX+0.5;
        this.dY =newY+0.5;
    }

    /**
     * Moves the agent to the specified coordinates
     */
    public void Move(double newX, double newY){
        int xIntNew=(int)newX;
        int yIntNew=(int)newY;
        int xIntOld=(int) dX;
        int yIntOld=(int) dY;
        if(xIntNew!=xIntOld||yIntNew!=yIntOld) {
            myGrid.RemAgentFromSquare(this, myGrid.SQtoI(xIntOld,yIntOld));
            myGrid.AddAgentToSquare(this, myGrid.SQtoI(xIntNew,yIntNew));
        }
        dX =newX;
        dY =newY;
    }

    /**
     * gets the x coordinate of the agent
     */
    public double Xpt(){
        return dX;
    }

    /**
     * gets the y coordinate of the agent
     */
    public double Ypt(){
        return dY;
    }

    /**
     * gets the x coordinate of the square that the agent occupies
     */
    public int Xsq(){
        return (int) dX;
    }

    /**
     * gets the y coordinate of the square that the agent occupies
     */
    public int Ysq(){
        return (int) dY;
    }
}
