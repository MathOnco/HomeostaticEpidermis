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
     * @param colorOrder the order in which to fill the colors, default is rgb, but any permutation of these three characters is valid
     */
    public void SetColorHeat(int x, int y, double val,String colorOrder) {
        if(val>=0) {
            float c1 = (float) Math.min(1, val * 3);
            float c2 = 0;
            float c3 = 0;
            if (val > 0.333) {
                c2 = (float) Math.min(1, (val - 0.333) * 3);
            }
            if (val > 0.666) {
                c3 = (float) Math.min(1, (val - 0.666) * 3);
            }
            switch (colorOrder) {
                case "rgb":
                    SetColor(x, y, c1, c2, c3);
                    break;
                case "rbg":
                    SetColor(x, y, c1, c3, c2);
                    break;
                case "grb":
                    SetColor(x, y, c2, c1, c3);
                    break;
                case "gbr":
                    SetColor(x, y, c3, c1, c2);
                    break;
                case "brg":
                    SetColor(x, y, c2, c3, c1);
                    break;
                case "bgr":
                    SetColor(x, y, c3, c2, c1);
                    break;
                default:
                    SetColor(x, y, c1, c2, c3);
                    System.out.println("Invalid colorOrder string passed to SetColorHeat:"+colorOrder+"\ncolorOrder String must be some permutation of the characters 'r','g','b'");
                    break;
            }
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

    /** * Converts HSV values to RGB values
     * hue: 0->1 (Picks Color)
     * saturation: 0->1
     * brightness: 0->1
     **/
    public void SetColorHSV(int x,int y,float hue,float saturation,float brightness){
        float scale = 255f;
        int rgb = Color.HSBtoRGB(hue, saturation, brightness);
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        SetColor(x,y,r/scale,g/scale,b/scale);
    }
}
