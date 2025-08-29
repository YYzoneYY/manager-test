package com.ruoyi.system.domain.dto.actual;

import com.ruoyi.system.domain.dto.largeScreen.AlarmMessage;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2025/6/13
 * @description:
 */

@Data
public class MeasureWarnPushDTO implements AlarmMessage {

    // 预警id
    private Long alarmId;
    // 警情编号
    private String warnInstanceNum;
    // 预警类型
    private String alarmType;
    // 预警时间
    private Long alarmTime;
    // 预警内容
    private String alarmContent;
    // 监测值
    private BigDecimal monitoringValue;
    // 监测项
    private String monitoringItems;
}