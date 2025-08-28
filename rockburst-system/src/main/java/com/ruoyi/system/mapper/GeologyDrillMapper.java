package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.ruoyi.system.domain.Entity.GeologyDrillEntity;
import com.ruoyi.system.domain.vo.GeologyDrillVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author: shikai
 * @date: 2025/6/17
 * @description:
 */

@Mapper
public interface GeologyDrillMapper extends BaseMapper<GeologyDrillEntity> {

    Page<GeologyDrillVO> queryByPage(@Param("drillName") String drillName,
                                     @Param("mineId") Long mineId);

    int truncateTable(@Param("mineId") Long mineId);

    int resetAutoIncrement();
}