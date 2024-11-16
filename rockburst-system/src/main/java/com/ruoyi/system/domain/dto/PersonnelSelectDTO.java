package com.ruoyi.system.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2024/11/16
 * @description:
 */
@Data
public class PersonnelSelectDTO {

    @ApiModelProperty("施工人员名称")
    private String name;

    @ApiModelProperty("施工单位id")
    private Long constructionUnitId;

    @ApiModelProperty("工种")
    private String profession;

    @ApiModelProperty("开始时间")
    private Long startTime;

    @ApiModelProperty("结束时间")
    private Long endTime;
}