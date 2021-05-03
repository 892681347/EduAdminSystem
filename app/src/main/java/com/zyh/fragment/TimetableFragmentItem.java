package com.zyh.fragment;

import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zyh.beans.Course;
import com.zyh.beans.LoginBean;
import com.zyh.utills.Utills;

public class TimetableFragmentItem extends Fragment {
    private LoginBean loginBean;
    private Fragment timetableFragment;
    private String TAG = "TimetableFragmentItem";
    private TextView month;
    private TextView monthWord;
    private TextView[] weekDate;
    private Course[][] courseMsgs;
    private Course[][] course2Msgs;
    private CardView[][] courseItems;
    private CardView[][] course2Items;
    private LinearLayout[] weekLinearLayout;
    public LinearLayout nowShowAddNote;
    public LinearLayout nowAddNote;
    private LinearLayout[][] showAddNotes;
    private LinearLayout[][] addNotes;
    private CardView[][] notes;
    private TextView[][] noteNames;
    public TimetableFragmentItem timetableFragmentItem;
    int index;
    public static TimetableFragmentItem newInstance(int index) {
        TimetableFragmentItem newFragment = new TimetableFragmentItem();
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        newFragment.setArguments(bundle);
        return newFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            index = args.getInt("index");
        }
        String indexStr = index+"";
        View view = inflater.inflate(R.layout.table, container, false);
        month = (TextView)view.findViewById(R.id.table_month);
        monthWord = (TextView)view.findViewById(R.id.table_month_word);
        weekDate = new TextView[7];
        courseItems = new CardView[5][7];
        courseMsgs = new Course[5][7];
        course2Items = new CardView[2][7];     //占两大节的课
        course2Msgs = new Course[2][7];
        weekLinearLayout = new LinearLayout[8];
        showAddNotes = new LinearLayout[5][7];
        addNotes = new LinearLayout[5][7];
        notes = new CardView[5][7];
        noteNames = new TextView[5][7];
        Utills.initCourseView(view,weekDate,courseItems,courseMsgs,course2Items,course2Msgs,weekLinearLayout,showAddNotes,addNotes,notes,noteNames);

        timetableFragment = Utills.getTimetableFragmeent(this);
        timetableFragmentItem = this;
        loginBean = ((TimetableFragment) timetableFragment).loginBean;
//        if(!((TimetableFragment) timetableFragment).isFinished[index]){
//            Utills.postTimetable(loginBean,timetableFragment,((TimetableFragment) timetableFragment).semester,indexStr);
//        }
        Utills.showTimetable(timetableFragment,((TimetableFragment) timetableFragment).thisMainActivity,
                ((TimetableFragment) timetableFragment).courseLists.get(index),
                month,monthWord,weekDate,courseMsgs,course2Msgs,courseItems,
                course2Items, ((TimetableFragment) timetableFragment).nowWeek,
                ((TimetableFragment) timetableFragment).semester,
                ((TimetableFragment) timetableFragment).originalSemester,weekLinearLayout,showAddNotes,addNotes,timetableFragmentItem,notes,noteNames,index);



        return view;
    }
}