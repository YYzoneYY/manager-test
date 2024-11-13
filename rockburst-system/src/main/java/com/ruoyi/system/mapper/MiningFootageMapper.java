package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.Entity.MiningFootageEntity;
import com.ruoyi.system.domain.Entity.MiningRecordEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2024/11/11
 * @description:
 */

@Repository
public interface MiningFootageMapper extends BaseMapper<MiningFootageEntity> {

    /**
     * 统计已开采的总进度
     * @return 返回结果
     */
    BigDecimal minedLength(@Param("workfaceId") Long workfaceId);
}