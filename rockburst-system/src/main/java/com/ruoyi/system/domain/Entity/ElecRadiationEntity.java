package com.ruoyi.system.domain.Entity;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.system.domain.BusinessBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author: shikai
 * @date: 2025/8/16
 * @description:
 */

@Data
@ApiModel("电磁辐射")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("elec_radiation")
public class ElecRadiationEntity extends BusinessBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("电磁辐射id")
    @TableId(value = "radiation_id", type = IdType.AUTO)
    @NotNull(groups = {ParameterValidationUpdate.class}, message = "电磁辐射id不能为空")
    private Long radiationId;

    @ApiModelProperty("测点编码")
    @TableField(value = "measure_num")
    private String measureNum;

    @ApiModelProperty("工作面id")
    @TableField(value = "workface_id")
    private Long workFaceId;

    @ApiModelProperty("传感器类型")
    @TableField(value = "sensor_type")
    private String sensorType;

    @ApiModelProperty("传感器安装位置")
    @TableField(value = "sensor_location")
    private String sensorLocation;

    @ApiModelProperty("安装时间")
    @TableField(value = "install_time")
    private Long installTime;

    @ApiModelProperty("状态---是否启用（0是1否）")
    @TableField(value = "status")
    private String status;

    @ApiModelProperty("原始工作面名称(数采上来的数据)")
    @TableField(value = "original_work_face_name")
    private String originalWorkFaceName;

    @ApiModelProperty("删除标志(0存在2删除)")
    @TableField("del_flag")
    @TableLogic(value = "0", delval = "2")
    private String delFlag;

    @ApiModelProperty("标识(1-手动新增，2-数采自动接入)")
    @TableField(value = "tag")
    private String tag;

    @ApiModelProperty("所属矿")
    @TableField(value = "mine_id")
    private Long mineId;

    @ApiModelProperty("所属公司")
    @TableField(value = "company_id")
    private Long companyId;
}