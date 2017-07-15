package com.example.tyl.timer.activity;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
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
import android.view.View;
import android.widget.Toast;

import com.example.tyl.timer.R;
import com.example.tyl.timer.service.MyService;
import com.example.tyl.timer.util.DefaultItemTouchHelpCallback;
import com.example.tyl.timer.util.DefaultItemTouchHelper;
import com.example.tyl.timer.util.Information;
import com.example.tyl.timer.util.InformationAdapter;
import com.example.tyl.timer.util.InformationCompare;
import com.example.tyl.timer.util.MyDatabaseHelper;

import java.util.Collections;
import java.util.LinkedList;

/**
 * Created by TYL on 2017/6/12.
 */
//该界面里的所有操作对数据库的直接更改在返回时，为查看当天信息的界面
public class        ShowInformationActivity extends AppCompatActivity{
    int dayID;
    int year;
    int month;
    int day;
    int stateFromDay;
    FloatingActionButton mFloatingActionButton;
    Toolbar mToolbar;
    RecyclerView mRecyclerView;

    static final int PAST = 0;

    public int getStateFromDay() {
        return stateFromDay;
    }

    static InformationAdapter mAdapter;

     static  LinkedList<Information> editorlist ;


    public static InformationAdapter getmAdapter() {
        return mAdapter;
    }

    public static LinkedList<Information> getEditorlist() {
        return editorlist;
    }


    /**
     *  滑动删除监听
     */

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
                        Toast.makeText(ShowInformationActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
//                        mAdapter.notifyDataSetChanged();
                        break;
                    default:
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
        setContentView(R.layout.activity_info);
        Log.d("informain", "onCreate方法执行");
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.editor_floatbutton);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.text_recycler);
        Intent intent = getIntent();
        year = intent.getIntExtra("year", -1);
        month = intent.getIntExtra("month", -1);
        day = intent.getIntExtra("day", -1);
        stateFromDay = intent.getIntExtra("state", -1);
        dayID = intent.getIntExtra("dayID", -1);


        //加载界面，从数据库里获取每天的消息，形式为一个List<information>
        Intent bindIntent = new Intent(this, MyService.class);         //绑定服务，进行启动操作
        bindService(bindIntent, mServiceConnection, BIND_AUTO_CREATE);  //这里有可能出现问题，不是adapter而是活动绑定了服务

        editorlist = MyDatabaseHelper.sMyDatabaseHelper.getListByDayID(dayID);

        Collections.sort(editorlist, new InformationCompare());   //          给information排序
        mAdapter = new InformationAdapter(editorlist,this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        DefaultItemTouchHelper itemTouchHelper = new DefaultItemTouchHelper(onItemTouchCallbackListener);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        if (stateFromDay == PAST) {
            mFloatingActionButton.setVisibility(View.INVISIBLE);
        }

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        Information addInformation = new Information();
                        addInformation.setYear(year);
                        addInformation.setMonth(month);
                        addInformation.setDay(day);
                        addInformation.setDayID(dayID);
                        editorlist.add(0, addInformation);
                        mAdapter.notifyItemInserted(0);
//                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.smoothScrollToPosition(0);                        //mark
                        Toast.makeText(ShowInformationActivity.this,"创建成功,请点击编辑",Toast.LENGTH_SHORT).show();
            }
        });
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(year+"-"+month+"-"+day);
        if(actionBar!=null){
            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        }
    }



//接受ItemEditorActivity返回的数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case 123:
                if (resultCode == RESULT_OK) {
                    int position = data.getIntExtra("position",-1);
                    String hour=data.getStringExtra("hour");
                    String minute = data.getStringExtra("minute");
                    String lastTime = data.getStringExtra("lastTime");

                    Information information = editorlist.get(position);
                    information.setHour(Integer.valueOf(hour.equals("") ? "0" : hour));
                    information.setMinute(Integer.valueOf(minute.equals("") ? "0" : minute));
                    information.setLastTime(Integer.valueOf(lastTime.equals("") ? "0" : lastTime));
                    information.setInformation(data.getStringExtra("information"));
                    Collections.sort(editorlist, new InformationCompare());   //          给information排序
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "可尝试点击右端图标创建事务", Toast.LENGTH_SHORT).show();
                    break;
                }
        }
    }
    @Override
    public void onBackPressed() {
        switch (stateFromDay) {
            case 1:
            case 2:
                if( editorlist.size()==0){finish();}
             else if(editorlist.get(0).getCompleted()==-1){
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
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            dialog.show();} else {finish();}
                break;
            default:
                finish();
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }
}
