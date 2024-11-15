package com.ruoyi.system.domain.Entity;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.system.domain.BusinessBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author: shikai
 * @date: 2024/11/15
 * @description:
 */
@Data
@ApiModel("施工单位表")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("construction_unit")
public class ConstructionUnitEntity extends BusinessBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("施工单位id")
    @NotNull(groups = {ParameterValidationUpdate.class}, message = "施工单位id不能为空")
    @TableId(value = "construction_unit_id", type = IdType.AUTO)
    private Long constructionUnitId;

    @ApiModelProperty("施工单位名称")
    @NotBlank(groups = {ParameterValidationOther.class}, message = "施工单位名称不能为空")
    @Size(max = 50, groups = {ParameterValidationOther.class}, message = "施工单位长度不能超过50")
    @TableField(value = "construction_unit_name")
    private String constructionUnitName;

    @ApiModelProperty(value = "删除标志(0存在2删除)")
    @TableLogic(value = "0", delval = "2")
    @TableField("del_flag")
    private String delFlag;
}