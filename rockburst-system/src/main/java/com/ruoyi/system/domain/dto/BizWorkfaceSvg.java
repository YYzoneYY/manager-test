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
public class BizWorkfaceSvg
{
    private static final long serialVersionUID = 1L;

    private Long workfaceId;

    /** 其他备注或说明 */
    @ApiModelProperty(value = "地图 ")
    private String svg;

    @ApiModelProperty(value = "中心点")
    private String center;


}
