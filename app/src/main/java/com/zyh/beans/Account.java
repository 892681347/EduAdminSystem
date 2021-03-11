package com.zyh.beans;

import android.content.Intent;

import org.litepal.crud.LitePalSupport;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Account extends LitePalSupport {

    private String username;
    private String password;
    private String isLast;
}
