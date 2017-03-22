package com.example.catherine.bfalarm;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;
import java.util.TimeZone;

public class AlarmActivity extends AppCompatActivity {

    private Button button;
    private Button setButton;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) this.findViewById(R.id.button);
        setButton = (Button) this.findViewById(R.id.set);
        calendar = Calendar.getInstance();
        requestPermission();
        setListener();
    }

    private void setListener() {
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlarmActivity.this, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, 0, intent, 0);
                //获取闹钟管理器
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
                Toast.makeText(AlarmActivity.this, "闹钟已经取消！", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void requestPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED ||
                (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR))) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CALENDAR,
                                Manifest.permission.WRITE_CALENDAR},
                        12);
            }
        }
    }

    private void setAutoAlarm() {
        //建立Intent和PendingIntent来调用闹钟管理器
        Intent intent = new Intent(AlarmActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, 0, intent, 0);
//
        // We want the alarm to go off 30 seconds from now.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 10);

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void setAlarm() {


//                calendar.setTimeInMillis(System.currentTimeMillis());
//                new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(TimePicker arg0, int h, int m) {
//                        //更新按钮上的时间
//                        setButton.setText(h + ":" + m);
//                        //设置日历的时间，主要是让日历的年月日和当前同步
//                        calendar.setTimeInMillis(System.currentTimeMillis());
//                        //设置日历的小时和分钟
//                        calendar.set(Calendar.HOUR_OF_DAY, h);
//                        calendar.set(Calendar.MINUTE, m);
//                        //将秒和毫秒设置为0
//                        calendar.set(Calendar.SECOND, 0);
//                        calendar.set(Calendar.MILLISECOND, 0);
//                        //建立Intent和PendingIntent来调用闹钟管理器
//                        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
//                        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
//                        //获取闹钟管理器
//                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//                        //设置闹钟
//                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
////                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 10 * 1000, pendingIntent);
//                        Toast.makeText(MainActivity.this, "设置闹钟的时间为：" + String.valueOf(h) + ":" + String.valueOf(m), Toast.LENGTH_SHORT).show();
//                    }
//                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();

    }

}
