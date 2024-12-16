package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.ruoyi.system.domain.Entity.MiningFootageEntity;
import com.ruoyi.system.domain.Entity.MiningRecordEntity;
import com.ruoyi.system.domain.dto.MiningFootageDTO;
import com.ruoyi.system.domain.dto.MiningSelectDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2024/11/11
 * @description:
 */

@Mapper
public interface MiningFootageMapper extends BaseMapper<MiningFootageEntity> {

    Page<MiningFootageDTO> selectMiningFootageByPage(MiningSelectDTO miningSelectDTO);

    /**
     * 统计已开采的总进度
     * @return 返回结果
     */
    BigDecimal minedLength(@Param("workfaceId") Long workfaceId);

    /**
     * 统计的开采时间之前的所有开采进度
     * @param workfaceId 工作面id
     * @param time 时间
     * @return 返回结果
     */
    BigDecimal miningPaceSum(@Param("workfaceId") Long workfaceId, @Param("time")Long time);
}