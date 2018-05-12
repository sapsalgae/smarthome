package com.example.user.sweethome;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.user.sweethome.ConnectionManager.isConnected;
import static com.example.user.sweethome.ConnectionManager.mOut;

/**
 * Created by USER on 2018-01-30.
 */

public class CustomAdapter2 extends BaseAdapter {

    private int layoutID;
    final private String items[];
    private TextView text;
    private CheckBox box;
    private SeekBar seekBar;
    private LayoutInflater inflater;
    private Context context;

    public CustomAdapter2(Context context, int layoutId, String items[]) {
        this.context = context;
        this.layoutID = layoutId;
        this.items = items;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public String getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final String item = items[pos];
        int seekBarVal = 0;

        if (convertView == null) {
            convertView = inflater.inflate(layoutID, parent, false);

            /* 방 이름 */
            text = (TextView) convertView.findViewById(R.id.textView);
            text.setText(item);

            /* on off */
            box = (CheckBox) convertView.findViewById(R.id.checkBox);
            box.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (((CheckBox)v).isChecked()) {
                        // 불 켜기
                        if(!isConnected) {
                            Toast.makeText(context, "서버로 접속된 후 다시 해보세요", Toast.LENGTH_SHORT).show();
                        }

                        else {
                            //cs.setRoom(items[pos]);
                            new Thread(new SenderThread_d(item, true)).start();
                        }
                    }

                    else {
                        if(!isConnected) {
                            Toast.makeText(context, "서버로 접속된 후 다시 해보세요", Toast.LENGTH_SHORT).show();
                        }

                        else {
                            new Thread(new SenderThread_d(item, false)).start();
                        }
                    }
                }
            });

            /* seek bar */
            seekBar = (SeekBar) convertView.findViewById(R.id.seekBar);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    /* 아날로그 신호
                    if(isConnected) {
                        new Thread(new SenderThread_a(item, progress));
                    }
                    */
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if(isConnected) {
                        Toast.makeText(context, Integer.toString(seekBar.getProgress()), Toast.LENGTH_SHORT).show();
                        new Thread(new SenderThread_a(item, seekBar.getProgress())).start();
                    }

                    else {
                        Toast.makeText(context, "서버로 접속된 후 다시 해보세요", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        return convertView;
    }

    public class SenderThread_d implements Runnable {
        private String msg;
        private String led;
        private boolean light;

        SenderThread_d(String msg, boolean light) {
            this.msg = msg;
            this.light = light;
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

            try {

                Log.i("sender type: ", "2");
                Log.i("sender led: ", this.led);
                Log.i("sender light: ", Boolean.toString(light));

                mOut.println("2");
                mOut.println(this.led);
                Thread.sleep(1000);

                if (light)
                    mOut.println("1");

                else
                    mOut.println("0");

                mOut.flush();
            }

            catch(Exception e) {

            }
        }
    }

    public class SenderThread_a implements Runnable {
        private String msg;
        private String led;
        private int light;

        SenderThread_a(String msg, int light) {
            this.msg = msg;
            this.light = light;
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
                    led = "11";
                    break;
                case "거실":
                    led = "6";
                case "부엌":
                    led = "5";
                case "화장실":
                    led = "4"; // digital
                default:
                    break;

            }

            try {

                Log.i("sender type: ", "3");
                Log.i("sender led: ", this.led);
                Log.i("sender light: ", Integer.toString(light));

                mOut.println("3");
                mOut.println(this.led);
                Thread.sleep(1000);
                mOut.println(this.light);
                mOut.flush();
            }

            catch(Exception e) {

            }
        }
    }
}
