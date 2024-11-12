package com.ruoyi.system.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2024/11/12
 * @description:
 */

@Data
public class SurveyAreaVO {

    @ApiModelProperty(value = "测区id")
    private Long surveyAreaId;

    @ApiModelProperty(value = "测区名称")
    private String surveyAreaName;

    @ApiModelProperty(value = "所属工作面")
    private String workFaceNameFmt;

    @ApiModelProperty(value = "所属测区")
    private String miningAreaNameFmt;

    @ApiModelProperty(value = "所属巷道")
    private String tunnelNameFmt;

    @ApiModelProperty(value = "临界煤粉量")
    private BigDecimal criticalBraize;

    @ApiModelProperty(value = "距左帮")
    private BigDecimal leftGang;

    @ApiModelProperty(value = "距右帮")
    private BigDecimal rightGang;

    @ApiModelProperty(value = "创建时间")
    private String creatTimeFmt;

    private Long miningAreaId;
    private Long workFaceId;
    private Long tunnelId;
    private Long creatTime;
}