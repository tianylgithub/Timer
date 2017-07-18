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
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.tyl.timer.R;
import com.example.tyl.timer.fragment.EditorFragment;
import com.example.tyl.timer.service.MyService;
import com.example.tyl.timer.util.Day;
import com.example.tyl.timer.util.DayCompare;
import com.example.tyl.timer.util.MyDatabaseHelper;

import java.util.Collections;
import java.util.List;

import static com.example.tyl.timer.util.MyDatabaseHelper.sMyDatabaseHelper;


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
   static int state=0;
    public FloatingActionButton getFloatingActionButton() {
        return mFloatingActionButton;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       sMyDatabaseHelper.initialDate();
       MyDatabaseHelper.sMyDatabaseHelper.initialInfo();
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, MyService.class);
        startService(intent);

        state = state+1;

  mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("时间表");
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.day_floatbutton);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navi_view);
        navigationView.setItemIconTintList(null);
        navigationView.setCheckedItem(R.id.nav_today);      // 快速进入今天

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_today:

                        Day day = getToday();
                        if (day != null) {
                            Intent intent1 = new Intent(MainActivity.this, ShowInformationActivity.class);
                            intent1.putExtra("year", day.getYear());
                            intent1.putExtra("month", day.getMonth());
                            intent1.putExtra("day", day.getDay());
                            intent1.putExtra("dayID",day.getId() );
                            intent1.putExtra("state", 1);
                            MainActivity.this.startActivity(intent1);
                        }
                        break;
                    case R.id.nav_search:
                        Intent intent2 = new Intent(MainActivity.this, SearchActivity.class);
                        MainActivity.this.startActivity(intent2);
                        break;
                    case R.id.nav_showlife:
                        Intent intent3 = new Intent(MainActivity.this, ShowStatisticsActivity.class);
                        MainActivity.this.startActivity(intent3);

                        break;
                    case R.id.nav_tips:
                        Intent intent4 = new Intent(MainActivity.this, AppExplainActivity.class);
                        MainActivity.this.startActivity(intent4);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MainActivity", "onResume执行");
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("stateID", -99);
        startService(intent);
        if(state!=0&&MyService.sSelectActivitiesList.size() != 0) {
            Intent intent1 = new Intent(this, ShowInformationSelectActivity.class);
            startActivity(intent1);
        }
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
                int year = data.getIntExtra("year", -1);
                int month = data.getIntExtra("month", -1);
                int day = data.getIntExtra("day", -1);
                int position = data.getIntExtra("position", -1);
                Day date = EditorFragment.getmDays().get(position);

                date.setYear(year);
                date.setMonth(month);
                date.setDay(day);
                    date.setStatus(2);
                    int dayID = MyDatabaseHelper.addDay(date);    //将新建的一天插入数据库并返回该天的ID
                    date.setId(dayID);
                    Collections.sort(EditorFragment.getmDays(), new DayCompare());
                    EditorFragment.getDaysAdapter().notifyDataSetChanged();
                    EditorFragment.getRecyclerView().smoothScrollToPosition(position);}
                Toast.makeText(this, "新的日期创建成功,请点击添加任务", Toast.LENGTH_SHORT).show();


                break;
            default:
                break;
        }
    }

    Day getToday() {
        List<Day> dayList = EditorFragment.getmDays();
        if (dayList != null) {
            for (Day day : dayList) {
                if (day.getStatus() == 1) {
                    return day;
                }
            }

        }
        return null;
    }
}
