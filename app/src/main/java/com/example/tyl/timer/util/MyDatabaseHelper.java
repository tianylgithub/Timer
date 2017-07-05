package com.example.tyl.timer.util;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;


/**数据库工具
 *
 * 1、创建数据库以及两张表（TABLE_INFO,TABLE_DAY）,
 *
 * 2、
 *
 * Created by TYL on 2017/6/11.
 *
 */


public class MyDatabaseHelper extends SQLiteOpenHelper  {

    static final int PLAN = 0;
    static final int  WORKING= 1;
    static final int WARING = 6;

    public static MyDatabaseHelper sMyDatabaseHelper = new MyDatabaseHelper(MyApplication.getContex(), "Timer.db", null, 1);
    public   static SQLiteDatabase mSQLiteDatabase = sMyDatabaseHelper.getWritableDatabase();

//创建每天的数据数据 库表   infromain信息    TABLE_INFO  information; TABLE_DAY days
    public static final String CREATE_TABLE1= "create table  if  not exists TABLE_INFO ("
            +"id integer primary key autoincrement,"
            + "year  integer,"
            + "month  integer,"
            + "day   integer,"
            + "hour  integer,"
            + "minute  integer,"
            +"lastTime  integer,"
            + "completed  integer,"//完成状态 0:还为到指定时间；1:正在进行中；2：已完成；3：未完成
            + "information   text )";

    //创建天单位数据库表      day信息
    public static final String CREATE_TABLE2= "create table   if  not exists TABLE_DAY ("
            +"id    integer  primary key autoincrement,"
            + "year  integer,"
            + "month  integer,"
            + "day   integer,"
            + "allThing  integer,"
            + "done  integer,"
            + "losed  integer,"
            + "theRest   integer,"
            + "status integer )";            //status   0:昨日     1:今日     2:明日

    //创建触发器
//对information表进行插入操作室，day表会更新
    public static final String CREATE_TRIGGER1 = "CREATE TRIGGER  if  not exists INFO_INSERT "
            + "AFTER INSERT ON  TABLE_INFO "
            + "FOR EACH ROW "
            + "BEGIN "
            + "UPDATE  TABLE_DAY SET allThing=(select count(*) from TABLE_INFO WHERE TABLE_INFO.year=new.year AND TABLE_INFO.month=new.month AND TABLE_INFO.day=new.day)," +
            "done=(select count(*) from TABLE_INFO WHERE  TABLE_INFO.year=new.year AND TABLE_INFO.month=new.month AND TABLE_INFO.day=new.day AND completed=2)," +
            "losed=(select count(*) from TABLE_INFO WHERE TABLE_INFO.year=new.year AND TABLE_INFO.month=new.month AND TABLE_INFO.day=new.day AND completed=3),"+
            "theRest=(select count(*) from TABLE_INFO WHERE TABLE_INFO.year=new.year AND TABLE_INFO.month=new.month AND TABLE_INFO.day=new.day AND completed=0)"+
            "  WHERE TABLE_DAY.year=new.year AND TABLE_DAY.month=new.month AND TABLE_DAY.day=new.day ;"
            + "END;";
//
    public static final String CREATE_TRIGGER2 = "CREATE TRIGGER if  not exists INFO_UPDATE "
            + "AFTER UPDATE ON  TABLE_INFO "
            +"FOR EACH ROW "
            +"BEGIN "
        + "UPDATE TABLE_DAY SET allThing=(select count(*)from TABLE_INFO WHERE TABLE_INFO.year=new.year AND TABLE_INFO.month=new.month AND TABLE_INFO.day=new.day)," +
        " done=(select count(*) from TABLE_INFO WHERE  TABLE_INFO.year=new.year AND TABLE_INFO.month=new.month AND TABLE_INFO.day=new.day AND completed=2)," +
        " losed=(select count(*) from TABLE_INFO WHERE TABLE_INFO.year=new.year AND TABLE_INFO.month=new.month AND TABLE_INFO.day=new.day AND completed=3)," +
        " theRest=(select count(*) from TABLE_INFO WHERE TABLE_INFO.year=new.year AND TABLE_INFO.month=new.month AND TABLE_INFO.day=new.day AND completed=0) " +
        " WHERE TABLE_DAY.year=new.year AND TABLE_DAY.month=new.month AND TABLE_DAY.day=new.day;"
        +"END;";

    public static final String CREATE_TRIGGER3= "CREATE TRIGGER  if  not exists INFO_DELETE "
            + "AFTER DELETE ON TABLE_INFO "
            +"FOR EACH ROW "
            +"BEGIN "
            + "UPDATE TABLE_DAY SET allThing=(select count(*)from TABLE_INFO WHERE TABLE_INFO.year=old.year AND TABLE_INFO.month=old.month AND TABLE_INFO.day=old.day)," +
            "done=(select count(*) from TABLE_INFO WHERE  TABLE_INFO.year=old.year AND TABLE_INFO.month=old.month AND TABLE_INFO.day=old.day AND completed=2)," +
            "losed=(select count(*) from TABLE_INFO WHERE TABLE_INFO.year=old.year AND TABLE_INFO.month=old.month AND TABLE_INFO.day=old.day AND completed=3)," +
            "theRest=(select count(*) from TABLE_INFO WHERE TABLE_INFO.year=old.year AND TABLE_INFO.month=old.month AND TABLE_INFO.day=old.day AND completed=0)" +
            " WHERE TABLE_DAY.year=old.year AND TABLE_DAY.month=old.month AND TABLE_DAY.day=old.day;"
            +"END;";

//构造方法
    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }
//拿到数据库
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE1);
        db.execSQL(CREATE_TABLE2);
        db.execSQL(CREATE_TRIGGER1);
        Log.d("trigger1", "trigger1创建成功");
        db.execSQL(CREATE_TRIGGER2);
        Log.d("trigger2", "trigger2创建成功");
        db.execSQL(CREATE_TRIGGER3);
        Log.d("trigger2", "trigger2创建成功");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 根据年月日获的某一天的数据
     *
     * @param year
     * @param month
     * @param day
     * @return
     */

    public LinkedList<Information> getList(int year, int month, int day) {

          LinkedList<Information> mInformationsList = new LinkedList<Information>();
        Cursor cursor = mSQLiteDatabase.query("TABLE_INFO", null, "year=? AND month=? AND day=?",new String[]{year+"",month+"",day+""},null,null,null);
        if(cursor.moveToFirst()){
            do {
                Information information = new Information();
                //遍历cusor对象，取出数据变放存
                information.setId(cursor.getInt(cursor.getColumnIndex("id")));
                information.setYear(cursor.getInt(cursor.getColumnIndex("year")));
                information.setMonth(cursor.getInt(cursor.getColumnIndex("month")));
                information.setDay(cursor.getInt(cursor.getColumnIndex("day")));
                information.setHour(cursor.getInt(cursor.getColumnIndex("hour")));
                information.setMinute(cursor.getInt(cursor.getColumnIndex("minute")));
                information.setLastTme(cursor.getInt(cursor.getColumnIndex("lastTime")));
                information.setCompleted(cursor.getInt(cursor.getColumnIndex("completed")));
                information.setInformation(cursor.getString(cursor.getColumnIndex("information")));
                mInformationsList.add(information);
            } while (cursor.moveToNext());
            cursor.close();
            return mInformationsList;
        }
        cursor.close();
        return  new LinkedList<Information>();
    }

    /**
     * 根据年月日时分删除某一条具体的数据
     *
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     */
    public void deletInformation(int year, int month, int day, int hour, int minute) {
        mSQLiteDatabase.delete("TABLE_INFO","year=? AND month=? AND day=? AND hour=? AND minute=?",new  String[]{year+"",month+"",day+"",hour+"",minute+""});

    }

    /**
     * 根据年月日时分增加某一条具体数据
     *
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     */
    public void addInformation(int year, int month, int day, int hour, int minute,int   lastTime,int  completed,String information) {
        ContentValues values = new ContentValues();
        values.put("year", year);
        values.put("month", month);
        values.put("day", day);
        values.put("hour", hour);
        values.put("minute", minute);
        values.put("lastTime",lastTime);
        values.put("completed", completed);
        values.put("information", information);
        mSQLiteDatabase.insert("TABLE_INFO", null, values);
    }

    /**
     * 根据年月日时分更新某一条information的状态
     *
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @return
     */

    public   static    void   updateInformation(int year, int month, int day, int hour, int minute, int completed) {
        ContentValues values = new ContentValues();
        values.put("completed",completed);
        mSQLiteDatabase.update("TABLE_INFO",values,"year=? AND month=? AND day=? AND hour=? AND minute=?",new String[]{year+"",month+"",day+"",hour+"",minute+"" });
    }


    public static void updateInfoCompById(int id, int completed) {
        ContentValues values = new ContentValues();
        values.put("completed", completed);
        mSQLiteDatabase.update("TABLE_INFO", values, "id=?", new String[]{id + ""});

    }


    /**
     * 获取每天的概况
     *
     * @return
     */
    public   LinkedList<Day> getDays() {
        LinkedList<Day> mDayInfoList = new LinkedList<Day>();
        Cursor cursor = mSQLiteDatabase.query("TABLE_DAY", null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do {
                Day date = new Day();
                date.setYear(cursor.getInt(cursor.getColumnIndex("year")));
                date.setMonth(cursor.getInt(cursor.getColumnIndex("month")));
                date.setDay(cursor.getInt(cursor.getColumnIndex("day")));
                date.setAll(cursor.getInt(cursor.getColumnIndex("allThing")));
                date.setLosed(cursor.getInt(cursor.getColumnIndex("losed")));
                date.setDone(cursor.getInt(cursor.getColumnIndex("done")));
                date.setTheRest(cursor.getInt(cursor.getColumnIndex("theRest")));
                date.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
                mDayInfoList.add(date);
            } while (cursor.moveToNext());
            cursor.close();
            return mDayInfoList;
        }
        cursor.close();
        return new LinkedList<Day>();
    }


    public static Day getDay(Day day_year_month_day) {
        Cursor cursor = mSQLiteDatabase.query("TABLE_DAY", null, "year=? AND month=? AND day=?", new String[]{day_year_month_day.getYear() + "", day_year_month_day.getMonth() + "", day_year_month_day.getDay() + ""}, null, null, null, null);
        Day date = new Day();
        cursor.moveToFirst();
        date.setYear(cursor.getInt(cursor.getColumnIndex("year")));
        date.setMonth(cursor.getInt(cursor.getColumnIndex("month")));
        date.setDay(cursor.getInt(cursor.getColumnIndex("day")));
        date.setAll(cursor.getInt(cursor.getColumnIndex("allThing")));
        date.setLosed(cursor.getInt(cursor.getColumnIndex("losed")));
        date.setDone(cursor.getInt(cursor.getColumnIndex("done")));
        date.setTheRest(cursor.getInt(cursor.getColumnIndex("theRest")));
        date.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
        cursor.close();
        return date;
    }

    public static void  addDay(Day day) {
        ContentValues values = new ContentValues();
        values.put("year", day.getYear());
        values.put("month", day.getMonth());
        values.put("day", day.getDay());
        values.put("allThing",day.getAll());
        values.put("done",day.getDone());
        values.put("theRest",day.getTheRest());
        values.put("losed",day.getLosed());
        values.put("status",day.getStatus());
        mSQLiteDatabase.insert("TABLE_DAY", null, values);
    }
//    //    public static final String CREATE_TABLE2= "create table TABLE_DAY ("
//    +"id    integer  primary key autoincrement,"
//            + "year  integer,"
//            + "month  integer,"
//            + "day   integer,"
//            + "allThing  integer,"
//            + "done  integer,"
//            + "losed  integer,"
//            + "theRest   integer,"
//            + "status integer )";            //status   0:昨日     1:今日     2:明日
    /**用来更新status状态：  0昨日   1现在  2未来
     *
     * @param date
     * @param status
     */
    public static   void updateDay(Day date,int status) {
        int year = date.getYear();
        int month = date.getMonth();
        int day = date.getDay();
        ContentValues values = new ContentValues();
        values.put("status", status);
        mSQLiteDatabase.update("TABLE_DAY", values, "year=? AND month=? AND day=?", new String[]{year + "", month + "", day + ""});
    }


    public static boolean haveDay(Day day) {

        Cursor cursor = mSQLiteDatabase.query("TABLE_DAY", null, "year=? AND  month=? AND  day=?", new String[]{day.getYear() + "", day.getMonth()+ "", day.getDay() + ""}, null, null, null);

        if(cursor.moveToFirst()){
            cursor.close();
            return true;
            }
        cursor.close();
        return false;
    }


    public  static  boolean haveInfo(Information information) {
        Cursor cursor = mSQLiteDatabase.query("TABLE_INFO", null, "year=? AND month=? AND day=? AND hour=? AND minute=?", new String[]{information.getYear() + "", information.getMonth() + "", information.getDay() + "", information.getHour() + "", information.getMinute() + ""}, null, null, null, null);
        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }









//    public static ArrayList<Information> getTrableMaker() {
//        Cursor  cursor=mSQLiteDatabase.query("TABLE_INFO",null,"completed=? OR ? OR ?")
//
//
//    }

    /**
     * 获得状态为0，1,6  information
     * @return
     */
    public static ArrayList<Information> getTargetInfo016() {
        Cursor cursor=mSQLiteDatabase.query("TABLE_INFO",null,"completed=? OR ? OR ?",new String[]{0+"",1+"",6+""},null,null,null);
        ArrayList<Information> informationsList = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                Information information = new Information();
                //遍历cusor对象，取出数据变放存
                information.setId(cursor.getInt(cursor.getColumnIndex("id")));
                information.setYear(cursor.getInt(cursor.getColumnIndex("year")));
                information.setMonth(cursor.getInt(cursor.getColumnIndex("month")));
                information.setDay(cursor.getInt(cursor.getColumnIndex("day")));
                information.setHour(cursor.getInt(cursor.getColumnIndex("hour")));
                information.setMinute(cursor.getInt(cursor.getColumnIndex("minute")));
                information.setLastTme(cursor.getInt(cursor.getColumnIndex("lastTime")));
                information.setCompleted(cursor.getInt(cursor.getColumnIndex("completed")));
                information.setInformation(cursor.getString(cursor.getColumnIndex("information")));
                informationsList.add(information);
            } while (cursor.moveToNext());
            cursor.close();
            return informationsList;
        }
        cursor.close();
        return  informationsList;
    }

    /**
     * 获得状态为0、1的信息
     * @return
     */



    public static ArrayList<Information> getTargetInfo01() {
        Cursor cursor=mSQLiteDatabase.query("TABLE_INFO",null,"completed=? OR ? ",new String[]{0+"",1+""},null,null,null);
        ArrayList<Information> informationsList = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                Information information = new Information();
                //遍历cusor对象，取出数据变放存
                information.setId(cursor.getInt(cursor.getColumnIndex("id")));
                information.setYear(cursor.getInt(cursor.getColumnIndex("year")));
                information.setMonth(cursor.getInt(cursor.getColumnIndex("month")));
                information.setDay(cursor.getInt(cursor.getColumnIndex("day")));
                information.setHour(cursor.getInt(cursor.getColumnIndex("hour")));
                information.setMinute(cursor.getInt(cursor.getColumnIndex("minute")));
                information.setLastTme(cursor.getInt(cursor.getColumnIndex("lastTime")));
                information.setCompleted(cursor.getInt(cursor.getColumnIndex("completed")));
                informationsList.add(information);
            } while (cursor.moveToNext());
            cursor.close();
            return informationsList;
        }
        cursor.close();
        return  informationsList;
    }



















    /**
     * 更新日期
     */

    public  static  void updateDate() {
        int year = TimeUtil.getYear();
        int month = TimeUtil.getMonth();
        int day = TimeUtil.getDay();

        int newDAY=0;
        Cursor cursor = mSQLiteDatabase.query("TABLE_DAY", null, "status=? OR ?", new String[]{1+"",2+""}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                ContentValues values = new ContentValues();
                int year1 = cursor.getInt(cursor.getColumnIndex("year"));
                int minth1 = cursor.getInt(cursor.getColumnIndex("month"));
                int day1 = cursor.getInt(cursor.getColumnIndex("day"));
                int id = cursor.getInt(cursor.getColumnIndex("id"));

                int i = 500 * (year - year1) + 35 * (month - minth1) + (day - day1);

                if ( i> 0) {          //在今天之前的日子
                    values.put("status",0);
                    mSQLiteDatabase.update("TABLE_DAY", values, "id=?", new String[]{id + ""});
                } else if (i == 0) {
                    values.put("status", 1);
                    mSQLiteDatabase.update("TABLE_DAY", values, "id=?", new String[]{id + ""});
                    newDAY = newDAY + 1;
                }
            } while (cursor.moveToNext());
            cursor.close();
            if (newDAY == 0) {                          //不存在今天数据，插入新的一天
                ContentValues values = new ContentValues();
                values.put("year", year);
                values.put("month", month);
                values.put("day", day);
                values.put("allThing", 0);
                values.put("done", 0);
                values.put("losed", 0);
                values.put("theRest", 0);
                values.put("status", 1);
                MyDatabaseHelper.mSQLiteDatabase.insert("TABLE_DAY", null, values);
            }
        }else {                                          //不存在数据，第一次启动app
            ContentValues values = new ContentValues();
            values.put("year", year);
            values.put("month", month);
            values.put("day", day);
            values.put("allThing", 0);
            values.put("done", 0);
            values.put("losed", 0);
            values.put("theRest", 0);
            values.put("status", 1);
            MyDatabaseHelper.mSQLiteDatabase.insert("TABLE_DAY", null, values);
        }
    }




    /**
     * 开机初始化信息
     */


   public static void initial() {
        updateDate();
        ArrayList<Information> informationsList = sMyDatabaseHelper.getTargetInfo01();     //查找数据库中状态为PLAN（0），WORKING(1)
        if (informationsList.size() > 0) {
            for(Information information:informationsList) {
                int id = information.getId();
                int year = information.getYear();
                int month = information.getMonth();
                int day = information.getDay();
                int hour = information.getHour();
                int minute = information.getMinute();
                int lastTime = information.getLastTme();
                int completed = information.getCompleted();
                long begin = TimeUtil.getMillis(year, month, day, hour, minute);
                switch (completed) {
                    case PLAN://状态是未来的，检查是否有漏过的计划

                        if (begin + lastTime * 60000 - TimeUtil.getNowMillis() >= 0) {                           //如果结束时间在当前时间之后

                            if (begin - TimeUtil.getNowMillis() <= 0) {                              //开始时间在当前时间之前
                                sMyDatabaseHelper.updateInfoCompById(id, 1);
                            }

                        } else {                                                                       //结束时间在当前时间之前
                            sMyDatabaseHelper.updateInfoCompById(id, 6);
                        }
                        break;
                    case WORKING:                      //已经开始的计划

                        if (begin + lastTime * 60000 - TimeUtil.getNowMillis() < 0) {                            //已经开始并且结束的任务
                            sMyDatabaseHelper.updateInfoCompById(id, 6);
                        }
                        break;
                    default:
                        break;
                }

            }
        }
    }
}
