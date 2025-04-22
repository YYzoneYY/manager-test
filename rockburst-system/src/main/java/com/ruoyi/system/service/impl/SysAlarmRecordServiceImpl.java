package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ruoyi.common.core.domain.BasePermission;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.SysAlarmRecord;
import com.ruoyi.system.mapper.SysAlarmRecordMapper;
import com.ruoyi.system.service.ISysAlarmRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 工程视频Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Service
public class SysAlarmRecordServiceImpl extends ServiceImpl<SysAlarmRecordMapper, SysAlarmRecord> implements ISysAlarmRecordService
{
    @Autowired
    private SysAlarmRecordMapper sysAlarmRecordMapper;

    @Override
    public MPage<SysAlarmRecord> pageList(BasePermission permission, SysAlarmRecord record , Pagination pagination) {

        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        MPJLambdaWrapper<SysAlarmRecord> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper
                .eq(record.getStatus() !=null , SysAlarmRecord::getStatus,record.getStatus())
                .eq(SysAlarmRecord::getUserId,currentUser.getUserId())
                .orderByAsc(SysAlarmRecord::getStatus)
                .orderByDesc(SysAlarmRecord::getPushTime);
        IPage<SysAlarmRecord> sss = this.pageDeep(pagination , queryWrapper);
        return new MPage<>(sss);
    }
}
