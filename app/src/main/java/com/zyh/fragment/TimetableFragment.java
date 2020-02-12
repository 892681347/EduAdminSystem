package com.zyh.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.zyh.activities.MainActivity;
import com.zyh.beans.CourseBean;
import com.zyh.beans.LoginBean;
import com.zyh.fragment.timetableFragment.TimetableFragment1;
import com.zyh.fragment.timetableFragment.TimetableFragment10;
import com.zyh.fragment.timetableFragment.TimetableFragment11;
import com.zyh.fragment.timetableFragment.TimetableFragment12;
import com.zyh.fragment.timetableFragment.TimetableFragment13;
import com.zyh.fragment.timetableFragment.TimetableFragment14;
import com.zyh.fragment.timetableFragment.TimetableFragment15;
import com.zyh.fragment.timetableFragment.TimetableFragment16;
import com.zyh.fragment.timetableFragment.TimetableFragment17;
import com.zyh.fragment.timetableFragment.TimetableFragment18;
import com.zyh.fragment.timetableFragment.TimetableFragment19;
import com.zyh.fragment.timetableFragment.TimetableFragment2;
import com.zyh.fragment.timetableFragment.TimetableFragment20;
import com.zyh.fragment.timetableFragment.TimetableFragment3;
import com.zyh.fragment.timetableFragment.TimetableFragment4;
import com.zyh.fragment.timetableFragment.TimetableFragment5;
import com.zyh.fragment.timetableFragment.TimetableFragment6;
import com.zyh.fragment.timetableFragment.TimetableFragment7;
import com.zyh.fragment.timetableFragment.TimetableFragment8;
import com.zyh.fragment.timetableFragment.TimetableFragment9;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimetableFragment extends Fragment {
    public LoginBean loginBean;
    private Spinner spinner1;
    private ArrayAdapter<String> adapter1;
    private String[] datas;
    private String[] weeks;
    public String semester;
    public String week = "ssss";
    public String[] context = {"abc","def","ghi","klm"};
    public int getTimetableNum = 0;
    public List<String> timetableList = Arrays.asList(null,null,null,null,null,null,null,null,null
            ,null,null,null,null,null,null,null,null,null,null,null,null);
//    ("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"
//            , "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21")
    public List<List<List<CourseBean.Course>>> courseLists = Arrays.asList(null,null,null,null,null,null,null,null,null
        ,null,null,null,null,null,null,null,null,null,null,null,null);
    private TextView weekText;
    //声明ViewPager
    private ViewPager mViewPager;
    //适配器
    private FragmentPagerAdapter mAdapter;
    //装载Fragment的集合
    private List<Fragment> mFragments;
    public Boolean[] isFinished = {false,false,false,false,false,false,false,false,false,false,false
            ,false,false,false,false,false,false,false,false,false,false};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timetable, container, false);
        mViewPager = (ViewPager)view.findViewById(R.id.id_viewpager1);
        weekText = (TextView)view.findViewById(R.id.text_week);
        initDatas();//初始化数据

        spinner1 = (Spinner)view.findViewById(R.id.timetableSpinner1);
        MainActivity mainActivity = (MainActivity)getActivity();
        //spinner1
        //需要修改！！！！！！！！！！！！
        while (true) {
            datas = mainActivity.semesters;
            if (!(datas==null||datas.length==0)) {
                Log.d("GradeFragment","ActionBegin: getting datas...");
                break;
            }
        }
        if (datas==null){
            Log.d("GradeFragment","ActionBegin: datas equals null!!!");
        }
        loginBean = mainActivity.loginBean;
        semester = loginBean.getData().getNowXueqi();
        adapter1 = new ArrayAdapter<String>(mainActivity,android.R.layout.simple_spinner_item,datas);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (spinner1==null){
            Log.d("GradeFragment","ActionBegin: spinner equals null!!!");
        }
        if (adapter1==null){
            Log.d("GradeFragment","ActionBegin: adapter equals null!!!");
        }
        spinner1.setAdapter(adapter1);
        spinner1.setOnItemSelectedListener(new SpinnerSelectedListener1());
        ////////////////////////////////////
        for(int i=0;i<datas.length;i++){
            if (datas[i].equals(semester)){
                spinner1.setSelection(i,true);
            }
        }
        //////////////////////////////////
        spinner1.setVisibility(View.VISIBLE);

        //spinner2
        String[] weekss = {"0","1","2","3","4","5","6","7","8",
                "9","10","11","12","13","14","15","16","17","18","19","20"};
        weeks = weekss;
        Log.d("TimetableFragment","ActionBegin");
        return view;
    }

    private void initDatas() {
        mFragments = new ArrayList<>();
//        mFragments.add(new TestFragment());
        mFragments.add(new TimetableFragment1());
        mFragments.add(new TimetableFragment2());
        mFragments.add(new TimetableFragment3());
        mFragments.add(new TimetableFragment4());
        mFragments.add(new TimetableFragment5());
        mFragments.add(new TimetableFragment6());
        mFragments.add(new TimetableFragment7());
        mFragments.add(new TimetableFragment8());
        mFragments.add(new TimetableFragment9());
        mFragments.add(new TimetableFragment10());
        mFragments.add(new TimetableFragment11());
        mFragments.add(new TimetableFragment12());
        mFragments.add(new TimetableFragment13());
        mFragments.add(new TimetableFragment14());
        mFragments.add(new TimetableFragment15());
        mFragments.add(new TimetableFragment16());
        mFragments.add(new TimetableFragment17());
        mFragments.add(new TimetableFragment18());
        mFragments.add(new TimetableFragment19());
        mFragments.add(new TimetableFragment20());


        //初始化适配器
        mAdapter = new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {//从集合中获取对应位置的Fragment
                week = String.valueOf(position);
                return mFragments.get(position);
            }

            @Override
            public int getCount() {//获取集合中Fragment的总数
                return mFragments.size();
            }

        };
        if (mViewPager==null){
            Log.d("null","mViewPager==null");
        }
        //不要忘记设置ViewPager的适配器
        mViewPager.setAdapter(mAdapter);
        //设置ViewPager的切换监听
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            //页面滚动事件
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            //页面选中事件
            @Override
            public void onPageSelected(int position) {
                int nowWeek = position+1;
                weekText.setText(String.valueOf(nowWeek));
                mViewPager.setCurrentItem(position);
            }

            @Override
            //页面滚动状态改变事件
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class SpinnerSelectedListener1 implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            semester = datas[position];
            for(int i=0;i<isFinished.length;i++){
                isFinished[i] = false;
            }
            weekText.setText("1");
            initDatas();
//            mViewPager.setCurrentItem(18);
//            mViewPager.setCurrentItem(0);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

}
