package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.Entity.ConstructionPersonnelEntity;
import com.ruoyi.system.domain.dto.ConstructPersonnelDTO;
import com.ruoyi.system.domain.dto.PersonnelChoiceListDTO;
import com.ruoyi.system.domain.dto.PersonnelSelectDTO;

import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/15
 * @description:
 */
public interface ConstructionPersonnelService extends IService<ConstructionPersonnelEntity> {

    /**
     * 新增施工人员
     * @param constructPersonnelDTO 参数DTO
     * @return 返回结果
     */
    ConstructPersonnelDTO insertConstructionPersonnel(ConstructPersonnelDTO constructPersonnelDTO, Long mineId);

    /**
     * 施工人员编辑
     * @param constructPersonnelDTO 参数DTO
     * @return 返回结果
     *
     */
    ConstructPersonnelDTO updateConstructionPersonnel(ConstructPersonnelDTO constructPersonnelDTO, Long mineId);

    /**
     * 批量删除
     * @param constructionPersonnelIds 施工人员id数组
     * @return 返回结果
     */
    boolean deleteConstructionPersonnel(Long[] constructionPersonnelIds);

    /**
     * 根据id查询
     * @param constructionPersonnelId 施工人员id
     * @return 返回结果
     */
    ConstructPersonnelDTO getConstructionPersonnelById(Long constructionPersonnelId);

    /**
     * 分页查询
     * @param personnelSelectDTO 查询参数DTO
     * @param pageNum 当前页
     * @param pageSize 每页显示条数
     * @return 返回结果
     */
    TableData pageQueryList(PersonnelSelectDTO personnelSelectDTO, Integer pageNum, Integer pageSize, Long mineId);

    /**
     * 获取施工人员下拉列表
     * @param constructionUnitId 单位id
     * @param profession 工种标识
     * @return 返回结果
     */
    List<PersonnelChoiceListDTO> getPersonnelChoiceList(Long constructionUnitId, String profession);
}