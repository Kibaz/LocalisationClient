package objects;

/**
 * Created by Marcus on 27/03/2019.
 */

public class Model {

    private int vaoID;
    private int vertexCount;

    public Model(int vaoID, int vertexCount)
    {
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
    }

    public int getID()
    {
        return vaoID;
    }

    public int getVertexCount()
    {
        return vertexCount;
    }

}
