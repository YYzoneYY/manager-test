package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.Entity.ExcavationRecordEntity;
import com.ruoyi.system.domain.dto.ExcavationRecordDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/21
 * @description:
 */

@Mapper
public interface ExcavationRecordMapper extends BaseMapper<ExcavationRecordEntity> {

    /**
     * 根据recordId查询修改记录
     */
    List<ExcavationRecordDTO> queryByExcavationRecordId(@Param("excavationFootageId") Long excavationFootageId);
}