package networking;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import database.Models;
import objects.Circle;

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

    public static float locationX;
    public static float locationY;

    public static Circle pointer;

    private Handler handler;

    // Constructor
    public Client(int port, String address, Handler handler)
    {
        this.handler = handler;
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
                            //Log.d("Message",message);
                            processLocationData(message);
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

    private void processLocationData(String message)
    {
        if(!message.contains("New location"))
        {
            return;
        }

        String strData = message.split(" ")[2]; // Remove header
        String[] values = strData.split(",");

        float posX = Float.parseFloat(values[0]);
        float posY = Float.parseFloat(values[1]);
        String macAddress = values[2];

        // Check whether the location corresponds to the local device or not
        // Ensure string formatting is in the same format i.e. both lower case
        if(macAddress.toLowerCase().equals(this.macAddress.toLowerCase()))
        {
            // Set location of this device
            locationX = posX;
            locationY = posY;
            if(pointer == null) {
                pointer = new Circle(0.05f, locationX, locationY, 20);
                pointer.setModel(Models.circle);
                pointer.setColour(0,0,1);
                Message threadMsg = new Message();
                threadMsg.what = 1;
                handler.sendMessage(threadMsg);
                return;
            }
            pointer.setX(locationX);
            pointer.setY(locationY);
            Message threadMsg = new Message();
            threadMsg.what = 1;
            handler.sendMessage(threadMsg);
            return;
        }

        PeerDevice peer = new PeerDevice(macAddress,posX,posY);
        PeerManager.registerOrUpdatePeerDevice(peer);



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
