package objects;

import android.opengl.GLES11;
import android.opengl.GLES20;
import android.opengl.GLES30;
import java.util.ArrayList;
import java.util.List;
import shaders.BasicShader;
import utils.Maths;

/**
 * Created by Marcus on 25/03/2019.
 */

public class Circle {

    // Fields
    private float radius;
    private float x; // Center X
    private float y; // Center Y
    private float z; // Position in 3D space
    private int segments;

    private float[] vertices;
    private int[] indices;
    private float[] colour;

    private Model model;

    // Constructor
    public Circle(float radius, float x, float y, int segments)
    {
        this.radius = radius;
        this.x = x;
        this.y = y;
        this.z = 0;
        this.segments = segments;
        constructCircle(segments);
        colour = new float[3];
    }

    public float[] getVertices()
    {
        return vertices;
    }

    public void setColour(float r, float g, float b)
    {
        colour[0] = r;
        colour[1] = g;
        colour[2] = b;
    }

    public void setModel(Model model)
    {
        this.model = model;
    }

    public void setZ(float value)
    {
        this.z += value;
    }

    public void increasePosition(float dx, float dy, float dz)
    {
        this.x += dx;
        this.y += dy;
        this.z += dz;
    }

    public void constructCircle(int segments)
    {
        List<Float> verts = new ArrayList<>();

        float increment = (float) (2.0f * Math.PI / segments);
        for(float angle = 0.0f; angle <= 2.0f * Math.PI; angle+= increment)
        {
            verts.add((float)(radius * Math.cos(angle)));
            verts.add((float)(radius * Math.sin(angle)));
            verts.add(0f);
        }
        vertices = new float[verts.size()];
        for(int i = 0; i < verts.size(); i++)
        {
            vertices[i] = verts.get(i);
        }


    }

    /*
    Deprecated code
    public void draw(GL10 gl)
    {
        FloatBuffer vertexBuffer;
        IntBuffer indicesBuffer;

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        byteBuffer = ByteBuffer.allocateDirect(indices.length);
        byteBuffer.order(ByteOrder.nativeOrder());
        indicesBuffer = byteBuffer.asIntBuffer();
        indicesBuffer.put(indices);
        indicesBuffer.position(0);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glVertexPointer(3,GL10.GL_FLOAT,0,vertexBuffer);

        gl.glDrawArrays(GL10.GL_TRIANGLE_FAN,0,vertices.length/3);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
    */

    public void draw(BasicShader shader)
    {
        GLES30.glBindVertexArray(model.getID());
        GLES20.glEnableVertexAttribArray(0);
        prepare(shader);
        GLES11.glDrawArrays(GLES11.GL_TRIANGLE_FAN,0,model.getVertexCount());
        GLES20.glDisableVertexAttribArray(0);
        GLES30.glBindVertexArray(0);
    }

    private void prepare(BasicShader shader)
    {
        shader.loadTransformationMatrix(Maths.createTransformationMatrix(this.x,this.y,this.z,0,0,0,1,1,1));
        shader.loadColour(colour);
        shader.loadOpacity(0);
    }
}
