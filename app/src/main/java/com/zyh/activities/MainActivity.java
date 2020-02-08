package com.zyh.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zyh.beans.LoginBean;
import com.zyh.beans.SemesterBean;
import com.zyh.fragment.ExamFragment;
import com.zyh.fragment.GradeFragment;
import com.zyh.fragment.IndividualFragment;
import com.zyh.fragment.R;
import com.zyh.fragment.TimetableFragment;
import com.zyh.utills.Utills;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends FragmentActivity implements View.OnClickListener{
    private long exitTime = 0;

    private TextView topNmae;

    //声明ViewPager
    private ViewPager mViewPager;
    //适配器
    private FragmentPagerAdapter mAdapter;
    //装载Fragment的集合
    private List<Fragment> mFragments;

    //四个Tab对应的布局
    private LinearLayout mTabTimetable;
    private LinearLayout mTabGrade;
    private LinearLayout mTabExam;
    private LinearLayout mTabIndividual;

    //四个Tab对应的ImageButton
    private ImageButton mImgTimetable;
    private ImageButton mImgGrade;
    private ImageButton mImgExam;
    private ImageButton mImgIndividual;
    public LoginBean loginBean;
    public String[] semesters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        loginBean = (LoginBean) intent.getSerializableExtra("loginBean");
        Log.d("MainActivity","ActionBegin:Already get loginBean");
        postSemester(loginBean.getData().getToken());
//        textView.setText(loginBean.toString());
        initViews();//初始化控件
        initEvents();//初始化事件
        initDatas();//初始化数据
    }
    public static void actionStart(Context context, LoginBean loginBean){
        Intent intent = new Intent(context,MainActivity.class);
        intent.putExtra("loginBean", loginBean);
        context.startActivity(intent);
        ((Activity)context).finish();
    }

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }

    private void postSemester(final String token){
        Log.d("MainActivity","ActionBegin:1Haven't get semesters");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Log.d("MainActivity","ActionBegin:2Haven't get semesters");
                    OkHttpClient client = new OkHttpClient();
                    Log.d("MainActivity","ActionBegin:3Haven't get semesters");
                    Request request = new Request.Builder()
                            .url("http://47.106.159.165:8081/getAllSemester")
                            .addHeader("token",token)
                            .build();
                    Log.d("MainActivity","ActionBegin:4Haven't get semesters");
                    Response response = client.newCall(request).execute();
                    Log.d("MainActivity","ActionBegin:5Haven't get semesters");
                    String responseData = response.body().string();
                    Log.d("MainActivity","ActionBegin:6Haven't get semesters");
                    SemesterBean semesterBean = Utills.parseJSON(responseData, SemesterBean.class);
                    Log.d("MainActivity","ActionBegin:7Haven't get semesters");
                    semesters = semesterBean.getData();
                    Log.d("MainActivity","ActionBegin:Already get semesters");
                }catch (Exception e) {
                    Log.d("okHttpError","okHttpError");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /*
     用Fragment和Viewpaper实现滑动和点击效果
     Begin
     */
    private void initDatas() {
        mFragments = new ArrayList<>();
        //将四个Fragment加入集合中
        mFragments.add(new TimetableFragment());
        mFragments.add(new GradeFragment());
        mFragments.add(new ExamFragment());
        mFragments.add(new IndividualFragment());

        //初始化适配器
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {//从集合中获取对应位置的Fragment
                return mFragments.get(position);
            }

            @Override
            public int getCount() {//获取集合中Fragment的总数
                return mFragments.size();
            }

        };
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
                //设置position对应的集合中的Fragment
                mViewPager.setCurrentItem(position);
                resetImgs();
                selectTab(position);
            }

            @Override
            //页面滚动状态改变事件
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initEvents() {
        //设置四个Tab的点击事件
        mTabTimetable.setOnClickListener(this);
        mTabGrade.setOnClickListener(this);
        mTabExam.setOnClickListener(this);
        mTabIndividual.setOnClickListener(this);

    }

    //初始化控件
    private void initViews() {
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);

        mTabTimetable = (LinearLayout) findViewById(R.id.id_tab_weixin);
        mTabGrade = (LinearLayout) findViewById(R.id.id_tab_frd);
        mTabExam = (LinearLayout) findViewById(R.id.id_tab_address);
        mTabIndividual = (LinearLayout) findViewById(R.id.id_tab_setting);

        mImgTimetable = (ImageButton) findViewById(R.id.id_tab_weixin_img);
        mImgGrade = (ImageButton) findViewById(R.id.id_tab_frd_img);
        mImgExam = (ImageButton) findViewById(R.id.id_tab_address_img);
        mImgIndividual = (ImageButton) findViewById(R.id.id_tab_setting_img);

        topNmae = (TextView)findViewById(R.id.top_name);
    }

    @Override
    public void onClick(View v) {
        //先将四个ImageButton置为灰色
        resetImgs();

        //根据点击的Tab切换不同的页面及设置对应的ImageButton为绿色
        switch (v.getId()) {
            case R.id.id_tab_weixin:
                selectTab(0);
                break;
            case R.id.id_tab_frd:
                selectTab(1);
                break;
            case R.id.id_tab_address:
                selectTab(2);
                break;
            case R.id.id_tab_setting:
                selectTab(3);
                break;
        }
    }

    private void selectTab(int i) {
        //根据点击的Tab设置对应的ImageButton为绿色
        switch (i) {
            case 0:
                mImgTimetable.setImageResource(R.mipmap.timetable_pressed);
                topNmae.setText("课程表");
                break;
            case 1:
                mImgGrade.setImageResource(R.mipmap.grade_pressed);
                topNmae.setText("成绩");
                break;
            case 2:
                mImgExam.setImageResource(R.mipmap.exam_pressed);
                topNmae.setText("考试");
                break;
            case 3:
                mImgIndividual.setImageResource(R.mipmap.individual_pressed);
                topNmae.setText("我的");
                break;
        }
        //设置当前点击的Tab所对应的页面
        mViewPager.setCurrentItem(i);
    }

    //将四个ImageButton设置为灰色
    private void resetImgs() {
        mImgTimetable.setImageResource(R.mipmap.timetable);
        mImgGrade.setImageResource(R.mipmap.grade);
        mImgExam.setImageResource(R.mipmap.exam);
        mImgIndividual.setImageResource(R.mipmap.individual);
    }
    /*
     用Fragment和Viewpaper实现滑动和点击效果
     End
     */
}
