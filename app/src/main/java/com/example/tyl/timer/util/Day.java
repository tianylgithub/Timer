package com.example.tyl.timer.util;

/**
 * Created by TYL on 2017/6/13.
 */

public class Day {
    private int mAll;
    private int mDone;
    private int mLosed;
    private int mTheRest;
    private int year;
  private   int month;
    private    int day;
    private  int status= -1;              // PAST=0; NOW=1,FUTURE=2



    public int getAll() {
        return mAll;
    }

    public void setAll(int all) {
        mAll = all;
    }

    public int getDone() {
        return mDone;
    }

    public void setDone(int done) {
        mDone = done;
    }

    public int getLosed() {
        return mLosed;
    }

    public void setLosed(int losed) {
        mLosed = losed;
    }

    public int getTheRest() {
        return mTheRest;
    }

    public void setTheRest(int theRest) {
        mTheRest = theRest;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void plusAll() {
        mAll = mAll + 1;
    }

    public void minusAll() {
        mAll = mAll - 1;
    }

    public void plusTheRest() {
        mTheRest = mTheRest + 1;
    }

    public void minusTheRest() {
        mTheRest = mTheRest - 1;
    }


    public void plusLosed() {
        mLosed = mLosed + 1;
    }

    public void plusDone() {
        mDone = mDone + 1;
    }





}
