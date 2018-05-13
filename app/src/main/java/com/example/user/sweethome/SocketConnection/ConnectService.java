package com.example.user.sweethome.SocketConnection;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.example.user.sweethome.Raspberry.DoorlockActivity;
import static com.example.user.sweethome.SocketConnection.ConnectionInfo.*;

public class ConnectService extends Service {

    private String TAG = "ConnectService";
    private Thread mConnectThread;
    private Thread mReceiveThread;
    private ICallback mCallback;
    private Messenger mActivityMessenger = null;

    public static final int MSG_CONNECT_THREAD = 2;
    public static final int MSG_REGISTER_CLIENT = 3;

    private final Messenger mMessenger = new Messenger(new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.what) {
                case MSG_CONNECT_THREAD:
                    mConnectThread = (ConnectThread)msg.obj;
                    mConnectThread.start();
                    break;
            }

            return false;
        }
    }));

    @Override
    public void onCreate() {
        super.onCreate();
    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        // Log.d(TAG, "connecting to RaspberryServer");
        // new Thread(new ConnectThread()).start();

        return super.onStartCommand(intent, flags, startId);
    }

    public class ConnectServiceBinder extends Binder {
        public ConnectService getService() {
            return ConnectService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mMessenger.getBinder();
    }


    public interface ICallback {
        public void setThread(ConnectThread connectThread, ReceiveThread receiveThread);
    }

    public void registerCallback(ICallback callback) {
        mCallback = callback;
    }

    public void ConnectServiceFunc() {
        // 서비스에서 처리할 내용
    }
}
