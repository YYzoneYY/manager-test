package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class BizTunnelBar extends BaseSelfEntity
{
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long barId;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "名称")
    private String barName;

    @ApiModelProperty(value = "工作面id")
    private Long workfaceId;

    @ApiModelProperty(value = "巷道id")
    private Long tunnelId;

    @ApiModelProperty(value = "开始导线点")
    private Long startPointId;

    @ApiModelProperty(value = "结束导线点")
    private Long endPointId;

    @ApiModelProperty(value = "开始导线点前后距离")
    private Double startMeter;

    @ApiModelProperty(value = "结束导线点前后距离")
    private Double endMeter;


    @ApiModelProperty(value = "开始导线点前后距离")
    private String startx;


    @ApiModelProperty(value = "开始导线点前后距离")
    private String starty;

    @ApiModelProperty(value = "结束导线点前后距离")
    private String endx;


    @ApiModelProperty(value = "结束导线点前后距离")
    private String endy;


    @ApiModelProperty(value = "预设孔方向角度")
    private Integer directAngle;

    @ApiModelProperty(value = "迎头孔方向角度")
    private Integer ytAngle;

    @ApiModelProperty(value = "钻孔孔方向距离")
    private Double directRange;

//    @ApiModelProperty(value = "迎头孔方向距离")
//    private Double prpo;

//    @ApiModelProperty(value = "预设孔方向距离")
//    private Double directRangePre;

    @ApiModelProperty(value = "svg")
    @TableField()
    private String svg;

    @ApiModelProperty(value = "a")
    @TableField()
    private Double a;

    @ApiModelProperty(value = "b")
    @TableField()
    private Double b;

    @ApiModelProperty(value = "c")
    @TableField()
    private Double c;

    @ApiModelProperty(value = "中心点")
    @TableField()
    private String center;

    @ApiModelProperty(value = "原始位置")
    private String source;

    @ApiModelProperty(value = "巷道走向")
    @TableField()
    private Double towardAngle;


}
