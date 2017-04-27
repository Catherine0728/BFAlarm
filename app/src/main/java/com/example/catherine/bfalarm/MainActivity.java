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

    String[] alarmContent = {"嗨，同学，想知道今天有哪些好玩儿的任务吗？快点击进来看看吧～", "啊，已经10点啦，再不完成今天的任务就要睡觉咯～"};
    private List<TimeTag> allTimeTag;//用来存储连续七天的账户信息
    private List<String> allColumns;
    private List<String> allColumns2;
    private List<String> allColumns3;
    private List<String> allColumnsUser1;//得到账户
    private List<String> allColumnsUser2;//得到账户

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        initView();
        setListener();
    }

    private void initView() {
        allTimeTag = new ArrayList<>();
        allColumns = new ArrayList<>();
        allColumns2 = new ArrayList<>();
        allColumns3 = new ArrayList<>();
        allColumnsUser1 = new ArrayList<>();
        allColumnsUser2 = new ArrayList<>();
    }

    private void setListener() {
        findViewById(R.id.initButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initAcount();
            }
        });
        findViewById(R.id.checkButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //读取系统日历账户
                Cursor userCursor = getContentResolver().query(Uri.parse(CALANDER_URL), null, null, null, null);
                int count = userCursor.getCount();
                if (count > 0) {
                    for (userCursor.moveToFirst(); !userCursor.isAfterLast(); userCursor.moveToNext()) {
                        allColumnsUser1.clear();
                        allColumnsUser2.clear();
                        String diaplayName = userCursor.getString(userCursor.getColumnIndex(CalendarContract.Events.CALENDAR_DISPLAY_NAME));
                        System.out.println(diaplayName);
                        if (diaplayName.contains("盒子鱼")) {
                            String[] columnnames1 = userCursor.getColumnNames();
                            for (String s : columnnames1
                                    ) {
                                allColumnsUser1.add(s + "==" + userCursor.getString(userCursor.getColumnIndex(s)));
                            }
                        } else {
                            String[] columnnames2 = userCursor.getColumnNames();
                            for (String s : columnnames2
                                    ) {
                                allColumnsUser2.add(s + "==" + userCursor.getString(userCursor.getColumnIndex(s)));
                            }
                        }
                        System.out.println("allColumnsUser1==>" + allColumnsUser1);
                        System.out.println("allColumnsUser2==>" + allColumnsUser2);
                    }


                }

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
                                allColumns.clear();
                                allColumns2.clear();
                                allColumns3.clear();
                                String eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
                                String acountName = eventCursor.getString(eventCursor.getColumnIndex(CalendarContract.Events.ACCOUNT_NAME));
                                System.out.println("-------------------------" + eventTitle);
                                String calendarID = eventCursor.getString(eventCursor.getColumnIndex(CalendarContract.Events.CALENDAR_ID));
                                if (acountName.contains(alarmContent[0])) {
                                    String[] columnnames2 = eventCursor.getColumnNames();
                                    for (String s : columnnames2
                                            ) {
                                        allColumns2.add(s + "==" + eventCursor.getString(eventCursor.getColumnIndex(s)));
                                    }
                                    System.out.println("查询事件＝＝＝＝》" + eventTitle + "calendarID==>" + calendarID);
                                    hasEvent = true;
                                } else if (acountName.contains(alarmContent[1])) {
                                    String[] columnnames3 = eventCursor.getColumnNames();
                                    for (String s : columnnames3
                                            ) {
                                        allColumns3.add(s + "==" + eventCursor.getString(eventCursor.getColumnIndex(s)));
                                    }
                                    System.out.println("查询事件＝＝＝＝》" + eventTitle + "calendarID==>" + calendarID);
                                    hasEvent = true;
                                } else {
                                    String[] columnnames = eventCursor.getColumnNames();
                                    for (String s : columnnames
                                            ) {
                                        allColumns.add(s + "==" + eventCursor.getString(eventCursor.getColumnIndex(s)));
                                    }
                                }
                                System.out.println("allColumns==>" + allColumns);
                                System.out.println("allColumns2===>" + allColumns2);
                                System.out.println("allColumns3===>" + allColumns3);
                            }
                            int size = allColumns.size();
                            int size2 = allColumns2.size();
                            int size3 = allColumns3.size();
                            System.out.println(size);
                            System.out.println(size2);
                            System.out.println(size3);
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
                        String timeZoneID = TimeZone.getDefault().getID();
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
                                if (userName.contains(alarmContent[0])) {
                                    boolean hasCurrentEvent = false;
                                    TimeTag timeTag = getCurrentTime(iSeven);
                                    boolean isSun = timeTag.isSun();
                                    if (name == 1 || isSun) {
                                        iSeven++;
                                        continue;
                                    }
                                    int eventCount = eventCursor.getCount();
                                    if (eventCount > 0) {
                                        for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
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
                                            System.out.println("7-1需要添加事件的账户：" + userName + "===calId: " + calId);
                                            contentValues.put("title", "盒子鱼");
                                            contentValues.put("description", alarmContent[0]);
                                            // 插入账户
                                            contentValues.put("calendar_id", calId);
                                            contentValues.put("latitude", 0);
                                            contentValues.put("longitude", 0);
                                            contentValues.put("eventStatus", 0);
                                            contentValues.put("availabilityStatus", 2);
//                                            contentValues.put("eventLocation", "北京");

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
                                            contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, timeZoneID);  //这个是时区，必须有，
                                            contentValues.put(CalendarContract.Events.STATUS, 1);
                                            contentValues.put(CalendarContract.Events.GUESTS_CAN_MODIFY, 0);
                                            contentValues.put(CalendarContract.Events.HAS_ATTENDEE_DATA, 1);
                                            contentValues.put(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, 1);
                                            contentValues.put(CalendarContract.Events.HAS_ATTENDEE_DATA, 1);


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
                                        System.out.println("7-2需要添加事件的账户：" + userName + "===calId: " + calId);
                                        contentValues.put("title", "盒子鱼");
                                        contentValues.put("description", alarmContent[0]);
                                        // 插入账户
                                        contentValues.put("calendar_id", calId);
                                        contentValues.put("latitude", 0);
                                        contentValues.put("longitude", 0);
                                        contentValues.put("eventStatus", 0);
                                        contentValues.put("availabilityStatus", 2);
//                                        contentValues.put("eventLocation", "北京");

                                        Calendar mCalendar = Calendar.getInstance();
                                        mCalendar.setTimeInMillis(System.currentTimeMillis());
                                        mCalendar.add(Calendar.DAY_OF_MONTH, iSeven);
                                        mCalendar.set(Calendar.HOUR_OF_DAY, 19);
                                        mCalendar.set(Calendar.MINUTE, 0);
                                        long start = mCalendar.getTime().getTime();
                                        mCalendar.add(Calendar.MINUTE, 1);
                                        long end = mCalendar.getTime().getTime();

                                        contentValues.put(CalendarContract.Events.DTSTART, start);
                                        contentValues.put(CalendarContract.Events.GUESTS_CAN_MODIFY, true);
                                        contentValues.put(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, 1);
                                        contentValues.put(CalendarContract.Events.HAS_ATTENDEE_DATA, 1);


//                                        contentValues.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;WKST=SU;BYDAY=MO,TU,WE,TH,FR,SA");
                                        contentValues.put(CalendarContract.Events.DTEND, end);
                                        contentValues.put(CalendarContract.Events.HAS_ALARM, true);//设置有闹钟提醒
                                        contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, timeZoneID);  //这个是时区，必须有，
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

                                } else if (userName.contains(alarmContent[1])) {//如果有10点
                                    boolean hasCurrentEvent = false;
                                    TimeTag timeTag = getCurrentTime(iTen);
                                    boolean isSun = timeTag.isSun();
                                    if (name == 1 || isSun) {
                                        iTen++;
                                        continue;
                                    }
                                    if (eventCursor.getCount() > 0) {
                                        for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
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
                                            contentValues.put("title", "盒子鱼");
                                            contentValues.put("description", alarmContent[1]);
                                            // 插入账户
                                            contentValues.put("calendar_id", calId);
                                            contentValues.put("eventLocation", "北京");
                                            contentValues.put("latitude", 0);
                                            contentValues.put("longitude", 0);
                                            contentValues.put("eventStatus", 0);
                                            contentValues.put("availabilityStatus", 2);

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
                                            contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, timeZoneID);  //这个是时区，必须有，
                                            contentValues.put(CalendarContract.Events.GUESTS_CAN_MODIFY, false);
                                            contentValues.put(CalendarContract.Events.STATUS, 1);
                                            contentValues.put(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, 1);
                                            contentValues.put(CalendarContract.Events.HAS_ATTENDEE_DATA, 1);


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
                                        contentValues.put("title", "盒子鱼");
                                        contentValues.put("description", alarmContent[1]);
                                        // 插入账户
                                        contentValues.put("calendar_id", calId);
//                                        contentValues.put("eventLocation", "北京");
                                        contentValues.put("latitude", 0);
                                        contentValues.put("longitude", 0);
                                        contentValues.put("eventStatus", 0);
                                        contentValues.put("availabilityStatus", 2);

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
                                        contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, timeZoneID);  //这个是时区，必须有，
                                        contentValues.put(CalendarContract.Events.STATUS, 1);
                                        contentValues.put(CalendarContract.Events.GUESTS_CAN_MODIFY, false);
                                        contentValues.put(CalendarContract.Events.GUESTS_CAN_SEE_GUESTS, 1);
                                        contentValues.put(CalendarContract.Events.HAS_ATTENDEE_DATA, 1);


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
                        String eventId = checkCurrentAcount(0);
                        if (eventId == null || eventId.equals("")) return;
                        int rownum = getContentResolver().delete(Uri.parse(CALANDER_URL), "_id==" + eventId, null);  //注意：会全部删除所有账户，新添加的账户一般从id=1开始，
                        System.out.println(rownum < 0 ? "没有可删除的" : "删除了:id为" + eventId + "的" + alarmContent[0]);

                    }
                });

        findViewById(R.id.delEvenTwo).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String eventId = checkCurrentAcount(1);
                        if (eventId == null || eventId.equals("")) return;
                        int rownum = getContentResolver().delete(Uri.parse(CALANDER_URL), "_id==" + eventId, null);  //注意：会全部删除所有账户，新添加的账户一般从id=1开始，
                        System.out.println(rownum < 0 ? "没有可删除的" : "删除了:id为" + eventId + "的" + alarmContent[0]);

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
                        allTimeTag.clear();

                    }

                });

    }

    private void insertAcount() {
        initDefaultCalendars();

        //添加日历账户
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                initCalendars(i, j);
            }
        }
    }


    /**
     * 添加账户
     *
     * @param i:表示每一天
     * @param hour：每一天的几点
     */
    private void initCalendars(int i, int hour) {
        TimeTag timeTag = getCurrentTime(i);
        String currentTime = timeTag.getCurrentTime();
        boolean isSun = timeTag.isSun();
        String singleAcountName = currentTime + alarmContent[hour];//得到晚上hour点单个账户名字
        timeTag.setAcountName(singleAcountName);
        if (allTimeTag.size() > 0) {
            for (TimeTag timeTag1 : allTimeTag) {
                if (timeTag1.equals(singleAcountName)) {
                    System.out.println("已经存在这个事件了");
                    return;
                }
            }
        }

        allTimeTag.add(timeTag);
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(Calendars.NAME, isSun);

        value.put(Calendars.ACCOUNT_NAME, singleAcountName);
        value.put(Calendars.ACCOUNT_TYPE, "LOCAL");
        value.put(Calendars.CALENDAR_DISPLAY_NAME, "盒子鱼name");
        value.put(Calendars.VISIBLE, 1);
        value.put(Calendars.CALENDAR_COLOR, -99999999);
        value.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
        value.put(Calendars.SYNC_EVENTS, 1);
        value.put(Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(Calendars.OWNER_ACCOUNT, "2831790756");

        Uri calendarUri = Calendars.CONTENT_URI;
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, singleAcountName)
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, "LOCAL")
                .build();

        getContentResolver().insert(calendarUri, value);
    }

    /**
     * 创建一个默认的账户，三星如果在删除所有账户后，可能会导致崩溃
     */
    private void initDefaultCalendars() {
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(Calendars.NAME, "默认");

        value.put(Calendars.ACCOUNT_NAME, "盒子鱼");
        value.put(Calendars.ACCOUNT_TYPE, "LOCAL");
        value.put(Calendars.CALENDAR_DISPLAY_NAME, "盒子鱼name");
        value.put(Calendars.VISIBLE, 1);
        value.put(Calendars.CALENDAR_COLOR, 00000000);
        value.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
        value.put(Calendars.SYNC_EVENTS, 1);
        value.put(Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(Calendars.OWNER_ACCOUNT, "默认账号");

        Uri calendarUri = Calendars.CONTENT_URI;
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, "盒子鱼")
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, "LOCAL")
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
            if (name.contains(alarmContent[0])) {
                hasEvent = true;
                sevenEvent++;
            }
            if (name.contains(alarmContent[1])) {
                hasEvent = true;
                tenEvent++;
            }
            if (hasEvent) {
//                String userName1 = userCursor.getString(userCursor.getColumnIndex(Calendars.NAME));
//                String userName0 = userCursor.getString(userCursor.getColumnIndex(Calendars.ACCOUNT_NAME));
                System.out.println("已有账户：" + name + "的id是：" + id);


            }
        }
        if (sevenEvent < 2) {
            for (int i = sevenEvent; i < 2; i++) {
                initCalendars(i, 0);
            }
        }
        if (tenEvent < 2) {
            for (int i = tenEvent; i < 2; i++) {
                initCalendars(i, 1);
            }
        }


    }

    /**
     * 检查当天的日历
     *
     * @param i 每天有两个提醒
     *          0，就是查看七点
     *          1，就是查看十点
     */
    private String checkCurrentAcount(int i) {
        String eventID = null;
        TimeTag timeReminder = getCurrentTime(0);
        String currentTime = timeReminder.getCurrentTime();
        //读取系统日历账户，如果为0的话先添加
        Cursor userCursor = getContentResolver().query(Uri.parse(CALANDER_URL), null, null, null, null);
        int count = userCursor.getCount();
        System.out.println(count);
        for (userCursor.moveToFirst(); !userCursor.isAfterLast(); userCursor.moveToNext()) {
            String name = userCursor.getString(userCursor.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME));
            String id = userCursor.getString(userCursor.getColumnIndex("_id"));
            String currentAcountName = currentTime + alarmContent[i];
            if (name.equals(currentAcountName)) {
                eventID = id;
                System.out.println("已有账户：" + name + "的id是：" + eventID);
            }
        }

        return eventID;
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
        if (week.contains("日")) {
            return true;
        }
        return false;
    }

}
