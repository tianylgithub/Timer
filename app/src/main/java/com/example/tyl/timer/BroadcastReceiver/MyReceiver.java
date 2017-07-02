package com.example.tyl.timer.BroadcastReceiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.tyl.timer.service.MyService;

import com.example.tyl.timer.util.MyApplication;
import com.example.tyl.timer.util.TimeUtil;


public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("asdf", "接收器onreceive开始");
        TimeUtil forCurrentTime = new TimeUtil();
        int day = forCurrentTime.getDay();
        //开启day服务，每日自动创建day数据
        Intent startDayIntent = new Intent(MyApplication.getContex(), MyService.class);
        startDayIntent.putExtra("day", day);
        MyApplication.getContex().startService(startDayIntent);
        Log.d("asdf", "接收器onreceive结束");
    }
}
