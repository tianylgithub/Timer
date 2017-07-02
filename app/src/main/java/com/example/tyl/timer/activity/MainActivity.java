package com.example.tyl.timer.activity;
//fragment版本库的问题,是supportv4，还是默认


import android.content.ContentValues;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.MenuItem;

import com.example.tyl.timer.fragment.EditorFragment;
import com.example.tyl.timer.R;
import com.example.tyl.timer.service.MyService;
import com.example.tyl.timer.util.Day;

import com.example.tyl.timer.util.MyDatabaseHelper;
import com.example.tyl.timer.util.TimeUtil;



/**
 *  1.开机发送广播到接收器MyReceiver,MyReceiver 启动服务MyService,服务中没隔1隔小时检查日期是否有变化
 *
 *  2.加载滑动界面（Toolbar,Fragment,floatingActionButton,navigationView）,其中Fragment加载recyclerview
 *
 */

public class MainActivity extends AppCompatActivity {
    FloatingActionButton mFloatingActionButton;
    DrawerLayout mDrawerLayout;
    Toolbar mToolbar;

    public FloatingActionButton getFloatingActionButton() {
        return mFloatingActionButton;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (!MyDatabaseHelper.haveDay(TimeUtil.getToday())) {

            ContentValues values = new ContentValues();

            values.put("year", TimeUtil.getYear());

            values.put("month", TimeUtil.getMonth());

            values.put("day", TimeUtil.getDay());

            values.put("allThing", 0);

            values.put("done", 0);

            values.put("losed", 0);

            values.put("theRest", 0);

            values.put("status", 1);

            MyDatabaseHelper.mSQLiteDatabase.insert("TABLE_DAY", null, values);  //  不存在今天的day数据库，新的日期变化插入新的一天并且status=1

        }
        Intent intent = new Intent(this, MyService.class);
        startService(intent);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
  mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_launcher);
        }
//        replaceFrament(new EditorFragment());
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.day_floatbutton);

    }

    //    替换碎片的方法
    private void replaceFrament(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_fragment,fragment );
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return true;
    }

    /**
     *
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 666:
                if(resultCode==RESULT_OK){
                int year = data.getIntExtra("year", 0);
                int month = data.getIntExtra("month", 0);
                int day = data.getIntExtra("day", 0);
                int position = data.getIntExtra("position", 0);
                Day date = EditorFragment.getmDays().get(position);
                date.setYear(year);
                date.setMonth(month);
                date.setDay(day);
                    date.setStatus(2);
                    MyDatabaseHelper.addDay(date);
                EditorFragment.getDaysAdapter().notifyDataSetChanged();}
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
