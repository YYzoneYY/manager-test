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
    //启用
    public static final String ENABLE = "0";
    //禁用
    public static final String DISABLE = "1";

    //未删除(逻辑)
    public static final String ZERO_DEL_FLAG = "0";
    //已删除(逻辑)
    public static final String TWO_DEL_FLAG = "2";

    // 测点增加标识
    public static final String MANUALLY_ADD = "1"; //手动新增
    public static final String AUTOMATIC_ACCESS = "2"; //自动接入

    // 测点编码初始值
    public static final String SUPPORT_RESISTANCE_INITIAL_VALUE = "1100MN11010001"; // 支架阻力测点初始值
    public static final String Drill_Stress_INITIAL_VALUE = "1100MN12010001"; // 钻孔应力测点初始值
    public static final String ANCHOR_STRESS_INITIAL_VALUE = "1100MN13010001"; // 锚杆应力测点初始值
    public static final String ANCHOR_CABLE_STRESS_INITIAL_VALUE = "1100MN13020001"; // 锚杆应力测点初始值

    public static final String ANCHOR_STRESS_TYPE = "1301";
    public static final String ANCHOR_CABLE_STRESS_TYPE = "1302";

    // 预警配置类型
    public static final String THRESHOLD_CONFIG = "thresholdConfig"; // 预警阈值配置
    public static final String INCREMENT_CONFIG = "incrementConfig"; // 预警增量配置
    public static final String GROWTH_RATE_CONFIG = "growthRateConfig"; // 预警增速配置

    // 施工文档标识
    public static final String ONE_TAG = "1";
    public static final String TWO_TAG = "2";
    public static final String THREE_TAG = "3";

    public static final String SECTION_SHAPE_DICT_TYPE = "section_shape";
    public static final String SUPPORT_FORM_DICT_TYPE = "support_form";
    public static final String AUDIT_STATUS_DICT_TYPE = "audit_status";


    public static final String WORKLOAD = "工作量";
    public static final String DISTANCE = "距离";
    public static final String WORKLOAD_DISTANCE = "工作量,距离";

    public static final String UPPER_TUNNEL = "SH";
    public static final String BELOW_TUNNEL = "XH";
    public static final String OPEN_OFF_CUT = "QY";
    public static final String OTHER = "other";

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

    public static final String Year_PLAN = "1"; // 年计划
    public static final String Month_PLAN = "2"; // 月计划
    public static final String TEMPORARY_PLAN = "3"; // 临时计划
    public static final String SPECIAL_PLAN = "4"; // 特殊计划

    // 类型 -掘进
    public static final String TUNNELING = "1";
    // 类型 - 回采
    public static final String STOPE = "2";

    public static final String ONE_TYPE = "1";
    public static final String TWO_TYPE = "2";
    public static final String THREE_TYPE = "3";
    public static final String FOUR_TYPE = "4";

    // 审核类型
    public static final String TEAM_AUDIT = "1"; // 区队审核
    public static final String DEPART_AUDIT = "2"; // 科室审核

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
