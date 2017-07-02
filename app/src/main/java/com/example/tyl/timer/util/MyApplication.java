package com.example.tyl.timer.util;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.tyl.timer.service.MyService;

/**
 * Created by TYL on 2017/6/23.
 */

public class MyApplication extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        sContext = getApplicationContext();

    }

    public static Context getContex() {
        return sContext;
    }
}
