package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.Entity.PlanAlarm;
import com.ruoyi.system.domain.dto.BizMineDto;

/**
 * @author: shikai
 * @date: 2025/2/17
 * @description:
 */
public interface PlanAlarmService extends IService<PlanAlarm> {

    /**
     * 查询矿井管理列表
     *
     * @param bizMine 矿井管理
     * @return 矿井管理集合
     */
    public MPage<PlanAlarm> selectPageList(BizMineDto bizMine, Pagination pagination);


}