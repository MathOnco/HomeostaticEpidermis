package AgentFramework.Gui;

import AgentFramework.Misc.*;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import static AgentFramework.Utils.*;


/**
 * a menu item that takes double input
 */
public class MenuDouble extends JFormattedTextField implements AgentFramework.Misc.MenuItem,GuiComp {
    static final DecimalFormat doubleFormat=new DecimalFormat("#.##########################");
    String labelText;
    JLabel label;
    double min;
    double max;
    int compX;
    int compY;

    /**
     * @param mySet the MenuSet that can be queried to get the value of this combo box
     * @param label the label of the menuDouble
     * @param initVal the starting setting of the menuDouble
     */
    public MenuDouble(MenuSet mySet, String label, double initVal, double min, double max){
        super(doubleFormat);
        this.setColumns(10);
        this.min=min;
        this.max=max;
        this.labelText=label;
        this.label=new JLabel(labelText);
        this.compX=1;
        this.compY=2;
        mySet.AddGuiMenuItem(this,Double.toString(initVal));
    }

    /**
     * @param mySet the MenuSet that can be queried to get the value of this combo box
     * @param label the label of the menuDouble
     * @param initVal the starting value of the menuDouble
     * @param nCols the number of digits that will fit on the display
     * @param compX the width on the gui GridBagLayout
     * @param compY the height on the gui GridBagLayout
     */
    public MenuDouble(MenuSet mySet, String label, double initVal, double min, double max, int nCols, int compX, int compY){
        super(doubleFormat);
        this.setColumns(nCols);
        this.min=min;
        this.max=max;
        this.labelText=label;
        this.label=new JLabel(labelText);
        this.compX=compX;
        this.compY=compY;
        mySet.AddGuiMenuItem(this,Double.toString(initVal));
    }

    /**
     * ignore
     */
    @Override
    public int TypeID() {
        return 2;
    }

    /**
     * ignore
     */
    @Override
    public void Set(String val) {
        this.setText(Double.toString(BoundVal(Double.parseDouble(val),min,max)));
    }

    /**
     * ignore
     */
    @Override
    public String Get() {
        String val=Double.toString(BoundVal(Double.parseDouble(this.getText()),min,max));
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
    public int NEntries() {
        return 2;
    }

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
    public void GetComps(ArrayList<Component> putHere, ArrayList<Integer> coordsHere, ArrayList<Integer> compSizesHere){
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
