package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.Entity.ExcavationFootageEntity;
import com.ruoyi.system.domain.dto.ExcavationFootageDTO;
import com.ruoyi.system.domain.dto.ExcavationSelectDTO;

import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2024/12/23
 * @description:
 */
public interface ExcavationFootageService extends IService<ExcavationFootageEntity> {

    /**
     * 新增掘进进尺
     * @param excavationFootageDTO 参数DTO
     * @return 返回结果
     */
    ExcavationFootageDTO insertExcavationFootage(ExcavationFootageDTO excavationFootageDTO);

    /**
     * 修改掘进进尺
     * @param excavationFootageDTO 参数DTO
     * @return 返回结果
     */
    ExcavationFootageEntity updateExcavationFootage(ExcavationFootageDTO excavationFootageDTO);

    /**
     * 擦除
     * @param excavationFootageDTO 参数DTO
     * @return 返回结果
     */
    int clear(ExcavationFootageDTO excavationFootageDTO);

    /**
     * 分页查询
     * @param excavationSelectDTO 参数DTO
     * @param pageNum 当前页
     * @param pageSize 每页显示条数
     * @return 返回结果
     */
    TableData pageQueryList(ExcavationSelectDTO excavationSelectDTO, Integer pageNum, Integer pageSize);

    /**
     * 查询时间相同的数据
     * @param excavationTime 掘进时间
     * @param tunnelId 巷道id
     * @return 返回结果
     */
    String queryByTime(Long excavationTime, Long tunnelId);

    /**
     * 获取剩余巷道长度
     * @param tunnelId 巷道id
     * @return 返回结果
     */
    BigDecimal getSurplusLength(Long tunnelId);
}