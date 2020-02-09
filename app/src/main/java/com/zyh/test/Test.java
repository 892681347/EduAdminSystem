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
        int[] a = {0,1,2,3,4,5,6,7,8,9};
        for(int b:a){
            int c = b%4;
            System.out.println(c);
        }
    }

}
