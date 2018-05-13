package com.example.user.sweethome;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

//import static android.content.ContentValues.TAG;
import static com.example.user.sweethome.ConnectionManager.*;

public class ConnectionService extends Service {

    private IBinder mBinder = new ConnectionBinder();
    private Thread mReceiverThread = null;
    private Handler handler;
    private final String TAG = "connect";

    public ConnectionService() {
    }

    class ConnectionBinder extends Binder {
        ConnectionService getService() {

            if(!isConnected)
                new Thread(new ConnectionThread(serverIP, port)).start();
            return ConnectionService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        // throw new UnsupportedOperationException("Not yet implemented");

        if(!isConnected)
            new Thread(new ConnectionThread(serverIP, port)).start();

        return mBinder;
    }

    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        // this.mConnectionStatus = MainActivity.getConnectionStatus();
        // mConnectionStatus = (TextView)findViewById(R.id.connection_status_textview);
        new Thread(new ConnectionThread(serverIP, port)).start();
    }

    public void startConnection() {
        new Thread(new ConnectionThread(serverIP, port)).start();
    }

    /*
    public void setRoom(String room) {
        this.room = room;
    }
    */

    private class ConnectionThread implements Runnable
    {
        private String serverIP;
        private int serverPort;

        ConnectionThread(String ip, int port) {
            serverIP = ip;
            serverPort = port;

            // mConnectionStatus.setText("connecting to " + serverIP + "......");
            MainActivity.show("connecting to " + serverIP + "......");
            /* messenger 방법
            sendMsgToActivity("connecting to " + serverIP + "......");
            */
        }

        @Override
        public void run() {
            try {
                mSocket = new Socket(serverIP, serverPort);
                mServerIP = mSocket.getRemoteSocketAddress().toString();
            } catch (UnknownHostException e) {
                Log.d(TAG, "ConnectThread: can't find host");
            } catch (SocketTimeoutException e) {
                Log.d(TAG, "ConnectThread: timeout");
            } catch (Exception e) {
                Log.e(TAG, ("ConnectThread: " + e.getMessage()));
            }

            if (mSocket != null) {
                try {
                    mOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream(), "UTF-8")), true);
                    mIn = new BufferedReader(new InputStreamReader(mSocket.getInputStream(), "UTF-8"));
                    ConnectionManager.isConnected = true;
                }

                catch (IOException e) {
                    Log.e(TAG, ("ConnectThread: " + e.getMessage()));
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (isConnected) {
                        Log.d(TAG, "connected to " + serverIP);
                        // mConnectionStatus.setText("connected to " + serverIP);
                        MainActivity.show("connected to " + serverIP);

                        mReceiverThread = new Thread(new ReceiverThread());
                        mReceiverThread.start();
                    } else {
                        Log.d(TAG, "failed to connect to server " + serverIP);
                        // mConnectionStatus.setText("failed to connect to server " + serverIP);
                        MainActivity.show("failed to connect to server " + serverIP);
                    }
                }
            });

            /*
            if(isConnected) {
                mReceiverThread = new Thread(new ConnectionService.ReceiverThread());
                mReceiverThread.start();
                // isConnected 전달

                Log.d(TAG, "connected to " + serverIP);

                // MainActivity에서 실행
                if(mConnectionStatus != null) {
                    mConnectionStatus.setText("connected to " + serverIP);

                }
            }

            else {
                Log.d(TAG, "failed to connect to server " + serverIP);

                // MainActivity에서 실행
                if(mConnectionStatus != null)
                    mConnectionStatus.setText("failed to connect to server " + serverIP);
            }
            */

            /*
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (isConnected) {
                        Log.d(TAG, "connected to " + serverIP);

                        // MainActivity에서 실행
                        mConnectionStatus.setText("connected to " + serverIP);


                        mReceiverThread = new Thread(new ConnectionService.ReceiverThread());
                        mReceiverThread.start();
                    } else {
                        Log.d(TAG, "failed to connect to server " + serverIP);

                        // MainActivity에서 실행
                        mConnectionStatus.setText("failed to connect to server " + serverIP);

                    }
                }
            });
            */
        }
    }

    public class SenderThread implements Runnable {
        private String msg;
        private String led;

        SenderThread(String msg)
        {
            this.msg = msg;
        }

        @Override
        public void run()
        {
            switch(this.msg){
                case "방1":
                    led = "11";
                    break;
                case "방2":
                    led = "12";
                    break;
                case "방3":
                    led = "13";
                    break;
                case "거실":
                    led = "10";
                case "부엌":
                    led = "9";
                case "화장실":
                    led = "8";
                default:
                    break;

            }

            mOut.println(this.led);
            mOut.flush();
        }
    }

    private class ReceiverThread implements  Runnable {
        public void run() {
            try {
                while (isConnected) {
                    if (mIn == null) {
                        Log.d(TAG, "ReceiverThread: mIn is null");
                        break;
                    }

                    final String recvMessage = mIn.readLine();

                    if (recvMessage != null) {
                        // recvMessage 전달

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // recvMessage 전달
                                Toast.makeText(ConnectionService.this, room + " " + recvMessage, Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "recv message: " + recvMessage);
                                // mConversationArrayAdapter.insert(mServerIP + " - " + recvMessage, 0);
                            }

                        });

                    }

                }

                Log.d(TAG, "ReceiverThread: thread has exited");

                if (mOut != null) {
                    mOut.flush();
                    mOut.close();
                }

                mIn = null;
                mOut = null;

                if (mSocket != null) {
                    try {
                        mSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "ReceiverThread: " + e);

            }
        }
    }

    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }
}
