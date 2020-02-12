package com.zyh.beans;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class CourseBean {
    private String code;
    private String msg;
    private List<List<Course>> data;

    @Data
    @AllArgsConstructor
    public class Course{
        private String courseName;
        private String teacher;
        private String time;
        private String address;
    }
}
