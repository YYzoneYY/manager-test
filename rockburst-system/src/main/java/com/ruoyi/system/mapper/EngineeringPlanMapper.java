package com.ruoyi.system.mapper;

import com.github.pagehelper.Page;
import com.github.yulichang.base.MPJBaseMapper;
import com.ruoyi.system.domain.Entity.EngineeringPlanEntity;
import com.ruoyi.system.domain.dto.SelectPlanDTO;
import com.ruoyi.system.domain.vo.EngineeringPlanVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: shikai
 * @date: 2024/11/22
 * @description:
 */

@Mapper
public interface EngineeringPlanMapper extends MPJBaseMapper<EngineeringPlanEntity> {

    Page<EngineeringPlanVO> queryPage(SelectPlanDTO selectPlanDTO);
}