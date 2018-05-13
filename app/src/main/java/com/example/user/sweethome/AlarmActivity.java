package com.example.user.sweethome;

/**
 * Created by USER on 2018-01-19.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import static com.example.user.sweethome.AlarmDataManager.*;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TimePicker;
import android.widget.ToggleButton;


public class AlarmActivity extends Activity {

    final long oneday = 24 * 60 * 60 * 1000; /* 24시간 */
    Context context;
    AlarmMusicDialog alarmMusic;
    private long time = 0;
    private String musicFile;
    private AlarmManager mManager;
    private GregorianCalendar mCalendar;
    private TimePicker mTime;
    private NotificationManager mNotification;

    private Button okButton, cancelButton;
    private ToggleButton lightButton[];
    private ToggleButton weekButton[];
    private CheckBox repeat, sound, vibrate;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        context = getApplicationContext();
        createInstance();

        /* 완료 버튼 */
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                // (3) 조건 체크
                time = getTime();
                boolean[] week = setAlarm(time);
                addList(week); /* 알람 정보를 ListActivity에 넘겨서 리스트뷰에 반영하기 */
                // onBackPressed();
            }
        });

        /* 취소 버튼 */
        cancelButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                onBackPressed();
            }
        });

        // (2) 이 부분 musicFile 값이 제대로 리턴되는지 확인할 것
        sound.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(sound.isChecked()){
                    alarmMusic = new AlarmMusicDialog(context);
                }
            }
        });

        /* 리스트 목록을 눌렀을 때 */
        Intent intent = getIntent();

        if(intent != null) {
            intent.getIntExtra("alarmID", alarmID);
            getAlarmData(alarmID);
        }
    }

    public boolean[] setAlarm(long time) {
        boolean[] light = {lightButton[0].isChecked(), lightButton[1].isChecked(), lightButton[2].isChecked(),
                            lightButton[3].isChecked(), lightButton[4].isChecked(), lightButton[5].isChecked()};
        boolean[] week = {weekButton[0].isChecked(), weekButton[1].isChecked()
                                , weekButton[2].isChecked(), weekButton[3].isChecked()
                                , weekButton[4].isChecked(), weekButton[5].isChecked()
                                , weekButton[6].isChecked()};

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("alarmID", alarmID);
        intent.putExtra("light", light);
        intent.putExtra("week", week);
        intent.putExtra("repeat", repeat.isChecked());
        intent.putExtra("sound", sound.isChecked());
        intent.putExtra("vibrate", vibrate.isChecked());

        if(sound.isChecked()) {
            musicFile = alarmMusic.getMusicFile();
            intent.putExtra("file", musicFile);
        }

        PendingIntent pIntent = getPendingIntent(intent, alarmID);
        alarmID++;

        if(repeat.isChecked())
            mManager.setRepeating(AlarmManager.RTC_WAKEUP, time, oneday, pIntent);
        else
            mManager.set(AlarmManager.RTC_WAKEUP, time, pIntent);

        // saveAlarmData(alarmID, light, week, mCalendar.HOUR_OF_DAY, mCalendar.MINUTE, repeat.isChecked(), sound.isChecked(), vibrate.isChecked(), musicFile);

        return week;
    }

    public void saveAlarmData(int alarmID, boolean[] light, boolean[] week, int hour, int min, boolean repeat, boolean sound, boolean vibrate, String musicFile) {
        FileWriter fw = null;
        BufferedWriter bw = null;

        try {
            fw = new FileWriter("alarmData.txt", true);
            bw = new BufferedWriter(fw);
            bw.write(alarmID); bw.write(" ");

            for(boolean l: light) {
                bw.write(String.valueOf(l)); bw.write(" ");
            }

            for(boolean w: week) {
                bw.write(String.valueOf(w)); bw.write(" ");
            }

            bw.write(hour); bw.write(" ");
            bw.write(min); bw.write(" ");
            bw.write(String.valueOf(repeat)); bw.write(" ");
            bw.write(String.valueOf(sound)); bw.write(" ");
            bw.write(String.valueOf(vibrate)); bw.write(" ");
            bw.write(musicFile); bw.write("\n");

            bw.flush();
            bw.close();
        }

        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void getAlarmData(int alarmID) {
        FileReader fr = null;
        BufferedReader br = null;
        String s = null;
        String data[];

        try {
            fr = new FileReader("alarmData.txt");
            br = new BufferedReader(fr);

            while((s = br.readLine()) != null) {
                data = s.split(" ");

                if(Integer.parseInt(data[0]) == alarmID) {
                    setting(data);
                    break;
                }
            }

            br.close();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setting(String[] data) {
        boolean bool = false;

        try {
            for(int i = 1; i < 7; i++) {
                bool = Boolean.parseBoolean(data[i]);
                lightButton[i - 1].setChecked(bool);
            }

            for(int i = 7; i < 14; i++) {
                bool = Boolean.parseBoolean(data[i]);
                weekButton[i - 1].setChecked(bool);
            }

            mTime.setCurrentHour(Integer.parseInt(data[14]));
            mTime.setCurrentMinute(Integer.parseInt(data[15]));
            repeat.setChecked(Boolean.parseBoolean(data[16]));
            sound.setChecked(Boolean.parseBoolean(data[17]));
            vibrate.setChecked(Boolean.parseBoolean(data[18]));

            if(!data[19].equals("null")) {
                musicFile = data[19];
            }
        }

        catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void cancelAlarm(Context context, int alarmID) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pending = getPendingIntent(intent, alarmID);

        if(pending != null)
            alarmManager.cancel(pending);

        alarmManager.cancel(pending);
    }

    public void showData() {
        Intent intent = getIntent();

    }

    private long getTime() {
        // current time
        long atime = System.currentTimeMillis();
        long btime;

        // timepicker
        mCalendar.set(mCalendar.HOUR_OF_DAY, this.mTime.getCurrentHour());
        mCalendar.set(Calendar.MINUTE, this.mTime.getCurrentMinute());
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);
        btime = mCalendar.getTimeInMillis();

        if(atime > btime){
            btime += oneday;
        }

        return btime;
    }

    public void addList(boolean[] week){
        Intent intent = new Intent(this, AlarmListActivity.class);
        AlarmData alarmData = new AlarmData(alarmID, this.mTime.getCurrentHour(), this.mTime.getCurrentMinute(), week);
        // Log.d("AlarmActivity:addList", String.valueOf(mCalendar.HOUR_OF_DAY));
        // Log.d("AlarmActivity:addList", String.valueOf(mCalendar.MINUTE));
        AlarmDataManager.addAlarmData(alarmData);
        // intent.putExtra("alarmData", alarmData);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    private PendingIntent getPendingIntent(Intent intent, int id)
    {
        PendingIntent pIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pIntent;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void createInstance() {
        mNotification = (NotificationManager)getSystemService(NOTIFICATION_SERVICE); // (1)  이게 뭐지?
        mManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        mCalendar = new GregorianCalendar();
        Log.i("HelloAlarmActivity", mCalendar.getTime().toString());

        // alarmIDs = new ArrayList<Integer>();
        okButton = (Button)findViewById(R.id.ok);
        cancelButton = (Button)findViewById(R.id.cancel);

        lightButton = new ToggleButton[6];
        lightButton[0] = (ToggleButton) findViewById(R.id.light1);
        lightButton[1] = (ToggleButton) findViewById(R.id.light2);
        lightButton[2] = (ToggleButton) findViewById(R.id.light3);
        lightButton[3] = (ToggleButton) findViewById(R.id.light4);
        lightButton[4] = (ToggleButton) findViewById(R.id.light5);
        lightButton[5] = (ToggleButton) findViewById(R.id.light6);

        weekButton = new ToggleButton[7];
        weekButton[0] = (ToggleButton) findViewById(R.id.toggle_sun);
        weekButton[1] = (ToggleButton) findViewById(R.id.toggle_mon);
        weekButton[2] = (ToggleButton) findViewById(R.id.toggle_tue);
        weekButton[3] = (ToggleButton) findViewById(R.id.toggle_wed);
        weekButton[4] = (ToggleButton) findViewById(R.id.toggle_thu);
        weekButton[5] = (ToggleButton) findViewById(R.id.toggle_fri);
        weekButton[6] = (ToggleButton) findViewById(R.id.toggle_sat);

        mTime = (TimePicker)findViewById(R.id.timePicker);
        repeat = (CheckBox) findViewById(R.id.repeat);
        sound = (CheckBox) findViewById(R.id.sound);
        vibrate = (CheckBox) findViewById(R.id.vibrate);
        initiateAlarm();
    }

    public void initiateAlarm() {
        mTime.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
        mTime.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
        // mTime.setOnTimeChangedListener();

        /*
        mTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener(){
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute){
                updateDisplay(hourOfDay, minute);
            }
        });
        */
    }
    /*
    private void setAlarm(){
        mManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), pendingIntent());

        mTime.getCurrentHour();
        mTime.getCurrentMinute();
    }
    */

    /*
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute){
        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mCalendar.set(Calendar.MINUTE, minute);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);
        // mCalendar.set(mDate.getYear(), mDate.getMonth(), mDate.getDayOfMonth(), hourOfDay, minute);
        Log.i("HelloAlarmActivity", mCalendar.getTime().toString());
    }
    */
}
