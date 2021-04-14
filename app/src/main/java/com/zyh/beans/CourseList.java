package com.zyh.beans;


import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class CourseList extends LitePalSupport {
    private String username;
    private String semester;
    public List<String> courseResponseDatas;
}
