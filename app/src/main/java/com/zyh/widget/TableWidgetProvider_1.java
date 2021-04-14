package com.zyh.widget;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.SystemClock;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.zyh.beans.Account;
import com.zyh.beans.Course;
import com.zyh.beans.CourseBean;
import com.zyh.beans.CourseList;
import com.zyh.beans.CourseId;
import com.zyh.fragment.R;
import com.zyh.utills.CalendarUtil;
import com.zyh.utills.Utills;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * onEnable() ：当小部件第一次被添加到桌面时回调该方法，可添加多次，但只在第一次调用。对用广播的 Action 为 ACTION_APPWIDGET_ENABLE。
 *
 * onUpdate():  当小部件被添加时或者每次小部件更新时都会调用一次该方法，配置文件中配置小部件的更新周期 updatePeriodMillis，每次更新都会调用。
 *               对应广播 Action 为：ACTION_APPWIDGET_UPDATE 和 ACTION_APPWIDGET_RESTORED 。
 *
 * onDisabled(): 当最后一个该类型的小部件从桌面移除时调用，对应的广播的 Action 为 ACTION_APPWIDGET_DISABLED。
 *
 * onDeleted(): 每删除一个小部件就调用一次。对应的广播的 Action 为： ACTION_APPWIDGET_DELETED 。
 *
 * onRestored(): 当小部件从备份中还原，或者恢复设置的时候，会调用，实际用的比较少。对应广播的 Action 为 ACTION_APPWIDGET_RESTORED。
 *
 * onAppWidgetOptionsChanged(): 当小部件布局发生更改的时候调用。对应广播的 Action 为 ACTION_APPWIDGET_OPTIONS_CHANGED。
 */

public class TableWidgetProvider_1 extends AppWidgetProvider {
    public static final String TAG = "TableWidgetProvider_1";
    public static final String APPWIDGET_ENABLED = "android.appwidget.action.APPWIDGET_ENABLED";
    public static final String CLICK_ACTION = "com.ryg.chapter_5.action.CLICK";
    public static final String NEXT_WEEK = "com.zyh.eduadminsystem.NEXT_WEEK";
    public static final String NOW_WEEK = "com.zyh.eduadminsystem.NOW_WEEK";
    private String username;
    private String semester;
    private String week;
    private String time;
    private int nowWeek;
    private CourseList couList;
    private CourseBean courseBean;
    private List<List<CourseBean.Course>> courseList;

    private RemoteViews remoteViews;
    private int month;
    private int monthWord;
    private int[] weekDate;
    private CourseId[][] courseMsgs;
    private CourseId[][] course2Msgs;
    private int[][] courseItems;
    private int[][] course2Items;
    private int[] weekday;
    private int[] weekdayLine;
    private static int next = 1;
    public TableWidgetProvider_1() {
        super();
        Log.i(TAG, "TableWidgetProvider_1() : start");
    }
    public void initCourseView(){
        //int[][] courseItems, CourseId[][] courseMsgs,int[][] course2Items,CourseId[][] course2Msgs
        /*courseItems*/

        courseItems = new int[5][7];
        courseMsgs = new CourseId[5][7];
        course2Items = new int[2][7];     //占两大节的课
        course2Msgs = new CourseId[2][7];
        weekday = new int[8];
        weekdayLine = new int[8];
        courseItems[0][0] = R.id.widget_course_1_1;
        courseItems[0][1] = R.id.widget_course_2_1;
        courseItems[0][2] = R.id.widget_course_3_1;
        courseItems[0][3] =  R.id.widget_course_4_1;
        courseItems[0][4] =  R.id.widget_course_5_1;
        courseItems[0][5] =  R.id.widget_course_6_1;
        courseItems[0][6] =  R.id.widget_course_7_1;

        courseItems[1][0] =  R.id.widget_course_1_2;
        courseItems[1][1] =  R.id.widget_course_2_2;
        courseItems[1][2] =  R.id.widget_course_3_2;
        courseItems[1][3] =  R.id.widget_course_4_2;
        courseItems[1][4] =  R.id.widget_course_5_2;
        courseItems[1][5] =  R.id.widget_course_6_2;
        courseItems[1][6] =  R.id.widget_course_7_2;

        courseItems[2][0] =  R.id.widget_course_1_3;
        courseItems[2][1] =  R.id.widget_course_2_3;
        courseItems[2][2] =  R.id.widget_course_3_3;
        courseItems[2][3] =  R.id.widget_course_4_3;
        courseItems[2][4] =  R.id.widget_course_5_3;
        courseItems[2][5] =  R.id.widget_course_6_3;
        courseItems[2][6] =  R.id.widget_course_7_3;

        courseItems[3][0] =  R.id.widget_course_1_4;
        courseItems[3][1] =  R.id.widget_course_2_4;
        courseItems[3][2] =  R.id.widget_course_3_4;
        courseItems[3][3] =  R.id.widget_course_4_4;
        courseItems[3][4] =  R.id.widget_course_5_4;
        courseItems[3][5] =  R.id.widget_course_6_4;
        courseItems[3][6] =  R.id.widget_course_7_4;

        courseItems[4][0] =  R.id.widget_course_1_5;
        courseItems[4][1] =  R.id.widget_course_2_5;
        courseItems[4][2] =  R.id.widget_course_3_5;
        courseItems[4][3] =  R.id.widget_course_4_5;
        courseItems[4][4] =  R.id.widget_course_5_5;
        courseItems[4][5] =  R.id.widget_course_6_5;
        courseItems[4][6] =  R.id.widget_course_7_5;

        /*courseMsgs*/

        courseMsgs[0][0] = new CourseId(R.id.widget_course_1_1_name,
                 R.id.widget_course_1_1_place,R.id.widget_course_1_1_property);
        courseMsgs[0][1] = new CourseId(R.id.widget_course_2_1_name,
                 R.id.widget_course_2_1_place,R.id.widget_course_2_1_property);
        courseMsgs[0][2] = new CourseId(R.id.widget_course_3_1_name,
                 R.id.widget_course_3_1_place,R.id.widget_course_3_1_property);
        courseMsgs[0][3] = new CourseId(R.id.widget_course_4_1_name,
                 R.id.widget_course_4_1_place,R.id.widget_course_4_1_property);
        courseMsgs[0][4] = new CourseId(R.id.widget_course_5_1_name,
                 R.id.widget_course_5_1_place,R.id.widget_course_5_1_property);
        courseMsgs[0][5] = new CourseId(R.id.widget_course_6_1_name,
                 R.id.widget_course_6_1_place,R.id.widget_course_6_1_property);
        courseMsgs[0][6] = new CourseId(R.id.widget_course_7_1_name,
                 R.id.widget_course_7_1_place,R.id.widget_course_7_1_property);

        courseMsgs[1][0] = new CourseId(R.id.widget_course_1_2_name,
                 R.id.widget_course_1_2_place,R.id.widget_course_1_2_property);
        courseMsgs[1][1] = new CourseId(R.id.widget_course_2_2_name,
                 R.id.widget_course_2_2_place,R.id.widget_course_2_2_property);
        courseMsgs[1][2] = new CourseId(R.id.widget_course_3_2_name,
                 R.id.widget_course_3_2_place,R.id.widget_course_3_2_property);
        courseMsgs[1][3] = new CourseId(R.id.widget_course_4_2_name,
                 R.id.widget_course_4_2_place,R.id.widget_course_4_2_property);
        courseMsgs[1][4] = new CourseId(R.id.widget_course_5_2_name,
                 R.id.widget_course_5_2_place,R.id.widget_course_5_2_property);
        courseMsgs[1][5] = new CourseId(R.id.widget_course_6_2_name,
                 R.id.widget_course_6_2_place,R.id.widget_course_6_2_property);
        courseMsgs[1][6] = new CourseId(R.id.widget_course_7_2_name,
                 R.id.widget_course_7_2_place,R.id.widget_course_7_2_property);

        courseMsgs[2][0] = new CourseId(R.id.widget_course_1_3_name,
                 R.id.widget_course_1_3_place,R.id.widget_course_1_3_property);
        courseMsgs[2][1] = new CourseId(R.id.widget_course_2_3_name,
                 R.id.widget_course_2_3_place,R.id.widget_course_2_3_property);
        courseMsgs[2][2] = new CourseId(R.id.widget_course_3_3_name,
                 R.id.widget_course_3_3_place,R.id.widget_course_3_3_property);
        courseMsgs[2][3] = new CourseId(R.id.widget_course_4_3_name,
                 R.id.widget_course_4_3_place,R.id.widget_course_4_3_property);
        courseMsgs[2][4] = new CourseId(R.id.widget_course_5_3_name,
                 R.id.widget_course_5_3_place,R.id.widget_course_5_3_property);
        courseMsgs[2][5] = new CourseId(R.id.widget_course_6_3_name,
                 R.id.widget_course_6_3_place,R.id.widget_course_6_3_property);
        courseMsgs[2][6] = new CourseId(R.id.widget_course_7_3_name,
                 R.id.widget_course_7_3_place,R.id.widget_course_7_3_property);

        courseMsgs[3][0] = new CourseId(R.id.widget_course_1_4_name,
                 R.id.widget_course_1_4_place,R.id.widget_course_1_4_property);
        courseMsgs[3][1] = new CourseId(R.id.widget_course_2_4_name,
                 R.id.widget_course_2_4_place,R.id.widget_course_2_4_property);
        courseMsgs[3][2] = new CourseId(R.id.widget_course_3_4_name,
                 R.id.widget_course_3_4_place,R.id.widget_course_3_4_property);
        courseMsgs[3][3] = new CourseId(R.id.widget_course_4_4_name,
                 R.id.widget_course_4_4_place,R.id.widget_course_4_4_property);
        courseMsgs[3][4] = new CourseId(R.id.widget_course_5_4_name,
                 R.id.widget_course_5_4_place,R.id.widget_course_5_4_property);
        courseMsgs[3][5] = new CourseId(R.id.widget_course_6_4_name,
                 R.id.widget_course_6_4_place,R.id.widget_course_6_4_property);
        courseMsgs[3][6] = new CourseId(R.id.widget_course_7_4_name,
                 R.id.widget_course_7_4_place,R.id.widget_course_7_4_property);

        courseMsgs[4][0] = new CourseId(R.id.widget_course_1_5_name,
                 R.id.widget_course_1_5_place,R.id.widget_course_1_5_property);
        courseMsgs[4][1] = new CourseId(R.id.widget_course_2_5_name,
                 R.id.widget_course_2_5_place,R.id.widget_course_2_5_property);
        courseMsgs[4][2] = new CourseId(R.id.widget_course_3_5_name,
                 R.id.widget_course_3_5_place,R.id.widget_course_3_5_property);
        courseMsgs[4][3] = new CourseId(R.id.widget_course_4_5_name,
                 R.id.widget_course_4_5_place,R.id.widget_course_4_5_property);
        courseMsgs[4][4] = new CourseId(R.id.widget_course_5_5_name,
                 R.id.widget_course_5_5_place,R.id.widget_course_5_5_property);
        courseMsgs[4][5] = new CourseId(R.id.widget_course_6_5_name,
                 R.id.widget_course_6_5_place,R.id.widget_course_6_5_property);
        courseMsgs[4][6] = new CourseId(R.id.widget_course_7_5_name,
                 R.id.widget_course_7_5_place,R.id.widget_course_7_5_property);

        /*course2Items*/

        course2Items[0][0] = R.id.widget_top1;
        course2Items[0][1] = R.id.widget_top2;
        course2Items[0][2] = R.id.widget_top3;
        course2Items[0][3] = R.id.widget_top4;
        course2Items[0][4] = R.id.widget_top5;
        course2Items[0][5] = R.id.widget_top6;
        course2Items[0][6] = R.id.widget_top7;

        course2Items[1][0] = R.id.widget_course_1_34;
        course2Items[1][1] = R.id.widget_course_2_34;
        course2Items[1][2] = R.id.widget_course_3_34;
        course2Items[1][3] = R.id.widget_course_4_34;
        course2Items[1][4] = R.id.widget_course_5_34;
        course2Items[1][5] = R.id.widget_course_6_34;
        course2Items[1][6] = R.id.widget_course_7_34;


        course2Msgs[0][0] = new CourseId(R.id.widget_course_1_12_name,
                R.id.widget_course_1_12_place,R.id.widget_course_1_12_property);
        course2Msgs[0][1] = new CourseId(R.id.widget_course_2_12_name,
                R.id.widget_course_2_12_place,R.id.widget_course_2_12_property);
        course2Msgs[0][2] = new CourseId(R.id.widget_course_3_12_name,
                R.id.widget_course_3_12_place,R.id.widget_course_3_12_property);
        course2Msgs[0][3] = new CourseId(R.id.widget_course_4_12_name,
                R.id.widget_course_4_12_place,R.id.widget_course_4_12_property);
        course2Msgs[0][4] = new CourseId(R.id.widget_course_5_12_name,
                R.id.widget_course_5_12_place,R.id.widget_course_5_12_property);
        course2Msgs[0][5] = new CourseId(R.id.widget_course_6_12_name,
                R.id.widget_course_6_12_place,R.id.widget_course_6_12_property);
        course2Msgs[0][6] = new CourseId(R.id.widget_course_7_12_name,
                R.id.widget_course_7_12_place,R.id.widget_course_7_12_property);

        course2Msgs[1][0] = new CourseId(R.id.widget_course_1_34_name,
                R.id.widget_course_1_34_place,R.id.widget_course_1_34_property);
        course2Msgs[1][1] = new CourseId(R.id.widget_course_2_34_name,
                R.id.widget_course_2_34_place,R.id.widget_course_2_34_property);
        course2Msgs[1][2] = new CourseId(R.id.widget_course_3_34_name,
                R.id.widget_course_3_34_place,R.id.widget_course_3_34_property);
        course2Msgs[1][3] = new CourseId(R.id.widget_course_4_34_name,
                R.id.widget_course_4_34_place,R.id.widget_course_4_34_property);
        course2Msgs[1][4] = new CourseId(R.id.widget_course_5_34_name,
                R.id.widget_course_5_34_place,R.id.widget_course_5_34_property);
        course2Msgs[1][5] = new CourseId(R.id.widget_course_6_34_name,
                R.id.widget_course_6_34_place,R.id.widget_course_6_34_property);
        course2Msgs[1][6] = new CourseId(R.id.widget_course_7_34_name,
                R.id.widget_course_7_34_place,R.id.widget_course_7_34_property);

        weekday[1] = R.id.monday;
        weekday[2] = R.id.tuesday;
        weekday[3] = R.id.wednesday;
        weekday[4] = R.id.thursday;
        weekday[5] = R.id.friday;
        weekday[6] = R.id.saturday;
        weekday[7] = R.id.sunday;

        weekdayLine[1] = R.id.monday_line;
        weekdayLine[2] = R.id.tuesday_line;
        weekdayLine[3] = R.id.wednesday_line;
        weekdayLine[4] = R.id.thursday_line;
        weekdayLine[5] = R.id.friday_line;
        weekdayLine[6] = R.id.saturday_line;
        weekdayLine[7] = R.id.sunday_line;
    }
    private void init_view(){
        //Utills.clear();
        //显示课程信息
        for(int i=0;i<5;i++){
            for (int j=0;j<7;j++){
                if (courseList!=null && courseList.get(i).get(j)!=null){
                    CourseBean.Course course = courseList.get(i).get(j);
                    int courseItem;
                    CourseId courseMsg;
                    if (course.getTime().contains("01-02-03-04")){    //跨两大节的课
                        courseItem = course2Items[0][j];
                        courseMsg = course2Msgs[0][j];
                    }else if(course.getTime().contains("05-06-07-08")){
                        courseItem = course2Items[1][j];
                        courseMsg = course2Msgs[1][j];
                    }else {
                        courseItem = courseItems[i][j];
                        courseMsg = courseMsgs[i][j];
                    }
//                remoteViews.setImageViewBitmap(R.id.imageView1,
//                                rotateBitmap(context, srcbBitmap, degree);
                    remoteViews.setViewVisibility(courseItem,View.VISIBLE);

                    remoteViews.setInt(courseItem, "setBackgroundColor",Utills.randomColor(course.getCourseName()));

                    remoteViews.setTextViewText(courseMsg.getCourseNameId(),Utills.controlWords(course.getCourseName()));
                    remoteViews.setTextViewText(courseMsg.getCourseAddressId(),"@"+course.getAddress());
                }else{
                    remoteViews.setViewVisibility(course2Items[0][j],View.INVISIBLE);
                    remoteViews.setViewVisibility(course2Items[1][j],View.INVISIBLE);
                    remoteViews.setViewVisibility(courseItems[i][j],View.INVISIBLE);
                }
            }
        }
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.i(TAG, "onReceive : action = " + intent.getAction());
        if(intent.getAction().equals(APPWIDGET_ENABLED)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Account lastAccount = LitePal.where("isLast = ?","1").findLast(Account.class);
                    username = lastAccount.getUsername();
                    semester = lastAccount.getSemester();
                    week = lastAccount.getWeek();
                    time = lastAccount.getTime();
                    nowWeek = CalendarUtil.getNowWeek(time,Integer.parseInt(week));

                    couList = LitePal.where("semester = ? and username = ?",semester,username).findFirst(CourseList.class);
                    if(couList!=null) {
                        Log.i(TAG,"list is not empty! "+couList.getUsername()+"  "+couList.getSemester()+"  "+nowWeek);
                        courseBean = Utills.parseJSON(couList.getCourseResponseDatas().get(nowWeek),CourseBean.class);
                        courseList = courseBean.getData();
                        remoteViews = new RemoteViews(context
                                .getPackageName(), R.layout.table_widget_1);
                        initCourseView();
                        init_view();
                        showData(APPWIDGET_ENABLED,true);
                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                        appWidgetManager.updateAppWidget(new ComponentName(
                                context, TableWidgetProvider_1.class),remoteViews);
                    }else{
                        Log.i(TAG,"couList is NULL");
                    }

                }
            }).start();
        }else if(intent.getAction().equals(NEXT_WEEK)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Account lastAccount = LitePal.where("isLast = ?","1").findLast(Account.class);
                    username = lastAccount.getUsername();
                    semester = lastAccount.getSemester();
                    week = lastAccount.getWeek();
                    time = lastAccount.getTime();
                    nowWeek = CalendarUtil.getNowWeek(time,Integer.parseInt(week));

                    couList = LitePal.where("semester = ? and username = ?",semester,username).findFirst(CourseList.class);
                    if(couList!=null) {
                        Log.i(TAG,"list is not empty! "+couList.getUsername()+"  "+couList.getSemester()+"  "+nowWeek);
                        courseBean = Utills.parseJSON(couList.getCourseResponseDatas().get(nowWeek+next),CourseBean.class);
                        courseList = courseBean.getData();
                        remoteViews = new RemoteViews(context
                                .getPackageName(), R.layout.table_widget_1);
                        initCourseView();
                        init_view();
                        if(next==1) showData(NEXT_WEEK,false);
                        else showData(APPWIDGET_ENABLED,true);
                        if(next==1) remoteViews.setImageViewResource(R.id.widget_right,R.mipmap.left);
                        else remoteViews.setImageViewResource(R.id.widget_right,R.mipmap.right);
                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                        appWidgetManager.updateAppWidget(new ComponentName(
                                context, TableWidgetProvider_1.class),remoteViews);
                        if(next==1) next = 0;
                        else next = 1;
                    }else{
                        Log.i(TAG,"couList is NULL");
                    }

                }
            }).start();
        }


    }
    private void showData(String action, boolean showWeek){
        Date date = new Date(); // this object contains the current date value
        SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
        if(action.equals(APPWIDGET_ENABLED)){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
            remoteViews.setTextViewText(R.id.now_date,formatter.format(date));
            remoteViews.setTextViewText(R.id.now_week,"第"+nowWeek+"周");
            remoteViews.setViewVisibility(R.id.weekday,View.VISIBLE);
            remoteViews.setTextViewText(R.id.weekday,dateFm.format(date));
        }else if (action.equals(NEXT_WEEK)){
            remoteViews.setTextViewText(R.id.now_date,"下周");
            remoteViews.setTextViewText(R.id.now_week,"第"+(nowWeek+1)+"周");
            remoteViews.setViewVisibility(R.id.weekday,View.INVISIBLE);
        }
        for(int i=1;i<=7;i++){
            if(i==CalendarUtil.getOneWeekday(dateFm.format(date))&&showWeek) {
                remoteViews.setTextColor(weekday[i],0xff66CCCC);
                remoteViews.setViewVisibility(weekdayLine[i],View.VISIBLE);
            }
            else {
                remoteViews.setTextColor(weekday[i],0x8A000000);
                remoteViews.setViewVisibility(weekdayLine[i],View.INVISIBLE);
            }
        }
    }
    /**
     * 每次窗口小部件被点击更新都调用一次该方法
     * 这个方法里设置按钮监控
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.i(TAG, "onUpdate");
        final int counter = appWidgetIds.length;
        Log.i(TAG, "counter = " + counter);
        for (int i = 0; i < counter; i++) {
            int appWidgetId = appWidgetIds[i];
            onWidgetUpdate(context, appWidgetManager, appWidgetId);
        }


    }

    /**
     * 窗口小部件更新
     *
     * @param context
     * @param appWidgeManger
     * @param appWidgetId
     */
    private void onWidgetUpdate(Context context,
                                AppWidgetManager appWidgeManger, int appWidgetId) {

        Log.i(TAG, "appWidgetId = " + appWidgetId);
        remoteViews = new RemoteViews(context
                .getPackageName(), R.layout.table_widget_1);
        initCourseView();
        Account lastAccount = LitePal.where("isLast = ?","1").findLast(Account.class);
        String username1 = lastAccount.getUsername();
        String semester1 = lastAccount.getSemester();
        String week1 = lastAccount.getWeek();
        String time1 = lastAccount.getTime();
        int nowWeek1 = CalendarUtil.getNowWeek(time1,Integer.parseInt(week1));
        Log.i(TAG,"更新  username: "+username1+" ,semester : "+semester1+" ,nowWeek:  "+nowWeek1);
        if(!username1.equals(username)||!semester1.equals(semester)||nowWeek1!=nowWeek){
            username = username1;
            semester = semester1;
            week = week1;
            time = time1;
            nowWeek = nowWeek1;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    couList = LitePal.where("semester = ? and username = ?",semester,username).findFirst(CourseList.class);
                    if(couList!=null) {
                        Log.i(TAG,"list is not empty! "+couList.getUsername()+"  "+couList.getSemester()+"  "+nowWeek);
                        courseBean = Utills.parseJSON(couList.getCourseResponseDatas().get(nowWeek),CourseBean.class);
                        courseList = courseBean.getData();

                        init_view();
                        remoteViews.setImageViewResource(R.id.widget_right,R.mipmap.right);
                        showData(APPWIDGET_ENABLED,true);
                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                        appWidgetManager.updateAppWidget(new ComponentName(
                                context, TableWidgetProvider_1.class),remoteViews);
                    }else{
                        Log.i(TAG,"couList is NULL");
                    }

                }
            }).start();
        }else Log.i(TAG,"不需要更新");
        Intent NextClick = new Intent();
        NextClick.setAction(NEXT_WEEK);
        NextClick.setComponent(new ComponentName(context, TableWidgetProvider_1.class));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                NextClick, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_right, pendingIntent);

        appWidgeManger.updateAppWidget(appWidgetId, remoteViews);
        // "窗口小部件"点击事件发送的Intent广播
//        Intent intentClick = new Intent();
//        intentClick.setAction(CLICK_ACTION);
//        intentClick.setComponent(new ComponentName(context, TableWidgetProvider_1.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
//                intentClick, 0);
//        remoteViews.setOnClickPendingIntent(R.id.imageView1, pendingIntent);
//        appWidgeManger.updateAppWidget(appWidgetId, remoteViews);
    }

    private Bitmap rotateBitmap(Context context, Bitmap srcbBitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.setRotate(degree);
        Bitmap tmpBitmap = Bitmap.createBitmap(srcbBitmap, 0, 0,
                srcbBitmap.getWidth(), srcbBitmap.getHeight(), matrix, true);
        return tmpBitmap;
    }
}
