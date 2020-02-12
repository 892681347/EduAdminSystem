package com.zyh.utills;

import android.app.Person;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zyh.beans.Course;
import com.zyh.beans.CourseBean;
import com.zyh.beans.LoginBean;
import com.zyh.fragment.R;
import com.zyh.fragment.TimetableFragment;

import java.util.Random;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Utills {
    private static int[] colors = {0x64ff00ff,0xb44d3900,0xb4f400a1,0xb4daa520,0xb44169e1,0xb4b509ff};

    public static int randomColor(){
        return colors[new Random().nextInt(colors.length)];
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
        weekDate[0] = (TextView)view.findViewById(R.id.table_Mondaydate);
        weekDate[1] = (TextView)view.findViewById(R.id.table_Tuesdaydate);
        weekDate[2] = (TextView)view.findViewById(R.id.table_Wednesdaydate);
        weekDate[3] = (TextView)view.findViewById(R.id.table_Thursdaydate);
        weekDate[4] = (TextView)view.findViewById(R.id.table_Fridaydate);
        weekDate[5] = (TextView)view.findViewById(R.id.table_Saturdaydate);
        weekDate[6] = (TextView)view.findViewById(R.id.table_Sundaydate);
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
                            .url("http://47.106.159.165:8081/getCourse")
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
}
