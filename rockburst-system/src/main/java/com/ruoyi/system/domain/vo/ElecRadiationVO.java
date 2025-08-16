package com.ruoyi.system.domain.vo;

import com.ruoyi.system.domain.Entity.ElecRadiationEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/8/16
 * @description:
 */

@Data
public class ElecRadiationVO extends ElecRadiationEntity {

    @ApiModelProperty(value = "工作面名称")
    private String workFaceName;

    @ApiModelProperty(value = "安装时间格式化")
    private String installTimeFmt;
}