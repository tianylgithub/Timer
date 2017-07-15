package com.example.tyl.timer.util;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tyl.timer.R;
import com.example.tyl.timer.activity.ShowInformationActivity;
import com.example.tyl.timer.activity.ShowSearchActivity;

import java.util.ArrayList;

/**
 * Created by TYL on 2017/7/10.
 */

public class SearchAdapterDay extends RecyclerView.Adapter<SearchAdapterDay.ViewHolder> {
    static final int PAST = 0;
    static final int NOW = 1;
    static final int FUTURE = 2;


    ShowSearchActivity mShowSearchActivity;
    ArrayList<Day> mDays;


    public SearchAdapterDay(ShowSearchActivity showSearchActivity) {
        mShowSearchActivity = showSearchActivity;
        mDays = showSearchActivity.getArrayList();
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView search_date;
        TextView search_all;
        TextView search_havefinish;
        TextView search_havelosed;
        TextView search_therest_or_finish_percent ;
        ProgressBar search_progressbar;
        ImageView search_status;
        public ViewHolder(View view) {
            super(view);
            mView = view;
            search_date = (TextView) view.findViewById(R.id.date);
            search_all = (TextView) view.findViewById(R.id.show_all);
            search_havefinish = (TextView) view.findViewById(R.id.show_havedone);
            search_havelosed = (TextView) view.findViewById(R.id.show_havelosed);

            search_therest_or_finish_percent = (TextView) view.findViewById(R.id.show_therest_or_finish_percent);
            search_progressbar = (ProgressBar) view.findViewById(R.id.progressbar);
            search_status = (ImageView) view.findViewById(R.id.status);
        }
    }
    @Override
    public int getItemCount() {
        return mDays.size();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_day, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                Day day = mDays.get(position);
                Intent intent = new Intent(mShowSearchActivity, ShowInformationActivity.class);
                intent.putExtra("year", day.getYear());
                intent.putExtra("month", day.getMonth());
                intent.putExtra("day", day.getDay());
                intent.putExtra("state", day.getStatus());
                intent.putExtra("dayID", day.getId());
                mShowSearchActivity.startActivity(intent);

            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Day day = mDays.get(position);
        holder.search_date.setText(day.getYear() + "-" + day.getMonth() + "-" + day.getDay());

        int finish = day.getDone();
        int all = day.getAll();
        int theRest = day.getTheRest();
        int losed = day.getLosed();
        holder.search_all.setText("计划:"+all);

        holder.search_havefinish.setText("完成:" + finish);
        holder.search_havelosed.setText("失败:" + losed);

        switch (day.getStatus()) {
            case PAST:
                holder.search_status.setImageResource(R.drawable.daypast);   //过去的
                switch (all) {
                    case 0:
                        holder.search_therest_or_finish_percent.setText("完成率:0%");
                        break;
                    default:
                        holder.search_therest_or_finish_percent.setText("完成率:" + finish*10000 / all/10000.00*100+"%");
                        break;
                }
                break;
            case NOW:
                holder.search_status.setImageResource(R.drawable.daynow); //现在的
                holder.search_therest_or_finish_percent.setText("待办:" + theRest);
                break;
            case FUTURE:
                holder.search_status.setImageResource(R.drawable.dayfuturehaveinfo);//未来的有info
                holder.search_therest_or_finish_percent.setText("待办:" + theRest);
                break;
        }
        holder.search_progressbar.setMax(all);
        holder.search_progressbar.setProgress(finish);

    }

}