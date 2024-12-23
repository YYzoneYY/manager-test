package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.ruoyi.system.domain.Entity.ExcavationFootageEntity;
import com.ruoyi.system.domain.dto.ExcavationFootageDTO;
import com.ruoyi.system.domain.dto.ExcavationSelectDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2024/11/21
 * @description:
 */

@Mapper
public interface ExcavationFootageMapper extends BaseMapper<ExcavationFootageEntity> {

    Page<ExcavationFootageDTO> selectQueryPage(ExcavationSelectDTO excavationSelectDTO);

    /**
     * 统计已掘进总长度
     * @return 返回结果
     */
    BigDecimal excavationLength(@Param("tunnelId") Long tunnelId);

    BigDecimal excavationPaceSum(@Param("tunnelId") Long tunnelId, @Param("time") Long time);
}