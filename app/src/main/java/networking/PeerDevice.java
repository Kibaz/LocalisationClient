package networking;

import database.Models;
import objects.Circle;
import rendering.Loader;

/**
 * Created by Marcus on 30/03/2019.
 */

public class PeerDevice {

    // Fields
    private String macAddress;
    private float locationX;
    private float locationY;

    private Circle pointer;

    // Constructor
    public PeerDevice(String macAddress, float x, float y)
    {
        this.macAddress = macAddress;
        this.locationX = x;
        this.locationY = y;
        pointer = new Circle(0.05f,x,y,20);
        pointer.setModel(Models.circle);
        pointer.setColour(0,1,0);
    }

    public String getMacAddress()
    {
        return macAddress;
    }

    public float getLocationX()
    {
        return locationX;
    }

    public float getLocationY()
    {
        return locationY;
    }

    public Circle getPointer()
    {
        return pointer;
    }


}
