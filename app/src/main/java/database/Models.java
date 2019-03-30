package database;

import objects.Circle;
import objects.Model;
import rendering.Loader;

/**
 * Created by Marcus on 30/03/2019.
 */

public class Models {

    public static Model circle = Loader.loadToVAO((new Circle(0.05f,0,0,20)).getVertices());

}
