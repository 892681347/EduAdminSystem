package com.zyh.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xuexiang.xui.widget.picker.widget.OptionsPickerView;
import com.xuexiang.xui.widget.picker.widget.builder.OptionsPickerBuilder;
import com.xuexiang.xui.widget.picker.widget.listener.OnOptionsSelectListener;
import com.zyh.activities.MainActivity;
import com.zyh.beans.GradeBean;
import com.zyh.beans.LoginBean;
import com.zyh.recyclerView.GradeAdapter;
import com.zyh.utills.Utills;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GradeFragment extends Fragment {
    private LoginBean loginBean;
    private TextView selectGrade;
    private LinearLayout selectGradeLinear;
    private int gradeSelectOption = 0;
    private LinearLayout grade_point_block;
    private TextView grade_point;
    private RecyclerView recyclerView;
    private LinearLayout noGrade;
    private TextView tip;
    private MainActivity mainActivity;
    private String[] datas;
    public String semester;
    private List<GradeBean.Datas> gradeList;
    private Boolean isFinished;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainActivity = (MainActivity)getActivity();
        View view = inflater.inflate(R.layout.grade, container, false);
        isFinished = false;
        tip = view.findViewById(R.id.tip);
        noGrade = view.findViewById(R.id.no_grade);
        recyclerView = view.findViewById(R.id.grade_recycler_view);
        selectGrade = view.findViewById(R.id.select_grade);
        selectGradeLinear = view.findViewById(R.id.select_grade_linear);
        grade_point_block = view.findViewById(R.id.grade_point_block);
        grade_point = view.findViewById(R.id.grade_point);
        waitingAndSet();
        return view;
    }
    private void waitingAndSet(){
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loginBean = mainActivity.loginBean;
                        selectGradeLinear.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showPickerView();
                            }
                        });
                        semester = loginBean.getData().getNowXueqi();
                        for(int i=0;i<datas.length;i++){
                            if (datas[i].equals(semester)){
                                showGrade(i);
                            }
                        }
                    }
                });
            }
        }).start();
    }
    private void showPickerView() {
        OptionsPickerView pvOptions = new OptionsPickerBuilder(mainActivity, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                showGrade(options1);
            }
        })
                .setTitleText("选择学期")
                .setSelectOptions(gradeSelectOption)
                .build();
        pvOptions.setPicker(datas);
        pvOptions.show();
    }
    private void showGrade(int options){
        selectGrade.setText(datas[options]);
        gradeSelectOption = options;
        String semester = datas[options];
        new Thread(new Runnable() {
            @Override
            public void run() {
                postGrade(semester);
                while(isFinished.equals(false)){
                    Log.d("GradeFragment","notFinished");
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (gradeList==null){
                            recyclerView.setVisibility(View.INVISIBLE);
                            grade_point_block.setVisibility(View.INVISIBLE);
                            noGrade.setVisibility(View.VISIBLE);
                            tip.setVisibility(View.GONE);
                            isFinished = false;
                        }else {
                            recyclerView.setVisibility(View.VISIBLE);
                            grade_point_block.setVisibility(View.VISIBLE);
                            noGrade.setVisibility(View.INVISIBLE);
                            tip.setVisibility(View.VISIBLE);
                            showGradeRecyclerView();
                            showGradePoint(gradeList);
                        }
                    }
                });
            }
        }).start();


    }
    private void postGrade(final String semester) {
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
                            .build();
                    Request request = new Request.Builder()
                            .url("http://42.193.177.76:8081/queryScore")
                            .post(requestBody)
                            .addHeader("token",token)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    GradeBean gradeBean = Utills.parseJSON(responseData, GradeBean.class);
                    System.out.println("123456    "+gradeBean.getData());
                    gradeList = gradeBean.getData();
                    isFinished = true;
                }catch (Exception e) {
                    Log.d("okHttpError","okHttpError");
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void showGradeRecyclerView(){
        final String cookie = loginBean.getData().getCookie();
        final String token = loginBean.getData().getToken();
        RecyclerView recyclerView = (RecyclerView)getActivity().findViewById(R.id.grade_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mainActivity);
        recyclerView.setLayoutManager(layoutManager);
        GradeAdapter adapter = new GradeAdapter(gradeList,getActivity(),token,cookie);
        recyclerView.setAdapter(adapter);
        isFinished = false;
    }
    private void showGradePoint(List<GradeBean.Datas> gradeList){
        double pointCreditSum = 0;
        double creditSum = 0;
        for (GradeBean.Datas grade : gradeList) {
            creditSum += Double.parseDouble(grade.getXuefen());
            pointCreditSum += Double.parseDouble(grade.getPoint())*Double.parseDouble(grade.getXuefen());
        }
        double AVGPoint = (double)Math.round(pointCreditSum/creditSum*100)/100;
        grade_point.setText(String.valueOf(AVGPoint));
    }
}