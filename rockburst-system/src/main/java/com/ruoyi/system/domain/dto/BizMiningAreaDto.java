package com.ruoyi.system.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseSelfEntity;
import com.ruoyi.system.constant.GroupUpdate;
import io.swagger.v3.oas.annotations.media.Schema;
import com.ruoyi.system.constant.GroupAdd;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 采区管理对象 biz_mining_area
 * 
 * @author ruoyi
 * @date 2024-11-11
 */

@Getter
@Setter
public class BizMiningAreaDto
{
    private static final long serialVersionUID = 1L;

    /** 采区的唯一标识符 */
    @NotNull(groups = GroupUpdate.class)
    private Long miningAreaId;

    /** 所属矿井ID（外键） */
    @Schema(description = "所属矿井ID")
    @NotNull(groups = GroupAdd.class)
    private Long mineId;

    /** 采区名称 */
    @NotNull(groups = GroupAdd.class)
    @Schema(description = "采区名称")
    private String miningAreaName;

    /** 采区面积（单位：平方米） */
    @NotNull(groups = GroupAdd.class)
    @Schema(description = "采区面积")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal miningAreaArea;

    /** 采区状态（如开采中、暂停开采等） */
    @NotNull(groups = GroupAdd.class)
    @Schema(description = "采区状态 如=1 开采中、2 暂停开采等")
    private Integer status;

    /** 采区开采开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "采区开采开始日期")
    private Date startDate;

    /** 采区开采结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "采区开采结束日期")
    private Date endDate;

    /** 其他备注或说明 */
    @Schema(description = "其他备注或说明")
    private String notes;



}
