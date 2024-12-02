package com.ruoyi.system.domain.dto;

import com.ruoyi.system.domain.Entity.SupportResistanceEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * @author: shikai
 * @date: 2024/11/28
 * @description:
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class SupportResistanceDTO extends SupportResistanceEntity {

    @ApiModelProperty(value = "传感器编号")
    private String sensorNum;

    @ApiModelProperty(value = "预警方案相关信息")
    private WarnSchemeDTO warnSchemeDTO;
}