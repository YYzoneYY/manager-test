package com.ruoyi.system.domain.dto;

import com.ruoyi.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/6/17
 * @description:
 */

@Data
public class DrillPropertiesDTO {

    @ApiModelProperty("序号")
    private Integer num;

    @ApiModelProperty("标高")
    private String elevation;

    @ApiModelProperty("岩性")
    private String lithology;

    @ApiModelProperty("岩性编号")
    private String lithologyNum;

    @ApiModelProperty("厚度")
    private String thickness;

    @ApiModelProperty("埋深")
    private String buriedDepth;

    @ApiModelProperty("岩性描述")
    private String lithologyDescribe;

    @ApiModelProperty("备注")
    private String remarks;
}