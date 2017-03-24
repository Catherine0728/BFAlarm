package com.example.catherine.bfalarm;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import android.provider.CalendarContract.Calendars;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    //Android2.2版本以后的URL，之前的就不写了

    private static String CALANDER_URL = "content://com.android.calendar/calendars";
    private static String CALANDER_EVENT_URL = "content://com.android.calendar/events";
    private static String CALANDER_REMIDER_URL = "content://com.android.calendar/reminders";

    String sevenEventID;
    String tenEventID;
    String[] acountName = {"boxfish", "盒子鱼"};
    String[] alarmContent = {"嗨，同学，想知道今天有哪些好玩儿的任务吗？［坏笑］快点击进来看看吧～", "啊，已经10点啦，再不完成今天的任务就要睡觉咯～"};
    private String tag = "盒子鱼";

    int[] clockTime = {19, 22};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        setListener();
    }

    private void setListener() {
        findViewById(R.id.initButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initAcount();
            }
        });

        findViewById(R.id.readUserButton).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkCurrentAcount();
                    }
                });

        findViewById(R.id.readEventButton).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //读取事件
                        Cursor eventCursor = getContentResolver().query(Uri.parse(CALANDER_EVENT_URL), null, null, null, null);
                        boolean hasEvent = false;
                        int count = eventCursor.getCount();
                        System.out.println("共有" + count + "个事件");
                        if (count > 0) {
                            for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
//                        eventCursor.moveToLast();             //注意：这里与添加事件时的账户相对应，都是向最后一个账户添加
                                String eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
                                String calendarID = eventCursor.getString(eventCursor.getColumnIndex(CalendarContract.Events.CALENDAR_ID));
//                                String sevenAlarmTitle = currentTime + acountName[0];
//                                String tenAlarmTitle = currentTime + acountName[1];
                                if (eventTitle.equals(acountName[0])) {
                                    System.out.println("查询事件＝＝＝＝》" + eventTitle + "calendarID==>" + calendarID);
                                    hasEvent = true;
                                } else if (eventTitle.equals(acountName[1])) {
                                    System.out.println("查询事件＝＝＝＝》" + eventTitle + "calendarID==>" + calendarID);
                                    System.out.println(calendarID);
                                    hasEvent = true;
                                }
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "请先添加事件!!!", Toast.LENGTH_SHORT).show();
                        }
                        if (!hasEvent) {
                            Toast.makeText(MainActivity.this, "请先添加事件!!!", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

        findViewById(R.id.writeEventButton).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 获取要出入的gmail账户的id
                        Cursor userCursor = getContentResolver().query(Uri.parse(CALANDER_URL), null, null, null, null);
                        Cursor eventCursor = getContentResolver().query(Uri.parse(CALANDER_EVENT_URL), null, null, null, null);
                        int count = userCursor.getCount();
                        if (count > 0) {
                            int iSeven = 0;
                            int iTen = 0;
                            for (userCursor.moveToFirst(); !userCursor.isAfterLast(); userCursor.moveToNext()) {
                                String calId = userCursor.getString(userCursor.getColumnIndex("_id"));
                                String name = userCursor.getString(userCursor.getColumnIndex(Calendars.ACCOUNT_NAME));
                                ContentValues contentValues = new ContentValues();

                                if (name.contains(acountName[0]) || name.contains(acountName[1])) {
                                    boolean hasCurrentEvent = false;
                                    if (name.contains(acountName[0])) {
                                        String currentTime = getCurrentTime(iSeven);
                                        if (eventCursor.getCount() > 0) {
                                            for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                                                String title = eventCursor.getString(eventCursor.getColumnIndex("title"));
                                                String calendarId = eventCursor.getString(eventCursor.getColumnIndex("calendar_id"));
                                                System.out.println(calendarId);
                                                if (calendarId.equals(calId)) {
                                                    hasCurrentEvent = true;
                                                    System.out.println("已经有" + calendarId + "这个事件了");
                                                }
                                            }
                                        } else {
                                            System.out.println("已经存在这些账户：" + name + "===calId: " + calId);
                                            contentValues.put("title", currentTime + acountName[0]);
                                            contentValues.put("description", alarmContent[0]);
                                            // 插入账户
                                            contentValues.put("calendar_id", calId);
                                            contentValues.put("eventLocation", "北京");

                                            Calendar mCalendar = Calendar.getInstance();
                                            mCalendar.setTimeInMillis(System.currentTimeMillis());
                                            String week = String.valueOf(mCalendar.get(Calendar.DAY_OF_WEEK));//得到当前是星期几，如果是周末，那么就不需要设置账户
                                            mCalendar.add(Calendar.DAY_OF_MONTH, iSeven);
                                            mCalendar.set(Calendar.HOUR_OF_DAY, clockTime[0]);
                                            mCalendar.set(Calendar.MINUTE, 0);
                                            long start = mCalendar.getTime().getTime();
                                            mCalendar.set(Calendar.MINUTE, 1);
//                                        mCalendar.set(Calendar.HOUR_OF_DAY, clockTime[j]+1);
//                                        mCalendar.add(Calendar.MINUTE, clockTime[j] + time);
                                            long end = mCalendar.getTime().getTime();

                                            contentValues.put(CalendarContract.Events.DTSTART, start);
//                                        contentValues.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;WKST=SU;BYDAY=MO,TU,WE,TH,FR,SA");
                                            contentValues.put(CalendarContract.Events.DTEND, end);
                                            contentValues.put(CalendarContract.Events.HAS_ALARM, true);//设置有闹钟提醒
                                            contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Shanghai");  //这个是时区，必须有，
                                            contentValues.put(CalendarContract.Events.STATUS, 1);
                                            //添加事件
                                            Uri newEvent = getContentResolver().insert(Uri.parse(CALANDER_EVENT_URL), contentValues);
                                            if (newEvent == null) {
                                                //添加日历事件失败直接返回
                                                Toast.makeText(MainActivity.this, "添加失败!!!", Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            //事件提醒的设定
                                            ContentValues values = new ContentValues();
                                            values.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
                                            values.put(CalendarContract.Reminders.MINUTES, 1);  //提前一天有提醒  1440
                                            // 提前10分钟有提醒
                                            values.put(CalendarContract.Reminders.METHOD, 1);


                                            Uri uriNew = getContentResolver().insert(Uri.parse(CALANDER_REMIDER_URL), values);
                                            if (uriNew == null) {
                                                //添加闹钟提醒失败直接返回
                                                Toast.makeText(MainActivity.this, "插入事件失败!!!", Toast.LENGTH_SHORT).show();
                                                return;
                                            } else {
                                                Toast.makeText(MainActivity.this, "插入事件成功!!!", Toast.LENGTH_SHORT).show();
                                            }
                                            iSeven++;
                                        }
                                        if (!hasCurrentEvent) {
                                            System.out.println("已经存在这些账户：" + name + "===calId: " + calId);
                                            contentValues.put("title", currentTime + acountName[0]);
                                            contentValues.put("description", alarmContent[0]);
                                            // 插入账户
                                            contentValues.put("calendar_id", calId);
                                            contentValues.put("eventLocation", "北京");

                                            Calendar mCalendar = Calendar.getInstance();
                                            mCalendar.setTimeInMillis(System.currentTimeMillis());
                                            String week = String.valueOf(mCalendar.get(Calendar.DAY_OF_WEEK));//得到当前是星期几，如果是周末，那么就不需要设置账户
                                            mCalendar.add(Calendar.DAY_OF_MONTH, iSeven);
                                            mCalendar.set(Calendar.HOUR_OF_DAY, clockTime[0]);
                                            mCalendar.set(Calendar.MINUTE, 0);
                                            long start = mCalendar.getTime().getTime();
                                            mCalendar.set(Calendar.MINUTE, 1);
//                                        mCalendar.set(Calendar.HOUR_OF_DAY, clockTime[j]+1);
//                                        mCalendar.add(Calendar.MINUTE, clockTime[j] + time);
                                            long end = mCalendar.getTime().getTime();

                                            contentValues.put(CalendarContract.Events.DTSTART, start);
//                                        contentValues.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;WKST=SU;BYDAY=MO,TU,WE,TH,FR,SA");
                                            contentValues.put(CalendarContract.Events.DTEND, end);
                                            contentValues.put(CalendarContract.Events.HAS_ALARM, true);//设置有闹钟提醒
                                            contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Shanghai");  //这个是时区，必须有，
                                            contentValues.put(CalendarContract.Events.STATUS, 1);
                                            //添加事件
                                            Uri newEvent = getContentResolver().insert(Uri.parse(CALANDER_EVENT_URL), contentValues);
                                            if (newEvent == null) {
                                                //添加日历事件失败直接返回
                                                Toast.makeText(MainActivity.this, "添加失败!!!", Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            //事件提醒的设定
                                            ContentValues values = new ContentValues();
                                            values.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
                                            values.put(CalendarContract.Reminders.MINUTES, 1);  //提前一天有提醒  1440
                                            // 提前10分钟有提醒
                                            values.put(CalendarContract.Reminders.METHOD, 1);


                                            Uri uriNew = getContentResolver().insert(Uri.parse(CALANDER_REMIDER_URL), values);
                                            if (uriNew == null) {
                                                //添加闹钟提醒失败直接返回
                                                Toast.makeText(MainActivity.this, "插入事件失败!!!", Toast.LENGTH_SHORT).show();
                                                return;
                                            } else {
                                                Toast.makeText(MainActivity.this, "插入事件成功!!!", Toast.LENGTH_SHORT).show();
                                            }
                                            iSeven++;
                                    }

                                } else {
                                    String currentTime = getCurrentTime(iTen);
                                    if (name.equals(currentTime + acountName[1])) {
                                        System.out.println("已经有" + name + "这个事件了");
                                        iTen++;
                                    } else {
                                        System.out.println("已经存在这些账户：" + name + "===calId: " + calId);
                                        contentValues.put("title", currentTime + acountName[1]);
                                        contentValues.put("description", alarmContent[1]);
                                        // 插入账户
                                        contentValues.put("calendar_id", calId);
                                        contentValues.put("eventLocation", "北京");

                                        Calendar mCalendar = Calendar.getInstance();
                                        mCalendar.setTimeInMillis(System.currentTimeMillis());
                                        mCalendar.add(Calendar.DAY_OF_MONTH, iTen);
                                        mCalendar.set(Calendar.HOUR_OF_DAY, clockTime[1]);
                                        mCalendar.set(Calendar.MINUTE, 0);
                                        long start = mCalendar.getTime().getTime();
                                        mCalendar.set(Calendar.MINUTE, 1);
//                                        mCalendar.set(Calendar.HOUR_OF_DAY, clockTime[j]+1);
//                                        mCalendar.add(Calendar.MINUTE, clockTime[j] + time);
                                        long end = mCalendar.getTime().getTime();

                                        contentValues.put(CalendarContract.Events.DTSTART, start);
//                                        contentValues.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;WKST=SU;BYDAY=MO,TU,WE,TH,FR,SA");
                                        contentValues.put(CalendarContract.Events.DTEND, end);
                                        contentValues.put(CalendarContract.Events.HAS_ALARM, true);//设置有闹钟提醒
                                        contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Shanghai");  //这个是时区，必须有，
                                        contentValues.put(CalendarContract.Events.STATUS, 1);
                                        //添加事件
                                        Uri newEvent = getContentResolver().insert(Uri.parse(CALANDER_EVENT_URL), contentValues);
                                        if (newEvent == null) {
                                            //添加日历事件失败直接返回
                                            Toast.makeText(MainActivity.this, "添加失败!!!", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        //事件提醒的设定
                                        ContentValues values = new ContentValues();
                                        values.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
                                        values.put(CalendarContract.Reminders.MINUTES, 1);  //提前一天有提醒  1440
                                        // 提前10分钟有提醒
                                        values.put(CalendarContract.Reminders.METHOD, 1);


                                        Uri uriNew = getContentResolver().insert(Uri.parse(CALANDER_REMIDER_URL), values);
                                        if (uriNew == null) {
                                            //添加闹钟提醒失败直接返回
                                            Toast.makeText(MainActivity.this, "插入事件失败!!!", Toast.LENGTH_SHORT).show();
                                            return;
                                        } else {
                                            Toast.makeText(MainActivity.this, "插入事件成功!!!", Toast.LENGTH_SHORT).show();
                                        }
                                        iTen++;
                                    }

                                }
                            }

                        }
                    } else {
                        Toast.makeText(MainActivity.this, "没有账户，请先添加账户", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }
    });

    findViewById(R.id.delEventOne).

    setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick (View view){
//                //// TODO: 17/3/22 三星崩溃的解决
            String currentTime = getCurrentTime(0);
            if (currentTime == null) return;
//                                //删除事件
            int rownum = getContentResolver().delete(Uri.parse(CALANDER_URL), "_id==" + sevenEventID, null);  //注意：会全部删除所有账户，新添加的账户一般从id=1开始，
//                        int rownum = getContentResolver().delete(Uri.parse(CALANDER_EVENT_URL), Calendars.ACCOUNT_NAME +"="+ AcountName[0], null);  //注意：会全部删除所有账户，新添加的账户一般从id=1开始，
//                                //可以令_id=你添加账户的id，以此删除你添加的账户
            System.out.println(rownum < 0 || sevenEventID == null || sevenEventID.equals("-1") ? "没有可删除的" : "删除了:id为" + sevenEventID + "的" + currentTime + acountName[0]);

        }
    });

    findViewById(R.id.delEvenTwo).

    setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick (View view){
            String currentTime = getCurrentTime(0);
            if (currentTime == null) return;
            //删除事件
            int rownum = getContentResolver().delete(Uri.parse(CALANDER_URL), "_id==" + tenEventID, null);  //注意：会全部删除所有账户，新添加的账户一般从id=1开始，
            //可以令_id=你添加账户的id，以此删除你添加的账户
            System.out.println(rownum < 0 || tenEventID == null || tenEventID.equals("-1") ? "没有可删除的" : "删除了:id为" + tenEventID + "的" + currentTime + acountName[1]);

        }

    });

    findViewById(R.id.delAll).

    setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick (View view){
            //删除事件
            int rownum = getContentResolver().delete(Uri.parse(CALANDER_URL), "_id!=-1", null);  //注意：会全部删除所有账户，新添加的账户一般从id=1开始，
            //可以令_id=你添加账户的id，以此删除你添加的账户
            System.out.println(rownum < 0 ? "没有可删除的" : "删除了所有账户");

        }

    });

}

    private void insertAcount() {
        initDefaultCalendars();

        //添加日历账户
        for (int i = 0; i < 7; i++) {
            initSevenCalendars(i);
            initTenCalendars(i);
        }
    }


    //添加账户


    private void initSevenCalendars(int i) {
        String currentTime = getCurrentTime(i);
        if (Utils.isEmpty(currentTime)) return;
        System.out.println("添加" + i + "天后的账号:" + currentTime + acountName[0]);
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(Calendars.NAME, currentTime + acountName[0]);

        value.put(Calendars.ACCOUNT_NAME, currentTime + acountName[0]);
        value.put(Calendars.ACCOUNT_TYPE, tag);
        value.put(Calendars.CALENDAR_DISPLAY_NAME, tag);
        value.put(Calendars.VISIBLE, 1);
        value.put(Calendars.CALENDAR_COLOR, -99999999);
        value.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
        value.put(Calendars.SYNC_EVENTS, 0);
        value.put(Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(Calendars.OWNER_ACCOUNT, currentTime + acountName[0]);
        value.put(Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = Calendars.CONTENT_URI;
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, currentTime + acountName[0])
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, tag)
                .build();

        getContentResolver().insert(calendarUri, value);
    }

    private void initTenCalendars(int i) {
        String currentTime = getCurrentTime(i);
        if (Utils.isEmpty(currentTime)) return;
        System.out.println("添加" + i + "天后的账号:" + currentTime + acountName[1]);
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(Calendars.NAME, currentTime + acountName[1]);

        value.put(Calendars.ACCOUNT_NAME, currentTime + acountName[1]);
        value.put(Calendars.ACCOUNT_TYPE, "盒子鱼");
        value.put(Calendars.CALENDAR_DISPLAY_NAME, currentTime + acountName[1]);
        value.put(Calendars.VISIBLE, 1);
        value.put(Calendars.CALENDAR_COLOR, -99999999);
        value.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
        value.put(Calendars.SYNC_EVENTS, 0);
        value.put(Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(Calendars.OWNER_ACCOUNT, currentTime + acountName[1]);
        value.put(Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = Calendars.CONTENT_URI;
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, currentTime + acountName[1])
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, "盒子鱼")
                .build();

        getContentResolver().insert(calendarUri, value);
    }

    private void initDefaultCalendars() {
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(Calendars.NAME, "默认");

        value.put(Calendars.ACCOUNT_NAME, "默认账号");
        value.put(Calendars.ACCOUNT_TYPE, tag);
        value.put(Calendars.CALENDAR_DISPLAY_NAME, tag);
        value.put(Calendars.VISIBLE, 1);
        value.put(Calendars.CALENDAR_COLOR, -9206951);
        value.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
        value.put(Calendars.SYNC_EVENTS, 1);
        value.put(Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(Calendars.OWNER_ACCOUNT, "默认账号");
        value.put(Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = Calendars.CONTENT_URI;
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, "默认账号")
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, tag)
                .build();

        getContentResolver().insert(calendarUri, value);
    }


    /**
     * 初始化账户
     */
    private void initAcount() {
        int sevenEvent = 0;
        int tenEvent = 0;
        //读取系统日历账户，如果为0的话先添加
        Cursor userCursor = getContentResolver().query(Uri.parse(CALANDER_URL), null, null, null, null);
        int count = userCursor.getCount();

        if (count == 0) {
            insertAcount();
        }
        userCursor = getContentResolver().query(Uri.parse(CALANDER_URL), null, null, null, null);
        count = userCursor.getCount();
        System.out.println("Count: " + count);
        boolean hasEvent = false;
        for (userCursor.moveToFirst(); !userCursor.isAfterLast(); userCursor.moveToNext()) {
            String name = userCursor.getString(userCursor.getColumnIndex(Calendars.ACCOUNT_NAME));
            String id = userCursor.getString(userCursor.getColumnIndex("_id"));
            if (name.contains(acountName[0])) {
                hasEvent = true;
                sevenEvent++;
            }
            if (name.contains(acountName[1])) {
                hasEvent = true;
                tenEvent++;
            }
            if (hasEvent) {
//                String userName1 = userCursor.getString(userCursor.getColumnIndex(Calendars.NAME));
//                String userName0 = userCursor.getString(userCursor.getColumnIndex(Calendars.ACCOUNT_NAME));
//                System.out.println("NAME&&:ACCOUNT_NAME " + userName1 + " -- ACCOUNT_NAME: " + userName0);
                System.out.println("已有账户：" + name + "的id是：" + id);


            }
        }
        if (sevenEvent < 6) {
            for (int i = sevenEvent; i < 7; i++) {
                initSevenCalendars(i);
            }
        }
        if (tenEvent < 6) {
            for (int i = tenEvent; i < 7; i++) {

                initTenCalendars(i);
            }
        }


    }

    /**
     * 检查当天的日历
     */
    private void checkCurrentAcount() {
        sevenEventID = "-1";
        tenEventID = "-1";
        String currentTime = getCurrentTime(0);
        if (currentTime == null) return;
        //读取系统日历账户，如果为0的话先添加
        Cursor userCursor = getContentResolver().query(Uri.parse(CALANDER_URL), null, null, null, null);
        int count = userCursor.getCount();

        System.out.println("Count: " + count);
        for (userCursor.moveToFirst(); !userCursor.isAfterLast(); userCursor.moveToNext()) {
            String name = userCursor.getString(userCursor.getColumnIndex(Calendars.ACCOUNT_NAME));
            String id = userCursor.getString(userCursor.getColumnIndex("_id"));
            String currentSevenAcountName = currentTime + acountName[0];
            String currentTenAcountName = currentTime + acountName[1];
            if (name.equals(currentSevenAcountName)) {
                sevenEventID = id;
                System.out.println("已有账户：" + name + "的id是：" + sevenEventID);
            }
            if (name.equals(currentTenAcountName)) {
                tenEventID = id;
                System.out.println("已有账户：" + name + "的id是：" + tenEventID);
            }
        }

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

    private String getCurrentTime(int i) {
        SimpleDateFormat sf = new SimpleDateFormat("yy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

        String time = sf.format(c.getTime());

        if (i == 0) {
            if (isSun(c.getTime().getTime())) {
                System.out.println(time + "我是星期日");
                return null;
            }
            return time;
        }
        c.add(Calendar.DAY_OF_MONTH, i);
        time = sf.format(c.getTime());
        if (isSun(c.getTime().getTime())) {
            System.out.println(time + "我是星期日");
            return null;
        }
        return time;
    }

    private boolean isSun(long time) {
        Date date = new Date(time);
        SimpleDateFormat sfWeek = new SimpleDateFormat("E");
        String week = sfWeek.format(date);
        if (week.equals("星期日")) {
            return true;
        }
        return false;
    }

    /**
     * Calendars table columns
     */
    public static final String[] CALENDARS_COLUMNS = new String[]{
            Calendars._ID,                           // 0
            Calendars.ACCOUNT_NAME,                  // 1
            Calendars.CALENDAR_DISPLAY_NAME,         // 2
            Calendars.OWNER_ACCOUNT                  // 3
    };
//    /** Events table columns */
//    public static final String[] EVENTS_COLUMNS = new String[] {
//            Events._ID,
//            Events.CALENDAR_ID,
//            Events.TITLE,
//            Events.DESCRIPTION,
//            Events.EVENT_LOCATION,
//            Events.DTSTART,
//            Events.DTEND,
//            Events.EVENT_TIMEZONE,
//            Events.HAS_ALARM,
//            Events.ALL_DAY,
//            Events.AVAILABILITY,
//            Events.ACCESS_LEVEL,
//            Events.STATUS,
//    };
//    /** Reminders table columns */
//    public static final String[] REMINDERS_COLUMNS = new String[] {
//            Reminders._ID,
//            Reminders.EVENT_ID,
//            Reminders.MINUTES,
//            Reminders.METHOD,
//    };
//    /** Reminders table columns */
//    public static final String[] ATTENDEES_COLUMNS = new String[] {
//            Attendees._ID,
//            Attendees.ATTENDEE_NAME,
//            Attendees.ATTENDEE_EMAIL,
//            Attendees.ATTENDEE_STATUS
//    };

    /**
     * 根据账户查询账户日历
     *
     * @param param Map<String, String>
     * @return List
     */
    public List<Map<String, String>> queryCalendars(Map<String, String> param) {
        String accountName = null;
        String accountType = null;
        String ownerAccount = null;

        if (param != null) {
            accountName = param.get("accountName");//账户名称
            accountType = param.get("accountType");//账户类型
            ownerAccount = param.get("ownerAccount");//拥有者账户
        }

        List<Map<String, String>> calendars = new ArrayList<Map<String, String>>();

        Cursor cursor = null;
        StringBuffer selection = new StringBuffer(" 1 = 1 ");
        List<String> selectionArgs = new ArrayList<String>();
        //本地帐户查询：ACCOUNT_TYPE_LOCAL是一个特殊的日历账号类型，它不跟设备账号关联。这种类型的日历不同步到服务器
        //如果是谷歌的账户是可以同步到服务器的
        if (Utils.isEmpty(accountName) && Utils.isEmpty(accountType) && Utils.isEmpty(ownerAccount)) {
            selection.append(" AND " + Calendars.ACCOUNT_TYPE + " = ? ");
            selectionArgs.add("LOCAL");
        } else {
            if (!Utils.isEmpty(accountName)) {
                selection.append(" AND " + Calendars.ACCOUNT_NAME + " = ? ");
                selectionArgs.add(accountName);
            }
            if (!Utils.isEmpty(accountType)) {
                selection.append(" AND " + Calendars.ACCOUNT_TYPE + " = ? ");
                selectionArgs.add(accountType);
            }
            if (!Utils.isEmpty(ownerAccount)) {
                selection.append(" AND " + Calendars.OWNER_ACCOUNT + " = ? ");
                selectionArgs.add(ownerAccount);
            }
        }
        cursor = getContentResolver().query(Uri.parse(CALANDER_URL), CALENDARS_COLUMNS, selection.toString(),
                selectionArgs.toArray(new String[]{}), null);
        while (cursor.moveToNext()) {
            Map<String, String> calendar = new HashMap<String, String>();
            // Get the field values
            calendar.put("calendarId", cursor.getString(0));
            calendar.put("accountName", cursor.getString(1));
            calendar.put("displayName", cursor.getString(2));
            calendar.put("ownerAccount", cursor.getString(3));
            System.out.println("查询到日历：" + calendar);
            calendars.add(calendar);
        }
        return calendars;
    }


}
