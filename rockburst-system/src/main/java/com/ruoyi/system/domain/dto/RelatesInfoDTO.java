package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: shikai
 * @date: 2024/12/11
 * @description:
 */

@Data
public class RelatesInfoDTO {

    @ApiModelProperty(value = "位置id")
    private Long positionId;

    @ApiModelProperty(value = "钻孔数")
    private Integer drillNumber;

    @ApiModelProperty(value = "孔深")
    private BigDecimal holeDepth;

    @ApiModelProperty(value = "间距")
    private BigDecimal spacing;

    @ApiModelProperty(value = "区域集合")
    private List<AreaDTO> areaDTOS;

    @ApiModelProperty(value = "位置格式化")
    private String positionFmt;
}