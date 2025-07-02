package com.ruoyi.system.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/7/2
 * @description:
 */

@Data
public class PushConfigDTO {

    @ApiModelProperty("标识")
    private String tag;

    @ApiModelProperty("用户id组")
    private List<Long> userIdGroup;
}