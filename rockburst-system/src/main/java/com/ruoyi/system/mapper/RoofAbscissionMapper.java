package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.ruoyi.system.domain.Entity.RoofAbscissionEntity;
import com.ruoyi.system.domain.dto.MeasureSelectDTO;
import com.ruoyi.system.domain.vo.RoofAbscissionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/8/15
 * @description:
 */

@Mapper
public interface RoofAbscissionMapper extends BaseMapper<RoofAbscissionEntity> {

    String selectMaxMeasureNum(@Param("mineId") Long mineId);

    Page<RoofAbscissionVO> selectQueryPage(@Param("measureSelectDTO") MeasureSelectDTO measureSelectDTO,
                                           @Param("mineId") Long mineId);

    List<String> selectMeasureNumList(@Param("surveyAreaName") String surveyAreaName,
                                      @Param("mineId") Long mineId);
}