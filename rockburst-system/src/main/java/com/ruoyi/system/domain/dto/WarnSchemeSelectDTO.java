package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/8/11
 * @description:
 */

@Data
public class WarnSchemeSelectDTO {

    @ApiModelProperty("所属工作面")
    private Long workFaceId;

    @ApiModelProperty("场景类型")
    private String sceneType;

    @ApiModelProperty("状态")
    private String status;
}