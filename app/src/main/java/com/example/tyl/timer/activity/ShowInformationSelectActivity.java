package com.example.tyl.timer.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.example.tyl.timer.R;
import com.example.tyl.timer.service.MyService;
import com.example.tyl.timer.util.Information;
import com.example.tyl.timer.util.MyDatabaseHelper;
import com.example.tyl.timer.util.informaitonSelectAdapter;

import java.util.List;


/**
 * 展示需要选择结果的事务的界面
 */

public class ShowInformationSelectActivity extends AppCompatActivity {
    Toolbar mToolbar;
    RecyclerView mRecyclerView;
    informaitonSelectAdapter mAdapter;
    public informaitonSelectAdapter getAdapter() {
        return mAdapter;
    }

    MyService.AlarmBinder mAlarmBinder;
        private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAlarmBinder = (MyService.AlarmBinder) service;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private ServiceConnection mServiceConnection1;



    public MyService.AlarmBinder getAlarmBinder() {
        return mAlarmBinder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Log.d("ShowInformationSelect", "onCreate方法执行");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_information_select);
        Intent intent1 = new Intent(this, MyService.class);
        bindService(intent1,mServiceConnection,BIND_AUTO_CREATE);
        mToolbar = (Toolbar) findViewById(R.id.infoSelect);
        mRecyclerView = (RecyclerView) findViewById(R.id.infoSelectRecyclerView);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("需要确定结果的事务");

//        int Id = getIntent().getIntExtra("infoID", -1);
//        Log.d("ShowInfoHint", Id + "");
        Information information = getIntent().getParcelableExtra("information");
//        if (Id != -1) {                                                          //定时任务触发
            if (information != null) {

            Log.d("ShowInformationSelect", "定时任务执行");
//            Information information = MyService.sInformationMap.get(Id);
            MyService.sSelectActivitiesList.add(information);
            MyService.sInformationHintList.remove(information);
//            MyService.sInformationMap.remove(Id);
                int Id = information.getId();

            MyDatabaseHelper.sMyDatabaseHelper.updateInformationById(Id, 6);              //更改info数据库状态 1 为  6

           mServiceConnection1 = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                MyService.AlarmBinder   mAlarmBinder = (MyService.AlarmBinder) service;
                    mAlarmBinder.infoHintOrSelectChangeForeground();
                }
                @Override
                public void onServiceDisconnected(ComponentName name) {
                }
            };
            Intent intent2 = new Intent(this, MyService.class);
            bindService(intent2,mServiceConnection1,BIND_AUTO_CREATE);

//            Log.d("ShowInformationSelect", "" + MyService.sSelectActivitiesList.size());
//            Log.d("ShowInformationSelect", "changeForegroud执行");

            //更改daylist 的显示信息  Done+1
            List<Information> informationList = ShowInformationActivity.getEditorlist();
            if (informationList != null) {
                for (Information information1 : informationList) {
                    if (information1.getId() == Id) {
                        information1.setCompleted(6);
                    }
                }
                ShowInformationActivity.getmAdapter().notifyDataSetChanged();
            }
        }
//        Log.d("ShowInformationSelect", "已经跳过");

        mAdapter = new informaitonSelectAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);}

    @Override
    protected void onPause() {
//        Log.d("ShowInformationSelect", "SelectOnpause成功！");
        super.onPause();
        unbindService(mServiceConnection);
        if (mServiceConnection1 != null) {
            unbindService(mServiceConnection1);
        }
        finish();
    }
    @Override
    public void onBackPressed() {
        Toast.makeText(this, "请对任务情况作出选择", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
