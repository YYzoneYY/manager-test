package com.ruoyi.system.domain.dto.actual;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ruoyi.system.domain.Entity.MultiplePlanEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: shikai
 * @date: 2025/8/19
 * @description:
 */

@Data
public class MultipleParamPlanDTO{

    @ApiModelProperty("测点编码")
    private String measureNum;

    @ApiModelProperty("安装位置")
    private String sensorLocation;

    @ApiModelProperty(value = "监测项")
    private String monitorItems;

    @ApiModelProperty("工作面id")
    private Long workFaceId;

    @ApiModelProperty("传感器类型")
    private String sensorType;

}