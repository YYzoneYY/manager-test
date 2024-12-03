package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.Entity.AnchorCableStressEntity;
import com.ruoyi.system.mapper.AnchorCableStressMapper;
import com.ruoyi.system.service.AnchorStressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author: shikai
 * @date: 2024/12/3
 * @description:
 */

@Transactional
@Service
public class AnchorStressServiceImpl extends ServiceImpl<AnchorCableStressMapper, AnchorCableStressEntity> implements AnchorStressService {

    @Resource
    private AnchorCableStressMapper anchorCableStressMapper;
}