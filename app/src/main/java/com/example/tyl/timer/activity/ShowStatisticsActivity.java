package com.example.tyl.timer.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tyl.timer.R;
import com.example.tyl.timer.util.MyDatabaseHelper;
import com.example.tyl.timer.util.TimeUtil;


/**
 * 展示统计信息界面
 */
public class ShowStatisticsActivity extends AppCompatActivity {


    Toolbar mToolbar;

    static final int AVERAGE = 27760;                   //按照人类平均寿命(76)岁记录，人一辈子有27760天
    int birthYear;
    int birthMonth;
    int birthDay;
    int beginYear;
    int beginMonth;
    int beginDay;

    TextView show_picture_text;
    TextView month_day_text;
    TextView month_work_text;
    TextView year_day_text;
    TextView year_work_text;
    TextView begin_day_text;
    TextView begin_day_text1;
    TextView begin_work_text;
    TextView  age_text;

    ProgressBar month_day_bar;
    ProgressBar month_work_bar;
    ProgressBar  year_day_bar;
    ProgressBar year_work_bar;
    ProgressBar begin_day_bar;
    ProgressBar begin_day_bar1;
    ProgressBar begin_work_bar;
    ProgressBar  age_bar;


    int yearNow ;                                               //year now
    int monthNow ;                                             //month now
    int dayInMonth ;                                                 // day in month now
    int numMaxInMonth ;               // num max in month now
    int dayInYear ;                                        // day int year now
    int numMaxInYear ;                             // num max in year now

    int infoNumMaxInMonth ;
    int infoNumDoneInMonth ;
    int infoNumLosedInMonth;

    int infoNumMaxInYear ;
    int infoNumDoneInYear;
    int infoNumLosedInYear;

    int dayNumFromBegin ;      //从安装应用起总共的天数
    int dayAllNumFinishFromBegin;                                      //从安装应用起 当天全部完成的天数
    int dayMarkNumFromBegin ;                                                //从安装应用起 当天有安排的记录数
    int dayUnMarkNum ;                                                           //无记 数

    int infoFinishNumFromBegin ;                                         //所有完成的信息  数
    int infoLosedNumFromBegin ;                                            //所有没有完成的信息 数
    int infoALL ;                                                       //所有结束的信息 数
    int dayNumFromBirth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ShowStatisticsActivity", "onCreate方法执行");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_picture);
        mToolbar = (Toolbar) findViewById(R.id.picture_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("数据统计");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
       birthYear= preferences.getInt("birthYear", -1);
       birthMonth= preferences.getInt("birthMonth", -1);
       birthDay= preferences.getInt("birthDay", -1);
        if (birthYear == -1) {                  //需要输入生日
//            Log.d("ShowStatisticsActivity", "启动birthday编写界面");
            Intent intent = new Intent(this, BirthdayActivity.class);
            startActivity(intent);
        }

        SharedPreferences prf = PreferenceManager.getDefaultSharedPreferences(this);
         beginYear = prf.getInt("beginYear", 0);
         beginMonth = prf.getInt("beginMonth", 0);
         beginDay = prf.getInt("beginDay", 0);
//        Log.d("ShowStatisticsActivity", "beginYear:" + beginYear + "beginMonth:" + beginMonth + "beginDay:" + beginDay);

         yearNow = TimeUtil.getYear();                                               //year now
         monthNow = TimeUtil.getMonth();                                             //month now
         dayInMonth = TimeUtil.getDay();                                                 // day in month now
         numMaxInMonth = TimeUtil.getMaxDayNumByYM(yearNow, monthNow);               // num max in month now
         dayInYear = TimeUtil.getDayInYear();                                        // day int year now
         numMaxInYear = TimeUtil.getDayNumByYear(yearNow);                             // num max in year now

         infoNumMaxInMonth = MyDatabaseHelper.getInfoNumMaxInMonth(yearNow,monthNow);
         infoNumDoneInMonth = MyDatabaseHelper.getInfoNumDoneInMonth(yearNow, monthNow);
         infoNumLosedInMonth = MyDatabaseHelper.getInfoNumLosedInMonth(yearNow, monthNow);

         infoNumMaxInYear = MyDatabaseHelper.getInfoNumMaxInYear(yearNow);
         infoNumDoneInYear = MyDatabaseHelper.getInfoNumDoneInYear(yearNow);
         infoNumLosedInYear = MyDatabaseHelper.getInfoNumLosedInYear(yearNow);

         dayNumFromBegin = TimeUtil.dayNumFromDate(beginYear, beginMonth, beginDay, yearNow, monthNow, dayInMonth);      //从安装应用起总共的天数
        dayAllNumFinishFromBegin = MyDatabaseHelper.getDayAllFinishNumFromBegin();                                      //从安装应用起 当天全部完成的天数
         dayMarkNumFromBegin = MyDatabaseHelper.getDayMarkNumFromBegin();                                                //从安装应用起 当天有安排的记录数
         dayUnMarkNum = dayNumFromBegin - dayMarkNumFromBegin;                                                           //无记 数

         infoFinishNumFromBegin = MyDatabaseHelper.getInfoFininshNumFromBegin();                                         //所有完成的信息  数
         infoLosedNumFromBegin = MyDatabaseHelper.getInfoLosedNumFromBegin();                                            //所有没有完成的信息 数
         infoALL = infoFinishNumFromBegin + infoLosedNumFromBegin;                                                       //所有结束的信息 数
         dayNumFromBirth = TimeUtil.dayNumFromDate(birthYear, birthMonth, birthDay, yearNow, monthNow, dayInMonth);

        show_picture_text = (TextView) findViewById(R.id.show_picture_text);
        show_picture_text.setText("日期:"+yearNow+"年"+monthNow+"月"+dayInMonth+"日");

        month_day_bar= (ProgressBar) findViewById(R.id.month_day_probar);                   //本月天数信息
        month_day_bar.setMax(numMaxInMonth);
        month_day_bar.setProgress(dayInMonth);
        month_day_text = (TextView) findViewById(R.id.month_day_text);
        double a = (dayInMonth * 100 / numMaxInMonth / 1.0);
        month_day_text.setText("本月为" + monthNow + "月,共" + numMaxInMonth + "天,今天是第" + dayInMonth + "天," + monthNow + "月已经过了" + a + "%");


        month_work_bar = (ProgressBar) findViewById(R.id.month_work_probar);            //本月任务信息
        month_work_bar.setMax(infoNumMaxInMonth);
        month_work_bar.setProgress(infoNumLosedInMonth);
        month_work_text = (TextView) findViewById(R.id.month_work_text);


        double b;
        if (infoNumMaxInMonth == 0) {
            b = 0.00;
        }else {
            b = (infoNumLosedInMonth * 100 / infoNumMaxInMonth / 1.0);
        }
        month_work_text.setText("目前为止本月事务共"+infoNumMaxInMonth+"件,有"+infoNumDoneInMonth+"件已经结束,其中失败"+infoNumLosedInMonth+"件,失败率为"+b+"%");





        year_day_bar = (ProgressBar) findViewById(R.id.year_day_probar);                    //今年天数信息
        year_day_bar.setMax(numMaxInYear);
        year_day_bar.setProgress(dayInYear);
        year_day_text = (TextView) findViewById(R.id.year_day_text);
        double c = (dayInYear * 100 / numMaxInYear / 1.0);
        year_day_text.setText("今年共" + numMaxInYear + "天,今天是第" + dayInYear + "天," + yearNow + "年已经过去" + c + "%");



        year_work_bar = (ProgressBar) findViewById(R.id.year_work_probar);                  //今年任务信息
        year_work_bar.setMax(infoNumMaxInYear);
        year_work_bar.setProgress(infoNumLosedInYear);
        year_work_text = (TextView) findViewById(R.id.year_work_text);

        double d;
        if (infoNumMaxInYear== 0) {
            d = 0.00;
        }else {
            d = (infoNumLosedInYear * 100 / infoNumMaxInYear / 1.0);
        }
        year_work_text.setText("目前为止今年事务共" + infoNumMaxInMonth + ",有" + infoNumDoneInYear + "任务已经结束,其中失败" + infoNumLosedInYear + "件,失败率为" + d + "%");



        begin_day_bar = (ProgressBar) findViewById(R.id.begin_day_probar);                 //使用至今天数信息   全部完成任务
        begin_day_bar.setMax(dayNumFromBegin);
        begin_day_bar.setProgress(dayAllNumFinishFromBegin);
        begin_day_text = (TextView) findViewById(R.id.begin_day_text);
        double e = (dayAllNumFinishFromBegin * 100 / dayNumFromBegin / 1.0);
        begin_day_text.setText("从使用本应用开始共" + dayNumFromBegin + "天,有" + dayAllNumFinishFromBegin + "天全部完成任务,全部完成率为" + e + "%");




        begin_work_bar = (ProgressBar) findViewById(R.id.begin_work_probar);             //使用至今任务数信息
        begin_work_bar.setMax(infoALL);
        begin_work_bar.setProgress(infoLosedNumFromBegin);
        begin_work_text = (TextView) findViewById(R.id.begin_work_text);
        double f;
        if (infoALL == 0) {
            f = 0.00;
        }else {
            f = (infoLosedNumFromBegin * 100 / infoALL / 1.0);
        }
        begin_work_text.setText("从使用本应用开始共记录" + infoALL + "件事务,其中失败" + infoLosedNumFromBegin + "件,失败率为" + f + "%");




        begin_day_bar1 = (ProgressBar) findViewById(R.id.begin_day_probar1);                 // 使用至今信息  无记数
        begin_day_bar1.setMax(dayNumFromBegin);
        begin_day_bar1.setProgress(dayUnMarkNum);
        begin_day_text1 = (TextView) findViewById(R.id.begin_day_text1);
        double g = (dayUnMarkNum * 100 / dayNumFromBegin / 1.0);           //无记率
        double h = (infoALL*100/ dayNumFromBegin / 100.0);                //每天平均事务数量
        begin_day_text1.setText("从使用本应用开始有"+dayUnMarkNum+"天没有任何记录,无记率为"+g+"%,平均每天事务数量为"+h+"件/天");
        Log.d("ShowStatisticsActivity", beginYear + ":" + beginMonth + ":" + beginDay );



        age_bar = (ProgressBar) findViewById(R.id.age_probar);                          //暴露年龄信息
        age_bar.setMax(AVERAGE);
        age_bar.setProgress(dayNumFromBirth);
        age_text = (TextView) findViewById(R.id.age_text);
        double i = dayNumFromBirth*10000/ AVERAGE/100.0;
        age_text.setText("截止目前已经度过生命的"+i+"% (按2015人口普查平均寿命76岁(27760天));请珍惜时间");
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
