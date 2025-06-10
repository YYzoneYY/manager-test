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
public class TunnelReturnDTO {

    @ApiModelProperty("巷道id")
    private Long tunnelId;

    @ApiModelProperty("巷道名称")
    private String tunnelName;

    @ApiModelProperty("回采工程")
    private List<ProjectDataDTO> miningProjects;

    @ApiModelProperty("掘进工程")
    private List<ProjectDataDTO> excavationProjects;
}