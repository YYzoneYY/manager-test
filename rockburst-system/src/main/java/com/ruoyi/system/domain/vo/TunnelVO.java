package com.ruoyi.system.domain.vo;

import com.ruoyi.system.domain.Entity.TunnelEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: shikai
 * @date: 2024/11/21
 * @description:
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class TunnelVO extends TunnelEntity {

    @ApiModelProperty("工作面名称")
    private String workFaceName;

    @ApiModelProperty("创建时间格式化")
    private String createTimeFrm;

    @ApiModelProperty("更新时间格式化")
    private String updateTimeFrm;

    @ApiModelProperty("断面形状格式化")
    private String sectionShapeFmt;

    @ApiModelProperty("支护形式格式化")
    private String supportFormFmt;
}