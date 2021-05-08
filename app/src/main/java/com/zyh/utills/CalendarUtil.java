package com.zyh.utills;


import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

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
    /**
     * date2比date1多的天数
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDays(Date date1,Date date2)
    {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1= cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if(year1 != year2)   //不同一年
        {
            int timeDistance = 0 ;
            for(int i = year1 ; i < year2 ; i ++)
            {
                if(i%4==0 && i%100!=0 || i%400==0)    //闰年            
                {
                    timeDistance += 366;
                }
                else    //不是闰年
                {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2-day1) ;
        }
        else    //同一年
        {
            //System.out.println("判断day2 - day1 : " + (day2-day1));
            return day2-day1;
        }
    }
    /**
     * 实现给定某日期，判断是星期几
     * @param date 日期，必须为yyyy-MM-dd格式
     * @return
     */
    public static int getWeekday(String date){
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdw = new SimpleDateFormat("E");
        Date d = null;
        try {
            d = sd.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String str = sdw.format(d);
        return getOneWeekday(str);
    }
    public static int getOneWeekday(String str){
        if(str.equals("星期一")) return 1;
        else if(str.equals("星期二")) return 2;
        else if(str.equals("星期三")) return 3;
        else if(str.equals("星期四")) return 4;
        else if(str.equals("星期五")) return 5;
        else if(str.equals("星期六")) return 6;
        else  return 7;
    }
    public static String getWeekday(int day){
        if(day==1) return "周一";
        else if(day==2) return "周二";
        else if(day==3) return "周三";
        else if(day==4) return "周四";
        else if(day==5) return "周五";
        else if(day==6) return "周六";
        else  return "周日";
    }
    public static int getNowWeek(String date1ate,int oldWeek){
        int ans = oldWeek;
        int oldxq = getWeekday(date1ate); //旧星期
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        Date oldDate = null;
        try {
            oldDate = sd.parse(date1ate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date nowDate = new Date(); //今天日期
        if(oldxq!=7){
            if(differentDays(oldDate,nowDate)<7-oldxq) return ans; //在当周
            else { //不在当周
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(oldDate);
                calendar.add(calendar.DATE,7-oldxq); //把日期往后增加一天,正数往后推,负数往前移动
                oldDate=calendar.getTime();  //下周天日期
                ans+=1;//第二周
                return ans + (differentDays(oldDate,nowDate) / 7);
            }
        }else return ans + (differentDays(oldDate,nowDate) / 7);
    }

    /**
     * 13位时间戳转换成"yyyy-MM-dd HH:mm:ss"
     * @param time
     * @return
     */
    public static String timeStamp2Date(String time) {
        Long timeLong = Long.parseLong(time);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");//要转换的时间格式
        Date date;
        try {
            date = sdf.parse(sdf.format(timeLong));
            return sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
