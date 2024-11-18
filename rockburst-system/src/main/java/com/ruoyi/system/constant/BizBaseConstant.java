package com.ruoyi.system.constant;

import lombok.Data;

public class BizBaseConstant {

    public static final String DELFLAG_N = "0";
    public static final String DELFLAG_Y = "2";

    // 矿井 营运状态
    public static final Integer MINE_STATUS_ON = 1;
    // 矿井 停止营运状态
    public static final Integer MINE_STATUS_OFF = 0;


    //填报类型

    public static final String FILL_TYPE_CD = "CD";
    public static final String FILL_TYPE_LDPR = "LDPR";
    public static final String FILL_TYPE_PRB= "PRB";
    public static final String FILL_TYPE_RD = "RD";
    public static final String FILL_TYPE_FPR = "FPR";
    public static final String FILL_TYPE_LDF = "LDF";
    public static final String FILL_TYPE_RHF = "RHF";


    //填报状态
    public static final Integer FILL_STATUS_PEND = 0;
    public static final Integer FILL_STATUS_TEAM_PASS = 1;
    public static final Integer FILL_STATUS_DEPART_PASS = 2;
    public static final Integer FILL_STATUS_TEAM_BACK = 3;
    public static final Integer FILL_STATUS_DEPART_BACK = 4;



}
