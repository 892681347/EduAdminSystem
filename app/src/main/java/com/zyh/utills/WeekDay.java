package com.zyh.utills;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * @author zsw
 * @date 2019/11/19 20:28
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WeekDay implements Comparable<WeekDay> {
    private Integer weekId;
    private String weekMonStr;


    @Override
    public int compareTo(@NotNull WeekDay o) {
        return this.getWeekId() - o.getWeekId();
    }
}
