package com.ruoyi.system.domain.vo;

import com.ruoyi.system.domain.dto.DrillPropertiesDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/6/19
 * @description:
 */

@Data
public class GeologyDrillVO {

    @ApiModelProperty("地质钻孔数据id")
    private Long geologyDrillId;

    @ApiModelProperty("钻孔名称")
    private String dataName;

    @ApiModelProperty("标高")
    private String groundElevation;

    @ApiModelProperty("底板标高")
    private String baseElevation;

    @ApiModelProperty("煤层厚度")
    private String coalThickness;

    @ApiModelProperty("中心点坐标")
    private String center;

    @ApiModelProperty("地质钻孔属性信息")
    private List<DrillPropertiesDTO> drillPropertiesDTOS;
}