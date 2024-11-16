package com.ruoyi.system.domain.vo;

import com.ruoyi.system.domain.Entity.ClassesEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: shikai
 * @date: 2024/11/16
 * @description:
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class ClassesVO extends ClassesEntity {

    @ApiModelProperty(value = "创建时间")
    private String createTimeFmt;

    @ApiModelProperty(value = "修改时间")
    private String updateTimeFmt;
}