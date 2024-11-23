package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.ruoyi.system.domain.Entity.PlanAuditEntity;
import com.ruoyi.system.domain.dto.SelectPlanDTO;
import com.ruoyi.system.domain.vo.EngineeringPlanVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: shikai
 * @date: 2024/11/23
 * @description:
 */

@Mapper
public interface PlanAuditMapper extends BaseMapper<PlanAuditEntity> {

    Integer selectMaxNumber(Long engineeringPlanId);

    Page<EngineeringPlanVO> queryPage(SelectPlanDTO selectPlanDTO);

    /**
     * 审核历史分页查询
     */
    Page<EngineeringPlanVO> auditHistoryPage(SelectPlanDTO selectPlanDTO);
}