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
    public static final String ZERO_DEL_FLAG = "0";
    //已删除(逻辑)
    public static final String TWO_DEL_FLAG = "2";

    // 测点增加标识
    public static final String MANUALLY_ADD = "1"; //手动新增
    public static final String AUTOMATIC_ACCESS = "2"; //自动接入

    // 测点编码初始值
    public static final String INITIAL_VALUE = "1100MN11010001";

    // 预警配置类型
    public static final String THRESHOLD_CONFIG = "thresholdConfig"; // 预警阈值配置
    public static final String INCREMENT_CONFIG = "incrementConfig"; // 预警增量配置
    public static final String GROWTH_RATE_CONFIG = "growthRateConfig"; // 预警增速配置

    // 施工文档标识
    public static final String ONE_TAG = "1";
    public static final String TWO_TAG = "2";

    public static final String SECTION_SHAPE_DICT_TYPE = "section_shape";
    public static final String SUPPORT_FORM_DICT_TYPE = "support_form";
    public static final String AUDIT_STATUS_DICT_TYPE = "audit_status";

    public static final Integer LEVEL = 1;

    public static final Integer NUMBER = 1;

    // 审核状态-待提交
    public static final String TO_BE_SUBMITTED = "0";
    // 审核状态-待审核
    public static final String AUDIT_STATUS_DICT_VALUE = "1";
    // 审核状态-审核中
    public static final String IN_REVIEW_DICT_VALUE = "2";
    // 审核状态-已审核
    public static final String AUDITED_DICT_VALUE = "3";
    // 审核状态-已驳回
    public static final String REJECTED = "4";

    // 审核结果
    public static final String AUDIT_SUCCESS = "1";
    public static final String AUDIT_REJECT = "2";

    // 类型 -掘进
    public static final String TUNNELING = "1";
    // 类型 - 回采
    public static final String STOPE = "2";

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
