package com.example.tyl.timer.util;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tyl.timer.R;
import com.example.tyl.timer.activity.ShowInformationActivity;
import com.example.tyl.timer.activity.ShowInformationSelectActivity;
import com.example.tyl.timer.fragment.EditorFragment;
import com.example.tyl.timer.service.MyService;

import java.util.LinkedList;
import java.util.List;

/**     展示需要选择结果事务的适配器
 * Created by TYL on 2017/7/8.
 */

public class informaitonSelectAdapter extends RecyclerView.Adapter<informaitonSelectAdapter.ViewHolder> {

    LinkedList<Information> mInformationSelectList = MyService.sSelectActivitiesList;

    ShowInformationSelectActivity mShowInformationSelectActivity;

   public informaitonSelectAdapter(ShowInformationSelectActivity showInformationSelectActivity) {
        mShowInformationSelectActivity = showInformationSelectActivity;
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView select_time;
        TextView select_information;
        Button done_button;
        Button lose_button;

        public ViewHolder(View view) {
            super(view);
            select_time = (TextView) view.findViewById(R.id.info_select_time);
            select_information = (TextView) view.findViewById(R.id.info_select_information);
            done_button = (Button) view.findViewById(R.id.select_done_button);
            lose_button     = (Button) view.findViewById(R.id.select_lose_button);
        }
    }
    @Override
    public int getItemCount() {
        return mInformationSelectList.size();
    }
    @Override
    public  ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_select_information, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.done_button.setOnClickListener(new View.OnClickListener() {                          //任务完成，
            @Override
            public void onClick(View v) {
                final int position = viewHolder.getAdapterPosition();
                final Information   information= mInformationSelectList.get(position);
               final int id = information.getId();
                final int dayID = information.getDayID();
                AlertDialog.Builder dialog = new AlertDialog.Builder(mShowInformationSelectActivity);
                dialog.setTitle("注意:");
                dialog.setMessage("我已完成事务?");
                dialog.setCancelable(false);
                dialog.setPositiveButton("是的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        MyDatabaseHelper.sMyDatabaseHelper.updateInformationById(id,2);    //更改数据库状态为2，

                        MyService.sSelectActivitiesList.remove(information);

                        mShowInformationSelectActivity.getAdapter().notifyItemRemoved(position);

                        List<Day> dayList = EditorFragment.getmDays();
                        if (dayList != null) {
                            for (Day day : dayList) {
                                if (day.getId() == dayID) {
                                    day.plusDone();
                                }
                            }
                            EditorFragment.getDaysAdapter().notifyDataSetChanged();
                        }                                                                                //更改daylist 的显示信息  Done+1
                        List<Information> informationList = ShowInformationActivity.getEditorlist();
                        if (informationList != null) {
                            for (Information information : informationList) {
                                if (information.getId() == id) {
                                    information.setCompleted(2);
                                }
                            }
                            ShowInformationActivity.getmAdapter().notifyDataSetChanged();           //更改infolist显示 6为2
                        }
                        mShowInformationSelectActivity.getAlarmBinder().infoHintOrSelectChangeForeground();                             //只是为了更改Foreground                             //只是为了更改Foreground
                        if (MyService.sSelectActivitiesList.size() == 0) {
                            mShowInformationSelectActivity.finish();
                            Toast.makeText(mShowInformationSelectActivity, "确定完毕!", Toast.LENGTH_SHORT).show();
                        }
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
        });
        viewHolder.lose_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = viewHolder.getAdapterPosition();
                final Information   information= mInformationSelectList.get(position);
                final int id = information.getId();
                final int dayID = information.getDayID();
                AlertDialog.Builder dialog = new AlertDialog.Builder(mShowInformationSelectActivity);
                dialog.setTitle("注意:");
                dialog.setMessage("我没有完成事务?");
                dialog.setCancelable(false);
                dialog.setPositiveButton("是的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        MyDatabaseHelper.sMyDatabaseHelper.updateInformationById(id, 3);    //更改数据库状态为2，
                        MyService.sSelectActivitiesList.remove(information);
                        mShowInformationSelectActivity.getAdapter().notifyItemRemoved(position);
                        List<Day> dayList = EditorFragment.getmDays();
                        if (dayList != null) {
                            for (Day day : dayList) {
                                if (day.getId() == dayID) {
                                    day.plusLosed();
                                }
                            }
                            EditorFragment.getDaysAdapter().notifyDataSetChanged();
                        }                                                                                //更改daylist 的显示信息  Losed+1
                        List<Information> informationList = ShowInformationActivity.getEditorlist();
                        if (informationList != null) {
                            for (Information information : informationList) {
                                if (information.getId() == id) {
                                    information.setCompleted(3);
                                }
                            }
                            ShowInformationActivity.getmAdapter().notifyDataSetChanged();           //更改infolist显示 1为3
                        }
                            mShowInformationSelectActivity.getAlarmBinder().infoHintOrSelectChangeForeground();                             //只是为了更改Foreground
                            if (MyService.sSelectActivitiesList .size() == 0) {
                                mShowInformationSelectActivity.finish();
//                                Intent intent = new Intent(mShowInformationSelectActivity, MainActivity.class);
//                                mShowInformationSelectActivity.startActivity(intent);
                                Toast.makeText(mShowInformationSelectActivity, "确定完毕!", Toast.LENGTH_SHORT).show();
                            }
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
        });
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Information information =  mInformationSelectList.get(position);
        holder.select_time.setText("开始时间:"+information.getMonth()+"."+information.getDay()+" "+information.getHour()+":"+information.getMinute()+"  持续时间:"+information.getLastTime()+"分钟");
        holder.select_information.setText("事务:"+information.getInformation());
    }

}
