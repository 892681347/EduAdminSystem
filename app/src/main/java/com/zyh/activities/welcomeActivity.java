package com.zyh.activities;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import com.kongzue.dialog.util.TextInfo;
import com.kongzue.dialog.v2.DialogSettings;
import com.kongzue.dialog.v2.MessageDialog;
import com.kongzue.dialog.v2.SelectDialog;
import com.xuexiang.xui.XUI;
import com.zyh.beans.Account;
import com.zyh.beans.LoginBean;
import com.zyh.beans.Note;
import com.zyh.beans.Version;
import com.zyh.beans.VersionBean;
import com.zyh.fragment.R;
import com.zyh.utills.Utills;

import org.litepal.LitePal;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Notification.EXTRA_CHANNEL_ID;
import static android.provider.Settings.EXTRA_APP_PACKAGE;
import static com.kongzue.dialog.v2.DialogSettings.STYLE_IOS;

public class welcomeActivity extends AppCompatActivity {
    private final String TAG = "welcomeActivity";
    private Context context = welcomeActivity.this;
    private Timer timer;
    private String downloadURL;
    private final int NOTICE = 1;
    private int status = 0;
    private int serviceStatus = 0;
    private String account;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        DialogSettings.style = STYLE_IOS;
        DialogSettings.use_blur = true;
        XUI.initTheme(this);

        postVersion();
        if(ContextCompat.checkSelfPermission(welcomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(welcomeActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }
    private void startNextActivity(){
        TimerTask delayTask = new TimerTask() {
            @Override
            public void run() {
                List<Account> lastAccount = LitePal.where("isLast = ?","1").find(Account.class);
                if (!lastAccount.isEmpty()){
                    account = lastAccount.get(0).getUsername();

                    String pw = lastAccount.get(0).getPassword();
                    if (pw!=null && pw!=""){
                        int version = android.os.Build.VERSION.SDK_INT;
//                        if (version < 29) {
//                            startMainActivity(account,pw);
//                        }else {
//                            startLoginActivity();
//                        }
                        startMainActivity(account,pw);
                    }else {
                        startLoginActivity();
                    }
                }else {
                    startLoginActivity();
                }
            }
        };
        timer = new Timer();
        timer.schedule(delayTask,750);
    }
    private void startLoginActivity(){
        Intent intent = new Intent(welcomeActivity.this,LoginActivity.class);
        startActivity(intent);
        welcomeActivity.this.finish();
    }
    private void startMainActivity(String username, String password){
        sendRequestWithOkHttp(username,password);
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
                    loginHandle(loginBean);
                }catch (SocketTimeoutException e){
                    showTimeoutDialog();
                }catch (Exception e) {
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
                MessageDialog.build(welcomeActivity.this, "登录超时", "\n也许是教务系统官网出现了问题...\n" +
                                "请确保教务系统官网能够访问\n亦或是保存的账号密码出现了问题，可尝试清除应用数据后重新打开应用", "知道了",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startLoginActivity();
                            }
                        }).showDialog();

            }
        });
    }
    private void loginHandle(LoginBean loginBean){
        String code = loginBean.getCode();
        if(code.equals("200")){
            Intent intent = new Intent(welcomeActivity.this,MainActivity.class);
            MainActivity.actionStart(this, loginBean,account);
        }else if(code.equals("401")){
            showToast("账号或密码错误");
            startLoginActivity();
        }else if (code.equals("501")){
            showToast("服务器错误，请联系开发人员");
        }else {//502
            showToast("教务系统无相应");
        }
    }
    private void showToast(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showUpdateDialog(final String url) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SelectDialog.show(context, "有新版本可用", "是否更新", "更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                        startNextActivity();
                    }
                }, "下次再说", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startNextActivity();
                    }
                });
            }
        });
    }
    private void showFirstUseDialog(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MessageDialog.build(welcomeActivity.this, "新版本介绍\n", "当前版本新特性:\n\n" +
                                "\t\t1.ui界面优化\n\n\t\t2.增加课表备注功能\n\n\t\t3.增加通知功能" +
                                "\n\n如果有任何建议欢迎反馈给我们，谢谢支持！\n", "知道了",
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startNextActivity();
                    }
                }).setTitleTextInfo(new TextInfo()
                        .setGravity(Gravity.START)
                        .setBold(true)
                ).setContentTextInfo(new TextInfo()
                        .setGravity(Gravity.START)
                ).showDialog();

            }
        });
    }
    private void postVersion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://42.193.177.76:8081/getLastVersion")
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    VersionBean versionBean = Utills.parseJSON(responseData, VersionBean.class);
                    SharedPreferences sharedPreferences = getSharedPreferences("FirstRun",MODE_PRIVATE);
                    boolean first_run = sharedPreferences.getBoolean("isFirstRun",true);
                    if (!Version.isNeedUpdate(versionBean.getData().getVersionName())){
                        if (first_run){
                            sharedPreferences.edit().putBoolean("isFirstRun",false).apply();
                            showFirstUseDialog();
                        }else {
                            startNextActivity();
                        }
                    }else {
                        //设置isFirstRun为true，当更新app后就会调用showFirstUseDialog（）方法。
                        sharedPreferences.edit().putBoolean("isFirstRun",true).apply();
                        showUpdateDialog(versionBean.getData().getApkPath());
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void onRequestPermissionResult(int requestCode,String[] permissions,int[] grantResults){
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"拒绝权限将无法使用程序",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }
    @Override
    protected void onPause() {
        Log.d("welcomActivity","onPause");
        if (timer!=null){
            timer.cancel();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d("welcomActivity","onResume");
        if (timer!=null){
            startNextActivity();
        }
        super.onResume();
    }
}
