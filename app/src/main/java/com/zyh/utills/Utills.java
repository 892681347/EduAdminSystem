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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zyh.activities.MainActivity;
import com.zyh.activities.NoteActivity;
import com.zyh.beans.Course;
import com.zyh.beans.CourseBean;
import com.zyh.beans.CourseList;
import com.zyh.beans.LoginBean;
import com.zyh.beans.Note;
import com.zyh.fragment.R;
import com.zyh.fragment.TimetableFragment;
import com.zyh.fragment.TimetableFragmentItem;

import org.litepal.LitePal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;
import java.util.StringTokenizer;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;
import static com.zyh.utills.WeekUtil.getWeekDays;
import static org.litepal.LitePalApplication.getContext;

public class Utills {
    private static String TAG = "Utills";
    private static Stack<Thread> threadStack = new Stack();
    static Map<String, Integer> map = new HashMap();
    static List<Integer> list = new ArrayList<>();
    static List<Integer> full = new ArrayList<>();
    private static int[] colors = {0xFFFF2E63,0xFF66BFBF,0xFFFF9999,0XFFE889E5,0xFF4791B1,0xFF00BBF0
            ,0xFFE6CFE5,0xFF74B49B,0xFFAC73FF,0xFFA1D9FF,0xFFFF5126,0xFFFACF5A,0XFFF2B581,0xFF79A3D9};

    private static AlertDialog dialog = null;
    public static void clear(){
        for(int i=0;i<colors.length;i++){
            full.add(colors[i]);
        }
        list.clear();
        map.clear();
    }

    public static int randomColor(String name){
        if(map.get(name)!=null) return map.get(name);
        if(list.containsAll(full)) list.clear();
        int color = colors[new Random().nextInt(colors.length)];
        int index = 0;
        for(int i=0;i<colors.length;i++){
            if(colors[i]==color){
                index = i;
                break;
            }
        }
        while(list.contains(color)){
            index = (index+1)%colors.length;
            color = colors[index];
            Log.d(TAG, "randomColor: SSS");
        }
        list.add(color);
        map.put(name, color);
        return color;
    }

    public static String controlWords(String words){
        if(words.length()<=6) return words;
        else return words.substring(0,5)+"...";
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
        List<Fragment>list=(List<Fragment>) fragment.getActivity().getSupportFragmentManager().getFragments();

        for(Fragment f:list){
            if(f!=null && f instanceof TimetableFragment){
                timetableFragment = f;
                break;
            }
        }
        return (TimetableFragment)timetableFragment;
    }

    /**
     * 由每个TimetableFragment调用
     * @param timetableFragment
     * @param activity
     * @param courseList 网络返回的课程数据
     * @param month 具体月份
     * @param monthWord “月”字
     * @param weekDate 每天的日期（年月日的日）
     * @param courseMsgs 常规课程数据
     * @param course2Msgs 两大节课程数据
     * @param courseItems 常规课程cardView
     * @param course2Items 两大节课程cardView
     * @param nowWeek 当前学习周
     * @param semester 当前学期
     * @param originalSemester
     * @param index 学习周，决定取courseList数组中的哪一组
     */
    public static void showTimetable(final Fragment timetableFragment, final Activity activity, final List<List<CourseBean.Course>> courseList,
                                     final TextView month, final TextView monthWord, final TextView[] weekDate,
                                     final Course[][] courseMsgs, final Course[][] course2Msgs, final CardView[][] courseItems
            , final CardView[][] course2Items, final String nowWeek,
                                     final String semester, final String originalSemester,
                                     final LinearLayout[] weekLinearLayout, final LinearLayout[][] showAddNotes,
                                     final LinearLayout[][] addNotes, final TimetableFragmentItem timetableFragmentItem,
                                     final CardView[][] notes, final TextView[][] noteNames, final int index){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!((TimetableFragment) timetableFragment).isFinished[index]){
                    //Log.i(TAG,"wait for courseList_"+index);
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //显示课程信息
                        Utills.showCourseMsgOnUi(activity,
                                ((TimetableFragment) timetableFragment).courseLists.get(index),
                                month,monthWord,weekDate,courseMsgs,course2Msgs,courseItems,
                                course2Items, ((TimetableFragment) timetableFragment).nowWeek,
                                ((TimetableFragment) timetableFragment).semester,
                                ((TimetableFragment) timetableFragment).originalSemester,index);
                        //显示备注
                        Utills.showNote(activity,notes,noteNames,index+"",((TimetableFragment) timetableFragment).semester,courseList);
                        //添加备注
                        Utills.addNote(activity,courseItems,notes,showAddNotes,addNotes,timetableFragmentItem, index+"",
                                ((TimetableFragment) timetableFragment).semester);
                    }
                });
            }
        }).start();
        //本学期本周标注星期几
        if(TimetableFragment.isThisSemester && TimetableFragment.thisWeek==index){
            Date date = new Date(); // this object contains the current date value
            SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
            weekLinearLayout[CalendarUtil.getOneWeekday(dateFm.format(date))].setBackgroundResource(R.drawable.shape_corner_table);
        }
        //课表点击事件
        Utills.initCourseControl(timetableFragmentItem, activity,((TimetableFragment) timetableFragment).courseLists.get(index),courseItems,course2Items);


    }
    public static void addNote(Activity activity, CardView[][] courseItems,CardView[][] notes, LinearLayout[][] showAddNotes, LinearLayout[][] addNotes,
                                TimetableFragmentItem timetableFragmentItem, String nowWeek, String semester){
        for(int i=0;i<5;i++){ //课程第几节
            for(int j=0;j<7;j++){ //星期几
                final int time = i+1;
                final int dayInWeek = j+1;
                LinearLayout addNote = addNotes[i][j];
                LinearLayout showAddNote = showAddNotes[i][j];
                if(courseItems[i][j].getVisibility()==View.VISIBLE||notes[i][j].getVisibility()==View.VISIBLE){
                    showAddNote.setVisibility(View.INVISIBLE);
                }else {
                    showAddNote.setVisibility(View.VISIBLE);
                }
                addNote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(TAG,"addNote");
                        addNote.setVisibility(View.INVISIBLE);
                        showAddNote.setVisibility(View.VISIBLE);
                        NoteActivity.actionStart(activity,((MainActivity)activity).username,semester,nowWeek,dayInWeek,time,true);
                        Log.i(TAG,"return back");
                    }
                });
                showAddNote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(TAG,"showAddNote");
                        if(timetableFragmentItem.nowAddNote!=null) timetableFragmentItem.nowAddNote.setVisibility(View.INVISIBLE);
                        if(timetableFragmentItem.nowShowAddNote!=null) timetableFragmentItem.nowShowAddNote.setVisibility(View.VISIBLE);
                        showAddNote.setVisibility(View.INVISIBLE);
                        addNote.setVisibility(View.VISIBLE);
                        timetableFragmentItem.nowAddNote = addNote;
                        timetableFragmentItem.nowShowAddNote = showAddNote;
                    }
                });
            }
        }
    }
    //显示备注
    public static void showNote(Activity activity,CardView[][] notes, TextView[][] noteNames, String nowWeek,
                                 String semester, List<List<CourseBean.Course>> courseList){
        for(int i=0;i<5;i++){
            for (int j=0;j<7;j++){
                if (courseList==null||courseList.get(i).get(j)==null){
                    Log.i(TAG,"username:  "+((MainActivity)activity).username);
                    Note note = LitePal.where("username = ? and semester = ? and week = ? and " +
                                    "dayInWeek = ? and time = ?",((MainActivity)activity).username,semester,nowWeek,
                            j+1+"",i+1+"").findFirst(Note.class);
                    if(note!=null){
                        final int time = i+1;
                        final int dayInWeek = j+1;
                        //默认颜色
                        CardView noteView = notes[i][j];
                        TextView noteName = noteNames[i][j];
                        noteView.setVisibility(View.VISIBLE);
                        noteView.setCardBackgroundColor(Utills.randomColor(note.getName()));
                        noteName.setText(note.getName());

                        noteView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                NoteActivity.actionStart(activity,((MainActivity)activity).username,semester,nowWeek,dayInWeek,time,false);
                                Log.i(TAG,"return back");
                            }
                        });
                    }
                }
            }
        }
    }
    public static void initCourseControl(final TimetableFragmentItem timetableFragmentItem,final Activity activity,final List<List<CourseBean.Course>> courseList,
                                         final CardView[][] courseItems,final CardView[][] course2Items){
        if(courseList==null) return;
        for(int i=0;i<5;i++){
            for (int j=0;j<7;j++){
                CourseBean.Course course = courseList.get(i).get(j);
                if(course==null) continue;
                courseItems[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(timetableFragmentItem.nowAddNote!=null) timetableFragmentItem.nowAddNote.setVisibility(View.INVISIBLE);
                        if(timetableFragmentItem.nowShowAddNote!=null) timetableFragmentItem.nowShowAddNote.setVisibility(View.VISIBLE);

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
                if(course==null) continue;
                course2Items[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(timetableFragmentItem.nowAddNote!=null) timetableFragmentItem.nowAddNote.setVisibility(View.INVISIBLE);
                        if(timetableFragmentItem.nowShowAddNote!=null) timetableFragmentItem.nowShowAddNote.setVisibility(View.VISIBLE);

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
                CardView courseItem;
                Course courseMsg;
                for(int i=0;i<5;i++){
                    if(courseList==null) continue;
                    for (int j=0;j<7;j++){
                        if (courseList.get(i).get(j)!=null){
                            CourseBean.Course course = courseList.get(i).get(j);
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
                            courseItem.setCardBackgroundColor(Utills.randomColor(course.getCourseName()));
                            courseMsg.getCourseName().setText(course.getCourseName());
                            courseMsg.getCourseAddress().setText("@"+course.getAddress());
                            if (course.getTime().contains("双周")){
                                courseMsg.getCourseProperty().setVisibility(View.VISIBLE);
                            }
                            if (course.getTime().contains("单周")){
                                courseMsg.getCourseProperty().setVisibility(View.VISIBLE);
                                courseMsg.getCourseProperty().setText("单周");
                            }
                        }else{
                            /*
                            Note note = LitePal.where("semester = ? and week = ? and " +
                                    "dayInWeek = ? and time = ?",semester,nowWeek,
                                    j+1+"",i+1+"").findFirst(Note.class);
                            if(note!=null){
                                courseItem = courseItems[i][j];
                                courseMsg = courseMsgs[i][j];
                                courseItem.setVisibility(View.VISIBLE);
                                courseItem.setCardBackgroundColor(courseItem.getResources().getColor(R.color.add_note));
                                courseMsg.getCourseName().setText(note.getName());
                                courseMsg.getCourseAddress().setText("@"+note.getPlace());
                            }
                            */
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

    public static void showIsNowWeek(String selectSemester,String originalSemester,TextView isNowWeek,
                                     String selectedWeek,String nowWeek){
        if (selectSemester.equals(originalSemester)) TimetableFragment.isThisSemester = true;
        else TimetableFragment.isThisSemester = false;
        if (selectSemester.equals(originalSemester) && selectedWeek.equals(nowWeek)){
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

    public static void initCourseView(View view, TextView[] weekDate, CardView[][] courseItems,
                                      Course[][] courseMsgs, CardView[][] course2Items,
                                      Course[][] course2Msgs, LinearLayout[] weekLinearLayout,
                                      LinearLayout[][] showAddNotes, LinearLayout[][] addNotes,
                                      CardView[][] notes, TextView[][] noteNames){

        weekLinearLayout[1] = view.findViewById(R.id.monday_linear);
        weekLinearLayout[2] = view.findViewById(R.id.tuesday_linear);
        weekLinearLayout[3] = view.findViewById(R.id.wednesday_linear);
        weekLinearLayout[4] = view.findViewById(R.id.thursday_linear);
        weekLinearLayout[5] = view.findViewById(R.id.friday_linear);
        weekLinearLayout[6] = view.findViewById(R.id.saturday_linear);
        weekLinearLayout[7] = view.findViewById(R.id.sunday_linear);
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

        /*showAddNotes*/

        showAddNotes[0][0] = view.findViewById(R.id.show_add_note_1_1);
        showAddNotes[0][1] = view.findViewById(R.id.show_add_note_2_1);
        showAddNotes[0][2] = view.findViewById(R.id.show_add_note_3_1);
        showAddNotes[0][3] = view.findViewById(R.id.show_add_note_4_1);
        showAddNotes[0][4] = view.findViewById(R.id.show_add_note_5_1);
        showAddNotes[0][5] = view.findViewById(R.id.show_add_note_6_1);
        showAddNotes[0][6] = view.findViewById(R.id.show_add_note_7_1);

        showAddNotes[1][0] = view.findViewById(R.id.show_add_note_1_2);
        showAddNotes[1][1] = view.findViewById(R.id.show_add_note_2_2);
        showAddNotes[1][2] = view.findViewById(R.id.show_add_note_3_2);
        showAddNotes[1][3] = view.findViewById(R.id.show_add_note_4_2);
        showAddNotes[1][4] = view.findViewById(R.id.show_add_note_5_2);
        showAddNotes[1][5] = view.findViewById(R.id.show_add_note_6_2);
        showAddNotes[1][6] = view.findViewById(R.id.show_add_note_7_2);

        showAddNotes[2][0] = view.findViewById(R.id.show_add_note_1_3);
        showAddNotes[2][1] = view.findViewById(R.id.show_add_note_2_3);
        showAddNotes[2][2] = view.findViewById(R.id.show_add_note_3_3);
        showAddNotes[2][3] = view.findViewById(R.id.show_add_note_4_3);
        showAddNotes[2][4] = view.findViewById(R.id.show_add_note_5_3);
        showAddNotes[2][5] = view.findViewById(R.id.show_add_note_6_3);
        showAddNotes[2][6] = view.findViewById(R.id.show_add_note_7_3);

        showAddNotes[3][0] = view.findViewById(R.id.show_add_note_1_4);
        showAddNotes[3][1] = view.findViewById(R.id.show_add_note_2_4);
        showAddNotes[3][2] = view.findViewById(R.id.show_add_note_3_4);
        showAddNotes[3][3] = view.findViewById(R.id.show_add_note_4_4);
        showAddNotes[3][4] = view.findViewById(R.id.show_add_note_5_4);
        showAddNotes[3][5] = view.findViewById(R.id.show_add_note_6_4);
        showAddNotes[3][6] = view.findViewById(R.id.show_add_note_7_4);

        showAddNotes[4][0] = view.findViewById(R.id.show_add_note_1_5);
        showAddNotes[4][1] = view.findViewById(R.id.show_add_note_2_5);
        showAddNotes[4][2] = view.findViewById(R.id.show_add_note_3_5);
        showAddNotes[4][3] = view.findViewById(R.id.show_add_note_4_5);
        showAddNotes[4][4] = view.findViewById(R.id.show_add_note_5_5);
        showAddNotes[4][5] = view.findViewById(R.id.show_add_note_6_5);
        showAddNotes[4][6] = view.findViewById(R.id.show_add_note_7_5);

        /*addNotes*/

        addNotes[0][0] = view.findViewById(R.id.add_note_1_1);
        addNotes[0][1] = view.findViewById(R.id.add_note_2_1);
        addNotes[0][2] = view.findViewById(R.id.add_note_3_1);
        addNotes[0][3] = view.findViewById(R.id.add_note_4_1);
        addNotes[0][4] = view.findViewById(R.id.add_note_5_1);
        addNotes[0][5] = view.findViewById(R.id.add_note_6_1);
        addNotes[0][6] = view.findViewById(R.id.add_note_7_1);

        addNotes[1][0] = view.findViewById(R.id.add_note_1_2);
        addNotes[1][1] = view.findViewById(R.id.add_note_2_2);
        addNotes[1][2] = view.findViewById(R.id.add_note_3_2);
        addNotes[1][3] = view.findViewById(R.id.add_note_4_2);
        addNotes[1][4] = view.findViewById(R.id.add_note_5_2);
        addNotes[1][5] = view.findViewById(R.id.add_note_6_2);
        addNotes[1][6] = view.findViewById(R.id.add_note_7_2);

        addNotes[2][0] = view.findViewById(R.id.add_note_1_3);
        addNotes[2][1] = view.findViewById(R.id.add_note_2_3);
        addNotes[2][2] = view.findViewById(R.id.add_note_3_3);
        addNotes[2][3] = view.findViewById(R.id.add_note_4_3);
        addNotes[2][4] = view.findViewById(R.id.add_note_5_3);
        addNotes[2][5] = view.findViewById(R.id.add_note_6_3);
        addNotes[2][6] = view.findViewById(R.id.add_note_7_3);

        addNotes[3][0] = view.findViewById(R.id.add_note_1_4);
        addNotes[3][1] = view.findViewById(R.id.add_note_2_4);
        addNotes[3][2] = view.findViewById(R.id.add_note_3_4);
        addNotes[3][3] = view.findViewById(R.id.add_note_4_4);
        addNotes[3][4] = view.findViewById(R.id.add_note_5_4);
        addNotes[3][5] = view.findViewById(R.id.add_note_6_4);
        addNotes[3][6] = view.findViewById(R.id.add_note_7_4);

        addNotes[4][0] = view.findViewById(R.id.add_note_1_5);
        addNotes[4][1] = view.findViewById(R.id.add_note_2_5);
        addNotes[4][2] = view.findViewById(R.id.add_note_3_5);
        addNotes[4][3] = view.findViewById(R.id.add_note_4_5);
        addNotes[4][4] = view.findViewById(R.id.add_note_5_5);
        addNotes[4][5] = view.findViewById(R.id.add_note_6_5);
        addNotes[4][6] = view.findViewById(R.id.add_note_7_5);

        /*notes*/

        notes[0][0] = view.findViewById(R.id.note_1_1);
        notes[0][1] = view.findViewById(R.id.note_2_1);
        notes[0][2] = view.findViewById(R.id.note_3_1);
        notes[0][3] = view.findViewById(R.id.note_4_1);
        notes[0][4] = view.findViewById(R.id.note_5_1);
        notes[0][5] = view.findViewById(R.id.note_6_1);
        notes[0][6] = view.findViewById(R.id.note_7_1);

        notes[1][0] = view.findViewById(R.id.note_1_2);
        notes[1][1] = view.findViewById(R.id.note_2_2);
        notes[1][2] = view.findViewById(R.id.note_3_2);
        notes[1][3] = view.findViewById(R.id.note_4_2);
        notes[1][4] = view.findViewById(R.id.note_5_2);
        notes[1][5] = view.findViewById(R.id.note_6_2);
        notes[1][6] = view.findViewById(R.id.note_7_2);

        notes[2][0] = view.findViewById(R.id.note_1_3);
        notes[2][1] = view.findViewById(R.id.note_2_3);
        notes[2][2] = view.findViewById(R.id.note_3_3);
        notes[2][3] = view.findViewById(R.id.note_4_3);
        notes[2][4] = view.findViewById(R.id.note_5_3);
        notes[2][5] = view.findViewById(R.id.note_6_3);
        notes[2][6] = view.findViewById(R.id.note_7_3);

        notes[3][0] = view.findViewById(R.id.note_1_4);
        notes[3][1] = view.findViewById(R.id.note_2_4);
        notes[3][2] = view.findViewById(R.id.note_3_4);
        notes[3][3] = view.findViewById(R.id.note_4_4);
        notes[3][4] = view.findViewById(R.id.note_5_4);
        notes[3][5] = view.findViewById(R.id.note_6_4);
        notes[3][6] = view.findViewById(R.id.note_7_4);

        notes[4][0] = view.findViewById(R.id.note_1_5);
        notes[4][1] = view.findViewById(R.id.note_2_5);
        notes[4][2] = view.findViewById(R.id.note_3_5);
        notes[4][3] = view.findViewById(R.id.note_4_5);
        notes[4][4] = view.findViewById(R.id.note_5_5);
        notes[4][5] = view.findViewById(R.id.note_6_5);
        notes[4][6] = view.findViewById(R.id.note_7_5);

        /*noteNames*/

        noteNames[0][0] = view.findViewById(R.id.note_1_1_name);
        noteNames[0][1] = view.findViewById(R.id.note_2_1_name);
        noteNames[0][2] = view.findViewById(R.id.note_3_1_name);
        noteNames[0][3] = view.findViewById(R.id.note_4_1_name);
        noteNames[0][4] = view.findViewById(R.id.note_5_1_name);
        noteNames[0][5] = view.findViewById(R.id.note_6_1_name);
        noteNames[0][6] = view.findViewById(R.id.note_7_1_name);

        noteNames[1][0] = view.findViewById(R.id.note_1_2_name);
        noteNames[1][1] = view.findViewById(R.id.note_2_2_name);
        noteNames[1][2] = view.findViewById(R.id.note_3_2_name);
        noteNames[1][3] = view.findViewById(R.id.note_4_2_name);
        noteNames[1][4] = view.findViewById(R.id.note_5_2_name);
        noteNames[1][5] = view.findViewById(R.id.note_6_2_name);
        noteNames[1][6] = view.findViewById(R.id.note_7_2_name);

        noteNames[2][0] = view.findViewById(R.id.note_1_3_name);
        noteNames[2][1] = view.findViewById(R.id.note_2_3_name);
        noteNames[2][2] = view.findViewById(R.id.note_3_3_name);
        noteNames[2][3] = view.findViewById(R.id.note_4_3_name);
        noteNames[2][4] = view.findViewById(R.id.note_5_3_name);
        noteNames[2][5] = view.findViewById(R.id.note_6_3_name);
        noteNames[2][6] = view.findViewById(R.id.note_7_3_name);

        noteNames[3][0] = view.findViewById(R.id.note_1_4_name);
        noteNames[3][1] = view.findViewById(R.id.note_2_4_name);
        noteNames[3][2] = view.findViewById(R.id.note_3_4_name);
        noteNames[3][3] = view.findViewById(R.id.note_4_4_name);
        noteNames[3][4] = view.findViewById(R.id.note_5_4_name);
        noteNames[3][5] = view.findViewById(R.id.note_6_4_name);
        noteNames[3][6] = view.findViewById(R.id.note_7_4_name);

        noteNames[4][0] = view.findViewById(R.id.note_1_5_name);
        noteNames[4][1] = view.findViewById(R.id.note_2_5_name);
        noteNames[4][2] = view.findViewById(R.id.note_3_5_name);
        noteNames[4][3] = view.findViewById(R.id.note_4_5_name);
        noteNames[4][4] = view.findViewById(R.id.note_5_5_name);
        noteNames[4][5] = view.findViewById(R.id.note_6_5_name);
        noteNames[4][6] = view.findViewById(R.id.note_7_5_name);




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
    public static void postAllTimetable(final LoginBean loginBean, final Fragment timetableFragment, final String semester, final int nowWeek){
        while(!threadStack.empty()){
            Thread t = threadStack.pop();
            try{
                //t.interrupt();
            }catch (Exception e){
                Log.e(TAG,e.getMessage());
            }

        }
        //查询数据库是否有存该用户当前学期的课表，若有则返回
        String username = ((MainActivity)timetableFragment.getActivity()).username;

        CourseList couList = LitePal.where("semester = ? and username = ?",semester,username).findFirst(CourseList.class);
        if(couList!=null) {
            //Toast.makeText(timetableFragment.getActivity(),"not empty!"+couList.getUsername()+"  "+couList.getSemester(),Toast.LENGTH_SHORT).show();
            Log.i(TAG,"list is not empty! "+couList.getUsername()+"  "+couList.getSemester());
            for(int i=1;i<=20;i++){
                Log.i(TAG,"this couList "+i+" is "+couList.getCourseResponseDatas().get(i));
                CourseBean courseBean = Utills.parseJSON(couList.getCourseResponseDatas().get(i),CourseBean.class);
                if(courseBean==null) Log.i(TAG,"courseBean is null, couList[i]: "+couList.getCourseResponseDatas().get(i));
                if(((TimetableFragment)timetableFragment).semester.equals(semester)){
                    ((TimetableFragment)timetableFragment).courseLists.set(i,courseBean.getData());
                    ((TimetableFragment) timetableFragment).isFinished[i] = true;
                }
            }
            return;
        }else Log.i(TAG,"list is empty!");
        List<String> list = new ArrayList<>();
        for(int i=0;i<=20;i++){
            list.add(null);
        }
        CourseList cList = new CourseList();
        cList.setUsername(username);
        cList.setSemester(semester);
        final String cookie = loginBean.getData().getCookie();
        final String token = loginBean.getData().getToken();
        Thread T = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = false;
                for(int week=nowWeek;;week = (week+1)%21){
                    if(week==0) week = 1;
                    if(flag && week==nowWeek) {
                        //储存该用户当前学期的课表
                        cList.setCourseResponseDatas(list);
                        for(int i=1;i<=20;i++){
                            if(cList.getCourseResponseDatas().get(i)==null) Log.i(TAG,"this cList "+i+" is null!!!");
                            else Log.i(TAG,"this cList "+i+" is "+cList.getCourseResponseDatas().get(i));
                        }

                        cList.save();
                        CourseList cList1 = LitePal.where("semester = ?",semester).findFirst(CourseList.class);
                        Log.i(TAG,"save success "+semester);
                        for(int i=1;i<=20;i++){
                            if(cList1.getCourseResponseDatas().get(i)==null) Log.i(TAG,"this cList "+i+" is null!!!");
                            else Log.i(TAG,"this cList "+i+" is "+cList1.getCourseResponseDatas().get(i));
                        }
                        break;
                    }

                    if(week==nowWeek) flag = true;
                    try{
                        OkHttpClient client = new OkHttpClient();
                        RequestBody requestBody = new FormBody.Builder()
                                .add("cookie",cookie)
                                .add("xueqi",semester)
                                .add("zc",week+"")
                                .build();
                        Request request = new Request.Builder()
                                .url("http://42.193.177.76:8081/getCourse")
                                .post(requestBody)
                                .addHeader("token",token)
                                .build();
                        Response response = client.newCall(request).execute();
                        String responseData = response.body().string();
                        //存入课程数据
                        list.set(week,responseData);
                        CourseBean courseBean = Utills.parseJSON(responseData,CourseBean.class);
                        if(((TimetableFragment)timetableFragment).semester.equals(semester)){
                            ((TimetableFragment)timetableFragment).courseLists.set(week,courseBean.getData());
                            ((TimetableFragment) timetableFragment).isFinished[week] = true;
                        }
                        Log.i(TAG,"have gotten "+semester+" courseList_"+week);
                    }catch (Exception e) {
                        Log.d("okHttpError","okHttpError");
                        e.printStackTrace();
                    }
                }
            }

        });
        threadStack.push(T);
        T.start();

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
