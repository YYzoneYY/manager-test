package com.ruoyi.system.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.BizVideoMapper;
import com.ruoyi.system.domain.BizVideo;
import com.ruoyi.system.service.IBizVideoService;

/**
 * 工程视频Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Service
public class BizVideoServiceImpl extends ServiceImpl<BizVideoMapper, BizVideo> implements IBizVideoService
{
    @Autowired
    private BizVideoMapper bizVideoMapper;


}
