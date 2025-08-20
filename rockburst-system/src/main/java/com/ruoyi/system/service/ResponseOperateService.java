package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.Entity.ResponseOperateEntity;
import com.ruoyi.system.domain.dto.actual.ResponseOperateDTO;

/**
 * @author: shikai
 * @date: 2025/8/20
 * @description:
 */
public interface ResponseOperateService extends IService<ResponseOperateEntity> {

    int addResponseOperate(String warnInstanceNum, ResponseOperateDTO responseOperateDTO, Long mineId);
}