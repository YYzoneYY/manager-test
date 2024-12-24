package com.ruoyi.system.domain.dto;

import com.ruoyi.system.domain.Entity.ExcavationRecordEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: shikai
 * @date: 2024/12/23
 * @description:
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class ExcavationRecordDTO extends ExcavationRecordEntity {

    @ApiModelProperty("修改类型")
    private String type;

    @ApiModelProperty("回采时间格式化")
    private String ExcavationTimeFrm;

    @ApiModelProperty(value = "更新时间格式化")
    private String updateTimeFrm;

    @ApiModelProperty(value = "用户名称")
    private String createByFmt;
}