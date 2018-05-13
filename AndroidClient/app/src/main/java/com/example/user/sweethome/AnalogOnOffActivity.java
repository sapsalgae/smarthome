package com.example.user.sweethome;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.user.sweethome.Raspberry.DoorlockActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static com.example.user.sweethome.ConnectionManager.isConnected;
import static com.example.user.sweethome.ConnectionManager.serverIP;

public class AnalogOnOffActivity extends AppCompatActivity {

    private ArrayList<String> list;
    private static TextView mConnectionStatus;
    private CustomAdapter2 mAdapter;
    private Button button1, button2, button3, button4;
    private ListView listView;
    private Context context;
    private ConnectionService cs;
    private boolean isService = false;
    final private String[] items = {"방1", "방2", "방3", "거실", "부엌", "화장실"};

    /* 서비스와 연결 (bind) */
    ServiceConnection conn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            ConnectionService.ConnectionBinder cb = (ConnectionService.ConnectionBinder) service;
            cs = cb.getService();
            isService = true;
        }

        public void onServiceDisconnected(ComponentName name) {
            isService = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analog_on_off);

        list = new ArrayList<String>();
        mConnectionStatus = (TextView) findViewById(R.id.connection_status_textview);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        listView = (ListView) findViewById(R.id.listView);
        context = getApplicationContext();

        if(isConnected) {
            mConnectionStatus.setText("connected to " + serverIP);
        }

        else {
            mConnectionStatus.setText("failed to connect to server " + serverIP);
        }

        mAdapter = new CustomAdapter2(this, R.layout.custom_list_view2, items);
        listView.setAdapter(mAdapter);

        // button1: digitalLED
        button1.setOnClickListener(new Button.OnClickListener() {
                                       public void onClick(View v) {
                                           Intent intent = new Intent(context, MainActivity.class);
                                           intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                           startActivity(intent);
                                       }
                                   }
        );

        // button2: analogLED
        button2.setOnClickListener(new Button.OnClickListener() {
                                       public void onClick(View v) {
                                           Intent intent = new Intent(context, AnalogOnOffActivity.class);
                                           intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                           startActivity(intent);
                                       }
                                   }
        );

        // button3: alarm
        button3.setOnClickListener(new Button.OnClickListener() {
                                       public void onClick(View v) {
                                           Intent intent = new Intent(context, AlarmListActivity.class);
                                           intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                           startActivity(intent);
                                       }
                                   }
        );

        button4.setOnClickListener(new Button.OnClickListener() {
                                       public void onClick(View v) {
                                           Intent intent = new Intent(context, DoorlockActivity.class);
                                           intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                           startActivity(intent);
                                       }
                                   }
        );
    }
}
