package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/1/16
 * @description:
 */

@Data
public class ProfessionDTO {

    @ApiModelProperty("工种名称")
    private String professionName;

    @ApiModelProperty("工种值")
    private String professionValue;

    @ApiModelProperty("人员信息")
    private List<StaffDTO> staffDTOS;
}