package com.zyh;

import android.app.Application;

import com.xuexiang.xui.XUI;

import org.litepal.LitePal;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        LitePal.initialize(this);
        XUI.init(this);
        XUI.debug(true);
        super.onCreate();
    }
}
