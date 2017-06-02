package Vis3DEngine;

/**
 * Created by schencro on 6/1/17.
 */



public interface IHud {

    GameItem[] getGameItems();

    default void cleanup() {
        GameItem[] gameItems = getGameItems();
        for(GameItem gameItem : gameItems){
            gameItem.getMesh().cleanUp();
        }
    }

}
