package com.zyh.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.zyh.activities.MainActivity;
import com.zyh.beans.LoginBean;
import com.zyh.fragment.R;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TimetableFragment extends Fragment {
    private LoginBean loginBean;
    private TextView textView;
    private Spinner spinner1;
    private Spinner spinner2;
    private ArrayAdapter<String> adapter1;
    private ArrayAdapter<String> adapter2;
    private String[] datas;
    private String[] weeks;
    private String semester;
    private String week;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timetable, container, false);

        textView = (TextView)view.findViewById(R.id.timetableInfo);
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
    private void ShowMsg(final String data){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(data);
            }
        });
    }
}
