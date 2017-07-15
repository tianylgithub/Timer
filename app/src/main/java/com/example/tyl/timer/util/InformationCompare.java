package com.example.tyl.timer.util;

import java.util.Comparator;

/**  目的，由大到小排   值>0. o1在o2后面
 * Created by TYL on 2017/6/18.
 */

public class InformationCompare implements Comparator<Information> {
    @Override
    public int compare(Information o1, Information o2) {

        switch (o1.getCompleted()) {

            case -1:                                                                        //o1未安排的任务

                switch (o2.getCompleted()) {
                    case -1:                                                             //o1未安排，o2未安排:

                        int i =70*(o2.getHour() - o1.getHour())+(o2.getMinute()-o1.getMinute());
                        switch (i) {
                            case 0:
                                return o2.getLastTime() - o1.getLastTime();
                            default:
                                return i;
                        }

                        default:                            //o1未安排，o2已被安排，此时o1应在o2前面
                            return -1;
                }
            default:                                            //o1被安排
                switch (o2.getCompleted()) {
                    case -1:
                        return 1;                               //o1被安排，o2未被安排，此时o1在o2后面

                    default:                                    //o1、o2被安排

                        int i=70*(o2.getHour() - o1.getHour())+(o2.getMinute() - o1.getMinute());
                        switch (i) {
                            case 0:
                                return o2.getLastTime() - o1.getLastTime();
                            default:
                                return i;
                        }

                }
        }
        }
    }
