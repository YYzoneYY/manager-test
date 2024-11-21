package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: shikai
 * @date: 2024/11/20
 * @description:
 */

@Data
public class ConstructFIleDTO {

    @ApiModelProperty(value = "层级id")
    private Long dataId;

    @ApiModelProperty(value = "文件id")
    private Long[] fileIds;

    @ApiModelProperty(value = "文件名称")
    private String documentName;
}