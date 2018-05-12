package com.example.user.sweethome.SocketConnection;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.example.user.sweethome.*;
import com.example.user.sweethome.Raspberry.DoorlockActivity;

import java.io.IOException;

import static com.example.user.sweethome.SocketConnection.ConnectionInfo.*;

/**
 * Created by USER on 2018-05-01.
 */

public class ReceiveThread extends Thread {

    private String TAG = "ReceiveThread";
    private boolean mCondition;
    private ICallback mCallback;
    private Activity mActivity;

    public ReceiveThread(Activity activity) {
        mActivity = activity;
    }

    public void run() {
        try {
            while (isConnected) {
                if (mIn == null) {
                    Log.d(TAG, "ReceiverThread: mIn is null");
                    break;
                }

                final String recvMessage = mIn.readLine();

                if (recvMessage != null) {
                    /* recvMessage 전달 */
                    if(mCondition && (mCallback != null)) {

                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mCallback.showToast(recvMessage);
                            }
                        });
                    }

                    Log.d(TAG, "recv message: " + recvMessage);


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

    public interface ICallback {
        void showToast(String msg);
    }

    public ReceiveThread() {
        mCondition = false;
        mCallback = null;
    }

    public void setCallback(ICallback callback) {
        this.mCallback = callback;
    }
}
