package AgentFramework.Gui;

import AgentFramework.Utils;
import AgentFramework.Misc.GuiComp;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * a gui item that is used to efficiently visualize in 2 dimensions
 * uses an array of pixels whose color values are individually set
 */
public class GuiVis extends JPanel implements GuiComp {
    public final int xDim;
    public final int yDim;
    public final int scale;
    public final int compX;
    public final int compY;
    BufferedImage buff;
    int[] data;
    Graphics2D g;

    /**
     * @param pixelW width of the GuiVis in pixels
     * @param pixelH height of the GuiVis in pixels
     * @param scaleFactor the width and height in screen pixels of each GuiVis pixel
     * @param compX width on the gui GridBagLayout
     * @param compY height on the gui GridBagLayout
     */
    public GuiVis(int pixelW, int pixelH, int scaleFactor,int compX,int compY){
        this.setVisible(true);
        xDim=pixelW;
        yDim=pixelH;
        this.compX=compX;
        this.compY=compY;
        buff=new BufferedImage(xDim,yDim,BufferedImage.TYPE_INT_RGB);
        data=((DataBufferInt)buff.getRaster().getDataBuffer()).getData();
        scale=scaleFactor;
        this.setPreferredSize(new Dimension((int)Math.ceil(xDim*scaleFactor), (int)Math.ceil(yDim*scaleFactor)));
        this.setMaximumSize(new Dimension((int)Math.ceil(xDim*scaleFactor), (int)Math.ceil(yDim*scaleFactor)));
        this.setMinimumSize(new Dimension((int)Math.ceil(xDim*scaleFactor), (int)Math.ceil(yDim*scaleFactor)));
    }

    /**
     * sets pixel at the specified x,y position to the rgb color value specified, bounding components between 0 and 1
     */
    public void SetColorBound(int x, int y, float r, float g, float b){
        r=Utils.BoundValF(r,0,1);
        g=Utils.BoundValF(g,0,1);
        b=Utils.BoundValF(b,0,1);
        SetColor(x,y,r,g,b);
    }

    /**
     * sets the pixel at the specified x,y position using the heat colormap, which goes from black to red to yellow to white
     */
    public void SetColorHeat(int x, int y, double val){
        if(val>0) {
            float r = (float) Math.min(1, val * 3);
            float g = 0;
            float b = 0;
            if (val > 0.333) {
                g = (float) Math.min(1, (val - 0.333) * 3);
            }
            if (val > 0.666) {
                b = (float) Math.min(1, (val - 0.666) * 3);
            }
            SetColor(x, y, r, g, b);
        }
    }

    /**
     * gets the x component of the vis window
     */
    @Override
    public int compX(){return compX;}
    /**
     * gets the y component of the vis window
     */
    @Override
    public int compY(){return compY;}

    /**
     * called by the Gui class to place the vis window
     */
    @Override
    public void GetComps(ArrayList<Component> putHere, ArrayList<Integer> coordsHere, ArrayList<Integer> compSizesHere) {
        putHere.add(this);
        coordsHere.add(0);
        coordsHere.add(0);
        compSizesHere.add(compX);
        compSizesHere.add(compY);
    }
    public void SetColor(int x, int y, float r, float g, float b){
        Color c=new Color(r,g,b);
        //buff.setRGB(x,compY-1-y,c.getRGB());
        data[(yDim-y-1)*xDim+x]=c.getRGB();
    }

    /**
     * sets all pixels to the rgb color specified, bounding components between 0 and 1
     */
    public void ClearColor(float r, float g, float b){
        r=Utils.BoundValF(r,0,1);
        g=Utils.BoundValF(g,0,1);
        b=Utils.BoundValF(b,0,1);
        Color c=new Color(r,g,b);
        Arrays.fill(data,c.getRGB());
    }

    /**
     * called by the Gui to draw the vis
     */
    @Override
    public void paint(Graphics g){
        ((Graphics2D)g).drawImage(buff.getScaledInstance(scale*xDim,scale*yDim,Image.SCALE_FAST),null,null);
        repaint();
    }
}
