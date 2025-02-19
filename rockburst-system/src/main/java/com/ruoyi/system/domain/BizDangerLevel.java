package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ruoyi.common.core.domain.BaseSelfEntity;
import com.ruoyi.system.constant.GroupAdd;
import com.ruoyi.system.constant.GroupUpdate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

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

    @NotNull(groups = GroupAdd.class)
    @ApiModelProperty(value = "等级", required = true)
    private String level;

    @NotNull(groups = GroupAdd.class)
    @ApiModelProperty(value = "名称",required = true)
    private String name;

    @NotNull(groups = GroupAdd.class)
    @ApiModelProperty(value = "间隔",required = true)
    private Double spaced;

    @ApiModelProperty(value = "比例 地图:实际  ==> 1: xx  以 xx为 比例尺")
    private Double scale;

    @NotNull(groups = {GroupAdd.class, GroupUpdate.class})
    @ApiModelProperty(value = "颜色",required = true)
    private String color;




}
