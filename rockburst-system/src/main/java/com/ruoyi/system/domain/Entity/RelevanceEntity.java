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
 * @date: 2025/9/3
 * @description:
 */

@Data
@ApiModel("测点-工作面关联")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("relevance")
public class RelevanceEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("关联信息id")
    @TableId(value = "relevance_id", type = IdType.AUTO)
    private Long relevanceId;

    @ApiModelProperty("测点编码")
    @TableField(value = "measure_num")
    private String measureNum;

    @ApiModelProperty("传感器类型")
    @TableField(value = "sensor_type")
    private String sensorType;

    @ApiModelProperty("原始工作面名称(数采上来的数据)")
    @TableField(value = "original_work_face_name")
    private String originalWorkFaceName;

    @ApiModelProperty("工作面id(关联后)")
    @TableField(value = "workface_id")
    private Long workFaceId;

    @ApiModelProperty("所属矿")
    @TableField(value = "mine_id")
    private Long mineId;

    @ApiModelProperty("所属公司")
    @TableField(value = "company_id")
    private Long companyId;
}