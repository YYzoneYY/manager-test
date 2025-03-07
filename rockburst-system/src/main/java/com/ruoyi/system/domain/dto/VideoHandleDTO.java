package com.ruoyi.system.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/3/7
 * @description:
 */

@Data
public class VideoHandleDTO {

    @ApiModelProperty(value = "视频识别之前url")
    private String beforeVideoUrl;

}