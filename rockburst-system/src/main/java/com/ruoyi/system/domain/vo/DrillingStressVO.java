package com.ruoyi.system.domain.vo;

import com.ruoyi.system.domain.Entity.DrillingStressEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2024/12/3
 * @description:
 */
@Data
public class DrillingStressVO extends DrillingStressEntity {

    @ApiModelProperty(value = "工作面名称")
    private String workFaceName;

    @ApiModelProperty(value = "安装时间格式化")
    private String installTimeFmt;
}