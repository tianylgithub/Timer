package com.example.tyl.timer.util;

import android.os.SystemClock;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static java.util.Calendar.getInstance;

/**
 * Created by TYL on 2017/6/12.
 */
//获取系统时间信息的工具

public class TimeUtil {




    //获取 单个(int)  年  月  日
    public static int getYear() {
        Calendar mCalendar =getInstance();
        return  mCalendar.get(Calendar.YEAR);
    }

    public static int getMonth() {
        Calendar mCalendar = getInstance();

        return mCalendar.get(Calendar.MONTH)+1;
    }

    public static int getDay() {

        Calendar mCalendar = getInstance();
        return mCalendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getHour() {

        Calendar mCalendar = getInstance();
        return mCalendar.get(Calendar.HOUR_OF_DAY);
    }

    public   static    int getMinuts()

   {
       Calendar mCalendar = getInstance();
        return mCalendar.get(Calendar.MINUTE);

    }

    public static long getNowMillis() {
        Calendar mCalendar = getInstance();
        return mCalendar.getTimeInMillis();
    }




//获取总的毫秒数
    public static   long   getMillis(int year,int month,int day,int hour,int minute){
        Calendar mCalendar = getInstance();
        mCalendar.set(year,(month-1),day,hour,minute);
        long millis = mCalendar.getTimeInMillis();
        Log.d("1970年开始", millis + "");
        return millis;
    }

    /**添加日期时候的时候对获得前一天的年月日
     *
     * @return       返回昨天的日期
     */
    public static Day  getYesterday(){
        Calendar ca = getInstance();
        ca.add(Calendar.DAY_OF_MONTH, -1);
        int year = ca.get(Calendar.YEAR);
        int month = ca.get(Calendar.MONTH)+1;
        int day = ca.get(Calendar.DAY_OF_MONTH);
        Day date = new Day();
        date.setYear(year);
        date.setMonth(month);
        date.setDay(day);
        return date;
    }

    public static Day getToday() {

        Calendar calendar = getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Day date = new Day();
        date.setYear(year);
        date.setMonth(month);
        date.setDay(day);
        return date;
    }

}