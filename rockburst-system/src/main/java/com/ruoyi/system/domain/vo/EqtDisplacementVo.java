package com.ruoyi.system.domain.vo;

import com.ruoyi.system.domain.EqtDisplacement;
import com.ruoyi.system.domain.dto.WarnSchemeDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author: 吴大林
 * @date: 2024/12/2
 * @description:
 */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class EqtDisplacementVo extends EqtDisplacement {



    @ApiModelProperty(value = "预警方案相关信息")
    private WarnSchemeDTO warnSchemeDTO;
}