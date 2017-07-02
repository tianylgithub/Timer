package com.example.tyl.timer.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tyl.timer.R;
import com.example.tyl.timer.util.MyDatabaseHelper;

import java.io.File;

import static com.example.tyl.timer.util.MyDatabaseHelper.updateInformation;

/**
 * Created by TYL on 2017/6/20.
 */

public class informationHint extends AppCompatActivity {

    int year;
    int month;
    int day;
    int hour;
    int minute;
    String information;
    TextView tiemText;
    TextView informationText;
    Button cloaseButton;

    @Override
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
        information = intent.getStringExtra("information");
        MyDatabaseHelper.sMyDatabaseHelper.updateInformation(year, month, day, hour, minute, 1);
        tiemText = (TextView) findViewById(R.id.show_time_text);
        informationText = (TextView) findViewById(R.id.shouw_information_text);
        cloaseButton = (Button) findViewById(R.id.closeButton);
        tiemText.setText("" + hour + "时" + minute + "秒");
        informationText.setText(information);


        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        final Notification notification = new NotificationCompat.Builder(this).setContentTitle("任务开始了")
                .setContentText("请全力完成自己的计划")
                .setSmallIcon(R.mipmap.ic_launcher).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)).setSound(Uri.fromFile(new File("/system/media/audio/ringtones/Luna.ogg"))).setVibrate(new long[]{0, 1000, 1000, 1000}).setLights(Color.GREEN, 1000, 1000).setAutoCancel(true).build();
        notificationManager.notify(1, notification);
        cloaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancel(1);
                finish();
            }
        });
    }
}
