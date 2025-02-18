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
@ApiModel("危险区等级")
public class BizDangerLevel extends BaseSelfEntity
{
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long dangerLevelId;


    @ApiModelProperty(value = "等级")
    private String level;

    @ApiModelProperty(value = "间隔")
    private Double spaced;

    @ApiModelProperty(value = "比例 地图:实际  ==> 1: xx  以 xx为 比例尺")
    private Double scale;


    @ApiModelProperty(value = "颜色")
    private String color;




}
