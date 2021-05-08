package com.zyh.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.kongzue.dialog.v2.TipDialog;
import com.xuexiang.xui.widget.dialog.bottomsheet.BottomSheet;
import com.zyh.fragment.R;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.zyh.utills.Utills.validatePhoneNumber;

public class FeedbackActivity extends AppCompatActivity {
    EditText name;
    EditText phone;
    EditText content;
    Button submit;
    ImageView returnBack;
    private String token;
    private String lastName;
    private String lastPhone;
    private String lastContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_feedback);

        Intent intent = getIntent();
        lastName="";
        lastPhone="";
        lastContent="";
        token = (String) intent.getStringExtra("token");
        name = (EditText)findViewById(R.id.feedback_name);
        phone = (EditText)findViewById(R.id.feedback_phone);
        content = (EditText)findViewById(R.id.feedback_content);
        submit = (Button)findViewById(R.id.feedback_submit);
        returnBack = findViewById(R.id.return_img);
        name.setHintTextColor(getResources().getColor(R.color.feedback_text_color));
        phone.setHintTextColor(getResources().getColor(R.color.feedback_text_color));
        content.setHintTextColor(getResources().getColor(R.color.feedback_text_color));
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameString = name.getText().toString();
                String phoneString = phone.getText().toString();
                String contentString = content.getText().toString();
                int contentLength = contentString.length();
                if (nameString.equals("")){
                    Toast.makeText(FeedbackActivity.this, "姓名不可为空", Toast.LENGTH_SHORT).show();
                }else if (!validatePhoneNumber(phoneString)){
                    Toast.makeText(FeedbackActivity.this, "请填入正确手机号", Toast.LENGTH_SHORT).show();
                } else if (contentLength<4 || contentLength>105){
                    Toast.makeText(FeedbackActivity.this, "请输入4-100字", Toast.LENGTH_SHORT).show();
                }else if (lastContent.equals(contentString) && lastPhone.equals(phoneString) && lastName.equals(nameString)){
                    Toast.makeText(FeedbackActivity.this, "请勿重复提交", Toast.LENGTH_SHORT).show();
                }else {
                    lastName = nameString;
                    lastPhone = phoneString;
                    lastContent = contentString;
                    postFeedback(token,nameString,phoneString,contentString);
                }

            }
        });
        returnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
                            .url("http://42.193.177.76:8081/advice")
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
                TipDialog.show(FeedbackActivity.this, "提交成功", TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_FINISH);
                content.setText("");
            }
        });
    }
}
