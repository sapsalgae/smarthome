package com.example.user.sweethome;

import java.io.Serializable;

/**
 * Created by USER on 2018-01-24.
 */

public class AlarmData {
    private int alarmID;
    private int hour;
    private int min;
    private boolean[] week;

    AlarmData(int alarmID, int hour, int min, boolean[] week) {
        this.alarmID = alarmID;
        this.hour = hour;
        this.min = min;
        this.week = week;
    }

    public void setAlarmID(int alarmID) {
        this.alarmID = alarmID;
    }

    public int getAlarmID() {
        return this.alarmID;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getHour() {
        return this.hour;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMin() {
        return this.min;
    }

    public void setWeek(boolean[] week) {
        this.week = week;
    }

    public boolean[] getWeek() {
        return this.week;
    }
}
