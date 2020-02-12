package com.zyh.fragment.timetableFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zyh.beans.Course;
import com.zyh.beans.CourseBean;
import com.zyh.beans.LoginBean;
import com.zyh.fragment.R;
import com.zyh.fragment.TimetableFragment;
import com.zyh.utills.Utills;

import java.util.List;

/**
 * Created by caobotao on 16/1/4.
 */
public class TimetableFragment9 extends Fragment {
    private LoginBean loginBean;
    private Fragment timetableFragment;

    private TextView textView;

    private TextView month;
    private TextView[] weekDate;
    private Course[][] courseMsgs;
    private Course[][] course2Msgs;
    private CardView[][] courseItems;
    private CardView[][] course2Items;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.table, container, false);
        month = (TextView)view.findViewById(R.id.table_month);
        weekDate=new TextView[7];
        courseItems = new CardView[5][7];
        courseMsgs = new Course[5][7];
        course2Items = new CardView[2][7];
        course2Msgs = new Course[2][7];
        Utills.initCourseView(view,weekDate,courseItems,courseMsgs,course2Items,course2Msgs);
        timetableFragment = getTimetableFragmeent();
        loginBean = ((TimetableFragment) timetableFragment).loginBean;
        if(!((TimetableFragment) timetableFragment).isFinished[9]){
            Utills.postTimetable(loginBean,timetableFragment,((TimetableFragment) timetableFragment).semester,"9");
        }
        showTimetable();
        return view;
    }

    public void showTimetable(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!((TimetableFragment) timetableFragment).isFinished[9]){}
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        analyseCourseList(getActivity(),((TimetableFragment) timetableFragment).courseLists.get(9));
                    }
                });
            }
        }).start();
    }
    public void analyseCourseList(final FragmentActivity activity,final List<List<CourseBean.Course>> courseList){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<5;i++){
                    for (int j=0;j<7;j++){
                        if (courseList.get(i).get(j)!=null){
                            CourseBean.Course course = courseList.get(i).get(j);
                            CardView courseItem;
                            Course courseMsg;
                            if (course.getTime().contains("01-02-03-04")){
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
            }
        });

    }

    public TimetableFragment getTimetableFragmeent(){
        Fragment timetableFragment = null;
        List<Fragment>list=(List<Fragment>) TimetableFragment9.this.getFragmentManager().getFragments();
        for(Fragment f:list){
            if(f!=null && f instanceof TimetableFragment){
                timetableFragment = f;
                break;
            }
        }
        return (TimetableFragment)timetableFragment;
    }
}
