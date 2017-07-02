package com.example.tyl.timer.activity;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tyl.timer.R;
import com.example.tyl.timer.service.MyService;
import com.example.tyl.timer.util.MyApplication;
import com.example.tyl.timer.util.MyDatabaseHelper;

import java.io.File;

import static android.icu.text.RelativeDateTimeFormatter.Direction.THIS;
import static android.os.Build.VERSION_CODES.M;
import static com.example.tyl.timer.util.MyDatabaseHelper.updateInformation;

/**在此完成3个逻辑：1、激发一个notification，提示用户输入完成的结果，如果用户无在一定时间内无操作，则视为没有完成  (将notification等级提到最高，强制使用者完成)
 *
 *                  2、根据选择完成对数据库completed选项的修改   y
 *
 *                  3、完成对service中的notification的数字-1     y
 *
 * Created by TYL on 2017/6/18.
 */



public class SelectActivity extends AppCompatActivity implements View.OnClickListener{
    int year;
    int month;
    int day;
    int hour;
    int minute;
    String information;
    int state;

    NotificationManager notificationManager = (NotificationManager) MyApplication.getContex().getSystemService(NOTIFICATION_SERVICE);

    MyService.AlarmBinder mAlarmBinder;
    int requestCode;
    AlarmManager alarmManager = (AlarmManager)MyApplication.getContex().getSystemService(Context.ALARM_SERVICE);

    Intent ii = new Intent(this, SelectActivity.class);
    PendingIntent pi = PendingIntent.getActivity(this,0,ii,0);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yes_not_activity);
        state = state + 1;
        TextView timeText = (TextView) findViewById(R.id.showtime);
        TextView timeInformation = (TextView) findViewById(R.id.showinformation);
        Button yesButton = (Button) findViewById(R.id.yes);
        Button notButton = (Button) findViewById(R.id.not);
        Intent intent = getIntent();
        year = intent.getIntExtra("year", -1);
        month = intent.getIntExtra("month", -1);
        day = intent.getIntExtra("day", -1);
        hour = intent.getIntExtra("hour", -1);
        minute = intent.getIntExtra("minute", -1);
        information = intent.getStringExtra("information");
        timeText.setText(""+year+"-"+month+"-"+day+"-"+hour+"-"+minute);
        timeInformation.setText(information);

        Log.d("selectacitive", "选择执行啦");

        MyDatabaseHelper.sMyDatabaseHelper.updateInformation(year,month,day,hour,minute,6);
        Intent i = new Intent(this, MyService.class);
        bindService(i, mServiceConnection, BIND_AUTO_CREATE); //绑定服务，更改前台状态

        mAlarmBinder.taskFinish();

        requestCode = Integer.valueOf("" + minute + hour + month + day);
        Notification notification = new NotificationCompat.Builder(this).setContentText("" + year + month + day + hour + minute).setContentText(information).setSmallIcon(R.drawable.axes).setContentIntent(pi).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.chick)).setPriority(NotificationCompat.PRIORITY_MAX).setVibrate(new long[]{0, 1000, 1000, 1000}).setLights(Color.GREEN, 1000, 1000).setSound(Uri.fromFile(new File("/system/media/audio/ringtones/luna.ogg"))).build();
        notificationManager.notify(requestCode, notification);
        yesButton.setOnClickListener(this);
        notButton.setOnClickListener(this);

        Long triggerAtTime = SystemClock.elapsedRealtime()+ 60 * 60 * 1000;
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        if(state>=3){
            MyDatabaseHelper.sMyDatabaseHelper.updateInformation(year,month,day,hour,minute,3);
            mAlarmBinder.taskFinish();
//            notificationManager.cancel(requestCode);
//            unbindService(mServiceConnection);
            finish();
        }


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
//                        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(SelectActivity.this, "Timer.db", null, 1);
                        MyDatabaseHelper.sMyDatabaseHelper.updateInformation(year, month, day, hour, minute,2);
                        mAlarmBinder.taskFinish();
//                        unbindService(mServiceConnection);
////                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                        notificationManager.cancel(requestCode);
                        finish();

                    }
                });

                dialog.setNegativeButton("点错了。。", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }

                });
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
                        mAlarmBinder.taskFinish();
//                        unbindService(mServiceConnection);
////                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                        notificationManager.cancel(requestCode);
                        finish();
                    }
                });

                dialog1.setNegativeButton("点错啦！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                return;
        }
    }



    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAlarmBinder = (MyService.AlarmBinder) service;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
//                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(requestCode);
        alarmManager.cancel(pi);
    }
}
