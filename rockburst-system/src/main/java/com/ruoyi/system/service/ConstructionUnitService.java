package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.Entity.ConstructionUnitEntity;
import com.ruoyi.system.domain.dto.ConstructUnitSelectDTO;
import com.ruoyi.system.domain.dto.ConstructionUnitDTO;
import com.ruoyi.system.domain.dto.UnitChoiceListDTO;
import com.ruoyi.system.domain.dto.UnitDataDTO;

import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/15
 * @description:
 */
public interface ConstructionUnitService extends IService<ConstructionUnitEntity> {

    /**
     * 新增施工单位
     * @param constructionUnitDTO 参数DTO
     * @return 返回结果
     */
    ConstructionUnitDTO insertConstructionUnit(ConstructionUnitDTO constructionUnitDTO);

    /**
     * 修改施工单位
     * @param constructionUnitDTO 参数DTO
     * @return 返回结果
     */
    ConstructionUnitDTO updateConstructionUnit(ConstructionUnitDTO constructionUnitDTO);

    /**
     * 根据id查询施工单位
     * @param constructionUnitId id
     * @return 返回结果
     */
    ConstructionUnitDTO getConstructionUnitById(Long constructionUnitId);

    /**
     * 分页查询
     * @param constructUnitSelectDTO 查询参数DTO
     * @param pageNum 页数
     * @param pageSize 条数
     * @return 返回结果
     */
    TableData pageQueryList(ConstructUnitSelectDTO constructUnitSelectDTO, Integer pageNum, Integer pageSize);

    /**
     * 批量删除
     * @param constructionUnitIds 施工单位id数组
     * @return 返回结果
     */
    boolean deleteConstructionUnit(Long[] constructionUnitIds);

    /**
     * 获取施工单位下拉列表
     * @return 返回结果
     */
    List<UnitChoiceListDTO> getUnitChoiceList();

    List<UnitDataDTO> getUnitDataListForApp();
}