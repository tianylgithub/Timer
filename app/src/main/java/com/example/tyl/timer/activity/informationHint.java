package com.example.tyl.timer.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tyl.timer.R;
import com.example.tyl.timer.fragment.EditorFragment;
import com.example.tyl.timer.service.MyService;
import com.example.tyl.timer.util.Day;
import com.example.tyl.timer.util.Information;
import com.example.tyl.timer.util.MyDatabaseHelper;

import java.io.File;
import java.util.List;

/**
 * Created by TYL on 2017/6/20.
 */

public class informationHint extends AppCompatActivity {

    int year;
    int month;
    int day;
    int hour;
    int minute;
    int lastTime;
    String information;
    TextView tiemText;
    TextView informationText;
    Button cloaseButton;
    MyService.AlarmBinder mAlarmBinder;
    int requestCode;

    NotificationManager notificationManager;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_hint);
        Log.d("informationHint", "提示执行啦");
        Intent intent = getIntent();
        year = intent.getIntExtra("year", -1);
        month = intent.getIntExtra("month", -1);
        day = intent.getIntExtra("day", -1);
        hour = intent.getIntExtra("hour", -1);
        minute = intent.getIntExtra("minute", -1);
        lastTime = intent.getIntExtra("lastTime", -1);
        information = intent.getStringExtra("information");

        tiemText = (TextView) findViewById(R.id.show_time_text);
        informationText = (TextView) findViewById(R.id.shouw_information_text);
        cloaseButton = (Button) findViewById(R.id.closeButton);
        tiemText.setText(month+"-"+day+" " + hour + ":" + minute+"  持续时间:"+lastTime);
        informationText.setText(information);


        MyDatabaseHelper.sMyDatabaseHelper.updateInformation(year, month, day, hour, minute, 1);

        if (EditorFragment.getDaysAdapter()!= null) {
            int dayPosition = getDayPosition(year, month, day);
            if(dayPosition!=-1) {
                Day day = EditorFragment.getmDays().get(dayPosition);
                day.minusTheRest();
                EditorFragment.getDaysAdapter().notifyDataSetChanged();
            }
        }

        if (ShowInformationActivity.getmAdapter() != null) {
            int infoPosition = getInfoPosition(year, month, day, hour, minute);
            if (infoPosition != -1) {
                Information information = ShowInformationActivity.getEditorlist().get(infoPosition);
                information.setCompleted(1);
                ShowInformationActivity.getmAdapter().notifyDataSetChanged();
            }

        }
        Intent i = new Intent(this, MyService.class);
        bindService(i, mServiceConnection, BIND_AUTO_CREATE);

// 开启通知提醒任务开始
         notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        final Notification notification = new NotificationCompat.Builder(this).setContentTitle("任务开始了")
                .setContentText("请全力完成自己的计划")
                .setSmallIcon(R.drawable.infohint).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.infohint)).setSound(Uri.fromFile(new File("/system/media/audio/rington" +
                        "es/Luna.ogg"))).setVibrate(new long[]{0, 1000, 1000, 1000}).setLights(Color.GREEN, 1000, 1000).setAutoCancel(true).setStyle(new NotificationCompat.BigTextStyle().bigText("" +
                        month+"-"+day+" "+hour+":"+minute+" "+" 持续时间:"+lastTime+"|"+information)).build();

        requestCode = Integer.valueOf("" + hour + day + year + minute);

        notificationManager.notify(requestCode, notification);
        cloaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                notificationManager.cancel(requestCode);
                notificationManager.cancel(requestCode);
               informationHint.this.finish();
            }
        });
    }
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAlarmBinder = (MyService.AlarmBinder) service;
            mAlarmBinder.changeForeground();
            unbindService(mServiceConnection);
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        finish();
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
