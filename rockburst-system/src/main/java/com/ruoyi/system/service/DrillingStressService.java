package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.Entity.DrillingStressEntity;
import com.ruoyi.system.domain.dto.DrillingStressDTO;
import com.ruoyi.system.domain.dto.MeasureSelectDTO;

/**
 * @author: shikai
 * @date: 2024/12/3
 * @description:
 */
public interface DrillingStressService extends IService<DrillingStressEntity> {

    /**
     * 新增测点
     * @param drillingStressDTO 参数DTO
     * @return 返回结果
     */
    int addMeasure(DrillingStressDTO drillingStressDTO);

    /**
     * 测点编辑
     * @param drillingStressDTO 参数DTO
     * @return 返回结果
     */
    int updateMeasure(DrillingStressDTO drillingStressDTO);

    /**
     * 根据Id查询
     * @param drillingStressId 测点id
     * @return 返回结果
     */
    DrillingStressDTO detail(Long drillingStressId);

    /**
     * 分页查询
     * @param measureSelectDTO 查询参数DTO
     * @param pageNum 当前页码
     * @param pageSize 条数
     * @return 返回结果
     */
    TableData pageQueryList(MeasureSelectDTO measureSelectDTO, Integer pageNum, Integer pageSize);

    /**
     * 删除/批量删除
     * @param drillingStressIds id数组
     * @return 返回结果
     */
    boolean deleteByIds(Long[] drillingStressIds);

    /**
     * (批量)启用/禁用
     * @param drillingStressIds id数组
     * @return 返回结果
     */
    int batchEnableDisable(Long[] drillingStressIds);
}