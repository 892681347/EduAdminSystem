package com.zyh.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends FragmentActivity implements OnClickListener {
    private long exitTime = 0;

    private TextView topNmae;

    //声明四个Tab的布局文件
    private LinearLayout mTabTimetable;
    private LinearLayout mTabGrade;
    private LinearLayout mTabExam;
    private LinearLayout mTabIndividual;

    //声明四个Tab的ImageButton
    private ImageButton mImgTimetable;
    private ImageButton mImgGrade;
    private ImageButton mImgExam;
    private ImageButton mImgIndividual;

    //声明四个Tab分别对应的Fragment
    private Fragment mFragTimetable;
    private Fragment mFragGrade;
    private Fragment mFragExam;
    private Fragment mFragIndividual;

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
        initViews();//初始化控件
        initEvents();//初始化事件
        selectTab(0);//默认选中第一个Tab
    }

    public static void actionStart(Context context, LoginBean loginBean){
        Intent intent = new Intent(context,MainActivity.class);
        intent.putExtra("loginBean", loginBean);
        context.startActivity(intent);
        ((Activity)context).finish();
    }

    private void initEvents() {
        //初始化四个Tab的点击事件
        mTabTimetable.setOnClickListener(this);
        mTabGrade.setOnClickListener(this);
        mTabExam.setOnClickListener(this);
        mTabIndividual.setOnClickListener(this);
    }

    private void initViews() {
        //初始化四个Tab的布局文件
        mTabTimetable = (LinearLayout) findViewById(R.id.id_tab_weixin);
        mTabGrade = (LinearLayout) findViewById(R.id.id_tab_frd);
        mTabExam = (LinearLayout) findViewById(R.id.id_tab_address);
        mTabIndividual = (LinearLayout) findViewById(R.id.id_tab_setting);

        //初始化四个ImageButton
        mImgTimetable = (ImageButton) findViewById(R.id.id_tab_weixin_img);
        mImgGrade = (ImageButton) findViewById(R.id.id_tab_frd_img);
        mImgExam = (ImageButton) findViewById(R.id.id_tab_address_img);
        mImgIndividual = (ImageButton) findViewById(R.id.id_tab_setting_img);

        topNmae = (TextView)findViewById(R.id.top_name);
    }

    //处理Tab的点击事件
    @Override
    public void onClick(View v) {
        //先将四个ImageButton置为灰色
        resetImgs();
        switch (v.getId()) {
            case R.id.id_tab_weixin:
                selectTab(0);//当点击的是微信的Tab就选中微信的Tab
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
        //获取FragmentManager对象
        FragmentManager manager = getSupportFragmentManager();
        //获取FragmentTransaction对象
        FragmentTransaction transaction = manager.beginTransaction();
        //先隐藏所有的Fragment
        hideFragments(transaction);
        switch (i) {
            //当选中点击的是微信的Tab时
            case 0:
                //设置微信的ImageButton为绿色
                mImgTimetable.setImageResource(R.mipmap.timetable_pressed);
                topNmae.setText("课程表");
                //如果微信对应的Fragment没有实例化，则进行实例化，并显示出来
                if (mFragTimetable == null) {
                    mFragTimetable = new TimetableFragment();
                    transaction.add(R.id.id_content, mFragTimetable);
                } else {
                    //如果微信对应的Fragment已经实例化，则直接显示出来
                    transaction.show(mFragTimetable);
                }
                break;
            case 1:
                mImgGrade.setImageResource(R.mipmap.grade_pressed);
                topNmae.setText("成绩");
                if (mFragGrade == null) {
                    mFragGrade = new GradeFragment();
                    transaction.add(R.id.id_content, mFragGrade);
                } else {
                    transaction.show(mFragGrade);
                }
                break;
            case 2:
                mImgExam.setImageResource(R.mipmap.exam_pressed);
                topNmae.setText("考试");
                if (mFragExam == null) {
                    mFragExam = new ExamFragment();
                    transaction.add(R.id.id_content, mFragExam);
                } else {
                    transaction.show(mFragExam);
                }
                break;
            case 3:
                mImgIndividual.setImageResource(R.mipmap.individual_pressed);
                topNmae.setText("我的");
                if (mFragIndividual == null) {
                    mFragIndividual = new IndividualFragment();
                    transaction.add(R.id.id_content, mFragIndividual);
                } else {
                    transaction.show(mFragIndividual);
                }
                break;
        }
        //不要忘记提交事务
        transaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (mFragTimetable != null) {
            transaction.hide(mFragTimetable);
        }
        if (mFragGrade != null) {
            transaction.hide(mFragGrade);
        }
        if (mFragExam != null) {
            transaction.hide(mFragExam);
        }
        if (mFragIndividual != null) {
            transaction.hide(mFragIndividual);
        }
    }

    //将四个ImageButton置为灰色
    private void resetImgs() {
        mImgTimetable.setImageResource(R.mipmap.timetable);
        mImgGrade.setImageResource(R.mipmap.grade);
        mImgExam.setImageResource(R.mipmap.exam);
        mImgIndividual.setImageResource(R.mipmap.individual);
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
}

