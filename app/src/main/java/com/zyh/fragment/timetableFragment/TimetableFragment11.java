package com.zyh.fragment.timetableFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zyh.beans.LoginBean;
import com.zyh.fragment.R;
import com.zyh.fragment.TimetableFragment;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by caobotao on 16/1/4.
 */
public class TimetableFragment11 extends Fragment {
    private LoginBean loginBean;
    private TextView textView;
    private Fragment timetableFragment;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1, container, false);
        textView = (TextView)view.findViewById(R.id.text_1);
        timetableFragment = getTimetableFragmeent();
        loginBean = ((TimetableFragment) timetableFragment).loginBean;
        if(!((TimetableFragment) timetableFragment).isFinished[11]){
            postTimetable(((TimetableFragment) timetableFragment).semester,"11");
        }
//        while(!((TimetableFragment) timetableFragment).isFinished[11]){}
//        textView.setText(((TimetableFragment) timetableFragment).timetableList.get(11));
        return view;
    }
    public TimetableFragment getTimetableFragmeent(){
        Fragment timetableFragment = null;
        List<Fragment> list=(List<Fragment>) TimetableFragment11.this.getFragmentManager().getFragments();
        for(Fragment f:list){
            if(f!=null && f instanceof TimetableFragment){
                timetableFragment = f;
                break;
            }
        }
        return (TimetableFragment)timetableFragment;
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
                    int nowWeek = Integer.parseInt(week);
                    ((TimetableFragment)timetableFragment).timetableList.set(nowWeek,responseData);
                    ((TimetableFragment)timetableFragment).getTimetableNum++;
                    Log.d("Notfinished",String.valueOf(((TimetableFragment)timetableFragment).getTimetableNum));
                    if (((TimetableFragment)timetableFragment).getTimetableNum==4){
                        Log.d("finished","Get All Timetable!");
                    }
                    ((TimetableFragment) timetableFragment).isFinished[11] = true;
                }catch (Exception e) {
                    Log.d("okHttpError","okHttpError");
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
