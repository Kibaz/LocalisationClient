package honours.localisationclient;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import networking.Client;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    // Fields

    // Views
    private GLView glView;
    private TextView accelX;
    private TextView accelY;
    private TextView accelZ;

    // Networking
    private Client client;

    /*
     * Using Android's built-in accelerometer to acquire
     * acceleration data as input to the localisation server's
     * filtering and data processing algorithm. This will handle a
     * combination of RSSI data measured by 3 monitoring devices and
     * the acceleration data acquired by the local mobile device.
     */
    private SensorManager sensorManager;
    private Sensor accelerometer;

    // Variables to store acceleration data
    private float lastX, lastY, lastZ; // Store previous states

    private float deltaX = 0; // Store change in X
    private float deltaY = 0; // Store change in Y
    private float deltaZ = 0; // Store change in Z

    // Activity initialisation method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialise views
        initViews();

        // Initialise client networking operations
        initClientInstance();

        // Initialise built-in sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // Check if the device consists of the linear acceleration sensor type
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null)
        {
            // Device has an accelerometer, proceed with accelerometer setup
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        else
        {
            Log.d("Error: ", "This Device does not support the accelerometer");
        }
    }

    private void initViews()
    {
        glView = (GLView) findViewById(R.id.glView);
        accelX = (TextView) findViewById(R.id.accelX);
        accelY = (TextView) findViewById(R.id.accelY);
        accelZ = (TextView) findViewById(R.id.accelZ);
    }

    private void initClientInstance()
    {
        // Create client instance for communication with the localisation server
        client = new Client(8127,"192.168.1.7");
        client.listen(); // Listen for UDP packets sent from server
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        setDefaultAcceleration();

        displayAccelerationValues();

        deltaX = Math.abs(lastX - event.values[0]);
        deltaY = Math.abs(lastY - event.values[1]);
        deltaZ = Math.abs(lastZ - event.values[2]);

        String messageToServer = "AccelX: " + deltaX + " AccelY: " + deltaY + " AccelZ: " + deltaZ;
        client.setMessage(messageToServer);
        client.send();

    }

    private void setDefaultAcceleration()
    {
        accelX.setText("0.0");
        accelY.setText("0.0");
        accelZ.setText("0.0");
    }

    private void displayAccelerationValues()
    {
        accelX.setText(Float.toString(deltaX));
        accelY.setText(Float.toString(deltaY));
        accelZ.setText(Float.toString(deltaZ));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
