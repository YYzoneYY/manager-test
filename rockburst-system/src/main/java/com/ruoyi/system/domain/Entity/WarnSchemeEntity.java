package com.ruoyi.system.domain.Entity;

import com.alibaba.fastjson2.annotation.JSONField;
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
@ApiModel("预警方案")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "warn_scheme")
public class WarnSchemeEntity extends BusinessBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("预警方案id")
    @TableId(value = "warn_scheme_id", type = IdType.AUTO)
    private Long warnSchemeId;

    @ApiModelProperty("预警方案名称")
    @TableField(value = "scheme_name")
    private String warnSchemeName;

    @ApiModelProperty("场景类型")
    @TableField(value = "scene_type")
    private String sceneType;

    @ApiModelProperty("工作面id")
    @TableField(value = "work_face_id")
    private Long workFaceId;

    @ApiModelProperty("静默时间")
    @TableField(value = "quiet_hour")
    private String quietHour;

    @ApiModelProperty("预警阈值配置")
    @TableField(value = "threshold_config", typeHandler = JsonListTypeHandler.class)
    private List<Map<String, Object>> thresholdConfig;

    @ApiModelProperty("预警增量配置")
    @TableField(value = "increment_config", typeHandler = JsonListTypeHandler.class)
    private List<Map<String, Object>> incrementConfig;

    @ApiModelProperty("预警增速配置")
    @TableField(value = "growth_rate_config", typeHandler = JsonListTypeHandler.class)
    private List<Map<String, Object>> growthRateConfig;

    @ApiModelProperty("状态")
    @TableField(value = "status")
    private String status;

    @ApiModelProperty("删除标志(0存在2删除)")
    @TableField("del_flag")
    @TableLogic(value = "0", delval = "2")
    private String delFlag;

    @ApiModelProperty("所属矿")
    private Long mineId;

    @ApiModelProperty("所属公司")
    private Long companyId;
}