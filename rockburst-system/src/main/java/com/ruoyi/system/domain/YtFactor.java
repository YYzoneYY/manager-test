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
@ApiModel("云图影响因素")
public class YtFactor extends BaseSelfEntity
{
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long factorId;


    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "类型")
    private String factorType;


    @ApiModelProperty(value = "开始导线点")
    private Long startPointId;

    @ApiModelProperty(value = "结束导线点")
    private Long endPointId;

    @ApiModelProperty(value = "开始导线点前后距离")
    private Double startMeter;

    @ApiModelProperty(value = "结束导线点前后距离")
    private Double endMeter;


    @ApiModelProperty(value = "值")
    private Integer value;



}
