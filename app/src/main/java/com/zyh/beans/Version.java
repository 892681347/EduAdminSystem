package com.zyh.beans;

public class Version {
    private final static String version = "v1.0.0";
    private final static String apkName = "1";   //如果apk文件名为1.apk，则此处填1
    public static String getVersion() {
        return version;
    }

    public static String getApkName() {
        return apkName;
    }
}
