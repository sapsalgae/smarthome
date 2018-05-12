package com.example.user.sweethome;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.shitij.goyal.slidebutton.SwipeButton;

public class AlarmRingActivity extends AppCompatActivity {

    private Context context;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    private SwipeButton swipeOffButton;
    private SwipeButton swipeOnButton;
    private Button mButton;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ring);
        // turnOnScreen();
        context = getApplicationContext();
        mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        swipeOffButton = (SwipeButton) findViewById(R.id.swipeOff);
        swipeOnButton = (SwipeButton) findViewById(R.id.swipeOn);
        mButton = (Button) findViewById(R.id.button);
        intent = getIntent();

        swipeOffButton.addOnSwipeCallback(new SwipeButton.Swipe() {
            @Override
            public void onButtonPress() {

            }

            @Override
            public void onSwipeCancel() {

            }

            @Override
            public void onSwipeConfirm() {
                // 알람 삭제

            }
        });

        swipeOnButton.addOnSwipeCallback(new SwipeButton.Swipe() {
            @Override
            public void onButtonPress() {

            }

            @Override
            public void onSwipeCancel() {

            }

            @Override
            public void onSwipeConfirm() {
                PendingIntent pintent = PendingIntent.getBroadcast(context, 0, intent, 0);
                AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 5, pintent); // Millisec * Second * Minute
            }
        });

        swipeOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });
    }

    public void turnOnScreen(){
        // turn on screen
        Log.v("AlarmRingActivity", "ON!");
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
        mWakeLock.acquire();
    }

    /*
    private void cancelAlarm()
    {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pending = getPendingIntent(intent);
        alarmManager.cancel(pending);
    }

    private PendingIntent getPendingIntent(Intent intent)
    {
        PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pIntent;
    }
    */
}
