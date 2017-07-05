package com.example.tyl.timer.activity;

import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tyl.timer.R;
import com.example.tyl.timer.fragment.EditorFragment;
import com.example.tyl.timer.util.CollectorForSelect;
import com.example.tyl.timer.util.Day;
import com.example.tyl.timer.util.Information;
import com.example.tyl.timer.util.MyApplication;
import com.example.tyl.timer.util.MyDatabaseHelper;

import java.util.List;

/**在此完成3个逻辑：1、激发一个notification，提示用户输入完成的结果，如果用户无在一定时间内无操作，则视为没有完成  (将notification等级提到最高，强制使用者完成)
 *
 *                  2、根据选择完成对数据库completed选项的修改   y
 *
 *                  3、完成对service中的notification的数字-1     y
 *
 * Created by TYL on 2017/6/18.
 */

public class SelectActivity extends CollectorForSelect implements View.OnClickListener{
    int year;
    int month;
    int day;
    int hour;
    int minute;
    int lastTime;
    String information;
    int state;

    public int getYear() {
        return year;
    }

//    public void setYear(int year) {
//        this.year = year;
//    }

    public int getDay() {
        return day;
    }

//    public void setDay(int day) {
//        this.day = day;
//    }

    public int getHour() {
        return hour;
    }

//    public void setHour(int hour) {
//        this.hour = hour;
//    }

    public int getMinute() {
        return minute;
    }

//    public void setMinute(int minute) {
//        this.minute = minute;
//    }

    public int getMonth() {
        return month;
    }

//    public void setMonth(int month) {
//        this.month = month;
//    }


    public int getLastTime() {
        return lastTime;
    }

//    public void setLastTime(int lastTime) {
//        this.lastTime = lastTime;
//    }

    public String getInformation() {
        return information;
    }
//
//    public void setInformation(String information) {
//        this.information = information;
//    }

//    PendingIntent pi;


    NotificationManager notificationManager = (NotificationManager) MyApplication.getContex().getSystemService(NOTIFICATION_SERVICE);


//    MyService.AlarmBinder mAlarmBinder;

    int requestCode;


    public int getRequestCode() {
        return requestCode;
    }

//    public void setRequestCode(int requestCode) {
//        this.requestCode = requestCode;
//    }

//    AlarmManager alarmManager = (AlarmManager)MyApplication.getContex().getSystemService(Context.ALARM_SERVICE);

//    Intent ii = new Intent(this, SelectActivity.class);
//    PendingIntent pi = PendingIntent.getActivity(this,0,ii,0);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yes_not_activity);
        Log.d("selectacitive", "选择执行啦");
        Intent intent = getIntent();
        year = intent.getIntExtra("year", -1);
        month = intent.getIntExtra("month", -1);
        day = intent.getIntExtra("day", -1);
        hour = intent.getIntExtra("hour", -1);
        minute = intent.getIntExtra("minute", -1);
        lastTime = intent.getIntExtra("lastTime", -1);
        information = intent.getStringExtra("information");
        requestCode = Integer.valueOf("" + minute + hour + month + day);
        state = state + 1;
        TextView timeText = (TextView) findViewById(R.id.showtime);
        TextView timeInformation = (TextView) findViewById(R.id.showinformation);
        Button yesButton = (Button) findViewById(R.id.yes);
        Button notButton = (Button) findViewById(R.id.not);
//        Intent intent = getIntent();
//        year = intent.getIntExtra("year", -1);
//        month = intent.getIntExtra("month", -1);
//        day = intent.getIntExtra("day", -1);
//        hour = intent.getIntExtra("hour", -1);
//        minute = intent.getIntExtra("minute", -1);
//        lastTime = intent.getIntExtra("lastTime", -1);
//        information = intent.getStringExtra("information");
        timeText.setText(month+"-"+day+" "+hour+":"+minute+"  持续时间:"+lastTime);
        timeInformation.setText(information);



        addMap(this);
//        Intent i = new Intent(this, MyService.class);
//        bindService(i, mServiceConnection, BIND_AUTO_CREATE); //绑定服务，更改前台状态

//        mAlarmBinder.taskFinish();

//        Intent ii = new Intent(this, SelectActivity.class);
//        ii.putExtra("year", year);
//        ii.putExtra("month", month);
//        ii.putExtra("day", day);
//        ii.putExtra("hour", hour);
//        ii.putExtra("minute", minute);
//        ii.putExtra("information", information);
////        PendingIntent  pi = PendingIntent.getActivity(this,0,ii,0);
//
//
//        Notification notification = new NotificationCompat.Builder(this).setContentText("" + year + month + day + hour + minute).setContentText(information).setSmallIcon(R.drawable.infoselecthint).setContentIntent(pi).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.infoselecthint)).setPriority(NotificationCompat.PRIORITY_MAX).setVibrate(new long[]{0, 1000, 1000, 1000}).setLights(Color.GREEN, 1000, 1000).setSound(Uri.fromFile(new File("/system/media/audio/ringtones/luna.ogg"))).build();
//        notificationManager.notify(requestCode, notification);
        yesButton.setOnClickListener(this);
        notButton.setOnClickListener(this);
   //定时任务是否可以不用写
//        Long triggerAtTime = SystemClock.elapsedRealtime()+ 30 * 60 * 1000;
//        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
//        if(state>=3){
//            MyDatabaseHelper.sMyDatabaseHelper.updateInformation(year,month,day,hour,minute,3);
//            mAlarmBinder.taskFinish();
//            notificationManager.cancel(requestCode);
//            unbindService(mServiceConnection);
//            finish();
//        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.yes:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("注意:");
                dialog.setMessage("我已完成？");
                dialog.setCancelable(false);
                dialog.setPositiveButton("是的！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//
                        MyDatabaseHelper.sMyDatabaseHelper.updateInformation(year, month, day, hour, minute,2);    //更改数据库状态为2，

                        if (EditorFragment.getDaysAdapter()!= null) {
                            int dayPosition = getDayPosition(year, month, day);
                            if(dayPosition!=-1) {
                                Day day2 = EditorFragment.getmDays().get(dayPosition);
                                day2.plusDone();
                                EditorFragment.getDaysAdapter().notifyDataSetChanged();
                            }
                        }
                        if (ShowInformationActivity.getmAdapter() != null) {
                            int infoPosition = getInfoPosition(year, month, day, hour, minute);
                            if (infoPosition != -1) {
                                Information information2 = ShowInformationActivity.getEditorlist().get(infoPosition);
                                information2.setCompleted(2);
                                ShowInformationActivity.getmAdapter().notifyDataSetChanged();
                            }

                        }



                        SelectActivity.this.finish();

                    }
                });

                dialog.setNegativeButton("点错了。。", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }

                });
                dialog.show();
                break;
            case R.id.not:

                AlertDialog.Builder dialog1 = new AlertDialog.Builder(this);
                dialog1.setTitle("注意:");
                dialog1.setMessage("我又拖延了？");
                dialog1.setCancelable(false);
                dialog1.setPositiveButton("是的。。", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(SelectActivity.this, "Timer.db", null, 1);
                       MyDatabaseHelper.sMyDatabaseHelper.updateInformation(year, month, day, hour, minute,3);
//                        mAlarmBinder.changeForeground();
//                        unbindService(mServiceConnection);
////                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                        notificationManager.cancel(requestCode);


                        if (EditorFragment.getDaysAdapter()!= null) {
                            int dayPosition = getDayPosition(year, month, day);
                            if(dayPosition!=-1) {
                                Day day2 = EditorFragment.getmDays().get(dayPosition);
                                day2.plusDone();
                                EditorFragment.getDaysAdapter().notifyDataSetChanged();
                            }
                        }
                        if (ShowInformationActivity.getmAdapter() != null) {
                            int infoPosition = getInfoPosition(year, month, day, hour, minute);
                            if (infoPosition != -1) {
                                Information information2 = ShowInformationActivity.getEditorlist().get(infoPosition);
                                information2.setCompleted(2);
                                ShowInformationActivity.getmAdapter().notifyDataSetChanged();
                            }

                        }
                       SelectActivity.this.finish();
                    }
                });

                dialog1.setNegativeButton("点错啦！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                dialog1.show();
                break;
        }

    }



//    private ServiceConnection mServiceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            mAlarmBinder = (MyService.AlarmBinder) service;
//            mAlarmBinder.changeForeground();
//
//            if(state>=3){
//                MyDatabaseHelper.sMyDatabaseHelper.updateInformation(year,month,day,hour,minute,3);
//                mAlarmBinder.changeForeground();
////            notificationManager.cancel(requestCode);
////            unbindService(mServiceConnection);
//                SelectActivity.this.finish();
//            }
//
//        }
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//
//        }
//    };

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unbindService(mServiceConnection);
//                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        deleteActivity(this);

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
