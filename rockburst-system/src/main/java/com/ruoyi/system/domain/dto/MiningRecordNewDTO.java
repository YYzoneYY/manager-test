package com.ruoyi.system.domain.dto;

import com.ruoyi.system.domain.Entity.MiningRecordEntity;
import com.ruoyi.system.domain.Entity.MiningRecordNewEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: shikai
 * @date: 2024/11/13
 * @description:
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class MiningRecordNewDTO extends MiningRecordNewEntity {

    @ApiModelProperty("修改类型")
    private String type;

    @ApiModelProperty("回采时间格式化")
    private String miningTimeFrm;

    @ApiModelProperty(value = "更新时间格式化")
    private String updateTimeFrm;

    @ApiModelProperty(value = "用户名称")
    private String createByFmt;
}