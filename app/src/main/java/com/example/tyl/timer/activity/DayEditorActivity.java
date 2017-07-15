package com.example.tyl.timer.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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


/**
 *
 *
 *
 * Created by TYL on 2017/6/21.
 */

public class DayEditorActivity extends AppCompatActivity {
    EditText year_editor;
    EditText month_editor;
    EditText day_editor;
    Button setDate_button;
    int position;

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
                    Toast.makeText(DayEditorActivity.this, "年月日不需要以0开头", Toast.LENGTH_SHORT).show();
                    s.clear();
                }

                switch (mEditText.getId()) {
                    case R.id.year_editor:
                        if (num>TimeUtil.getYear()+1) {
                            Toast.makeText(DayEditorActivity.this, "好高骛远可不行，请专注当下", Toast.LENGTH_SHORT).show();
                            s.clear();
                        }
                        break;
                    case R.id.month_editor:
                        if (num>=13) {
                            Toast.makeText(DayEditorActivity.this, "月份请从1-12中选一", Toast.LENGTH_SHORT).show();
                            s.clear();
                        }
                        break;
                    case R.id.day_editor:
                        if (num>32) {
                            Toast.makeText(DayEditorActivity.this, "天数请从1-31中选一", Toast.LENGTH_SHORT).show();
                            s.clear();
                        }
                        break;
                }
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_day);
        year_editor = (EditText) findViewById(R.id.year_editor);
        month_editor = (EditText) findViewById(R.id.month_editor);
        day_editor = (EditText) findViewById(R.id.day_editor);
        Intent intent = getIntent();

        year_editor.setText(TimeUtil.getYear()+"");
        year_editor.addTextChangedListener(new mTextWatcher(year_editor));

        month_editor.setText(TimeUtil.getMonth()+"");
        month_editor.addTextChangedListener(new mTextWatcher(month_editor));

        day_editor.setText("");
        day_editor.addTextChangedListener(new mTextWatcher(day_editor));
        position = intent.getIntExtra("position", 0);
        setDate_button = (Button) findViewById(R.id.setDate_button);
        setDate_button.setOnClickListener(new View.OnClickListener(){
            @Override                                                                       //限制year，mont，day在未来
            public void onClick(View v) {
                String year = year_editor.getText().toString();
                int intYear = (year.equals("") ?  0 : Integer.valueOf(year));
                String month = month_editor.getText().toString();
                int intMonth = (month.equals("") ? 0 : Integer.valueOf(month));
                String day = day_editor.getText().toString();
                int  intDay=(day.equals("") ? 0 : Integer.valueOf(day));
                if(intYear*intMonth*intDay==0){
                    Toast.makeText(DayEditorActivity.this, "请输入完整的日期", Toast.LENGTH_SHORT).show();
                    return;
                } else if((700*(intYear- TimeUtil.getYear())+50*(intMonth-TimeUtil.getMonth())+intDay-TimeUtil.getDay())<=0){
                    Toast.makeText(DayEditorActivity.this, "只能对今天以后做些什么！", Toast.LENGTH_SHORT).show();
                    return;
                }else if(intDay > TimeUtil.getMaxDayNumByYM(intYear, intMonth)){
                    Toast.makeText(DayEditorActivity.this, "输入的日期有误", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(MyDatabaseHelper.mSQLiteDatabase.query("TABLE_DAY",null,"year=? AND month=? AND day=?",new String[]{year+"",month+"",day+""},null,null,null).getCount()!=0){
                    Toast.makeText(DayEditorActivity.this, "日期已经存在，请移步编辑", Toast.LENGTH_SHORT).show();
                        DayEditorActivity.this.finish();
                }
                else  {
                    Intent intent = new Intent();
                    intent.putExtra("year", intYear);
                    intent.putExtra("month", intMonth);
                    intent.putExtra("day", intDay);
                    intent.putExtra("position", position);
                    setResult(RESULT_OK, intent);
                    DayEditorActivity.this.finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("注意");
        dialog.setMessage("没有点确定返回编辑的信息将无法保存，继续？");
        dialog.setCancelable(false);
        dialog.setPositiveButton("是的", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DayEditorActivity.this.finish();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    return;
            }
        });
        dialog.show();
    }


}
