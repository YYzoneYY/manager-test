package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.ruoyi.system.domain.Entity.AnchorCableStressEntity;
import com.ruoyi.system.domain.dto.MeasureSelectDTO;
import com.ruoyi.system.domain.vo.AnchorCableStressVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author: shikai
 * @date: 2024/12/3
 * @description:
 */

@Mapper
public interface AnchorCableStressMapper extends BaseMapper<AnchorCableStressEntity> {

    String selectMaxMeasureNum(@Param("sensorType") String sensorType);

    Page<AnchorCableStressVO> selectQueryPage(MeasureSelectDTO measureSelectDTO);
}