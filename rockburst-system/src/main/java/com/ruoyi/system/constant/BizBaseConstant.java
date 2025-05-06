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


    /*
      传感器类型:
        1101-工作面支架阻力
        1201-钻孔应力
        1202-围岩应力
        1301-锚杆应力
        1302-锚索应力
        1401-顶板离层位移
        1501-巷道位移
        1801-电磁强度
        1802-电磁脉冲
    */
    public static final String ZJZL = "1101";
    public static final String ZKYL = "1201";
    public static final String WYYL = "1202";
    public static final String MGYL = "1301";
    public static final String MSYL = "1302";
    public static final String DBLCWY = "1401";
    public static final String HDWY = "1501";
    public static final String DCQD = "1801";
    public static final String DCMC = "1802";

    public static final String getMeasurePre(String preStr,Integer num){

        String numStr = String.format("%04d", num);
        return String.join("1100", "MN", "preStr",numStr );

    }





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
    public static final String FILL_TYPE_CD_YT = "CD_YT";
    public static final String FILL_TYPE_LDPR_YT = "LDPR_YT";
    public static final String FILL_TYPE_PRB_YT= "PRB_YT";


    //填报状态
    //初始填报状态
    /*
    * 待提交   0
    * 区队待审核  1
    * 区队审核中  2
    * 区队通过   3
    * 区队驳回   4
    * 科室审核中 5
    * 科室通过  6
    * 科室驳回  7
    * */
    public static final Integer FILL_STATUS_PEND = 0;
    public static final Integer FILL_STATUS_TEAM_LOAD = 1;
    public static final Integer FILL_STATUS_TEAM_DOING = 2;
    public static final Integer FILL_STATUS_TEAM_OK = 3;
    public static final Integer FILL_STATUS_TEAM_BACK = 4;
    public static final Integer FILL_STATUS_DEART_DOING = 5;
    public static final Integer FILL_STATUS_DEART_OK = 6;
    public static final Integer FILL_STATUS_DEART_BACK = 7;




}
