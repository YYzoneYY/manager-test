package com.ruoyi.system.domain.vo;

import com.ruoyi.system.domain.Entity.ProjectWarnSchemeEntity;
import com.ruoyi.system.domain.dto.DistanceRuleDTO;
import com.ruoyi.system.domain.dto.WorkloadRuleDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: shikai
 * @date: 2024/12/12
 * @description:
 */

@Data
public class ProjectWarnSchemeVO extends ProjectWarnSchemeEntity {

    @ApiModelProperty(value = "工作量规则")
    private List<WorkloadRuleDTO> workloadRuleDTOS;

    @ApiModelProperty(value = "距离规则")
    private DistanceRuleDTO distanceRuleDTO;

    @ApiModelProperty(value = "预警类型")
    private String warnType;
}