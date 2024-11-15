package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.Entity.ConstructionPersonnelEntity;
import com.ruoyi.system.mapper.ConstructionPersonnelMapper;
import com.ruoyi.system.service.ConstructionPersonnelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author: shikai
 * @date: 2024/11/15
 * @description:
 */
@Service
@Transactional
public class ConstructionPersonnelServiceImpl extends ServiceImpl<ConstructionPersonnelMapper, ConstructionPersonnelEntity> implements ConstructionPersonnelService {

    @Resource
    private ConstructionPersonnelMapper constructionPersonnelMapper;
}