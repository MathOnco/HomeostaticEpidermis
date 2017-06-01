package TestVis3DEngine;
/**
 * Created by schencro on 5/31/17.
 */
import Vis3DEngine.graph.*;
import org.joml.Vector2f;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.*;
import Vis3DEngine.GameItem;
import Vis3DEngine.IGameLogic;
import Vis3DEngine.MouseInput;
import Vis3DEngine.Window;
import Vis3DEngine.graph.MeshColour;
import Vis3DEngine.graph.Mesh;
import org.joml.Vector4f;

import java.util.Random;

public class DummyGame implements IGameLogic {

    private static final Random RN = new Random();

    private static final float MOUSE_SENSITIVITY = 0.2f;

    private final Vector3f cameraInc;

    private final Renderer renderer;

    private final Camera camera;

    private GameItem[] gameItems;

    private Vector3f ambientLight;

    private PointLight pointLight;

    private DirectionalLight directionalLight;

    private float lightAngle;

    private static final float CAMERA_POS_STEP = 0.05f;

    public DummyGame() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        lightAngle = -90;
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);

        float reflectance = 1f;
        //Mesh mesh = OBJLoader.loadMesh("/models/bunny.obj");
        //Material material = new Material(new Vector3f(0.2f, 0.5f, 0.5f), reflectance);

        Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
        Texture texture = new Texture("/textures/FaceBlock.png");
        Material material = new Material(texture, reflectance);

        mesh.setMaterial(material);
        gameItems = new GameItem[20*5*20];
        int i=0;
        for(int x=0; x<20; x++){
            for (int y = 0; y < 5; y++) {
                for (int z = 0; z < 20; z++) {
                    GameItem gameItem = new GameItem(mesh);
                    gameItem.setScale(0.5f);
                    gameItem.setPosition(RN.nextInt(20)-10, RN.nextInt(20)-10, RN.nextInt(20)-10);
                    gameItems[i]=gameItem;
                    i++;
                }
            }
        }

        ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);
        Vector3f lightColour = new Vector3f(1, 1, 1);
        Vector3f lightPosition = new Vector3f(0, 0, 1);
        float lightIntensity = 1.0f;
        pointLight = new PointLight(lightColour, lightPosition, lightIntensity);
        PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
        pointLight.setAttenuation(att);

        lightPosition = new Vector3f(-1, 0, 0);
        lightColour = new Vector3f(1, 1, 1);
        directionalLight = new DirectionalLight(lightColour, lightPosition, lightIntensity);
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -1;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -1;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            cameraInc.y = -1;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            cameraInc.y = 1;
        }
        float lightPos = pointLight.getPosition().z;
        if (window.isKeyPressed(GLFW_KEY_N)) {
            this.pointLight.getPosition().z = lightPos + 0.1f;
        } else if (window.isKeyPressed(GLFW_KEY_M)) {
            this.pointLight.getPosition().z = lightPos - 0.1f;
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        // Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

        // Update camera based on mouse
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }

        // Update directional light direction, intensity and colour
        lightAngle += 1.1f;
        if (lightAngle > 90) {
            directionalLight.setIntensity(0);
            if (lightAngle >= 360) {
                lightAngle = -90;
            }
        } else if (lightAngle <= -80 || lightAngle >= 80) {
            float factor = 1 - (float) (Math.abs(lightAngle) - 80) / 10.0f;
            directionalLight.setIntensity(factor);
            directionalLight.getColor().y = Math.max(factor, 0.9f);
            directionalLight.getColor().z = Math.max(factor, 0.5f);
        } else {
            directionalLight.setIntensity(1);
            directionalLight.getColor().x = 1;
            directionalLight.getColor().y = 1;
            directionalLight.getColor().z = 1;
        }
        double angRad = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float) Math.sin(angRad);
        directionalLight.getDirection().y = (float) Math.cos(angRad);
    }

    @Override
    public void render(Window window) {
        renderer.render(window, camera, gameItems, ambientLight, pointLight, directionalLight);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for (GameItem gameItem : gameItems) {
            gameItem.getMesh().cleanUp();
        }
    }

}

