package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.ruoyi.system.domain.Entity.ElecRadiationEntity;
import com.ruoyi.system.domain.dto.MeasureTSelectDTO;
import com.ruoyi.system.domain.dto.actual.ParameterDTO;
import com.ruoyi.system.domain.vo.ElecRadiationVO;
import org.apache.ibatis.annotations.Param;

/**
 * @author: shikai
 * @date: 2025/8/16
 * @description:
 */
public interface ElecRadiationMapper extends BaseMapper<ElecRadiationEntity> {

    String selectMaxMeasureNum(@Param("mineId") Long mineId);

    Page<ElecRadiationVO> selectQueryPage(@Param("measureTSelectDTO") MeasureTSelectDTO measureTSelectDTO,
                                          @Param("mineId") Long mineId);

    Page<ParameterDTO> selectParameterPage(@Param("keyword") String keyword,
                                           @Param("mineId") Long mineId);
}