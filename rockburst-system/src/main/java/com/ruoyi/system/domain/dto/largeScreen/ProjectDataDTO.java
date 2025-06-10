package com.ruoyi.system.domain.dto.largeScreen;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/6/9
 * @description:
 */

@Data
public class ProjectDataDTO {

    @ApiModelProperty("工程类型")
    private String projectType;

    private List<DataDTO> data;
}