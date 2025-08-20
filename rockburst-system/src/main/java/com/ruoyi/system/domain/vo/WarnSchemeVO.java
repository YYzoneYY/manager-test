package com.ruoyi.system.domain.vo;

import com.ruoyi.system.domain.dto.GrowthRateConfigDTO;
import com.ruoyi.system.domain.dto.IncrementConfigDTO;
import com.ruoyi.system.domain.dto.ThresholdConfigDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author: shikai
 * @date: 2025/8/11
 * @description:
 */

@Data
public class WarnSchemeVO {

    @ApiModelProperty("预警方案id")
    private Long warnSchemeId;

    @ApiModelProperty("预警方案名称")
    private String schemeName;

    @ApiModelProperty("场景类型")
    private String sceneType;

    @ApiModelProperty("工作面id")
    private Long workFaceId;

    private List<Map<String, Object>> thresholdConfig;

    private List<Map<String, Object>> incrementConfig;

    private List<Map<String, Object>> growthRateConfig;

    @ApiModelProperty(value = "预警阈值配置")
    private List<ThresholdConfigDTO> thresholdConfigDTOS;

    @ApiModelProperty(value = "预警增量配置")
    private List<IncrementConfigDTO> incrementConfigDTOS;

    @ApiModelProperty(value = "预警增速配置")
    private List<GrowthRateConfigDTO> growthRateConfigDTOS;

    @ApiModelProperty("增量/速计算H")
    private String duration;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("所属工作面名称")
    private String workFaceNameFmt;
}