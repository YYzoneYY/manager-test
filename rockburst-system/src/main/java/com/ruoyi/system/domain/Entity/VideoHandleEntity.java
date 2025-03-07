package com.ruoyi.system.domain.Entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ruoyi.system.domain.BusinessBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2024/11/19
 * @description:
 */
@Data
@ApiModel("视频分析处理")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("video_handle")
public class VideoHandleEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId(value = "video_handle_id", type = IdType.AUTO)
    private Long videoHandleId;

    @ApiModelProperty(value = "工程填报id")
    @TableField("project_id")
    private Long projectId;

    @ApiModelProperty(value = "视频识别之前url")
    @TableField("before_video_url")
    private String beforeVideoUrl;

    @ApiModelProperty(value = "识别状态(0:未识别，1:已识别)")
    @TableField("status")
    private String status;

    @ApiModelProperty(value = "视频识别之后url")
    @TableField("after_video_url")
    private String afterVideoUrl;
}