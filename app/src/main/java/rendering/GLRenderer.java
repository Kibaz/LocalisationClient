package rendering;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import honours.localisationclient.MainActivity;
import objects.Circle;
import rendering.Loader;
import shaders.BasicShader;

/**
 * Created by Marcus on 07/02/2019.
 */

public class GLRenderer implements GLSurfaceView.Renderer{

    private BasicShader shader;
    private Circle circle;

    // Fields
    private float aspectRatio = 0; // Retain aspect ratio of device

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0,0.5f,0.5f,1f);
        shader = new BasicShader(MainActivity.context);
        circle = new Circle(0.5f,0,0,20);
        circle.setModel(Loader.loadToVAO(circle.getVertices()));
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        aspectRatio = (float) width / (float) height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        shader.start();
        circle.draw(shader,1/aspectRatio);
        shader.stop();
    }
}
