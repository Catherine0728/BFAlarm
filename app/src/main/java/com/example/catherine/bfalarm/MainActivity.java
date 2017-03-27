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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
                                String eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
                                String calendarID = eventCursor.getString(eventCursor.getColumnIndex(CalendarContract.Events.CALENDAR_ID));
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
                        int count = userCursor.getCount();
                        if (count > 0) {
                            int iSeven = 0;
                            int iTen = 0;
                            for (userCursor.moveToFirst(); !userCursor.isAfterLast(); userCursor.moveToNext()) {
                                String calId = userCursor.getString(userCursor.getColumnIndex("_id"));
                                int name = userCursor.getInt(userCursor.getColumnIndex(Calendars.NAME));
                                String userName = userCursor.getString(userCursor.getColumnIndex(Calendars.ACCOUNT_NAME));


                                StringBuffer stringBuffer = new StringBuffer();
                                List<String> selectionArgs = new ArrayList<String>();
                                stringBuffer.append(" 1=1 ");
                                stringBuffer.append(" AND " + CalendarContract.Events.CALENDAR_ID + "=? ");
                                selectionArgs.add(calId);
                                Cursor eventCursor = getContentResolver().query(
                                        Uri.parse(CALANDER_EVENT_URL),
                                        null,
                                        stringBuffer.length() == 0 ? null : stringBuffer.toString(),
                                        selectionArgs.size() == 0 ? null : selectionArgs.toArray(new String[]{}),
                                        null);


                                ContentValues contentValues = new ContentValues();
                                if (userName.contains(acountName[0])) {
                                    boolean hasCurrentEvent = false;
                                    TimeTag timeTag = getCurrentTime(iSeven);
                                    String currentTime = timeTag.getCurrentTime();
                                    boolean isSun = timeTag.isSun();
                                    if (name == 1 || isSun) {
                                        iSeven++;
                                        continue;
                                    }
                                    int eventCount = eventCursor.getCount();
                                    if (eventCount > 0) {
                                        for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                                            String title = eventCursor.getString(eventCursor.getColumnIndex("title"));
                                            String calendarId = eventCursor.getString(eventCursor.getColumnIndex("calendar_id"));
                                            System.out.println(calendarId);
                                            if (calendarId.equals(calId)) {
                                                hasCurrentEvent = true;
                                                System.out.println("已经有" + calendarId + "这个事件了");
                                            }
                                        }
                                        if (hasCurrentEvent) {
                                            iSeven++;
                                        } else {
                                            System.out.println("7-1已经存在这些账户：" + userName + "===calId: " + calId);
                                            contentValues.put("title", currentTime + acountName[0]);
                                            contentValues.put("description", alarmContent[0]);
                                            // 插入账户
                                            contentValues.put("calendar_id", calId);
                                            contentValues.put("eventLocation", "北京");

                                            Calendar mCalendar = Calendar.getInstance();
                                            mCalendar.setTimeInMillis(System.currentTimeMillis());
                                            mCalendar.add(Calendar.DAY_OF_MONTH, iSeven);
                                            mCalendar.set(Calendar.HOUR_OF_DAY, 19);
                                            mCalendar.set(Calendar.MINUTE, 0);
                                            long start = mCalendar.getTime().getTime();
                                            mCalendar.add(Calendar.MINUTE, 1);
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
                                        System.out.println("7-2已经存在这些账户：" + userName + "===calId: " + calId);
                                        contentValues.put("title", currentTime + acountName[0]);
                                        contentValues.put("description", alarmContent[0]);
                                        // 插入账户
                                        contentValues.put("calendar_id", calId);
                                        contentValues.put("eventLocation", "北京");

                                        Calendar mCalendar = Calendar.getInstance();
                                        mCalendar.setTimeInMillis(System.currentTimeMillis());
                                        mCalendar.add(Calendar.DAY_OF_MONTH, iSeven);
                                        mCalendar.set(Calendar.HOUR_OF_DAY, 19);
                                        mCalendar.set(Calendar.MINUTE, 0);
                                        long start = mCalendar.getTime().getTime();
                                        mCalendar.add(Calendar.MINUTE, 1);
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

                                } else if (userName.contains(acountName[1])) {//如果有10点
                                    TimeTag timeTag = getCurrentTime(iTen);
                                    String currentTime = timeTag.getCurrentTime();
                                    if (!userName.contains(currentTime)) {
                                        iTen++;
                                    }
                                    boolean hasCurrentEvent = false;
                                    boolean isSun = timeTag.isSun();
                                    if (name == 1 || isSun) {
                                        iTen++;
                                        continue;
                                    }
                                    System.out.println(timeTag + "====" + name);
                                    if (eventCursor.getCount() > 0) {
                                        for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                                            String title = eventCursor.getString(eventCursor.getColumnIndex("title"));
                                            String calendarId = eventCursor.getString(eventCursor.getColumnIndex("calendar_id"));
                                            if (calendarId.equals(calId)) {
                                                hasCurrentEvent = true;
                                                System.out.println("已经有" + calendarId + "这个事件了");
                                            }
                                        }
                                        if (hasCurrentEvent) {
                                            iTen++;
                                        } else {
                                            System.out.println("10-1已经存在这些账户：" + userName + "===calId: " + calId);
                                            contentValues.put("title", currentTime + acountName[1]);
                                            contentValues.put("description", alarmContent[1]);
                                            // 插入账户
                                            contentValues.put("calendar_id", calId);
                                            contentValues.put("eventLocation", "北京");

                                            Calendar mCalendar = Calendar.getInstance();
//                                        String[] strTime = getCurrentTimeStr(name);
//                                        mCalendar.set(Integer.valueOf(strTime[0]), Integer.valueOf(strTime[1]), Integer.valueOf(strTime[2]), 22, 0);
////
//                                        long start = mCalendar.getTimeInMillis();
//                                        Calendar endCalendar = Calendar.getInstance();
//                                        endCalendar.set(Integer.valueOf(strTime[0]), Integer.valueOf(strTime[1]), Integer.valueOf(strTime[2]), 22, 1);
////
//                                        long end = endCalendar.getTimeInMillis();
                                            mCalendar.setTimeInMillis(System.currentTimeMillis());
                                            mCalendar.add(Calendar.DAY_OF_MONTH, iTen);
                                            mCalendar.set(Calendar.HOUR_OF_DAY, 22);
                                            mCalendar.set(Calendar.MINUTE, 0);
                                            long start = mCalendar.getTime().getTime();
                                            mCalendar.add(Calendar.MINUTE, 1);
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
                                    } else {
                                        System.out.println("10-2已经存在这些账户：" + userName + "===calId: " + calId);
                                        contentValues.put("title", currentTime + acountName[1]);
                                        contentValues.put("description", alarmContent[1]);
                                        // 插入账户
                                        contentValues.put("calendar_id", calId);
                                        contentValues.put("eventLocation", "北京");

                                        Calendar mCalendar = Calendar.getInstance();
                                        mCalendar.setTimeInMillis(System.currentTimeMillis());
                                        mCalendar.add(Calendar.DAY_OF_MONTH, iTen);
                                        mCalendar.set(Calendar.HOUR_OF_DAY, 22);
                                        mCalendar.set(Calendar.MINUTE, 0);
                                        long start = mCalendar.getTime().getTime();
                                        mCalendar.add(Calendar.MINUTE, 1);
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
                        TimeTag timeTag = getCurrentTime(0);
                        String currentTime = timeTag.getCurrentTime();
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
                    public void onClick(View view) {
                        TimeTag timeTag = getCurrentTime(0);
                        String currentTime = timeTag.getCurrentTime();
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
                    public void onClick(View view) {
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
        TimeTag timeTag = getCurrentTime(i);
        String currentTime = timeTag.getCurrentTime();
        boolean isSun = timeTag.isSun();
        if (Utils.isEmpty(currentTime)) return;
        System.out.println("添加" + i + "天后的账号:" + currentTime + acountName[0]);
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(Calendars.NAME, isSun);

        value.put(Calendars.ACCOUNT_NAME, currentTime + acountName[0]);
        value.put(Calendars.ACCOUNT_TYPE, tag);
        value.put(Calendars.CALENDAR_DISPLAY_NAME, tag);
        value.put(Calendars.VISIBLE, 1);
        value.put(Calendars.CALENDAR_COLOR, -00000000);
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
        TimeTag timeTag = getCurrentTime(i);
        boolean isSun = timeTag.isSun();
        String currentTime = timeTag.getCurrentTime();
        if (Utils.isEmpty(currentTime)) return;
        System.out.println("添加" + i + "天后的账号:" + currentTime + acountName[1]);
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(Calendars.NAME, isSun);

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
        if (sevenEvent < 7) {
            for (int i = sevenEvent; i < 7; i++) {
                initSevenCalendars(i);
            }
        }
        if (tenEvent < 7) {
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
        TimeTag timeTag = getCurrentTime(0);
        String currentTime = timeTag.getCurrentTime();
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

    private TimeTag getCurrentTime(int i) {
        TimeTag timeTag = new TimeTag();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

        String time = sf.format(c.getTime());
        long currentTimeMillis = c.getTime().getTime();//得到当前的date值
        if (i == 0) {
            timeTag.setCurrentTime(time);
            if (isSun(currentTimeMillis)) {
                timeTag.setSun(true);
                return timeTag;
            }
            return timeTag;
        }
        c.add(Calendar.DAY_OF_MONTH, i);
        time = sf.format(c.getTime());
        currentTimeMillis = c.getTime().getTime();//得到当前的date值
        timeTag.setCurrentTime(time);
        if (isSun(currentTimeMillis)) {
            timeTag.setSun(true);
            return timeTag;
        }
        return timeTag;
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

    private String[] getCurrentTimeStr(String acountName) {
        String[] str = acountName.split("-");
        return str;
    }


}
