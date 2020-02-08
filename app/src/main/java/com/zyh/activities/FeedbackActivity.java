package com.zyh.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zyh.beans.LoginBean;
import com.zyh.fragment.R;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FeedbackActivity extends AppCompatActivity {
    EditText name;
    EditText phone;
    EditText content;
    Button submit;
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Intent intent = getIntent();
        token = (String) intent.getStringExtra("token");
        name = (EditText)findViewById(R.id.feedback_name);
        phone = (EditText)findViewById(R.id.feedback_phone);
        content = (EditText)findViewById(R.id.feedback_content);
        submit = (Button)findViewById(R.id.feedback_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameString = name.getText().toString();
                String phoneString = phone.getText().toString();
                String contentString = content.getText().toString();
                postFeedback(token,nameString,phoneString,contentString);
            }
        });
    }
    public static void actionStart(Context context, String token){
        Intent intent = new Intent(context,FeedbackActivity.class);
        intent.putExtra("token", token);
        context.startActivity(intent);
    }
    private void postFeedback(final String token, final String nameString, final String phoneString, final String contentString){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("name",nameString)
                            .add("phone",phoneString)
                            .add("content",contentString)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://47.106.159.165:8081/advice")
                            .post(requestBody)
                            .addHeader("token",token)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    addFeedbackSucess();

                }catch (Exception e) {
                    Log.d("okHttpError","okHttpError");
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void addFeedbackSucess(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FeedbackActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
