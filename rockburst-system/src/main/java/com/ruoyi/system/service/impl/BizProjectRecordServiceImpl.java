package com.ruoyi.system.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.query.MPJQueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.PageUtils;
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

    @DataScope(deptAlias = "dss",userAlias = "a")
    public List<BizProjectRecord> getlist(BizProjectRecord bizProjectRecord){
        MPJQueryWrapper<BizProjectRecord> queryWrapper = new MPJQueryWrapper<>();
        System.out.println("bizProjectRecord = " + bizProjectRecord);

        queryWrapper.selectAll(BizProjectRecord.class).leftJoin("sys_dept dss on dss.dept_id = t.dept_id");
//                        .apply(bizProjectRecord.getParams())
//        queryWrapper.lambda().eq(BizProjectRecord::getProjectId, 1);
        queryWrapper.apply(bizProjectRecord.getParams().get("dataScope").toString());
        return bizProjectRecordMapper.selectList(queryWrapper);
    }

}
