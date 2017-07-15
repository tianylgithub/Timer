package com.example.tyl.timer.util;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tyl.timer.R;
import com.example.tyl.timer.activity.DayEditorActivity;
import com.example.tyl.timer.activity.MainActivity;
import com.example.tyl.timer.activity.ShowInformationActivity;

import java.util.List;

/**
 * Created by TYL on 2017/6/15.
 */

public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.ViewHolder> {



    static final int NOTHING= -1;
    static final  int   PAST = 0;
    static final   int NOW = 1;
    static final   int FUTURE=2;
  static   MainActivity     mMainActivity;


    private   static    List<Day> mDayList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView date;
        TextView show_all;
        TextView show_haveDone;
        TextView show_haveLosed;
        TextView show_theRest_or_finish_percent;
        ProgressBar mProgressBar;
        View dayView;
        ImageView status;

        public ViewHolder(View view) {
            super(view);
            dayView = view;
            date = (TextView) view.findViewById(R.id.date);
            show_all = (TextView) view.findViewById(R.id.show_all);
            show_haveDone = (TextView) view.findViewById(R.id.show_havedone);
            show_haveLosed = (TextView) view.findViewById(R.id.show_havelosed);
            show_theRest_or_finish_percent = (TextView) view.findViewById(R.id.show_therest_or_finish_percent);
            mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
            status = (ImageView) view.findViewById(R.id.status);
        }
    }

    public DaysAdapter(List<Day>  dayList, MainActivity activity) {
        mDayList = dayList;
        mMainActivity = activity;
    }

    //创建ViewHolder，Item视图缓存保存；并设置监听器，并将条目级别设置到Intent中传给下一个活动

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_day, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.dayView.setOnClickListener(new   View.OnClickListener(){
            @Override
            public void onClick(View v) {

                int position = holder.getAdapterPosition();
                Day day = mDayList.get(position);
                switch (day.getStatus()){
                    case PAST:                                    //状态是 已完成、进行时、未来时打开信息界面
                    case NOW:
                    case FUTURE:
                Intent intent = new Intent(mMainActivity, ShowInformationActivity.class);
                        intent.putExtra("dayID", day.getId());
                intent.putExtra("year", day.getYear());
                intent.putExtra("month", day.getMonth());
                intent.putExtra("day", day.getDay());
                intent.putExtra("state", day.getStatus());
                        intent.putExtra("position", position);
                mMainActivity.startActivity(intent);
                        break;
                    case  NOTHING:                                   //状态是空白是打开是编辑day的界面
                        Intent i = new Intent(mMainActivity, DayEditorActivity.class);
                        i.putExtra("position", position);
                        mMainActivity.startActivityForResult(i,666);
                        break;
                }
            }
        });
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Day day = mDayList.get(position);
       holder.date.setText(day.getYear()+"-"+day.getMonth()+"-"+day.getDay());
        int finish = day.getDone();
        int all = day.getAll();
        int theRest = day.getTheRest();
        int losed = day.getLosed();
        holder.show_all.setText("计划:"+all);
        holder.show_haveDone.setText("完成:" + finish);
        holder.show_haveLosed.setText("失败:" + losed);

        switch (day.getStatus()) {
            case PAST:
                holder.status.setImageResource(R.drawable.daypast);   //过去的
                switch (all) {
                    case 0:
                        holder.show_theRest_or_finish_percent.setText("完成率:0%");
                        break;
                    default:
                        holder.show_theRest_or_finish_percent.setText("完成率:" + finish*10000 / all/10000.00*100+"%");
                        break;
                }
                break;
            case NOW:
                holder.status.setImageResource(R.drawable.daynow); //现在的
                holder.show_theRest_or_finish_percent.setText("待办:" + theRest);
                break;
            case FUTURE:
                holder.status.setImageResource(R.drawable.dayfuturehaveinfo);//未来的有info
                holder.show_theRest_or_finish_percent.setText("待办:" + theRest);
                break;
            case NOTHING:
                holder.status.setImageResource(R.drawable.dayfuturenoinfo);//未来的没有info
                holder.show_theRest_or_finish_percent.setText("待办:" + theRest);
                break;
        }

        holder.mProgressBar.setMax(all);
        holder.mProgressBar.setProgress(finish);

    }

    @Override
    public int getItemCount() {
        Log.d("info_adapter", "不好意思你输了");
        return mDayList.size();

    }
}
