package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ruoyi.common.core.domain.BaseSelfEntity;
import com.ruoyi.system.constant.GroupAdd;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 工作面管理对象 biz_workface
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
@Getter
@Setter
@Accessors(chain = true)
@ApiModel("工作面管理对象")
public class BizWorkface extends BaseSelfEntity
{
    private static final long serialVersionUID = 1L;

    /** 工作面的唯一标识符 */
    @TableId( type = IdType.ASSIGN_ID)
    private Long workfaceId;

    /** 所属矿井ID（外键） */
    @ApiModelProperty(value = "所属矿井ID", example = "外=键")
    private Long mineId;

    @ApiModelProperty(value = "工作面编号")
    @NotNull(groups = GroupAdd.class)
    private String workfaceNo;

    /** 工作面名称 */
    @ApiModelProperty(value = "工作面名称")
    private String workfaceName;

    /** 工作面类型（如采掘、运输等） */
    @ApiModelProperty(value = "工作面类型", example = "如=采掘、运输等")
    private String type;

    /** 工作面状态（如开工、停工等） */
    @ApiModelProperty(value = "工作面状态", example = "如=开工、停工等")
    private Integer status;

    /** 年生产能力（单位：吨） */
    @ApiModelProperty(value = "年生产能力", example = "单=位：吨")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal capacity;

    /** 工作面开始工作日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "工作面开始工作日期")
    private Date workStartDate;

    /** 工作面结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "工作面结束日期")
    private Date workEndDate;

    /** 工作面负责人 */
    @ApiModelProperty(value = "工作面负责人")
    private String leader;

    /** 煤层厚度 */
    @ApiModelProperty(value = "煤层厚度")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal depth;

    /** 所属采区 */
    @ApiModelProperty(value = "所属采区")
    private Long miningAreaId;

    /** 煤层名称 */
    @ApiModelProperty(value = "煤层名称")
    private String coalSeam;

    /** 工作面采高（单位：米） */
    @ApiModelProperty(value = "工作面采高", example = "单=位：米")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal faceHeight;

    /** 采煤方式（如露天采矿、地下开采等） */
    @ApiModelProperty(value = "采煤方式", example = "如=露天采矿、地下开采等")
    private String miningType;

    /** 倾向长度（单位：米） */
    @ApiModelProperty(value = "倾向长度", example = "单=位：米")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal dipLength;

    /** 走向长度（单位：米） */
    @ApiModelProperty(value = "走向长度", example = "单=位：米")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal strikeLength;

    /** 煤容量（单位：吨） */
    @ApiModelProperty(value = "煤容量", example = "单=位：吨")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal coalCapacity;

    /** 煤层走向倾角（单位：度） */
    @ApiModelProperty(value = "煤层走向倾角", example = "单=位：度")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal seamInclination;

    /** 平均埋深（单位：米） */
    @ApiModelProperty(value = "平均埋深", example = "单=位：米")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal avgBurialDepth;

    /** 上边界采深（单位：米） */
    @ApiModelProperty(value = "上边界采深", example = "单=位：米")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal upperBoundaryDepth;

    /** 下边界采深（单位：米） */
    @ApiModelProperty(value = "下边界采深", example = "单=位：米")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal lowerBoundaryDepth;

    /** 推进长度（单位：米） */
    @ApiModelProperty(value = "推进长度", example = "单=位：米")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal advanceLength;

    /** 水平应力（单位：MPa） */
    @ApiModelProperty(value = "水平应力", example = "单=位：MPa")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal horizontalStress;

    /** 其他备注或说明 */
    @ApiModelProperty(value = "其他备注或说明")
    private String notes;



}
