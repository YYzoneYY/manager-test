package com.ruoyi.system.domain.Entity;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.system.domain.BusinessBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author: shikai
 * @date: 2024/11/15
 * @description:
 */
@Data
@ApiModel("施工人员表")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("construction_personnel")
public class ConstructionPersonnelEntity extends BusinessBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("施工人员id")
    @TableId(value = "construction_personnel_id", type = IdType.AUTO)
    private Long constructionPersonnelId;

    @ApiModelProperty("名称")
    @TableField(value = "name")
    private String name;

    @ApiModelProperty("施工单位id")
    @TableField(value = "construction_unit_id")
    private Long constructionUnitId;

    @ApiModelProperty("工种")
    @TableField(value = "profession")
    private String profession;

    @ApiModelProperty(value = "删除标志(0存在2删除)")
    @TableLogic(value = "0", delval = "2")
    @TableField("del_flag")
    private String delFlag;
}