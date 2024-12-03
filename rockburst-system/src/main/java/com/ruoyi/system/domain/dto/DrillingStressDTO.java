package com.ruoyi.system.domain.dto;

import com.ruoyi.system.domain.Entity.DrillingStressEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: shikai
 * @date: 2024/12/3
 * @description:
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class DrillingStressDTO extends DrillingStressEntity {

    @ApiModelProperty(value = "预警方案相关信息")
    private WarnSchemeDTO warnSchemeDTO;
}