package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 矿井管理对象 biz_mine
 * 
 * @author ruoyi
 * @date 2024-11-11
 */

@Getter
@Setter
public class BizMine extends BaseSelfEntity
{
    private static final long serialVersionUID = 1L;

    /** 矿井的唯一标识符 */
    private Long mineId;

    /** 矿井名称 */
    @Excel(name = "矿井名称")
    @Schema(description = "矿井名称")
    @TableField()
    private String mineName;

    /** 所属省份 */
    @Excel(name = "所属省份")
    @Schema(description = "所属省份")
    @TableField()
    private String province;

    /** 所属城市 */
    @Schema(description = "所属城市")
    @Excel(name = "所属城市")
    @TableField()
    private String city;

    /** 所属区县 */
    @Schema(description = "所属区县")
    @Excel(name = "所属区县")
    @TableField()
    private String district;

    /** 详细地址 */
    @Schema(description = "详细地址")
    @Excel(name = "详细地址")
    @TableField()
    private String detailedAddress;


    /** 地理位置坐标 (经纬度) */
    @Schema(description = "地理位置坐标")
    @Excel(name = "地理位置坐标 (经纬度)")
    @TableField()
    private String location;

    /** 矿井类型（如露天、地下） */
    @Schema(description = "矿井类型")
    @Excel(name = "矿井类型", readConverterExp = "如=露天、地下")
    @TableField()
    private String type;

    /** 矿井深度（单位：米） */
    @Schema(description = "矿井深度")
    @Excel(name = "矿井深度", readConverterExp = "单=位：米")
    @TableField()
    private BigDecimal depth;

    /** 矿井状态（如运营中、关闭） */
    @Schema(description = "矿井状态")
    @Excel(name = "矿井状态", readConverterExp = "如=运营中、关闭")
    @TableField()
    private Integer status;

    /** 矿井年生产能力（单位：吨） */
    @Schema(description = "矿井年生产能力")
    @Excel(name = "矿井年生产能力", readConverterExp = "单=位：吨")
    @TableField()
    private Long capacity;

    /** 矿井所有者或运营公司 */
    @Schema(description = "矿井所有者或运营公司")
    @Excel(name = "矿井所有者或运营公司")
    @TableField()
    private String owner;

    /** 矿井投产日期 */
    @Schema(description = "矿井投产日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "矿井投产日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField()
    private Date startDate;

    /** 上次检查日期 */
    @Schema(description = "上次检查日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "上次检查日期", width = 30, dateFormat = "yyyy-MM-dd")
    @TableField()
    private Date lastInspectionDate;

    /** 其他备注或说明 */
    @Schema(description = "其他备注或说明")
    @Excel(name = "其他备注或说明")
    @TableField()
    private String notes;

    /** 删除标志（0代表存在 2代表删除） */
    @Schema(description = "删除标志（0代表存在 2代表删除）")
    @TableField()
    private String delFlag;


}
