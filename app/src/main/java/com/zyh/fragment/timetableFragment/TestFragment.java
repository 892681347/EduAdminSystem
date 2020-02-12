package com.zyh.fragment.timetableFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zyh.beans.Course;
import com.zyh.fragment.R;
import com.zyh.utills.Utills;


/**
 * Created by caobotao on 16/1/4.
 */
public class TestFragment extends Fragment {
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
        courseMsgs[0][0].getCourseName().setText("zyh");
        courseMsgs[4][5].getCourseName().setText("zyh");
        return view;
    }

}
