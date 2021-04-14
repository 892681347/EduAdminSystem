package com.zyh.beans;

import java.util.StringTokenizer;

public class Version {
    /**
     * 更新必看
     * 更新版本需要修改的内容：
     * 1.version 当前版本号
     * 2.apkName 用于下载一下版本apk后，打开指定文件的文件名
     * 3.Manifest中的<provider>，授予打开指定文件的权限，十分重要！！！
     * 4.修改build.gradle中的版本号
     * */
    //上一版v2.3.0
    private final static String version = "v2.5.0"; //更新时不能比上一版本少“.”
    // 如果apk文件名为1.apk，则此处填1,同时需修改Manifest中的<provider>
    private final static String apkName = "长理教务v3.1.0";   //这是下一版的名字，用于下载下一版apk，同时需修改Manifest中的<provider>！！！
    public static String getVersion() {
        return version;
    }

    public static String getApkName() {//用于下载下一版apk
        return apkName;
    }
    public static boolean isNeedUpdate(String newVersion){
        StringTokenizer newTokenizer = new StringTokenizer(newVersion, "v.");
        StringTokenizer verionTokenizer = new StringTokenizer(version, "v.");
        while(newTokenizer.hasMoreElements()){
            if (!verionTokenizer.hasMoreElements()) return true;
            //int i = Integer.parseInt( s );
            int i = Integer.parseInt(newTokenizer.nextToken()); //新版本号分割字符
            int j = Integer.parseInt(verionTokenizer.nextToken()); //当前版本号分割字符
            if (i>j) return true;
        }
        return false;
    }

    public static void main(String[] args) {
        String newVersion = "v2.3.0.1";
        System.out.println(isNeedUpdate(newVersion));
    }

}
