package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.Entity.MiningRecordNewEntity;
import com.ruoyi.system.domain.dto.MiningRecordNewDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/2/24
 * @description:
 */

@Repository
public interface MiningRecordNewMapper extends BaseMapper<MiningRecordNewEntity>{

    /**
     * 查询修改记录列表
     */
    List<MiningRecordNewDTO> queryByMiningFootageId(@Param("miningFootageId") Long miningFootageId);
}