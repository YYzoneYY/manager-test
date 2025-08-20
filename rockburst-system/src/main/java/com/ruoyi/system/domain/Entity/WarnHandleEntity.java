package com.ruoyi.system.domain.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.system.domain.BusinessBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author: shikai
 * @date: 2025/8/20
 * @description:
 */

@Data
@ApiModel("警情处理")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("warn_handle")
public class WarnHandleEntity extends BusinessBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("处理id")
    @TableId(value = "warn_handle_id", type = IdType.AUTO)
    private Long warnHandleId;

    @ApiModelProperty("警情编号")
    @TableField(value = "warnInstanceNum")
    private String warnInstanceNum;

    @ApiModelProperty("预警详情")
    @TableField(value = "warn_details")
    private String warnDetails;

    @ApiModelProperty("处理人")
    @TableField(value = "handle_name")
    private String handleName;

    @ApiModelProperty("处理状态")
    @TableField(value = "handle_status")
    private String handleStatus;

    @ApiModelProperty("是否执行应急响应")
    @TableField(value = "is_response")
    private String isResponse;

    @ApiModelProperty("处理详情")
    @TableField(value = "handle_details")
    private String handleDetails;

    @ApiModelProperty("所属矿")
    @TableField(value = "mine_id")
    private Long mineId;

    @ApiModelProperty("所属公司")
    @TableField(value = "company_id")
    private Long companyId;
}