package com.example.tyl.timer.util;

import java.util.Comparator;

/**
 * Created by TYL on 2017/7/8.
 */

public class Information16Compare implements Comparator<Information> {
    @Override





    public int compare(Information o1, Information o2) {
        int i= (8000000 * (o1.getYear() - o2.getYear()) + 65000 * (o1.getMonth() - o2.getMonth()) + 2000 * (o1.getDay() - o2.getDay()) + 70 * (o1.getHour() - o2.getHour()) + o1.getMinute() - o2.getMinute());

        switch (i) {
            case 0:
                return o1.getCompleted() - o2.getLastTime();
                default:
                    return i;
        }



    }
}
