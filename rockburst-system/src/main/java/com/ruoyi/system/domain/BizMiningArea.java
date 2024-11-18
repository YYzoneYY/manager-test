package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ruoyi.common.core.domain.BaseSelfEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import com.ruoyi.common.annotation.Excel;
import lombok.experimental.Accessors;

/**
 * 采区管理对象 biz_mining_area
 * 
 * @author ruoyi
 * @date 2024-11-11
 */

@Getter
@Setter
@Accessors(chain = true)
@ApiModel("采区管理对象")
public class BizMiningArea extends BaseSelfEntity
{
    private static final long serialVersionUID = 1L;

    /** 采区的唯一标识符 */
    @TableId( type = IdType.AUTO)
    private Long miningAreaId;

    /** 所属矿井ID（外键） */
    @Schema(description = "所属矿井ID")
    @Excel(name = "所属矿井ID", readConverterExp = "外=键")
    private Long mineId;

    /** 采区名称 */
    @Excel(name = "采区名称")
    @Schema(description = "采区名称")
    private String miningAreaName;

    /** 采区面积（单位：平方米） */
    @Excel(name = "采区面积", readConverterExp = "单=位：平方米")
    @Schema(description = "采区面积")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal miningAreaArea;

    /** 采区状态（如开采中、暂停开采等） */
    @Excel(name = "采区状态", readConverterExp = "如=1 开采中、2 暂停开采等")
    @Schema(description = "采区状态")
    private Integer status;

    /** 采区开采开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "采区开采开始日期")
    @Excel(name = "采区开采开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startDate;

    /** 采区开采结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "采区开采结束日期")
    @Excel(name = "采区开采结束日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endDate;

    /** 其他备注或说明 */
    @Excel(name = "其他备注或说明")
    @Schema(description = "其他备注或说明")
    private String notes;



}
