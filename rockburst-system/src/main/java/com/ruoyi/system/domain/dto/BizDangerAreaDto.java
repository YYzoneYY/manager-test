package com.ruoyi.system.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.github.yulichang.annotation.EntityMapping;
import com.ruoyi.system.domain.BizDangerLevel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 矿井管理对象 biz_mine
 *
 * @author ruoyi
 * @date 2024-11-11
 */

@Getter
@Setter
@Accessors(chain = true)
public class BizDangerAreaDto
{

    @TableId(type = IdType.AUTO)
    private Long dangerAreaId;

    @ApiModelProperty(value = "等级",required = true)
    private String level;

    @ApiModelProperty(value = "工作面id",required = true)
    private Long workfaceId;

    @ApiModelProperty(value = "工作面name")
    private String workfaceName;

    @ApiModelProperty(value = "状态")
    private Integer status;


    @ApiModelProperty(value = "名字")
    private String name;



    @ApiModelProperty(value = "巷道id",required = true)
    private Long tunnelId;

    @ApiModelProperty(value = "开始导线点",required = true)
    private Long startPointId;

    @ApiModelProperty(value = "结束导线点",required = true)
    private Long endPointId;

    @ApiModelProperty(value = "开始导线点前后距离",required = true)
    private Double startMeter;

    @ApiModelProperty(value = "结束导线点前后距离",required = true)
    private Double endMeter;

    @ApiModelProperty(value = "地图位置")
    private String svg;

    @ApiModelProperty(value = "原始位置")
    private String source;


    @ApiModelProperty(value = "序号")
    private Integer no;


    @ApiModelProperty(value = "非生产帮开始导线点x")
    private String fscbStartx;

    @ApiModelProperty(value = "非生产帮开始导线点y")
    private String fscbStarty;

    @ApiModelProperty(value = "非生产帮结束导线点x")
    private String fscbEndx;

    @ApiModelProperty(value = "非生产帮结束导线点y")
    private String fscbEndy;


    @ApiModelProperty(value = "生产帮开始导线点x")
    private String scbStartx;

    @ApiModelProperty(value = "生产帮开始导线点y")
    private String scbStarty;

    @ApiModelProperty(value = "生产帮结束导线点x")
    private String scbEndx;

    @ApiModelProperty(value = "生产帮结束导线点y")
    private String scbEndy;


    @ApiModelProperty(value = "导线点集合")
    private String pointList;



    @ApiModelProperty(value = "中心点")
    @TableField()
    private String center;

    @ApiModelProperty(value = "间隔距离")
    @TableField(exist = false)
    @EntityMapping(thisField = "level" , joinField = "level")
    private BizDangerLevel dangerLevel;




}
