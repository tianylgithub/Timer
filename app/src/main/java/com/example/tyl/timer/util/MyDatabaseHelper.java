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
//info 状态
    static final int    NOTHING = -1;
    static final int  PLAN= 0;
    static final int WORKING = 1;
    static final int FINISHED = 2;
    static final int UNFINISHED = 3;
    static final int WARING = 6;
    // day 状态
    static final int PAST = 0;
    static final int NOW = 1;
    static final int FUTURE = 2;

    public static MyDatabaseHelper sMyDatabaseHelper = new MyDatabaseHelper(MyApplication.getContex(), "Timer.db", null, 1);
    public   static SQLiteDatabase mSQLiteDatabase = sMyDatabaseHelper.getWritableDatabase();

//创建每天的数据数据 库表   infromain信息    TABLE_INFO  information; TABLE_DAY days
    public static final String CREATE_TABLE1= "create table  if  not exists TABLE_INFO ("
            +"id integer primary key autoincrement,"
             +"dayID  integer,"
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
            + "AFTER INSERT  ON TABLE_INFO "
            + "FOR EACH ROW "
            + "BEGIN "
            + "UPDATE  TABLE_DAY SET allThing=(select count(*) from TABLE_INFO WHERE TABLE_INFO.dayID=new.dayID)," +
            "done=(select count(*) from TABLE_INFO WHERE  TABLE_INFO.dayID=new.dayID AND completed=2)," +
            "losed=(select count(*) from TABLE_INFO WHERE TABLE_INFO.dayID=new.dayID AND completed=3),"+
            "theRest=(select count(*) from TABLE_INFO WHERE TABLE_INFO.dayID=new.dayID AND completed=0) "+
            "  WHERE TABLE_DAY.id=new.dayID ;"
            + "END;";
//
    public static final String CREATE_TRIGGER2 = "CREATE TRIGGER if  not exists INFO_UPDATE "
            + "AFTER UPDATE ON  TABLE_INFO "
            +"FOR EACH ROW "
            +"BEGIN "
        + "UPDATE TABLE_DAY SET allThing=(select count(*)from TABLE_INFO WHERE TABLE_INFO.dayID=new.dayID), " +
        " done=(select count(*) from TABLE_INFO WHERE  TABLE_INFO.dayID=new.dayID AND completed=2), " +
        " losed=(select count(*) from TABLE_INFO WHERE TABLE_INFO.dayID=new.dayID AND completed=3), " +
        " theRest=(select count(*) from TABLE_INFO WHERE TABLE_INFO.dayID=new.dayID AND completed=0) " +
        " WHERE TABLE_DAY.id=new.dayID; "
        +"END;";

    public static final String CREATE_TRIGGER3= "CREATE TRIGGER  if  not exists INFO_DELETE "
            + "AFTER DELETE ON TABLE_INFO "
            +"FOR EACH ROW "
            +"BEGIN "
            + "UPDATE TABLE_DAY SET allThing=(select count(*)from TABLE_INFO WHERE TABLE_INFO.dayID=old.dayID)," +
            "done=(select count(*) from TABLE_INFO WHERE  TABLE_INFO.dayID=old.dayID AND completed=2)," +
            "losed=(select count(*) from TABLE_INFO WHERE TABLE_INFO.dayID=old.dayID AND completed=3)," +
            "theRest=(select count(*) from TABLE_INFO WHERE TABLE_INFO.dayID=old.dayID AND completed=0) " +
            " WHERE  TABLE_DAY.id=old.dayID;"
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

//    /**
//     * 根据年月日获的某一天的数据
//     *
//     * @param year
//     * @param month
//     * @param day
//     * @return
//     */
//
//    public static LinkedList<Information> getListByYMD(int year, int month, int day) {
//
//          LinkedList<Information> mInformationsList = new LinkedList<Information>();
//        Cursor cursor = mSQLiteDatabase.query("TABLE_INFO", null, "year=? AND month=? AND day=?",new String[]{year+"",month+"",day+""},null,null,null);
//        if(cursor.moveToFirst()){
//            do {
//                Information information = new Information();
//                //遍历cusor对象，取出数据变放存
//                information.setId(cursor.getInt(cursor.getColumnIndex("id")));
//                information.setYear(cursor.getInt(cursor.getColumnIndex("year")));
//                information.setMonth(cursor.getInt(cursor.getColumnIndex("month")));
//                information.setDay(cursor.getInt(cursor.getColumnIndex("day")));
//                information.setHour(cursor.getInt(cursor.getColumnIndex("hour")));
//                information.setMinute(cursor.getInt(cursor.getColumnIndex("minute")));
//                information.setLastTime(cursor.getInt(cursor.getColumnIndex("lastTime")));
//                information.setCompleted(cursor.getInt(cursor.getColumnIndex("completed")));
//                information.setInformation(cursor.getString(cursor.getColumnIndex("information")));
//                information.setDayID(cursor.getInt(cursor.getColumnIndex("dayID")));
//                mInformationsList.add(information);
//            } while (cursor.moveToNext());
//            cursor.close();
//            return mInformationsList;
//        }
//        cursor.close();
//        return  new LinkedList<Information>();
//    }


    public static int getStatusByDayID(int dayID) {
   Cursor cursor= mSQLiteDatabase.query("TABLE_DAY", null, "id=?", new String[]{dayID + ""}, null, null, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(cursor.getColumnIndex("status"));
        }
        return -1;
    }



    public static boolean have_search_haveData(String year, String month, String day, String information) {
        String yearUtil;
        String monthUtil;
        String dayUtil;

        if (year==null) {
            yearUtil = "year like ?";
            year = "%";
        }else {
            yearUtil = "year = ?";
        }

        if (month==null) {
            monthUtil = " AND month like ?";
            month = "%";
        }else {
            monthUtil = " AND month = ?";
        }

        if (day==null) {
            dayUtil = " AND day like ?";
            day = "%";
        }else {
            dayUtil = " AND day = ?";
        }

        if(information!=null) {
            Cursor cursor = mSQLiteDatabase.query("TABLE_INFO", null, yearUtil+monthUtil+dayUtil+" AND information like ?", new String[]{year,month,day,"%"+information+"%"}, null, null,null);
            if (cursor.moveToFirst()) {
                return true;
            }else {
                return false;}
        }else {
           Cursor cursor= mSQLiteDatabase.query("TABLE_DAY", null, yearUtil+monthUtil+dayUtil, new String[]{year, month, day}, null, null, null);
            if (cursor.moveToFirst()) {
                return true;
            }else {
                return false;
            }
        }
    }









    public   static     ArrayList searchData(String year,String month,String day,String information)
    {
        String yearUtil;
        String monthUtil;
        String dayUtil;

        if (year==null) {
            yearUtil = "year like ?";
            year = "%";
        }else {
            yearUtil = "year = ?";
        }

        if (month==null) {
            monthUtil = " AND month like ?";
            month = "%";
        }else {
            monthUtil = " AND month = ?";
        }

        if (day==null) {
            dayUtil = " AND day like ?";
            day = "%";
        }else {
            dayUtil = " AND day = ?";
        }

        ArrayList<Information> ArrayList = new ArrayList<Information>();
        ArrayList<Day> ArrayList1=new ArrayList<Day>();
        if (information != null) {                                              //information有值，返回infolist
Cursor  cursor=mSQLiteDatabase.query("TABLE_INFO", null, yearUtil+monthUtil+dayUtil+" AND information like ?", new String[]{year,month,day,"%"+information+"%"}, null, null, "year DESC,month DESC,day DESC ,hour DESC, minute DESC,lastTime DESC");
            if (cursor.moveToFirst()) {
                do {
                    Information informationNew = new Information();
                    informationNew.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    informationNew.setDayID(cursor.getInt(cursor.getColumnIndex("dayID")));
                    informationNew.setYear(cursor.getInt(cursor.getColumnIndex("year")));
                    informationNew.setMonth(cursor.getInt(cursor.getColumnIndex("month")));
                    informationNew.setDay(cursor.getInt(cursor.getColumnIndex("day")));
                    informationNew.setHour(cursor.getInt(cursor.getColumnIndex("hour")));
                    informationNew.setMinute(cursor.getInt(cursor.getColumnIndex("minute")));
                    informationNew.setLastTime(cursor.getInt(cursor.getColumnIndex("lastTime")));
                    informationNew.setCompleted(cursor.getInt(cursor.getColumnIndex("completed")));
                    informationNew.setInformation(cursor.getString(cursor.getColumnIndex("information")));
                    ArrayList.add(informationNew);
                } while (cursor.moveToNext());
            }
            cursor.close();
            return ArrayList;

        }else {                                                              //此时information没有值，返回daylist
            Cursor cursor = mSQLiteDatabase.query("TABLE_DAY", null, yearUtil+monthUtil+dayUtil, new String[]{year, month, day}, null, null, "year DESC, month  DESC,day DESC");
            if (cursor.moveToFirst()) {
                do {
                    Day day1 = new Day();
                    day1.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    day1.setYear(cursor.getInt(cursor.getColumnIndex("year")));
                    day1.setMonth(cursor.getInt(cursor.getColumnIndex("month")));
                    day1.setDay(cursor.getInt(cursor.getColumnIndex("day")));
                    day1.setAll(cursor.getInt(cursor.getColumnIndex("allThing")));
                    day1.setDone(cursor.getInt(cursor.getColumnIndex("done")));
                    day1.setLosed(cursor.getInt(cursor.getColumnIndex("losed")));
                    day1.setTheRest(cursor.getInt(cursor.getColumnIndex("theRest")));
                    day1.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
                    ArrayList1.add(day1);
                } while (cursor.moveToNext());
            }
            cursor.close();
            return ArrayList1;
        }
    }




    public static boolean havaList(Day dayUsed) {
        Cursor cursor = mSQLiteDatabase.query("TABLE_INFO", null, "dayID=?", new String[]{dayUsed.getId() + ""}, null, null, null);
        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }








    public static LinkedList<Information> getListByDayID(int ID) {
        LinkedList<Information> mInformationsList = new LinkedList<Information>();
        Cursor cursor = mSQLiteDatabase.query("TABLE_INFO", null, "dayID=?",new String[]{ID+""},null,null,null);
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
                information.setLastTime(cursor.getInt(cursor.getColumnIndex("lastTime")));
                information.setCompleted(cursor.getInt(cursor.getColumnIndex("completed")));
                information.setInformation(cursor.getString(cursor.getColumnIndex("information")));
                information.setDayID(cursor.getInt(cursor.getColumnIndex("dayID")));
                mInformationsList.add(information);
            } while (cursor.moveToNext());
            cursor.close();
            return mInformationsList;
        }
        cursor.close();
        return mInformationsList;
    }
//    /**
//     * 根据年月日时分删除某一条具体的数据
//     *
//     * @param year
//     * @param month
//     * @param day
//     * @param hour
//     * @param minute
//     */
////    public void deletInformation(int year, int month, int day, int hour, int minute) {
//        mSQLiteDatabase.delete("TABLE_INFO","year=? AND month=? AND day=? AND hour=? AND minute=?",new  String[]{year+"",month+"",day+"",hour+"",minute+""});
//
//    }

    public static void deletInformation(Information infoUsed) {
        int id = infoUsed.getId();
        mSQLiteDatabase.delete("TABLE_INFO", "id=?", new String[]{id + ""});
    }




//    /**
//     * 根据年月日时分增加某一条具体数据
//     *
//     * @param year
//     * @param month
//     * @param day
//     * @param hour
//     * @param minute
//     */
//    public void addInformation(int year, int month, int day, int hour, int minute,int   lastTime,int  completed,String information) {
//        ContentValues values = new ContentValues();
//        values.put("year", year);
//        values.put("month", month);
//        values.put("day", day);
//        values.put("hour", hour);
//        values.put("minute", minute);
//        values.put("lastTime",lastTime);
//        values.put("completed", completed);
//        values.put("information", information);
//        mSQLiteDatabase.insert("TABLE_INFO", null, values);
//    }


    public static int addInfoByinfomation(Information infoUsed) {
        ContentValues values = new ContentValues();
        values.put("dayID", infoUsed.getDayID());
        values.put("year", infoUsed.getYear());
        values.put("month", infoUsed.getMonth());
        values.put("day", infoUsed.getDay());
        values.put("hour", infoUsed.getHour());
        values.put("minute", infoUsed.getMinute());
        values.put("lastTime", infoUsed.getLastTime());
        values.put("completed", infoUsed.getCompleted());
        values.put("information", infoUsed.getInformation());
   long i= mSQLiteDatabase.insert("TABLE_INFO", null, values);
        int id = (int) i;
        return id;

    }


//    /**
//     *
//     * @param id
//     * @param completed
//     */
//    public   static    void   updateInformation(int id, int completed) {
//        ContentValues values = new ContentValues();
//        values.put("completed",completed);
//        mSQLiteDatabase.update("TABLE_INFO",values,"id=?",new String[]{id+"" });
//    }



    public static void updateInformationById(int id, int completed) {
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
                date.setId(cursor.getInt(cursor.getColumnIndex("id")));
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


//    public static Day getDayByDate(Day day_year_month_day) {
//        Cursor cursor = mSQLiteDatabase.query("TABLE_DAY", null, "year=? AND month=? AND day=?", new String[]{day_year_month_day.getYear() + "", day_year_month_day.getMonth() + "", day_year_month_day.getDay() + ""}, null, null, null, null);
//        Day date = new Day();
//        if (cursor.moveToFirst()) {
//            date.setYear(cursor.getInt(cursor.getColumnIndex("year")));
//            date.setMonth(cursor.getInt(cursor.getColumnIndex("month")));
//            date.setDay(cursor.getInt(cursor.getColumnIndex("day")));
//            date.setAll(cursor.getInt(cursor.getColumnIndex("allThing")));
//            date.setLosed(cursor.getInt(cursor.getColumnIndex("losed")));
//            date.setDone(cursor.getInt(cursor.getColumnIndex("done")));
//            date.setTheRest(cursor.getInt(cursor.getColumnIndex("theRest")));
//            date.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
//            cursor.close();
//            return date;
//        }
//        return null;
//    }

    public static Day getDayByID(int dayID) {
        Cursor cursor = mSQLiteDatabase.query("TABLE_DAY", null, "id=?", new String[]{dayID + ""}, null, null, null, null);
        Day date = new Day();
       if( cursor.moveToFirst()){
           date.setId(cursor.getInt(cursor.getColumnIndex("id")));
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
        return null;
    }

    /**
     *
     * @param day
     * @return  返回id key
     */

    public static int   addDay(Day day) {

        ContentValues values = new ContentValues();
        values.put("year", day.getYear());
        values.put("month", day.getMonth());
        values.put("day", day.getDay());
        values.put("allThing",day.getAll());
        values.put("done",day.getDone());
        values.put("theRest",day.getTheRest());
        values.put("losed",day.getLosed());
        values.put("status",day.getStatus());
     return  (int)mSQLiteDatabase.insert("TABLE_DAY", null, values);
    }




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


    /**
     * 根据提供的day（有day，month，year）判断数据里是否存在该day
     * @param day
     * @return  若存在，返回day的id key ,不存在返回-1
     */
    public static int haveDay(Day day) {
        Cursor cursor = mSQLiteDatabase.query("TABLE_DAY", null, "year=? AND  month=? AND  day=?", new String[]{day.getYear() + "", day.getMonth()+ "", day.getDay() + ""}, null, null, null);
        if(cursor.moveToFirst()){
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            cursor.close();
            return id;
            }
        cursor.close();
        return -1;
    }

    /**
     * 根据提供的info(该info有dayID)判断数据里是否存在该info
     * @param information
     *@return ture 有；false 无
     */

    public  static  boolean haveInfo(Information information) {
        Cursor cursor = mSQLiteDatabase.query("TABLE_INFO", null, "dayID=? AND hour=? AND minute=?",new String[]{information.getDayID()+"",information.getHour()+"",information.getMinute()+""}, null, null, null, null);
        if (cursor.moveToFirst()) {
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

//    /**
//     * 获得状态为0，1,6  information
//     * @return
//     */
//    public static ArrayList<Information> getTargetInfo016() {
//        Cursor cursor=mSQLiteDatabase.query("TABLE_INFO",null,"completed=? OR ? OR ?",new String[]{0+"",1+"",6+""},null,null,null);
//        ArrayList<Information> informationsList = new ArrayList<>();
//        if(cursor.moveToFirst()){
//            do {
//                Information information = new Information();
//                //遍历cusor对象，取出数据变放存
//                information.setId(cursor.getInt(cursor.getColumnIndex("id")));
//                information.setYear(cursor.getInt(cursor.getColumnIndex("year")));
//                information.setMonth(cursor.getInt(cursor.getColumnIndex("month")));
//                information.setDay(cursor.getInt(cursor.getColumnIndex("day")));
//                information.setHour(cursor.getInt(cursor.getColumnIndex("hour")));
//                information.setMinute(cursor.getInt(cursor.getColumnIndex("minute")));
//                information.setLastTime(cursor.getInt(cursor.getColumnIndex("lastTime")));
//                information.setCompleted(cursor.getInt(cursor.getColumnIndex("completed")));
//                information.setInformation(cursor.getString(cursor.getColumnIndex("information")));
//                information.setDayID(cursor.getInt(cursor.getColumnIndex("dayID")));
//                informationsList.add(information);
//            } while (cursor.moveToNext());
//            cursor.close();
//            return informationsList;
//        }
//        cursor.close();
//        return  informationsList;
//    }



    /**
     * 获状态为PLAN(0)  information
     * @return
     */
    public static LinkedList<Information> getTargetInfoPlan() {
        Cursor cursor = mSQLiteDatabase.query("TABLE_INFO", null, "completed=?", new String[]{PLAN + ""}, null, null, null);
        LinkedList<Information> informationsList = new LinkedList<>();
        if (cursor.moveToFirst()) {
            do {
                Information information = new Information();
                //遍历cusor对象，取出数据变放存
                information.setId(cursor.getInt(cursor.getColumnIndex("id")));
                information.setDayID(cursor.getInt(cursor.getColumnIndex("dayID")));
                information.setYear(cursor.getInt(cursor.getColumnIndex("year")));
                information.setMonth(cursor.getInt(cursor.getColumnIndex("month")));
                information.setDay(cursor.getInt(cursor.getColumnIndex("day")));
                information.setHour(cursor.getInt(cursor.getColumnIndex("hour")));
                information.setMinute(cursor.getInt(cursor.getColumnIndex("minute")));
                information.setLastTime(cursor.getInt(cursor.getColumnIndex("lastTime")));
                information.setCompleted(cursor.getInt(cursor.getColumnIndex("completed")));
                information.setInformation(cursor.getString(cursor.getColumnIndex("information")));
                informationsList.add(information);
            } while (cursor.moveToNext());
            cursor.close();
            return informationsList;
        }
        cursor.close();
        return null;
    }

    /**
     * 获得状态为WORKING(0)  information
     * @return
     */
    public static LinkedList<Information> getTargetInfoWorking() {
        Cursor cursor = mSQLiteDatabase.query("TABLE_INFO", null, "completed=?", new String[]{WORKING + ""}, null, null, null);
        LinkedList<Information> informationsList = new LinkedList<>();
        if (cursor.moveToFirst()) {
            do {
                Information information = new Information();
                //遍历cusor对象，取出数据变放存
                information.setId(cursor.getInt(cursor.getColumnIndex("id")));
                information.setDayID(cursor.getInt(cursor.getColumnIndex("dayID")));
                information.setYear(cursor.getInt(cursor.getColumnIndex("year")));
                information.setMonth(cursor.getInt(cursor.getColumnIndex("month")));
                information.setDay(cursor.getInt(cursor.getColumnIndex("day")));
                information.setHour(cursor.getInt(cursor.getColumnIndex("hour")));
                information.setMinute(cursor.getInt(cursor.getColumnIndex("minute")));
                information.setLastTime(cursor.getInt(cursor.getColumnIndex("lastTime")));
                information.setCompleted(cursor.getInt(cursor.getColumnIndex("completed")));
                information.setInformation(cursor.getString(cursor.getColumnIndex("information")));
                informationsList.add(information);
            } while (cursor.moveToNext());
            cursor.close();
            return informationsList;
        }
        cursor.close();
        return null;
    }


    /**
     * 获得状态为WORNING(0)  information
     * @return
     */
    public static LinkedList<Information> getTargetInfoWaring() {
        Cursor cursor = mSQLiteDatabase.query("TABLE_INFO", null, "completed=?", new String[]{WARING + ""}, null, null, null);
        LinkedList<Information> informationsList = new LinkedList<>();
        if (cursor.moveToFirst()) {
            do {
                Information information = new Information();
                //遍历cusor对象，取出数据变放存
                information.setId(cursor.getInt(cursor.getColumnIndex("id")));
                information.setDayID(cursor.getInt(cursor.getColumnIndex("dayID")));
                information.setYear(cursor.getInt(cursor.getColumnIndex("year")));
                information.setMonth(cursor.getInt(cursor.getColumnIndex("month")));
                information.setDay(cursor.getInt(cursor.getColumnIndex("day")));
                information.setHour(cursor.getInt(cursor.getColumnIndex("hour")));
                information.setMinute(cursor.getInt(cursor.getColumnIndex("minute")));
                information.setLastTime(cursor.getInt(cursor.getColumnIndex("lastTime")));
                information.setCompleted(cursor.getInt(cursor.getColumnIndex("completed")));
                information.setInformation(cursor.getString(cursor.getColumnIndex("information")));
                informationsList.add(information);
            } while (cursor.moveToNext());
            cursor.close();
            return informationsList;
        }
        cursor.close();
        return null;
    }




        /**
         * 获得状态为0、1的信息
         * @return
         */
    public static ArrayList<Information> getTargetInfoPlanOrWoring() {
        Cursor cursor=mSQLiteDatabase.query("TABLE_INFO",null,"completed=? OR ? ",new String[]{PLAN+"",WORKING+""},null,null,null);
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
                information.setLastTime(cursor.getInt(cursor.getColumnIndex("lastTime")));
                information.setCompleted(cursor.getInt(cursor.getColumnIndex("completed")));
                information.setDayID(cursor.getInt(cursor.getColumnIndex("dayID")));
                informationsList.add(information);
            } while (cursor.moveToNext());
            cursor.close();
            return informationsList;
        }
        cursor.close();
        return  null;
    }
    /**
     * 开机初始化day
     * 检查状态为NOW(1)，FUTURE(2)的day信息是否过期，过期则改状态
     *
     */

    public  static int initialDate() {
        int year = TimeUtil.getYear();
        int month = TimeUtil.getMonth();
        int day = TimeUtil.getDay();
        int dayID=-7;                       //随便写的一个负数，因为最后dayID都会是一个正整数
        int newDAY=0;
        Cursor cursor = mSQLiteDatabase.query("TABLE_DAY", null, "status=? OR ?", new String[]{ NOW+"",FUTURE+""}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                ContentValues values = new ContentValues();
                int year1 = cursor.getInt(cursor.getColumnIndex("year"));
                int month1 = cursor.getInt(cursor.getColumnIndex("month"));
                int day1 = cursor.getInt(cursor.getColumnIndex("day"));
                int id = cursor.getInt(cursor.getColumnIndex("id"));

                int i = 500 * (year - year1) + 35 * (month - month1) + (day - day1);

                if ( i> 0) {          //在今天之前的日子全部更新为PAST
                    values.put("status",PAST);
                    mSQLiteDatabase.update("TABLE_DAY", values, "id=?", new String[]{id + ""});
                } else if (i == 0) {    //今天
                    values.put("status", NOW);
                    mSQLiteDatabase.update("TABLE_DAY", values, "id=?", new String[]{id + ""});
                    newDAY = newDAY + 1;
                    dayID = id;
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
                values.put("status", NOW);
              dayID=(int)MyDatabaseHelper.mSQLiteDatabase.insert("TABLE_DAY", null, values);
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
            values.put("status", NOW);
           dayID= (int)MyDatabaseHelper.mSQLiteDatabase.insert("TABLE_DAY", null, values);
        }

        return dayID;
    }
    /**
     * 开机初始化info
     */
   public static void initialInfo() {
        ArrayList<Information> informationsList = sMyDatabaseHelper.getTargetInfoPlanOrWoring();     //查找数据库中状态为PLAN（0），WORKING(1)
        if (informationsList!=null) {
            for(Information information:informationsList) {
                int id = information.getId();
                int year = information.getYear();
                int month = information.getMonth();
                int day = information.getDay();
                int hour = information.getHour();
                int minute = information.getMinute();
                int lastTime = information.getLastTime();
                int completed = information.getCompleted();
                long begin = TimeUtil.getMillis(year, month, day, hour, minute);
                switch (completed) {
                    case PLAN://状态是未来的，检查是否有漏过的计划
                        if (begin + lastTime * 60000 - TimeUtil.getNowMillis() >= 0) {                           //如果结束时间在当前时间之后
                            if (begin - TimeUtil.getNowMillis() <= 0) {                              //开始时间在当前时间之前
                                sMyDatabaseHelper.updateInformationById(id, 1);
                            }
                        } else {                                                                       //结束时间在当前时间之前
                            sMyDatabaseHelper.updateInformationById(id, 6);
                        }
                        break;
                    case WORKING:                      //已经开始的计划

                        if (begin + lastTime * 60000 - TimeUtil.getNowMillis() < 0) {                            //已经开始并且结束的任务
                            sMyDatabaseHelper.updateInformationById(id, 6);
                        }
                        break;
                    default:
                        break;
                }

            }
        }
    }

    /**
     * 获取当月信息的数量
     * @param month
     * @return
     */
    public static int getInfoNumMaxInMonth(int year,int month) {
        Cursor cursor = mSQLiteDatabase.query("TABLE_INFO", null, "year=? AND month=?", new String[]{year+"",month + ""}, null, null, null);
        return cursor.getCount();
    }

    /**
     * 获取当月已经结束信息的数量
     *
     * @return
     */
    public static int getInfoNumDoneInMonth(int year, int month) {
        Cursor cursor = mSQLiteDatabase.query("TABLE_INFO", null, "year=? AND month=?  AND completed IN (2,3)", new String[]{year + "", month + ""}, null, null, null);
        return cursor.getCount();

    }

    /**
     * 获取当月已经结束信息的数量
     * @param year
     * @param month
     * @return
     */


    public static int getInfoNumLosedInMonth(int year, int month) {
        Cursor cursor = mSQLiteDatabase.query("TABLE_INFO", null, "year=? AND month=? AND completed=?", new String[]{year + "", month + "", UNFINISHED + ""}, null, null, null);
        return cursor.getCount();

    }





    /**
     * 获取当月信息的数量
     * @param
     * @return
     */
    public static int getInfoNumMaxInYear(int year) {
        Cursor cursor = mSQLiteDatabase.query("TABLE_INFO", null, "year=? ", new String[]{year+""}, null, null, null);
        return cursor.getCount();
    }

    /**
     * 获取今年已经结束信息的数量
     *
     * @return
     */
    public static int getInfoNumDoneInYear(int year) {
        Cursor cursor = mSQLiteDatabase.query("TABLE_INFO", null, "year=? AND  completed in (2,3)", new String[]{year + ""}, null, null, null);
        return cursor.getCount();

    }

    /**
     * 获取今年失败信息的数量
     * @param year
     * @param
     * @return
     */


    public static int getInfoNumLosedInYear(int year) {
        Cursor cursor = mSQLiteDatabase.query("TABLE_INFO", null, "year=? AND completed=?", new String[]{year + "", UNFINISHED + ""}, null, null, null);
        return cursor.getCount();

    }


    /**
     * 获取从安装开始 当天全部完成 天数和
     * @return
     */

    public static int getDayAllFinishNumFromBegin() {

        Cursor cursor = mSQLiteDatabase.query("TABLE_DAY", null, " allThing!=? AND losed=? AND  status=?", new String[]{0+"",0+"" , 0+ ""}, null, null, null);
        return cursor.getCount();
    }


    /**
     * 获取数据库中到目前为止所有的有安排的天数
     */

    public static int getDayMarkNumFromBegin() {
        Cursor cursor = mSQLiteDatabase.query("TABLE_DAY", null, " allThing!=? AND  status=?", new String[]{0+"",PAST+"" }, null, null, null);
        return cursor.getCount();
    }


    /**
     * 查询所有完成的info
     * @return
     */


    public static int getInfoFininshNumFromBegin() {
        Cursor cursor = mSQLiteDatabase.query("TABLE_INFO", null, "completed = ?", new String[]{FINISHED+""}, null, null, null);
        return cursor.getCount();
    }

    /**
     * 查询所有没有完成的info
     * @return
     */

    public static int getInfoLosedNumFromBegin() {
        Cursor cursor = mSQLiteDatabase.query("TABLE_INFO", null, "completed = ?", new String[]{UNFINISHED+""}, null, null, null);
        return cursor.getCount();
    }
}
