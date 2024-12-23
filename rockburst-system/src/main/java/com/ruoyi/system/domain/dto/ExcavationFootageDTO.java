package com.ruoyi.system.domain.dto;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.Entity.ExcavationFootageEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2024/12/23
 * @description:
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class ExcavationFootageDTO extends ExcavationFootageEntity {

    @ApiModelProperty("掘进累进进度")
    private BigDecimal excavationPaceSum;

    @ApiModelProperty("修改掘进进度")
    private BigDecimal excavationPaceEdit;

    //1 代表有时间相同的，空代表没有时间相同的
    @ApiModelProperty("掘进时间相同的数据 1:代表有时间相同的，空(null):代表没有时间相同的")
    private String timeFlag;

    @ApiModelProperty("已掘进长度")
    private BigDecimal excavatedLength;
}