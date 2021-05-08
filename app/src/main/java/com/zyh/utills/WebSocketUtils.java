package com.zyh.utills;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.zyh.activities.MainActivity;
import com.zyh.activities.NoticeActivity;
import com.zyh.beans.MessageBean;
import com.zyh.beans.Messages;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.litepal.LitePal;

import java.net.URI;
import java.util.Date;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WebSocketUtils {
    private static final String TAG = "WebSocketUtils";
    private WebSocketClient webSocketClient;
    private Activity activity;
    private String token;
    private URI serverURI;
    public WebSocketUtils(Activity activity, String token,ImageView pot){
        this.activity = activity;
        this.token = token;
        serverURI = URI.create("ws://42.193.177.76:8081/websocket?token="+token);
        webSocketClient = new WebSocketClient(serverURI) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
            }
            @Override
            public void onMessage(String message) {
                MessageBean messageBean = Utills.parseJSON(message,MessageBean.class);
                Messages messages = new Messages();
                messages.setContent(messageBean.getContent());
                messages.setFormId(messageBean.getFromId());
                messages.setTime(CalendarUtil.timeStamp2Date(messageBean.getCreateTime()+""));
                messages.setRead(false);
                messages.save();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pot.setVisibility(View.VISIBLE);
                    }
                });
            }
            @Override
            public void onClose(int code, String reason, boolean remote) { }

            @Override
            public void onError(Exception ex) { }
        };
        webSocketClient.connect();
    }
    public void close(){
        webSocketClient.close();
    }
    private void showToast(String content){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, content, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 获取全部未读消息
     * @param token
     */
    public static void getUnReadMessage(String token) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();
                    HttpUrl.Builder urlBuilder = HttpUrl.parse("http://42.193.177.76:8081/getUnReadMessage")
                            .newBuilder();
                    urlBuilder.addQueryParameter("token", token);
                    Request.Builder requestBuilder = new Request.Builder()
                            .url(urlBuilder.build())
                            .addHeader("token",token);
                    requestBuilder.method("GET",null);
                    Request request = requestBuilder.build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.i(TAG,"getUnReadMessage:  " + responseData);
                }catch (Exception e) {
                    Log.d("getUnReadMessage","Error");
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public static void hasUnReadMessage(Activity activity, ImageView pot){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Messages> messagesList = LitePal.findAll(Messages.class);
                for(Messages msg : messagesList){
                    if (!msg.isRead()){
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pot.setVisibility(View.VISIBLE);
                            }
                        });
                        return;
                    }
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pot.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }).start();

    }
}
