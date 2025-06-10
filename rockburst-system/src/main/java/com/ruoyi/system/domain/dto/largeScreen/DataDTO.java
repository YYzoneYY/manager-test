package com.ruoyi.system.domain.dto.largeScreen;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author: shikai
 * @date: 2025/6/9
 * @description:
 */

@Data
public class DataDTO {

    @ApiModelProperty("工程id")
    private Long projectId;

    @ApiModelProperty("钻孔编号")
    private String drillNum;

    @ApiModelProperty("施工时间")
    private Date constructTime;

    @ApiModelProperty("视频地址")
    private List<String> urls;

    @ApiModelProperty("AI视频地址")
    private List<String> AIUrls;
}