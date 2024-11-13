package com.ruoyi.system.domain.dto;

import com.ruoyi.system.domain.Entity.MiningRecordEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2024/11/13
 * @description:
 */
@Data
public class MiningRecordDTO extends MiningRecordEntity {

    @ApiModelProperty("修改类型")
    private String type;

    @ApiModelProperty("回采时间戳")
    private String miningTimeFrm;

    @ApiModelProperty(value = "更新时间戳")
    private String updateTimeFrm;

    @ApiModelProperty(value = "用户名称")
    private String createByFmt;
}