package com.ruoyi.system.domain.dto;

import com.ruoyi.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/6/17
 * @description:
 */

@Data
public class ImportDrillMappingDTO {

    @ApiModelProperty("序号")
    @Excel(name = "序号", sort = 1)
    private Integer num;

    @ApiModelProperty("标高")
    @Excel(name = "标高", sort = 2)
    private String elevation;

    @ApiModelProperty("岩性")
    @Excel(name = "岩性", sort = 3)
    private String lithology;

    @ApiModelProperty("岩性编号")
    @Excel(name = "岩性编号", sort = 4)
    private String lithologyNum;

    @ApiModelProperty("厚度")
    @Excel(name = "厚度", sort = 5)
    private String thickness;

    @ApiModelProperty("埋深")
    @Excel(name = "埋深", sort = 6)
    private String buriedDepth;

    @ApiModelProperty("岩性描述")
    @Excel(name = "岩性描述", sort = 7)
    private String lithologyDescribe;

    @ApiModelProperty("备注")
    @Excel(name = "备注", sort = 8)
    private String remarks;
}