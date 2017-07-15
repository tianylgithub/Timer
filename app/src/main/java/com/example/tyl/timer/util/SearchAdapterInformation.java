package com.example.tyl.timer.util;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.tyl.timer.R;
import com.example.tyl.timer.activity.ShowInformationActivity;
import com.example.tyl.timer.activity.ShowSearchActivity;

import java.util.ArrayList;

/**
 * Created by TYL on 2017/7/10.
 */

public class SearchAdapterInformation extends RecyclerView.Adapter<SearchAdapterInformation.ViewHolder> {

    ShowSearchActivity mShowSearchActivity;
    ArrayList<Information> mInformationArrayList;

    static final int PLAN = 0;
    static final int WORKING = 1;
    static final int FINISHED = 2;
    static final int UNFINISHED = 3;
    static final int WARING = 6;

    public SearchAdapterInformation(ShowSearchActivity showSearchActivity) {
        mShowSearchActivity = showSearchActivity;
        mInformationArrayList = showSearchActivity.getArrayList();
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView search_beginTime;
        TextView search_lastTime;
        TextView search_textView;
        View search_information_show;
        Button search_imageView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            search_beginTime = (TextView) view.findViewById(R.id.begin_time);
            search_lastTime = (TextView) view.findViewById(R.id.lastTime);
            search_textView = (TextView) view.findViewById(R.id.text_view);
            search_information_show = view.findViewById(R.id.information_show);
            search_imageView = (Button) view.findViewById(R.id.alarm_button);
        }
    }
    @Override
    public int getItemCount() {
        return mInformationArrayList.size();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_information, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                Information information = mInformationArrayList.get(position);
                int dayID = information.getDayID();
                int status = MyDatabaseHelper.getStatusByDayID(dayID);
                Intent intent = new Intent(mShowSearchActivity, ShowInformationActivity.class);
                intent.putExtra("year", information.getYear());
                intent.putExtra("month", information.getMonth());
                intent.putExtra("day", information.getDay());
                intent.putExtra("state", status);
                intent.putExtra("dayID", dayID);
                mShowSearchActivity.startActivity(intent);
            }
        });
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Information information = mInformationArrayList.get(position);
        holder.search_beginTime.setText("开始:"+information.getYear()+"."+information.getMonth() + "." + information.getDay() + "  " + information.getHour() + ":" + information.getMinute());
        holder.search_lastTime.setText("持续:" + information.getLastTime()+"分钟");
        holder.search_textView.setText("事务:" + information.getInformation());
        switch (information.getCompleted()) {
            case PLAN:
                holder.search_imageView.setBackgroundResource(R.drawable.infornothing);   //还未到
                holder.search_information_show.setBackgroundColor(Color.parseColor("#CCFF66"));
                break;
            case WORKING:
                holder.search_imageView.setBackgroundResource(R.drawable.infoworiking);   //正在进行
                holder.search_information_show.setBackgroundColor(Color.parseColor("#0099FF"));
                break;
            case FINISHED:
                holder.search_imageView.setBackgroundResource(R.drawable.infofinish);   //已经完成
                holder.search_information_show.setBackgroundColor(Color.parseColor("#666699"));
                break;
            case UNFINISHED:
                holder.search_imageView.setBackgroundResource(R.drawable.infolosed);   //没有完成
                holder.search_information_show.setBackgroundColor(Color.parseColor("#0066CC"));
                break;
            case WARING:
                holder.search_imageView.setBackgroundResource(R.drawable.infofinishbutnocomfurm);   //已结束但还没确认的信息
                holder.search_information_show.setBackgroundColor(Color.parseColor("#FFFF66"));
                break;
            default:
                break;
        }
    }
}