package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ruoyi.common.core.domain.BaseSelfEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 矿井危险区对象 BizDangerArea
 *
 * @author ruoyi
 * @date 2024-11-11
 */

@Getter
@Setter
@Accessors(chain = true)
@ApiModel("计划关联预设点")
public class BizPlanPreset extends BaseSelfEntity
{
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "计划id")
    private Long planId;

    @ApiModelProperty(value = "预设点id")
    private Long presetPointId;

    @ApiModelProperty(value = "底部 预设点位置")
    private String bottom;

    @ApiModelProperty(value = "顶部 预设点位置")
    private String top;

    @ApiModelProperty(value = "危险区id")
    private Long dangerAreaId;



}
