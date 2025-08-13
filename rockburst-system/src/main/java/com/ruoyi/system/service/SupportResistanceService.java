package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.Entity.SupportResistanceEntity;
import com.ruoyi.system.domain.dto.MeasureSelectDTO;
import com.ruoyi.system.domain.dto.SupportResistanceDTO;

/**
 * @author: shikai
 * @date: 2024/11/27
 * @description:
 */
public interface SupportResistanceService extends IService<SupportResistanceEntity> {

    /**
     * 新增测点
     * @param supportResistanceDTO 参数DTO
     * @return 返回结果
     */
    int addMeasure(SupportResistanceDTO supportResistanceDTO, Long mineId);

    /**
     * 测点修改
     * @param supportResistanceDTO 参数DTO
     * @return 返回结果
     */
    int updateMeasure(SupportResistanceDTO supportResistanceDTO);

    /**
     * 分页查询
     * @param measureSelectDTO 查询参数DTO
     * @param pageNum 当前页码
     * @param pageSize 条数
     * @return 返回结果
     */
    TableData pageQueryList(MeasureSelectDTO measureSelectDTO, Long mineId, Integer pageNum, Integer pageSize);

    /**
     * 根据id查询
     * @param supportResistanceId id
     * @return 返回结果
     */
    SupportResistanceDTO detail(Long supportResistanceId);

    /**
     * 删除/批量删除
     * @param supportResistanceIds id数组
     * @return 返回结果
     */
    boolean deleteByIds(Long[] supportResistanceIds);

    /**
     * (批量)启用/禁用
     * @param supportResistanceIds id数组
     * @return 返回结果
     */
    int batchEnableDisable(Long[] supportResistanceIds);
}