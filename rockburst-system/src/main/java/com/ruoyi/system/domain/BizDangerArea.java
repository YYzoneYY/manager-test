package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.github.yulichang.annotation.EntityMapping;
import com.ruoyi.common.core.domain.BaseSelfEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 矿井危险区对象 BizDangerArea
 *
 * @author ruoyi
 * @date 2024-11-11
 */

@Getter
@Setter
@Accessors(chain = true)
@ApiModel("矿井危险区对象")
public class BizDangerArea extends BaseSelfEntity
{
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long dangerAreaId;

    @ApiModelProperty(value = "序号")
    private Integer no;


    @ApiModelProperty(value = "名字")
    private String name;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "等级")
    private String level;

    @ApiModelProperty(value = "工作面id")
    private Long workfaceId;

    @ApiModelProperty(value = "巷道id")
    private Long tunnelId;

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

    @ApiModelProperty(value = "地图位置")
    private String svg;

    @ApiModelProperty(value = "导线点集合")
    private String pointlist;


    @ApiModelProperty(value = "开始导线点")
    private Long startPointId;

    @ApiModelProperty(value = "结束导线点")
    private Long endPointId;

    @ApiModelProperty(value = "开始导线点前后距离")
    private Double startMeter;

    @ApiModelProperty(value = "结束导线点前后距离")
    private Double endMeter;


    @ApiModelProperty(value = "原始位置")
    private String source;

    @ApiModelProperty(value = "中心点")
    @TableField()
    private String center;

    @ApiModelProperty(value = "间隔距离")
    @TableField(exist = false)
    @EntityMapping(thisField = "level" , joinField = "level")
    private BizDangerLevel dangerLevel;


}
