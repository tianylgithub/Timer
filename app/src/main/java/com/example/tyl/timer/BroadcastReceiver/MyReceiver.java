package com.example.tyl.timer.BroadcastReceiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.tyl.timer.service.MyService;
import com.example.tyl.timer.util.MyApplication;


public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //开启day服务，每日自动创建day数据
        Intent startDayIntent = new Intent(MyApplication.getContex(), MyService.class);
        MyApplication.getContex().startService(startDayIntent);
//        Log.d("MyReciver", "接收器onreceive结束");
    }
}
