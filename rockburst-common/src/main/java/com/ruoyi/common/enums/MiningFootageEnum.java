package com.ruoyi.common.enums;

/**
 * 手动回采进尺枚举类
 */

public enum MiningFootageEnum {

    NORMAL("0","正常数据"),
    Not_FILLED_IN("1","未填写"),
    REVISE("2","修改"),
    ERASE("3","擦除"),
    SAME_TIME("4","时间相同"),
    DIFFERENT_TIME("5","时间不同");
    private String index;

    private String name;

    private MiningFootageEnum(String index, String name){
        this.index=index;
        this.name =name;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }
}
