package com.zyh.beans;

import lombok.Data;

@Data
public class MessageBean {
    private String content;
    private String conversationId;
    private long createTime;
    private int fromId;
    private int status;
    private int toId;
}
