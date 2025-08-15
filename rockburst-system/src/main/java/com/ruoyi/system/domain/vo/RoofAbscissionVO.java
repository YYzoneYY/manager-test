package com.ruoyi.system.domain.vo;

import com.ruoyi.system.domain.Entity.RoofAbscissionEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/8/15
 * @description:
 */

@Data
public class RoofAbscissionVO extends RoofAbscissionEntity {

    @ApiModelProperty(value = "工作面名称")
    private String workFaceName;

    @ApiModelProperty(value = "巷道名称")
    private String tunnelName;

    @ApiModelProperty(value = "安装时间格式化")
    private String installTimeFmt;
}