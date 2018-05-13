package com.example.user.sweethome;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by USER on 2018-01-23.
 */

public class CustomAdapter extends BaseAdapter {

    private ArrayList<AlarmData> mList;
    private TextView text1;
    private TextView text2;
    private CheckBox box;
    private Context context;
    private int layoutID;
    private LayoutInflater inflater;

    public CustomAdapter(Context context, int layoutID) {
        this.context = context;
        this.layoutID = layoutID;
        this.mList = AlarmDataManager.getAlarmDataList();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.mList.size();
    }

    @Override
    public AlarmData getItem(int position) {
        return this.mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final String day = "일월화수목금토";
        int i = 0;
        StringBuilder time = new StringBuilder();
        StringBuilder week = new StringBuilder();

        if(convertView == null) {
            convertView = inflater.inflate(layoutID, parent, false);

            /* 시간 세팅 */
            text1 = (TextView) convertView.findViewById(R.id.text1);
            time.append(String.valueOf(this.mList.get(position).getHour()));
            time.append(":");
            time.append(String.valueOf(this.mList.get(position).getMin()));
            text1.setText(time.toString());

            /* 요일 세팅 */
            text2 = (TextView) convertView.findViewById(R.id.text2);
            for(boolean w: this.mList.get(position).getWeek()) {
                if(w) {
                    week.append("<b>");
                    week.append(day.charAt(i++));
                    week.append("</b>");
                }

                else {
                    week.append(day.charAt(i++));
                }

                week.append(" ");
            }

            text2.setText(Html.fromHtml(week.toString()));

            box = (CheckBox) convertView.findViewById(R.id.checkbox);
            box.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if(box.isChecked()) {
                        // 알람 재등록
                    }

                    else {
                        // 알람 취소
                    }
                }
            });

            /* 리스트 뷰 아이템을 길게 누르면 발생하는 이벤트를 AlarmListActivity에서 처리해도 되는지 잘 모르겠음
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // AlarmActivity로 넘어가기
                }
            });

            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // 삭제
                    return true;
                }
            });
            */
        }

        return convertView;
    }

    public void add(AlarmData msg) {
        this.mList.add(msg);
        AlarmDataManager.addAlarmData(msg);
    }

    public void remove(int position) {
        this.mList.remove(position);
        AlarmDataManager.getAlarmDataList().remove(position);
    }
}
