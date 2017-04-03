package AgentFramework.Gui;

import AgentFramework.Misc.GuiCloseAction;
import AgentFramework.Misc.GuiComp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/**
 * a JFrame wrapper that holds menu and gui items
 * the window that the other gui items sit in
 */
public class Gui extends JFrame{
    private JPanel panel;
    private GridBagConstraints gbc;
    boolean main;
    boolean alive;
    ArrayList<GuiComp> comps;
    ArrayList<Integer> compCoords;
    ArrayList<Component> subComps=new ArrayList<>();
    ArrayList<Integer> subCompCoords=new ArrayList<>();
    ArrayList<Integer> subCompSizes=new ArrayList<>();
    int[] locs;
    GuiCloseAction closeAction;

    /**
     * @param title the title that will appear at the top of the window
     * @param main whether the program should terminate on closing the window
     * @param closeAction function that will run when the window is closed
     */
    public Gui(String title, boolean main,GuiCloseAction closeAction){
        this.main=main;
        this.comps=new ArrayList<>();
        this.compCoords=new ArrayList<>();
        this.setResizable(false);//fixes window size
        this.setLocationRelativeTo(null);//puts window in middle of screen
        this.closeAction=closeAction;
        if(main){
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    if(closeAction!=null) {
                        closeAction.Action(e);
                    }
                    setVisible(false);
                    dispose();
                    System.exit(0);
                }
            });
        }
        else {
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    if(alive) {
                        alive=false;
                        if(closeAction!=null) {
                            closeAction.Action(e);
                        }
                        setVisible(false);
                        dispose();
                    }
                }
            });
        }
        this.setTitle(title);
        panel = new JPanel();
        gbc=new GridBagConstraints();
        panel.setLayout(new GridBagLayout());
        this.add(panel);
        this.locs=new int[1000*1000];
        for(int i=0;i<1000*1000;i++){
            locs[i]=-1;
        }
    }
    /**
     * @param title the title that will appear at the top of the window
     * @param main whether the program should terminate on closing the window
     */
    public Gui(String title,boolean main) {
        this.main=main;
        this.comps=new ArrayList<>();
        this.compCoords=new ArrayList<>();
        this.setResizable(false);//fixes window size
        this.setLocationRelativeTo(null);//puts window in middle of screen
        if(main){
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        else {
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
        this.setTitle(title);
        panel = new JPanel();
        gbc=new GridBagConstraints();
        panel.setLayout(new GridBagLayout());
        this.add(panel);
        this.locs=new int[1000*1000];
        for(int i=0;i<1000*1000;i++){
            locs[i]=-1;
        }
    }

    /**
     * Disables or enables all interacton with the Gui
     * @param onOff whether to enable or disable the gui
     */
    public void GreyOut(boolean onOff){
        this.setEnabled(!onOff);
        this.panel.setEnabled(!onOff);
        for(GuiComp gc:comps){
            subComps.clear();
            subCompCoords.clear();
            subCompSizes.clear();
            gc.GetComps(subComps,subCompCoords,subCompSizes);
            for(Component sc:subComps){
                sc.setEnabled(!onOff);
            }
        }
    }

    void PlaceComponent(GuiComp comp,int x,int y,int w,int h){
        int iComp=comps.size();
        comps.add(comp);
        compCoords.add(x);
        compCoords.add(y);
        for(int i=x;i<x+w;i++){
            for(int j=y;j<y+h;j++){
                locs[CtoI(i,j)]=iComp;
            }
        }
    }

    /**
     * starts the gui thread and reveals the gui to the user
     */
    public void RunGui(){
        alive=true;
        for(int i=0;i<comps.size();i++){
            int compX=compCoords.get(i*2);
            int compY=compCoords.get(i*2+1);
            GuiComp comp=comps.get(i);
            subComps.clear();
            subCompCoords.clear();
            subCompSizes.clear();
            comp.GetComps(subComps,subCompCoords,subCompSizes);
            for(int j=0;j<subComps.size();j++){
                Component subComp=subComps.get(j);
                int subX=subCompCoords.get(j*2);
                int subY=subCompCoords.get(j*2+1);
                int subW=subCompSizes.get(j*2);
                int subH=subCompSizes.get(j*2+1);
                AddComponent(subComp,compX+subX,compY+subY,subW,subH);
            }
        }
        this.pack();
        this.setVisible(true);
        panel.setVisible(true);
    }

    /**
     * puts component argument into the gui at the specified position
     * @param comp component to be added
     * @param w width of component in gui grid squares
     * @param h height of component in gui grid squares
     */
    public void AddComponent(Component comp, int x, int y, int w, int h) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = w;
        gbc.gridheight = h;
        panel.add(comp,gbc);
        comp.setVisible(true);
//        if(obj instanceof VisualizerOld){
//            ((VisualizerOld)obj).createBufferStrategy(3);
//        }
        if(comp instanceof GuiVis){
            ((GuiVis)comp).g=(Graphics2D)(((GuiVis)comp).getGraphics());
        }
    }
    int CtoI(int x,int y){ return x*1000+y; }
    int CtoLocVal(int x,int y){
        if(x<0||x>=1000||y<0||y>=1000){ throw new IllegalArgumentException("going for coord outside Gui range"); }
        return this.locs[x*1000+y];
    }

    /**
     * adds component by dropping down into the specified column
     * think connect 4 or tetris
     * @param comp component to be added
     * @param col column to drop from. the left end of the component will occupy this column
     */
    public void AddCol(GuiComp comp, int col){
        int w=comp.compX();
        int h=comp.compY();
        int found=0;
        for(int y=999;y>=0;y--) {
            for (int x = col; x < col + w; x++) {
                if(CtoLocVal(x,y)!=-1){
                    found=y+1;
                    break;
                }
            }
            if(found!=0){
                break;
            }
        }
        PlaceComponent(comp,col,found,w,h);
    }

    /**
     * destroys the gui with the window closing event
     */
    public void Destroy(){
        dispatchEvent(new WindowEvent(this,WindowEvent.WINDOW_CLOSING));
    }
}

