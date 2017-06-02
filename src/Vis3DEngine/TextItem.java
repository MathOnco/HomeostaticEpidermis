package Vis3DEngine;

/**
 * Created by schencro on 6/1/17.
 */
import Vis3DEngine.graph.FontTexture;
import Vis3DEngine.graph.Material;
import Vis3DEngine.graph.VAO;
import Vis3DEngine.graph.Texture;

import java.util.ArrayList;
import java.util.List;


public class TextItem extends GameItem {

    private static final float ZPOS = 0.0f;

    private static final int VERTICES_PER_QUAD = 4;

    private String text;

    private FontTexture fontTexture;

    private float startx;

    private float starty;

    public TextItem(String text, FontTexture fontTexture, float xPos, float yPos) throws Exception {
        super();
        this.text = text;
        this.fontTexture = fontTexture;
        this.startx = xPos;
        this.starty = yPos*-1;
        setMesh(buildMesh(this.startx, this.starty));
    }

    private VAO buildMesh(float startx, float starty) {
        List<Float> positions = new ArrayList();
        List<Float> textCoords = new ArrayList();
        float[] normals   = new float[0];
        List<Integer> indices   = new ArrayList();
        char[] characters = text.toCharArray();
        int numChars = characters.length;

        for(int i=0; i<numChars; i++) {
            FontTexture.CharInfo charInfo = fontTexture.getCharInfo(characters[i]);

            // Build a character tile composed by two triangles

            // Left Top vertex
            positions.add(startx); // x
            positions.add(starty); //y
            positions.add(ZPOS); //z
            textCoords.add((float) charInfo.getStartX() / (float) fontTexture.getWidth());
            textCoords.add(0.0f);
            indices.add(i * VERTICES_PER_QUAD);

            // Left Bottom vertex
            positions.add(startx); // x
            positions.add((float) fontTexture.getHeight()+starty); //y
            positions.add(ZPOS); //z
            textCoords.add((float) charInfo.getStartX() / (float) fontTexture.getWidth());
            textCoords.add(1.0f);
            indices.add(i * VERTICES_PER_QUAD + 1);

            // Right Bottom vertex
            positions.add(startx + charInfo.getWidth()); // x
            positions.add((float) fontTexture.getHeight()+starty); //y
            positions.add(ZPOS); //z
            textCoords.add((float) (charInfo.getStartX() + charInfo.getWidth()) / (float) fontTexture.getWidth());
            textCoords.add(1.0f);
            indices.add(i * VERTICES_PER_QUAD + 2);

            // Right Top vertex
            positions.add(startx + charInfo.getWidth()); // x
            positions.add(starty); //y
            positions.add(ZPOS); //z
            textCoords.add((float) (charInfo.getStartX() + charInfo.getWidth()) / (float) fontTexture.getWidth());
            textCoords.add(0.0f);
            indices.add(i * VERTICES_PER_QUAD + 3);

            // Add indices por left top and bottom right vertices
            indices.add(i * VERTICES_PER_QUAD);
            indices.add(i * VERTICES_PER_QUAD + 2);

            startx += charInfo.getWidth();
        }
        float[] posArr = Utils.listToArray(positions);
        float[] textCoordsArr = Utils.listToArray(textCoords);
        int[] indicesArr = indices.stream().mapToInt(i->i).toArray();
        VAO mesh = new VAO(posArr, textCoordsArr, normals, indicesArr);
        mesh.setMaterial(new Material(fontTexture.getTexture()));
        return mesh;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        Texture texture = this.getMesh().getMaterial().getTexture();
        this.getMesh().deleteBuffers();
        this.setMesh(buildMesh(startx, starty));
    }
}
