package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ruoyi.common.core.domain.BaseSelfEntity;
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
public class SysDxfEntity extends BaseSelfEntity
{
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long Id;

    @ApiModelProperty(value = "名称")
    private String dxfKey;

    @ApiModelProperty(value = "类型")
    private String dxfValue;



}
