package objects;

import android.opengl.GLES11;
import android.opengl.GLES20;
import android.opengl.GLES30;

import shaders.BasicShader;
import utils.Maths;

/**
 * Created by Marcus on 28/03/2019.
 */

public class Square {

    private float x;
    private float y;
    private float z;

    private Model model;
    private float[] colour;
    private float[] vertices;

    public Square(float x, float y, float z,float[] vertices)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.vertices = vertices;
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

    public void draw(BasicShader shader)
    {
        GLES30.glBindVertexArray(model.getID());
        GLES20.glEnableVertexAttribArray(0);
        prepare(shader);
        GLES11.glLineWidth(10);
        GLES11.glDrawArrays(GLES11.GL_LINE_STRIP,0,model.getVertexCount());
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
