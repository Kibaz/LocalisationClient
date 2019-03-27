package shaders;

import android.content.Context;
import android.opengl.GLES11;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * Created by Marcus on 26/03/2019.
 */

public abstract class ShaderProgram {

    private int program;
    private int vertShaderID;
    private int fragShaderID;

    public ShaderProgram(String vertexShader, String fragmentShader, Context context)
    {
        vertShaderID = loadShader(vertexShader,GLES20.GL_VERTEX_SHADER,context);
        fragShaderID = loadShader(fragmentShader, GLES20.GL_FRAGMENT_SHADER,context);
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertShaderID);
        GLES20.glAttachShader(program, fragShaderID);
        bindAttributes();
        GLES20.glLinkProgram(program);
        GLES20.glValidateProgram(program);
        getAllUniformLocations();
    }

    public void start()
    {
        GLES20.glUseProgram(program);
    }

    public void stop()
    {
        GLES20.glUseProgram(0);
    }

    public void clear()
    {
        stop();
        GLES20.glDetachShader(program,vertShaderID);
        GLES20.glDetachShader(program,fragShaderID);
        GLES20.glDeleteShader(vertShaderID);
        GLES20.glDeleteShader(vertShaderID);
        GLES20.glDeleteProgram(program);
    }

    private int loadShader(String filename, int type, Context context)
    {

        StringBuilder shaderSource = new StringBuilder();
        try {
            InputStream is = context.getAssets().open(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while((line = reader.readLine()) != null)
            {
                shaderSource.append(line).append("//\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderSource.toString());
        GLES20.glCompileShader(shader);
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS,compiled,0);
        if( compiled[0] == 0)
        {
            Log.e("Shader status: ", GLES20.glGetShaderInfoLog(shader));
            System.err.println("Could not compile shader!");
            System.exit(-1);
        }

        return shader;
    }

    protected abstract void bindAttributes();

    protected abstract void getAllUniformLocations();

    protected void bindAttribute(int attr, String varName)
    {
        GLES20.glBindAttribLocation(program, attr, varName);
    }

    protected int getUniformLocation(String uniformName)
    {
        return GLES20.glGetUniformLocation(program, uniformName);
    }

    protected void loadMatrix(int location, float[] matrix)
    {
        GLES20.glUniformMatrix4fv(location,1,false,matrix,0);
    }

    protected void loadVector(int location, float[] vector)
    {
        GLES20.glUniform3f(location,vector[0],vector[1],vector[2]);
    }

    protected void load2DVector(int location, float[] vector)
    {
        GLES20.glUniform2f(location,vector[0],vector[1]);
    }

    protected void loadFloat(int location, float value)
    {
        GLES20.glUniform1f(location, value);
    }


}
