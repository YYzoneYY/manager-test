package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.ruoyi.system.domain.Entity.MiningEntity;
import com.ruoyi.system.domain.dto.MiningFootageDTO;
import com.ruoyi.system.domain.dto.MiningFootageNewDTO;
import com.ruoyi.system.domain.dto.MiningSelectDTO;
import com.ruoyi.system.domain.dto.MiningSelectNewDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: shikai
 * @date: 2025/2/24
 * @description:
 */

@Repository
public interface MiningMapper extends BaseMapper<MiningEntity> {

    Page<MiningFootageNewDTO> selectMiningFootageByPage(@Param("miningSelectNewDTO") MiningSelectNewDTO miningSelectNewDTO,
                                                        @Param("displayForm") Long displayForm);

   Page<MiningFootageNewDTO> selectMining(@Param("miningSelectNewDTO") MiningSelectNewDTO miningSelectNewDTO);

    /**
     * 统计某条巷道的回采总长度
     * @return 返回结果
     */
    BigDecimal mineLength(@Param("workFaceId") Long workFaceId,
                          @Param("tunnelId") Long tunnelId);

    /**
     * 统计的开采时间之前的所有开采进度
     * @param tunnelId 巷道id
     * @param time 时间
     * @return 返回结果
     */
    BigDecimal miningPaceSum(@Param("tunnelId") Long tunnelId, @Param("time")Long time);

    BigDecimal miningPaceSumT(@Param("tunnelIds") List<Long> tunnelIds, @Param("time")Long time);
}