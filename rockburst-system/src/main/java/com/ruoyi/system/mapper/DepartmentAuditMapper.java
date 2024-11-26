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
                                @Param("projectIds") List<Long> projectIds);
}