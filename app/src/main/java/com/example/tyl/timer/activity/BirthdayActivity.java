package com.example.tyl.timer.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tyl.timer.R;
import com.example.tyl.timer.util.TimeUtil;

public class BirthdayActivity extends AppCompatActivity {

    EditText birth_year;
    EditText birth_month;
    EditText birth_day;
    Button dataButton;



    class mTextWatcher implements TextWatcher {

        EditText mEditText;

        mTextWatcher(EditText e) {
            mEditText = e;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
        @Override
        public void afterTextChanged(Editable s) {
            if (!s.toString().equals("")) {

                int num = Integer.valueOf(s.toString());
                int len = s.toString().length();
                if (len == 1 && num == 0) {
                    Toast.makeText(BirthdayActivity.this, "年月日需要以0开头吗?！", Toast.LENGTH_SHORT).show();
                    s.clear();
                }

                switch (mEditText.getId()) {
                    case R.id.birth_year:
                        if (num> TimeUtil.getYear()+1) {
                            Toast.makeText(BirthdayActivity.this, "好高骛远可不行，请专注当下", Toast.LENGTH_SHORT).show();
                            s.clear();
                        }
                        break;
                    case R.id.birth_month:
                        if (num>=13) {
                            Toast.makeText(BirthdayActivity.this, "月份请从1-12中选一", Toast.LENGTH_SHORT).show();
                            s.clear();
                        }
                        break;
                    case R.id.birth_day:
                        if (num>32) {
                            Toast.makeText(BirthdayActivity.this, "天数请从1-31中选一", Toast.LENGTH_SHORT).show();
                            s.clear();
                        }
                        break;
                }
            }
        }
    }
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        Log.d("BirthdayActivity", "onCreate方法执行");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthday);
        birth_year = (EditText) findViewById(R.id.birth_year);

        birth_year.addTextChangedListener(new mTextWatcher(birth_year));

        birth_month = (EditText) findViewById(R.id.birth_month);

        birth_month.addTextChangedListener(new mTextWatcher(birth_month));

        birth_day = (EditText) findViewById(R.id.birth_day);

        birth_day.addTextChangedListener(new mTextWatcher(birth_day));
        dataButton = (Button) findViewById(R.id.set_Birth_Date_button);
        dataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String year = birth_year.getText().toString();
               final int intYear = (year.equals("") ? 0 : Integer.valueOf(year));
                final  String month = birth_month.getText().toString();
                final  int intMonth = (month.equals("") ? 0 : Integer.valueOf(month));
                final   String day = birth_day.getText().toString();
                final   int intDay = (day.equals("") ? 0 : Integer.valueOf(day));

                if (intYear * intMonth * intDay == 0) {
                    Toast.makeText(BirthdayActivity.this, "请输入完整的日期", Toast.LENGTH_SHORT).show();
                    return;
                } else if (intYear > TimeUtil.getYear() ) {
                    Toast.makeText(BirthdayActivity.this, "您输入的年份有误", Toast.LENGTH_SHORT).show();
                    return;
                } else if (intMonth>12) {
                    Toast.makeText(BirthdayActivity.this,"您输入的月份有误", Toast.LENGTH_SHORT).show();
                    return;
                } else if (intDay > TimeUtil.getMaxDayNumByYM(intYear, intMonth)) {
                    Toast.makeText(BirthdayActivity.this, "您输入的天数有误", Toast.LENGTH_SHORT).show();
                    return;
                }else {

                    AlertDialog.Builder dialog = new AlertDialog.Builder(BirthdayActivity.this);
                    dialog.setTitle("注意:");
                    dialog.setMessage("你的生日确定为"+intYear+"年"+intMonth+"月"+intDay+"日?");
                    dialog.setCancelable(false);

                    dialog.setNegativeButton("重新输入", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }

                            }
                    );

                    dialog.setPositiveButton("是的", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(BirthdayActivity.this).edit();
                            editor.putInt("birthYear", intYear);
                            editor.putInt("birthMonth", intMonth);
                            editor.putInt("birthDay", intDay);
                            editor.apply();
                            Intent intent = new Intent(BirthdayActivity.this, ShowPictureActivity.class);
                            startActivity(intent);
                            BirthdayActivity.this.finish();
                            Log.d("BirthdayActivity", "Birthday界面结束");
                        }
                    }
                    );
                    dialog.show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "请先输入出生日期", Toast.LENGTH_SHORT).show();
    }
}
