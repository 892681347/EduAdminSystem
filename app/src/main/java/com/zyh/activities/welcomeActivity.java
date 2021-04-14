package com.zyh.activities;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
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
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.zyh.beans.Account;
import com.zyh.beans.LoginBean;
import com.zyh.beans.Version;
import com.zyh.beans.VersionBean;
import com.zyh.fragment.R;
import com.zyh.update.DownloadService;
import com.zyh.utills.Utills;

import org.litepal.LitePal;

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

public class welcomeActivity extends Activity {
    private Timer timer;
    private DownloadService.DownloadBinder downloadBinder;
    private String downloadURL;
    private final int NOTICE = 1;
    private int status = 0;
    private int serviceStatus = 0;
    private String account;


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        postVersion();
        serviceInit();
        if(ContextCompat.checkSelfPermission(welcomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(welcomeActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }
    private void serviceInit(){
        Intent serviceIntent = new Intent(welcomeActivity.this,DownloadService.class);
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
        bindService(serviceIntent,connection,BIND_AUTO_CREATE);
        Log.d("downloadBinder","init");
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
        timer.schedule(delayTask,1500);
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
                }catch (Exception e) {
                    Log.d("okHttpError","okHttpError");
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void loginHandle(LoginBean loginBean){
        String code = loginBean.getCode();
        if(code.equals("200")){
            Intent intent = new Intent(welcomeActivity.this,MainActivity.class);
            MainActivity.actionStart(this, loginBean,account);
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
    private void showNoticeForNone(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            //数字是随便写的“40”，
            nm.createNotificationChannel(new NotificationChannel("40", "App Service", NotificationManager.IMPORTANCE_DEFAULT));
            NotificationCompat.Builder builder = new NotificationCompat.Builder(welcomeActivity.this, "40");
            builder.setSmallIcon(R.drawable.ic_launcher_background);
            builder.setContentTitle("通了个知");
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            builder.setContentText("别管这条通知，看了也白看");
            //其中的2，是也随便写的，正式项目也是随便写
            downloadBinder.startForegroundForNone(10 ,builder.build());
        }
    }
    private void showUpdateDialog(final String url) {
        downloadURL = url;
        builder = new AlertDialog.Builder(this).setIcon(R.mipmap.ic_launcher).setTitle("有新版本可用")
                .setMessage("是否更新").setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startNextActivity();
                        startDownloadAPK(url);
                    }
                }).setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showNoticeForNone();
                        startNextActivity();
                        dialogInterface.dismiss();
                    }
                }).setNeutralButton("手动更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("http://42.193.177.76:8081/apk/CSUST.apk"));
                        startActivity(intent);
                        showNoticeForNone();
                        startNextActivity();
                        dialog.dismiss();
                    }
                });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                 AlertDialog alertDialog = builder.create();
                 alertDialog.setCanceledOnTouchOutside(false);
                 alertDialog.show();
            }
        });
    }
    private void showFirstUseDialog(){
        builder = new AlertDialog.Builder(this).setIcon(R.mipmap.ic_launcher).setTitle("新版本介绍")
                .setMessage("当前版本新特性:\n\n" +
                        "\t\t1.新增课表桌面小部件\n\n\t\t2.ui界面优化\n\n\t\t3.算法更新-同课程颜色相同\n\n\t\t4.课表存入本地数据库" +
                        "\n\n如果有任何建议欢迎反馈给我们，谢谢支持！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startNextActivity();
                    }
                });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
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
                        showNoticeForNone();
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

    protected void onDestroy(){
        super.onDestroy();
        unbindService(connection);

    }
    private void startDownloadAPK(final String url){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!checkNotifySetting()){
                    Toast.makeText(welcomeActivity.this,"请务必打开通知权限，否则无法更新",Toast.LENGTH_SHORT).show();
                    openNotityRight();
                    status = NOTICE;
                    return;
                }
                if (downloadBinder==null){
                    Log.d("downloadBinder","null");
                    return;
                }
                downloadBinder.startDownload(url);
            }
        });
    }
    private boolean checkNotifySetting() {
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        // areNotificationsEnabled方法的有效性官方只最低支持到API 19，低于19的仍可调用此方法不过只会返回true，即默认为用户已经开启了通知。
        boolean isOpened = manager.areNotificationsEnabled();
        return isOpened;
    }
    public void openNotityRight(){
        try {
            // 根据isOpened结果，判断是否需要提醒用户跳转AppInfo页面，去打开App通知权限
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            //这种方案适用于 API 26, 即8.0（含8.0）以上可以用
            intent.putExtra(EXTRA_APP_PACKAGE, getPackageName());
            intent.putExtra(EXTRA_CHANNEL_ID, getApplicationInfo().uid);

            //这种方案适用于 API21——25，即 5.0——7.1 之间的版本可以使用
            intent.putExtra("app_package", getPackageName());
            intent.putExtra("app_uid", getApplicationInfo().uid);

            // 小米6 -MIUI9.6-8.0.0系统，是个特例，通知设置界面只能控制"允许使用通知圆点"——然而这个玩意并没有卵用，我想对雷布斯说：I'm not ok!!!
            //  if ("MI 6".equals(Build.MODEL)) {
            //      intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            //      Uri uri = Uri.fromParts("package", getPackageName(), null);
            //      intent.setData(uri);
            //      // intent.setAction("com.android.settings/.SubSettings");
            //  }
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            // 出现异常则跳转到应用设置界面：锤子坚果3——OC105 API25
            Intent intent = new Intent();

            //下面这种方案是直接跳转到当前应用的设置界面。
            //https://blog.csdn.net/ysy950803/article/details/71910806
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
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
        if (status==NOTICE){
            if (!checkNotifySetting()){
                Toast.makeText(welcomeActivity.this,"未打开通知权限无法更新",Toast.LENGTH_SHORT).show();
            }
            Log.d("NOTICE","startDownload");
            downloadBinder.startDownload(downloadURL);
            status = 0;
        }
        super.onResume();
    }
}
