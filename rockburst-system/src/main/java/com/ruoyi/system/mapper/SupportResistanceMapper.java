package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.ruoyi.system.domain.Entity.SupportResistanceEntity;
import com.ruoyi.system.domain.dto.MeasureSelectDTO;
import com.ruoyi.system.domain.vo.SupportResistanceVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: shikai
 * @date: 2024/11/27
 * @description:
 */

@Mapper
public interface SupportResistanceMapper extends BaseMapper<SupportResistanceEntity> {

    String selectMaxMeasureNum();

    Page<SupportResistanceVO> selectQueryPage(MeasureSelectDTO measureSelectDTO);
}