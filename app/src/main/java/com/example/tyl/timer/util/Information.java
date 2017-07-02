package com.example.tyl.timer.util;

import static android.R.attr.data;

/**
 * Created by TYL on 2017/6/12.
 */

public class Information {
    private int id;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int lastTme;
    private int completed;  //
    private String information="";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getLastTme() {
        return lastTme;
    }

    public void setLastTme(int lastTme) {
        this.lastTme = lastTme;
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
}
