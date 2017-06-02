package Vis3DEngine.graph;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

/**
 * Created by schencro on 6/2/17.
 */
public class VBO {
    FloatBuffer MemBuffer = null;
    
    private int vboID;
    
    public VBO (float[] vboData, VAO vaoObj) {
        
        try {
            this.vboID = glGenBuffers();
            MemBuffer = MemoryUtil.memAllocFloat(vboData.length);
            MemBuffer.put(vboData).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vaoObj.getVaoId());
            glBufferData(GL_ARRAY_BUFFER, MemBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        } catch (Exception e){
            this.vboID = -1;
        }
        
    }
    
    public int getID(){ return vboID; }
}
