package honours.localisationclient;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

import networking.Client;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // Apply a noise threshold for the data to be sent
    // This was taken from observed data
    private static final float NOISE_THRESHOLD = 0.2f;

    // Views
    private GLView glView;

    private TextView location;
    private TextView numClientsText;

    // Networking
    private Client client;
    private WifiManager wifiManager;
    private Timer timer = new Timer();

    /*
     * Using Android's built-in accelerometer to acquire
     * acceleration data as input to the localisation server's
     * filtering and data processing algorithm. This will handle a
     * combination of RSSI data measured by 3 monitoring devices and
     * the acceleration data acquired by the local mobile device.
     */
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gravitySensor;
    private Sensor magnetometer;

    // Variables to store acceleration data
    private float lastX, lastY, lastZ; // Store previous states

    private float deltaX = 0; // Store change in X
    private float deltaY = 0; // Store change in Y
    private float deltaZ = 0; // Store change in Z

    private float[] gravity_values = new float[4];
    private float[] magnetic_values = new float[4];
    private float[] linear_accel_vals = new float[4];

    private float[] rotationMatrix = new float[16];
    private float[] I = new float[16];

    private float[] earthAccel = new float[4];

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
        // Check if the device consists of an accelerometer sensor type
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null) {
            // Device has an accelerometer, proceed with accelerometer setup
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Log.d("Error: ", "This Device does not support the accelerometer");
        }

        // Check if the device consists of an magnetometer
        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            // Device has an accelerometer, proceed with accelerometer setup
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Log.d("Error: ", "This Device does not support the magnetometer");
        }

        // Check if the device supports a sensor of gravitational pull
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null) {
            // Device has an accelerometer, proceed with accelerometer setup
            gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
            sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Log.d("Error: ", "This Device does not support the gravity sensor");
        }

        // Continuously scan wifi
        timer.schedule(new TimerTask()
        {
            @Override
            public void run() {
                scanWifi();
            }
        },0,100);

    }

    private void initViews() {
        // Initialise OpenGL View
        glView = (GLView) findViewById(R.id.glView);

        // Initialise all text views
        location = new TextView(this);
        location.setText("Location: ");
        location.setTextColor(Color.WHITE);
        location.setTextSize(20);

        numClientsText = new TextView(this);
        numClientsText.setText("People in area: ");
        numClientsText.setTextColor(Color.WHITE);
        numClientsText.setTextSize(20);

        // Text View Positioning using FrameLayout
        FrameLayout.LayoutParams locationParams = new FrameLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        locationParams.leftMargin = 10;
        locationParams.topMargin = 5;
        FrameLayout.LayoutParams numClientsParams = new FrameLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        numClientsParams.leftMargin = 10;
        numClientsParams.topMargin = 45;
        
        // Add Text Views as overlay on OpenGL view
        addContentView(location, locationParams);
        addContentView(numClientsText, numClientsParams);

    }

    private void initClientInstance() {
        // Create client instance for communication with the localisation server
        client = new Client(8127, "192.168.1.7");
        client.listen(); // Listen for UDP packets sent from server

        // Retrieve MAC Address from device
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled())
        {
            Toast.makeText(this,"WiFi is disabled ... We need to enable it",Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        client.setMACAddress(wifiInfo.getMacAddress());
    }

    private void scanWifi()
    {
        wifiManager.startScan();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        switch (event.sensor.getType()) {
            // Retrieve linear acceleration values (i.e. acceleration minus gravity)
            case Sensor.TYPE_LINEAR_ACCELERATION:
                linear_accel_vals = lowPassFilter(event.values.clone(),linear_accel_vals);

                // Convert the Android System coordinates to real world coordinates system
                convertSystemCoordsToRealWorldCoords();
                /*
                // If reasonable values have been obtained from the sensors
                // Send the acceleration values to the server for processing
                String message = "Acceleration;" + earthAccel[0] + "," + earthAccel[1] + "," + earthAccel[2] + "," + client.getMACAddress();
                client.setMessage(message); // Configure message to be sent
                client.send(); // Send message to the server
                */

                break;
            // Retrieve gravitational effect on each axis
            case Sensor.TYPE_GRAVITY:
                gravity_values[0] = event.values[0];
                gravity_values[1] = event.values[1];
                gravity_values[2] = event.values[2];
                gravity_values[3] = 0;
                break;
            // Retrieve magnetometer values on each axis - required for generating rotation matrix
            case Sensor.TYPE_MAGNETIC_FIELD:
                magnetic_values[0] = event.values[0];
                magnetic_values[1] = event.values[1];
                magnetic_values[2] = event.values[2];
                magnetic_values[3] = 0;
                break;
        }

    }

    private float[] lowPassFilter(float input[], float output[])
    {
        if(output == null)
        {
            return input;
        }

        for(int i = 0; i < input.length; i++)
        {
            output[i] = output[i] + NOISE_THRESHOLD * (input[i] - output[i]);
        }

        return output;


    }

    private void convertSystemCoordsToRealWorldCoords() {
        /*
         * Create rotation matrix based on the values retrieved
         * from the magnetometer and gravity sensor
         */
        SensorManager.getRotationMatrix(rotationMatrix, I, gravity_values, magnetic_values);
        float[] inversion = new float[16]; // Store inverted rotation matrix
        // Carry out inversion of rotation matrix using openGL code
        android.opengl.Matrix.invertM(inversion, 0, rotationMatrix, 0);
        // Multiply inverted rotation matrix with the acceleration values (without gravity)
        // Store this in an array of float values
        android.opengl.Matrix.multiplyMV(earthAccel, 0, inversion, 0, linear_accel_vals, 0);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
