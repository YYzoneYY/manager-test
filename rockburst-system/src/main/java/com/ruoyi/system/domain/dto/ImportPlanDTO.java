package com.ruoyi.system.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.system.domain.Entity.ParameterValidationOther;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2025/2/21
 * @description:
 */

@Data
public class ImportPlanDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "年度")
    @NotBlank(message = "年度不能为空")
    @Excel(name = "年度(必填)", sort = 1, width = 20)
    private String annual;

    @ApiModelProperty(value = "工作面名称")
    @NotBlank(message = "工作面名称不能为空")
    @Excel(name = "工作面名称(必填)", sort = 2, width = 20)
    private String workFaceName;

    @ApiModelProperty(value = "计划类型")
    @NotBlank(message = "计划类型不能为空")
    @Excel(name = "计划类型(必填)", sort = 3, width = 20)
    private String planType;

    @ApiModelProperty(value = "类型")
    @NotBlank(message = "类型不能为空")
    @Excel(name = "类型(必填)", sort = 4, width = 20)
    private String type;

    @ApiModelProperty(value = "总钻数")
    @NotBlank(message = "总钻数不能为空")
    @Excel(name = "总孔数(必填)", sort = 5, width = 20)
    @Pattern(regexp = "^([-+])?\\d*(\\.\\d+)?$", message = "请输入正确的总钻数,只能为数字")
    private String totalDrillNumber;

    @ApiModelProperty(value = "总孔深")
    @NotBlank(message = "总孔深不能为空")
    @Excel(name = "总孔深m(必填)", sort = 6, width = 20)
    @Pattern(regexp = "^([-+])?\\d*(\\.\\d+)?$", message = "请输入正确的总孔深,只能为数字")
    private String totalHoleDepth;

    @ApiModelProperty(value = "计划别名")
    @Excel(name = "计划别名", sort = 7, width = 20)
    private String planName;

    @ApiModelProperty(value = "巷道名称")
    @NotBlank(message = "巷道名称不能为空")
    @Excel(name = "巷道名称(必填)", sort = 8, width = 20)
    private String tunnelName;

    @ApiModelProperty(value = "起始导线点")
    @NotBlank(message = "起始导线点不能为空")
    @Excel(name = "起始导线点(必填,符号为英文符号)", sort = 9, width = 20)
    private String startPoint;

    @ApiModelProperty(value = "距起始点距离")
    @NotBlank(message = "距起始点距离不能为空")
    @Excel(name = "距起始点距离\n" +
            "(必填,右侧为正方向;正方向示例:5,负方向示例:-10;所有符号为英文符号)", sort = 10, width = 20)
    private String startDistance;

    @ApiModelProperty(value = "终始导线点")
    @NotBlank(message = "终始导线点不能为空")
    @Excel(name = "终始导线点(必填,符号为英文符号)", sort = 11, width = 20)
    private String endPoint;

    @ApiModelProperty(value = "距终始点距离")
    @NotBlank(message = "距终始点距离不能为空")
    @Excel(name = "距终始点距离\n" +
            "(必填,右侧为正方向;正方向示例:5,负方向示例:-10;所有符号为英文符号)", sort = 12, width = 20)
    private String endDistance;

    @ApiModelProperty(value = "钻孔类型")
    @NotBlank(message = "钻孔类型不能为空")
    @Excel(name = "钻孔类型(必填)", sort = 13, width = 20)
    private String drillType;

    @ApiModelProperty(value = "计划开始时间")
    @NotBlank(message = "计划开始时间不能为空")
    @Excel(name = "计划开始时间(必填,格式yyyy-MM-dd)", dateFormat = "yyyy-MM-dd", sort = 14, width = 20)
    private String startTime;

    @ApiModelProperty(value = "计划结束时间")
    @NotBlank(message = "计划结束时间不能为空")
    @Excel(name = "计划结束时间(必填,格式yyyy-MM-dd)", dateFormat = "yyyy-MM-dd", sort = 15, width = 20)
    private String endTime;
}