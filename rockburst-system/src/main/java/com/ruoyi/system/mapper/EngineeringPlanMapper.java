package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.ruoyi.system.domain.Entity.EngineeringPlanEntity;
import com.ruoyi.system.domain.dto.SelectPlanDTO;
import com.ruoyi.system.domain.vo.EngineeringPlanVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author: shikai
 * @date: 2024/11/22
 * @description:
 */

@Mapper
public interface EngineeringPlanMapper extends BaseMapper<EngineeringPlanEntity>{

    Page<EngineeringPlanVO> queryPage(SelectPlanDTO selectPlanDTO);
}