package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.Entity.WarnSchemeEntity;
import com.ruoyi.system.mapper.WarnSchemeMapper;
import com.ruoyi.system.service.WarnSchemeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author: shikai
 * @date: 2024/11/29
 * @description:
 */

@Service
public class WarnSchemeServiceImpl extends ServiceImpl<WarnSchemeMapper, WarnSchemeEntity> implements WarnSchemeService {

    @Resource
    private WarnSchemeMapper warnSchemeMapper;
}