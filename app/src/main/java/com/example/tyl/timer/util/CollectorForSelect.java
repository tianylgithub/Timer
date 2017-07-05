package com.example.tyl.timer.util;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;

import com.example.tyl.timer.R;
import com.example.tyl.timer.activity.SelectActivity;
import com.example.tyl.timer.activity.ShowInformationActivity;
import com.example.tyl.timer.fragment.EditorFragment;
import com.example.tyl.timer.service.MyService;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;




/**
 * Created by TYL on 2017/7/4.
 */

public class CollectorForSelect   extends AppCompatActivity {
    static Map sHashMap = new HashMap();
   static AlarmManager alarmManager = (AlarmManager) MyApplication.getContex().getSystemService(Context.ALARM_SERVICE);
    SelectActivity mSelectActivity;
    MyServiceConnection mMyServiceConnection;
//    public LinkedList<Activity> mLinkedList = new LinkedList<>;
    /**
     * SelectActivity onCreate时候启用
     * @param selectActivity
     */
    public  void addMap(SelectActivity selectActivity) {

        int year = selectActivity.getYear();
        int month = selectActivity.getMonth();
        int day = selectActivity.getDay();
        int hour = selectActivity.getHour();
        int minute = selectActivity.getMinute();
        int lastTime = selectActivity.getLastTime();
        String information = selectActivity.getInformation();

        int requestCode = selectActivity.getRequestCode();

        LinkedList<SelectActivity> linkedList = (LinkedList<SelectActivity>) sHashMap.get(requestCode);
        if (linkedList == null) {                           //不存在
            mSelectActivity = selectActivity;
            linkedList = new LinkedList<SelectActivity>();
            linkedList.add(mSelectActivity);
            sHashMap.put(requestCode, linkedList);

            MyDatabaseHelper.sMyDatabaseHelper.updateInformation(year,month,day,hour,minute,6);   //第一次提醒时更改数据库


            if (ShowInformationActivity.getmAdapter() != null) {

                int infoPosition = getInfoPosition(year, month, day, hour, minute);
                if (infoPosition != -1) {
                    Information information1 = ShowInformationActivity.getEditorlist().get(infoPosition);
                    information1.setCompleted(6);
                    ShowInformationActivity.getmAdapter().notifyDataSetChanged();                          //更改info列表
                }
            }
            mMyServiceConnection= new MyServiceConnection(selectActivity);          //第一次提醒时绑定服务，执行onServiceConnect方法

            Intent i = new Intent(selectActivity, MyService.class);
            mSelectActivity.bindService(i, mMyServiceConnection, BIND_AUTO_CREATE);

            Intent ii = new Intent(this, SelectActivity.class);                                               //开启通知
            ii.putExtra("year", year);
            ii.putExtra("month", month);
            ii.putExtra("day", day);
            ii.putExtra("hour", hour);
            ii.putExtra("minute", minute);
            ii.putExtra("lastTime", lastTime);
            ii.putExtra("information", information);
            PendingIntent pi = PendingIntent.getActivity(this,0,ii,0);
            Notification notification = new NotificationCompat.Builder(this).setContentTitle(month +"-"+ day +" "+ hour +":"+ minute+"耗时:"+lastTime+"min").setContentText(information).setSmallIcon(R.drawable.infoselecthint).setContentIntent(pi).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.infoselecthint)).setPriority(NotificationCompat.PRIORITY_MAX).setAutoCancel(false).setVibrate(new long[]{0, 1000, 1000, 1000}).setLights(Color.GREEN, 1000, 1000).setSound(Uri.fromFile(new File("/system/media/audio/ringtones/luna.ogg"))).build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(requestCode, notification);
        }else {             //如果已经有打开过
            linkedList.add(selectActivity);
            sHashMap.put(requestCode, linkedList);
            if (linkedList.size() >3) {//如果有三次以上选择视而不见，则自动改写为没有完成
                MyDatabaseHelper.sMyDatabaseHelper.updateInformation(year, month, day, hour, minute, 3);
                if (EditorFragment.getDaysAdapter() != null) {
                    int dayPosition = getDayPosition(year, month, day);
                    if (dayPosition != -1) {
                        Day day2 = EditorFragment.getmDays().get(dayPosition);
                        day2.plusLosed();
                        EditorFragment.getDaysAdapter().notifyDataSetChanged();
                    }
                }
                if (ShowInformationActivity.getmAdapter() != null) {
                    int infoPosition = getInfoPosition(year, month, day, hour, minute);
                    if (infoPosition != -1) {
                        Information information2 = ShowInformationActivity.getEditorlist().get(infoPosition);
                        information2.setCompleted(3);
                        ShowInformationActivity.getmAdapter().notifyDataSetChanged();

                    }
                }
                deleteActivity(selectActivity);
            }
        }
    }

    /**
     *  SelectActivity结束时启用
     * @param selectActivity
     */
    public  void deleteActivity
    (SelectActivity selectActivity) {


        int requestCode = selectActivity.getRequestCode();

        LinkedList<SelectActivity> linkedList= (LinkedList<SelectActivity>) sHashMap.get(requestCode);

           if (linkedList != null) {
            for (SelectActivity selectActivity0: linkedList) {
                if(!selectActivity0.isFinishing()){
                    selectActivity0.finish();
                }
            }
               NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
               notificationManager.cancel(requestCode);
               if (mSelectActivity != null) {
                   mSelectActivity.unbindService(mMyServiceConnection);
               }

        }
        sHashMap.remove(requestCode);
    }


    /**
     * 服务连接
     */


    class MyServiceConnection implements ServiceConnection {
        SelectActivity mSelectActivity;

        MyService.AlarmBinder mAlarmBinder;

        MyServiceConnection(SelectActivity selectActivity) {
            mSelectActivity = selectActivity;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAlarmBinder = (MyService.AlarmBinder) service;
            mAlarmBinder.showSelect(mSelectActivity);
            mAlarmBinder.changeForeground();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mAlarmBinder.cancelSelect(mSelectActivity);
            mAlarmBinder.changeForeground();

        }
    }



    int getDayPosition(int year, int month, int day) {
        List<Day> dayList= EditorFragment.getmDays();
        if (dayList != null) {
            for(int position=0; position<dayList.size();position++){
                Day day2 = dayList.get(position);
                if ((day2.getDay() == day) && (day2.getMonth() ==month) && (day2.getYear() == year)) {
                    return position;
                }
            }
            return -1;
        }
        return -1;
    }


    int getInfoPosition(int year, int month, int day, int hour, int minute) {

        List<Information> infoList = ShowInformationActivity.getEditorlist();
        if (infoList != null) {
            for(int position=0;position<infoList.size();position++) {
                Information information = infoList.get(position);
                if (information.getMinute() == minute && information.getHour() == hour && information.getMonth() == month && information.getYear() == year) {
                    return position;

                }
            }
            return -1;
        }
        return -1;

    }


}
