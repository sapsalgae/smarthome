package com.example.user.sweethome;

import static com.example.user.sweethome.ConnectionManager.*;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.sweethome.Raspberry.DoorlockActivity;

public class MainActivity extends AppCompatActivity
{
    private static TextView mConnectionStatus;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;

    private static final String TAG = "TcpClient";
    private Context context;
    private Thread mReceiverThread = null;
    private ConnectionService cs;
    private boolean isService = false;

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

    public static void show(String msg) {
        mConnectionStatus.setText(msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context= getApplicationContext();

        String[] items = {"방1", "방2", "방3", "거실", "부엌", "화장실"};
        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        mConnectionStatus = (TextView)findViewById(R.id.connection_status_textview);
        button1 = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.button2);
        button3 = (Button)findViewById(R.id.button3);
        button4 = (Button)findViewById(R.id.button4);

        Intent intent = new Intent(context, ConnectionService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE); /* 서비스와 연결 시작 */

        if(isConnected) {
            show("connected to " + serverIP);
        }

        else {
            show("failed to connect to server " + serverIP);
        }

        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int i, long id) {

                        if(!isConnected) {
                            // showErrorDialog("서버로 접속된 후 다시 해보세요.");
                            Toast.makeText(context, "서버로 접속된 후 다시 해보세요", Toast.LENGTH_SHORT).show();
                        }

                        else {
                            String item = String.valueOf(parent.getItemAtPosition(i));
                            room = item;
                            new Thread(new SenderThread(item)).start();
                        }
                    }
                }
        );

        // button1 누르면, main 화면 (main화면이 아닐 때만)

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

        // button4: door
        button4.setOnClickListener(new Button.OnClickListener() {
                                       public void onClick(View v) {
                                           Intent intent = new Intent(context, DoorlockActivity.class);
                                           intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                           startActivity(intent);
                                       }
                                   }

        );
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        isConnected = false;
        unbindService(conn);
    }

    private static long back_pressed;
    @Override
    public void onBackPressed()
    {
        if(back_pressed + 2000 > System.currentTimeMillis())
        {
            super.onBackPressed();

            Log.d(TAG, "onBackPressed:");
            isConnected = false;
            finish();
        }

        else
        {
            Toast.makeText(getBaseContext(), "한번 더 뒤로가기를 누르면 종료됩니다.",  Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }
    }

    public class SenderThread implements Runnable {
        private String msg;
        private String led;

        SenderThread(String msg) {
            this.msg = msg;
        }

        @Override
        public void run()
        {
            switch(this.msg){
                case "방1":
                    led = "13";
                    break;
                case "방2":
                    led = "12";
                    break;
                case "방3":
                    led = "11"; // analog
                    break;
                case "거실":
                    led = "8";
                case "부엌":
                    led = "7";
                case "화장실":
                    led = "4";
                default:
                    break;

            }

            mOut.println("1");
            mOut.println(this.led);
            mOut.flush();
        }
    }

    public void showErrorDialog(String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("OK",  new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                finish();
            }
        });
        builder.create().show();
    }
}
