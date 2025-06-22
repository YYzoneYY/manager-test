package com.ruoyi.system.domain.Entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ruoyi.system.domain.BusinessBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
    @NotNull(groups = {ParameterValidationUpdate.class}, message = "巷道id不能为空")
    @TableId(value = "tunnel_id", type = IdType.AUTO)
    private Long tunnelId;

    @ApiModelProperty(value = "巷道名称")
    @NotBlank(groups = {ParameterValidationOther.class}, message = "巷道名称不能为空")
    @Size(max = 50, groups = {ParameterValidationOther.class}, message = "巷道名称长度不能超过50")
    @TableField("tunnel_name")
    private String tunnelName;

    @ApiModelProperty(value = "工作面id")
    @NotNull(groups = {ParameterValidationOther.class}, message = "工作面id不能为空")
    @TableField("work_face_id")
    private Long workFaceId;

    @ApiModelProperty(value = "巷道类型")
    @NotBlank(groups = {ParameterValidationOther.class}, message = "巷道类型不能为空")
    @TableField("tunnel_type")
    private String tunnelType;

    @ApiModelProperty(value = "断面形状")
    @TableField("section_shape")
    private String sectionShape;

    @ApiModelProperty(value = "支护形式")
    @TableField("support_form")
    private String supportForm;

    @ApiModelProperty(value = "巷道净宽")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "tunnel_width", updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal tunnelWidth;

    @ApiModelProperty(value = "巷道净高")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "tunnel_height", updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal tunnelHeight;

    @ApiModelProperty(value = "巷道长度(设计长度)")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "tunnel_length", updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal tunnelLength;

    @ApiModelProperty(value = "巷道净断面积")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "extent", updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal extent;

    @ApiModelProperty(value = "巷道状态")
    @NotBlank(groups = {ParameterValidationOther.class}, message = "巷道状态不能为空")
    @TableField("tunnel_status")
    private String tunnelStatus;

    @ApiModelProperty("点")
    @TableField()
    private String pointsList;

    @ApiModelProperty(value = "中心点")
    @TableField()
    private String center;

    @ApiModelProperty("迎头距离")
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal headDistance;

    @ApiModelProperty(value = "删除标志(0存在2删除)")
    @TableField("del_flag")
    @TableLogic(value = "0", delval = "2")
    private String delFlag;
    @ApiModelProperty(value = "初始回采进尺位置")
    @TableField()
    private String miningProgress;



    // 计算点m到直线ab的垂线方向与Y轴正方向夹角
    public static double angleWithYAxisOfPerpendicular(BigDecimal[] a1, BigDecimal[] a2, BigDecimal[] m) {
        double x1 = a1[0].doubleValue(), y1 = a1[1].doubleValue();
        double x2 = a2[0].doubleValue(), y2 = a2[1].doubleValue();
        double x0 = m[0].doubleValue(), y0 = m[1].doubleValue();

        double dx = x2 - x1;
        double dy = y2 - y1;

        // 计算垂足 P 到 m 的方向向量
        double len2 = dx * dx + dy * dy;
        if (len2 == 0) {
            // a1 == a2，线段退化成点
            dx = x1 - x0;
            dy = y1 - y0;
        } else {
            double t = ((x0 - x1) * dx + (y0 - y1) * dy) / len2;

            double px = x1 + t * dx;
            double py = y1 + t * dy;

            dx = px - x0;
            dy = py - y0;
        }

        // 求夹角：向量 (dx, dy) 与 y轴正方向 (0,1) 的夹角
        double vlen = Math.sqrt(dx * dx + dy * dy);
        if (vlen == 0) return 0.0; // 点在直线上，方向不确定，返回0°

        double cosTheta = dy / vlen;
        double thetaRad = Math.acos(cosTheta); // ∈ [0, π]
        return Math.toDegrees(thetaRad);       // 返回角度 ∈ [0°, 180°]
    }

    // 字符串解析 "[m,n]" 或 "m,n"
    public static BigDecimal[] parsePoint(String input) {
        if (input == null) throw new IllegalArgumentException("Input is null");
        input = input.trim().replaceAll("[\\[\\]]", "");
        String[] parts = input.split(",");
        if (parts.length != 2) throw new IllegalArgumentException("Invalid input: " + input);
        return new BigDecimal[]{
                new BigDecimal(parts[0].trim()),
                new BigDecimal(parts[1].trim())
        };
    }

    public static void main(String[] args) {
        BigDecimal[] a1 = parsePoint("1748.8763,2272.2789");
        BigDecimal[] a2 = parsePoint("2414.1421,2267.7059");
        BigDecimal[] m = parsePoint("2696.4222,2092.4084");

        double angle = angleWithYAxisOfPerpendicular(a1, a2, m);
        System.out.printf("与 Y 轴正方向夹角: %.2f 度%n", angle);
    }
}