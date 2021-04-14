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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.zyh.activities.MainActivity;
import com.zyh.beans.GradeBean;
import com.zyh.beans.LoginBean;
import com.zyh.beans.SemesterBean;
import com.zyh.recyclerView.GradeAdapter;
import com.zyh.utills.Utills;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GradeFragment extends Fragment {
    private LoginBean loginBean;
    private Spinner spinner;
    private LinearLayout grade_point_block;
    private TextView grade_point;
    private ArrayAdapter<String> adapter;
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
        spinner = (Spinner)view.findViewById(R.id.gradeSpinner);
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
                        adapter = new ArrayAdapter<String>(mainActivity,android.R.layout.simple_spinner_item,datas);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                        spinner.setOnItemSelectedListener(new SpinnerSelectedListener());
                        semester = loginBean.getData().getNowXueqi();
                        Utills.setCurrentSemester(datas,semester,spinner);
                        spinner.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).start();
    }
    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String semester = datas[position];
            postGrade(semester);
            while(isFinished.equals(false)){
                Log.d("GradeFragment","notFinished");
            }
            RecyclerView recyclerView = (RecyclerView)getActivity().findViewById(R.id.grade_recycler_view);
            LinearLayout noGrade = getActivity().findViewById(R.id.no_grade);
            TextView tip = getActivity().findViewById(R.id.tip);
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

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
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