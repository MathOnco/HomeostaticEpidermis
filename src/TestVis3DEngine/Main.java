package TestVis3DEngine;
/**
 * Created by schencro on 5/31/17.
 */
import Vis3DEngine.GameEngine;
import Vis3DEngine.IGameLogic;
 
public class Main {
 
    public static void main(String[] args) {
        try {
            boolean vSync = true;
            IGameLogic gameLogic = new DummyGame();
            GameEngine gameEng = new GameEngine("TestVis3DEngine", 1200, 1200, vSync, gameLogic);
            gameEng.start();
        } catch (Exception excp) {
            excp.printStackTrace();
            System.exit(-1);
        }
    }
}