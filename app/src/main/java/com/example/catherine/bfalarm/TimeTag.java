package com.example.catherine.bfalarm;

import java.io.Serializable;

/**
 * Created by catherine on 17/3/27.
 */

public class TimeTag implements Serializable {
    private String currentTime;
    private boolean isSun;
    private String acountName;
    private String calendarID;//用于存储事件ID

    public String getCalendarID() {
        return calendarID;
    }

    public void setCalendarID(String calendarID) {
        this.calendarID = calendarID;
    }

    public String getAcountName() {
        return acountName;
    }

    public void setAcountName(String acountName) {
        this.acountName = acountName;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public boolean isSun() {
        return isSun;
    }

    public void setSun(boolean sun) {
        isSun = sun;
    }

    @Override
    public boolean equals(Object obj) {
        String current = (String) obj;
        if (current.equals(getAcountName())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "TimeTag{" +
                "currentTime='" + currentTime + '\'' +
                ", isSun=" + isSun +
                ", acountName='" + acountName + '\'' +
                ", calendarID='" + calendarID + '\'' +
                '}';
    }
}
