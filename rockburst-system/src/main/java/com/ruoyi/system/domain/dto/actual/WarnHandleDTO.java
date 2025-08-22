package com.ruoyi.system.domain.dto.actual;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/8/20
 * @description:
 */

@Data
public class WarnHandleDTO {

    @ApiModelProperty("预警详情")
    private String warnDetails;

    @ApiModelProperty("处理人")
    private String handleName;

    @ApiModelProperty("处理状态")
    private String handStatus;

    @ApiModelProperty("是否执行应急响应")
    private String isResponse;

    @ApiModelProperty("处理详情")
    private String handleDetails;
}