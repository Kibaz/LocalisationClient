package networking;

import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * Created by Marcus on 07/02/2019.
 */

public class Client {

    // Constants
    private static final int BYTE_BUFFER_LENGTH = 512;

    // Fields
    private DatagramSocket socket; // UDP socket for sending and receiving data
    private int serverPort;
    private InetAddress serverAddress;

    private String message; // Store message to be sent

    private AsyncTask<Void,Void,Void> listenTask;
    private AsyncTask<Void,Void,Void> respondTask;

    private String macAddress;

    // Constructor
    public Client(int port, String address)
    {
        this.serverPort = port;
        try {
            this.serverAddress = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


    /*
     * Method for listening for packets of data
     * Sent from the localisation server
     */
    public void listen()
    {
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        // Run Asynchronous Task in the background of regular processing
        listenTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                if(socket != null)
                {
                    try
                    {
                        while(!socket.isClosed())
                        {
                            byte[] buffer = new byte[BYTE_BUFFER_LENGTH];
                            DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
                            socket.receive(packet);
                            String message = new String(packet.getData(),0,packet.getLength());
                            Log.d("Message", message);
                        }
                    } catch(IOException e)
                    {
                        e.printStackTrace();
                    }

                }

                return null;
            }

            protected void onPostExecute(Void result)
            {
                super.onPostExecute(result);
            }
        };

        if(Build.VERSION.SDK_INT >= 11)
        {
            listenTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else
        {
            listenTask.execute();
        }
    }

    private void processLocationData()
    {

    }

    /*
     * Method for sending bytes of data
     * To the localisation server
     */
    public void send()
    {
        respondTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                // Create packet to be sent using submitted data and server details
                byte[] data = message.getBytes();
                DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress,serverPort);
                try {
                    socket.send(packet); // Send packet data to server
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            protected void onPostExecute(Void result)
            {
                super.onPostExecute(result);
            }
        };

        if(Build.VERSION.SDK_INT >= 11)
        {
            respondTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else
        {
            respondTask.execute();
        }

    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getMACAddress()
    {
        return macAddress;
    }

    public void setMACAddress(String address)
    {
        this.macAddress = address;
    }

}
