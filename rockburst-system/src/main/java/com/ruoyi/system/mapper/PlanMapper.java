package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.ruoyi.system.domain.Entity.PlanEntity;
import com.ruoyi.system.domain.dto.SelectNewPlanDTO;
import com.ruoyi.system.domain.dto.largeScreen.PlanCountDTO;
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

    Page<PlanVO> queryPage(@Param("selectNewPlanDTO") SelectNewPlanDTO selectNewPlanDTO,
                           @Param("deptIds") List<Long> deptIds,
                           @Param("dateScopeSelf") Integer dateScopeSelf,
                           @Param("userName") String userName);

    List<PlanCountDTO> queryPlanCount();

}