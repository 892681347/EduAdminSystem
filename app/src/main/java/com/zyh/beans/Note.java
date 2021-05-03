package com.zyh.beans;

import org.litepal.crud.LitePalSupport;

import lombok.Data;

@Data
public class Note extends LitePalSupport {
    private String username;
    private String name;
    private String semester;
    private String week;
    private int dayInWeek;
    private int time;
}
