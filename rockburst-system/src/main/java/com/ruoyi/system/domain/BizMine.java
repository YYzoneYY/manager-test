package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ruoyi.common.core.domain.BaseSelfEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

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
    @TableId(type = IdType.AUTO)
    private Long mineId;

    /** 焊矿编码 */
    @ApiModelProperty(value = "焊矿编码")
    @TableField("mine_code")
    private String mineCode;

    /** 矿井名称 */
    @ApiModelProperty(value = "坐标")
    private String axis;

    /** 矿井名称 */
    @ApiModelProperty(value = "矿井名称")
    @TableField("mine_name")
    private String mineName;

    /** 社会统一代码 */
    @ApiModelProperty(value = "社会统一代码")
    @TableField("social_credit_code")
    private String socialCreditCode;

    /** 所属集团 */
    @ApiModelProperty(value = "所属集团")
    @TableField("group_affiliation")
    private String groupAffiliation;

    /** 煤矿地址 */
    @ApiModelProperty(value = "煤矿地址")
    @TableField("mine_address")
    private String mineAddress;

    /** 井田面积 (km) */
    @ApiModelProperty(value = "井田面积 (km)")
    @TableField("mine_area")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal mineArea;

    /** 煤炭资源储量 (万吨) */
    @ApiModelProperty(value = "煤炭资源储量 (万吨)")
    @TableField("coal_resources")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal coalResources;

    /** 核定生产能力 (万吨/年) */
    @ApiModelProperty(value = "核定生产能力 (万吨/年)")
    @TableField("production_capacity")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal productionCapacity;

    /** 煤矿类型 */
    @ApiModelProperty(value = "煤矿类型")
    @TableField("mine_type")
    private String mineType;

    /** 生产许可状态 */
    @ApiModelProperty(value = "生产许可状态")
    @TableField("production_license_status")
    private String productionLicenseStatus;

    /** 主管政府部门级别 */
    @ApiModelProperty(value = "主管政府部门级别")
    @TableField("government_level")
    private String governmentLevel;

    /** 法定代表人 */
    @ApiModelProperty(value = "法定代表人")
    @TableField("legal_representative")
    private String legalRepresentative;

    /** 法定代表人电话 */
    @ApiModelProperty(value = "法定代表人电话")
    @TableField("legal_representative_phone")
    private String legalRepresentativePhone;

    /** 企业负责人 */
    @ApiModelProperty(value = "企业负责人")
    @TableField("enterprise_responsible")
    private String enterpriseResponsible;

    /** 企业负责人电话 */
    @ApiModelProperty(value = "企业负责人电话")
    @TableField("enterprise_responsible_phone")
    private String enterpriseResponsiblePhone;

    /** 安全生产标准化等级 */
    @ApiModelProperty(value = "安全生产标准化等级")
    @TableField("safety_standard_level")
    private String safetyStandardLevel;

    /** 矿井井型 */
    @ApiModelProperty(value = "矿井井型")
    @TableField("mine_structure")
    private String mineStructure;

    /** 允许最大采深 (m) */
    @ApiModelProperty(value = "允许最大采深 (m)")
    @TableField("max_depth")
    private Long maxDepth;

    /** 矿井状况 */
    @ApiModelProperty(value = "矿井状况")
    @TableField("status")
    private String status;

    /** 开拓方式 */
    @ApiModelProperty(value = "开拓方式")
    @TableField("mining_method")
    private String miningMethod;

    /** 设计生产能力 (万吨/年) */
    @ApiModelProperty(value = "设计生产能力 (万吨/年)")
    @TableField("designed_production_capacity")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal designedProductionCapacity;

    /** 开采类型 */
    @ApiModelProperty(value = "开采类型")
    @TableField("mining_type")
    private String miningType;

    /** 运输方式 */
    @ApiModelProperty(value = "运输方式")
    @TableField("transportation_method")
    private String transportationMethod;

    /** 供电方式 */
    @ApiModelProperty(value = "供电方式")
    @TableField("power_supply_method")
    private String powerSupplyMethod;

    /** 通风方式 */
    @ApiModelProperty(value = "通风方式")
    @TableField("ventilation_method")
    private String ventilationMethod;

    /** 主要灾害类型 */
    @ApiModelProperty(value = "主要灾害类型")
    @TableField("major_hazards")
    private String majorHazards;

    /** 矿井地质类型 */
    @ApiModelProperty(value = "矿井地质类型")
    @TableField("mine_geological_type")
    private String mineGeologicalType;

    /** 水文地质类型 */
    @ApiModelProperty(value = "水文地质类型")
    @TableField("hydrological_geological_type")
    private String hydrologicalGeologicalType;

    /** 矿井正常涌水量 */
    @ApiModelProperty(value = "矿井正常涌水量")
    @TableField("normal_water_inflow")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal normalWaterInflow;

    /** 矿井最大涌水量 */
    @ApiModelProperty(value = "矿井最大涌水量")
    @TableField("max_water_inflow")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal maxWaterInflow;

    /** 瓦斯等级 */
    @ApiModelProperty(value = "瓦斯等级")
    @TableField("gas_level")
    private String gasLevel;

    /** 煤尘爆炸性 */
    @ApiModelProperty(value = "煤尘爆炸性")
    @TableField("coal_dust_explosiveness")
    private String coalDustExplosiveness;

    /** 煤层自燃倾向性 */
    @ApiModelProperty(value = "煤层自燃倾向性")
    @TableField("coal_layer_self_ignition")
    private String coalLayerSelfIgnition;

    /** 是否有冲击地压 */
    @ApiModelProperty(value = "是否有冲击地压")
    @TableField("has_rockburst")
    private Boolean hasRockburst;

    /** 煤层顶板岩性 */
    @ApiModelProperty(value = "煤层顶板岩性")
    @TableField("coal_layer_roof_rock_type")
    private String coalLayerRoofRockType;


    /** 矿图id */
    @ApiModelProperty(value = "矿图id")
    @TableField("table_id")
    private Long tableId;

    @ApiModelProperty(value = "svg")
    @TableField()
    private String svg;

    @ApiModelProperty(value = "中心点")
    @TableField()
    private String center;


    /** 矿图id */
    @ApiModelProperty(value = "jituanid")
    private Long companyId;

}
