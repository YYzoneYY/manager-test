package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.query.MPJQueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.system.domain.vo.BizProjectRecordVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.BizProjectRecordMapper;
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.service.IBizProjectRecordService;

/**
 * 工程填报记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Service
public class BizProjectRecordServiceImpl extends ServiceImpl<BizProjectRecordMapper, BizProjectRecord> implements IBizProjectRecordService
{
    @Autowired
    private BizProjectRecordMapper bizProjectRecordMapper;

    @DataScope(deptAlias = "dss")
    public List<BizProjectRecord> getlist(BizProjectRecord bizProjectRecord){
        MPJLambdaWrapper<BizProjectRecord> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper.selectAll(BizProjectRecord.class)
                .leftJoin(SysDept.class,SysDept::getDeptId,BizProjectRecord::getDeptId)
                .selectAs(SysDept::getDeptName,BizProjectRecordVo::getDeptName);
        List<BizProjectRecordVo> sss = bizProjectRecordMapper.selectJoinList(BizProjectRecordVo.class, queryWrapper);
        System.out.println("sss = " + JSONUtil.toJsonStr(sss));
        return new ArrayList<>();
    }

    @Override
    public List<BizProjectRecordVo> auditList(BizProjectRecord bizProjectRecord) {
        return null;
    }
}







