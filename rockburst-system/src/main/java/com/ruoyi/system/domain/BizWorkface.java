package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ruoyi.common.core.domain.BaseSelfEntity;
import lombok.Getter;
import lombok.Setter;
import com.ruoyi.common.annotation.Excel;

/**
 * 工作面管理对象 biz_workface
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
@Getter
@Setter
public class BizWorkface extends BaseSelfEntity
{
    private static final long serialVersionUID = 1L;

    /** 工作面的唯一标识符 */
    private Long workfaceId;

    /** 所属矿井ID（外键） */
    @Excel(name = "所属矿井ID", readConverterExp = "外=键")
    private Long mineId;

    /** 工作面名称 */
    @Excel(name = "工作面名称")
    private String workfaceName;

    /** 工作面类型（如采掘、运输等） */
    @Excel(name = "工作面类型", readConverterExp = "如=采掘、运输等")
    private String type;

    /** 工作面状态（如开工、停工等） */
    @Excel(name = "工作面状态", readConverterExp = "如=开工、停工等")
    private Integer status;

    /** 年生产能力（单位：吨） */
    @Excel(name = "年生产能力", readConverterExp = "单=位：吨")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal capacity;

    /** 工作面开始工作日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "工作面开始工作日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date workStartDate;

    /** 工作面结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "工作面结束日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date workEndDate;

    /** 工作面负责人 */
    @Excel(name = "工作面负责人")
    private String leader;

    /** 所属采区 */
    @Excel(name = "所属采区")
    private Long areaId;

    /** 煤层名称 */
    @Excel(name = "煤层名称")
    private String coalSeam;

    /** 工作面采高（单位：米） */
    @Excel(name = "工作面采高", readConverterExp = "单=位：米")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal faceHeight;

    /** 采煤方式（如露天采矿、地下开采等） */
    @Excel(name = "采煤方式", readConverterExp = "如=露天采矿、地下开采等")
    private String miningType;

    /** 倾向长度（单位：米） */
    @Excel(name = "倾向长度", readConverterExp = "单=位：米")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal dipLength;

    /** 走向长度（单位：米） */
    @Excel(name = "走向长度", readConverterExp = "单=位：米")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal strikeLength;

    /** 煤容量（单位：吨） */
    @Excel(name = "煤容量", readConverterExp = "单=位：吨")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal coalCapacity;

    /** 煤层走向倾角（单位：度） */
    @Excel(name = "煤层走向倾角", readConverterExp = "单=位：度")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal seamInclination;

    /** 平均埋深（单位：米） */
    @Excel(name = "平均埋深", readConverterExp = "单=位：米")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal avgBurialDepth;

    /** 上边界采深（单位：米） */
    @Excel(name = "上边界采深", readConverterExp = "单=位：米")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal upperBoundaryDepth;

    /** 下边界采深（单位：米） */
    @Excel(name = "下边界采深", readConverterExp = "单=位：米")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal lowerBoundaryDepth;

    /** 推进长度（单位：米） */
    @Excel(name = "推进长度", readConverterExp = "单=位：米")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal advanceLength;

    /** 水平应力（单位：MPa） */
    @Excel(name = "水平应力", readConverterExp = "单=位：MPa")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal horizontalStress;

    /** 其他备注或说明 */
    @Excel(name = "其他备注或说明")
    private String notes;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;


}
