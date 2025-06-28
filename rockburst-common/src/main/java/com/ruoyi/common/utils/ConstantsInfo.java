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
    public static final String AUDIT_STATUS_DICT_TYPE = "plan_audit_status";
    public static final String PROFESSION_DICT_TYPE = "profession";
    public static final String DRILL_TYPE_DICT_TYPE = "drill_type";
    public static final String TYPE_DICT_TYPE = "type";
    public static final String DRILL_DEVICE_DICT_TYPE = "drill_device";
    public static final String YEAR_DICT_TYPE = "year";
    public static final String PLAN_TYPE_DICT_TYPE = "plan_type";
    public static final String DANGER_AREA_LEVEL_DICT_TYPE = "danger_area_level";


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

    // 填报信息审核标签
    public static final String INITIAL_TAG = "0"; // 初始化标识
    public static final String TEAM_TAG = "1"; // 区队审核
    public static final String DEPT_TAG = "2"; // 科室审核

    // 类型 -掘进
    public static final String TUNNELING = "1";
    // 类型 - 回采
    public static final String STOPE = "2";

    // 施工员
    public static final String CONSTRUCTION_WORKER = "1";
    // 验收员
    public static final String INSPECTOR = "2";

    public static final String ONE_TYPE = "1";
    public static final String TWO_TYPE = "2";
    public static final String THREE_TYPE = "3";
    public static final String FOUR_TYPE = "4";

    // 审核类型
    public static final String TEAM_AUDIT = "1"; // 区队审核
    public static final String DEPART_AUDIT = "2"; // 科室审核

    public static final String ZERO_IDENTIFY_STATUS = "0"; //未识别
    public static final String ONE_IDENTIFY_STATUS = "1"; //已识别

    // 返回数据提示词
    public static final String CUE_WORD_ONE = "号钻孔与";
    public static final String CUE_WORD_TWO = "号钻孔不满足当前卸压计划中";
    public static final String CUE_WORD_THREE = "间距不大于";
    public static final String CUE_WORD_FOUR = "米的要求，发生预警!!";

    // 报警屏蔽状态
    public static final String SHIELD_STATUS = "1"; //屏蔽状态
    public static final String UN_SHIELD_STATUS = "0"; //未屏蔽状态

    public static final String ALARM_IN = "1"; //报警中
    public static final String ALARM_SHIELD_IN = "2"; //报警屏蔽中
    public static final String ALARM_END = "0"; //报警结束

    public static final String QUANTITY_ALARM = "quantity_alarm"; //工程量报警
    public static final String DRILL_SPACE_ALARM = "drill_space_alarm"; //钻孔间距报警

    public static final String ALARM_TYPE = "alarm_type"; // 报警类型
    public static final String ALARM_STATUS = "alarm_status"; // 报警状态
    public static final String HANDLE_STATUS = "handle_status"; // 处理状态

    public static final String UNTREATED = "0"; //未处理
    public static final String PROCESSING = "1"; //处理中
    public static final String TURN_OFF_ALARM = "2"; //关闭报警;

    public static final Long ALARM_SYSTEM = 9999L;
    public static final String REMARKS_SYSTEM = "系统自动关闭报警";

    public static final String TEAM_AUDIT_PUSH = "team";
    public static final String DEPT_AUDIT_PUSH = "dept";
    public static final  String ALARM_PUSH = "alarm_push";


    //只取200条监测数据
    public static final  int SUB = 200;

    //只取100条微震数据
    public static final  int INTERCEPT_MIC = 100;
}
