package utils;


import android.opengl.Matrix;
import android.util.Log;

import objects.Camera;

/**
 * Created by Marcus on 26/03/2019.
 */

public class Maths {

    /*
        Create transformation matrix to allow movement of the circle objects
        Also handles scaling
        Also handles rotation - not entirely necessary for this application however
     */
    public static float[] createTransformationMatrix(float tx, float ty, float tz, float rx, float ry, float rz, float scaleX, float scaleY, float scaleZ)
    {
        float[] matrix = new float[16];
        Matrix.setIdentityM(matrix,0);
        Matrix.translateM(matrix,0,matrix,0,tx,ty,tz);
        Matrix.rotateM(matrix,0,matrix,0,rx,1,0,0);
        Matrix.rotateM(matrix,0,matrix,0,ry,0,1,0);
        Matrix.rotateM(matrix,0,matrix,0,rz,0,0,1);
        Matrix.scaleM(matrix,0,matrix,0,scaleX,scaleY,scaleZ);
        return matrix;
    }

    public static float[] createViewMatrix(Camera camera)
    {
        float[] matrix = new float[16];
        Matrix.setIdentityM(matrix,0);
        Matrix.rotateM(matrix,0,matrix,0,camera.getPitch(),1,0,0);
        Matrix.rotateM(matrix,0,matrix,0,camera.getYaw(),0,1,0);
        float[] cameraPos = camera.getPosition();
        float[] inverseCamPos = new float[] {-cameraPos[0], -cameraPos[1], -cameraPos[2]};
        Matrix.translateM(matrix,0,matrix,0,inverseCamPos[0],inverseCamPos[1],inverseCamPos[2]);
        return matrix;
    }

}
