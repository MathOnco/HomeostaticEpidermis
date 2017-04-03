package AgentFramework.Gui;

import AgentFramework.Misc.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * a menu item that facilitates choosing from a set of options
 */
public class MenuComboBox extends JComboBox implements AgentFramework.Misc.MenuItem,GuiComp {
    String labelText;
    JLabel label;
    int compX;
    int compY;

    /**
     * @param mySet the MenuSet that can be queried to get the value of this combo box
     * @param label the label of the comboBox
     * @param initVal the starting setting of the combo box
     * @param options the array of options. the setting of the combo box corresponds to the index of the option in the array
     */
    public MenuComboBox(MenuSet mySet, String label, int initVal, String[] options){
        super(options);
        this.labelText=label;
        this.label=new JLabel(labelText);
        this.compX=1;
        this.compY=2;
        mySet.AddGuiMenuItem(this,Integer.toString(initVal));
    }
    /**
     * @param mySet the MenuSet that can be queried to get the value of this combo box
     * @param label the label of the comboBox
     * @param initVal the starting setting of the combo box
     * @param options the array of options. the setting of the combo box corresponds to the index of the option in the array
     * @param compX the width on the gui GridBagLayout
     * @param compY the height on the gui GridBagLayout
     */
    public MenuComboBox(MenuSet mySet, String label, int initVal, String[] options, int compX, int compY){
        super(options);
        this.labelText=label;
        this.label=new JLabel(labelText);
        this.compX=compX;
        this.compY=compY;
        mySet.AddGuiMenuItem(this,Integer.toString(initVal));
    }

    /**
     * ignore
     */
    @Override
    public int TypeID() {
        return 1;
    }

    /**
     * ignore
     */
    @Override
    public void Set(String val) {
        this.setSelectedIndex(Integer.parseInt(val));
    }

    /**
     * ignore
     */
    @Override
    public String Get() {
        return Integer.toString(this.getSelectedIndex());
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
    public int NEntries() { return label!=null?2:1; }

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
    public int compX(){
        return compX;
    }
    /**
     * ignore
     */
    @Override
    public int compY(){
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
