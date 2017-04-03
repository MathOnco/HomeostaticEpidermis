package Epidermis_Model;

/**
 * Created by schencro on 3/27/17.
 */

public class EpidermisCellVis {
    public int[] division_vis;
    public int[] stationary_vis;
    public int[] movement_vis;

    /**
     * Used to visualize cell status for those that divided in that time step
     **/
    public EpidermisCellVis() {
        division_vis = new int[]{
                1, 0, 2, 0, 3, 0, // -***-
                0, 1, 4, 1, // *---*
                0, 2, 4, 2, // *---*
                0, 3, 4, 3, // *---*
                1, 4, 2, 4, 3, 4}; // -***-


        /** Used to visualize cell status for those that are alive, but are quiescent no activity in that time step **/
        stationary_vis = new int[]{
                    0, 0, 1, 0, 2, 0, 3, 0, 4, 0, // *****
                    0, 1, 1, 1, 2, 1, 3, 1, 4, 1, // *****
                    0, 2, 1, 2, 2, 2, 3, 2, 4, 2, // *****
                    0, 3, 1, 3, 2, 3, 3, 3, 4, 3,// *****
                    0, 4, 1, 4, 2, 4, 3, 4, 4, 4}; // *****

        /** Used to visualize cell status for those that moved, but are not dividing **/
        movement_vis = new int[]{
                    0, 0 , 1, 0, 2, 0, 3, 0, 4, 0, // *****
                    0, 1, 2, 1, 4, 1, // *-*-*
                    0, 2, 1, 2, 2, 2, 3, 2, 4, 2, // *****
                    0, 3, 2, 3, 4, 3,// *-*-*
                    0, 4, 1, 4, 2, 4, 3, 4, 4, 4}; // *****
        }
}
