package shaders;

import android.content.Context;

import honours.localisationclient.MainActivity;

/**
 * Created by Marcus on 26/03/2019.
 */

public class BasicShader extends ShaderProgram {

    private static final String VERTEX_SHADER = "shape_vertex_shader.vert";
    private static final String FRAGMENT_SHADER = "shape_fragment_shader.frag";

    // Define uniform locations to be registered by teh GLSL shader program
    private int location_transformationMatrix;
    private int location_colour;
    private int location_opacity;

    public BasicShader(Context context) {
        super(VERTEX_SHADER, FRAGMENT_SHADER,context);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0,"position");
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_colour = super.getUniformLocation("colour");
        location_opacity = super.getUniformLocation("opacity");
    }

    public void loadColour(float[] colour)
    {
        super.loadVector(location_colour, colour);
    }

    public void loadTransformationMatrix(float[] matrix)
    {
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    public void loadOpacity(float opacity)
    {
        super.loadFloat(location_opacity, opacity);
    }
}
