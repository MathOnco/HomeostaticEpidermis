package AgentFramework;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Created by rafael on 2/17/17.
 */
class AgentList <T extends AgentBase> implements Iterable<T>{
    ArrayList<T> agents;
    ArrayList<T> deads;
    Constructor<?> builder;
    int iLastAlive;
    int pop;
    final GridBase myGrid;

    AgentList(Class<T> type, GridBase myGrid){
        this.builder=type.getDeclaredConstructors()[0];
        this.builder.setAccessible(true);
        this.agents=new ArrayList<>();
        this.deads=new ArrayList<>();
        this.iLastAlive=-1;
        this.pop=0;
        this.myGrid=myGrid;
    }
    T GetNewAgent(){
    T newAgent;
    //internal function, inserts agent into AgentGridMin.AgentGrid2_5
    if(deads.size()>0){
        newAgent=deads.remove(deads.size()-1);
    }
    else if(agents.size()>iLastAlive+1){
        iLastAlive++;
        newAgent=agents.get(iLastAlive);
    }
    else {
        try {
            newAgent = (T)builder.newInstance();
        }
        catch (Exception e){
            throw new RuntimeException("Could not instantiate");
        }
        agents.add(newAgent);
        newAgent.myGrid=this.myGrid;
        iLastAlive++;
        newAgent.iList=iLastAlive;
        //agent.iList= iLastAlive;
    }
    newAgent.alive=true;
    newAgent.birthTick=this.myGrid.tick;
    newAgent.myGrid=myGrid;
    pop++;
    return newAgent;
    }
    void RemoveAgent(T agent) {
        agent.alive = false;
        deads.add(agent);
        pop--;
    }
    List<T> GetAllAgents(){
        return Collections.unmodifiableList(this.agents);//will contain dead agents and newly born agents
    }

    @Override
    public Iterator<T> iterator() {
        return new myIter(this);
    }
    private class myIter implements Iterator<T>{
        AgentList<T> myList;
        int iAgent;
        T ret;

        T NextAgent(){
            //use within a while loop that exits when the returned agent is null to iterate over all agents (advances Age of agents)
            while(iAgent<=iLastAlive) {
                T possibleRet=agents.get(iAgent);
                iAgent += 1;
                if (possibleRet != null && possibleRet.alive && possibleRet.birthTick != myList.myGrid.tick) {
                    return possibleRet;
                }
            }
            return null;
        }
        myIter(AgentList<T> myList){
            this.myList=myList;
            this.iAgent=0;
            this.ret=null;
        }
        @Override
        public boolean hasNext() {
            while(iAgent<=iLastAlive) {
                T possibleRet=agents.get(iAgent);
                iAgent += 1;
                if (possibleRet != null && possibleRet.alive && possibleRet.birthTick != myList.myGrid.tick) {
                    ret=possibleRet;
                    return true;
                }
            }
            ret=null;
            return false;
        }

        @Override
        public T next() {
            return ret;
        }
    }
    public void ShuffleAgents(Random rn){
        //shuffles the AgentGridMin.AgentGrid2_5 agents list (Don't run during agent iteration)
        for(int iSwap1 = iLastAlive; iSwap1>0; iSwap1--){
            int iSwap2=rn.nextInt(iSwap1+1);
            T swap1=agents.get(iSwap1);
            T swap2=agents.get(iSwap2);
            swap1.iList = iSwap2;
            swap2.iList = iSwap1;
            agents.set(iSwap2,swap1);
            agents.set(iSwap1,swap2);
        }
    }
    public void CleanAgents(){
        //cleans the grid by removing dead agents (Don't run during agent iteration)
        //may need to double check implementation!!
        int iNext=0;
        while(iNext<= iLastAlive){
            T nextDead=agents.get(iNext);
            if(!nextDead.alive){
                while(iLastAlive >=iNext) {
                    T subAgent=agents.get(iLastAlive);
                    if(subAgent.alive){
                        subAgent.iList=iNext;
                        nextDead.iList=iLastAlive;
                        agents.set(iNext,subAgent);
                        agents.set(iLastAlive,nextDead);
                        iLastAlive--;
                        iNext++;
                        break;
                    }
                    iLastAlive--;
                }
            }
            else{
                iNext++;
            }
        }
        //if(iLastAlive +1!=agents.size()) {
        //    agents.subList(iLastAlive + 1, agents.size()).clear();
        //}
        deads.clear();
    }
}
