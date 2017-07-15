package com.example.tyl.timer.activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tyl.timer.R;

public class AppExplainActivity extends AppCompatActivity {

    TextView mTextView;
    TextView mTextTips;
    Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_explain);
        Toast.makeText(this,"欢迎使用本应用!",Toast.LENGTH_SHORT).show();
        mTextView = (TextView) findViewById(R.id.app_text);
        mTextTips = (TextView) findViewById(R.id.app_tips);
        mToolbar = (Toolbar) findViewById(R.id.explan_toolbar);
        mToolbar.setTitle("使用说明:");
        mTextView.setText("事务规划器是一个可以制定计划并统计完成情况的应用,利用它你能看到自己的规划多少得到了实施.希望能督促它你尽可能达成自己的规划,享受自律带来的" +
                "自信.");
        mTextTips.setText("注意: 本应用需要开启通知栏显示才能使用,请打开手机 设置-通知和状态栏 开启本应用的通知许可;因为各手机厂商的不同,下面图标的最后三个通知图标" +
                "可能无法正常显示(比如小米通知栏可能只显示应用的图标)，功能正常使用。");

    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
