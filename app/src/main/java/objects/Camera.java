package objects;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import honours.localisationclient.MainActivity;
import rendering.GLRenderer;

/**
 * Created by Marcus on 27/03/2019.
 */

public class Camera {

    // Fields
    private float[] position;

    private float pitch = 0;
    private float yaw = 0;
    private float roll;

    private float zoom = 5;

    private float time = 0;

    // Constructor
    public Camera()
    {
        position = new float[3]; // X, Y, Z
        position[2] = zoom;
    }

    public void setZoom(float zoom)
    {
        this.zoom = zoom;
    }

    public void zoom()
    {
        if((int) position[2] != (int) zoom)
        {
            position[2] += (zoom-position[2]) * GLRenderer.getDeltaTime() * 2.0f;
        }
    }

    // Getters and Setters
    public float[] getPosition()
    {
        return position;
    }

    public float getPitch()
    {
        return pitch;
    }

    public float getYaw()
    {
        return yaw;
    }

    public float getRoll()
    {
        return roll;
    }

    public void increasePosition(float dx, float dy, float dz)
    {
        this.position[0] += dx;
        this.position[1] += dy;
        this.position[2] += dz;
    }

}
