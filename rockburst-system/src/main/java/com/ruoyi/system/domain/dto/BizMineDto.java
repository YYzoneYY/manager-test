package com.ruoyi.system.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseSelfEntity;
import com.ruoyi.system.constant.GroupAdd;
import com.ruoyi.system.constant.GroupUpdate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
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
public class BizMineDto
{

    /** 矿井的唯一标识符 */
    @NotNull(groups = GroupUpdate.class)
    private Long mineId;


    /** 矿井名称 */
    @ApiModelProperty(value = "矿井名称")
    @NotNull(groups = GroupAdd.class)
    private String mineName;

    /** 所属省份 */
    @ApiModelProperty(value = "所属省份")
    @NotNull(groups = GroupAdd.class)
    private String province;

    /** 所属城市 */
    @ApiModelProperty(value = "所属城市")
    @NotNull(groups = GroupAdd.class)
    private String city;

    /** 所属区县 */
    @ApiModelProperty(value = "所属区县")
    @NotNull(groups = GroupAdd.class)
    private String district;

    /** 详细地址 */
    @ApiModelProperty(value = "详细地址")
    @NotNull(groups = GroupAdd.class)
    private String detailedAddress;


    /** 地理位置坐标 (经纬度) */
    @ApiModelProperty(value = "地理位置坐标")
    @NotNull(groups = GroupAdd.class)
    private String location;

    /** 矿井类型（如露天、地下） */
    @ApiModelProperty(value = "矿井类型")
    @NotNull(groups = GroupAdd.class)
    private String type;

    /** 矿井深度（单位：米） */
    @ApiModelProperty(value = "矿井深度")
    @NotNull(groups = GroupAdd.class)
    private BigDecimal depth;

    /** 矿井状态（如运营中、关闭） */
    @ApiModelProperty(value = "矿井状态")
    @NotNull(groups = GroupAdd.class)
    private Integer status;

    /** 矿井年生产能力（单位：吨） */
    @ApiModelProperty(value = "矿井年生产能力")
    private Long capacity;

    /** 矿井所有者或运营公司 */
    @ApiModelProperty(value = "矿井所有者或运营公司")
    private String owner;

    /** 矿井投产日期 */
    @ApiModelProperty(value = "矿井投产日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    /** 上次检查日期 */
    @ApiModelProperty(value = "上次检查日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date lastInspectionDate;

    /** 其他备注或说明 */
    @ApiModelProperty(value = "其他备注或说明")
    private String notes;




}
