package com.ruoyi.system.domain.EsEntity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexId;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.rely.Analyzer;
import org.dromara.easyes.annotation.rely.FieldType;
import org.dromara.easyes.annotation.rely.IdType;

import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2025/8/13
 * @description:
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@IndexName(value = "measure_actual", keepGlobalPrefix = false)
public class MeasureActualEntity {

    @IndexId(type = IdType.NONE)
    private String id;

    @IndexField(value = "actualId", fieldType = FieldType.KEYWORD)
    private String actualId;

    @ApiModelProperty(value = "所属矿")
    @IndexField(value = "mineId", fieldType = FieldType.KEYWORD)
    private String mineId;

    @ApiModelProperty(value = "测点编码")
    @IndexField(value = "measureNum", fieldType = FieldType.KEYWORD)
    private String measureNum;

    @ApiModelProperty(value = "传感器编号")
    @IndexField(value = "sensorNum", fieldType = FieldType.KEYWORD)
    private String sensorNum;

    @ApiModelProperty(value = "传感器类型")
    @IndexField(value = "sensorType", fieldType = FieldType.KEYWORD)
    private String sensorType;

    @ApiModelProperty(value = "传感器位置")
    @IndexField(value = "sensorLocation", fieldType = FieldType.TEXT, analyzer = Analyzer.IK_MAX_WORD, searchAnalyzer = Analyzer.IK_SMART)
    private String sensorLocation;

    @ApiModelProperty(value = "监测值")
    @IndexField(value = "monitoringValue", fieldType = FieldType.DOUBLE)
    private BigDecimal monitoringValue;

    @ApiModelProperty(value = "浅基点值")
    @IndexField(value = "valueShallow", fieldType = FieldType.DOUBLE)
    private BigDecimal valueShallow;

    @ApiModelProperty(value = "深基点值")
    @IndexField(value = "valueDeep", fieldType = FieldType.DOUBLE)
    private BigDecimal valueDeep;

    @ApiModelProperty(value = "电磁辐射强度极大值")
    @IndexField(value = "eleMaxValue", fieldType = FieldType.DOUBLE)
    private BigDecimal eleMaxValue;

    @ApiModelProperty(value = "电磁脉冲")
    @IndexField(value = "elePulse", fieldType = FieldType.DOUBLE)
    private BigDecimal elePulse;

    @ApiModelProperty(value = "测点状态")
    @IndexField(value = "monitoringStatus", fieldType = FieldType.KEYWORD)
    private String monitoringStatus;

    @ApiModelProperty("数据时间")
    @IndexField(value = "dataTime", fieldType = FieldType.LONG)
    private Long dataTime;
}