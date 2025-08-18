package com.ruoyi.system.service;

import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.dto.actual.WarnMessageDTO;
import com.ruoyi.system.domain.dto.actual.WarnSelectDTO;

/**
 * @author: shikai
 * @date: 2025/8/18
 * @description:
 */
public interface WarnMessageService {

    Boolean createIndex();

    TableData warnMessagePage(WarnSelectDTO warnSelectDTO, Long mineId, Integer pageNum, Integer pageSize);

    WarnMessageDTO detail(String warnInstanceNum, Long mineId);
}