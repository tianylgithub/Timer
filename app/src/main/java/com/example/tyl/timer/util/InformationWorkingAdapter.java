package com.example.tyl.timer.util;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tyl.timer.R;
import com.example.tyl.timer.service.MyService;

import java.util.LinkedList;

/** 展示正在进行事务的适配器
 * Created by TYL on 2017/7/8.
 */

public class InformationWorkingAdapter extends RecyclerView.Adapter<InformationWorkingAdapter.ViewHolder> {

    LinkedList<Information> mInformationHintList = MyService.sInformationHintList;


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView info_time;
        TextView info_information;

        public ViewHolder(View view) {
            super(view);
            info_time = (TextView) view.findViewById(R.id.info_working_time);
            info_information = (TextView) view.findViewById(R.id.info_working_information);
        }
    }
    @Override
    public int getItemCount() {
        return mInformationHintList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_working_information, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Information information = mInformationHintList.get(position);
        holder.info_time.setText("开始时间:" + information.getMonth() + "." + information.getDay() + " " + information.getHour() + ":" + information.getMinute() + "  持续时间:" + information.getLastTime()+"分钟");
        holder.info_information.setText("事务:"+information.getInformation());
    }
}
