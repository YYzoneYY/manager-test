package com.ruoyi.system.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 矿井管理对象 biz_mine
 *
 * @author ruoyi
 * @date 2024-11-11
 */

@Getter
@Setter
@Accessors(chain = true)
public class BizPresetPointDto
{

    @TableId(type = IdType.AUTO)
    private Long presetPointId;

    @ApiModelProperty(value = "巷道")
    private String tunnelId;

    @ApiModelProperty(value = "帮")
    private String tunnelBarId;

    @ApiModelProperty(value = "危险区")
    private String dangerAreaId;


    @ApiModelProperty(value = "实际位置 导线点")
    private Long pointId;


    @ApiModelProperty(value = "实际位置 前后距离")
    private Double meter;


    @ApiModelProperty(value = "地图位置")
    private String svg;



}
