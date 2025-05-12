package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.Entity.CacheDataEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author: shikai
 * @date: 2025/5/9
 * @description:
 */

@Mapper
public interface CacheDataMapper extends BaseMapper<CacheDataEntity>{

    Integer selectMaxNumber(@Param("dangerAreaId") Long dangerAreaId);
}