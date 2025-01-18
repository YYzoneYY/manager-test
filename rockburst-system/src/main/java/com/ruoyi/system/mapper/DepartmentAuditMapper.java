package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.ruoyi.system.domain.Entity.DepartmentAuditEntity;
import com.ruoyi.system.domain.dto.SelectDeptAuditDTO;
import com.ruoyi.system.domain.dto.SelectProjectDTO;
import com.ruoyi.system.domain.vo.ProjectVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/26
 * @description:
 */

@Mapper
public interface DepartmentAuditMapper extends BaseMapper<DepartmentAuditEntity> {

    Integer selectMaxNumber(Long projectId);

    Page<ProjectVO> queryByPage(@Param("selectDeptAuditDTO") SelectDeptAuditDTO selectDeptAuditDTO,
                                @Param("projectIds") List<Long> projectIds,
                                @Param("deptIds") List<Long> deptIds,
                                @Param("dateScopeSelf") Integer dateScopeSelf,
                                @Param("userName") String userName);

    Page<ProjectVO> auditHistoryPage(SelectProjectDTO selectProjectDTO);

    /**
     * 适用于APP的科室审核分页查询(待审核)
     */
    Page<ProjectVO> queryByPageForApp(@Param("fillingType") String fillingType,
                                      @Param("constructionUnitId") Long constructionUnitId,
                                      @Param("projectIds") List<Long> projectIds);

    /**
     * 适用于APP的科室审核分页查询(已审核)
     */
    Page<ProjectVO> approvedQueryByPageForApp(@Param("projectIds") List<Long> projectIds,
                                              @Param("fillingType") String fillingType,
                                              @Param("constructionUnitId") Long constructionUnitId);
}