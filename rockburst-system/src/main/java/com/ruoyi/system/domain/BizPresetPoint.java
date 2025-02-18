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
@ApiModel("矿井危险区对象")
public class BizPresetPoint extends BaseSelfEntity
{
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long presetPointId;

    @ApiModelProperty(value = "巷道")
    private Long tunnelId;

    @ApiModelProperty(value = "帮")
    private Long tunnelBarId;

    @ApiModelProperty(value = "危险区")
    private Long dangerAreaId;


    @ApiModelProperty(value = "实际位置 导线点")
    private Long pointId;


    @ApiModelProperty(value = "实际位置 前后距离")
    private Double meter;


    @ApiModelProperty(value = "纬度")
    private String latitude;

    @ApiModelProperty(value = "经度")
    private String longitude;



    @ApiModelProperty(value = "纬度T")
    private String latitudet;

    @ApiModelProperty(value = "经度T")
    private String longitudet;




}
