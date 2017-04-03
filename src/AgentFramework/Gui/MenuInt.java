package AgentFramework.Gui;

import AgentFramework.Misc.*;
import static AgentFramework.Utils.*;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * a menu item that takes int input
 */
public class MenuInt extends JFormattedTextField implements AgentFramework.Misc.MenuItem,GuiComp {
    static final DecimalFormat intFormat=new DecimalFormat("#");
    String labelText;
    JLabel label;
    int min;
    int max;
    int compX;
    int compY;
    /**
     * @param mySet the MenuSet that can be queried to get the value of this menuInt
     * @param label the label of the menuInt
     * @param initVal the starting value of the menuInt
     */
    public MenuInt(MenuSet mySet, String label, int initVal, int min, int max){
        super(intFormat);
        this.compX=1;
        this.compY=2;
        this.setColumns(10);
        this.min=min;
        this.max=max;
        this.labelText=label;
        if(label.length()>0){
            this.label=new JLabel(labelText);
        }
        mySet.AddGuiMenuItem(this,Integer.toString(initVal));
    }

    /**
     * @param mySet the MenuSet that can be queried to get the value of this menuInt
     * @param label the label of the menuInt
     * @param initVal the starting value of the menuInt
     * @param nCols the number of digits that will fit on the display
     * @param compX the width on the gui GridBagLayout
     * @param compY the height on the gui GridBagLayout
     */
    public MenuInt(MenuSet mySet, String label, int initVal, int min, int max, int nCols, int compX, int compY){
        super(intFormat);
        this.compX=compX;
        this.compY=compY;
        this.setColumns(nCols);
        this.min=min;
        this.max=max;
        this.labelText=label;
        if(label.length()>0){
            this.label=new JLabel(labelText);
        }
        mySet.AddGuiMenuItem(this,Integer.toString(initVal));
    }

    /**
     * ignore
     */
    @Override
    public int TypeID() { return 1; }

    /**
     * ignore
     */
    @Override
    public void Set(String val) { this.setText(Integer.toString(BoundValI(Integer.parseInt(val),min,max))); }

    /**
     * ignore
     */
    @Override
    public String Get() {
        String val=Integer.toString(BoundValI(Integer.parseInt(this.getText()),min,max));
        this.Set(val);
        return val;
    }

    /**
     * ignore
     */
    @Override
    public String GetLabel() {
        return labelText;
    }

    /**
     * ignore
     */
    @Override
    public int NEntries() { return 2; }

    /**
     * ignore
     */
    @Override
    public <T extends Component> T GetEntry(int iEntry) {
        switch(iEntry){
            case 0: return (T)label;
            case 1: return (T)this;
            default: throw new IllegalArgumentException(iEntry+" does not match to an item!");
        }
    }

    /**
     * ignore
     */
    @Override
    public int compX() {
        return compX;
    }

    /**
     * ignore
     */
    @Override
    public int compY() {
        return compY;
    }

    /**
     * ignore
     */
    @Override
    public void GetComps(ArrayList<Component> putHere, ArrayList<Integer> coordsHere, ArrayList<Integer> compSizesHere) {
        int labelEnd=compY/2;
        putHere.add(this.label);
        coordsHere.add(0);
        coordsHere.add(0);
        compSizesHere.add(compX);
        compSizesHere.add(labelEnd);
        putHere.add(this);
        coordsHere.add(0);
        coordsHere.add(labelEnd);
        compSizesHere.add(compX);
        compSizesHere.add(compY-labelEnd);
    }
}
