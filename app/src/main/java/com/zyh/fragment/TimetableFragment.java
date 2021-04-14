package com.zyh.fragment;

import android.app.Activity;
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
import com.zyh.beans.Account;
import com.zyh.beans.CourseBean;
import com.zyh.beans.LoginBean;
import com.zyh.utills.Utills;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TimetableFragment extends Fragment {
    public static boolean isThisSemester = false;
    public static int thisWeek;
    public LoginBean loginBean;
    private Spinner spinner;
    private ArrayAdapter<String> adapter1;
    private String[] datas;
    public String semester;
    public String nowWeek;
    public String selectedWeek;
    public String originalSemester;
    public Activity thisMainActivity;
    public String week = "ssss";
    public String[] context = {"abc","def","ghi","klm"};
    public int getTimetableNum = 0;
    public List<String> timetableList = Arrays.asList(null,null,null,null,null,null,null,null,null
            ,null,null,null,null,null,null,null,null,null,null,null,null);
//    ("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"
//            , "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21")
    public List<List<List<CourseBean.Course>>> courseLists = Arrays.asList(null,null,null,null,null,null,null,null,null   //21个
        ,null,null,null,null,null,null,null,null,null,null,null,null);
    private TextView weekText;
    private TextView isNowWeek;
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
        isNowWeek = (TextView)view.findViewById(R.id.text_isnowweek);


        spinner = (Spinner)view.findViewById(R.id.timetableSpinner1);
        MainActivity mainActivity = (MainActivity)getActivity();
        thisMainActivity = mainActivity;
        //spinner
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
        Log.d("nowWeek",loginBean.getData().getNowWeek());
        semester = loginBean.getData().getNowXueqi();

        originalSemester = semester;
        nowWeek = loginBean.getData().getNowWeek();
        thisWeek = Integer.valueOf(nowWeek);
        //更新当前用户的当前学期、周次、登录时间
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyy-MM-dd");// a为am/pm的标记
        Date date = new Date();// 获取当前时间
        String username = ((MainActivity) getActivity()).username;
        Account account = new Account();
        account.setSemester(semester);
        account.setWeek(nowWeek);
        account.setTime(sdf.format(date));
        account.updateAll("username = ?",username);

        initDatas();//初始化数据
        //Utills.postAllTimetable(loginBean,this,semester,Integer.parseInt(nowWeek));

        adapter1 = new ArrayAdapter<String>(mainActivity,android.R.layout.simple_spinner_item,datas);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (spinner==null){
            Log.d("GradeFragment","ActionBegin: spinner equals null!!!");
        }
        if (adapter1==null){
            Log.d("GradeFragment","ActionBegin: adapter equals null!!!");
        }
        spinner.setAdapter(adapter1);
        spinner.setOnItemSelectedListener(new SpinnerSelectedListener1());
        ////////////////////////////////////
        Utills.setCurrentSemester(datas,semester,spinner);

        //////////////////////////////////
        spinner.setVisibility(View.VISIBLE);



        Log.d("TimetableFragment","ActionBegin");
        return view;
    }

    private void initDatas() {
        mFragments = new ArrayList<>();
        for(int i=1;i<=20;i++){
            mFragments.add(TimetableFragmentItem.newInstance(i));
        }


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
        //设置初始页面
        selectedWeek = "1";
        if (!nowWeek.equals("-1") && semester.equals(originalSemester)){
            selectedWeek = nowWeek;
            mViewPager.setCurrentItem(Integer.parseInt(nowWeek)-1);
        }
        //设置ViewPager的切换监听
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            //页面滚动事件
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            //页面选中事件
            @Override
            public void onPageSelected(int position) {
                int thisWeek = position+1;
                selectedWeek = thisWeek+"";
                weekText.setText(String.valueOf(thisWeek));
                Utills.showIsNowWeek(spinner,originalSemester,isNowWeek,selectedWeek,nowWeek);

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
            Utills.clear();
            semester = datas[position];
            weekText.setText("1");
            for(int i=0;i<isFinished.length;i++){
                isFinished[i] = false;
            }
            Utills.postAllTimetable(loginBean,TimetableFragment.this,semester,1);
            initDatas();
            Utills.showIsNowWeek(spinner,originalSemester,isNowWeek,selectedWeek,nowWeek);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

}
