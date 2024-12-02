package com.ruoyi.system.domain.Entity;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.system.domain.BusinessBaseEntity;
import com.ruoyi.system.handler.JsonListTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author: shikai
 * @date: 2024/11/29
 * @description:
 */

@Data
@ApiModel("单独预警方案")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("warn_scheme_separate")
public class WarnSchemeSeparateEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键id")
    @TableId(value = "warn_scheme_sepatate_id", type = IdType.AUTO)
    private Long warnSchemeSeparateId;

    @ApiModelProperty("预警方案id")
    @TableField(value = "warn_scheme_id")
    private Long warnSchemeId;

    @ApiModelProperty("工作面id")
    @TableField(value = "work_face_id")
    private Long workFaceId;

    @ApiModelProperty("场景类型")
    @TableField(value = "scene_type")
    private String sceneType;

    @ApiModelProperty("测点编码")
    @TableField(value = "measure_num")
    private String measureNum;

    @ApiModelProperty("预警阈值配置")
    @TableField(value = "threshold_config", typeHandler = JsonListTypeHandler.class)
    private List<Map<String, Object>> thresholdConfig;

    @ApiModelProperty("预警增量配置")
    @TableField(value = "increment_config", typeHandler = JsonListTypeHandler.class)
    private List<Map<String, Object>> incrementConfig;

    @ApiModelProperty("预警增速配置")
    @TableField(value = "growth_rate_config", typeHandler = JsonListTypeHandler.class)
    private List<Map<String, Object>> growthRateConfig;
}