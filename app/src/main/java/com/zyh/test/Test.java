package com.zyh.test;

import android.util.Log;

import com.google.gson.Gson;
import com.zyh.utills.CalendarUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import lombok.AllArgsConstructor;
import lombok.Data;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Test {
    static String device_id = "524181837";
    static String apikey = "qD2aM7i=uickjuPrSRwvGbBfQX0=";
    public static void main(String[] args) throws ParseException, InterruptedException {
        sendPost();
    }
    private static void sendPost(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                BufferedReader reader = null;
                try {
                    //获取Json数据代码
                    URL url = new URL("https://api.heclouds.com/devices/" + device_id +"/datastreams/");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(3 * 1000);
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("api-key", apikey);
                    InputStream in = conn.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while((line = reader.readLine())!=null){
                        response.append(line);
                    }
                    //2、解析Json数据
                    System.out.println("数据：   "+response.toString());
//            JSONObject jsonObject1 = new JSONObject(response.toString());
//            JSONObject jsonObject2 = jsonObject1.getJSONObject("data");
//            String datastream = jsonObject2.getString("current_value");
//            int data = jsonObject2.getInt("current_value");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}