package AgentFramework;

/**
 * Created by rafael on 2/17/17.
 */
class AgentBase <T extends GridBase>{
    int birthTick;
    int iList;
    boolean alive;
    T myGrid;

    /**
     * Returns the grid that the agent lives in
     */
    public T G(){
        return myGrid;
    }

    /**
     * Returns how many grid ticks the agent has been alive for
     */
    public int Age(){
        return myGrid.tick-birthTick;
    }


    /**
     * Returns the birth tick of the cell
     */
    public int BirthTick() { return birthTick;}

    /**
     * Returns whether the agent is alive or has been disposed
     */
    public boolean Alive(){
        return alive;
    }
}
