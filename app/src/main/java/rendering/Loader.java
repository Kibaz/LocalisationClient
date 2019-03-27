package rendering;

import android.opengl.GLES11;
import android.opengl.GLES20;
import android.opengl.GLES30;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import objects.Model;

/**
 * Created by Marcus on 27/03/2019.
 */

public class Loader {

    private static List<Integer> vaos = new ArrayList<>();
    private static List<Integer> vbos = new ArrayList<>();

    public static Model loadToVAO(float[] vertices)
    {
        int vaoID = createVAO();
        storeInAttribList(0,3,vertices);
        unbindVAO();
        return new Model(vaoID,vertices.length/3);
    }

    private static int createVAO()
    {
        int[] ids = new int[1];
        GLES30.glGenVertexArrays(1,ids,0);
        vaos.add(ids[0]);
        GLES30.glBindVertexArray(ids[0]);
        return ids[0];
    }

    private static void storeInAttribList(int attribNum, int coordSize, float[] data)
    {
        int[] buffers = new int[1];
        GLES20.glGenBuffers(1,buffers,0);
        vbos.add(buffers[0]);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,buffers[0]);
        FloatBuffer buffer = ByteBuffer.allocateDirect(data.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        buffer.put(data).position(0);


        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,buffer.capacity() * 4,buffer,GLES20.GL_STATIC_DRAW);
        GLES20.glVertexAttribPointer(attribNum,coordSize, GLES11.GL_FLOAT,false,0,0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,0);
    }

    private static void unbindVAO(){

        GLES30.glBindVertexArray(0);
    }

    public static void clear()
    {
        for(int vao: vaos)
        {
            int[] vaoBuffer = new int[] {vao};
            GLES30.glDeleteVertexArrays(1,vaoBuffer,0);
        }

        for(int vbo: vbos)
        {
            int[] vboBuffer = new int[] {vbo};
            GLES30.glDeleteBuffers(1,vboBuffer,0);
        }
    }

}
