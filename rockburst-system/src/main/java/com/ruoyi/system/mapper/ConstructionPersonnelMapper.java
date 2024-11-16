package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.ruoyi.system.domain.Entity.ConstructionPersonnelEntity;
import com.ruoyi.system.domain.dto.PersonnelChoiceListDTO;
import com.ruoyi.system.domain.dto.PersonnelSelectDTO;
import com.ruoyi.system.domain.vo.ConstructPersonnelVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/15
 * @description:
 */
@Repository
public interface ConstructionPersonnelMapper extends BaseMapper<ConstructionPersonnelEntity> {

    /**
     * 分页查询
     * @param personnelSelectDTO 查询参数DTO
     * @return 返回结果
     */
    Page<ConstructPersonnelVO> selectConstructionPersonnelByPage(PersonnelSelectDTO personnelSelectDTO);

    /**
     * 根据施工单位和工种查询施工人员
     * @param constructionUnitId 施工单位id
     * @param profession 工种
     * @return 返回结果
     */
    ArrayList<PersonnelChoiceListDTO> selectChoiceList(@Param("constructionUnitId") Long constructionUnitId, @Param("profession") String profession);
}