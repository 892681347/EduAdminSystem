package com.zyh.beans;

import java.util.List;

import lombok.Data;

@Data
public class GradeBean {
    private String code;
    private String msg;
    private List<Datas> data;
    @Data
    public class Datas{
        private String xueqi;
        private String courseName;
        private String score;
        private String type;
        private String xuefen;
        private String point;
        private String method;
        private String property;
        private String nature;
    }
}
