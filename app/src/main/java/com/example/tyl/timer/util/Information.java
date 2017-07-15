package com.example.tyl.timer.util;

/**
 * Created by TYL on 2017/6/12.
 */

//public class Information implements Parcelable {
public class Information  {

    private int id = -1;
    private int dayID;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int lastTime;
    private int completed = -1;
    private String information = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDayID() {
        return dayID;
    }

    public void setDayID(int dayID) {
        this.dayID = dayID;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getLastTime() {
        return lastTime;
    }

    public void setLastTime(int lastTime) {
        this.lastTime = lastTime;
    }

    public int getCompleted() {
        return completed;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeInt(id);
//        dest.writeInt(dayID);
//        dest.writeInt(year);
//        dest.writeInt(month);
//        dest.writeInt(day);
//        dest.writeInt(hour);
//        dest.writeInt(minute);
//        dest.writeInt(lastTime);
//        dest.writeInt(completed);
//        dest.writeString(information);
//    }
//    public static final Parcelable.Creator<Information> CREATOR = new Parcelable.Creator<Information>() {
//        @Override
//        public Information createFromParcel(Parcel source) {
//            Information information1 = new Information();
//            information1.id = source.readInt();
//            information1.dayID = source.readInt();
//            information1.year = source.readInt();
//            information1.month = source.readInt();
//            information1.day = source.readInt();
//            information1.hour = source.readInt();
//            information1.minute = source.readInt();
//            information1.lastTime = source.readInt();
//            information1.completed = source.readInt();
//            information1.information = source.readString();
//            return information1;
//        }
//        @Override
//        public Information[] newArray(int size) {
//            return new Information[size];
//        }
//    };
}
