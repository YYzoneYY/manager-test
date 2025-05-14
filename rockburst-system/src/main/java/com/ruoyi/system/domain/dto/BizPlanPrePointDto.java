package com.ruoyi.system.domain.dto;

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
public class BizPlanPrePointDto
{


    @ApiModelProperty(value = "巷道id")
    private Long tunnelId;

    @ApiModelProperty(value = "开始导线点")
    private Long startPointId;

    @ApiModelProperty(value = "结束导线点")
    private Long endPointId;


    @ApiModelProperty(value = "开始导线点 前后距离")
    private Double startMeter;


    @ApiModelProperty(value = "结束导线点 前后距离")
    private Double endMeter;

    @ApiModelProperty(value = "起始导线点坐标")
    private String startPointCoordinate;

    @ApiModelProperty(value = "终始导线点坐标")
    private String endPointCoordinate;

}
