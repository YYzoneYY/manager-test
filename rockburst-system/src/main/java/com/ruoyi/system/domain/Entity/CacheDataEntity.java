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
 * @date: 2025/2/17
 * @description:
 */

@Data
@ApiModel("缓冲数据表")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("cache_data")
public class CacheDataEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "cache_data_id", type = IdType.AUTO)
    private Long cacheDataId;

    @ApiModelProperty(value = "工程id")
    @TableField(value = "project_id")
    private Long projectId;

    @ApiModelProperty(value = "序号")
    @TableField(value = "no")
    private Integer no;

    @ApiModelProperty(value = "所属危险区id")
    @TableField(value = "danger_area_id")
    private Long dangerAreaId;
}