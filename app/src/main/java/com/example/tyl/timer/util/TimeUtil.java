package com.example.tyl.timer.util;

import java.util.Calendar;

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
//        Log.d("1970年开始", millis + "");
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

    /**
     * 获得今天的年月日
     * @return
     */


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

    /**
     * 根据年份获得该年多少天
     * @param year
     * @return
     */
    public static int getDayNumByYear(int year) {
        if (year % 4 == 0) {
            if (year % 100 == 0) {
                return 365;
            }
            return 366;
        }
        return 365;
    }

    /**
     * 根据年份月份获得该月最大数
     * @param year
     * @param month
     * @return
     */

    public static int getMaxDayNumByYM(int year, int month) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            default :                                   //此时为2月
                if (getDayNumByYear(year) == 365) {
                    return 28;
                }
                return 29;
        }

    }

    /**
     * 获取今天是今年中的哪一天
     * @return
     */
    public static int getDayInYear() {
        Calendar calendar = getInstance();
        int dayNum = calendar.get(Calendar.DAY_OF_YEAR);
        return dayNum;
    }


    /**
     * 获取今天是当月中的哪一天
     * @return
     */
    public static int getDayNumInMonth() {
        Calendar calendar = getInstance();
        int dayNum = calendar.get(Calendar.DAY_OF_MONTH);
        return dayNum;
    }


    /**
     * 根据日期得到是当年的第几天
     * @return
     */

    public static int getDateNumByYMD(int year,int month,int day) {

        if (month == 1) {
            return day;
        }else {
            int num=0;
            for (int m = 1; m<month;m++) {
                num = num+getMaxDayNumByYM(year, m);
            }
            return num + day;
        }
    }
    /**
     *  year ,month,day,1和2之间相差多少天  1 < 2
     * @param year1
     * @param month1
     * @param day1
     * @param year2
     * @param month2
     * @param day2
     * @return
     */
    public static int dayNumFromDate(int year1, int month1, int day1, int year2, int month2, int day2) {
        if (year1 == year2) {                                                                       //如果是同一年
         return    getDateNumByYMD(year2, month2, day2) - getDateNumByYMD(year1, month1, day1)+1;
        }else {
            int num=0;
            for(int m= year1;m<year2;m++) {
                num = num + getDayNumByYear(m);
            }
            return num + getDateNumByYMD(year2, month2, day2) - getDateNumByYMD(year1, month1, day1) + 1;
        }
    }
}