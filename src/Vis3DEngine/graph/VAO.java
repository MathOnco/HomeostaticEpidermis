package Vis3DEngine.graph;
/**
 * Created by schencro on 5/31/17.
 */
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryUtil;

public class VAO {

    private final int vaoId;

    private final List<Integer> vboIdList;

    private final int vertexCount;

    private Material material;

    public VAO(float[] positions, float[] textCoords, float[] colours, float[] normals, int[] indices) {
        FloatBuffer posBuffer = null;
        FloatBuffer textCoordsBuffer = null;
        FloatBuffer colourBuffer = null;
        FloatBuffer vecNormalsBuffer = null;
        IntBuffer indicesBuffer = null;
        try {
            vertexCount = indices.length;
            vboIdList = new ArrayList();

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);


            // Position VBO
            int vboId = glGenBuffers();
            vboIdList.add(vboId);
            posBuffer = MemoryUtil.memAllocFloat(positions.length);
            posBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            // Texture coordinates VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.length);
            textCoordsBuffer.put(textCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            // Colour VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            colourBuffer = MemoryUtil.memAllocFloat(colours.length);
            colourBuffer.put(colours).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, colourBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

            // Vertex normals VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            vecNormalsBuffer = MemoryUtil.memAllocFloat(normals.length);
            vecNormalsBuffer.put(normals).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, vecNormalsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(3, 3, GL_FLOAT, false, 0, 0);

            // Index VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } finally {
            if (posBuffer != null) {
                MemoryUtil.memFree(posBuffer);
            }
            if (textCoordsBuffer != null) {
                MemoryUtil.memFree(textCoordsBuffer);
            }
            if (vecNormalsBuffer != null) {
                MemoryUtil.memFree(vecNormalsBuffer);
            }
            if (indicesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer);
            }
            if (colourBuffer != null) {
                MemoryUtil.memFree(colourBuffer);
            }
        }
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    private int createVBO(float[] data,int elemLen,int index){
        int vboID=0;
        FloatBuffer MemBuffer=null;
        try {
            vboID = glGenBuffers();
            MemBuffer = MemoryUtil.memAllocFloat(data.length);
            MemBuffer.put(data).flip();
            glBindBuffer(GL_ARRAY_BUFFER, getVaoId());
            glBufferData(GL_ARRAY_BUFFER, MemBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(index, elemLen, GL_FLOAT, false, 0, 0);
        } finally {
            MemoryUtil.memFree(MemBuffer);
        }
        return vboID;
    }
    private int createVBO(int[] data,int elemLen,int index){
        int vboID=-1;
        IntBuffer MemBuffer=null;
        try {
            vboID = glGenBuffers();
            MemBuffer = MemoryUtil.memAllocInt(data.length);
            MemBuffer.put(data).flip();
            glBindBuffer(GL_ARRAY_BUFFER, getVaoId());
            glBufferData(GL_ARRAY_BUFFER, MemBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(index, elemLen, GL_INT, false, 0, 0);
        } finally {
            MemoryUtil.memFree(MemBuffer);
        }
        return vboID;
    }

    public void render() {
        Texture texture = material.getTexture();
        if (texture != null) {
            // Activate firs texture bank
            glActiveTexture(GL_TEXTURE0);
            // Bind the texture
            glBindTexture(GL_TEXTURE_2D, texture.getId());
        }

        // Draw the mesh
        glBindVertexArray(getVaoId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);

        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);

        // Restore state
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(3);
        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void cleanUp() {
        glDisableVertexAttribArray(0);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : vboIdList) {
            glDeleteBuffers(vboId);
        }

        // Delete the texture
        Texture texture = material.getTexture();
        if (texture != null) {
            texture.cleanup();
        }

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }

    public void deleteBuffers() {
        glDisableVertexAttribArray(0);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : vboIdList) {
            glDeleteBuffers(vboId);
        }

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }
}