package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.ruoyi.system.domain.Entity.PlanAuditEntity;
import com.ruoyi.system.domain.dto.SelectPlanDTO;
import com.ruoyi.system.domain.vo.PlanVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/23
 * @description:
 */

@Mapper
public interface PlanAuditMapper extends BaseMapper<PlanAuditEntity> {

    Integer selectMaxNumber(@Param("planId") Long planId);

    Page<PlanVO> queryPage(@Param("selectPlanDTO") SelectPlanDTO selectPlanDTO,
                           @Param("deptIds") List<Long> deptIds,
                           @Param("dateScopeSelf") Integer dateScopeSelf,
                           @Param("userName") String userName);

    /**
     * 审核历史分页查询
     */
    Page<PlanVO> auditHistoryPage(@Param("selectPlanDTO") SelectPlanDTO selectPlanDTO,
                                  @Param("planIds") List<Long> planIds,
                                  @Param("deptIds") List<Long> deptIds,
                                  @Param("dateScopeSelf") Integer dateScopeSelf,
                                  @Param("userName") String userName);
}