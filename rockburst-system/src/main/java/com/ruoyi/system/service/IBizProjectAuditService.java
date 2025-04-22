package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.BizProjectAudit;

/**
 * 工程填报审核记录Service接口
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
public interface IBizProjectAuditService   extends IService<BizProjectAudit>
{
    int audit(Long projectId);

    int addAudittEAM(BizProjectAudit teamAuditDTO);

    int addAuditDeart(BizProjectAudit teamAuditDTO);

}
