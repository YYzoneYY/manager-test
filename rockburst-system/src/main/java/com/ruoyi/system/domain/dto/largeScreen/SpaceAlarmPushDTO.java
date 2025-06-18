package com.ruoyi.system.domain.dto.largeScreen;

import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/6/16
 * @description:
 */

@Data
public class SpaceAlarmPushDTO {

    // 告警类型
    private String alarmType;
    // 告警时间
    private Long alarmTime;
    // 当前钻孔所属工程id
    private Long currentProjectId;
    // 当前钻孔编号
    private String currentDrillNum;
    // 对比钻孔编号
    private String contrastDrillNum;
    // 危险区等级名称
    private String dangerLevelName;
    // 安全间距
    private Double spaced;
    // 实际距离
    private Double actualDistance;
    // 巷道名称
    private String tunnelName;
    // 工作面名称
    private String workFaceName;
}