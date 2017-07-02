package com.example.tyl.timer.activity;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.icu.text.LocaleDisplayNames;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.tyl.timer.R;
import com.example.tyl.timer.service.MyService;
import com.example.tyl.timer.util.DefaultItemTouchHelpCallback;
import com.example.tyl.timer.util.DefaultItemTouchHelper;
import com.example.tyl.timer.util.TimeUtil;
import com.example.tyl.timer.util.Information;
import com.example.tyl.timer.util.InformationAdapter;
import com.example.tyl.timer.util.InformationCompare;
import com.example.tyl.timer.util.MyDatabaseHelper;

import java.util.Collections;
import java.util.LinkedList;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * Created by TYL on 2017/6/12.
 */
//该界面里的所有操作对数据库的直接更改在返回时，为查看当天信息的界面
public class ShowInformationActivity extends AppCompatActivity{

    int year;
    int month;
    int day;
    int position;
    int stateFromDay;
    FloatingActionButton mFloatingActionButton;
    Toolbar mToolbar;
    RecyclerView mRecyclerView;


    public int getPosition() {
        return position;
    }

    public int getStateFromDay() {
        return stateFromDay;
    }



  public   static InformationAdapter mAdapter;

    private static  LinkedList<Information> editorlist ;

    MyDatabaseHelper mMyDatabaseHelper =MyDatabaseHelper.sMyDatabaseHelper;

    DefaultItemTouchHelpCallback.OnItemTouchCallbackListener onItemTouchCallbackListener = new DefaultItemTouchHelpCallback.OnItemTouchCallbackListener() {
        @Override
        public void onSwiped(int adapterPosition) {
            if (editorlist != null) {
                switch (editorlist.get(adapterPosition).getCompleted()) {
                    case 0:
                        Toast.makeText(ShowInformationActivity.this, "计划在轨，当先移取", Toast.LENGTH_SHORT).show();
                        mAdapter.notifyDataSetChanged();
                        break;

                    case 1:
                        Toast.makeText(ShowInformationActivity.this, "历史已进入，势无可挡", Toast.LENGTH_SHORT).show();
                        finish();
                        mAdapter.notifyDataSetChanged();
                        break;
                    case 2:
                    case 3:
                        Toast.makeText(ShowInformationActivity.this, "凡已发生，皆不可移", Toast.LENGTH_SHORT).show();
                        mAdapter.notifyDataSetChanged();
                        break;
                    case -1:
                        editorlist.remove(adapterPosition);
                       mAdapter.notifyItemRemoved(adapterPosition);
//                        mAdapter.notifyDataSetChanged();
                        break;
                }
            }
        }
    };

    private  MyService.AlarmBinder mAlarmBinder;


    public MyService.AlarmBinder getmAlarmBinder() {
        return mAlarmBinder;
    }



    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAlarmBinder = (MyService.AlarmBinder) service;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        Log.d("informain", "onCreate方法执行");
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.editor_floatbutton);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.text_recycler);
        Intent intent = getIntent();
        year = intent.getIntExtra("year", -1);
        month = intent.getIntExtra("month", -1);
        day = intent.getIntExtra("day", -1);
        position = intent.getIntExtra("position", -1);
        stateFromDay = intent.getIntExtra("state", -1);
        //根据state判断：如果是past0 则recyclerview的选项不能编辑（移动，删除）；如果是Future2，则可以编辑；如果是now1，则部分可以??????
        // 编辑（提交时与时间进行比较）。弹出的编辑界面是透明的可以看到上一个活动。???????????????

        //加载界面，从数据库里获取每天的消息，形式为一个List<information>
        Intent bindIntent = new Intent(this, MyService.class);         //绑定服务，进行启动操作
        bindService(bindIntent, mServiceConnection, BIND_AUTO_CREATE);  //这里有可能出现问题，不是adapter而是活动绑定了服务

        editorlist = mMyDatabaseHelper.getList(year, month, day);



        Collections.sort(editorlist, new InformationCompare());   //          给information排序
        mAdapter = new InformationAdapter(editorlist,this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        DefaultItemTouchHelper itemTouchHelper = new DefaultItemTouchHelper(onItemTouchCallbackListener);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);


//        if(editorlist==null){
//            mRecyclerView.setBackgroundResource(R.drawable.wsj2015_2);//此时数据库里面没有任何信息，提示你应该添加信息
//        }else {
//
//        }

        //点击悬浮按钮添加一个可编辑条目，此时仅对Linklist<Information>做改变   ？？？？？根据排序规则不知道能不能行？？
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Information addInformation = new Information();
                    addInformation.setYear(year);
                addInformation.setMonth(month);
                addInformation.setDay(day);
                addInformation.setCompleted(-1);
                editorlist.add(0,addInformation);
               mAdapter.notifyItemInserted(0);
//                mAdapter.notifyDataSetChanged();

            }
        });
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.zen_icons_key);
        }
    }


//滑动拖拽对recyclerview里的条目进行相应的删除、交换时间的操作，此时操作发生在List<Information>级别


//点击导航按钮返回到上一个活动。并弹出提示（确定保存？如果有条目未进行启动则则提醒是否启动，否则会丢失这些信息）此时提交list<Information>对information数据库
// 进行保存更改。返回时应重新加载dayItem信息
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                    //返回时提醒是否退出有可能丢未启动计划的信息
//                break;
//            default:
//
//        }
//        return true;
//    }


//接受ItemEditorActivity返回的数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case 123:
                if (resultCode == RESULT_OK) {
                    int position = data.getIntExtra("position",-1);
                    Information information = editorlist.get(position);
                    String hour=data.getStringExtra("hour");
                    String minute = data.getStringExtra("minute");
                    String lastTime = data.getStringExtra("lastTime");
                    information.setHour(Integer.valueOf(hour.equals("") ? "0" : hour));
                    information.setMinute(Integer.valueOf(minute.equals("") ? "0" : minute));
                    information.setLastTme(Integer.valueOf(lastTime.equals("") ? "0" : lastTime));
                    information.setInformation(data.getStringExtra("information"));
                    mAdapter.notifyDataSetChanged();
                }
        }
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("注意");
        dialog.setMessage("没有点确定返回编辑的信息将无法保存，继续？");
        dialog.setCancelable(false);
        dialog.setPositiveButton("是的", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
             ShowInformationActivity.this.finish();
            }
        });

        dialog.setNegativeButton("我考虑下", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        dialog.show();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        Log.d("解除服务", "解除服务233");
    }
}
