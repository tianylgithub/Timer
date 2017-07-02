package com.example.tyl.timer.util;

import java.util.Comparator;

/**
 * Created by TYL on 2017/6/18.
 */

public class InformationCompare implements Comparator<Information> {
    @Override
    public int compare(Information o1, Information o2) {
        return 70* (o1.getHour() - o2.getHour())+(o1.getMinute() - o2.getMinute());             //排序规则
        }
    }
