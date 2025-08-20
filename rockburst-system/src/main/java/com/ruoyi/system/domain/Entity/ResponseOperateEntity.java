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
@ApiModel("预警响应")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("response_operate")
public class ResponseOperateEntity extends BusinessBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("响应操作id")
    @TableId(value = "response_operate_id", type = IdType.AUTO)
    private Long responseOperateId;

    @ApiModelProperty("警情编号")
    @TableField(value = "warnInstanceNum")
    private String warnInstanceNum;

    @ApiModelProperty("预警详情")
    @TableField(value = "warn_details")
    private String warnDetails;

    @ApiModelProperty("事故类别")
    @TableField(value = "accident_type")
    private String accidentType;

    @ApiModelProperty("事故级别")
    @TableField(value = "accident_level")
    private String accidentLevel;

    @ApiModelProperty("事故描述")
    @TableField(value = "accident_depict")
    private String accidentDepict;

    @ApiModelProperty("所属矿")
    @TableField(value = "mine_id")
    private Long mineId;

    @ApiModelProperty("所属公司")
    @TableField(value = "company_id")
    private Long companyId;
}