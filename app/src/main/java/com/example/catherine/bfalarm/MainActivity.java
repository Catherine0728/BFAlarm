package com.example.catherine.bfalarm;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.TimeZone;

import android.provider.CalendarContract.Calendars;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    //Android2.2版本以后的URL，之前的就不写了

    private static String CALANDER_URL = "content://com.android.calendar/calendars";
    private static String CALANDER_EVENT_URL = "content://com.android.calendar/events";
    private static String CALANDER_REMIDER_URL = "content://com.android.calendar/reminders";

    String[] names = {"7点", "10 点"};
    String sevenEventID;
    String tenEventID;
    String[] AcountName = {"7 clock alarm", "10 clock alarm"};
    String[] alarmTitle = {"7点的标题", "10点的标题"};
    String[] alarmContent = {"嗨，同学，想知道今天有哪些好玩儿的任务吗？［坏笑］快点击进来看看吧～", "啊，已经10点啦，再不完成今天的任务就要睡觉咯～"};

    int[] clockTime = {2, 8};
    private int time = 2;

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
                initCalendars();
                initAcount(true);
            }
        });

        findViewById(R.id.readUserButton).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        initAcount(false);
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
                        System.out.println(count);
                        if (count > 0) {
                            for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
//                        eventCursor.moveToLast();             //注意：这里与添加事件时的账户相对应，都是向最后一个账户添加
                                String eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
                                if (eventTitle.equals(alarmTitle[0])) {
                                    System.out.println("查询事件＝＝＝＝》" + eventTitle);
                                    hasEvent = true;
                                } else if (eventTitle.equals(alarmTitle[1])) {
                                    System.out.println("查询事件＝＝＝＝》" + eventTitle);
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

                        int count = userCursor.getCount();
                        if (count > 0) {
                            int i = 0;
                            for (userCursor.moveToFirst(); !userCursor.isAfterLast(); userCursor.moveToNext()) {
//                        userCursor.moveToLast();  //注意：是向最后一个账户添加，开发者可以根据需要改变添加事件 的账户
                                String calId = userCursor.getString(userCursor.getColumnIndex("_id"));
                                ContentValues contentValues = new ContentValues();
                                System.out.println("calendar_id: " + calId + "====i====" + i + "==sevenEventID==" + sevenEventID + "==tenEventID==" + tenEventID);
                                if (calId.equals(sevenEventID) || calId.equals(tenEventID)) {
                                    contentValues.put("title", alarmTitle[i]);
                                    contentValues.put("description", alarmContent[i]);
                                    // 插入账户
                                    contentValues.put("calendar_id", calId);
                                    contentValues.put("eventLocation", "北京");

                                    Calendar mCalendar = Calendar.getInstance();
                                    mCalendar.setTimeInMillis(System.currentTimeMillis());
                                    mCalendar.add(Calendar.MINUTE, clockTime[i]);
//                mCalendar.set(Calendar.HOUR_OF_DAY, 16);
//                mCalendar.set(Calendar.MINUTE, 9);
                                    long start = mCalendar.getTime().getTime();
                                    mCalendar.add(Calendar.MINUTE, clockTime[i] + time);
                                    long end = mCalendar.getTime().getTime();

                                    contentValues.put(CalendarContract.Events.DTSTART, start);
//                                contentValues.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;COUNT=7;WKST=SU;BYDAY=MO,TU,WE,TH,FR,SA");
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
                                    i++;
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
                    public void onClick(View view) {
//                //// TODO: 17/3/22 三星崩溃的解决
//                ContentValues contentValues = new ContentValues();
//                Calendar mCalendar = Calendar.getInstance();
//                mCalendar.setTimeInMillis(System.currentTimeMillis());
//                mCalendar.set(Calendar.HOUR_OF_DAY, 7);
//                mCalendar.set(Calendar.MINUTE, 1);
//                long start = mCalendar.getTime().getTime();
//                mCalendar.add(Calendar.HOUR_OF_DAY, 8);
//                long end = mCalendar.getTime().getTime();
//
//                contentValues.put(CalendarContract.Events.DTSTART, start);
//                contentValues.put(CalendarContract.Events.DTEND, end);
//                int num = getContentResolver().update(ContentUris.withAppendedId(Uri.parse(CALANDER_EVENT_URL), 1), contentValues, null, null);
//                Toast.makeText(MainActivity.this, num < 0 ? "更新失败" : "更新了: " + alarmTitle[0], Toast.LENGTH_SHORT).show();

//                        ArrayList<String> sevenEvents = getSevenEvent();
//                        if (sevenEvents != null && sevenEvents.size() != 0) {
//
//                            for (String s : sevenEvents) {
//                                //删除事件
                        int rownum = getContentResolver().delete(Uri.parse(CALANDER_URL), "_id==" + sevenEventID, null);  //注意：会全部删除所有账户，新添加的账户一般从id=1开始，
//                                //可以令_id=你添加账户的id，以此删除你添加的账户
                        System.out.println(rownum < 0 ? "没有可删除的" : "删除了:id为" + sevenEventID + "的" + alarmTitle[0]);

//                            }
//
//                        } else {
//                            Toast.makeText(MainActivity.this, "没有可删除的", Toast.LENGTH_SHORT).show();
//
//                        }
                    }
                });

        findViewById(R.id.delEvenTwo).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        ArrayList<String> tenEvents = getTenEvent();
//                        if (tenEvents != null && tenEvents.size() != 0) {
//                            for (String s : tenEvents) {
                        //删除事件
                        int rownum = getContentResolver().delete(Uri.parse(CALANDER_URL), "_id==" + tenEventID, null);  //注意：会全部删除所有账户，新添加的账户一般从id=1开始，
                        //可以令_id=你添加账户的id，以此删除你添加的账户
                        System.out.println(rownum < 0 ? "没有可删除的" : "删除了:id为" + tenEventID + "的" + alarmTitle[1]);

                    }

//                        } else {
//                            Toast.makeText(MainActivity.this, "没有可删除的", Toast.LENGTH_SHORT).show();
//
//                        }
//                    }
                });

    }

//    private ArrayList<String> getSevenEvent() {
//        ArrayList<String> sevenEvent = new ArrayList<>();
//        String name = AcountName[0];
//        for (String key : namesAndCalendarId.keySet()) {
//            if (key.equals(name)) {
//                sevenEvent.add(namesAndCalendarId.get(name));
//            }
//        }
//        return sevenEvent;
//
//    }
//
//    private ArrayList<String> getTenEvent() {
//        ArrayList<String> tenEvent = new ArrayList<>();
//        String name = AcountName[1];
//        for (String key : namesAndCalendarId.keySet()) {
//            if (key.equals(name)) {
//                tenEvent.add(namesAndCalendarId.get(name));
//            }
//        }
//        return tenEvent;
//    }

    private void insertAcount() {
        //添加日历账户
        for (int i = 0; i < 2; i++) {
            initCalendars(i);
        }
    }


    //添加账户

    private void initCalendars(int i) {
        System.out.println("添加账号:" + AcountName[i]);
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(Calendars.NAME, names[i]);

        value.put(Calendars.ACCOUNT_NAME, AcountName[i]);
        value.put(Calendars.ACCOUNT_TYPE, "盒子鱼");
        value.put(Calendars.CALENDAR_DISPLAY_NAME, "我的闹钟");
        value.put(Calendars.VISIBLE, 1);
        value.put(Calendars.CALENDAR_COLOR, -9206951);
        value.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
        value.put(Calendars.SYNC_EVENTS, 1);
        value.put(Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(Calendars.OWNER_ACCOUNT, AcountName[i]);
        value.put(Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = Calendars.CONTENT_URI;
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, AcountName[i])
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, "盒子鱼")
                .build();

        getContentResolver().insert(calendarUri, value);
    }

    private void initCalendars() {
        System.out.println("添加账号:185");
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(Calendars.NAME, "185");

        value.put(Calendars.ACCOUNT_NAME, "185185");
        value.put(Calendars.ACCOUNT_TYPE, "盒子鱼");
        value.put(Calendars.CALENDAR_DISPLAY_NAME, "我的闹钟");
        value.put(Calendars.VISIBLE, 1);
        value.put(Calendars.CALENDAR_COLOR, -9206951);
        value.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
        value.put(Calendars.SYNC_EVENTS, 1);
        value.put(Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(Calendars.OWNER_ACCOUNT, "185185");
        value.put(Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = Calendars.CONTENT_URI;
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, "185185")
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, "盒子鱼")
                .build();

        getContentResolver().insert(calendarUri, value);
    }


    /**
     * 检查当前账户
     *
     * @param isInit 如果为真，代表需要初始化
     */
    private void initAcount(boolean isInit) {
        //读取系统日历账户，如果为0的话先添加
        Cursor userCursor = getContentResolver().query(Uri.parse(CALANDER_URL), null, null, null, null);
        int count = userCursor.getCount();

        if (count == 0 && isInit) {
            insertAcount();
        }
        userCursor = getContentResolver().query(Uri.parse(CALANDER_URL), null, null, null, null);
        count = userCursor.getCount();
        System.out.println("Count: " + count);
        boolean hasSeven = false;
        boolean hasTen = false;
        for (userCursor.moveToFirst(); !userCursor.isAfterLast(); userCursor.moveToNext()) {
            String name = userCursor.getString(userCursor.getColumnIndex(Calendars.ACCOUNT_NAME));
            if (AcountName[0].equals(name)) {
                sevenEventID = userCursor.getString(userCursor.getColumnIndex("_id"));
                System.out.println("已有账户：" + name + "的id是：" + sevenEventID);
                hasSeven = true;
            }
            if (AcountName[1].equals(name)) {
                tenEventID = userCursor.getString(userCursor.getColumnIndex("_id"));
                System.out.println("已有账户：" + name + "的id是：" + tenEventID);
                hasTen = true;
            }
            if (isInit) {
                if (hasSeven || hasTen) {
                    System.out.println("name: " + name);
                    String userName1 = userCursor.getString(userCursor.getColumnIndex(Calendars.NAME));
                    String userName0 = userCursor.getString(userCursor.getColumnIndex(Calendars.ACCOUNT_NAME));
                    Toast.makeText(MainActivity.this, "NAME: " + userName1 + " -- ACCOUNT_NAME: " + userName0, Toast.LENGTH_SHORT).show();

                }
            }
        }
        if (isInit) {
            if (!hasSeven) {
                initCalendars(0);
            }
            if (!hasTen) {
                initCalendars(1);
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


}
