package com.example.tyl.timer.util;

import java.util.Comparator;

/**
 * Created by TYL on 2017/6/18.
 */

public class DayCompare implements Comparator<Day> {
    @Override
    public int compare(Day o1, Day o2) {
        return  700*(o1.getYear() - o2.getYear()) + 35*(o1.getMonth() - o2.getMonth()) + (o1.getDay() - o2.getDay());    //排序规则
    }
}
