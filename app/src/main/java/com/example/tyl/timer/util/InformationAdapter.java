package com.example.tyl.timer.util;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tyl.timer.BroadcastReceiver.MyReceiver;
import com.example.tyl.timer.R;

import com.example.tyl.timer.activity.ShowInformationActivity;
import com.example.tyl.timer.activity.ItemEditorActivity;
import com.example.tyl.timer.fragment.EditorFragment;
import com.example.tyl.timer.service.MyService;

import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.os.Build.VERSION_CODES.M;
import static com.example.tyl.timer.R.id.main_fragment;
import static com.example.tyl.timer.R.id.text_view;
import static com.example.tyl.timer.fragment.EditorFragment.getDaysAdapter;


/**
 * Created by TYL on 2017/6/16.
 */

public class InformationAdapter extends RecyclerView.Adapter<InformationAdapter.ViewHolder> {

    int InfoState;
    static final int FUTURE = 2;
    static final int HAPPEND = 0;
    static final int NOW = 1;

    static ShowInformationActivity mEditorActivity;

   static List<Information> mInformationList;


    class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView hour_view;
        TextView minute_view;
        TextView lastTime;
        TextView text_view;
        Button alarm_button;

        public ViewHolder(View view) {
            super(view);
            mView = view.findViewById(R.id.editor_button);
            hour_view = (TextView) view.findViewById(R.id.hour_view);
            minute_view = (TextView) view.findViewById(R.id.minute_view);
            lastTime = (TextView) view.findViewById(R.id.lastTime);
            text_view = (TextView) view.findViewById(R.id.text_view);
            alarm_button = (Button) view.findViewById(R.id.alarm_button);
        }
    }
    public InformationAdapter(List<Information> informationList, ShowInformationActivity editorActivity) {
        mInformationList = informationList;
        mEditorActivity = editorActivity;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_information_adapter, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        InfoState = mEditorActivity.getStateFromDay();
        //      点击进入编辑界面
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int position = holder.getAdapterPosition();
                Information information = mInformationList.get(position);








                switch (InfoState) {     //根据天日期来决定0过去 1今天 未来2
                    case HAPPEND:


                        Toast.makeText(mEditorActivity, "不可对今天前的信息进行更改（逝者如斯，一去无返）", Toast.LENGTH_SHORT).show();
                        break;
                    //首先对条目进行判断看是不是1、已经发生的 2、已经启动的，对于1不可改，对于2进行数据库相关删除操作
                    // 再此处点击可以启动startActivityForResult,来获在IetmEditorActivity中编辑的内容并补充到

                    case NOW:
                        switch (information.getCompleted()) {
                            case  -1:
                                Intent intent = new Intent(mEditorActivity, ItemEditorActivity.class);
                                intent.putExtra("position", position);
                            //    intent.putExtra("id", information.getId());
                                intent.putExtra("hour", information.getHour());
                                intent.putExtra("minute", information.getMinute());
                                intent.putExtra("information", information.getInformation());
                                intent.putExtra("lastTime", information.getLastTme());
                                mEditorActivity.startActivityForResult(intent, 123);
                                break;
                            case 0:
                                Toast.makeText(mEditorActivity, "计划在轨，当先移取", Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Toast.makeText(mEditorActivity, "因果已动，只有成败!", Toast.LENGTH_SHORT).show();
                                break;
                            case  2:
                            case 3:

                                Toast.makeText(mEditorActivity, "凡已发生，皆不可移", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        break;
                    case FUTURE:
                        //状态是关于今天以后的，弹出条目编辑界面可对未来进行编辑   还未发生，默认
                        switch (information.getCompleted()) {
                            case -1:
                                Intent intent = new Intent(mEditorActivity, ItemEditorActivity.class);
//                                intent.putExtra("id", information.getId());
                                intent.putExtra("hour", information.getHour());
                                intent.putExtra("minute", information.getMinute());
                                intent.putExtra("information", information.getInformation());
                                mEditorActivity.startActivityForResult(intent, 123);

                            case 0:
                               Toast.makeText(mEditorActivity,"计划在轨，当先移取",Toast.LENGTH_SHORT).show();
                            default:
                                break;
                        }
                }
            }
        });


        //  设置是否开启任务,及注册alram任务，完成对数据库的操作（监控completed参数）
        holder.alarm_button.setOnClickListener(new View.OnClickListener() {



























            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                final Information information = mInformationList.get(position);
                int state = information.getCompleted();

                switch (state) {
                    case -1:

                        if ((information.getHour() != 0) || (information.getMinute() != 0)||(information.getLastTme()!=0) || (information.getInformation() != null)) {

                            //弹出对话在此完成设定并对数据库更新并改变条目颜色
                            AlertDialog.Builder dialog = new AlertDialog.Builder(InformationAdapter.this.mEditorActivity);
                            dialog.setTitle("注意");
                            dialog.setMessage("你确定计划如此吗？");
                            dialog.setCancelable(false);
                            dialog.setPositiveButton("是的", new DialogInterface.OnClickListener() {


                                @Override

                                public void onClick(DialogInterface dialog, int which) {

//                                    MyDatabaseHelper dbHelper = new MyDatabaseHelper(mEditorActivity, "Timer.db", null, 1);

                                    if ((70 * (information.getHour() -TimeUtil.getHour()) + (information.getMinute() - TimeUtil.getMinuts())) > 0) {     //在启动时现实时间允许
//                                        dbHelper.addInformation(information.getYear(), information.getMonth(), information.getDay(), information.getHour(),
//                                                information.getMinute(),information.getLastTme(), information.getCompleted(), information.getInformation());

                                        information.setCompleted(0);    //0的状态是已在安排中但未执行

                                        MyDatabaseHelper.sMyDatabaseHelper.addInformation(information.getYear(), information.getMonth(), information.getDay(), information.getHour(),

                                                information.getMinute(),information.getLastTme(), information.getCompleted(), information.getInformation());
//                                            holder.alarm_button.setBackgroundResource(R.drawable.zen_icons_key);

                                        ShowInformationActivity.mAdapter.notifyDataSetChanged();

                                        mEditorActivity.getmAlarmBinder().startAlarm(information.getYear(), information.getMonth(), information.getDay(), information.getHour(), information.getMinute(), information.getLastTme(),information.getInformation());


                                        Day day = EditorFragment.getmDays().get(mEditorActivity.getPosition());
                                        day.plusTheRest();
                                        EditorFragment.getDaysAdapter().notifyDataSetChanged();

                                    } else {
                                        Toast.makeText(mEditorActivity, "午时已过", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                            dialog.setNegativeButton("我在考虑下", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    }
                            );
                            dialog.show();

                        } else {
                            Toast.makeText(mEditorActivity, "毫无计划，漏洞百出", Toast.LENGTH_SHORT).show();
                        }


                        break;

                    case 0:
                        //已经启动却未发生的，弹出对话框是否更改，是的话从数据库删除条目，并将该条目在list中的状态变为-1
                        AlertDialog.Builder dialog = new AlertDialog.Builder(InformationAdapter.this.mEditorActivity);
                        dialog.setTitle("注意");
                        dialog.setMessage("你确定要取消一个之前的计划？");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("是的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                MyDatabaseHelper dbHelper = new MyDatabaseHelper(mEditorActivity, "Timer.db", null, 1);
                                information.setCompleted(-1);
                                dbHelper.deletInformation(information.getYear(), information.getMonth(), information.getDay(), information.getHour(),
                                        information.getMinute());

                                ShowInformationActivity.mAdapter.notifyDataSetChanged();

                                mEditorActivity.getmAlarmBinder().cancelAlarm(information.getYear(), information.getMonth(), information.getDay(), information.getHour(), information.getMinute());

                                Day day = EditorFragment.getmDays().get(mEditorActivity.getPosition());
                                day.minusTheRest();
                                EditorFragment.getDaysAdapter().notifyDataSetChanged();
                            }
                        });

                        dialog.setNegativeButton("我在考虑下", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        dialog.show();

                        break;

                    case 1:                  //正在发生中
                        Toast.makeText(mEditorActivity, "因果已动，只有成败！", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                    case 3:
                        Toast.makeText(mEditorActivity, "凡已发生，皆不可移", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }











            }
        });
        return holder;
    }


    //将数据绑定视图
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Information information = mInformationList.get(position);
        if(information!=null) {
            holder.hour_view.setText(information.getHour() + "小时");
            holder.minute_view.setText(information.getMinute() + "分钟");
            holder.text_view.setText("需用时"+information.getInformation()+"分钟");
            switch (information.getCompleted()) {
                case 0:
                    holder.alarm_button.setBackgroundResource(R.mipmap.ic_launcher);   //还未到
                    break;
                case 1:
                    holder.alarm_button.setBackgroundResource(R.drawable.zen_icons_key);   //正在进行
                    break;
                case 2:
                    holder.alarm_button.setBackgroundResource(R.drawable.zen_icons_lock_closed );   //已经完成
                    break;
                case 3:
                    holder.alarm_button.setBackgroundResource(R.drawable.zen_icons_mail_empty);   //没有完成
                    break;
                case -1:
                    holder.alarm_button.setBackgroundResource(R.drawable.zen_icons_lock_open);   //混沌之初
                    break;
            }
        }

    }
    //    返回一共有多少视图
    @Override
    public int getItemCount() {
        return mInformationList.size();
    }
}
