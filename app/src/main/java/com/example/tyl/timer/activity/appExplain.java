package com.example.tyl.timer.activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tyl.timer.R;

public class appExplain extends AppCompatActivity {

    TextView mTextView;
    Button mButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_explain);
        mTextView = (TextView) findViewById(R.id.app_text);
        mButton = (Button) findViewById(R.id.app_closeButton);
        mTextView.setText("时间计划器是一个可以制定计划并统计完成情况的应用,利用它你能看到自己的规划多少得到了实施.希望能督促它你尽可能达成自己的规划,享受自律带来的" +
                "自信.\n ps:图谱功能和监听功能还在完成中,敬请期待.如果您有什么想对我说的,请加QQ:962638847.");
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

    }

    @Override
    public void onBackPressed() {
    }
}
