package com.ruoyi.common.utils;


/**
 * 通用常量信息
 *
 * @author tsit
 */
public class ConstantsInfo {
    /**
     * 数字标识 没有实际意义，需在编写时赋予含义
     */
    public static final String NEGATIVE_ONE_SIGN = "-1";
    //未绑定
    public static final String ZERO_STATUS = "0";
    //已绑定
    public static final String ONE_STATUS = "1";

    //未删除(逻辑)
    public static final Integer ZERO_DEL_FLAG = 0;
    //已删除(逻辑)
    public static final Integer TWO_DEL_FLAG = 2;

    // 工作面下拉框标识
    public static final String TAG = "1";

    // 不明确是否分层
    public static final String UNKNOWN = "0";

    // 分层
    public static final String LAMINATION = "1";

    // 不分层
    public static final String NOLAYERING = "2";

    // 是煤层
    public static final String YES = "1";

    // 不是煤层
    public static final String No = "0";

    // 边界开采情况 未开采
    public static final String UNMINED = "0";

    // 边界开采情况 已开采
    public static final String MINED = "1";

    //只取200条监测数据
    public static final  int SUB = 200;

    //只取100条微震数据
    public static final  int INTERCEPT_MIC = 100;
}
