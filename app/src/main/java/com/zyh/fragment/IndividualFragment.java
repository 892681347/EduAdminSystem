package com.zyh.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zyh.activities.FeedbackActivity;
import com.zyh.activities.MainActivity;
import com.zyh.beans.HeadPicBean;
import com.zyh.beans.LoginBean;
import com.zyh.utills.Utills;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class IndividualFragment extends Fragment {
    Button addFeedback;
    String token;
    String cookie;
    String name;
    String stuId;
    String college;
    String major;
    String className;
    TextView name_text;
    TextView stuId_text;
    TextView college_text;
    TextView marjor_text;
    TextView className_text;
    com.makeramen.roundedimageview.RoundedImageView head_pic;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.individual, container, false);
        final MainActivity mainActivity = (MainActivity)getActivity();

        LoginBean.Datas.StuInfo stuInfo = ((LoginBean.Datas)mainActivity.loginBean.getData()).getStuInfo();
        name = stuInfo.getName();
        stuId = stuInfo.getStuId();
        college = stuInfo.getCollege();
        major = stuInfo.getMajor();
        className = stuInfo.getClassName();

        token = mainActivity.loginBean.getData().getToken();
        cookie = mainActivity.loginBean.getData().getCookie();
        getHeadPic();
        initView(view);
        Log.d("IndividualFragment","ActionBegin");
        addFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedbackActivity.actionStart(mainActivity,token);
            }
        });
        return view;
    }

    private void initView(View view){
        head_pic = (com.makeramen.roundedimageview.RoundedImageView)view.findViewById(R.id.head_pic);
        addFeedback = (Button)view.findViewById(R.id.addFeedback) ;
        name_text = (TextView)view.findViewById(R.id.name);
        stuId_text = (TextView)view.findViewById(R.id.stu_id);
        college_text = (TextView)view.findViewById(R.id.college);
        marjor_text = (TextView)view.findViewById(R.id.major);
        className_text = (TextView)view.findViewById(R.id.class_name);
        name_text.setText(name);
        stuId_text.setText(stuId);
        college_text.setText(college);
        marjor_text.setText(major);
        className_text.setText(className);
    }
    private void getHeadPic() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("cookie",cookie)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://47.106.159.165:8081/getHeadImg")
                            .post(requestBody)
                            .addHeader("token",token)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    HeadPicBean headPicBean = Utills.parseJSON(responseData,HeadPicBean.class);
                    ShowHeadPic(headPicBean.getData());
                }catch (Exception e) {
                    Log.d("okHttpError","okHttpError");
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void ShowHeadPic(String base64){
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        final Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                head_pic.setImageBitmap(decodedByte);
            }
        });
    }
}