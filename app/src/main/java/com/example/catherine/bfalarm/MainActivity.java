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
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import android.provider.CalendarContract.Calendars;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    //Android2.2版本以后的URL，之前的就不写了

    private static String CALANDER_URL = "content://com.android.calendar/calendars";
    private static String CALANDER_EVENT_URL = "content://com.android.calendar/events";
    private static String CALANDER_REMIDER_URL = "content://com.android.calendar/reminders";

    String[] names = {"7bf.cn", "10bf.cn"};
    String sevenEventID;
    String tenEventID;
    String[] AcountName = {"7boxfish.cn", "10boxfish.cn"};
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
                        String currentTime = getCurrentTime(0);
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
                                String sevenAlarmTitle = currentTime + alarmTitle[0];
                                String tenAlarmTitle = currentTime + alarmTitle[1];
                                if (eventTitle.equals(sevenAlarmTitle)) {
                                    System.out.println("查询事件＝＝＝＝》" + eventTitle);
                                    System.out.println(calendarID);
                                    hasEvent = true;
                                } else if (eventTitle.equals(tenAlarmTitle)) {
                                    System.out.println("查询事件＝＝＝＝》" + eventTitle);
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
                            int i = 0;
                            for (userCursor.moveToFirst(); !userCursor.isAfterLast(); userCursor.moveToNext()) {
//                        userCursor.moveToLast();  //注意：是向最后一个账户添加，开发者可以根据需要改变添加事件 的账户
                                String calId = userCursor.getString(userCursor.getColumnIndex("_id"));
                                ContentValues contentValues = new ContentValues();
                                System.out.println("calId: " + calId + "==sevenEventID==" + sevenEventID + "==tenEventID==" + tenEventID);
//                                if (hasEvent()) return;
//                                System.out.println("需要添加事件");
                                String currentTime = getCurrentTime(i);

                                if (calId.equals(sevenEventID) || calId.equals(tenEventID)) {
                                    for (int j = 0; j < 2; j++) {
                                        contentValues.put("title", currentTime + alarmTitle[j]);
                                        contentValues.put("description", alarmContent[j]);
                                        // 插入账户
                                        contentValues.put("calendar_id", calId);
                                        contentValues.put("eventLocation", "北京");
//                                    contentValues.put("transparency", 2);

                                        Calendar mCalendar = Calendar.getInstance();
                                        mCalendar.setTimeInMillis(System.currentTimeMillis());
                                        mCalendar.add(Calendar.MINUTE, clockTime[j]);
//                mCalendar.set(Calendar.HOUR_OF_DAY, 16);
//                mCalendar.set(Calendar.MINUTE, 9);
                                        long start = mCalendar.getTime().getTime();
                                        mCalendar.add(Calendar.MINUTE, clockTime[j] + time);
                                        long end = mCalendar.getTime().getTime();

                                        contentValues.put(CalendarContract.Events.DTSTART, start);
//                                    contentValues.put(CalendarContract.Events.DISPLAY_COLOR, -99999999);
//                                    contentValues.put(CalendarContract.Events.EVENT_COLOR, -99999999);
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
                        String currentTime = getCurrentTime(0);

//                        ContentValues contentValues = new ContentValues();
//                        //设置事件状态暂定（0），确认（1）或取消（2）：
//                        contentValues.put(CalendarContract.Events.STATUS, 2);
                        //  设置它的可见性默认看到此事件（0），保密（1），私营（2），或公共（3）：
//                        contentValues.put("visibility", 2);
//                        int num = getContentResolver().update(ContentUris.withAppendedId(Uri.parse(CALANDER_EVENT_URL), Integer.valueOf(sevenEventID)), contentValues, null, null);
//                        Toast.makeText(MainActivity.this, num < 0 ? "更新失败" : "更新了: " + alarmTitle[0], Toast.LENGTH_SHORT).show();

//                        delEvents(sevenEventID, "2", false);
//                        getEventID();
//                                //删除事件
                        int rownum = getContentResolver().delete(Uri.parse(CALANDER_URL), "_id==" + sevenEventID, null);  //注意：会全部删除所有账户，新添加的账户一般从id=1开始，
//                        int rownum = getContentResolver().delete(Uri.parse(CALANDER_EVENT_URL), Calendars.ACCOUNT_NAME +"="+ AcountName[0], null);  //注意：会全部删除所有账户，新添加的账户一般从id=1开始，
//                                //可以令_id=你添加账户的id，以此删除你添加的账户
                        System.out.println(rownum < 0 || sevenEventID == null || sevenEventID.equals("-1") ? "没有可删除的" : "删除了:id为" + sevenEventID + "的" + currentTime + alarmTitle[0]);

                    }
                });

        findViewById(R.id.delEvenTwo).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String currentTime = getCurrentTime(0);
                        //删除事件
                        int rownum = getContentResolver().delete(Uri.parse(CALANDER_URL), "_id==" + tenEventID, null);  //注意：会全部删除所有账户，新添加的账户一般从id=1开始，
                        //可以令_id=你添加账户的id，以此删除你添加的账户
                        System.out.println(rownum < 0 || tenEventID == null || tenEventID.equals("-1") ? "没有可删除的" : "删除了:id为" + tenEventID + "的" + currentTime + alarmTitle[1]);

                    }

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
        initDefaultCalendars();

        //添加日历账户
        for (int i = 0; i < 6; i++) {
            initSevenCalendars(i);
            initTenCalendars(i);
        }
    }


    //添加账户

    private void initSevenCalendars(int i) {
        String currentTime = getCurrentTime(i);
        System.out.println(currentTime);
        System.out.println("添加账号:" + currentTime + AcountName[0]);
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(Calendars.NAME, currentTime + names[0]);

        value.put(Calendars.ACCOUNT_NAME, currentTime + AcountName[0]);
        value.put(Calendars.ACCOUNT_TYPE, "盒子鱼");
        value.put(Calendars.CALENDAR_DISPLAY_NAME, "盒子鱼的闹钟");
        value.put(Calendars.VISIBLE, 1);
        value.put(Calendars.CALENDAR_COLOR, -99999999);
        value.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
        value.put(Calendars.SYNC_EVENTS, 0);
        value.put(Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(Calendars.OWNER_ACCOUNT, currentTime + AcountName[0]);
        value.put(Calendars.CAN_ORGANIZER_RESPOND, 0);
//        value.put("transparency", 2);

        Uri calendarUri = Calendars.CONTENT_URI;
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, currentTime + AcountName[0])
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, "盒子鱼")
                .build();

        getContentResolver().insert(calendarUri, value);
    }

    private void initTenCalendars(int i) {
        String currentTime = getCurrentTime(i);
        System.out.println(currentTime);
        System.out.println("添加账号:" + currentTime + AcountName[1]);
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(Calendars.NAME, currentTime + names[1]);

        value.put(Calendars.ACCOUNT_NAME, currentTime + AcountName[1]);
        value.put(Calendars.ACCOUNT_TYPE, "盒子鱼");
        value.put(Calendars.CALENDAR_DISPLAY_NAME, "盒子鱼的闹钟");
        value.put(Calendars.VISIBLE, 1);
        value.put(Calendars.CALENDAR_COLOR, -99999999);
        value.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
        value.put(Calendars.SYNC_EVENTS, 0);
        value.put(Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(Calendars.OWNER_ACCOUNT, currentTime + AcountName[1]);
        value.put(Calendars.CAN_ORGANIZER_RESPOND, 0);
//        value.put("transparency", 2);

        Uri calendarUri = Calendars.CONTENT_URI;
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, currentTime + AcountName[1])
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, "盒子鱼")
                .build();

        getContentResolver().insert(calendarUri, value);
    }

    private void initDefaultCalendars() {
        System.out.println("添加默认账号");
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(Calendars.NAME, "默认");

        value.put(Calendars.ACCOUNT_NAME, "默认账号");
        value.put(Calendars.ACCOUNT_TYPE, "盒子鱼");
        value.put(Calendars.CALENDAR_DISPLAY_NAME, "我的闹钟");
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
        sevenEventID = "-1";
        tenEventID = "-1";
        String currentTime = getCurrentTime(0);
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
            String id = userCursor.getString(userCursor.getColumnIndex("_id"));
            String currentSevenAcountName = currentTime + AcountName[0];
            String currentTenAcountName = currentTime + AcountName[1];
            if (currentSevenAcountName.equals(name)) {
                sevenEventID = id;
                System.out.println("已有账户：" + name + "的id是：" + sevenEventID);
                hasSeven = true;
            }
            if (currentTenAcountName.equals(name)) {
                tenEventID = id;
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
                for (int i = 0; i < 6; i++) {
                    initSevenCalendars(i);
                }
            }
            if (!hasTen) {
                for (int i = 0; i < 6; i++) {

                    initTenCalendars(i);
                }
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

    /**
     * 删除event表里数据
     *
     * @param ids
     * @return
     */
    public Map<String, String> delEvents(String ids, String calendarId, boolean delAll) {
        Map<String, String> result = new HashMap<String, String>();

        String selection = null;

        if (delAll) {
            selection = CalendarContract.Events._ID + " > 0";
        } else if (calendarId != null || calendarId != "") {
            selection = CalendarContract.Events.CALENDAR_ID + "=" + calendarId;
        } else if (ids == null || ids == "") {
            result.put("result", "0");
            result.put("obj", "要删除日程事件的id为空！");
            return result;
        } else {
//            for (String id : ids) {
//                if (Utils.isNumber(id)) {
            String where = ids + ",";
//                }
//            }
            selection = CalendarContract.Events._ID + " in(" + where.substring(0, where.length() - 1) + ")";
        }

        try {
            int n = getContentResolver().delete(
                    Uri.parse(CALANDER_URL),
                    selection,
                    null);

            result.put("result", "1");
            result.put("obj", n + "");

        } catch (Exception e) {
            result.put("result", "-1");
            result.put("obj", "删除错误：" + e.toString());
        }
        return result;
    }


    private String getCurrentTime(int i) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        String time = sf.format(c.getTime());
//        System.out.println("当前日期：" + time);
        if (i == 0) {
            return time;
        }
        c.add(Calendar.DAY_OF_MONTH, i);
        time = sf.format(c.getTime());
        System.out.println("增加" + i + "天后日期 ：" + time);
        return time;
    }


}
