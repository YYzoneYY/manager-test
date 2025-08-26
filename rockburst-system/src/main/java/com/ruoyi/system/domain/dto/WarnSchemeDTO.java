package com.ruoyi.system.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
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

    @ApiModelProperty("预警方案id")
    private Long warnSchemeId;

    @ApiModelProperty("预警方案名称")
    private String warnSchemeName;

    @ApiModelProperty("场景类型")
    private String sceneType;

    @ApiModelProperty("标志")
    private String mark;

    @ApiModelProperty("工作面id")
    private Long workFaceId;

    @ApiModelProperty("静默时间")
    private String quietHour;

    @ApiModelProperty(value = "预警阈值配置")
    private List<ThresholdConfigDTO> thresholdConfigDTOS;

    @ApiModelProperty(value = "预警增量配置")
    private List<IncrementConfigDTO> incrementConfigDTOS;

    @ApiModelProperty(value = "预警增速配置")
    private List<GrowthRateConfigDTO> growthRateConfigDTOS;

    @ApiModelProperty("状态")
    private String status;

}