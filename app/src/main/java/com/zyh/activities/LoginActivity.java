package com.zyh.activities;


import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kongzue.dialog.util.TextInfo;
import com.kongzue.dialog.v2.MessageDialog;
import com.zyh.beans.Account;
import com.zyh.beans.LoginBean;
import com.zyh.beans.Version;
import com.zyh.fragment.R;
import com.zyh.utills.Utills;

import org.litepal.LitePal;

import java.net.SocketTimeoutException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private long exitTime = 0;
    Button sendRequest;
    EditText usernameEdit;
    EditText passwordEdit;
    CheckBox ifSaveAccount;
    ImageView ifView;
    ProgressBar progressBar;
    private static String username;

    private String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //取消welcomActivity活动的多余通知
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(10);
        sendRequest = (Button) findViewById(R.id.send_request);
        usernameEdit = (EditText)findViewById(R.id.username);
        passwordEdit = (EditText)findViewById(R.id.password);
        ifView = (ImageView) findViewById(R.id.if_view);
        ifSaveAccount = (CheckBox) findViewById(R.id.if_save);
        progressBar = (ProgressBar) findViewById(R.id.waitting) ;
        progressBar.setVisibility(View.GONE);
        sendRequest.setOnClickListener(this);
        ifView.setOnClickListener(this);
        Log.d("LoginActivity","ActionBegin");

        List<Account> lastAccount = LitePal.where("isLast = ?","1").find(Account.class);
        if (!lastAccount.isEmpty()){
            usernameEdit.setText(lastAccount.get(0).getUsername());
            String pw = lastAccount.get(0).getPassword();
            if (pw!=null && pw!=""){
                passwordEdit.setText(pw);
                ifSaveAccount.setChecked(true);
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onClick(View v){
        if (v.getId()==R.id.send_request){
            username = usernameEdit.getText().toString();
            password = passwordEdit.getText().toString();
            if (username.equals("")||password.equals("")){
                String nameword;
                if (username.equals("") && password.equals("")) nameword="账号和密码";
                else if(username.equals("")) nameword="账号";
                else  nameword="密码";
                Toast.makeText(this,"请输入"+nameword,Toast.LENGTH_SHORT).show();
                return;
            }
            waitBegin(); //设置缓冲圈可见，登录按钮不可点击
            sendRequestWithOkHttp(username,password);
        }else if(v.getId()==R.id.if_view){
            //获取当前图片ConstantState类对象
            Drawable.ConstantState constantState = ifView.getDrawable().getCurrent().getConstantState();
            //获取需要比较的图片ConstantState类对象
            Drawable.ConstantState t2 = getDrawable(R.drawable.view).getConstantState();
            if (constantState.equals(t2)) {//隐藏密码
                ifView.setImageResource(R.drawable.view_off);
                passwordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else {//显示密码
                ifView.setImageResource(R.drawable.view);
                passwordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        }
    }

    private void waitBegin(){
        progressBar.setVisibility(View.VISIBLE);
        sendRequest.setEnabled(false);
    }
    private void waitEnd(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                sendRequest.setEnabled(true);
            }
        });
    }

    private void sendRequestWithOkHttp(final String username, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("username",username)
                            .add("password",password)
                            .add("agent", Version.getVersion())
                            .build();
                    Request request = new Request.Builder()
                            .url("http://42.193.177.76:8081/login")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    //showResponse(responseData);
                    Log.d("LoginActivity","responseData:  "+responseData);
                    LoginBean loginBean = Utills.parseJSON(responseData,LoginBean.class);
                    loginHandle(loginBean,username,password);
                }catch (SocketTimeoutException e){
                    showTimeoutDialog();
                    waitEnd();
                } catch (Exception e) {
                    Log.d("okHttpError","okHttpError");
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void showTimeoutDialog(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MessageDialog.build(LoginActivity.this, "登录超时\n", "也许是教务系统官网出现了问题...\n" +
                                "请确保教务系统官网能够访问\n", "知道了",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).showDialog();

            }
        });
    }
    private void loginHandle(LoginBean loginBean, String username, String password){
        String code;
        code = loginBean.getCode();
        if(code.equals("200")){
            if (ifSaveAccount.isChecked()){
                updateDB(username,password);
            }else{
                updateDB(username);
            }
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            MainActivity.actionStart(this, loginBean,username);
        }else if(code.equals("401")){
            showToast("账号或密码错误");
        }else if (code.equals("501")){
            showToast("服务器错误，请联系开发人员");
        }else {//502
            showToast("教务系统无相应");
        }
        waitEnd();
    }
    private void showToast(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDB(String username,String password){
        List<Account> accounts = LitePal.where("username = ?",username).find(Account.class);
        Account account = new Account();
        if (accounts.isEmpty()){
            setAllLast();
            account.setUsername(username);
            account.setPassword(password);
            account.setIsLast("1");
            account.save();
        }else{
            setAllLast();
            account.setPassword(password);
            account.setIsLast("1");
            account.updateAll("username = ?",username);
        }
        List<Account> accountsAll = LitePal.findAll(Account.class);
        String allAccount="";
        for(Account account1 : accountsAll){
            allAccount+=account1.toString();
        }
        Log.d("allAccount",allAccount);
    }

    private void updateDB(String username){
        List<Account> accounts = LitePal.where("username = ?",username).find(Account.class);
        Account account = new Account();
        if (accounts.isEmpty()){
            setAllLast();
            account.setUsername(username);
            account.setIsLast("1");
            account.save();
        }else{
            setAllLast();
            if (!accounts.get(0).getPassword().equals("")){
                account.setPassword("");
            }
            account.setIsLast("1");
            account.updateAll("username = ?",username);
        }
        List<Account> accountsAll = LitePal.findAll(Account.class);
        String allAccount="";
        for(Account account1 : accountsAll){
            allAccount+=account1.toString();
        }
        Log.d("allAccount",allAccount);
    }

    /**
     * 将所有项的last设为0
     */
    private void setAllLast(){
        Account account = new Account();
        account.setIsLast("0");
        account.updateAll();
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

