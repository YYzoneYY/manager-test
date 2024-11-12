package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.ruoyi.system.domain.Entity.SurveyAreaEntity;
import com.ruoyi.system.domain.dto.SurveySelectDTO;
import com.ruoyi.system.domain.vo.SurveyAreaVO;
import org.springframework.stereotype.Repository;

/**
 * @author: shikai
 * @date: 2024/11/11
 * @description:
 */

@Repository
public interface SurveyAreaMapper extends BaseMapper<SurveyAreaEntity> {

    /**
     * 分页查询
     * @param surveySelectDTO 查询参数实体类
     * @return 查询结果
     */
    Page<SurveyAreaVO> selectSurveyAreaByPage(SurveySelectDTO surveySelectDTO);
}