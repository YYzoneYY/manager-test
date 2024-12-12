package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.ruoyi.system.domain.Entity.ProjectWarnSchemeEntity;
import com.ruoyi.system.domain.dto.SelectProjectWarnDTO;
import com.ruoyi.system.domain.vo.ProjectWarnSchemeVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: shikai
 * @date: 2024/12/12
 * @description:
 */

@Mapper
public interface ProjectWarnSchemeMapper extends BaseMapper<ProjectWarnSchemeEntity> {

    Page<ProjectWarnSchemeVO> selectQueryByPage(SelectProjectWarnDTO selectProjectWarnDTO);

}