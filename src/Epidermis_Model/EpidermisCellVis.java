package Epidermis_Model;

import AgentFramework.Gui.GuiVis;

/**
 * Created by schencro on 3/27/17.
 */

public class EpidermisCellVis {
    public int[] division_vis;
    public int[] division_vis_alt;
    public int[] stationary_vis;
    public int[] movement_vis;

    public void DrawCellonGrid(GuiVis vis, EpidermisCell theCell){
        switch (theCell.Action) {
            case EpidermisConst.DIVIDE:
                for(int y=0;y<5;y++){
                    for(int x=0;x<5;x++){
                        int cVal=division_vis[x+y*5];
                        //vis.SetColor(theCell.Xsq()*5+x, theCell.Ysq()*5+y,cVal*theCell.r,cVal*theCell.g,cVal*theCell.b);
                        vis.SetColor(theCell.Xsq()*5+x, theCell.Ysq()*5+y,cVal*1f,cVal*0f,cVal* 0f);
                    }
                }
            break;
            case EpidermisConst.MOVING:
                for(int y=0;y<5;y++){
                    for(int x=0;x<5;x++){
                        int cVal=movement_vis[x+y*5];
                        //vis.SetColor(theCell.Xsq()*5+x, theCell.Ysq()*5+y,cVal*theCell.r,cVal*theCell.g,cVal*theCell.b);
                        vis.SetColor(theCell.Xsq()*5+x, theCell.Ysq()*5+y,cVal*.5f,cVal*1f,cVal* 0f);
                    }
                }
            break;
            case EpidermisConst.STATIONARY:
                for (int i = 0; i < stationary_vis.length; i += 2) {
                    vis.SetColor(stationary_vis[i] + theCell.Xsq()*5, stationary_vis[i + 1] + theCell.Ysq()*5, theCell.r, theCell.g, theCell.b);
                }
            break;
            default:
                System.out.println("ERROR: Unable to draw cell. Cell Action not found.");
        }
    }

    public void DrawEmptyCell(GuiVis vis, int x, int y){
        for(int i=0; i<5; i++){
            for(int j=0; j<5; j++){
                vis.SetColor(i+x*5, j+y*5, 0f, 0f, 0f);
            }
        }
    }

    /**
     * Used to visualize cell status for those that divided in that time step
     **/
    public EpidermisCellVis() {
        division_vis = new int[]{
                0,1,1,1,0,
                1,0,0,0,1,
                1,0,0,0,1,
                1,0,0,0,1,
                0,1,1,1,0
        };


        /** Used to visualize cell status for those that are alive, but are quiescent no activity in that time step **/
        stationary_vis = new int[]{
                    0, 0, 1, 0, 2, 0, 3, 0, 4, 0, // *****
                    0, 1, 1, 1, 2, 1, 3, 1, 4, 1, // *****
                    0, 2, 1, 2, 2, 2, 3, 2, 4, 2, // *****
                    0, 3, 1, 3, 2, 3, 3, 3, 4, 3,// *****
                    0, 4, 1, 4, 2, 4, 3, 4, 4, 4}; // *****

        /** Used to visualize cell status for those that moved, but are not dividing **/
        movement_vis = new int[]{
                    1,1,1,1,1, // *****
                    1,0,1,0,1, // *-*-*
                    1,1,1,1,1, // *****
                    1,0,1,0,1,// *-*-*
                    1,1,1,1,1}; // *****
}
}
