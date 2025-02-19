package com.ruoyi.system.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.ruoyi.system.domain.BizDangerArea;
import com.ruoyi.system.domain.BizDangerLevel;
import com.ruoyi.system.domain.BizTravePoint;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 采区管理对象 biz_mining_area
 * 
 * @author ruoyi
 * @date 2024-11-11
 */

@Getter
@Setter
@Accessors(chain = true)
@ApiModel("矿井危险区对象")
public class BizDangerAreaVo extends BizDangerArea
{

    @ApiModelProperty(value = "危险等级")
    @TableField(exist = false)
    BizDangerLevel bizDangerLevel;

    @ApiModelProperty(value = "开始导线点")
    @TableField(exist = false)
    BizTravePoint startPoint;

    @ApiModelProperty(value = "结束导线点")
    @TableField(exist = false)
    BizTravePoint endPoint;



}
