package com.ruoyi.system.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BizProjectCDMAP {


    @ApiModelProperty(value = "施工地点")
    private String constructTime;

    @ApiModelProperty(value = "施工地点")
    private String crumbWeight;

    @ApiModelProperty(value = "施工地点")
    private Long travePointId;

    @ApiModelProperty(value = "施工地点")
    private String constructRange;

    @ApiModelProperty(value = "施工地点")
    private Long tunnelId;




}
