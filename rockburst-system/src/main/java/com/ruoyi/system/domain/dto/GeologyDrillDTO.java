package com.ruoyi.system.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/6/17
 * @description:
 */

@Data
public class GeologyDrillDTO {

    @ApiModelProperty("钻孔名称")
    private String dataName;

    @ApiModelProperty("标高")
    private String groundElevation;

    @ApiModelProperty("底板标高")
    private String baseElevation;

    @ApiModelProperty("煤层厚度")
    private String coalThickness;

    @ApiModelProperty("中心坐标")
    private String center;
}