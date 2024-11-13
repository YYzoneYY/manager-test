package com.ruoyi.system.domain.Entity;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.system.domain.BusinessBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2024/11/13
 * @description:
 */
@Data
@ApiModel("回采进尺表")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("miming_footage")
public class MiningFootageEntity extends BusinessBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("回采进尺id")
    @TableId(value = "mining_footage_id", type = IdType.AUTO)
    private Long miningFootageId;

    @ApiModelProperty("工作面id")
    @TableField("workface_id")
    private Long workfaceId;

    @ApiModelProperty("回采时间")
    @TableField("mining_time")
    private Long miningTime;

    @ApiModelProperty("回采进度")
    @TableField("mining_pace")
    private BigDecimal miningPace;

    //0正常数据 1未填写的 2修改 3 擦除
    @ApiModelProperty("修改标识")
    @TableField("flag")
    private String flag;

    @ApiModelProperty(value = "删除标志(0存在2删除)")
    @TableLogic(value = "0", delval = "2")
    @TableField("del_flag")
    private String delFlag;
}