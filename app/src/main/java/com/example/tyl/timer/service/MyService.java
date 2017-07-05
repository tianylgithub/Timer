package com.example.tyl.timer.service;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.tyl.timer.R;
import com.example.tyl.timer.activity.MainActivity;
import com.example.tyl.timer.activity.SelectActivity;
import com.example.tyl.timer.activity.informationHint;
import com.example.tyl.timer.fragment.EditorFragment;
import com.example.tyl.timer.util.Day;
import com.example.tyl.timer.util.Information;
import com.example.tyl.timer.util.MyApplication;
import com.example.tyl.timer.util.MyDatabaseHelper;
import com.example.tyl.timer.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

//服务，在这里面进行设置定时信息
public class MyService extends Service {

    //info状态值
    static final int PLAN = 0;
    static final int  WORKING= 1;
    static final int WARING = 6;


    static MyDatabaseHelper sMyDatabaseHelper = MyDatabaseHelper.sMyDatabaseHelper;

    static SQLiteDatabase sSQLiteDatabase = MyDatabaseHelper.mSQLiteDatabase;

   static AlarmManager alarmManager = (AlarmManager) MyApplication.getContex(). getSystemService(Context.ALARM_SERVICE);

    /**
     * 在此处对之前有可能因事故没有处理的INFO进行挑选并请求处理
     */

    @Override
    public void onCreate() {
        ArrayList<Information> informationsList = sMyDatabaseHelper.getTargetInfo016();     //查找数据库中状态为PLAN（0），WORKING(1), WARING(6)的信息
        if (informationsList.size() > 0) {
            for (Information information : informationsList) {
                int year = information.getYear();
                int month = information.getMonth();
                int day = information.getDay();
                int hour = information.getHour();
                int minute = information.getMinute();
                int lastTime = information.getLastTme();
                String info = information.getInformation();
                int completed = information.getCompleted();

                switch (completed) {
                    case PLAN:
                        long triggerLast = lastTime * 60000;            //持续时间
                        long begin = TimeUtil.getMillis(year, month, day, hour, minute);            //根据年月日获得毫秒时间，便于对在下个活动中取出对数据库操作。
                        long EndTriggerTime = begin + triggerLast;

                        int requestCodeP1 = Integer.valueOf("" + minute + hour + day + month);       //创建任务开始时的提醒
                        Intent intentP1 = new Intent(this, informationHint.class);
                        intentP1.putExtra("year", year);
                        intentP1.putExtra("month", month);
                        intentP1.putExtra("day", day);
                        intentP1.putExtra("hour", hour);
                        intentP1.putExtra("minute", minute);
                        intentP1.putExtra("lastTime", lastTime);
                        intentP1.putExtra("information", info);
                        PendingIntent pendingIntent2 = PendingIntent.getActivity(MyService.this, requestCodeP1, intentP1, 0);
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, begin, pendingIntent2);
                        startForeground(1, getNotification(MyDatabaseHelper.getDay(TimeUtil.getToday())));

                        Intent intentP2 = new Intent(this, SelectActivity.class);          //创建任务结束时的提醒
                        intentP2.putExtra("year", year);
                        intentP2.putExtra("month", month);
                        intentP2.putExtra("day", day);
                        intentP2.putExtra("hour", hour);
                        intentP2.putExtra("minute", minute);
                        intentP2.putExtra("lastTime", lastTime);
                        intentP2.putExtra("information", info);
                        int requestCodeP2 = Integer.valueOf("" + month + day + hour + minute);          //根据设定的时间给intent确定唯一性，用于取消同一事物。（不用triggerattime因为int和long的取值矛盾）
                        PendingIntent pendingIntent1 = PendingIntent.getActivity(this, requestCodeP2, intentP2, 0);
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, EndTriggerTime, pendingIntent1);
                        break;
                    case WORKING:
                        Intent intentW1 = new Intent(this, informationHint.class);        //直接弹出已经开始提醒
                        intentW1.putExtra("year", year);
                        intentW1.putExtra("month", month);
                        intentW1.putExtra("day", day);
                        intentW1.putExtra("hour", hour);
                        intentW1.putExtra("minute", minute);
                        intentW1.putExtra("lastTime", lastTime);
                        intentW1.putExtra("information", info);
                        startActivity(intentW1);

                        long triggerLast1 = lastTime * 60000;            //持续时间
                        long begin1 = TimeUtil.getMillis(year, month, day, hour, minute);            //根据年月日获得毫秒时间，便于对在下个活动中取出对数据库操作。
                        long EndTriggerTime1 = begin1 + triggerLast1;
                        Intent intent1 = new Intent(this, SelectActivity.class);
                        intent1.putExtra("year", year);
                        intent1.putExtra("month", month);
                        intent1.putExtra("day", day);
                        intent1.putExtra("hour", hour);
                        intent1.putExtra("minute", minute);
                        intent1.putExtra("lastTime", lastTime);
                        intent1.putExtra("information", info);
                        int requestCodeW = Integer.valueOf("" + month + day + hour + minute);          //根据设定的时间给intent确定唯一性，用于取消同一事物。（不用triggerattime因为int和long的取值矛盾）
                        PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, requestCodeW, intent1, 0);
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, EndTriggerTime1, pendingIntent);

                        break;

                    case WARING:
                        Intent intentWR = new Intent(this, SelectActivity.class);
                        intentWR.putExtra("year", information.getYear());
                        intentWR.putExtra("month", information.getMonth());
                        intentWR.putExtra("day", information.getDay());
                        intentWR.putExtra("hour", information.getHour());
                        intentWR.putExtra("minute", information.getMinute());
                        intentWR.putExtra("information", information.getInformation());
                        this.startActivity(intentWR);
                        break;

                }
            }
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //拿到intet(开机启动的或者是定时任务)中的年月日信息
/**
 * 每隔一个小时检查是不是新的一天来到了
 */
                int day1 = intent.getIntExtra("day", -1);

                if (!MyDatabaseHelper.haveDay(TimeUtil.getToday())) {
                    ContentValues values = new ContentValues();
                    values.put("year", TimeUtil.getYear());
                    values.put("month", TimeUtil.getMonth());
                    values.put("day", TimeUtil.getDay());
                    values.put("allThing", 0);
                    values.put("done", 0);
                    values.put("losed", 0);
                    values.put("theRest", 0);
                    values.put("status", 1);
                    sSQLiteDatabase.insert("TABLE_DAY", null, values);  //  不存在今天的day数据库，新的日期变化插入新的一天并且status=1
                    sMyDatabaseHelper.updateDay(TimeUtil.getYesterday(), 0);

                    if (EditorFragment.getDaysAdapter()!= null) {
                        Day today = TimeUtil.getToday();
                        today.setStatus(1);
                        EditorFragment.getmDays().add(today);
                        int dayPositionY = getDayPosition(TimeUtil.getYesterday());
                        if(dayPositionY!=-1) {
                            Day day = EditorFragment.getmDays().get(dayPositionY);
                            day.setStatus(0);
                        }
                        EditorFragment.getDaysAdapter().notifyDataSetChanged();
                    }



                } else {                                                             //存在今天的数据库，则更改今天的status信息
                    if (TimeUtil.getDay() != day1) {                                 //比较当前的信息是否和intent中传来的信息一致（年月日相符合），如不项目则day数据库表插入新的一天
                        sMyDatabaseHelper.updateDay(TimeUtil.getToday(), 1);
                        sMyDatabaseHelper.updateDay(TimeUtil.getYesterday(), 0);   //    更改昨天的status信息

                        if (EditorFragment.getDaysAdapter()!= null) {
//                            EditorFragment.getmDays().add(TimeUtil.getToday());
                            int dayPositionT = getDayPosition(TimeUtil.getYesterday());
                            if(dayPositionT!=-1) {
                                Day day = EditorFragment.getmDays().get(dayPositionT);  //昨天状态改为0
                                day.setStatus(0);
                            }
                            int dayPositionY = getDayPosition(TimeUtil.getToday());
                            if (dayPositionY != -1) {
                                Day day = EditorFragment.getmDays().get(dayPositionY);  //今天状态改为1
                                day.setStatus(1);
                            }
                            EditorFragment.getDaysAdapter().notifyDataSetChanged();
                        }






                    }
                }



        //创建间隔为1小时的定时任务
        Intent startDayIntent = new Intent(this, MyService.class);
        startDayIntent.putExtra("day", TimeUtil.getDay());
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getService(this, 1990, startDayIntent, 0);
        alarmManager.cancel(pi);
        int anHour = 2*60 * 60 * 1000;
        Long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        startForeground(1,getNotification(MyDatabaseHelper.getDay(TimeUtil.getToday())));
        Log.d("周期为1小时服务启动", "周期为一小时服务启动");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAlarmBinder;
    }


    private   AlarmBinder mAlarmBinder = new AlarmBinder();



    public class AlarmBinder extends Binder {

        public void startAlarm(int year,int month,int day,int hour,int minute ,int lastTime, String information){                      //启动任务
            Log.d("启动计划1", "启动计划1");
            long triggerLast = lastTime * 60000;            //持续时间
            long begin = TimeUtil.getMillis(year, month, day, hour, minute);            //根据年月日获得毫秒时间，便于对在下个活动中取出对数据库操作。
            long EndTriggerTime = begin + triggerLast;



            int requestCode1 = Integer.valueOf("" + minute + hour + day + month);       //创建任务开始时的提醒
            Intent intent1 = new Intent(MyService.this, informationHint.class);
            intent1.putExtra("year", year);
            intent1.putExtra("month", month);
            intent1.putExtra("day", day);
            intent1.putExtra("hour", hour);
            intent1.putExtra("minute", minute);
            intent1.putExtra("lastTime", lastTime);
            intent1.putExtra("information", information);
            PendingIntent pendingIntent2 = PendingIntent.getActivity(MyService.this, requestCode1, intent1, 0);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, begin, pendingIntent2);
            startForeground(1,getNotification(MyDatabaseHelper.getDay(TimeUtil.getToday())));
            Log.d("提示界面RTC", begin+"");
            Log.d("启动计划", "启动计划2");

            Intent intent2 = new Intent(MyService.this, SelectActivity.class);          //创建任务结束时的提醒
            intent2.putExtra("year",year);                                                           //
            intent2.putExtra("month",month);                                                         //
            intent2.putExtra("day",day);                                                            //
            intent2.putExtra("hour",hour);                                                         //
            intent2.putExtra("minute",minute);
            intent2.putExtra("lastTime", lastTime);
            intent2.putExtra("information", information);
            int requestCode2 = Integer.valueOf("" +month+day+hour+minute);          //根据设定的时间给intent确定唯一性，用于取消同一事物。（不用triggerattime因为int和long的取值矛盾）
            PendingIntent pendingIntent1 = PendingIntent.getActivity(MyService.this, requestCode2, intent2, 0);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, EndTriggerTime, pendingIntent1);
            Log.d("选择界面RTC", EndTriggerTime+"");


        }

        public void cancelAlarm(int year,int month, int day,int hour, int minute) {                     //取消任务,是不是一定要还原构造过程？逻辑上将用Requsetcode就可以了应该
            Log.d("取消计划1", "取消计划1");
            int requestCode1 = Integer.valueOf("" +month+day+hour+minute);
            int requestCode2 = Integer.valueOf("" + minute + hour + day + month);
            Intent intent = new Intent();
            PendingIntent pendingIntent1 = PendingIntent.getActivity(MyService.this, requestCode1, intent, 0);
            PendingIntent pendingIntent2 = PendingIntent.getActivity(MyService.this, requestCode2, intent, 0);
            alarmManager.cancel(pendingIntent1);
            alarmManager.cancel(pendingIntent2);
            startForeground(1,getNotification(MyDatabaseHelper.getDay(TimeUtil.getToday())));
            Log.d("取消计划2", "取消计划2");
        }


        public void showSelect(SelectActivity selectActivity) {

            int year = selectActivity.getYear();
            int month = selectActivity.getMonth();
            int day = selectActivity.getDay();
            int hour = selectActivity.getHour();
            int minute = selectActivity.getMinute();
            int lastTime = selectActivity.getLastTime();
            String information = selectActivity.getInformation();

            int requerstCode = selectActivity.getRequestCode();
            Intent intent = new Intent(MyService.this, SelectActivity.class);

            intent.putExtra("year", year);
            intent.putExtra("month", month);
            intent.putExtra("day", day);
            intent.putExtra("hour", hour);
            intent.putExtra("minute", minute);
            intent.putExtra("lastTime", lastTime);
            intent.putExtra("information", information);

            PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, requerstCode, intent, 0);
            Long triggerAtTime = SystemClock.elapsedRealtime()+ 30 * 60 * 1000;
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pendingIntent);

        }

        public  void   cancelSelect(SelectActivity selectActivity) {

            int requestCode = selectActivity.getRequestCode();
            Intent intent = new Intent();
            PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, requestCode, intent, 0);
            alarmManager.cancel(pendingIntent);

        }

        public void changeForeground() {
            Log.d("计划结束1", "取消计划1");
            startForeground(1,getNotification(MyDatabaseHelper.getDay(TimeUtil.getToday())));
            Log.d("计划结束2", "取消计划2");
        }
    }

public Notification getNotification(Day date ){
    int year = date.getYear();
    int month = date.getMonth();
    int day = date.getDay();
    int all = date.getAll();
    int done = date.getDone();
    int theRest = date.getTheRest();
    int lost = date.getLosed();
    Intent intent = new Intent(this, MainActivity.class);
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
    builder.setSmallIcon(R.mipmap.applogo);
    builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.applogo));
    builder.setContentIntent(pendingIntent);
    builder.setContentTitle("日期:"+year+"年"+month+"月"+day+"日");
    builder.setStyle(new NotificationCompat.BigTextStyle().bigText("今日计划数目:"+all+" 还剩多少计划:"+theRest+" 已经完成:"+done+" 未完成:"+lost));
return     builder.build();
}




    int getDayPosition(Day day) {
        int year = day.getYear();
        int month = day.getMonth();
        int date = day.getDay();

        List<Day> dayList= EditorFragment.getmDays();
        if (dayList != null) {
            for(int position=0; position<dayList.size();position++){
                Day day2 = dayList.get(position);
                if ((day2.getDay() == date) && (day2.getMonth() ==month) && (day2.getYear() == year)) {
                    return position;
                }
            }
            return -1;
        }
        return -1;
    }
}
