package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 工作面管理对象 biz_workface
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
@Getter
@Setter
public class BizWorkfaceSchemeDto
{

    private Long[] workfaceIds;

    @ApiModelProperty(value = "规划")
    private String scheme;


}
