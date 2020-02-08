package com.zyh.beans;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginBean implements Serializable {
    private String code;
    private Datas data;

    @Data
    public class Datas implements Serializable {
        private String cookie;
        private String token;
        private String nowXueqi;
        private StuInfo stuInfo;
        private String nowDate;
        private String nowWeek;
        private String totalWeek;
        @Data
        public class StuInfo implements Serializable {
            private String id;
            private String name;
            private String stuId;
            private String college;
            private String major;
            private String className;
        }
    }
}
