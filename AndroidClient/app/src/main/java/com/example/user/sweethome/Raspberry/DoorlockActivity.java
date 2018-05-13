package com.example.user.sweethome.Raspberry;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.sweethome.AlarmListActivity;
import com.example.user.sweethome.AnalogOnOffActivity;
import com.example.user.sweethome.MainActivity;
import com.example.user.sweethome.R;
import com.example.user.sweethome.SocketConnection.ConnectService;
import com.example.user.sweethome.SocketConnection.ConnectThread;
import com.example.user.sweethome.SocketConnection.ReceiveThread;
import com.example.user.sweethome.SocketConnection.SendThread;

public class DoorlockActivity extends AppCompatActivity {

    private Context context;
    private ConnectThread mConnectThread;
    private ReceiveThread mReceiveThread;
    private Messenger mServiceMessenger = null;
    private boolean mIsBound = false;
    private Intent serviceIntent;

    public TextView mConnectionStatus;
    private WebView mWebView;
    private Button mDoorOn, mDoorOff;
    private Button mButton1, mButton2, mButton3, mButton4;
    private final String url = "http://192.168.10.105:8080/stream.html";
    private final String TAG = "DoorlockActivity";

    private ServiceConnection mConnect = new ServiceConnection() {
        /* 서비스와 연결되면 실행됨 */
        @Override
        public void onServiceConnected(ComponentName classname, IBinder iBinder) {
            /* 메시지 주고 받기 */
            mServiceMessenger = new Messenger(iBinder);
            sendMessageToService(ConnectService.MSG_CONNECT_THREAD);
            // sendMessageToService(ConnectService.MSG_RECEIVE_THREAD);

            /* 뭐하는 건지 잘 모르겠음 */
            try {
                Message msg = Message.obtain(null, ConnectService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mServiceMessenger.send(msg);
            }

            catch(RemoteException e) {

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /* 메시지 받기 */
    private final Messenger mMessenger = new Messenger(new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.what) {

            }

            return false;
        }

    }));

    /* 메시지 보내기 */
    private void sendMessageToService(int type) {
        if(mIsBound) {
            if(mServiceMessenger != null) {
                try {
                    Message msg = null;
                    switch(type) {
                        case ConnectService.MSG_CONNECT_THREAD:
                            msg = Message.obtain(null, ConnectService.MSG_CONNECT_THREAD, mConnectThread);
                            Log.d(TAG, "send ConnectThread");
                            break;

                        /*
                        case ConnectService.MSG_RECEIVE_THREAD:
                            msg = Message.obtain(null, ConnectService.MSG_RECEIVE_THREAD, mReceiveThread);
                            Log.d(TAG, "send ReceiveThread");
                            break;
                        */
                    }

                    msg.replyTo = mMessenger;
                    mServiceMessenger.send(msg);

                } catch(RemoteException e) {
                    Log.d(TAG, "error while sending message");
                }
            }
        }
    }

    private void setStartService() {
        startService(new Intent(DoorlockActivity.this, ConnectService.class));
        bindService(new Intent(this, ConnectService.class), mConnect, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        Log.d(TAG, "bind to service");
    }

    private void setStopService() {
        if(mIsBound) {
            unbindService(mConnect);
            mIsBound = false;
        }

        stopService(new Intent(DoorlockActivity.this, ConnectService.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doorlock);
        context = getApplicationContext();

        mConnectionStatus = (TextView)findViewById(R.id.connection_status_textview);
        mWebView = (WebView) findViewById(R.id.webview);
        mDoorOn = (Button) findViewById(R.id.doorOn);
        mDoorOff = (Button) findViewById(R.id.doorOff);

        mButton1 = (Button) findViewById(R.id.button1);
        mButton2 = (Button) findViewById(R.id.button2);
        mButton3 = (Button) findViewById(R.id.button3);
        mButton4 = (Button) findViewById(R.id.button4);

        mReceiveThread = new ReceiveThread(this);
        mConnectThread = new ConnectThread(mReceiveThread, this);

        /* 콜백 함수 */
        ConnectThread.ICallback callback1 = new ConnectThread.ICallback() {
            @Override
            public void showText(String msg) {
                mConnectionStatus.setText(msg);
            }
        };

        mConnectThread.setCallback(callback1); /* 콜백함수 등록 */

        ReceiveThread.ICallback callback2 = new ReceiveThread.ICallback() {
            @Override
            public void showToast(String msg) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        };

        mReceiveThread.setCallback(callback2); /* 콜백함수 등록 */

        /* 서비스 시작 */
        setStartService();
        // sendMessageToService(ConnectService.MSG_CONNECT_THREAD);
        // sendMessageToService(ConnectService.MSG_RECEIVE_THREAD);

        /* 컴포넌트 설정 */
        showWebView();
        setMenuButton();
        setDoorButton();
    }

    public void showWebView() {
        mWebView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        /* enhance performance */
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDomStorageEnabled(true);
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.setScrollbarFadingEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        /*
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        */

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:(function() { " +
                        "document.getElementsByTagName('header')[0].style.display=xform; " +
                        "})()");
                super.onPageFinished(view, url);
            }
        });

        mWebView.loadUrl(url);
    }

    public void setDoorButton() {

        mDoorOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doorOpen(true);
            }
        });

        mDoorOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doorOpen(false);
            }
        });
    }


    public void setMenuButton() {

        /* button1: digitalLED */
        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

        /* button2: analogLED */
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AnalogOnOffActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

        /* button3: alarm */
        mButton3.setOnClickListener(new Button.OnClickListener() {
                                       public void onClick(View v) {
                                           Intent intent = new Intent(context, AlarmListActivity.class);
                                           intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                           startActivity(intent);
                                       }
                                   }

        );

        /* button4: door */
        mButton4.setOnClickListener(new Button.OnClickListener() {
                                        public void onClick(View v) {
                                            Intent intent = new Intent(context, DoorlockActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                            startActivity(intent);
                                        }
                                    }
        );
    }

    public void doorOpen(boolean open) {
        if(open)
            new Thread(new SendThread(1)).start();
        else
            new Thread(new SendThread(0)).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // setStopService();
    }

}