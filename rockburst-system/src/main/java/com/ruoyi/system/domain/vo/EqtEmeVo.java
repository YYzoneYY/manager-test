package com.ruoyi.system.domain.vo;

import com.ruoyi.system.domain.EqtEme;
import com.ruoyi.system.domain.dto.WarnSchemeDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author: 吴大林
 * @date: 2024/12/2
 * @description:
 */

@Getter
@Setter
@Accessors(chain = true)
public class EqtEmeVo extends EqtEme {



    @ApiModelProperty(value = "预警方案相关信息")
    private WarnSchemeDTO warnSchemeDTO;
}