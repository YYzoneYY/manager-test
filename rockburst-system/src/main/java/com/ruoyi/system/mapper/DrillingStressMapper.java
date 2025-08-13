package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.ruoyi.system.domain.Entity.DrillingStressEntity;
import com.ruoyi.system.domain.dto.MeasureSelectDTO;
import com.ruoyi.system.domain.vo.DrillingStressVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author: shikai
 * @date: 2024/12/2
 * @description:
 */

@Mapper
public interface DrillingStressMapper extends BaseMapper<DrillingStressEntity> {

    String selectMaxMeasureNum(@Param("mineId") Long mineId);

    Page<DrillingStressVO> selectQueryPage(@Param("measureSelectDTO") MeasureSelectDTO measureSelectDTO,
                                           @Param("mineId") Long mineId);
}