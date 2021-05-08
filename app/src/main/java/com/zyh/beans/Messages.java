package com.zyh.beans;

import org.litepal.crud.LitePalSupport;

import lombok.Data;

@Data
public class Messages extends LitePalSupport {
    private String content;
    private int formId;
    private String time;
    private boolean read;
}
