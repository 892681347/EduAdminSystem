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
    private ArrayAdapter<String> adapter;
    private MainActivity mainActivity;
    private String[] datas;
    private List<GradeBean.Datas> gradeList;
    private Boolean isFinished;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainActivity = (MainActivity)getActivity();
        View view = inflater.inflate(R.layout.grade, container, false);
        isFinished = false;
        spinner = (Spinner)view.findViewById(R.id.gradeSpinner);
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
        adapter = new ArrayAdapter<String>(mainActivity,android.R.layout.simple_spinner_item,datas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (spinner==null){
            Log.d("GradeFragment","ActionBegin: spinner equals null!!!");
        }
        if (adapter==null){
            Log.d("GradeFragment","ActionBegin: adapter equals null!!!");
        }
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new SpinnerSelectedListener());
        ////////////////////////////////////
        spinner.setSelection(1,true);
        //////////////////////////////////
        spinner.setVisibility(View.VISIBLE);
        Log.d("GradeFragment","ActionBegin");
        return view;
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
            TextView noGrade = (TextView) getActivity().findViewById(R.id.no_grade);
            if (gradeList==null){
                recyclerView.setVisibility(View.INVISIBLE);
                noGrade.setVisibility(View.VISIBLE);
                isFinished = false;
            }else {
                recyclerView.setVisibility(View.VISIBLE);
                noGrade.setVisibility(View.INVISIBLE);
                showGradeRecyclerView();
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
                            .url("http://47.106.159.165:8081/queryScore")
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
        RecyclerView recyclerView = (RecyclerView)getActivity().findViewById(R.id.grade_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mainActivity);
        recyclerView.setLayoutManager(layoutManager);
        GradeAdapter adapter = new GradeAdapter(gradeList);
        recyclerView.setAdapter(adapter);
        isFinished = false;
    }
}