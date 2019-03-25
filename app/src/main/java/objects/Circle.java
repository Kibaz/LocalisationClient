package objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marcus on 25/03/2019.
 */

public class Circle {

    // Fields
    private float radius;
    private float x; // Center X
    private float y; // Center Y

    private float[] vertices;

    // Constructor
    public Circle(float radius, float x, float y, int segments)
    {
        this.radius = radius;
        this.x = x;
        this.y = y;
        constructCircle(segments);
    }

    private void constructCircle(int segments)
    {
        List<Float> verts = new ArrayList<>();

        float increment = (float) (2.0f * Math.PI / segments);
        for(float angle = 0.0f; angle <= 2.0f * Math.PI; angle+= increment)
        {
            verts.add((float)(radius * Math.cos(angle)));
            verts.add((float)(radius * Math.sin(angle)));
            verts.add(0f);
        }
        float[] vertices = new float[verts.size()];
        for(int i = 0; i < verts.size(); i++)
        {
            vertices[i] = verts.get(i);
        }
    }
}
