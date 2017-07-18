package com.example.tyl.timer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.tyl.timer.R;
import com.example.tyl.timer.util.MyDatabaseHelper;
import com.example.tyl.timer.util.SearchAdapterDay;
import com.example.tyl.timer.util.SearchAdapterInformation;

import java.util.ArrayList;


/**
 * 展示search结果的界面
 */


public class ShowSearchActivity extends AppCompatActivity {
    static final int DAY = 1;
    static final int INFORMATION = 2;
    Toolbar mToolbar;
    RecyclerView mRecyclerView;

    ArrayList mArrayList;
    static int state;
    public ArrayList getArrayList() {
        return mArrayList;
    }
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_search);
        mToolbar = (Toolbar) findViewById(R.id.show_serarch_toolbar);

        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();

        mRecyclerView = (RecyclerView) findViewById(R.id.show_search_recycler);
        Intent intent = getIntent();
        String year = intent.getStringExtra("year");
        String month = intent.getStringExtra("month");
        String day = intent.getStringExtra("day");
        String information = intent.getStringExtra("information");
        if (information == null) {                                                       //information为空，此时返回day信息
            state = DAY;
        }    else {
            state = INFORMATION;}
        mArrayList = MyDatabaseHelper.searchData(year, month, day, information);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        switch (state) {
            case DAY:
                actionBar.setTitle("日期("+ (year==null?"X":year)+"年"+(month==null?"X":month)+"月"+(day==null?"X":day)+"日)");
                SearchAdapterDay dayAdapter = new SearchAdapterDay(this);
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setAdapter(dayAdapter);
                break;
            case INFORMATION:
                actionBar.setTitle("信息("+ (year==null?"X":year)+"年"+(month==null?"X":month)+"月"+(day==null?"X":day)+"日,事务:"+information+")");
                SearchAdapterInformation informationAdapter = new SearchAdapterInformation(this);
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setAdapter(informationAdapter);
                break;
        }
    }
}
