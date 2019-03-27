package objects;

/**
 * Created by Marcus on 27/03/2019.
 */

public class Camera {

    // Fields
    private float[] position;

    private float pitch = 0;
    private float yaw = 0;
    private float roll;

    private float zoom = 20;

    // Constructor
    public Camera()
    {
        position = new float[3]; // X, Y, Z
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
