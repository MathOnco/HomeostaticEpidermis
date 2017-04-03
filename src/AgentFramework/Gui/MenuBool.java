package AgentFramework.Gui;

import AgentFramework.Misc.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * a menu item that can be toggled to true or false
 */
public class MenuBool extends JToggleButton implements AgentFramework.Misc.MenuItem,GuiComp {
    int compX;
    int compY;
    public MenuBool(MenuSet mySet, String text, boolean initVal){
        super(text);
        this.compX=1;
        this.compY=1;
        mySet.AddGuiMenuItem(this,Boolean.toString(initVal));
    }
    public MenuBool(MenuSet mySet, String text, boolean initVal, int compX, int compY){
        super(text);
        this.compX=compX;
        this.compY=compY;
        mySet.AddGuiMenuItem(this,Boolean.toString(initVal));
    }

    /**
     * ignore
     */
    @Override
    public int TypeID() { return 0; }

    /**
     * sets the boolean value, true, false, t or f are all valid inputs
     */
    @Override
    public void Set(String val) {
        val=val.toLowerCase();
        boolean setVal;
        if(val.equals("true")||val.equals("t")){
            setVal=true;
        }else if(val.equals("false")||val.equals("f")){
            setVal=false;
        }
        else{
            throw new IllegalArgumentException(val+" cannot be interpreted as true or false");
        }
        this.setSelected(setVal);
    }

    /**
     * returns the current boolean value as a string
     */
    @Override
    public String Get() { return isSelected()?"true":"false"; }

    /**
     * returns teh label associated with the boolean
     */
    @Override
    public String GetLabel() {
        return this.getText();
    }

    /**
     * ignore
     */
    @Override
    public int NEntries() {
        return 1;
    }

    /**
     * ignore
     */
    @Override
    public <T extends Component> T GetEntry(int iEntry) {
        switch(iEntry){
            case 0: return (T)this;
            default: throw new IllegalArgumentException(iEntry+" does not match to an item!");
        }
    }

    /**
     * ignore
     */
    @Override
    public int compX(){ return compX; }

    /**
     * ignore
     */
    @Override
    public int compY(){ return compY; }

    /**
     * ignore
     */
    @Override
    public void GetComps(ArrayList<Component> putHere,ArrayList<Integer> coordsHere,ArrayList<Integer> sizesHere){
        putHere.add(this);
        coordsHere.add(0);
        coordsHere.add(0);
        sizesHere.add(compX);
        sizesHere.add(compY);
    }
}
