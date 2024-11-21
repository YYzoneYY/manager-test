package com.ruoyi.system.domain.Entity;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.system.domain.BusinessBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2024/11/19
 * @description:
 */
@Data
@ApiModel("巷道表")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tunnel")
public class TunnelEntity extends BusinessBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "巷道id")
    @TableId(value = "tunnel_id", type = IdType.AUTO)
    private Long tunnelId;

    @ApiModelProperty(value = "巷道名称")
    @TableField("tunnel_name")
    private String tunnelName;

    @ApiModelProperty(value = "工作面id")
    @TableField("work_face_id")
    private Long workFaceId;

    @ApiModelProperty(value = "断面形状")
    @TableField("section_shape")
    private String sectionShape;

    @ApiModelProperty(value = "支护形式")
    @TableField("support_form")
    private String supportForm;

    @ApiModelProperty(value = "巷道宽度")
    @TableField("tunnel_width")
    private BigDecimal tunnelWidth;

    @ApiModelProperty(value = "巷道高度")
    @TableField("tunnel_height")
    private BigDecimal tunnelHeight;

    @ApiModelProperty(value = "巷道长度")
    @TableField("tunnel_length")
    private BigDecimal tunnelLength;

    @ApiModelProperty(value = "巷道面积")
    @TableField("tunnel_area")
    private BigDecimal tunnelArea;

    @ApiModelProperty(value = "巷道状态")
    @TableField("tunnel_status")
    private String tunnelStatus;

    @ApiModelProperty(value = "删除标志(0存在2删除)")
    @TableField("del_flag")
    @TableLogic(value = "0", delval = "2")
    private String delFlag;
}