package com.example.tyl.timer.util;

import java.util.Comparator;

/**
 * Created by TYL on 2017/6/18.
 */

public class DayCompare implements Comparator<Day> {
    @Override
    public int compare(Day o1, Day o2) {

        switch (o1.getStatus()) {
            case -1:                                                            //o1是空条目
                switch (o2.getStatus()) {
                    case -1:                                                    //o2是空条目
                        return 0;
                    default:                                                    //o2不是空条目,此时o1在o2之前
                        return -1;
                }
            default:                                                        //o1不是空条目
                switch (o2.getStatus()) {
                    case -1:                                                //o2是空条目
                        return 1;
                    default:                                                //o2.不是空条目
                        return 700 * (o2.getYear() - o1.getYear()) + 35 * (o2.getMonth() - o1.getMonth()) + (o2.getDay() - o1.getDay());

                }
        }

    }
}
