package com.ruoyi.system.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/9/3
 * @description:
 */

@Data
public class RelevanceDTO {

    @ApiModelProperty("测点编码")
    private String measureNum;

    @ApiModelProperty("原始工作面名称(数采上来的数据)")
    private String originalWorkFaceName;

    @ApiModelProperty("工作面id(关联后)")
    private Long workFaceId;
}