package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.Entity.ProjectWarnSchemeEntity;
import com.ruoyi.system.domain.dto.ProjectWarnSchemeDTO;
import com.ruoyi.system.domain.dto.SelectProjectWarnDTO;

/**
 * @author: shikai
 * @date: 2024/12/12
 * @description:
 */
public interface ProjectWarnSchemeService extends IService<ProjectWarnSchemeEntity> {

    /**
     * 新增工程预警方案
     * @param projectWarnSchemeDTO 参数实体类
     * @return 返回结果
     */
    int insert(ProjectWarnSchemeDTO projectWarnSchemeDTO);

    /**
     * 工程预警方案编辑
     * @param projectWarnSchemeDTO 参数实体类
     * @return 返回结果
     */
    int update(ProjectWarnSchemeDTO projectWarnSchemeDTO);

    /**
     * 根据id查询
     * @param projectWarnSchemeId 工程预警方案id
     * @return 返回结果
     */
    ProjectWarnSchemeDTO detail(Long projectWarnSchemeId);

    /**
     * 分页查询
     * @param selectProjectWarnDTO 查询参数DTO
     * @param pageNum 页数
     * @param pageSize 条数
     * @return 返回结果
     */
    TableData queryByPage(SelectProjectWarnDTO selectProjectWarnDTO, Integer pageNum, Integer pageSize);

    /**
     * 删除
     * @param projectWarnSchemeIds 预警方案id
     * @return 返回结果
     */
    boolean deleteById(Long[] projectWarnSchemeIds);
}