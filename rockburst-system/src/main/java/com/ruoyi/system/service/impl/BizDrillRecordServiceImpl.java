package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.BizDrillRecordMapper;
import com.ruoyi.system.domain.BizDrillRecord;
import com.ruoyi.system.service.IBizDrillRecordService;

/**
 * 钻孔参数记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Service
public class BizDrillRecordServiceImpl  extends ServiceImpl<BizDrillRecordMapper, BizDrillRecord> implements IBizDrillRecordService

{
    @Autowired
    private BizDrillRecordMapper bizDrillRecordMapper;



}
