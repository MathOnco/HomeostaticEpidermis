package AgentFramework;

abstract class GridBase {
    int tick;
    public GridBase(){
    }

    /**
     * gets the current grid tick
     */
    public int Tick(){
        return tick;
    }

    /**
     * increments the current grid tick
     */
    public void IncTick(){
        tick+=1;
    }

    /**
     * sets the current tick to value
     */
    public void SetTick(int val){
        tick=val;

    }
}

