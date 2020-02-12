package com.zyh.beans;

import android.widget.TextView;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Course {
    private TextView courseName;
    private TextView courseAddress;
    private TextView courseProperty;
}
