package com.zyh.beans;

public class Version {
    //上一版v2.0.1
    private final static String version = "v2.2.0";
    private final static String apkName = "长理教务v2.3.0";   //这是下一版的名字
    // 如果apk文件名为1.apk，则此处填1,同时需修改Manifest中的<provider>
    public static String getVersion() {
        return version;
    }

    public static String getApkName() {
        return apkName;
    }
}
