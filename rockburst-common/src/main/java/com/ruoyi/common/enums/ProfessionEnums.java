package com.ruoyi.common.enums;

import lombok.Getter;

/**
 * @author: shiKai
 * @date: 2024/11/16 16:58
 * @description: 说明：工种
 */
@Getter
public enum ProfessionEnums {

    Follow_TEAM_LEADER("1", "跟班队长"),
    TECHNICIAN("2", "技术员"),
    CONSTRUCT_WORKER("3", "施工员"),
    SECURITY_INSPECTOR("4", "安检员"),
    BLASTER("5", "爆破员");

    private final String code;
    private final String info;


    ProfessionEnums(String code, String info) {
        this.code = code;
        this.info = info;
    }

    public static String getInfo(String code) {
        ProfessionEnums[] ProfessionEnums = values();
        for (ProfessionEnums professionEnums: ProfessionEnums) {
            if (professionEnums.code.equals(code)) {
                return professionEnums.info;
            }
        }
        return null;
    }

    public static String getCode(String info) {
        ProfessionEnums[] ProfessionEnums = values();
        for (ProfessionEnums professionEnums : ProfessionEnums) {
            if (professionEnums.info.equals(info)) {
                return professionEnums.code;
            }
        }
        return null;
    }
}
