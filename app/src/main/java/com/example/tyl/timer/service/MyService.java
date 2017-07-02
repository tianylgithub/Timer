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
import com.example.tyl.timer.util.Day;
import com.example.tyl.timer.util.MyApplication;
import com.example.tyl.timer.util.TimeUtil;
import com.example.tyl.timer.util.MyDatabaseHelper;

//服务，在这里面进行设置定时信息
public class MyService extends Service {

    public static int count;

    static MyDatabaseHelper sMyDatabaseHelper = MyDatabaseHelper.sMyDatabaseHelper;

    static SQLiteDatabase sSQLiteDatabase = MyDatabaseHelper.mSQLiteDatabase;

   static AlarmManager alarmManager = (AlarmManager) MyApplication.getContex(). getSystemService(Context.ALARM_SERVICE);


    @Override
    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //拿到intent(开机启动的或者是定时任务)中的年月日信息

        int day1 = intent.getIntExtra("day", -1);
//        Log.d("在下。。", "服务5。。");
        //获得当前的年月日信息


        //比较当前的信息是否和intent中传来的信息一致（年月日相符合），如不项目则day数据库表插入新的一天

        if (!MyDatabaseHelper.haveDay(TimeUtil.getToday())) {
//            Log.d("在下。。", "服务9。。");
            ContentValues values = new ContentValues();
//            Log.d("在下。。", "服务10。。");
            values.put("year", TimeUtil.getYear());
//            Log.d("在下。。", "服务11。。");
            values.put("month", TimeUtil.getMonth());
//            Log.d("在下。。", "服务12。。");
            values.put("day", TimeUtil.getDay());
//            Log.d("在下。。", "服务13。。");
            values.put("allThing", 0);
//            Log.d("在下。。", "服务14。。");
            values.put("done", 0);
//            Log.d("在下。。", "服务15。。");
            values.put("losed", 0);
//            Log.d("在下。。", "服务16。。");
            values.put("theRest", 0);
//            Log.d("在下。。", "服务17。。");
            values.put("status", 1);
//            Log.d("在下。。", "服务18。。");
            sSQLiteDatabase.insert("TABLE_DAY", null, values);  //  不存在今天的day数据库，新的日期变化插入新的一天并且status=1
//            Log.d("在下。。", "服务19。。");
              sMyDatabaseHelper.updateDay(TimeUtil.getYesterday(), 0);
//            Log.d("asdf", "服务器onstartcomman插入结束");
//            Log.d("在下。。", "服务20。。");


        }
        else {
            if (TimeUtil.getDay()!= day1) {
                sMyDatabaseHelper.updateDay(TimeUtil.getToday(), 1);                                  //存在今天的数据库，则更改今天的status信息
                sMyDatabaseHelper.updateDay(TimeUtil.getYesterday(), 0);   //    更改昨天的status信息
//                Log.d("asdf", "服务器onstartcomm更改状态结束");
            }
        }

        //创建间隔为1小时的定时任务
        Intent startDayIntent = new Intent(this, MyService.class);
        startDayIntent.putExtra("day", TimeUtil.getDay());
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getService(this, 0, startDayIntent, 0);
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


    private AlarmBinder mAlarmBinder = new AlarmBinder();



    public class AlarmBinder extends Binder {

        public void startAlarm(int year,int month,int day,int hour,int minute ,int lastTime, String information){                      //启动任务
            Log.d("启动计划1", "启动计划1");
            long triggerLast = lastTime * 60000;            //持续时间
            long begin = TimeUtil.getMillis(year, month, day, hour, minute);            //根据年月日获得毫秒时间，便于对在下个活动中取出对数据库操作。
            long EndTriggerTime = begin + triggerLast;

            Intent intent1 = new Intent(MyService.this, SelectActivity.class);
            intent1.putExtra("year",year);                                                           //
            intent1.putExtra("month",month);                                                         //
            intent1.putExtra("day",day);                                                            //
            intent1.putExtra("hour",hour);                                                         //
            intent1.putExtra("minute",minute);
            intent1.putExtra("lastTime", lastTime);
            intent1.putExtra("information", information);
            int requestCode1 = Integer.valueOf("" +month+day+hour+minute);          //根据设定的时间给intent确定唯一性，用于取消同一事物。（不用triggerattime因为int和long的取值矛盾）
            PendingIntent pendingIntent1 = PendingIntent.getActivity(MyService.this, requestCode1, intent1, 0);
            alarmManager.set(AlarmManager.RTC_WAKEUP, EndTriggerTime, pendingIntent1);


            int requestCode2 = Integer.valueOf("" + minute + hour + day + month);
            Intent intent2 = new Intent(MyService.this, informationHint.class);
            intent2.putExtra("year", year);
            intent2.putExtra("month", month);
            intent2.putExtra("day", day);
            intent2.putExtra("minute", minute);
            intent2.putExtra("lastTime", lastTime);
            intent2.putExtra("information", information);
            PendingIntent pendingIntent2 = PendingIntent.getActivity(MyService.this, requestCode2, intent2, 0);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, begin, pendingIntent2);
            startForeground(1,getNotification(MyDatabaseHelper.getDay(TimeUtil.getToday())));
            Log.d("启动计划", "启动计划2");

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


        public void taskFinish() {
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
    builder.setSmallIcon(R.mipmap.ic_launcher);
    builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
    builder.setContentIntent(pendingIntent);
    builder.setContentTitle("日期:"+year+"年"+month+"月"+day+"日");
    builder.setStyle(new NotificationCompat.BigTextStyle().bigText("今日计划数目:"+all+" 还剩多少计划:"+theRest+" 已经完成:"+done+" 未完成:"+lost));
return     builder.build();
}

}
