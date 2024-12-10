package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.Entity.ContentsEntity;
import com.ruoyi.system.mapper.ContentsMapper;
import com.ruoyi.system.service.ContentsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: shikai
 * @date: 2024/12/8
 * @description:
 */

@Service
public class ContentsServiceImpl extends ServiceImpl<ContentsMapper, ContentsEntity> implements ContentsService {

    @Resource
    private ContentsMapper contentsMapper;

}