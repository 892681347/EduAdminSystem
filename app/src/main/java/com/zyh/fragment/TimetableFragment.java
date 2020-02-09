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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.zyh.activities.MainActivity;
import com.zyh.beans.LoginBean;
import com.zyh.fragment.R;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TimetableFragment extends Fragment {
    private LoginBean loginBean;
    private Spinner spinner1;
    private Spinner spinner2;
    private ArrayAdapter<String> adapter1;
    private ArrayAdapter<String> adapter2;
    private String[] datas;
    private String[] weeks;
    private String semester;
    private String week;

    //声明ViewPager
    private ViewPager mViewPager;
    //适配器
    private FragmentPagerAdapter mAdapter;
    //装载Fragment的集合
    private List<Fragment> mFragments;

    //四个Tab对应的布局
    private LinearLayout mTabWeixin;
    private LinearLayout mTabFrd;
    private LinearLayout mTabAddress;
    private LinearLayout mTabSetting;

    //四个Tab对应的ImageButton
    private ImageButton mImgWeixin;
    private ImageButton mImgFrd;
    private ImageButton mImgAddress;
    private ImageButton mImgSetting;
    private TextView textView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timetable, container, false);
        View view1 = inflater.inflate(R.layout.tab1, container, false);
        textView = view1.findViewById(R.id.abcd);
        mViewPager = (ViewPager)view.findViewById(R.id.id_viewpager1);
        initDatas();//初始化数据

        spinner1 = (Spinner)view.findViewById(R.id.timetableSpinner1);
        spinner2 = (Spinner)view.findViewById(R.id.timetableSpinner2);
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
        spinner1.setSelection(1,true);
        //////////////////////////////////
        spinner1.setVisibility(View.VISIBLE);

        //spinner2
        String[] weekss = {"1","2","3","4","5","6","7","8",
                "9","10","11","12","13","14","15","16","17","18","19","20"};
        weeks = weekss;
        adapter2 = new ArrayAdapter<String>(mainActivity,android.R.layout.simple_spinner_item,weeks);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(new SpinnerSelectedListener2());
        ////////////////////////////////////
        spinner2.setSelection(1,true);
        //////////////////////////////////
        spinner2.setVisibility(View.VISIBLE);

        Log.d("TimetableFragment","ActionBegin");
        return view;
    }

    private void initViews() {
        mViewPager = (ViewPager) getActivity().findViewById(R.id.id_viewpager1);

    }

    private void initDatas() {
        mFragments = new ArrayList<>();
        //将四个Fragment加入集合中
        mFragments.add(new WeixinFragment());
        mFragments.add(new WeixinFragment());
        mFragments.add(new WeixinFragment());
        mFragments.add(new WeixinFragment());
//        mFragments.add(new FrdFragment());
//        mFragments.add(new AddressFragment());
//        mFragments.add(new SettingFragment());

        //初始化适配器
        mAdapter = new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {//从集合中获取对应位置的Fragment
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
            postTimetable(semester,week);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
    class SpinnerSelectedListener2 implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            week = weeks[position];
            postTimetable(semester,week);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
    private void postTimetable(final String semester, final String week) {
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
                    ShowMsg(semester+" "+week+" "+responseData);
                }catch (Exception e) {
                    Log.d("okHttpError","okHttpError");
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void ShowMsg(final String a){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(a);
            }
        });
    }
}
