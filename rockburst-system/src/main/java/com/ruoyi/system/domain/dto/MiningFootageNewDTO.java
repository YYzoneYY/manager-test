package com.ruoyi.system.domain.dto;

import com.ruoyi.system.domain.Entity.MiningEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2025/2/24
 * @description:
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class MiningFootageNewDTO extends MiningEntity {

    @ApiModelProperty("回采累进进度")
    private BigDecimal miningPaceSum;

    @ApiModelProperty("修改回采进度")
    private BigDecimal miningPaceEdit;

    //1 代表有时间相同的，空代表没有时间相同的
    @ApiModelProperty("开采时间相同的数据 1:代表有时间相同的，空(null):代表没有时间相同的")
    private String timeFlag;
}