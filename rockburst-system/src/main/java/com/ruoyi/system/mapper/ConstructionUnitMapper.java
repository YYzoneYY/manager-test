package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.ruoyi.system.domain.Entity.ConstructionUnitEntity;
import com.ruoyi.system.domain.dto.ConstructUnitSelectDTO;
import com.ruoyi.system.domain.vo.ConstructionUnitVO;
import org.springframework.stereotype.Repository;

/**
 * @author: shikai
 * @date: 2024/11/15
 * @description:
 */
@Repository
public interface ConstructionUnitMapper extends BaseMapper<ConstructionUnitEntity> {

    /**
     * 分页查询
     * @param constructUnitSelectDTO 参数DTO
     * @return 返回结果
     */
    Page<ConstructionUnitVO> selectConstructionUnitByPage(ConstructUnitSelectDTO constructUnitSelectDTO);
}