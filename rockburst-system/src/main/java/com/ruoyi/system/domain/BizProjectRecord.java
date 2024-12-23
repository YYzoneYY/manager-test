package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.yulichang.annotation.EntityMapping;
import com.github.yulichang.annotation.FieldMapping;
import com.ruoyi.common.core.domain.BaseSelfEntity;
import com.ruoyi.system.domain.Entity.ClassesEntity;
import com.ruoyi.system.domain.Entity.ConstructionPersonnelEntity;
import com.ruoyi.system.domain.Entity.ConstructionUnitEntity;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import java.util.Date;

/**
 * 工程填报记录对象 biz_project_record
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Setter
@Getter
@FieldNameConstants
@Accessors(chain = true)
@TableName("biz_project_record")
public class BizProjectRecord extends BaseSelfEntity
{
    private static final long serialVersionUID = 1L;

    /** 工程id */
    @TableId( type = IdType.AUTO)
    private Long projectId;

    /** 导线点 */
    @ApiModelProperty(value = "导线点")
    private Long travePointId;

    @ApiModelProperty(value = "钻孔类型")
    private String drillType;

    @ApiModelProperty(value = "计划Id")
    private Long planId;

    /** 距离 */
    @ApiModelProperty(value = "距离")
    private String constructRange;

    @ApiModelProperty(value = "施工类型 回踩 掘进")
    private String constructType;

    /** 施工时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "施工时间")
    private Date constructTime;

    /** 施工单位 */
    @ApiModelProperty(value = "施工单位")
    private Long constructUnitId;

    /** 施工班次 */
    @ApiModelProperty(value = "施工班次")
    private Long constructShiftId;

    /** 施工地点 */
    @ApiModelProperty(value = "工作面id")
    private Long workfaceId;


    /** 施工地点 */
    @ApiModelProperty(value = "巷道id")
    private Long tunnelId;

    /** 施工地点 */
    @ApiModelProperty(value = "施工地点(传入工作面id或者巷道id)")
    private Long locationId;



    /** 定位方式 */
    @ApiModelProperty(value = "定位方式")
    private String positionType;

    /** 钢带起始 */
    @ApiModelProperty(value = "钢带起始")
    private String steelBeltStart;



    /** 钢带终止 */
    @ApiModelProperty(value = "钢带终止")
    private String steelBeltEnd;

    /** 钻孔编号 */
    @ApiModelProperty(value = "钻孔编号")
    private String drillNum;


    /** 状态 */
    @ApiModelProperty(value = "状态")
    private Integer status;

    /** 施工负责人 */
    @ApiModelProperty(value = "施工负责人")
    private Long projecrHeader;

    /** 施工员 */
    @ApiModelProperty(value = "施工员")
    private Long worker;

    /** 安检员 */
    @ApiModelProperty(value = "爆破员")
    private Long bigbanger;

    /** 安检员 */
    @ApiModelProperty(value = "安检员")
    private Long securityer;

    /** 安检员 */
    @ApiModelProperty(value = "验收员")
    private Long accepter;

    /** 验收视频源文件 */
    @ApiModelProperty(value = "验收视频源文件")
    private String originalFile;

//    /** 进钻参数 */
//    @ApiModelProperty(value = "进钻参数")
//    private String drillParam;

//    /** 钻屑量 */
//    @ApiModelProperty(value = "钻屑量")
//    private String crumbWeight;

    /** 钻屑量 */
    @ApiModelProperty(value = "钻屑量")
    private Long deptId;


//    @ApiModelProperty(value = "工作面id")
//    private Long workfaceId;

    @ApiModelProperty(value = "阅读状态")
    private Integer isRead;


    @ApiModelProperty(value = "工作面")
    @TableField(exist = false)
    @FieldMapping(tag = BizWorkface.class ,thisField = "workfaceId" , joinField = "workfaceId" , select = "workfaceName")
    private String workfaceName;

    @ApiModelProperty(value = "巷道")
    @TableField(exist = false)
    @FieldMapping(tag = TunnelEntity.class ,thisField = "tunnelId" , joinField = "tunnelId" , select = "tunnelName")
    private String tunnelName;


    @ApiModelProperty(value = "施工单位实例")
    @TableField(exist = false)
    @EntityMapping(thisField = "constructUnitId", joinField = "constructionUnitId")
    private ConstructionUnitEntity constructionUnit;

    @ApiModelProperty(value = "施工班次实例")
    @TableField(exist = false)
    @EntityMapping(thisField = "constructShiftId", joinField = "classesId")
    private ClassesEntity constructShift;


    /** 导线点 */
    @ApiModelProperty(value = "导线点实例")
    @TableField(exist = false)
    @EntityMapping(thisField = "travePointId", joinField = "pointId")
    private BizTravePoint travePoint;

    /** 施工负责人 */
    @ApiModelProperty(value = "施工负责人实例")
    @TableField(exist = false)
    @EntityMapping(thisField = "projecrHeader", joinField = "constructionPersonnelId")
    private ConstructionPersonnelEntity projecrHeaderEntity;

    /** 施工员 */
    @ApiModelProperty(value = "施工员实例")
    @TableField(exist = false)
    @EntityMapping(thisField = "worker", joinField = "constructionPersonnelId")
    private ConstructionPersonnelEntity workerEntity;

    /** 安检员 */
    @ApiModelProperty(value = "爆破员实例")
    @TableField(exist = false)
    @EntityMapping(thisField = "bigbanger", joinField = "constructionPersonnelId")
    private ConstructionPersonnelEntity bigbangerEntity;

    /** 安检员 */
    @ApiModelProperty(value = "安检员实例")
    @TableField(exist = false)
    @EntityMapping(thisField = "securityer", joinField = "constructionPersonnelId")
    private ConstructionPersonnelEntity securityerEntity;

    /** 安检员 */
    @ApiModelProperty(value = "验收员实例")
    @TableField(exist = false)
    @EntityMapping(thisField = "accepter", joinField = "constructionPersonnelId")
    private ConstructionPersonnelEntity accepterEntity;



}
