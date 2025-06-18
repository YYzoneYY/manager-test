package com.ruoyi.system.domain.dto;

import com.ruoyi.system.domain.Entity.GeologyDrillEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/6/17
 * @description:
 */
@Data
public class GeologyDrillInfoDTO extends GeologyDrillEntity {

    @ApiModelProperty("地质钻孔属性信息")
    private List<DrillPropertiesDTO> drillPropertiesDTOS;
}