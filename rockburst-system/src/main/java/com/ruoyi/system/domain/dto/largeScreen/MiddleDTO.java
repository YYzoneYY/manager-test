package com.ruoyi.system.domain.dto.largeScreen;

import lombok.Data;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/6/9
 * @description:
 */

@Data
public class MiddleDTO {

    private String constructType;

    private List<Long> projectIds;

    private Long tunnelId;
}