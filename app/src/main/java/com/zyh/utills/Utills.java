package com.zyh.utills;

import android.app.Person;
import android.util.Log;

import com.google.gson.Gson;

public class Utills {

    public static <T>  T parseJSON(String jsonData,Class<T> classes){
        T t = null;
        try{
            Gson gson = new Gson();
            t = gson.fromJson(jsonData,classes);
        }catch (Exception e){
            Log.d("parseJSONError","parseJSONError");
            e.printStackTrace();
        }
        return t;
    }
}
