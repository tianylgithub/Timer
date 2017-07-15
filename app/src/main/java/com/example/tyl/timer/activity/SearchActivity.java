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
import com.example.tyl.timer.util.MyDatabaseHelper;
import com.example.tyl.timer.util.TimeUtil;

public class SearchActivity extends AppCompatActivity {
    class mTextWatcher1 implements TextWatcher {
        EditText mEditText;
        mTextWatcher1(EditText e) {
            mEditText = e;
        }                                       //此时需要让你年份成为四位数
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
                    Toast.makeText(SearchActivity.this, "年月日需要以0开头吗?！", Toast.LENGTH_SHORT).show();
                    s.clear();
                }
                switch (mEditText.getId()) {

                    case R.id.month_search:
                        if (num>=13) {
                            Toast.makeText(SearchActivity.this, "月份请从1-12中选一", Toast.LENGTH_SHORT).show();
                            s.clear();
                        }
                        break;
                    case R.id.day_search:
                        if (num>32) {
                            Toast.makeText(SearchActivity.this, "天数请从1-31中选一", Toast.LENGTH_SHORT).show();
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
    EditText information_search;
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
        year_search.setText(TimeUtil.getYear()+"");
        year_search.addTextChangedListener(new mTextWatcher1(year_search));
        month_search = (EditText) findViewById(R.id.month_search);
        month_search.setText(TimeUtil.getMonth()+"");
        month_search.addTextChangedListener(new mTextWatcher1(month_search));
        day_search = (EditText) findViewById(R.id.day_search);
        day_search.addTextChangedListener(new mTextWatcher1(day_search));
        information_search = (EditText) findViewById(R.id.information_search);
        search_button = (Button) findViewById(R.id.search_button);

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String year = year_search.getText().toString();
                year = (year.equals("") ?  null : year);
                String month = month_search.getText().toString();
                month = (month.equals("") ?  null : month);
                String day = day_search.getText().toString();
                day = (day.equals("") ?  null: day);
                String information = information_search.getText().toString();
                information = (information.equals("") ? null : information);

                if (MyDatabaseHelper.have_search_haveData(year, month, day, information)) {
                    Intent intent = new Intent(SearchActivity.this, ShowSearchActivity.class);
                    intent.putExtra("year", year);
                    intent.putExtra("month", month);
                    intent.putExtra("day", day);
                    intent.putExtra("information", information);
                    SearchActivity.this.startActivity(intent);
                    SearchActivity.this.finish();
                }else {                                                                                        //不存在要找内容
                    Toast.makeText(SearchActivity.this, "不存在要找的内容", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }
}
