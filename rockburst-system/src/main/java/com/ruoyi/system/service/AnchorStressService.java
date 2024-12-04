package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.Entity.AnchorCableStressEntity;
import com.ruoyi.system.domain.dto.AnchorCableStressDTO;
import com.ruoyi.system.domain.dto.MeasureSelectDTO;

/**
 * @author: shikai
 * @date: 2024/12/3
 * @description:
 */
public interface AnchorStressService extends IService<AnchorCableStressEntity> {

    /**
     * 测点新增
     * @param anchorCableStressDTO 参数DTO
     * @return 返回结果
     */
    int addMeasure(AnchorCableStressDTO anchorCableStressDTO);

    /**
     * 测点修改
     * @param anchorCableStressDTO 参数DTO
     * @return 返回结果
     */
    int updateMeasure(AnchorCableStressDTO anchorCableStressDTO);

    /**
     * 根据id查询
     * @param anchorCableStressId 主键id
     * @return 返回结果
     */
    AnchorCableStressDTO detail(Long anchorCableStressId);

    /**
     * 分页查询
     * @param measureSelectDTO 查询参数DTO
     * @param pageNum 当前页码
     * @param pageSize 条数
     * @return 返回结果
     */
    TableData pageQueryList(MeasureSelectDTO measureSelectDTO, Integer pageNum, Integer pageSize);

    /**
     * (批量)删除
     * @param anchorCableStressIds 主键id数组
     * @return 返回结果
     */
    boolean deleteByIds(Long[] anchorCableStressIds);

    /**
     * (批量)启用/禁用
     * @param anchorCableStressIds 主键id数组
     * @return 返回结果
     */
    int batchEnableDisable(Long[] anchorCableStressIds);
}