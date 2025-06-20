package com.ruoyi.system.domain.dto;

import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/6/13
 * @description:
 */

@Data
public class WarningDTO {

    private Long currentProjectId;
    // 当前钻孔编号
    private String currentDrillNum;
    // 前一个/后一个钻孔编号
    private String relatedDrillNum;
    // 前一个/后一个工程id
    private Long relatedProjectId;
    // 危险区等级名称
    private String dangerLevelName;
    // 安全间距
    private Double spaced;
    // 实际距离
    private Double actualDistance;
    // 报警时间
    private Long alarmTime;
    // 是否为前后两个钻孔中间的孔
    private boolean betweenDrills;

    private String tunnelName;
    private String workFaceName;

    private Double expectedPosition;
    private String ruleType;
}