package com.ruoyi.system.service;

import com.github.yulichang.extension.mapping.base.MPJDeepService;
import com.ruoyi.common.core.domain.BasePermission;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.SysAlarmRecord;

/**
 * 工程视频Service接口
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
public interface ISysAlarmRecordService extends MPJDeepService<SysAlarmRecord>
{

    MPage<SysAlarmRecord> pageList(BasePermission permission, SysAlarmRecord record , Pagination pagination);

}
