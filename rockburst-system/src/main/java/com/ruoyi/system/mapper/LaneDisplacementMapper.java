package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.Entity.LaneDisplacementEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/8/15
 * @description:
 */

@Mapper
public interface LaneDisplacementMapper extends BaseMapper<LaneDisplacementEntity> {

    String selectMaxMeasureNum(@Param("mineId") Long mineId);

    List<String> selectMeasureNumList(@Param("surveyAreaName") String surveyAreaName,
                                      @Param("mineId") Long mineId);
}