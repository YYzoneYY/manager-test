package com.ruoyi.system.domain.vo;

import com.ruoyi.system.domain.Entity.ConstructionUnitEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2024/11/15
 * @description:
 */
@Data
public class ConstructionUnitVO extends ConstructionUnitEntity {

    @ApiModelProperty(value = "创建时间")
    private String createTimeFmt;

    @ApiModelProperty(value = "修改时间")
    private String updateTimeFmt;
}