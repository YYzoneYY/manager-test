package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseSelfEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 矿井管理对象 biz_mine
 * 
 * @author ruoyi
 * @date 2024-11-11
 */

@Getter
@Setter
@Accessors(chain = true)
@ApiModel("导线点管理对象")
public class BizTravePoint extends BaseSelfEntity
{
    private static final long serialVersionUID = 1L;

    /** 矿井的唯一标识符 */
    @TableId( type = IdType.AUTO)
    private Long pointId;

    /** 矿井名称 */
    @ApiModelProperty(value = "导线点名称")
    @TableField()
    private String pointName;

    /** 所属省份 */
    @ApiModelProperty(value = "坐标")
    @TableField()
    private String axis;


    @ApiModelProperty(value = "状态")
    @TableField()
    private Integer status;






}
