package AgentFramework;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Extend the Grid2 class if you want a 2D lattice with one or more agents per grid square
 * @param <T> the AgentSQ2_5 or AgentPT2_5 extending agent class that will inhabit the grid
 */
abstract public class Grid2_5<T extends AgentSQ2_5> extends GridBase implements Iterable<T>{
    AgentList<T> agents;
    public final int xDim;
    public final int yDim;
    public final int length;
    T[] grid;
    int[] counts;

    /**
     * @param type pass T.class, used to instantiate agent instances within the grid as needed
     */
    public Grid2_5(int x, int y, Class<T> type){
        //creates a new grid with given dimensions
        xDim=x;
        yDim=y;
        length=x*y;
        agents=new AgentList<T>(type,this);
        grid=(T[])new AgentSQ2_5[length];
        counts= new int[length];
    }

    /**
     * gets the index of the square at the specified coordinates
     */
    public int SQtoI(int x, int y){
        //gets grid index from location
                return x*yDim+y;
    }

    /**
     * gets the x component of the square at the specified index
     */
    public int ItoX(int i){ return i/yDim; }

    /**
     * gets the y component of the square at the specified index
     */
    public int ItoY(int i){ return i%yDim; }

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
        if(x>=0&&x<xDim&&y>=0&&y<yDim){
            return true;
        }
        return false;
    }

    /**
     * returns whether the specified coordinates are inside the grid bounds
     */
    public boolean In(double x, double y){
        int xInt=(int)Math.floor(x);
        int yInt=(int)Math.floor(y);
        return In(xInt,yInt);
    }
    void AddAgentToSquare(T agent,int iGrid){
        //internal function, adds agent to grid square
        if(grid[iGrid]==null) {
            grid[iGrid]=agent;
        }
        else{
            grid[iGrid].prevSq=agent;
            agent.nextSq=grid[iGrid];
            grid[iGrid]=agent;
        }
        counts[iGrid]++;
    }

    /**
     * gets the index of the square at the specified coordinates with wrap around
     */
    public int SQwrapI(int x, int y){
        //wraps Coords to proper index
        if(In(x,y)) { return SQtoI(x,y);}
        return SQtoI(Utils.ModWrap(x,xDim),Utils.ModWrap(y,yDim));
    }

    void RemAgentFromSquare(T agent,int iGrid){
        //internal function, removes agent from grid square
        if(grid[iGrid]==agent){
            grid[iGrid]=(T)agent.nextSq;
        }
        if(agent.nextSq!=null){
            agent.nextSq.prevSq=agent.prevSq;
        }
        if(agent.prevSq!=null){
            agent.prevSq.nextSq=agent.nextSq;
        }
        agent.prevSq=null;
        agent.nextSq=null;
        counts[iGrid]--;
    }
    T GetNewAgent(){
        return agents.GetNewAgent();
    }

    /**
     * returns an uninitialized agent at the specified coordinates
     */
    public T NewAgent(int x, int y){
        T newAgent=GetNewAgent();
        newAgent.Setup(x,y);
        return newAgent;
    }

    /**
     * returns an uninitialized agent at the specified coordinates
     */
    public T NewAgent(double x, double y){
        T newAgent=GetNewAgent();
        newAgent.Setup(x,y);
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
    public Iterator<T> iterator(){
        return agents.iterator();
    }
    public int PopAtSQ(int x, int y){
        //gets population count at location
        return counts[SQtoI(x,y)];
    }
    public int PopAtI(int i){
        //gets population count at location
        return counts[i];
    }
    public ArrayList<T> AllAgents(){return (ArrayList<T>)this.agents.GetAllAgents();}

    /**
     * writes into ret the coordinates of SQs with the centerX and centerY coordinates added
     * returns the number of squares that were written into ret. any coordinates that fall out of bounds are not written
     * @param SQs list of coordinates of the form [x,y,x,y,...]
     * @param ret list into which the displaced coordinates will be written
     * @param centerX x displacement of coordinates
     * @param centerY y displacement of coordinates
     * @param wrap whether to wrap the coordinates that fall out of bounds
     * @return the number of coordinates written into the ret array
     */
    public int SQsToLocalIs(int[] SQs,int[] ret,int centerX,int centerY,boolean wrap){
        //moves coordinates to be around origin
        //if any of the coordinates are outside the bounds, they will not be added
        int ptCt=0;
        for(int i=0;i<SQs.length/2;i++){
            int x=SQs[i*2]+centerX;
            int y=SQs[i*2+1]+centerY;
            if(In(x,y)){
                ret[ptCt]= SQtoI(x,y);
                ptCt++;
            }
            else if(wrap){
                ret[ptCt]= SQwrapI(x,y);
                ptCt++;
            }
        }
        return ptCt;
    }

    /**
     * appends to the provided arraylist all agents on the square at the specified coordinates
     * @param putHere the arraylist ot be added to
     */
    public void SQtoAgents(ArrayList<T>putHere, int x, int y){
        T agent= grid[SQtoI(x,y)];
        while(agent!=null){
            putHere.add(agent);
            agent=(T)agent.nextSq;
        }
    }

    /**
     * calls dispose on all agents in the grid
     */
    public void ClearAgents(){
        ArrayList<T> allAgents=AllAgents();
        allAgents.stream().filter(curr->curr.alive).forEach(AgentSQ2_5::Dispose);
    }

    /**
     * returns the number of agents that are alive in the grid
     */
    public int Pop(){
        //gets population
        return agents.pop;
    }
}