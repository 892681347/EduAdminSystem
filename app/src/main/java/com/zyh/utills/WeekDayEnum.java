package com.zyh.utills;

/**
 * @author zsw
 * @date 2019/11/19 20:14
 */
public enum WeekDayEnum {
    Mon(1, "星期一"),
    Tus(2, "星期二"),
    Wen(3, "星期三"),
    Thu(4, "星期四"),
    Fri(5, "星期五"),
    Sat(6, "星期六"),
    Sun(7, "星期日");

    private Integer weekDayId;
    private String weekDayStr;
    
    public int getWeekDayId() {
        return weekDayId;
    }

    public String getWeekDayStr() {
        return weekDayStr;
    }

    WeekDayEnum(int weekDayId, String weekDayStr) {
        this.weekDayId = weekDayId;
        this.weekDayStr = weekDayStr;
    }

    public static Integer getWeekIdByStr(String weekDayStr){
        for(int i = 0;i < values().length;i++){
            if(values()[i].weekDayStr.equals(weekDayStr)){
                return values()[i].weekDayId;
            }
        }
        return null;
    }

}
