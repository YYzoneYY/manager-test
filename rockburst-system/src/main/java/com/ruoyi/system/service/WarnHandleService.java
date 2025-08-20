package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.Entity.WarnHandleEntity;
import com.ruoyi.system.domain.dto.WarnSchemeDTO;
import com.ruoyi.system.domain.dto.actual.WarnHandleDTO;

/**
 * @author: shikai
 * @date: 2025/8/20
 * @description:
 */
public interface WarnHandleService extends IService<WarnHandleEntity> {

    int addWarnHandle(String warnInstanceNum, WarnHandleDTO warnHandleDTO, Long mineId);
}