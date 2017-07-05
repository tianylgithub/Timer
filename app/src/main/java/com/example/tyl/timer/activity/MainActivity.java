package com.example.tyl.timer.activity;
//fragment版本库的问题,是supportv4，还是默认


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.tyl.timer.R;
import com.example.tyl.timer.fragment.EditorFragment;
import com.example.tyl.timer.service.MyService;
import com.example.tyl.timer.util.Day;
import com.example.tyl.timer.util.MyDatabaseHelper;
import com.example.tyl.timer.util.TimeUtil;

import java.util.List;


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
        super.onCreate(savedInstanceState);
        MyDatabaseHelper.sMyDatabaseHelper.initial();

        setContentView(R.layout.activity_main);

        final Intent intent = new Intent(this, MyService.class);
        startService(intent);

  mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.day_floatbutton);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navi_view);
        navigationView.setItemIconTintList(null);
        navigationView.setCheckedItem(R.id.nav_today);      // 快速进入今天
        navigationView.setCheckedItem(R.id.nav_search);       //查找某一天并进入
        navigationView.setCheckedItem(R.id.nav_showlife);   //大尺度展示完成图谱
        navigationView.setCheckedItem(R.id.nav_listener);  //正在监听的情况
        navigationView.setCheckedItem(R.id.nav_tips);    //说明

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_today:
                        int year = TimeUtil.getYear();
                        int month = TimeUtil.getMonth();
                        int day = TimeUtil.getDay();
                        int position = getPosition(year, month, day);
                        if (position != -1) {
                            Intent intent1 = new Intent(MainActivity.this, ShowInformationActivity.class);
                            intent1.putExtra("year", year);
                            intent1.putExtra("month", month);
                            intent1.putExtra("day", day);
                            intent1.putExtra("month", month);
                            intent1.putExtra("state", 1);
                            intent1.putExtra("position", position);
                            MainActivity.this.startActivity(intent1);

                        }else {
                            Toast.makeText(MainActivity.this,"查找的日期还未规划",Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case R.id.nav_search:
                        Intent intent2 = new Intent(MainActivity.this, Search.class);
                        MainActivity.this.startActivity(intent2);
                        break;

                    case R.id.nav_showlife:
                        Toast.makeText(MainActivity.this,"图谱功能正在筹备中，敬请期待!",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_listener:
                        Toast.makeText(MainActivity.this,"监听功能正在筹备中，敬请期待!",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_tips:
                        Intent intent3 = new Intent(MainActivity.this, appExplain.class);
                        startActivity(intent3);
                        Toast.makeText(MainActivity.this,"欢迎使用本应用!",Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

    }

    //    替换碎片的方法
//    private void replaceFrament(Fragment fragment) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.main_fragment,fragment );
//    }

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
                int year = data.getIntExtra("year", -1);
                int month = data.getIntExtra("month", -1);
                int day = data.getIntExtra("day", -1);
                int position = data.getIntExtra("position", -1);
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

    int getPosition(int year, int month, int day) {
      List<Day>    dayList=EditorFragment.getmDays();
        if (dayList != null) {
            for(int position=0; position<dayList.size();position++){
                Day day2 = dayList.get(position);
                if ((day2.getDay() == day) && (day2.getMonth() ==month) && (day2.getYear() == year)) {
                    return position;
                }
            }
            return -1;
        }
        return -1;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
