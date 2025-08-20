package com.ruoyi.system.domain.dto.actual;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/8/20
 * @description:
 */
@Data
public class ResponseOperateDTO {

    @ApiModelProperty("警情编号")
    private String warnInstanceNum;

    @ApiModelProperty("预警详情")
    private String warnDetails;

    @ApiModelProperty("事故类别")
    private String accidentType;

    @ApiModelProperty("事故级别")
    private String accidentLevel;

    @ApiModelProperty("事故描述")
    private String accidentDepict;
}