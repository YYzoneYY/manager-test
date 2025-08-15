package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.ruoyi.system.domain.Entity.AnchorCableStressEntity;
import com.ruoyi.system.domain.dto.MeasureSelectDTO;
import com.ruoyi.system.domain.vo.AnchorCableStressVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: shikai
 * @date: 2024/12/3
 * @description:
 */

@Mapper
public interface AnchorCableStressMapper extends BaseMapper<AnchorCableStressEntity> {

    String selectMaxMeasureNum(@Param("sensorType") String sensorType,
                               @Param("mineId") Long mineId);

    Page<AnchorCableStressVO> selectQueryPage(@Param("measureSelectDTO") MeasureSelectDTO measureSelectDTO,
                                              @Param("mineId") Long mineId);

    List<String> selectMeasureNumList(@Param("surveyAreaName") String surveyAreaName,
                                      @Param("mineId") Long mineId);
}