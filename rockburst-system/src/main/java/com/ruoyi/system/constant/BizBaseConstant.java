package com.ruoyi.system.constant;

public class BizBaseConstant {

    public static final String DELFLAG_N = "0";
    public static final String DELFLAG_Y = "2";

    /*
    回采 掘进类型
     */
    public static final String CONSTRUCT_TYPE_H = "2";
    public static final String CONSTRUCT_TYPE_J = "1";


    /*
    巷道分类
     */
    public static final String TUNNEL_SH = "SH";
    public static final String TUNNEL_XH = "XH";
    public static final String TUNNEL_QY = "QY";


    // 矿井 营运状态
    public static final Integer MINE_STATUS_ON = 1;
    // 矿井 停止营运状态
    public static final Integer MINE_STATUS_OFF = 0;


    // 工作面开采状态 未开采
    public static final Integer WORKFACE_STATUS_WKC = 0;
    // 工作面开采状态 开采中
    public static final Integer WORKFACE_STATUS_KCZ = 1;
    // 工作面开采状态 开采完成
    public static final Integer WORKFACE_STATUS_KCWC = 2;
    // 工作面开采状态 停止开采
    public static final Integer WORKFACE_STATUS_TZKC = 3;
    // 工作面开采状态 安全封闭
    public static final Integer WORKFACE_STATUS_AQFB = 4;

    //填报类型

    public static final String FILL_TYPE_CD = "CD";
    public static final String FILL_TYPE_LDPR = "LDPR";
    public static final String FILL_TYPE_PRB= "PRB";
    public static final String FILL_TYPE_RD = "RD";
    public static final String FILL_TYPE_FPR = "FPR";
    public static final String FILL_TYPE_LDF = "LDF";
    public static final String FILL_TYPE_RHF = "RHF";


    //填报状态
    //初始填报状态
    public static final Integer FILL_STATUS_PEND = 0;
    public static final Integer FILL_STATUS_TEAM_PASS = 1;
    public static final Integer FILL_STATUS_DEPART_PASS = 2;
    public static final Integer FILL_STATUS_TEAM_BACK = 3;
    public static final Integer FILL_STATUS_DEPART_BACK = 4;



}
