package com.ruoyi.system.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2024/11/12
 * @description:
 */
@Data
public class SurveyAreaDTO {

    @ApiModelProperty(value = "测区id")
    private Long surveyAreaId;

    @ApiModelProperty(value = "测区名称")
    private String surveyAreaName;

    @ApiModelProperty(value = "采区id")
    private Long miningAreaId;

    @ApiModelProperty(value = "工作面id")
    private Long workFaceId;

    @ApiModelProperty(value = "巷道id")
    private Long tunnelId;

    @ApiModelProperty(value = "临界煤粉量")
    private BigDecimal criticalBraize;

    @ApiModelProperty(value = "距左帮")
    private BigDecimal leftGang;

    @ApiModelProperty(value = "距右帮")
    private BigDecimal rightGang;
}