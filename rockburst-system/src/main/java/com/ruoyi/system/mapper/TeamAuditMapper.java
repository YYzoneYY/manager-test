package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.ruoyi.system.domain.Entity.TeamAuditEntity;
import com.ruoyi.system.domain.dto.SelectProjectDTO;
import com.ruoyi.system.domain.vo.ProjectVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: shikai
 * @date: 2024/11/25
 * @description:
 */

@Mapper
public interface TeamAuditMapper extends BaseMapper<TeamAuditEntity> {

    Integer selectMaxNumber(Long projectId);

    Page<ProjectVO> queryByPage(SelectProjectDTO selectProjectDTO);
}