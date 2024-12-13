package com.ruoyi.system.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 矿井管理对象 biz_mine
 *
 * @author ruoyi
 * @date 2024-11-11
 */

@Getter
@Setter
public class BizMineDto
{

    /** 矿井的唯一标识符 */
    @TableId(type = IdType.AUTO)
    private Long mineId;

    /** 焊矿编码 */
    @ApiModelProperty(value = "焊矿编码")
    private String mineCode;

    /** 矿井名称 */
    @ApiModelProperty(value = "矿井名称")
    private String mineName;

    /** 社会统一代码 */
    @ApiModelProperty(value = "社会统一代码")
    private String socialCreditCode;

    /** 所属集团 */
    @ApiModelProperty(value = "所属集团")
    private String groupAffiliation;

    /** 煤矿地址 */
    @ApiModelProperty(value = "煤矿地址")
    private String mineAddress;

    /** 井田面积 (km) */
    @ApiModelProperty(value = "井田面积 (km)")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal mineArea;

    /** 煤炭资源储量 (万吨) */
    @ApiModelProperty(value = "煤炭资源储量 (万吨)")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal coalResources;

    /** 核定生产能力 (万吨/年) */
    @ApiModelProperty(value = "核定生产能力 (万吨/年)")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal productionCapacity;

    /** 煤矿类型 */
    @ApiModelProperty(value = "煤矿类型")
    private String mineType;

    /** 生产许可状态 */
    @ApiModelProperty(value = "生产许可状态")
    private String productionLicenseStatus;

    /** 主管政府部门级别 */
    @ApiModelProperty(value = "主管政府部门级别")
    private String governmentLevel;

    /** 法定代表人 */
    @ApiModelProperty(value = "法定代表人")
    private String legalRepresentative;

    /** 法定代表人电话 */
    @ApiModelProperty(value = "法定代表人电话")
    private String legalRepresentativePhone;

    /** 企业负责人 */
    @ApiModelProperty(value = "企业负责人")
    private String enterpriseResponsible;

    /** 企业负责人电话 */
    @ApiModelProperty(value = "企业负责人电话")
    private String enterpriseResponsiblePhone;

    /** 安全生产标准化等级 */
    @ApiModelProperty(value = "安全生产标准化等级")
    private String safetyStandardLevel;

    /** 矿井井型 */
    @ApiModelProperty(value = "矿井井型")
    private String mineStructure;

    /** 允许最大采深 (m) */
    @ApiModelProperty(value = "允许最大采深 (m)")
    private Long maxDepth;

    /** 矿井状况 */
    @ApiModelProperty(value = "矿井状况")
    private String status;

    /** 开拓方式 */
    @ApiModelProperty(value = "开拓方式")
    private String miningMethod;

    /** 设计生产能力 (万吨/年) */
    @ApiModelProperty(value = "设计生产能力 (万吨/年)")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal designedProductionCapacity;

    /** 开采类型 */
    @ApiModelProperty(value = "开采类型")
    private String miningType;

    /** 运输方式 */
    @ApiModelProperty(value = "运输方式")
    private String transportationMethod;

    /** 供电方式 */
    @ApiModelProperty(value = "供电方式")
    private String powerSupplyMethod;

    /** 通风方式 */
    @ApiModelProperty(value = "通风方式")
    private String ventilationMethod;

    /** 主要灾害类型 */
    @ApiModelProperty(value = "主要灾害类型")
    private String majorHazards;

    /** 矿井地质类型 */
    @ApiModelProperty(value = "矿井地质类型")
    private String mineGeologicalType;

    /** 水文地质类型 */
    @ApiModelProperty(value = "水文地质类型")
    private String hydrologicalGeologicalType;

    /** 矿井正常涌水量 */
    @ApiModelProperty(value = "矿井正常涌水量")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal normalWaterInflow;

    /** 矿井最大涌水量 */
    @ApiModelProperty(value = "矿井最大涌水量")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal maxWaterInflow;

    /** 瓦斯等级 */
    @ApiModelProperty(value = "瓦斯等级")
    private String gasLevel;

    /** 煤尘爆炸性 */
    @ApiModelProperty(value = "煤尘爆炸性")
    private String coalDustExplosiveness;

    /** 煤层自燃倾向性 */
    @ApiModelProperty(value = "煤层自燃倾向性")
    private String coalLayerSelfIgnition;

    /** 是否有冲击地压 */
    @ApiModelProperty(value = "是否有冲击地压")
    private Boolean hasRockburst;

    /** 煤层顶板岩性 */
    @ApiModelProperty(value = "煤层顶板岩性")
    private String coalLayerRoofRockType;


    /** 备注 */
    @ApiModelProperty(value = "备注")
    private String remark;

    /** 矿图id */
    @ApiModelProperty(value = "矿图id")
    private Long tableId;


}
