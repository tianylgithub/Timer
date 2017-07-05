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


/**
 * Created by TYL on 2017/6/13.
 */
//在此编辑具体的任务信息 。 此活动为透明色，可以看到上一个活动的信息却无法操作。
public class ItemEditorActivity extends AppCompatActivity {
    EditText mHourEditor;
    EditText mMinutEditor;
    EditText mEditText;
    EditText mTaskEditor;
    Button set_Button;
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
            if(!(s.toString().equals(""))){
                int num = Integer.valueOf(s.toString());
                switch (mEditText.getId()) {
                    case R.id.hour_text:
                        if (num > 23) {
                            Toast.makeText(ItemEditorActivity.this, "小时请从0-23中的整数选一", Toast.LENGTH_SHORT).show();
                            mEditText.setText("");
                        }
                        break;
                    case R.id.minute_text:
                        if (num > 59) {
                            Toast.makeText(ItemEditorActivity.this, "分钟请从0-59整数中选一", Toast.LENGTH_SHORT).show();
                            mEditText.setText("");
                        }
                        break;
                    case R.id.last_text:
                        if (num <= 0) {
                            Toast.makeText(ItemEditorActivity.this, "请输入大于零的数字", Toast.LENGTH_SHORT).show();
                            mEditText.setText("");
                        }
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_information_activity);
        mHourEditor = (EditText) findViewById(R.id.hour_text);
        mMinutEditor = (EditText) findViewById(R.id.minute_text);


        mTaskEditor = (EditText) findViewById(R.id.task_text);
        mEditText = (EditText) findViewById(R.id.last_text);
         Intent intent = getIntent();

        position = intent.getIntExtra("position", -1);

        int hour = intent.getIntExtra("hour", 0);
        mHourEditor.setText(hour==0? "":hour+"");
        mHourEditor.addTextChangedListener(new mTextWatcher(mHourEditor));
        int minute = intent.getIntExtra("minute", 0);
        mMinutEditor.setText(minute==0? "":minute+"");
        mMinutEditor.addTextChangedListener(new mTextWatcher(mMinutEditor));
        int lastTime = intent.getIntExtra("lastTime", 0);
        mEditText.setText(lastTime==0? "":lastTime+"");
        mEditText.addTextChangedListener(new mTextWatcher(mEditText));
        mTaskEditor.setText(intent.getStringExtra("information")+"");

        set_Button = (Button) findViewById(R.id.set_button);


        set_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String hour = mHourEditor.getText().toString();
                String minute = mMinutEditor.getText().toString();
                String lastTime = mEditText.getText().toString();
                String information = mTaskEditor.getText().toString();

                Intent intentResult = new Intent();
                intentResult.putExtra("hour", hour);
                intentResult.putExtra("minute", minute);
                intentResult.putExtra("lastTime", lastTime);
                intentResult.putExtra("information", information);
                intentResult.putExtra("position", position);
                setResult(RESULT_OK,intentResult);
               ItemEditorActivity.this.finish();

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

                ItemEditorActivity.this.finish();
            }
        });

        dialog.setNegativeButton("我考虑下", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        dialog.show();
    }

}
