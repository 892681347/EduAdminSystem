package com.zyh.utills;

import android.app.Activity;
import android.app.Person;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zyh.beans.Course;
import com.zyh.beans.CourseBean;
import com.zyh.beans.LoginBean;
import com.zyh.fragment.R;
import com.zyh.fragment.TimetableFragment;
import com.zyh.fragment.timetableFragment.TimetableFragment1;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.zyh.utills.WeekUtil.getWeekDays;
import static org.litepal.LitePalApplication.getContext;

public class Utills {
    private static int[] colors = {0x64ff00ff,0xb44d3900,0xb4f400a1,0xb4daa520,0xb44169e1,0xb4b509ff};
    private static AlertDialog dialog = null;

    public static int randomColor(){
        return colors[new Random().nextInt(colors.length)];
    }

    public static void setCurrentSemester(String[] datas,String semester,Spinner spinner){
        for(int i=0;i<datas.length;i++){
            if (datas[i].equals(semester)){
                spinner.setSelection(i,true);
            }
        }
    }

    public static TimetableFragment getTimetableFragmeent(Fragment fragment){
        Fragment timetableFragment = null;
        List<Fragment>list=(List<Fragment>) fragment.getFragmentManager().getFragments();
        for(Fragment f:list){
            if(f!=null && f instanceof TimetableFragment){
                timetableFragment = f;
                break;
            }
        }
        return (TimetableFragment)timetableFragment;
    }

    public static void showTimetable(final Fragment timetableFragment, final Activity activity, final List<List<CourseBean.Course>> courseList,
                                     final TextView month, final TextView monthWord, final TextView[] weekDate,
                                     final Course[][] courseMsgs, final Course[][] course2Msgs, final CardView[][] courseItems
            , final CardView[][] course2Items, final String nowWeek,
                                     final String semester, final String originalSemester, final int index){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!((TimetableFragment) timetableFragment).isFinished[index]){}
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utills.showCourseMsgOnUi(activity,
                                ((TimetableFragment) timetableFragment).courseLists.get(index),
                                month,monthWord,weekDate,courseMsgs,course2Msgs,courseItems,
                                course2Items, ((TimetableFragment) timetableFragment).nowWeek,
                                ((TimetableFragment) timetableFragment).semester,
                                ((TimetableFragment) timetableFragment).originalSemester,index);
                        //课表点击事件
                        Utills.initCourseControl(activity,((TimetableFragment) timetableFragment).courseLists.get(index),courseItems,course2Items);
                    }
                });
            }
        }).start();
    }

    public static void initCourseControl(final Activity activity,final List<List<CourseBean.Course>> courseList,
                                         final CardView[][] courseItems,final CardView[][] course2Items){
        if(courseList==null) return;
        for(int i=0;i<5;i++){
            for (int j=0;j<7;j++){
                CourseBean.Course course = courseList.get(i).get(j);

                courseItems[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name =course.getCourseName();
                        String address = course.getAddress();
                        String teacher = course.getTeacher();
                        String time = course.getTime();
                        StringTokenizer st = new StringTokenizer(time, "[]");
                        String time_week="";
                        String time_clock="";
                        int i=0;
                        while(st.hasMoreElements()){
                            if (i==0){
                                time_week = st.nextToken();
                            }else if (i==1){
                                time_clock = st.nextToken();
                            }
                            i++;
                        }
                            //Toast.makeText(getContext(),name+","+address+","+teacher,Toast.LENGTH_SHORT).show();
                        showDialog(activity,name,time_week,time_clock,teacher,address);
                        //showDialog(activity,name,teacher,time,address);
                    }
                });
            }
        }
        for (int i=0;i<2;i++){
            for (int j = 0;j<7;j++){
                int h = 0;
                if (i==1) h = 2;
                CourseBean.Course course = courseList.get(h).get(j);

                course2Items[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name =course.getCourseName();
                        String address = course.getAddress();
                        String teacher = course.getTeacher();
                        String time = course.getTime();
                        StringTokenizer st = new StringTokenizer(time, "[]");
                        String time_week="";
                        String time_clock="";
                        int i=0;
                        while(st.hasMoreElements()){
                            if (i==0){
                                time_week = st.nextToken();
                            }else if (i==1){
                                time_clock = st.nextToken();
                            }
                            i++;
                        }
                        //Toast.makeText(getContext(),name+","+address+","+teacher,Toast.LENGTH_SHORT).show();
                        showDialog(activity,name,time_week,time_clock,teacher,address);
                        //showDialog(activity,name,teacher,time,address);
                    }
                });
            }
        }
    }

    public static void showDialog(final Activity activity,final String name,final String week,final String time,final String teacher,final String locate) {

        // 构建dialog显示的view布局
        View view_dialog = activity.getLayoutInflater().from(activity).inflate(R.layout.view_dialog, null);
        ((TextView)view_dialog.findViewById(R.id.name)).setText(name);
        ((TextView)view_dialog.findViewById(R.id.week)).setText(week);
        ((TextView)view_dialog.findViewById(R.id.time)).setText(time);
        ((TextView)view_dialog.findViewById(R.id.teacher)).setText(teacher);
        ((TextView)view_dialog.findViewById(R.id.locate)).setText(locate);

        if (dialog == null){
            // 创建AlertDialog对象
            dialog = new AlertDialog.Builder(activity)
                    .create();
            dialog.show();
            // 设置点击可取消
            dialog.setCancelable(true);

            // 获取Window对象
            Window window = dialog.getWindow();
            window.setBackgroundDrawableResource(android.R.color.transparent);
            //设置宽度
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.width = 850;// 调整该值可以设置对话框显示的宽度
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
            // 设置显示视图内容
            window.setContentView(view_dialog);
        }else {
            try {
                dialog.show();
                Window window = dialog.getWindow();
                window.setContentView(view_dialog);
            }catch (Exception e){
                e.printStackTrace();
                dialog = new AlertDialog.Builder(activity)
                        .create();
                dialog.show();
                // 设置点击可取消
                dialog.setCancelable(true);

                // 获取Window对象
                Window window = dialog.getWindow();
                window.setBackgroundDrawableResource(android.R.color.transparent);
                //设置宽度
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                lp.width = 850;// 调整该值可以设置对话框显示的宽度
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);
                // 设置显示视图内容
                window.setContentView(view_dialog);
            }

        }
    }
    public static void showGradeDialog(final Activity activity,final String name,final String pscj,final String pscjbl,final String qzcj,final String qzcjbl,final String qmcj,final String qmcjbl){
        // 构建dialog显示的view布局
        View view_dialog = activity.getLayoutInflater().from(activity).inflate(R.layout.view_grade_dialog, null);
        ((TextView)view_dialog.findViewById(R.id.name)).setText(name);
        ((TextView)view_dialog.findViewById(R.id.pscj)).setText(pscj);
        ((TextView)view_dialog.findViewById(R.id.pscj_bl)).setText(pscjbl);
        ((TextView)view_dialog.findViewById(R.id.qzcj)).setText(qzcj);
        ((TextView)view_dialog.findViewById(R.id.qzcj_bl)).setText(qzcjbl);
        ((TextView)view_dialog.findViewById(R.id.qmcj)).setText(qmcj);
        ((TextView)view_dialog.findViewById(R.id.qmcj_bl)).setText(qmcjbl);
//        if ((pscj==null||pscj.equals(""))&&(pscjbl==null||pscjbl.equals(""))){
//            ((RelativeLayout)view_dialog.findViewById(R.id.pscj_layout)).setVisibility(View.GONE);
//            ((RelativeLayout)view_dialog.findViewById(R.id.pscj_bl_layout)).setVisibility(View.GONE);
//        }
//        if ((qzcj==null||qzcj.equals(""))&&(qzcjbl==null||qzcjbl.equals(""))){
//            ((RelativeLayout)view_dialog.findViewById(R.id.qzcj_layout)).setVisibility(View.GONE);
//            ((RelativeLayout)view_dialog.findViewById(R.id.qzcj_bl_layout)).setVisibility(View.GONE);
//        }
//        if ((qmcj==null||qmcj.equals(""))&&(qmcjbl==null&&qmcjbl.equals(""))){
//            ((RelativeLayout)view_dialog.findViewById(R.id.qmcj_layout)).setVisibility(View.GONE);
//            ((RelativeLayout)view_dialog.findViewById(R.id.qmcj_bl_layout)).setVisibility(View.GONE);
//        }
        dialog = new AlertDialog.Builder(activity)
                .create();
        dialog.show();
        // 设置点击可取消
        dialog.setCancelable(true);

        // 获取Window对象
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        //设置宽度
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = 850;// 调整该值可以设置对话框显示的宽度
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        // 设置显示视图内容
        window.setContentView(view_dialog);

//        if (dialog == null){
//            // 创建AlertDialog对象
//            dialog = new AlertDialog.Builder(activity)
//                    .create();
//            dialog.show();
//            // 设置点击可取消
//            dialog.setCancelable(true);
//
//            // 获取Window对象
//            Window window = dialog.getWindow();
//            window.setBackgroundDrawableResource(android.R.color.transparent);
//            //设置宽度
//            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
//            lp.width = 850;// 调整该值可以设置对话框显示的宽度
//            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//            window.setAttributes(lp);
//            // 设置显示视图内容
//            window.setContentView(view_dialog);
//        }else {
//            try {
//                dialog.show();
//                Window window = dialog.getWindow();
//                window.setContentView(view_dialog);
//            }catch (Exception e){
//                e.printStackTrace();
//                dialog = new AlertDialog.Builder(activity)
//                        .create();
//                dialog.show();
//                // 设置点击可取消
//                dialog.setCancelable(true);
//
//                // 获取Window对象
//                Window window = dialog.getWindow();
//                window.setBackgroundDrawableResource(android.R.color.transparent);
//                //设置宽度
//                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
//                lp.width = 850;// 调整该值可以设置对话框显示的宽度
//                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//                window.setAttributes(lp);
//                // 设置显示视图内容
//                window.setContentView(view_dialog);
//            }
//
//        }
    }

    public static void showCourseMsgOnUi(final Activity activity, final List<List<CourseBean.Course>> courseList,
                                  final TextView month,final TextView monthWord,final TextView[] weekDate,
                                  final Course[][] courseMsgs,final Course[][] course2Msgs,final CardView[][] courseItems
            ,final CardView[][] course2Items,final String nowWeek,
                                         final String semester, final String originalSemester, final int index){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //显示课程信息
                for(int i=0;i<5;i++){
                    for (int j=0;j<7;j++){
                        if (courseList!=null && courseList.get(i).get(j)!=null){
                            CourseBean.Course course = courseList.get(i).get(j);
                            CardView courseItem;
                            Course courseMsg;
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
                            courseItem.setVisibility(View.VISIBLE);
                            courseItem.setCardBackgroundColor(Utills.randomColor());
                            courseMsg.getCourseName().setText(course.getCourseName());
                            courseMsg.getCourseAddress().setText("@"+course.getAddress());
                            if (course.getTime().contains("双周")){
                                courseMsg.getCourseProperty().setVisibility(View.VISIBLE);
                            }
                            if (course.getTime().contains("单周")){
                                courseMsg.getCourseProperty().setVisibility(View.VISIBLE);
                                courseMsg.getCourseProperty().setText("单周");
                            }

                        }
                    }
                }
                //显示顶部日期
                if (!nowWeek.equals("-1") && semester.equals(originalSemester)){
                    List<WeekDay> weekDays = null;
                    try {
                        weekDays = getWeekDays(Integer.parseInt(nowWeek), 20);
                        WeekDay weekDay = weekDays.get(index-1);
                        String weekMonStr = weekDay.getWeekMonStr();
                        Log.d("weekMonStr",weekMonStr);
                        String[] yearMonDay = weekMonStr.split("-");
                        String yearMonStr = yearMonDay[0]+"-"+yearMonDay[1];
                        int maxDay = Utills.getMonthday(yearMonStr);
                        //月份
                        String monthStr = Integer.parseInt(yearMonDay[1])+"";
                        month.setText(CalendarUtil.getMonthOfSpecifiedDayBefore(weekMonStr));
                        monthWord.setVisibility(View.VISIBLE);
                        //日期
                        int day =  Integer.parseInt(yearMonDay[2]);
                        for(int i=0;i<weekDate.length;i++){
                            if (i==0){//星期天，第一天
                                weekDate[i].setText(CalendarUtil.getDayOfSpecifiedDayBefore(weekMonStr));
                                continue;
                            }
                            if (day>maxDay) day=1;
                            weekDate[i].setText(day+"");
                            day++;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    month.setText("");
                    monthWord.setVisibility(View.INVISIBLE);
                    for(int i=0;i<weekDate.length;i++){
                        weekDate[i].setText("");
                    }
                }
            }
        });

    }

    public static int getMonthday(String strDate){
        try{
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
            Date date = format.parse(strDate);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            int days1 = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            System.out.println("天数为=" + days1);
            return days1;
        }catch (ParseException e){
            e.printStackTrace();
        }
        return 0;
    }

    public static void showIsNowWeek(Spinner spinner,String originalSemester,TextView isNowWeek,
                                     String selectedWeek,String nowWeek){
        if (((String)spinner.getSelectedItem()).equals(originalSemester) && selectedWeek.equals(nowWeek)){
            isNowWeek.setText("本周");
        }else {
            isNowWeek.setText("非本周");
        }
    }

    public static <T>  T parseJSON(String jsonData,Class<T> classes){
        T t = null;
        try{
            Gson gson = new Gson();
            t = gson.fromJson(jsonData,classes);
        }catch (Exception e){
            Log.d("parseJSONError","parseJSONError");
            e.printStackTrace();
        }
        return t;
    }

    public static void initCourseView(View view,TextView[] weekDate,CardView[][] courseItems,
                                      Course[][] courseMsgs,CardView[][] course2Items,Course[][] course2Msgs){
        Log.d("initCourseView","working");
        /*weekDate*/
        weekDate[0] = (TextView)view.findViewById(R.id.table_Sundaydate);
        weekDate[1] = (TextView)view.findViewById(R.id.table_Mondaydate);
        weekDate[2] = (TextView)view.findViewById(R.id.table_Tuesdaydate);
        weekDate[3] = (TextView)view.findViewById(R.id.table_Wednesdaydate);
        weekDate[4] = (TextView)view.findViewById(R.id.table_Thursdaydate);
        weekDate[5] = (TextView)view.findViewById(R.id.table_Fridaydate);
        weekDate[6] = (TextView)view.findViewById(R.id.table_Saturdaydate);

        /*courseItems*/

        courseItems[0][0] = (CardView) view.findViewById(R.id.course_1_1);
        courseItems[0][1] = (CardView) view.findViewById(R.id.course_2_1);
        courseItems[0][2] = (CardView) view.findViewById(R.id.course_3_1);
        courseItems[0][3] = (CardView) view.findViewById(R.id.course_4_1);
        courseItems[0][4] = (CardView) view.findViewById(R.id.course_5_1);
        courseItems[0][5] = (CardView) view.findViewById(R.id.course_6_1);
        courseItems[0][6] = (CardView) view.findViewById(R.id.course_7_1);

        courseItems[1][0] = (CardView) view.findViewById(R.id.course_1_2);
        courseItems[1][1] = (CardView) view.findViewById(R.id.course_2_2);
        courseItems[1][2] = (CardView) view.findViewById(R.id.course_3_2);
        courseItems[1][3] = (CardView) view.findViewById(R.id.course_4_2);
        courseItems[1][4] = (CardView) view.findViewById(R.id.course_5_2);
        courseItems[1][5] = (CardView) view.findViewById(R.id.course_6_2);
        courseItems[1][6] = (CardView) view.findViewById(R.id.course_7_2);

        courseItems[2][0] = (CardView) view.findViewById(R.id.course_1_3);
        courseItems[2][1] = (CardView) view.findViewById(R.id.course_2_3);
        courseItems[2][2] = (CardView) view.findViewById(R.id.course_3_3);
        courseItems[2][3] = (CardView) view.findViewById(R.id.course_4_3);
        courseItems[2][4] = (CardView) view.findViewById(R.id.course_5_3);
        courseItems[2][5] = (CardView) view.findViewById(R.id.course_6_3);
        courseItems[2][6] = (CardView) view.findViewById(R.id.course_7_3);

        courseItems[3][0] = (CardView) view.findViewById(R.id.course_1_4);
        courseItems[3][1] = (CardView) view.findViewById(R.id.course_2_4);
        courseItems[3][2] = (CardView) view.findViewById(R.id.course_3_4);
        courseItems[3][3] = (CardView) view.findViewById(R.id.course_4_4);
        courseItems[3][4] = (CardView) view.findViewById(R.id.course_5_4);
        courseItems[3][5] = (CardView) view.findViewById(R.id.course_6_4);
        courseItems[3][6] = (CardView) view.findViewById(R.id.course_7_4);

        courseItems[4][0] = (CardView) view.findViewById(R.id.course_1_5);
        courseItems[4][1] = (CardView) view.findViewById(R.id.course_2_5);
        courseItems[4][2] = (CardView) view.findViewById(R.id.course_3_5);
        courseItems[4][3] = (CardView) view.findViewById(R.id.course_4_5);
        courseItems[4][4] = (CardView) view.findViewById(R.id.course_5_5);
        courseItems[4][5] = (CardView) view.findViewById(R.id.course_6_5);
        courseItems[4][6] = (CardView) view.findViewById(R.id.course_7_5);

        /*courseMsgs*/

        courseMsgs[0][0] = new Course((TextView)view.findViewById(R.id.course_1_1_name),
                (TextView) view.findViewById(R.id.course_1_1_place),(TextView)view.findViewById(R.id.course_1_1_property));
        courseMsgs[0][1] = new Course((TextView)view.findViewById(R.id.course_2_1_name),
                (TextView) view.findViewById(R.id.course_2_1_place),(TextView)view.findViewById(R.id.course_2_1_property));
        courseMsgs[0][2] = new Course((TextView)view.findViewById(R.id.course_3_1_name),
                (TextView) view.findViewById(R.id.course_3_1_place),(TextView)view.findViewById(R.id.course_3_1_property));
        courseMsgs[0][3] = new Course((TextView)view.findViewById(R.id.course_4_1_name),
                (TextView) view.findViewById(R.id.course_4_1_place),(TextView)view.findViewById(R.id.course_4_1_property));
        courseMsgs[0][4] = new Course((TextView)view.findViewById(R.id.course_5_1_name),
                (TextView) view.findViewById(R.id.course_5_1_place),(TextView)view.findViewById(R.id.course_5_1_property));
        courseMsgs[0][5] = new Course((TextView)view.findViewById(R.id.course_6_1_name),
                (TextView) view.findViewById(R.id.course_6_1_place),(TextView)view.findViewById(R.id.course_6_1_property));
        courseMsgs[0][6] = new Course((TextView)view.findViewById(R.id.course_7_1_name),
                (TextView) view.findViewById(R.id.course_7_1_place),(TextView)view.findViewById(R.id.course_7_1_property));

        courseMsgs[1][0] = new Course((TextView)view.findViewById(R.id.course_1_2_name),
                (TextView) view.findViewById(R.id.course_1_2_place),(TextView)view.findViewById(R.id.course_1_2_property));
        courseMsgs[1][1] = new Course((TextView)view.findViewById(R.id.course_2_2_name),
                (TextView) view.findViewById(R.id.course_2_2_place),(TextView)view.findViewById(R.id.course_2_2_property));
        courseMsgs[1][2] = new Course((TextView)view.findViewById(R.id.course_3_2_name),
                (TextView) view.findViewById(R.id.course_3_2_place),(TextView)view.findViewById(R.id.course_3_2_property));
        courseMsgs[1][3] = new Course((TextView)view.findViewById(R.id.course_4_2_name),
                (TextView) view.findViewById(R.id.course_4_2_place),(TextView)view.findViewById(R.id.course_4_2_property));
        courseMsgs[1][4] = new Course((TextView)view.findViewById(R.id.course_5_2_name),
                (TextView) view.findViewById(R.id.course_5_2_place),(TextView)view.findViewById(R.id.course_5_2_property));
        courseMsgs[1][5] = new Course((TextView)view.findViewById(R.id.course_6_2_name),
                (TextView) view.findViewById(R.id.course_6_2_place),(TextView)view.findViewById(R.id.course_6_2_property));
        courseMsgs[1][6] = new Course((TextView)view.findViewById(R.id.course_7_2_name),
                (TextView) view.findViewById(R.id.course_7_2_place),(TextView)view.findViewById(R.id.course_7_2_property));

        courseMsgs[2][0] = new Course((TextView)view.findViewById(R.id.course_1_3_name),
                (TextView) view.findViewById(R.id.course_1_3_place),(TextView)view.findViewById(R.id.course_1_3_property));
        courseMsgs[2][1] = new Course((TextView)view.findViewById(R.id.course_2_3_name),
                (TextView) view.findViewById(R.id.course_2_3_place),(TextView)view.findViewById(R.id.course_2_3_property));
        courseMsgs[2][2] = new Course((TextView)view.findViewById(R.id.course_3_3_name),
                (TextView) view.findViewById(R.id.course_3_3_place),(TextView)view.findViewById(R.id.course_3_3_property));
        courseMsgs[2][3] = new Course((TextView)view.findViewById(R.id.course_4_3_name),
                (TextView) view.findViewById(R.id.course_4_3_place),(TextView)view.findViewById(R.id.course_4_3_property));
        courseMsgs[2][4] = new Course((TextView)view.findViewById(R.id.course_5_3_name),
                (TextView) view.findViewById(R.id.course_5_3_place),(TextView)view.findViewById(R.id.course_5_3_property));
        courseMsgs[2][5] = new Course((TextView)view.findViewById(R.id.course_6_3_name),
                (TextView) view.findViewById(R.id.course_6_3_place),(TextView)view.findViewById(R.id.course_6_3_property));
        courseMsgs[2][6] = new Course((TextView)view.findViewById(R.id.course_7_3_name),
                (TextView) view.findViewById(R.id.course_7_3_place),(TextView)view.findViewById(R.id.course_7_3_property));

        courseMsgs[3][0] = new Course((TextView)view.findViewById(R.id.course_1_4_name),
                (TextView) view.findViewById(R.id.course_1_4_place),(TextView)view.findViewById(R.id.course_1_4_property));
        courseMsgs[3][1] = new Course((TextView)view.findViewById(R.id.course_2_4_name),
                (TextView) view.findViewById(R.id.course_2_4_place),(TextView)view.findViewById(R.id.course_2_4_property));
        courseMsgs[3][2] = new Course((TextView)view.findViewById(R.id.course_3_4_name),
                (TextView) view.findViewById(R.id.course_3_4_place),(TextView)view.findViewById(R.id.course_3_4_property));
        courseMsgs[3][3] = new Course((TextView)view.findViewById(R.id.course_4_4_name),
                (TextView) view.findViewById(R.id.course_4_4_place),(TextView)view.findViewById(R.id.course_4_4_property));
        courseMsgs[3][4] = new Course((TextView)view.findViewById(R.id.course_5_4_name),
                (TextView) view.findViewById(R.id.course_5_4_place),(TextView)view.findViewById(R.id.course_5_4_property));
        courseMsgs[3][5] = new Course((TextView)view.findViewById(R.id.course_6_4_name),
                (TextView) view.findViewById(R.id.course_6_4_place),(TextView)view.findViewById(R.id.course_6_4_property));
        courseMsgs[3][6] = new Course((TextView)view.findViewById(R.id.course_7_4_name),
                (TextView) view.findViewById(R.id.course_7_4_place),(TextView)view.findViewById(R.id.course_7_4_property));

        courseMsgs[4][0] = new Course((TextView)view.findViewById(R.id.course_1_5_name),
                (TextView) view.findViewById(R.id.course_1_5_place),(TextView)view.findViewById(R.id.course_1_5_property));
        courseMsgs[4][1] = new Course((TextView)view.findViewById(R.id.course_2_5_name),
                (TextView) view.findViewById(R.id.course_2_5_place),(TextView)view.findViewById(R.id.course_2_5_property));
        courseMsgs[4][2] = new Course((TextView)view.findViewById(R.id.course_3_5_name),
                (TextView) view.findViewById(R.id.course_3_5_place),(TextView)view.findViewById(R.id.course_3_5_property));
        courseMsgs[4][3] = new Course((TextView)view.findViewById(R.id.course_4_5_name),
                (TextView) view.findViewById(R.id.course_4_5_place),(TextView)view.findViewById(R.id.course_4_5_property));
        courseMsgs[4][4] = new Course((TextView)view.findViewById(R.id.course_5_5_name),
                (TextView) view.findViewById(R.id.course_5_5_place),(TextView)view.findViewById(R.id.course_5_5_property));
        courseMsgs[4][5] = new Course((TextView)view.findViewById(R.id.course_6_5_name),
                (TextView) view.findViewById(R.id.course_6_5_place),(TextView)view.findViewById(R.id.course_6_5_property));
        courseMsgs[4][6] = new Course((TextView)view.findViewById(R.id.course_7_5_name),
                (TextView) view.findViewById(R.id.course_7_5_place),(TextView)view.findViewById(R.id.course_7_5_property));

        /*course2Items*/

        course2Items[0][0] = (CardView)view.findViewById(R.id.top1);
        course2Items[0][1] = (CardView)view.findViewById(R.id.top2);
        course2Items[0][2] = (CardView)view.findViewById(R.id.top3);
        course2Items[0][3] = (CardView)view.findViewById(R.id.top4);
        course2Items[0][4] = (CardView)view.findViewById(R.id.top5);
        course2Items[0][5] = (CardView)view.findViewById(R.id.top6);
        course2Items[0][6] = (CardView)view.findViewById(R.id.top7);

        course2Items[1][0] = (CardView)view.findViewById(R.id.course_1_34);
        course2Items[1][1] = (CardView)view.findViewById(R.id.course_2_34);
        course2Items[1][2] = (CardView)view.findViewById(R.id.course_3_34);
        course2Items[1][3] = (CardView)view.findViewById(R.id.course_4_34);
        course2Items[1][4] = (CardView)view.findViewById(R.id.course_5_34);
        course2Items[1][5] = (CardView)view.findViewById(R.id.course_6_34);
        course2Items[1][6] = (CardView)view.findViewById(R.id.course_7_34);


        course2Msgs[0][0] = new Course((TextView)view.findViewById(R.id.course_1_12_name),
                (TextView)view.findViewById(R.id.course_1_12_place),(TextView)view.findViewById(R.id.course_1_12_property));
        course2Msgs[0][1] = new Course((TextView)view.findViewById(R.id.course_2_12_name),
                (TextView)view.findViewById(R.id.course_2_12_place),(TextView)view.findViewById(R.id.course_2_12_property));
        course2Msgs[0][2] = new Course((TextView)view.findViewById(R.id.course_3_12_name),
                (TextView)view.findViewById(R.id.course_3_12_place),(TextView)view.findViewById(R.id.course_3_12_property));
        course2Msgs[0][3] = new Course((TextView)view.findViewById(R.id.course_4_12_name),
                (TextView)view.findViewById(R.id.course_4_12_place),(TextView)view.findViewById(R.id.course_4_12_property));
        course2Msgs[0][4] = new Course((TextView)view.findViewById(R.id.course_5_12_name),
                (TextView)view.findViewById(R.id.course_5_12_place),(TextView)view.findViewById(R.id.course_5_12_property));
        course2Msgs[0][5] = new Course((TextView)view.findViewById(R.id.course_6_12_name),
                (TextView)view.findViewById(R.id.course_6_12_place),(TextView)view.findViewById(R.id.course_6_12_property));
        course2Msgs[0][6] = new Course((TextView)view.findViewById(R.id.course_7_12_name),
                (TextView)view.findViewById(R.id.course_7_12_place),(TextView)view.findViewById(R.id.course_7_12_property));

        course2Msgs[1][0] = new Course((TextView)view.findViewById(R.id.course_1_34_name),
                (TextView)view.findViewById(R.id.course_1_34_place),(TextView)view.findViewById(R.id.course_1_34_property));
        course2Msgs[1][1] = new Course((TextView)view.findViewById(R.id.course_2_34_name),
                (TextView)view.findViewById(R.id.course_2_34_place),(TextView)view.findViewById(R.id.course_2_34_property));
        course2Msgs[1][2] = new Course((TextView)view.findViewById(R.id.course_3_34_name),
                (TextView)view.findViewById(R.id.course_3_34_place),(TextView)view.findViewById(R.id.course_3_34_property));
        course2Msgs[1][3] = new Course((TextView)view.findViewById(R.id.course_4_34_name),
                (TextView)view.findViewById(R.id.course_4_34_place),(TextView)view.findViewById(R.id.course_4_34_property));
        course2Msgs[1][4] = new Course((TextView)view.findViewById(R.id.course_5_34_name),
                (TextView)view.findViewById(R.id.course_5_34_place),(TextView)view.findViewById(R.id.course_5_34_property));
        course2Msgs[1][5] = new Course((TextView)view.findViewById(R.id.course_6_34_name),
                (TextView)view.findViewById(R.id.course_6_34_place),(TextView)view.findViewById(R.id.course_6_34_property));
        course2Msgs[1][6] = new Course((TextView)view.findViewById(R.id.course_7_34_name),
                (TextView)view.findViewById(R.id.course_7_34_place),(TextView)view.findViewById(R.id.course_7_34_property));
    }


    public static void show(final FragmentActivity activity, final Fragment timetableFragment, final TextView textView, final int index){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!((TimetableFragment) timetableFragment).isFinished[index]){}
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(((TimetableFragment) timetableFragment).timetableList.get(index));
                    }
                });
            }
        }).start();
    }

    public static void postTimetable(final LoginBean loginBean, final Fragment timetableFragment, final String semester, final String week) {
        final String cookie = loginBean.getData().getCookie();
        final String token = loginBean.getData().getToken();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("cookie",cookie)
                            .add("xueqi",semester)
                            .add("zc",week)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://42.193.177.76:8081/getCourse")
                            .post(requestBody)
                            .addHeader("token",token)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    CourseBean courseBean = Utills.parseJSON(responseData,CourseBean.class);
                    int nowWeek = Integer.parseInt(week);
                    ((TimetableFragment)timetableFragment).courseLists.set(nowWeek,courseBean.getData());
                    ((TimetableFragment) timetableFragment).isFinished[nowWeek] = true;
                }catch (Exception e) {
                    Log.d("okHttpError","okHttpError");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 验证手机号码是否合法
     */
    public static boolean validatePhoneNumber(String mobiles) {
        String telRegex = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(147,145))\\d{8}$";
        return !TextUtils.isEmpty(mobiles) && mobiles.matches(telRegex);
    }
}
