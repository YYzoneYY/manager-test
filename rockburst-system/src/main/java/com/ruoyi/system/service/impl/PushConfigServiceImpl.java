package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.Entity.PushConfigEntity;
import com.ruoyi.system.domain.dto.PushConfigDTO;
import com.ruoyi.system.mapper.PushConfigMapper;
import com.ruoyi.system.service.PushConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: shikai
 * @date: 2025/7/2
 * @description:
 */

@Service
@Transactional
public class PushConfigServiceImpl extends ServiceImpl<PushConfigMapper, PushConfigEntity> implements PushConfigService {

    @Resource
    private PushConfigMapper pushConfigMapper;

    @Override
    public boolean batchInsert(List<PushConfigDTO> pushConfigDTOS) {
        boolean flag = false;
        if (ObjectUtil.isNull(pushConfigDTOS)) {
            throw new RuntimeException("参数不能为空");
        }
        List<PushConfigEntity> pushConfigEntities = new ArrayList<>();
        pushConfigDTOS.forEach(pushConfigDTO -> {
            PushConfigEntity pushConfigEntity = new PushConfigEntity();
            pushConfigEntity.setTag(pushConfigDTO.getTag());
            pushConfigEntity.setUserIdGroup(pushConfigDTO.getUserIdGroup().toString());
            pushConfigEntities.add(pushConfigEntity);
        });
        flag = this.saveBatch(pushConfigEntities);
        return flag;
    }
}