package com.example.tyl.timer.util;

import android.app.Application;
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
import com.example.tyl.timer.activity.MainActivity;
import com.example.tyl.timer.activity.ShowInformationActivity;
import com.example.tyl.timer.activity.dayHint;

import java.util.List;

/**
 * Created by TYL on 2017/6/15.
 */

public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.ViewHolder> {



    int mCurrentYear = TimeUtil.getYear();
    int mCurrentMonth = TimeUtil.getMonth();
    int mCurrentDay = TimeUtil.getDay();


    int   dayState;
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
        TextView show_theRest;
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
            show_theRest = (TextView) view.findViewById(R.id.show_therest);
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day_recycler, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.dayView.setOnClickListener(new   View.OnClickListener(){
            @Override
            public void onClick(View v) {

                int position = holder.getAdapterPosition();
                Day day = mDayList.get(position);
                switch (day.getStatus()){
                    case 0:                                    //状态是 已完成、进行时、未来时打开信息界面
                    case 1:
                    case 2:
                Intent intent = new Intent(mMainActivity, ShowInformationActivity.class);
                intent.putExtra("year", day.getYear());
                intent.putExtra("month", day.getMonth());
                intent.putExtra("day", day.getDay());
                intent.putExtra("state", day.getStatus());
                        intent.putExtra("position", position);
                mMainActivity.startActivity(intent);
                        break;
                    case  -1:                                   //状态是空白是打开是编辑day的界面
                        Intent i = new Intent(mMainActivity, dayHint.class);
                        i.putExtra("position", position);
                        mMainActivity.startActivityForResult(i,666);
                        break;


//                        switch (day.getAll()){
//                            case 0:                         //因新的一天到来刚刚创立还未有information内容
//                                Intent i1 = new Intent(mMainActivity, dayHint.class);
//                                i1.putExtra("position", position);
//                                mMainActivity.startActivityForResult(i1,1);
//                                break;
//                            default:
//                                Intent dintent= new Intent(mMainActivity, ShowInformationActivity.class);
//                                dintent.putExtra("year", day.getYear());
//                                dintent.putExtra("month", day.getMonth());
//                                dintent.putExtra("day", day.getDay());
//                                dintent.putExtra("state", dayState);
//                                mMainActivity.startActivity(dintent);
//                                break;
//                        }

                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Day day = mDayList.get(position);
       holder.date.setText(day.getYear()+"年"+day.getMonth()+"月"+day.getDay()+"日");
        holder.show_theRest.setText("剩下:" + day.getTheRest());
        holder.show_haveDone.setText("已完成:" + day.getDone());
        holder.show_haveLosed.setText("未完成:" + day.getLosed());
        holder.mProgressBar.setMax(day.getAll());
        holder.mProgressBar.setProgress(day.getDone()+day.getLosed());


        switch (day.getStatus()) {
            case 0:
                holder.status.setImageResource(R.drawable.axes);   //过去的
                break;
            case 1:
                holder.status.setImageResource(R.drawable.canoe); //现在的
                break;
            case 2:
            case -1:
                holder.status.setImageResource(R.drawable.feathers);//未来的
                break;
        }

//        if (150*(day.getYear() - mCurrentYear) +70*( day.getMonth() - mCurrentMonth)+( day.getDay()-mCurrentDay)<0) {
//            holder.status.setImageResource(R.drawable.axes);        //过去的，不可编辑的
//            dayState = PAST;
//        } else if(150*(day.getYear() - mCurrentYear) +70*( day.getMonth() - mCurrentMonth)+( day.getDay()-mCurrentDay)>0){
//            holder.status.setImageResource(R.drawable.feathers);    //未来的，可以编辑的
//            dayState = FUTURE;
//        }   else {
//            holder.status.setImageResource(R.drawable.canoe);       //现在的，部分编辑的
//            dayState = NOW;
//        }
    }

    @Override
    public int getItemCount() {
        Log.d("info_adapter", "不好意思你输了");
        return mDayList.size();

    }
}
