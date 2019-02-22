package honours.localisationclient;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import networking.Client;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    // Fields
    private GLView glView;
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

    // Activity initialisation method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        glView = (GLView) findViewById(R.id.glView);

        // Initialise client networking operations
        client = new Client(8127,"192.168.1.7");
        client.listen(); // Start listening for messages from the server
        client.setMessage("test");
        client.send();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
