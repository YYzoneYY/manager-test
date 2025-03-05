package com.ruoyi.system.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.ruoyi.common.core.domain.BaseSelfEntity;
import com.ruoyi.system.domain.BizTravePoint;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 矿井危险区对象 BizDangerArea
 *
 * @author ruoyi
 * @date 2024-11-11
 */

@Getter
@Setter
@Accessors(chain = true)
@ApiModel("云图影响因素")
public class YtFactorVo extends BaseSelfEntity
{
    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "开始导线点")
    @TableField(exist = false)
    BizTravePoint startPoint;

    @ApiModelProperty(value = "结束导线点")
    @TableField(exist = false)
    BizTravePoint endPoint;

}
