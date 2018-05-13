package com.example.user.sweethome;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;

import java.io.File;
import java.util.Calendar;

import static android.app.Notification.DEFAULT_VIBRATE;
import static android.content.ContentValues.TAG;
import static com.example.user.sweethome.ConnectionManager.mOut;
import static com.example.user.sweethome.ConnectionManager.mSocket;

/**
 * Created by USER on 2018-01-20.
 */

public class AlarmReceiver extends BroadcastReceiver{

    private  NotificationManager notifier;
    private PendingIntent pintent;
    private int alarmID;
    private boolean[] light;
    private boolean[] week;
    private boolean repeat = false;
    private boolean sound = false;
    private boolean vibrate = false;

    @Override
    public void onReceive(Context context, Intent intent){

        Bundle extra = intent.getExtras();

        if(extra == null)
            return;

        alarmID = extra.getInt("alarmID");
        light = extra.getBooleanArray("light");
        week = extra.getBooleanArray("week");
        repeat = extra.getBoolean("repeat");
        sound = extra.getBoolean("sound");
        vibrate = extra.getBoolean("vibrate");
        Calendar cal = Calendar.getInstance();

        if(!week[cal.get(Calendar.DAY_OF_WEEK) - 1])
            return;

        notifier = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifier.notify(alarmID, getNotify(context));

        // 알람 울리기
        /*
            try{
                Intent i = new Intent(context, AlarmRingActivity.class);
                pintent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_ONE_SHOT);
                pintent.send();
            }
            catch(PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
            */

            if(light != null){
                Log.i("alarm", "thread start!");
                new Thread(new SenderThread(light)).start();
            }


            if(sound) {
                File file = new File(intent.getStringExtra("file"));
                MediaPlayer player = new MediaPlayer();

                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
                {
                    @Override
                    public void onPrepared(MediaPlayer mp){
                        mp.start();
                    }
                });

                try{
                    player.setDataSource(file.toString());
                    player.prepare();
                }

                catch(Exception e){
                    e.printStackTrace();
                }
            }
    }

    public Notification getNotify(Context context) {

        Notification.Builder builder = new Notification.Builder(context);
        builder.setAutoCancel(true);
        builder.setTicker("일어나!");
        builder.setContentTitle("sweetHome");
        builder.setContentText("알람이 울렸쬬요!!!");
        builder.setSmallIcon(R.drawable.kitten_on);
        // builder.setOngoing(true);

        if(sound)
            builder.setDefaults(Notification.DEFAULT_SOUND);
        if(vibrate) {
            //builder. enableVibration(true);
            //builder.setVibrationPattern(new long[]{200, 200, 500, 300});
            // builder.setDefaults(DEFAULT_VIBRATE);
            builder.setVibrate(new long[]{200, 200, 500, 300});
        }

        Notification notification = builder.build();

        return notification;
    }

    public class SenderThread implements Runnable {
        private boolean[] msg;
        private String led;

        SenderThread(boolean[] msg)
        {
            this.msg = msg;
        }

        @Override
        public void run(){
            for(int i = 0; i < msg.length; i++){
                if(msg[i]){
                    switch(i){
                        case 0:
                            led = "13";
                            Log.i(TAG, "led 13: " + Boolean.toString(msg[i]));
                            break;
                        case 1:
                            led = "12";
                            Log.i(TAG, "led 12: " + Boolean.toString(msg[i]));
                            break;
                        case 2:
                            led = "11";
                            Log.i(TAG, "led 11: " + Boolean.toString(msg[i]));
                            break;
                        case 3:
                            led = "10";
                        case 4:
                            led = "9";
                        case 5:
                            led = "8";
                        default:
                            break;
                    }

                    mOut.println("1");
                    mOut.println(this.led);
                    mOut.flush();

                    Log.i("alarm", this.led);

                    /*
                    try {
                        Thread.sleep(1000);
                    }

                    catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                    */
                }
            }
        }
    }
}
