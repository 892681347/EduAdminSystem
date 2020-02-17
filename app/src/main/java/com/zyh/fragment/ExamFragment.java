package com.zyh.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zyh.activities.MainActivity;
import com.zyh.beans.ExamBean;
import com.zyh.beans.GradeBean;
import com.zyh.beans.LoginBean;
import com.zyh.beans.SemesterBean;
import com.zyh.fragment.R;
import com.zyh.recyclerView.ExamAdapter;
import com.zyh.recyclerView.GradeAdapter;
import com.zyh.utills.Utills;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ExamFragment extends Fragment {
    private LoginBean loginBean;
    private Spinner spinner;
    private ArrayAdapter<String> adapter;
    public String semester;
    private String[] datas;
    private List<ExamBean.Exam> examList;
    private MainActivity mainActivity;
    private Boolean isFinished;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.exam, container, false);
        isFinished = false;
        spinner = (Spinner)view.findViewById(R.id.examSpinner);
        mainActivity = (MainActivity)getActivity();
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
                        Log.d("ExamFragment","ActionBegin: getting datas...");
                        break;
                    }
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        datas = mainActivity.semesters;
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
            postExam(semester);
            while(isFinished.equals(false)){
                Log.d("ExamFragment","notFinished");
            }
            RecyclerView recyclerView = (RecyclerView)getActivity().findViewById(R.id.exam_recycler_view);
            TextView noGrade = (TextView) getActivity().findViewById(R.id.no_exam);
            if (examList==null){
                recyclerView.setVisibility(View.INVISIBLE);
                noGrade.setVisibility(View.VISIBLE);
                isFinished = false;
            }else {
                recyclerView.setVisibility(View.VISIBLE);
                noGrade.setVisibility(View.INVISIBLE);
                showExamRecyclerView();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
    private void postExam(final String semester) {
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
                            .url("http://47.106.159.165:8081/getKsap")
                            .post(requestBody)
                            .addHeader("token",token)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    /*
                    responseData = "{\"code\":200,\"msg\":\"请求成功\",\"data\":[{\"campus\":\"云塘" +
                            "校区\",\"courseName\":\"计算机组成原理\",\"teacher\":\"曾道建\",\"" +
                            "startTime\":\"2019-12-10 14:20\",\"endTime\":\"2019-12-10 16:20\",\"" +
                            "address\":\"云综教A-201\",\"seatNumber\":\"\",\"ticketNumber\":\"\"}," +
                            "{\"campus\":\"云塘校区\",\"courseName\":\"程序设计、算法与数据结构（一）实验\",\"teacher\":\"" +
                            "颜宏文\",\"startTime\":\"2019-12-23 09:20\",\"endTime\":\"" +
                            "2019-12-23 11:20\",\"address\":\"云综教A-104\",\"seatNumber\":\"\",\"" +
                            "ticketNumber\":\"\"},{\"campus\":\"云塘校区\",\"courseName\":\"UML建模\"," +
                            "\"teacher\":\"黄园媛\",\"startTime\":\"2019-12-25 14:20\",\"endTime\":" +
                            "\"2019-12-25 16:20\",\"address\":\"云综教C-305\",\"seatNumber\":\"\"," +
                            "\"ticketNumber\":\"\"},{\"campus\":\"云塘校区\",\"courseName\":\"计算机图形学\"," +
                            "\"teacher\":\"桂彦\",\"startTime\":\"2019-12-26 09:20\",\"endTime\":\"2019-12-26 11:20\"," +
                            "\"address\":\"云综教B-406\",\"seatNumber\":\"\",\"ticketNumber\":\"\"}]}";
                    */
                    ExamBean examBean = Utills.parseJSON(responseData,ExamBean.class);
                    examList = examBean.getData();
                    isFinished = true;
                }catch (Exception e) {
                    Log.d("okHttpError","okHttpError");
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void showExamRecyclerView(){
        RecyclerView recyclerView = (RecyclerView)getActivity().findViewById(R.id.exam_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mainActivity);
        recyclerView.setLayoutManager(layoutManager);
        ExamAdapter adapter = new ExamAdapter(examList);
        recyclerView.setAdapter(adapter);
        isFinished = false;
    }
}
