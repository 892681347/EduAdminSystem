package com.zyh.beans;

import java.util.List;

import lombok.Data;
@Data
public class PscjBean {
    private String code;
    private String msg;
    private Datas data;
    @Data
    public class Datas{
        private String pscj;
        private String pscjBL;
        private String qmcj;
        private String qmcjBL;
        private String qzcj;
        private String qzcjBL;
    }
}
