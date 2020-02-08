package com.zyh.beans;

import java.util.List;

import lombok.Data;

@Data
public class ExamBean {
    private String code;
    private String msg;
    private List<Exam> data;
    @Data
    public class Exam{
        private String campus;
        private String courseName;
        private String teacher;
        private String startTime;
        private String endTime;
        private String address;
        private String seatNumber;
        private String ticketNumber;
    }
}
