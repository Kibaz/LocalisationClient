package honours.localisationclient;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import objects.Camera;
import rendering.GLRenderer;

/**
 * Created by Marcus on 07/02/2019.
 */

public class GLView extends GLSurfaceView implements View.OnTouchListener{

    private float prevX = 0;
    private float prevY = 0;

    private Camera camera;

    public GLView(Context context) {
        super(context);
        init();
    }

    public GLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init()
    {
        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(true);
        camera = new Camera();
        setRenderer(new GLRenderer(camera));
        setOnTouchListener(this);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if(event != null)
        {
            float x = event.getX();
            float y = event.getY();

            if(event.getAction() == MotionEvent.ACTION_MOVE)
            {
                float deltaX = (x - prevX) / MainActivity.screenDensity / 100f;
                float deltaY = (y - prevY) / MainActivity.screenDensity / 100f;
                camera.increasePosition(deltaX,deltaY,0);
                //Log.d("Camera X",Float.toString(camera.getPosition()[0]));
                //Log.d("Camera Y",Float.toString(camera.getPosition()[1]));
            }
            prevX = x;
            prevY = y;

            return true;
        }
        else
        {
            return super.onTouchEvent(event);
        }
    }

}
