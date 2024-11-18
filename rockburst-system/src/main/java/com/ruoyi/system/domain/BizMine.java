package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseSelfEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import com.ruoyi.common.annotation.Excel;
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
@ApiModel("矿井管理对象")
public class BizMine extends BaseSelfEntity
{
    private static final long serialVersionUID = 1L;

    /** 矿井的唯一标识符 */
    @TableId( type = IdType.AUTO)
    private Long mineId;

    /** 矿井名称 */
    @ApiModelProperty(value = "矿井名称")
    @TableField()
    private String mineName;

    /** 所属省份 */
    @ApiModelProperty(value = "所属省份")
    @TableField()
    private String province;

    /** 所属城市 */
    @ApiModelProperty(value = "所属城市")
    @TableField()
    private String city;

    /** 所属区县 */
    @ApiModelProperty(value = "所属区县")
    @TableField()
    private String district;

    /** 详细地址 */
    @ApiModelProperty(value = "详细地址")
    @TableField()
    private String detailedAddress;


    /** 地理位置坐标 (经纬度) */
    @ApiModelProperty(value = "地理位置坐标")
    @TableField()
    private String location;

    /** 矿井类型（如露天、地下） */
    @ApiModelProperty(value = "矿井类型")
    @TableField()
    private String type;

    /** 矿井深度（单位：米） */
    @ApiModelProperty(value = "矿井深度")
    @TableField()
    private BigDecimal depth;

    /** 矿井状态（如运营中、关闭） */
    @ApiModelProperty(value = "矿井状态")
    @TableField()
    private Integer status;

    /** 矿井年生产能力（单位：吨） */
    @ApiModelProperty(value = "矿井年生产能力")
    @TableField()
    private Long capacity;

    /** 矿井所有者或运营公司 */
    @ApiModelProperty(value = "矿井所有者或运营公司")
    @TableField()
    private String owner;

    /** 矿井投产日期 */
    @Schema(description = "矿井投产日期")
    @ApiModelProperty(value = "矿井投产日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField()
    private Date startDate;

    /** 上次检查日期 */
    @ApiModelProperty(value = "上次检查日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField()
    private Date lastInspectionDate;

    /** 其他备注或说明 */
    @ApiModelProperty(value = "其他备注或说明")
    @TableField()
    private String notes;




}
