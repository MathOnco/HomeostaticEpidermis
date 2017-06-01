package Vis3DEngine;
/**
 * Created by schencro on 5/31/17.
 */
public interface IGameLogic {

    void init(Window window) throws Exception;
    
    void input(Window window, MouseInput mouseInput);

    void update(float interval, MouseInput mouseInput);
    
    void render(Window window);
    
    void cleanup();
}