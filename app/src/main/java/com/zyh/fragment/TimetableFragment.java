package com.zyh.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.xuexiang.xui.widget.picker.widget.OptionsPickerView;
import com.xuexiang.xui.widget.picker.widget.builder.OptionsPickerBuilder;
import com.xuexiang.xui.widget.picker.widget.listener.OnOptionsSelectListener;
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
    private LinearLayout selectTimetableLinear;
    private TextView selectTimetable;
    private int timetableSelectOption = 0;
    private String[] datas;
    public String semester;
    public String nowWeek;
    public String selectedWeek;
    public String originalSemester;
    public Activity thisMainActivity;
    private MainActivity mainActivity;
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

        selectTimetableLinear = view.findViewById(R.id.select_timetable_linear);
        selectTimetable = view.findViewById(R.id.select_timetable);
        mainActivity = (MainActivity)getActivity();
        thisMainActivity = mainActivity;
        waitingAndSet();
        Log.d("TimetableFragment","ActionBegin");
        return view;
    }
    private void waitingAndSet(){
        loginBean = mainActivity.loginBean;
        Log.d("nowWeek",loginBean.getData().getNowWeek());
        semester = loginBean.getData().getNowXueqi();

        originalSemester = semester;
        nowWeek = loginBean.getData().getNowWeek();
        thisWeek = Integer.valueOf(nowWeek);
        new Thread(new Runnable() {
            @Override
            public void run() {
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
                updateLoginDB();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //initDatas();//初始化数据
                        selectTimetableLinear.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showPickerView();
                            }
                        });
                        for(int i=0;i<datas.length;i++){
                            if (datas[i].equals(semester)){
                                showTimetable(i);
                            }
                        }
                    }
                });
            }
        }).start();
    }
    private void updateLoginDB(){
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
    }
    private void showPickerView() {
        OptionsPickerView pvOptions = new OptionsPickerBuilder(mainActivity, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                showTimetable(options1);
            }
        })
                .setTitleText("选择学期")
                .setSelectOptions(timetableSelectOption)
                .build();
        pvOptions.setPicker(datas);
        pvOptions.show();
    }
    private void showTimetable(int options){
        selectTimetable.setText(datas[options]);
        timetableSelectOption = options;
        Utills.clear();
        semester = datas[options];
        weekText.setText("1");
        for(int i=0;i<isFinished.length;i++){
            isFinished[i] = false;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Utills.postAllTimetable(loginBean,TimetableFragment.this,semester,1);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initDatas();
                        //mAdapter.notifyDataSetChanged();
                        Utills.showIsNowWeek(datas[timetableSelectOption],originalSemester,isNowWeek,selectedWeek,nowWeek);
                    }
                });
            }
        }).start();
    }
    private void initDatas() {
        mFragments = new ArrayList<>();
        for(int i=1;i<=20;i++){
            mFragments.add(TimetableFragmentItem.newInstance(i));
        }
        //初始化适配器
        mAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            private  int mChildCount = 0;
            @Override
            public Fragment getItem(int position) {//从集合中获取对应位置的Fragment
                week = String.valueOf(position);
                return mFragments.get(position);
            }

            @Override
            public int getCount() {//获取集合中Fragment的总数
                return mFragments.size();
            }

            @Override
            public int getItemPosition(@NonNull Object object) {
                if ( mChildCount > 0) {
                    mChildCount --;
                    return POSITION_NONE;
                }
                return super.getItemPosition( object );
            }

            @Override
            public void notifyDataSetChanged() {
                mChildCount = getCount();
                super.notifyDataSetChanged();
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
            weekText.setText(String.valueOf(Integer.parseInt(nowWeek)));
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
                Utills.showIsNowWeek(datas[timetableSelectOption],originalSemester,isNowWeek,selectedWeek,nowWeek);

                mViewPager.setCurrentItem(position);
            }

            @Override
            //页面滚动状态改变事件
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onResume() {
        if(mAdapter!=null){
            mAdapter.notifyDataSetChanged();
        }
        super.onResume();
    }
}