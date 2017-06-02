package Vis3DEngine;
/**
 * Created by schencro on 5/31/17.
 */
import org.joml.Vector3f;
import Vis3DEngine.graph.VAO;

public class GameItem {

    private VAO mesh;

    private final Vector3f position;

    private float scale;

    private final Vector3f rotation;

    public GameItem(){
        position = new Vector3f(0, 0, 0);
        scale = 1;
        rotation = new Vector3f(0, 0, 0);
    }

    public GameItem(VAO mesh) {
        this();
        this.mesh=mesh;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public VAO getMesh() {
        return mesh;
    }

    public void setMesh(VAO mesh){ this.mesh = mesh; }
}