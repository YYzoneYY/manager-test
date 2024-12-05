package com.ruoyi.system.domain.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author: shikai
 * @date: 2024/12/2
 * @description:
 */

@Data
public class BatchOperateDTO {

    private List<Map<String, Object>> maps;
}