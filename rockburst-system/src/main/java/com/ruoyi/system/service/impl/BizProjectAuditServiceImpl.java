package com.ruoyi.system.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.ruoyi.system.mapper.BizProjectAuditMapper;
import com.ruoyi.system.domain.BizProjectAudit;
import com.ruoyi.system.service.IBizProjectAuditService;
import org.springframework.stereotype.Service;

/**
 * 工程填报审核记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Service
public class BizProjectAuditServiceImpl  extends ServiceImpl<BizProjectAuditMapper, BizProjectAudit> implements IBizProjectAuditService
{
    @Autowired
    private BizProjectAuditMapper bizProjectAuditMapper;



}
