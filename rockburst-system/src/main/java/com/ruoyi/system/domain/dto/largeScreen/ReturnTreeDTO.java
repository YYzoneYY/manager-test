package com.ruoyi.system.domain.dto.largeScreen;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/6/9
 * @description:
 */

@Data
public class ReturnTreeDTO {

    @ApiModelProperty("工作面id")
    private Long workFaceId;

    @ApiModelProperty("工作面名称")
    private String workFaceName;

    private List<TunnelReturnDTO> tunnelReturnDTOS;
}