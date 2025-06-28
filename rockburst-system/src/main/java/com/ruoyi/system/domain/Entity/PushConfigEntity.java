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
 * @date: 2025/6/28
 * @description:
 */
@Data
@ApiModel("推送配置表")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("push_config")
public class PushConfigEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("推送配置id")
    @TableId(value = "push_config_id", type = IdType.AUTO)
    private Long pushConfigId;

    @ApiModelProperty("标识")
    @TableField("tag")
    private String tag;

    @ApiModelProperty("用户id组")
    @TableField("user_id_group")
    private String userIdGroup;
}