package com.ruoyi.system.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class BizTunnelBarDto
{

    @TableId(type = IdType.AUTO)
    private Long barId;

    @ApiModelProperty(value = "类型", required = true)
    private String type;

    @ApiModelProperty(value = "工作面id", required = true)
    private Long workfaceId;

    @ApiModelProperty(value = "巷道id", required = true)
    private Long tunnelId;

    @ApiModelProperty(value = "开始导线点", required = true)
    private Long startPointId;

    @ApiModelProperty(value = "结束导线点", required = true)
    private Long endPointId;

    @ApiModelProperty(value = "开始导线点前后距离", required = true)
    private Double startMeter;

    @ApiModelProperty(value = "结束导线点前后距离", required = true)
    private Double endMeter;

    @ApiModelProperty(value = "地图位置", required = false)
    private String svg;

    @ApiModelProperty(value = "原始位置", required = false)
    private String source;


}
