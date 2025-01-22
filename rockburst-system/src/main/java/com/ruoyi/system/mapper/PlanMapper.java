package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.ruoyi.system.domain.Entity.PlanEntity;
import com.ruoyi.system.domain.dto.SelectPlanDTO;
import com.ruoyi.system.domain.vo.PlanVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/22
 * @description:
 */

@Mapper
public interface PlanMapper extends BaseMapper<PlanEntity>{

    Page<PlanVO> queryPage(@Param("selectPlanDTO") SelectPlanDTO selectPlanDTO,
                           @Param("planIds") List<Long> planIds,
                           @Param("deptIds") List<Long> deptIds,
                           @Param("dateScopeSelf") Integer dateScopeSelf,
                           @Param("userName") String userName);
}