package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.ruoyi.system.domain.Entity.MiningRecordEntity;
import com.ruoyi.system.domain.Entity.SurveyAreaEntity;
import com.ruoyi.system.domain.dto.MiningRecordDTO;
import com.ruoyi.system.domain.dto.SurveySelectDTO;
import com.ruoyi.system.domain.vo.SurveyAreaVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/11
 * @description:
 */

@Repository
public interface MiningRecordMapper extends BaseMapper<MiningRecordEntity> {

    /**
     * 查询修改记录列表
     */
    List<MiningRecordDTO> queryByMiningFootageId(@Param("miningFootageId") Long miningFootageId);

}