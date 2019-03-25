package objects;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import honours.localisationclient.GLView;

/**
 * Created by Marcus on 25/03/2019.
 */

public class Circle {

    // Fields
    private float radius;
    private float x; // Center X
    private float y; // Center Y
    private float x_scale;

    private float[] vertices;
    private int[] indices;

    // Constructor
    public Circle(float radius, float x, float y, int segments,float x_scale)
    {
        this.radius = radius;
        this.x = x;
        this.y = y;
        this.x_scale = x_scale;
        constructCircle(segments);
    }

    private void constructCircle(int segments)
    {
        List<Float> verts = new ArrayList<>();

        float increment = (float) (2.0f * Math.PI / segments);
        for(float angle = 0.0f; angle <= 2.0f * Math.PI; angle+= increment)
        {
            verts.add((float)(x_scale * radius * Math.cos(angle)));
            verts.add((float)(radius * Math.sin(angle)));
            verts.add(0f);
        }
        vertices = new float[verts.size()];
        for(int i = 0; i < verts.size(); i++)
        {
            vertices[i] = verts.get(i);
        }


    }

    public void draw(GL10 gl)
    {
        FloatBuffer vertexBuffer;
        IntBuffer indicesBuffer;


        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        /*byteBuffer = ByteBuffer.allocateDirect(indices.length);
        byteBuffer.order(ByteOrder.nativeOrder());
        indicesBuffer = byteBuffer.asIntBuffer();
        indicesBuffer.put(indices);
        indicesBuffer.position(0);*/

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glVertexPointer(3,GL10.GL_FLOAT,0,vertexBuffer);

        gl.glDrawArrays(GL10.GL_TRIANGLE_FAN,0,vertices.length/3);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);


    }
}
