package com.ruoyi.system.service;

import com.ruoyi.common.core.page.TableData;

import java.util.Date;

/**
 * @author: shikai
 * @date: 2025/8/28
 * @description:
 */
public interface PrintTypesetService {

    TableData queryPage(Date startTime, Date endTime, String drillNum, Long mineId, Integer pageNum, Integer pageSize);
}