package AgentFramework.Gui;

import AgentFramework.Misc.GuiComp;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * a gui item that presents text
 */
public class GuiLabel extends JLabel implements GuiComp {
    int xDim;
    int yDim;

    /**
     * @param text label text
     * @param compX width on the gui GridBagLayout GridBagLayout
     * @param compY height on the gui GridBagLayout GridBagLayout
     */
    public GuiLabel(String text, int compX, int compY){
        super(text);
        this.xDim=compX;
        this.yDim=compY;
    }

    /**
     * @param text label text
     */
    public GuiLabel(String text){
        super(text);
        this.xDim=1;
        this.yDim=1;
    }

    /**
     * gets the x component of the label
     */
    public int compX(){
        return xDim;
    }

    /**
     * gets the y component of the label
     */
    public int compY(){
        return yDim;
    }

    /**
     * called by the Gui class to place the label
     */
    public void GetComps(ArrayList<Component> putHere,ArrayList<Integer> putCoordsHere,ArrayList<Integer>compSizesHere){
        putHere.add(this);
        putCoordsHere.add(0);
        putCoordsHere.add(0);
        compSizesHere.add(xDim);
        compSizesHere.add(yDim);
    }

    /**
     * sets the label text
     */
    public void Set(String newText){ this.setText(newText); }
}
