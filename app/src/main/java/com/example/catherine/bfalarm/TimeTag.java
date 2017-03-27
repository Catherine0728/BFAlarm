package com.example.catherine.bfalarm;

import java.io.Serializable;

/**
 * Created by catherine on 17/3/27.
 */

public class TimeTag implements Serializable {
    private String currentTime;
    private boolean isSun;

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
    public String toString() {
        return "TimeTag{" +
                "currentTime='" + currentTime + '\'' +
                ", isSun=" + isSun +
                '}';
    }
}
