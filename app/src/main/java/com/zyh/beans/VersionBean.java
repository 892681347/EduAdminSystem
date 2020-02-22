package com.zyh.beans;

import lombok.Data;

@Data
public class VersionBean {
    private String code;
    private String msg;
    private VersionInfo data;
    @Data
    public class VersionInfo{
        private String versionId;
        private String versionName;
        private String apkPath;
        private String info;
        private String updateTime;
    }
}
