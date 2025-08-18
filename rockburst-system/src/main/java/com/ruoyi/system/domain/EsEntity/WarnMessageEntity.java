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
 * @date: 2025/8/18
 * @description:
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@IndexName(value = "warn_message", keepGlobalPrefix = false)
public class WarnMessageEntity {

    @IndexId(type = IdType.NONE)
    private String id;

    @IndexField(value = "warnMessageId", fieldType = FieldType.KEYWORD)
    private String warnMessageId;

    @ApiModelProperty(value = "所属矿")
    @IndexField(value = "mineId", fieldType = FieldType.KEYWORD)
    private String mineId;

    @ApiModelProperty(value = "警情编号")
    @IndexField(value = "warnInstanceNum", fieldType = FieldType.KEYWORD)
    private String warnInstanceNum;

    @ApiModelProperty(value = "测点编码")
    @IndexField(value = "measureNum", fieldType = FieldType.KEYWORD)
    private String measureNum;

    @ApiModelProperty(value = "工作面id")
    @IndexField(value = "workFaceId", fieldType = FieldType.KEYWORD)
    private Long workFaceId;

    @ApiModelProperty(value = "传感器类型")
    @IndexField(value = "sensorType", fieldType = FieldType.KEYWORD)
    private String sensorType;

    @ApiModelProperty(value = "监测项")
    @IndexField(value = "monitorItems", fieldType = FieldType.KEYWORD)
    private String monitorItems;

    @ApiModelProperty(value = "监测值")
    @IndexField(value = "monitoringValue", fieldType = FieldType.DOUBLE)
    private BigDecimal monitoringValue;

    @ApiModelProperty(value = "预警类型")
    @IndexField(value = "warnType", fieldType = FieldType.KEYWORD)
    private String warnType;

    @ApiModelProperty(value = "预警等级")
    @IndexField(value = "warnLevel", fieldType = FieldType.KEYWORD)
    private String warnLevel;

    @ApiModelProperty(value = "预警位置")
    @IndexField(value = "warnLocation", fieldType = FieldType.TEXT, analyzer = Analyzer.IK_MAX_WORD, searchAnalyzer = Analyzer.IK_SMART)
    private String warnLocation;

    @ApiModelProperty(value = "开始时间")
    @IndexField(value = "startTime", fieldType = FieldType.LONG)
    private Long startTime;

    @ApiModelProperty(value = "结束时间")
    @IndexField(value = "endTime", fieldType = FieldType.LONG)
    private Long endTime;

    @ApiModelProperty(value = "预警状态")
    @IndexField(value = "warnStatus", fieldType = FieldType.KEYWORD)
    private String warnStatus;

    @ApiModelProperty(value = "处理状态")
    @IndexField(value = "handStatus", fieldType = FieldType.KEYWORD)
    private String handStatus;
}