package com.example.tyl.timer.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.tyl.timer.R;
import com.example.tyl.timer.activity.MainActivity;
import com.example.tyl.timer.activity.ShowInformationWorkingActivity;
import com.example.tyl.timer.activity.ShowInformationSelectActivity;
import com.example.tyl.timer.fragment.EditorFragment;
import com.example.tyl.timer.util.Day;
import com.example.tyl.timer.util.DayCompare;
import com.example.tyl.timer.util.DaysAdapter;
import com.example.tyl.timer.util.Information;
import com.example.tyl.timer.util.InformationAboutWorkingAndWarningCompare;
import com.example.tyl.timer.util.MyApplication;
import com.example.tyl.timer.util.MyDatabaseHelper;
import com.example.tyl.timer.util.TimeUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static com.example.tyl.timer.util.MyDatabaseHelper.mSQLiteDatabase;
import static com.example.tyl.timer.util.MyDatabaseHelper.sMyDatabaseHelper;

//服务，在这里面进行设置定时信息
public class MyService extends Service {
    //info状态值
    static final int PLAN = 0;
    static final int WORKING = 1;
    static final int WARING = 6;
    static AlarmManager alarmManager = (AlarmManager) MyApplication.getContex().getSystemService(Context.ALARM_SERVICE);
    public static LinkedList<Information> sInformationHintList = new LinkedList<Information>();
    public static LinkedList<Information> sSelectActivitiesList = new LinkedList<Information>();
    public static HashMap<Integer, Information> sInformationMap = new HashMap();

    public static final int SEVICEBEGIN = 2;
    public static final int NORMAL = 3;

    static int todayID;
    static int day;

    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SEVICEBEGIN:
                    changeForeground();
                    Log.d("MyService", "message.servicebegin 开始执行");
                    showActivity();

                    Log.d("MyService", "message.servicebegin 执行结束");
                    break;
                case NORMAL:
                    changeForeground();
                    DaysAdapter daysAdapter = EditorFragment.getDaysAdapter();
                    if (daysAdapter != null) {
                        daysAdapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;

            }
        }
    };

    /**
     * 在此处对之前有可能因事故没有处理的INFO进行挑选请求处理
     */
    @Override
    public void onCreate() {
        Log.d("MyService", "服务onCreate方法执行");
        super.onCreate();
        new Thread(new Runnable() {
            @Override
            public void run() {                                                                     //APP第一次开启时保存时间
                SharedPreferences prf = PreferenceManager.getDefaultSharedPreferences(MyService.this);
                int yearPRF = prf.getInt("beginYear", -1);
                if (yearPRF == -1) {
                    SharedPreferences.Editor editor = prf.edit();
                    editor.putInt("beginYear", TimeUtil.getYear());
                    editor.putInt("beginMonth", TimeUtil.getMonth());
                    editor.putInt("beginDay", TimeUtil.getDay());
                    editor.putInt("beginHour", TimeUtil.getHour());
                    editor.putInt("beginMinute", TimeUtil.getMinuts());
                    editor.apply();
                }
                todayID = sMyDatabaseHelper.initialDate();
                MyDatabaseHelper.sMyDatabaseHelper.initialInfo();
                day = TimeUtil.getDay();

                LinkedList<Information> inforList6 = MyDatabaseHelper.getTargetInfoWaring();         //WARNING(6)信息
                if (inforList6 != null) {
                    Collections.sort(inforList6, new InformationAboutWorkingAndWarningCompare());
                    sSelectActivitiesList = inforList6;
                }
                LinkedList<Information> inforList1 = MyDatabaseHelper.getTargetInfoWorking();         //WORKING(1)信息
                if (inforList1 != null) {
                    Collections.sort(inforList1, new InformationAboutWorkingAndWarningCompare());
                    sInformationHintList = inforList1;
                    for (Information infoUsed : inforList1) {
                        int id = infoUsed.getId();
                        int year = infoUsed.getYear();
                        int month = infoUsed.getMonth();
                        int day = infoUsed.getDay();
                        int hour = infoUsed.getHour();
                        int minute = infoUsed.getMinute();
                        int lastTime = infoUsed.getLastTime();

                        long triggerLast = lastTime * 60000;            //持续时间
                        long begin = TimeUtil.getMillis(year, month, day, hour, minute);            //根据年月日获得毫秒时间，便于对在下个活动中取出对数据库操作。
                        long EndTriggerTime = begin + triggerLast;

                        sInformationMap.put(id, infoUsed);

                        int requestCode2 = Integer.valueOf("22" + id);          //根据设定的时间给intent确定唯一性，用于取消同一事物。（不用triggerattime因为int和long的取值矛盾）
                        Intent intent2 = new Intent(MyService.this, ShowInformationSelectActivity.class);          //创建任务结束时的提醒
                        intent2.putExtra("infoID", id);
                        PendingIntent pendingIntent1 = PendingIntent.getActivity(MyService.this, requestCode2, intent2, 0);
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, EndTriggerTime, pendingIntent1);
                    }

                }


                LinkedList<Information> inforList0 = MyDatabaseHelper.getTargetInfoPlan();         //PLAN(0)信息
                if (inforList0 != null) {
                    for (Information infoUsed : inforList0) {

                        int id = infoUsed.getId();
                        int year = infoUsed.getYear();
                        int month = infoUsed.getMonth();
                        int day = infoUsed.getDay();
                        int hour = infoUsed.getHour();
                        int minute = infoUsed.getMinute();
                        int lastTime = infoUsed.getLastTime();

                        long triggerLast = lastTime * 60000;            //持续时间
                        long begin = TimeUtil.getMillis(year, month, day, hour, minute);            //根据年月日获得毫秒时间，便于对在下个活动中取出对数据库操作。
                        long EndTriggerTime = begin + triggerLast;

                        sInformationMap.put(id, infoUsed);

                        int requestCode1 = Integer.valueOf("11" + id);       //开始任务提醒为
                        Intent intent1 = new Intent(MyService.this, ShowInformationWorkingActivity.class);
                        intent1.putExtra("infoID", id);
                        PendingIntent pendingIntent2 = PendingIntent.getActivity(MyService.this, requestCode1, intent1, 0);
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, begin, pendingIntent2);

                        int requestCode2 = Integer.valueOf("22" + id);          //根据设定的时间给intent确定唯一性，用于取消同一事物。（不用triggerattime因为int和long的取值矛盾）
                        Intent intent2 = new Intent(MyService.this, ShowInformationSelectActivity.class);          //创建任务结束时的提醒
                        intent2.putExtra("infoID", id);
                        PendingIntent pendingIntent1 = PendingIntent.getActivity(MyService.this, requestCode2, intent2, 0);
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, EndTriggerTime, pendingIntent1);
                    }
                }
                Message message = new Message();
                message.what = SEVICEBEGIN;
                mHandler.sendMessage(message);
            }
        }).start();
    }
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                int stateID = intent.getIntExtra("stateID", -1);                //dayID用来判别service由谁启动

                switch (stateID) {
                    case -99:                                               //此时由main日常（onResume）启动
//                        Log.d("MyService", "onPause传来-99执行");
                    case -9:                                              //service由自己启动，此时常规运作
                        int dayNow = TimeUtil.getDay();
                        if (day != dayNow && day != 0) {                                      //此时日期发生变化 且 不是第一次启动service （第一次todayID就是今天）
                            day = dayNow;
                            Log.d("MyService", "" + dayNow + ":" + day);
                            Log.d("MyService", "日期发生改变且不是第一次启动");
                            int daySearchID = MyDatabaseHelper.haveDay(TimeUtil.getToday());     //看是否存在今天的数据，state= id Key or  -1
                            if (daySearchID == -1) {                                                 //不存在今天的数据
                                ContentValues contentValues = new ContentValues();
                                contentValues.put("status", 0);
                                mSQLiteDatabase.update("TABLE_DAY", contentValues, "status=?", new String[]{1 + ""});     //更改昨天状态 1变成0
                                List<Day> dayList = EditorFragment.getmDays();
                                Day day1 = new Day();
                                day1.setYear(TimeUtil.getYear());
                                day1.setMonth(TimeUtil.getMonth());
                                day1.setDay(TimeUtil.getDay());
                                day1.setStatus(1);
                                todayID = sMyDatabaseHelper.addDay(day1);          //添加新的一天数据,并拿到id key;
                                day1.setId(todayID);
                                if (dayList != null) {
                                    for (Day day2 : dayList) {
                                        if (day2.getStatus() == 1) {
                                            day2.setStatus(0);                      //  day2为昨天 1   改为  0
                                        }
                                    }
                                    dayList.add(day1);                              //添加今天 day1
                                    Collections.sort(dayList, new DayCompare());
//                    EditorFragment.getDaysAdapter().notifyDataSetChanged();
                                }
                            } else {                                                             //存在今天的数据
                                todayID = daySearchID;
                                ContentValues contentValues1 = new ContentValues();
                                contentValues1.put("status", 0);
                                MyDatabaseHelper.mSQLiteDatabase.update("TABLE_DAY", contentValues1, "status=?", new String[]{1 + ""});    //更改昨天 1为0

                                ContentValues contentValues2 = new ContentValues();
                                contentValues2.put("status", 1);
                                MyDatabaseHelper.mSQLiteDatabase.update("TABLE_DAY", contentValues2, "id=?", new String[]{todayID + ""});   //更改今天2 为1
                                List<Day> dayList = EditorFragment.getmDays();
                                if (dayList != null) {
                                    for (Day day1 : dayList) {
                                        if (day1.getStatus() == 1) {
                                            day1.setStatus(0);                                                                      //更改昨天1 为0
                                        }
                                        if (day1.getId() == todayID) {
                                            day1.setStatus(1);                                                                      //更改今天  为1
                                        }
                                    }
                                    Collections.sort(dayList, new DayCompare());
//                    EditorFragment.getDaysAdapter().notifyDataSetChanged();
                                }
                            }

                            Message message1 = new Message();
                            message1.what = NORMAL;
                            mHandler.sendMessage(message1);
                        }
                        break;

                    default:                                                                                    //reciver开机启动、main第一次启动（onCreate）
                        break;
                }
            }
        }).start();


        //创建间隔为1小时的定时任务
        Intent startDayIntent = new Intent(this, MyService.class);
        startDayIntent.putExtra("dayID", -9);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getService(this, 1990, startDayIntent, 0);
        alarmManager.cancel(pi);
        int anHour = 2 * 60 * 60 * 1000;
        Long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        Log.d("周期为1小时服务启动", "周期为一小时服务启动");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAlarmBinder;
    }

    public AlarmBinder mAlarmBinder = new AlarmBinder();

    public class AlarmBinder extends Binder {
        public void startAlarm(final Information infoUsed) {
            Log.d("Myservice", "任务开始启动");
            int id = infoUsed.getId();
            sInformationMap.put(id, infoUsed);
            int year = infoUsed.getYear();
            int month = infoUsed.getMonth();
            int day = infoUsed.getDay();
            int hour = infoUsed.getHour();
            int minute = infoUsed.getMinute();
            int lastTime = infoUsed.getLastTime();
            long triggerLast = lastTime * 60000;            //持续时间
            long begin = TimeUtil.getMillis(year, month, day, hour, minute);            //根据年月日获得毫秒时间，便于对在下个活动中取出对数据库操作。
            long EndTriggerTime = begin + triggerLast;
            int requestCode1 = Integer.valueOf("11" + id);       //开始任务提醒为
            Intent intent1 = new Intent(MyService.this, ShowInformationWorkingActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent1.putExtra("infoID", id);
            PendingIntent pendingIntent1 = PendingIntent.getActivity(MyService.this, requestCode1, intent1, 0);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, begin, pendingIntent1);
            int requestCode2 = Integer.valueOf("22" + id);                                      //根据设定的时间给intent确定唯一性，用于取消同一事物。（不用triggerattime因为int和long的取值矛盾）
            Intent intent2 = new Intent(MyService.this, ShowInformationSelectActivity.class);          //创建任务结束时的提醒
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent2.putExtra("infoID", id);
            PendingIntent pendingIntent2 = PendingIntent.getActivity(MyService.this, requestCode2, intent2, 0);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, EndTriggerTime, pendingIntent2);
            infoAlarmChangeForeground();
            Log.d("Myservice", "任务启动结束");
        }
        public void cancelAlarm(final Information infoUsed) {                     //取消任务

            Log.d("Myservice", "任务取消开始");
            int id = infoUsed.getId();
            sInformationMap.remove(id);
            int requestCode1 = Integer.valueOf("11" + id);
            int requestCode2 = Integer.valueOf("22" + id);
            Intent intent = new Intent();
            Intent intent1 = new Intent(MyService.this, ShowInformationWorkingActivity.class);
            Intent intent2 = new Intent(MyService.this, ShowInformationSelectActivity.class);
            PendingIntent pendingIntent1 = PendingIntent.getActivity(MyService.this, requestCode1, intent1, 0);
            PendingIntent pendingIntent2 = PendingIntent.getActivity(MyService.this, requestCode2, intent2, 0);
            alarmManager.cancel(pendingIntent1);
            alarmManager.cancel(pendingIntent2);
            infoAlarmChangeForeground();
            Log.d("Myservice", "任务取消结束");
        }



        public void infoHintOrSelectChangeForeground() {
            int dayID = todayID;
            Day date = MyDatabaseHelper.getDayByID(dayID);
            int year = date.getYear();
            int month = date.getMonth();
            int day = date.getDay();
            int all = date.getAll();
            int done = date.getDone();
            int theRest = date.getTheRest();
            int lost = date.getLosed();
            int num = all - done - lost - theRest;
            NotificationCompat.Builder builder = new NotificationCompat.Builder(MyService.this);
            if (sSelectActivitiesList.size() != 0) {                                                      //当有需要确认的事务时
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.applogo));
                builder.setVibrate(new long[]{0, 1000, 1000, 1000,1000,1000});
                builder.setSmallIcon(R.drawable.infoselecthintforeground);
                builder.setContentTitle("日期:" + year + "年" + month + "月" + day + "日" + "  待确认事务:" + num);
                builder.setAutoCancel(false).setPriority(NotificationCompat.PRIORITY_MAX);
                Intent intent = new Intent(MyService.this, ShowInformationSelectActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, intent, 0);
                builder.setContentIntent(pendingIntent);
            } else if (sInformationHintList.size() != 0) {                                              //当有事务正在进行时
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.applogo));
                builder.setVibrate(new long[]{0, 1000, 1000, 1000,1000,1000});
                builder.setSmallIcon(R.drawable.infoworkingforeground);
                builder.setContentTitle("日期:" + year + "." + month + "." + day  + "  正在进行事务:" + num);
                builder.setAutoCancel(false).setPriority(NotificationCompat.PRIORITY_MAX);
                Intent intent = new Intent(MyService.this, ShowInformationWorkingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, intent, 0);
                builder.setContentIntent(pendingIntent);
            } else {                                                                                    //一般状态
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.applogo));
                builder.setSmallIcon(R.drawable.infohintforeground);
                builder.setContentTitle("日期:" + year + "." + month + "." + day  + "  剩余事务:" + theRest);
                builder.setAutoCancel(false).setPriority(NotificationCompat.PRIORITY_MAX);
                Intent intent = new Intent(MyService.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, intent, 0);
                builder.setContentIntent(pendingIntent);
            }
            Notification notification = builder.build();
            startForeground(1, notification);

        }

        public void infoAlarmChangeForeground() {
            int dayID = todayID;
            Day date = MyDatabaseHelper.getDayByID(dayID);
            int year = date.getYear();
            int month = date.getMonth();
            int day = date.getDay();
            int all = date.getAll();
            int done = date.getDone();
            int theRest = date.getTheRest();
            int lost = date.getLosed();
            int num = all - done - lost - theRest;
            NotificationCompat.Builder builder = new NotificationCompat.Builder(MyService.this);
            if (sSelectActivitiesList.size() != 0) {                                                      //当有需要确认的事务时
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.applogo));
                builder.setSmallIcon(R.drawable.infoselecthintforeground);
                builder.setContentTitle("日期:" + year + "年" + month + "月" + day + "日" + "  待确认事务:" + num);
                builder.setAutoCancel(false).setPriority(NotificationCompat.PRIORITY_MAX);
                Intent intent = new Intent(MyService.this, ShowInformationSelectActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, intent, 0);
                builder.setContentIntent(pendingIntent);
            } else if (sInformationHintList.size() != 0) {                                              //当有事务正在进行时
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.applogo));
                builder.setSmallIcon(R.drawable.infoworkingforeground);
                builder.setContentTitle("日期:" + year + "." + month + "." + day  + "  正在进行事务:" + num);
                builder.setAutoCancel(false).setPriority(NotificationCompat.PRIORITY_MAX);
                Intent intent = new Intent(MyService.this, ShowInformationWorkingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, intent, 0);
                builder.setContentIntent(pendingIntent);
            } else {                                                                                    //一般状态
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.applogo));
                builder.setSmallIcon(R.drawable.infohintforeground);
                builder.setContentTitle("日期:" + year + "." + month + "." + day  + "  剩余事务:" + theRest);
                builder.setAutoCancel(false).setPriority(NotificationCompat.PRIORITY_MAX);
                Intent intent = new Intent(MyService.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, intent, 0);
                builder.setContentIntent(pendingIntent);
            }
            Notification notification = builder.build();
            startForeground(1, notification);

        }
    }
    public void showActivity() {
        if (sSelectActivitiesList.size() != 0) {
            Intent intent = new Intent(MyService.this, ShowInformationSelectActivity.class);
           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            MyService.this.startActivity(intent);

//            Log.d("Myservice", "showActivity执行selsect");

        } else if (sInformationHintList.size() != 0) {
            Intent intent = new Intent(MyService.this, ShowInformationWorkingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            MyService.this.startActivity(intent);
//            Log.d("Myservice", "showActivity执行hint");
        }
    }

    public void changeForeground() {
        int dayID = todayID;
        Day date = MyDatabaseHelper.getDayByID(dayID);
        int year = date.getYear();
        int month = date.getMonth();
        int day = date.getDay();
        int all = date.getAll();
        int done = date.getDone();
        int theRest = date.getTheRest();
        int lost = date.getLosed();
        int num = all - done - lost - theRest;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        if (sSelectActivitiesList.size() != 0) {                                                      //当有需要确认的事务时
//            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.applogo));
            builder.setDefaults(NotificationCompat.DEFAULT_ALL);
            builder.setSmallIcon(R.drawable.infoselecthintforeground);
            builder.setContentTitle("日期:" + year + "年" + month + "月" + day + "日" + "  待确认事务:" + num);
            builder.setAutoCancel(false).setPriority(NotificationCompat.PRIORITY_MAX);
            Intent intent = new Intent(MyService.this, ShowInformationSelectActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, intent, 0);
            builder.setContentIntent(pendingIntent);
        } else if (sInformationHintList.size() != 0) {                                              //当有事务正在进行时
//            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.applogo));
            builder.setDefaults(NotificationCompat.DEFAULT_ALL);
            builder.setSmallIcon(R.drawable.infoworkingforeground);
            builder.setContentTitle("日期:" + year + "年" + month + "月" + day + "日" + "  正在进行事务:" + num);
            builder.setAutoCancel(false).setPriority(NotificationCompat.PRIORITY_MAX);
            Intent intent = new Intent(MyService.this, ShowInformationWorkingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, intent, 0);
            builder.setContentIntent(pendingIntent);

        } else {                                                                                    //一般状态
//            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.applogo));
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.infohintforeground));
            builder.setSmallIcon(R.drawable.infohintforeground);
            builder.setContentTitle("日期:" + year + "年" + month + "月" + day + "日" + "  剩余事务:" + theRest);
            builder.setAutoCancel(false).setPriority(NotificationCompat.PRIORITY_MAX);
            Intent intent = new Intent(MyService.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, intent, 0);
            builder.setContentIntent(pendingIntent);
        }
        Notification notification = builder.build();
        startForeground(1, notification);
    }

}
