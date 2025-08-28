package com.ruoyi.system.domain.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author: shikai
 * @date: 2025/8/19
 * @description:
 */

@Data
@ApiModel("多参量方案")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("multiple_param_plan")
public class MultiplePlanEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("多参量方案id")
    @TableId(value = "multiple_plan_id", type = IdType.AUTO)
    private Long multiplePlanId;

    @ApiModelProperty("参量名称")
    @TableField(value = "param_name")
    private String paramName;

    @ApiModelProperty("警情编号")
    @TableField(value = "warnInstanceNum")
    private String warnInstanceNum;

    @ApiModelProperty("测点编码")
    @TableField(value = "measure_num")
    private String measureNum;

    @ApiModelProperty("传感器位置")
    @TableField(value = "location")
    private String location;

    @ApiModelProperty(value = "监测项")
    @TableField(value = "monitor_items")
    private String monitorItems;

    @ApiModelProperty("工作面id")
    @TableField(value = "workface_id")
    private Long workFaceId;

    @ApiModelProperty("传感器类型")
    @TableField(value = "sensor_type")
    private String sensorType;

    @ApiModelProperty("所属矿")
    @TableField(value = "mine_id")
    private Long mineId;

    @ApiModelProperty("所属公司")
    @TableField(value = "company_id")
    private Long companyId;

}