package com.ruoyi.system.domain.Entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.system.domain.BusinessBaseEntity;
import io.swagger.annotations.ApiModel;
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
    private Long tunnelId;
    private String tunnelName;
    private Long workFaceId;
    private String sectionShape;
    private String supportForm;
    private String tunnelType;
    private BigDecimal tunnelWidth;
    private BigDecimal tunnelHeight;
    private BigDecimal tunnelArea;
}