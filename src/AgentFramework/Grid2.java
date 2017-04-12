package AgentFramework;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Extend the Grid2 class if you want a 2D lattice with at most one agent per grid square
 * @param <T> the AgentSQ2 extending agent class that will inhabit the grid
 */
public class Grid2<T extends AgentSQ2> extends GridBase implements Iterable<T>{
    AgentList<T> agents;
    public final int length;
    public final int xDim;
    public final int yDim;
    public T[] grid;

    /**
     * @param type pass T.class, used to instantiate agent instances within the grid as needed
     */
    public Grid2(int xDim, int yDim, Class<T> type){
        this.xDim=xDim;
        this.yDim=yDim;
        length=xDim*yDim;
        agents=new AgentList<T>(type,this);
        grid=(T[])new AgentSQ2[length];
    }

    /**
     * gets the index of the square at the specified coordinates
     */
    public int SQtoI(int x, int y){
        //gets grid index from location
        return x*yDim+y;
    }

    /**
     * gets the index of the square at the specified coordinates with wrap around
     */
    public int SQwrapI(int x, int y){
        //wraps Coords to proper index
        if(In(x,y)) { return SQtoI(x,y);}
        return SQtoI(Utils.ModWrap(x,xDim),Utils.ModWrap(y,yDim));
    }

    /**
     * gets the x component of the square at the specified index
     */
    public int ItoX(int i){
        return i/yDim;
    }

    /**
     * gets the y component of the square at the specified index
     */
    public int ItoY(int i){
        return i%yDim;
    }

    /**
     * gets the index of the square that contains the specified coordinates
     */
    public int PTtoI(double x, double y){
        //gets grid index from location
        return (int)Math.floor(x)*yDim+(int)Math.floor(y);
    }

    /**
     * returns whether the specified coordinates are inside the grid bounds
     */
    public boolean In(int x, int y){
        return x >= 0 && x < xDim && y >= 0 && y < yDim;
    }

    /**
     * returns whether the specified coordinates are inside the grid bounds
     */
    public boolean In(double x, double y){
        int xInt=(int)Math.floor(x);
        int yInt=(int)Math.floor(y);
        return In(xInt,yInt);
    }
    void RemAgentFromSquare(T agent,int iGrid){
        grid[iGrid]=null;
    }
    void AddAgentToSquare(T agent,int iGrid){
        if(grid[iGrid]!=null){
            throw new RuntimeException("Adding multiple agents on the same square!");
        }
        grid[iGrid]=agent;
    }

    /**
     * returns an uninitialized agent at the specified coordinates
     */
    public T NewAgent(int x, int y){
        T newAgent=agents.GetNewAgent();
        newAgent.Setup(x,y);
        return newAgent;

    }

    /**
     * returns an uninitialized agent at the specified index
     */
    public T NewAgent(int i){
        T newAgent=agents.GetNewAgent();
        newAgent.Setup(ItoX(i),ItoY(i));
        return newAgent;
    }
    void RemoveAgent(T agent,int iGrid){
        //internal function, removes agent from world
        RemAgentFromSquare(agent, iGrid);
        agents.RemoveAgent(agent);
    }

    /**
     * shuffles the agent list to randomize iteration
     * do not call this while in the middle of iteration
     * @param rn the Random number generator to be used
     */
    public void ShuffleAgents(Random rn){
        agents.ShuffleAgents(rn);
    }

    /**
     * cleans the list of agents, removing dead ones, may improve the efficiency of the agent iteration if many agents have died
     * do not call this while in the middle of iteration
     */
    public void CleanAgents(){
        agents.CleanAgents();
    }

    /**
     * calls CleanAgents, then SuffleAgents, then IncTick. useful to call at the end of a round of iteration
     * do not call this while in the middle of iteration
     * @param rn the Random number generator to be used
     */
    public void CleanShuffInc(Random rn){
        CleanAgents();
        ShuffleAgents(rn);
        IncTick();
    }

    /**
     * Gets the agent at the specified coordinates
     * returns null if no agent exists
     */
    public T SQtoAgent(int x, int y){ return grid[SQtoI(x,y)]; }

    /**
     * Gets the agent at the specified index
     * returns null if no agent exists
     */
    public T ItoAgent(int i){ return grid[i]; }

    /**
     * returns an umodifiable copy of the complete agentlist, including dead and just born agents
     */
    public ArrayList<T> AllAgents(){return (ArrayList<T>)this.agents.GetAllAgents();}

    /**
     * calls dispose on all agents in the grid
     */
    public void ClearAgents(){
        List<T> AllAgents=this.agents.GetAllAgents();
        AllAgents.stream().filter(curr -> curr.alive).forEach(AgentSQ2::Dispose);
    }

    @Override
    public Iterator<T> iterator(){
        return agents.iterator();
    }

    /**
     * writes into ret the coordinates of SQs with the centerX and centerY coordinates added
     * returns the number of squares that were written into ret. any coordinates that fall out of bounds are not written
     * @param SQs list of coordinates of the form [x,y,x,y,...]
     * @param ret list into which the displaced coordinates will be written
     * @param centerX x displacement of coordinates
     * @param centerY y displacement of coordinates
     * @param wrapX whether to wrap the coordinates that fall out of bounds in the X direction
     * @param wrapY whether to wrap the coordinates that fall out of bounds in the Y direction
     * @return the number of coordinates written into the ret array
     */
    public int SQsToLocalIs(int[] SQs, int[] ret, int centerX, int centerY, boolean wrapX, boolean wrapY){
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt=0;
        for(int i=0;i<SQs.length/2;i++) {
            int x = SQs[i * 2] + centerX;
            int y = SQs[i * 2 + 1] + centerY;
            if (!Utils.InDim(xDim, x)) {
                if (wrapX) {
                    x = Utils.ModWrap(x, xDim);
                } else {
                    continue;
                }
            }
            if (!Utils.InDim(yDim, y)) {
                if (wrapY) {
                    y = Utils.ModWrap(y, yDim);
                } else {
                    continue;
                }
            }
            ret[ptCt]=SQtoI(x,y);
            ptCt++;
        }
        return ptCt;
    }

    /**
     * returns the number of agents that are alive in the grid
     */
    public int Pop(){
        //gets population
        return agents.pop;
    }
}
