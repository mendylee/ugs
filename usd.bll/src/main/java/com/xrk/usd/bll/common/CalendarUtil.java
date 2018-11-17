package com.xrk.usd.bll.common;

import java.util.Calendar;

/**
 * 简单时间处理工具
 */
public class CalendarUtil {

    /**
     * 一天中的开始时间
     * @return
     */
    public static Calendar getBeginByDay(){
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY,0);
        now.set(Calendar.MINUTE,0);
        now.set(Calendar.SECOND,0);
        now.set(Calendar.MILLISECOND,0);
        return now;
    }

    /**
     * 一天中的结束时间
     * @return
     */
    public static Calendar getEndByDay(){
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY,23);
        now.set(Calendar.MINUTE,59);
        now.set(Calendar.SECOND,59);
        now.set(Calendar.MILLISECOND,999);
        return now;
    }

    /**
     * 一年中的开始时间
     * @return
     */
    public static Calendar getBeginByYear(){
        Calendar now = Calendar.getInstance();
        now.set(now.get(Calendar.YEAR),1,1,0,0,0);
        return now;
    }

    /**
     * 一年中的结束时间
     * @return
     */
    public static Calendar getEndByYear(){
        Calendar now = Calendar.getInstance();
        now.set(now.get(Calendar.YEAR),12,31,23,59,59);
        return now;
    }
}
