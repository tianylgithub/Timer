package com.example.tyl.timer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tyl.timer.R;
import com.example.tyl.timer.fragment.EditorFragment;
import com.example.tyl.timer.util.Day;

import java.util.List;

public class Search extends AppCompatActivity {



    class mTextWatcher1 implements TextWatcher {

        EditText mEditText;

        mTextWatcher1(EditText e) {
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
                    Toast.makeText(Search.this, "年月日需要以0开头吗?！", Toast.LENGTH_SHORT).show();
                    s.clear();
                }

                switch (mEditText.getId()) {

                    case R.id.month_search:
                        if (num>=13) {
                            Toast.makeText(Search.this, "月份请从1-12中选一", Toast.LENGTH_SHORT).show();
                            s.clear();
                        }
                        break;
                    case R.id.day_search:
                        if (num>32) {
                            Toast.makeText(Search.this, "天数请从1-31中选一", Toast.LENGTH_SHORT).show();
                            s.clear();
                        }
                        break;
                    default:
                        break;

                }

            }
        }
    }

    EditText year_search;
    EditText month_search;
    EditText day_search;
    Button search_button;

    @Override
    public void onBackPressed() {
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        year_search = (EditText) findViewById(R.id.year_search);
        year_search.addTextChangedListener(new mTextWatcher1(year_search));
        month_search = (EditText) findViewById(R.id.month_search);
        month_search.addTextChangedListener(new mTextWatcher1(month_search));
        day_search = (EditText) findViewById(R.id.day_search);
        day_search.addTextChangedListener(new mTextWatcher1(day_search));
        search_button = (Button) findViewById(R.id.search_button);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String year = year_search.getText().toString();
                int intYear = (year.equals("") ?  0 : Integer.valueOf(year));
                String month = month_search.getText().toString();
                int intMonth = (month.equals("") ? 0 : Integer.valueOf(month));
                String day = day_search.getText().toString();
                int  intDay=(day.equals("") ? 0 : Integer.valueOf(day));
                if(intYear*intMonth*intDay==0){
                    Toast.makeText(Search.this, "你确定你输入的是完整日期?", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    int position = getPosition(intYear, intMonth, intDay);
                    if (position != -1) {
                        int state = EditorFragment.getmDays().get(position).getStatus();
                        Intent intent1 = new Intent(Search.this, ShowInformationActivity.class);
                        intent1.putExtra("year", intYear);
                        intent1.putExtra("month", intMonth);
                        intent1.putExtra("day", intDay);
                        intent1.putExtra("state", state);
                        intent1.putExtra("position", position);
                        Search.this.startActivity(intent1);
                        Search.this.finish();
                    }else {
                        Toast.makeText(Search.this,"你要查看的日期并不存在",Toast.LENGTH_SHORT).show();
                        Search.this.finish();
                    }


                }
            }
        });

    }


    int getPosition(int year, int month, int day) {
        List<Day> dayList= EditorFragment.getmDays();
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












}
