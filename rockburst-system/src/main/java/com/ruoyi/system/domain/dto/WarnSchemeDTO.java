package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author: shikai
 * @date: 2024/12/2
 * @description:
 */

@Data
public class WarnSchemeDTO {

    @ApiModelProperty(value = "预警方案基础信息")
    private Map<String, Object> warnSchemeBasicInfoMap;

    @ApiModelProperty(value = "预警阈值配置")
    private List<ThresholdConfigDTO> thresholdConfigDTOS;

    @ApiModelProperty(value = "预警增量配置")
    private List<IncrementConfigDTO> incrementConfigDTOS;

    @ApiModelProperty(value = "预警增速配置")
    private List<GrowthRateConfigDTO> growthRateConfigDTOS;

}