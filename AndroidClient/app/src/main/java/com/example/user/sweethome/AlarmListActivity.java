package com.example.user.sweethome;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.sweethome.Raspberry.DoorlockActivity;

import java.util.GregorianCalendar;

import static com.example.user.sweethome.ConnectionManager.*;

public class AlarmListActivity extends AppCompatActivity{

    private Context context;
    private FloatingActionButton fab;
    private TextView mConnectionStatus;
    private ListView listView;
    private Button button1, button2, button3, button4;
    private GregorianCalendar mCalendar;
    private CustomAdapter mCustomAdapter;
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

    public void show(String msg) {
        mConnectionStatus.setText(msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        context = getApplicationContext();
        mCustomAdapter = new CustomAdapter(this, R.layout.custom_list_view);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        mConnectionStatus = (TextView)findViewById(R.id.connection_status_textview);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        listView = (ListView)findViewById(R.id.listView);

        Intent intent = new Intent(context, ConnectionService.class);
        // bindService(intent, conn, Context.BIND_AUTO_CREATE); /* 서비스와 연결 시작 */

        if(isConnected) {
            show("connected to " + serverIP);
        }

        else {
            show("failed to connect to server " + serverIP);
        }

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(mCustomAdapter);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AlarmActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

        // button1: digitalLED
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AnalogOnOffActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, AlarmActivity.class);
                intent.putExtra("alarmID", position);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if (position != ListView.INVALID_POSITION) {
                    mCustomAdapter.remove(position);
                    listView.clearChoices();
                    mCustomAdapter.notifyDataSetChanged();
                    Toast.makeText(AlarmListActivity.this, "Deleted", Toast.LENGTH_LONG).show();
                    cancelAlarm(AlarmListActivity.this, position);

                    return true;
                }

                return false;
            }
        });

    }

    /*
    private void addList(){
        Intent intent = getIntent();
        GregorianCalendar mCalendar = intent.getParcelableExtra("time");
        boolean week[] = intent.getBooleanArrayExtra("week");
        StringBuilder item = new StringBuilder();
        String hour = Integer.toString(mCalendar.HOUR_OF_DAY);
        String min = Integer.toString(mCalendar.MINUTE);
        String weeks = "일 월 화 수 목 금 토"; // bold 효과 주기
        ListView mListView = (ListView) findViewById(R.id.listView);

        item.append(hour); item.append(":"); item.append(min);
        item.append("    "); item.append(weeks);
        mCustomAdapter.add(item.toString());
    }
    */

    public void cancelAlarm(Context context, int alarmID)
    {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pending = getPendingIntent(intent, alarmID);

        if(pending != null)
            alarmManager.cancel(pending);
    }

    private PendingIntent getPendingIntent(Intent intent, int id)
    {
        PendingIntent pIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pIntent;
    }
}
