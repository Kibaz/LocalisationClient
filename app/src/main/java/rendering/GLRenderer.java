package rendering;

import database.Models;
import networking.Client;
import networking.PeerDevice;
import networking.PeerManager;
import objects.Camera;

import android.opengl.GLES11;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import honours.localisationclient.MainActivity;
import objects.Circle;
import objects.Square;
import shaders.BasicShader;
import utils.Maths;

/**
 * Created by Marcus on 07/02/2019.
 */

public class GLRenderer implements GLSurfaceView.Renderer{

    // Constants
    private final float FOV = 50; // Field of view
    private final float NEAR_PLANE = 0.1f;
    private final float FAR_PLANE = 1000;


    // Fields
    private BasicShader shader;
    private Circle circle;
    private Square redZone;
    private Camera camera;

    private long lastTimeStamp;
    private static float deltaTime;

    private float[] projectionMatrix;

    public GLRenderer(Camera camera)
    {
        this.camera = camera;
    }

    // Fields
    private float aspectRatio = 0; // Retain aspect ratio of device

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0,0.5f,0.5f,1f);
        shader = new BasicShader(MainActivity.context);
        circle = new Circle(0.05f,0,0,20);
        float [] verts = {
                -1f, 1f, 0f,
                -1f, 0f, 0f,
                1f, 0f, 0f,
                1f, 0f, 0f,
                1f, 1f, 0f,
                -1f, 1f, 0f
        };
        redZone = new Square(0,0,0,verts);
        redZone.setModel(Loader.loadToVAO(redZone.getVertices()));
        circle.setModel(Models.circle);
        Models.circle = Loader.loadToVAO(circle.getVertices());
        for(String macAddress: PeerManager.getPeersDevices().keySet())
        {
            PeerDevice device = PeerManager.getPeersDevices().get(macAddress);
            device.getPointer().setModel(Models.circle);
        }
        if(Client.pointer != null)
        {
            Client.pointer.setModel(Models.circle);
        }
        lastTimeStamp = System.currentTimeMillis();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        shader.start();
        createProjectionMatrix(width,height);
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES11.glEnable(GLES11.GL_DEPTH_TEST);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        camera.setZoom(MainActivity.zoom);
        camera.zoom();
        shader.start();
        shader.loadViewMatrix(Maths.createViewMatrix(camera));
        for(String macAddress: PeerManager.getPeersDevices().keySet())
        {
            PeerDevice device = PeerManager.getPeersDevices().get(macAddress);
            device.getPointer().draw(shader);
        }
        if(Client.pointer != null)
        {
            Client.pointer.draw(shader);
        }
        //circle.draw(shader);
        redZone.draw(shader);
        redZone.setColour(1,0,0);
        shader.stop();
        update();
    }


    private void createProjectionMatrix(int width, int height)
    {
        aspectRatio = (float) width / (float) height;
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV/2f))));
        float x_scale = y_scale / aspectRatio;
        float frustum_lngth = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new float[16];
        projectionMatrix[0] = x_scale;
        projectionMatrix[5] = y_scale;
        projectionMatrix[10] = -((FAR_PLANE + NEAR_PLANE)/frustum_lngth);
        projectionMatrix[11] = -1;
        projectionMatrix[14] = - ((2 * NEAR_PLANE * FAR_PLANE)/frustum_lngth);
        projectionMatrix[15] = 0;
    }

    public static float getDeltaTime()
    {
        return deltaTime;
    }

    private void update()
    {
        long currentTime = System.currentTimeMillis();
        deltaTime = (float) (currentTime - lastTimeStamp) / 1000;
        lastTimeStamp = currentTime;
    }
}
