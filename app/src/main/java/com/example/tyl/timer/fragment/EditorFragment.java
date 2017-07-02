package com.example.tyl.timer.fragment;

;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tyl.timer.R;
import com.example.tyl.timer.activity.MainActivity;
import com.example.tyl.timer.util.Day;
import com.example.tyl.timer.util.DayCompare;
import com.example.tyl.timer.util.DaysAdapter;
import com.example.tyl.timer.util.DefaultItemTouchHelpCallback;
import com.example.tyl.timer.util.DefaultItemTouchHelper;
import com.example.tyl.timer.util.Information;
import com.example.tyl.timer.util.MyDatabaseHelper;
import com.example.tyl.timer.util.TimeUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**1、碎片中加载daylist并呈现信息                 y
 *
 * 2、增加一个floatingButton动态添加day的信息          y
 *
 * 3、可以进行动态滑动删除 3.1 今天或之前不可变      3.2今天之后没有infromation的可以删除         y
 *
 * 4*、添加dayitem时应弹出activity，let user operation,and limit user operation (day>today)
 *
 *
 *
 *
 * Created by TYL on 2017/6/12.
 */
//以天为单位展示任务的完成情况，点击可以 进入当天的编辑
public class EditorFragment extends Fragment {

    private static List<Day>   mDays;
        RecyclerView mRecyclerView;
   static FloatingActionButton mFloatingActionButton;

   static DaysAdapter daysAdapter;
   public MainActivity mMainActivity;

    public static DaysAdapter getDaysAdapter() {
        return daysAdapter;
    }


    public static List<Day> getmDays() {
        return mDays;
    }

    public static void setmDays(List<Day> mDays) {
        EditorFragment.mDays = mDays;
    }

    /**
     * 滑动 对不同状态下的Day效果不同
     */

    DefaultItemTouchHelpCallback.OnItemTouchCallbackListener mOnItemTouchCallbackListener = new DefaultItemTouchHelpCallback.OnItemTouchCallbackListener() {
        @Override       //滑动删除 1、今天和之前不可删  2、未来有记录不可删
        public void onSwiped(int adapterPosition) {
            if (mDays != null) {
                Day day = mDays.get(adapterPosition);

                switch (day.getStatus()) {
                    case -1:                                                             // 删除一个新建的空壳
                        mDays.remove(adapterPosition);
                        daysAdapter.notifyItemRemoved(adapterPosition);
//                        daysAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                    case 0:                                                             //已经发生的  昨日
                    case 1:                                                             //正在发生的  今天
                        Toast.makeText(getActivity(), "凡已发生，皆不可移！", Toast.LENGTH_SHORT).show();
                        daysAdapter.notifyDataSetChanged();
                        break;
                    case 2:                                                             //还未发生的  未来
                        if (MyDatabaseHelper.sMyDatabaseHelper.getList(day.getYear(), day.getMonth(), day.getDay()).size()!=0) {

                            Toast.makeText(getActivity(), "计划在轨，当先移取", Toast.LENGTH_SHORT).show();
                            daysAdapter.notifyDataSetChanged();
                        } else {
                            mDays.remove(adapterPosition);
                            daysAdapter.notifyItemRemoved(adapterPosition);
//                            daysAdapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), "本来无一物，何处染尘埃", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text, container, false);
         mRecyclerView = (RecyclerView) view.findViewById(R.id.editor_recycler);

        //获取系统的今天日期
//        TimeUtil time = new TimeUtil();
        //点进相关记录后进入当天的情况，可以预览当天的安排详情，如果是还未到来的时间则可以进入进行编辑

        //加载daylist信息
        Log.d("fragment", "在下要加载了哦>.>");
        mDays=MyDatabaseHelper.sMyDatabaseHelper.getDays();
      Collections.sort(mDays, new DayCompare());
        mMainActivity = (MainActivity) getActivity();
       daysAdapter = new DaysAdapter(mDays,mMainActivity);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(daysAdapter);
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DefaultItemTouchHelper itemTouchHelper = new DefaultItemTouchHelper(mOnItemTouchCallbackListener);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        // floatingButton动态添加view
        MainActivity mainActivity = (MainActivity) getActivity();
        mFloatingActionButton = mainActivity.getFloatingActionButton();
        mFloatingActionButton.setOnClickListener(new   View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Day day = new Day();
                mDays.add(0,day);
                daysAdapter.notifyItemInserted(0);
//                daysAdapter.notifyDataSetChanged();
            }
        });





    }




























}
