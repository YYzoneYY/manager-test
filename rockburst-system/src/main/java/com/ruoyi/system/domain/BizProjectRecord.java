package com.ruoyi.system.domain;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 工程填报记录对象 biz_project_record
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Getter
@Setter
public class BizProjectRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 工程id */

    private Long projectId;

    /** 施工时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "施工时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date constructTime;

    /** 施工单位 */
    @Excel(name = "施工单位")
    private String constructUnitId;

    /** 施工班次 */
    @Excel(name = "施工班次")
    private String constructShiftId;

    /** 距离 */
    @Excel(name = "距离")
    private String constructRange;

    /** 施工地点 */
    @Excel(name = "施工地点")
    private String constructLocationId;

    /** 定位方式 */
    @Excel(name = "定位方式")
    private String positionType;

    /** 钢带起始 */
    @Excel(name = "钢带起始")
    private String steelBeltStart;

    /** 导线点 */
    @Excel(name = "导线点")
    private String travePointId;

    /** 钢带终止 */
    @Excel(name = "钢带终止")
    private String steelBeltEnd;

    /** 钻孔编号 */
    @Excel(name = "钻孔编号")
    private String drillNum;

    /** 位置 */
    @Excel(name = "位置")
    private String location;

    /** 状态 */
    @Excel(name = "状态")
    private Integer status;

    /** 施工负责人 */
    @Excel(name = "施工负责人")
    private String projecrHeader;

    /** 施工员 */
    @Excel(name = "施工员")
    private String worker;

    /** 安检员 */
    @Excel(name = "安检员")
    private String securityer;

    /** 验收视频源文件 */
    @Excel(name = "验收视频源文件")
    private String originalFile;

    /** 进钻参数 */
    @Excel(name = "进钻参数")
    private String drillParam;

    /** 钻屑量 */
    @Excel(name = "钻屑量")
    private String crumbWeight;

    /** 钻屑量 */
//    @Excel(name = "钻屑量")
    private Long deptId;
}
