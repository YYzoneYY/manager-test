package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.ruoyi.system.domain.Entity.TeamAuditEntity;
import com.ruoyi.system.domain.dto.SelectProjectDTO;
import com.ruoyi.system.domain.vo.ProjectVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/25
 * @description:
 */

@Mapper
public interface TeamAuditMapper extends BaseMapper<TeamAuditEntity> {

    Integer selectMaxNumber(Long projectId);

    Page<ProjectVO> queryByPage(@Param("selectProjectDTO") SelectProjectDTO selectProjectDTO,
                                @Param("deptIds") List<Long> deptIds,
                                @Param("dateScopeSelf") Integer dateScopeSelf,
                                @Param("userName") String userName);

    Page<ProjectVO> queryByPageForApp(@Param("fillingType") String fillingType, @Param("constructionUnitId") Long constructionUnitId);

    Page<ProjectVO> approvedQueryByPageForApp(@Param("projectIds") List<Long> projectIds,
                                              @Param("fillingType") String fillingType,
                                              @Param("constructionUnitId") Long constructionUnitId);
}