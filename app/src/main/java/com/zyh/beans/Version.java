package com.zyh.beans;

import java.util.StringTokenizer;

public class Version {
    /**
     * 更新必看
     * 更新版本需要修改的内容：
     * 1.version 当前版本号
     * 4.修改build.gradle中的版本号
     * */
    //上一版v2.5.0
    private final static String version = "v3.1.0"; //更新时不能比上一版本少“.”
    public static String getVersion() {
        return version;
    }


    public static boolean isNeedUpdate(String newVersion){
        StringTokenizer newTokenizer = new StringTokenizer(newVersion, "v.");
        StringTokenizer verionTokenizer = new StringTokenizer(version, "v.");
        while(newTokenizer.hasMoreElements()){
            if (!verionTokenizer.hasMoreElements()) return true;
            //int i = Integer.parseInt( s );
            int i = Integer.parseInt(newTokenizer.nextToken()); //新版本号分割字符
            int j = Integer.parseInt(verionTokenizer.nextToken()); //当前版本号分割字符
            //System.out.println("i,j:"+i+" "+j);
            if (i>j) return true;
            if(i<j) return false;
        }
        return false;
    }

    public static void main(String[] args) {
        String newVersion = "v2.5.0";
        System.out.println(isNeedUpdate(newVersion));
    }

}
