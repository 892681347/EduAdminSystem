package com.zyh.test;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Test {
    public static void main(String[] args){
        String a = "程序设计、算法与数据结构（二）";
        String b = "高等数学";
        System.out.println(a.length()+"  "+b.length());
    }

}
