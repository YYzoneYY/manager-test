package com.ruoyi.system.domain.dto.actual;

import com.ruoyi.system.domain.EsEntity.WarnMessageEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/8/18
 * @description:
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class WarnMessageDTO extends WarnMessageEntity {

    @ApiModelProperty(value = "监测值(处理后)")
    private String monitorValue;

    @ApiModelProperty(value = "监测项")
    private String monitorItems;

    @ApiModelProperty(value = "警情内容")
    private String warnContent;

    @ApiModelProperty(value = "多参量分析数据")
    List<ParamAnalyzeDTO> paramAnalyzeDTOs;

    @ApiModelProperty(value = "曲线图数据")
    private List<LineChartDTO> lineChartDTOs;

    @ApiModelProperty(value = "开始时间格式化")
    private String startTimeFmt;

    @ApiModelProperty(value = "结束时间格式化")
    private String endTimeFmt;
}