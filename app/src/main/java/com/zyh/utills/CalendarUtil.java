package com.zyh.utills;


import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author zsw
 * @date 2019/11/19 20:44
 */
public class CalendarUtil {
    /**
     * days为负数获取前 days天
     * days为整数获取后 days 天
     * @param now
     * @param sdf
     * @param days
     * @return
     */
    public static String getDateOfDesignDay(Date now, SimpleDateFormat sdf, int days) {
        try {
            Calendar c = Calendar.getInstance();
            if (null != now) {
                c.setTime(now);
            }
            c.add(Calendar.DAY_OF_YEAR, days);
            return sdf.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "123";
    }

    /**
     * 获得指定日期的前一天,只返回天
     * @param specifiedDay
     * @return
     * @throws Exception
     */
    public static String getDayOfSpecifiedDayBefore(String specifiedDay){
        Calendar c = Calendar.getInstance();
        Date date=null;
        try {
            date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day=c.get(Calendar.DATE);
        c.set(Calendar.DATE,day-1);
        int lastDay = c.get(Calendar.DATE);//获取日
        return lastDay+"";
    }
    /**
     * 获得指定日期的前一天,只返回天
     * @param specifiedDay
     * @return
     * @throws Exception
     */
    public static String getMonthOfSpecifiedDayBefore(String specifiedDay){
        Calendar c = Calendar.getInstance();
        Date date=null;
        try {
            date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day=c.get(Calendar.DATE);
        c.set(Calendar.DATE,day-1);
        //获取月份，0表示1月份
        int month = c.get(Calendar.MONTH) + 1;
        return month+"";
    }
}
