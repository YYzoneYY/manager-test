package com.ruoyi.system.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.ruoyi.system.domain.Entity.ConstructionPersonnelEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: shikai
 * @date: 2024/11/15
 * @description:
 */

@Data
public class ConstructPersonnelVO extends ConstructionPersonnelEntity {

    @ApiModelProperty("施工单位")
    private String constructionUnitFmt;

    @ApiModelProperty("工种")
    private String professionFmt;

    @ApiModelProperty(value = "创建时间")
    private String createTimeFmt;

    @ApiModelProperty(value = "修改时间")
    private String updateTimeFmt;

}