package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.ruoyi.system.domain.Entity.SupportResistanceEntity;
import com.ruoyi.system.domain.dto.MeasureSelectDTO;
import com.ruoyi.system.domain.dto.actual.ParameterDTO;
import com.ruoyi.system.domain.vo.SupportResistanceVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/27
 * @description:
 */

@Mapper
public interface SupportResistanceMapper extends BaseMapper<SupportResistanceEntity> {

    String selectMaxMeasureNum(@Param("mineId") Long mineId);

    Page<SupportResistanceVO> selectQueryPage(@Param("measureSelectDTO") MeasureSelectDTO measureSelectDTO,
                                              @Param("mineId") Long mineId);

    List<String> selectMeasureNumList(@Param("surveyAreaName") String surveyAreaName,
                                      @Param("mineId") Long mineId);

    Page<ParameterDTO> selectParameterPage(@Param("keyword") String keyword,
                                           @Param("mineId") Long mineId);
}