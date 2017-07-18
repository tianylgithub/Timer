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
import android.view.Menu;
import android.view.MenuItem;

import com.example.tyl.timer.R;
import com.example.tyl.timer.fragment.EditorFragment;
import com.example.tyl.timer.service.MyService;
import com.example.tyl.timer.util.Day;
import com.example.tyl.timer.util.InformationWorkingAdapter;
import com.example.tyl.timer.util.Information;
import com.example.tyl.timer.util.MyDatabaseHelper;

import java.util.List;


/**
 * 展示正在运行的事务界面
 */


public class ShowInformationWorkingActivity extends AppCompatActivity {

    Toolbar infoHintToolbar;
    RecyclerView mInfoRecyclerView;
    InformationWorkingAdapter mInfoHintAdapter;

    private ServiceConnection mServiceConnection;

    protected void onCreate(Bundle savedInstanceState) {
//        Log.d("ShowInformationHint", "onCreate方法执行");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_information_working);
        infoHintToolbar = (Toolbar) findViewById(R.id.infoHint);

        setSupportActionBar(infoHintToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("正在进行的事务");


        mInfoRecyclerView = (RecyclerView) findViewById(R.id.infoHintRecyclerView);

        int Id =  getIntent().getIntExtra("infoID", -1);
//        Log.d("ShowInfoHint", Id + "");
        if (Id!=-1) {                                                          //定时任务触发
//            Log.d("ShowInformationHint", "infohint执行定时任务");
            Information information = MyService.sInformationMap.get(Id);
            MyService.sInformationHintList.add(information);
            int dayID = information.getDayID();

            MyDatabaseHelper.sMyDatabaseHelper.updateInformationById(Id,1);                 //此时更新info数据库 0 为 1

            List<Day> dayList = EditorFragment.getmDays();                                  //更改ShowInfomation的显示信息
            if (dayList!= null) {
                for (Day day : dayList) {
                    if (day.getId() == dayID) {
                        day.minusTheRest();
                    }
                }                                                                            //更改daylist 的显示信息0为1  theRest-1
                EditorFragment.getDaysAdapter().notifyDataSetChanged();
            }
            List<Information> informationList = ShowInformationActivity.getEditorlist();
            if (informationList != null) {
                for (Information information1 : informationList) {
                    if (information1.getId() == Id) {
                        information1.setCompleted(1);
                    }
                }
                ShowInformationActivity.getmAdapter().notifyDataSetChanged();
            }
           mServiceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    MyService.AlarmBinder     mAlarmBinder = (MyService.AlarmBinder) service;
                    mAlarmBinder.infoHintOrSelectChangeForeground();
                }
                @Override
                public void onServiceDisconnected(ComponentName name) {
                }
            };
            Intent intent1 = new Intent(this, MyService.class);                             //只是为了更改Foreground
            bindService(intent1, mServiceConnection, BIND_AUTO_CREATE);
        }                                                                                  //此时不是第一次登陆
//        Log.d("ShowInformationHint", "已经跳过");
        mInfoHintAdapter = new InformationWorkingAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mInfoRecyclerView.setLayoutManager(layoutManager);
        mInfoRecyclerView.setAdapter(mInfoHintAdapter);
    }                                                                                         //打开app主界面


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.infohintforbacktoapp, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(ShowInformationWorkingActivity.this, MainActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    protected void onPause() {
//        Log.d("ShowInformationHint", "HintOnpause成功！");
        super.onPause();
        if (mServiceConnection != null) {
            unbindService(mServiceConnection);
//            Log.d("ShowinformationHint", "解除绑定");
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
