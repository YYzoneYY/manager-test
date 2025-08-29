package com.ruoyi.system.domain.Entity;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.system.domain.BusinessBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.rely.FieldType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author: shikai
 * @date: 2024/11/16
 * @description:
 */

@Data
@ApiModel("班次表")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("classes")
public class ClassesEntity extends BusinessBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("班次id")
    @NotNull(groups = {ParameterValidationUpdate.class}, message = "班次id不能为空")
    @TableId(value = "classes_id", type = IdType.AUTO)
    private Long classesId;

    @ApiModelProperty("班次名称")
    @NotBlank(groups = {ParameterValidationOther.class}, message = "班次名称不能为空")
    @Size(max = 50, groups = {ParameterValidationOther.class}, message = "班次名称长度不能超过50")
    @TableField(value = "classes_name")
    private String classesName;

    @ApiModelProperty(value = "删除标志(0存在2删除)")
    @TableLogic(value = "0", delval = "2")
    @TableField("del_flag")
    private String delFlag;

    @ApiModelProperty("所属矿")
    @TableField(value = "mine_id")
    private Long mineId;

    @ApiModelProperty("所属公司")
    @TableField(value = "company_id")
    private Long companyId;
}