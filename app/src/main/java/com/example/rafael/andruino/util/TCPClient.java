package com.example.rafael.andruino.util;

/**
 * Created by rapha on 3/15/2016.
 */

import android.os.AsyncTask;
import android.util.Log;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class TCPClient {
    private OnMessageReceived mMessageListener = null;
    private OnConnectionListener mConnectionListener = null;

    private ConnectTask connectTask = null;

    //Server config
    private String serverIp;
    private String serverMessage;
    private int serverPort = 23;

    //Buffers
    PrintWriter outBuffer;
    BufferedReader inBuffer;

    public TCPClient(OnMessageReceived messageListener, OnConnectionListener connectionListener) {
        mMessageListener = messageListener;
        mConnectionListener = connectionListener;
    }

    //Set ip to connect
    public void setIp(String ip) {
        serverIp = ip;
    }

    //Register listener to be called when message arrives
    public void registerMessageListener(OnMessageReceived messageListener) {
        mMessageListener = messageListener;
    }

    public void registerConnectionListener(OnConnectionListener connectionListener) {
        mConnectionListener = connectionListener;
    }

    //Send message
    public void sendMessage(String message) {
        if (outBuffer != null && !outBuffer.checkError()) {
            outBuffer.println(message);
            outBuffer.flush();
        }
    }

    //Start client
    public void startClient() {
        if (connectTask != null) {
            stopClient();
        }

        connectTask = new ConnectTask();
        connectTask.execute(serverIp);
    }

    //Stop client
    public void stopClient() {
        if (connectTask != null) {
            connectTask.clearSocket();
            connectTask.cancel(true);
            connectTask = null;
        }

        if (mConnectionListener != null) {
            mConnectionListener.onDisconnect();
        }
    }

    //Declare the interface. The method messageReceived(String message) will must be implemented inBuffer the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        void messageReceived(String message);
    }

    public interface OnConnectionListener {
        void onConnect();

        void onError();

        void onDisconnect();
    }

    public class ConnectTask extends AsyncTask<String, String, Void> {
        private Socket socket;

        public void clearSocket() {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(String... serverIp) {
            Log.d("ANDRUINO", "doInBackground");

            try {
                //here you must put your computer's IP address.
                InetAddress serverAddr = InetAddress.getByName(serverIp[0]);

                //create a socket to make the connection with the server
                socket = new Socket(serverAddr, serverPort);

                if (mConnectionListener != null) {
                    mConnectionListener.onConnect();
                }

                try {
                    //send the message to the server
                    outBuffer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                    Log.d("ANDRUINO", "C: Done.");

                    //receive the message which the server sends back
                    inBuffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    //in this while the client listens for the messages sent by the server
                    while (!isCancelled()) {
                        serverMessage = inBuffer.readLine();

                        if (serverMessage != null && mMessageListener != null) {
                            //call the method messageReceived from MyActivity class
                            Log.d("ANDRUINO", "S: Received Message: '" + serverMessage + "'");
                            mMessageListener.messageReceived(serverMessage);
                        }
                        serverMessage = null;
                    }
                } catch (Exception e) {
                    inBuffer.close();
                    outBuffer.close();

                    inBuffer = null;
                    outBuffer = null;

                    Log.d("ANDRUINO", "S: Error", e);
                }

            } catch (Exception e) {
                e.printStackTrace();

                if (mConnectionListener != null) {
                    mConnectionListener.onError();
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Log.d("ANDRUINO", "onProgressUpdate");
            super.onProgressUpdate(values);

            if (mMessageListener != null) {
                mMessageListener.messageReceived(values[0]);
            }
        }
    }
}