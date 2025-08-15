package com.ruoyi.system.domain.dto;

import com.ruoyi.system.domain.Entity.RoofAbscissionEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: shikai
 * @date: 2025/8/15
 * @description:
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class RoofAbscissionDTO extends RoofAbscissionEntity {

    @ApiModelProperty(value = "预警方案相关信息")
    private WarnSchemeDTO warnSchemeDTO;
}