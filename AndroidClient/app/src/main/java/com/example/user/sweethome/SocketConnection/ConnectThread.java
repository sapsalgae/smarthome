package com.example.user.sweethome.SocketConnection;


import android.app.Activity;
import android.util.Log;

import com.example.user.sweethome.Raspberry.DoorlockActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import static com.example.user.sweethome.SocketConnection.ConnectionInfo.*;

/**
 * Created by USER on 2018-04-24.
 */

public class ConnectThread extends Thread {

    private boolean mCondition;
    private ICallback mCallback;
    private Thread mReceiveThread;
    private final String TAG = "ConnectThread";
    private Activity mActivity;

    public ConnectThread(ReceiveThread receiveThread, Activity activity) {
        mCondition = true;
        mCallback = null;
        mReceiveThread = receiveThread;
        mActivity = activity;
    }

    @Override
    public void run() {
        try {
            CallBack("connecting to " + serverIP + "......");
            /*
            Log.d(TAG, "connecting to RaspberryServer");

            if(mCondition && (mCallback != null)) {
                mCallback.showText("connecting to " + serverIP + "......");
            }
            */

            mSocket = new Socket(serverIP, serverPort);
            // serverIP = mSocket.getRemoteSocketAddress().toString();
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
                isConnected = true;
            }

            catch (IOException e) {
                Log.e(TAG, ("ConnectThread: " + e.getMessage()));
            }
        }

        if (isConnected) {
            CallBack("connected to " + serverIP);
            mReceiveThread.start();
        } else {
            CallBack("failed to connect to server " + serverIP);

            /*
            if(mCondition && (mCallback != null)) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.showText("failed to connect to server " + serverIP);
                    }
                });
            }

            Log.d(TAG, "failed to connect to server " + serverIP);
            */
        }
    }

    public interface ICallback {
        void showText(String msg);
    }

    public void setCallback(ICallback callback) {
        this.mCallback = callback;
    }

    public void CallBack(final String str) {
        if(mCondition && (mCallback != null)) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCallback.showText(str);
                }
            });
        }

        Log.d(TAG, str);

    }
}
