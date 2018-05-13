package com.example.user.sweethome;

import java.util.ArrayList;

/**
 * Created by USER on 2018-01-24.
 */

public class AlarmDataManager {
    private static ArrayList<AlarmData> mList = new ArrayList<AlarmData>();
    // private static ArrayList<Integer> alarmIDList;
    static int alarmID = 0;

    public static void addAlarmData(AlarmData data) {
        mList.add(data);
    }

    public static ArrayList<AlarmData> getAlarmDataList() {
        return mList;
    }

    /*
    public static void addAlarmID(Integer alarmID) {
        alarmIDList.add(alarmID);
    }

    public static ArrayList<Integer> getAlarmIDList() {
        return alarmIDList;
    }

    public static void setAlarmID(int alarmID) {
        alarmID = alarmID;
    }

    public static int getAlarmID() {
        return alarmID;
    }
    */
}
