package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.Entity.MiningFootageEntity;
import com.ruoyi.system.domain.dto.MiningFootageDTO;
import com.ruoyi.system.domain.dto.MiningSelectDTO;

import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2024/11/13
 * @description:
 */
public interface MiningFootageService extends IService<MiningFootageEntity> {

    /**
     * 新增回采进尺
     * @param miningFootageDTO 参数实体类
     * @return 返回结果
     */
    MiningFootageDTO insertMiningFootage(MiningFootageDTO miningFootageDTO);

    /**
     * 回采进尺修改
     * @param miningFootageDTO 参数实体类
     * @return 返回结果
     */
    MiningFootageEntity updateMiningFootage(MiningFootageDTO miningFootageDTO);

    /**
     * 擦除
     * @param miningFootageDTO 参数DTO
     * @return 返回结果
     */
    int clear(MiningFootageDTO miningFootageDTO);

    /**
     * 分页查询
     * @param miningSelectDTO 参数DTO
     * @param pageNum 页数
     * @param pageSize 条数
     * @return 返回结果
     */
    TableData pageQueryList(MiningSelectDTO miningSelectDTO, Integer pageNum, Integer pageSize);

    /**
     * 查询是否有时间相同
     * @param miningTime 时间
     * @param workfaceId 工作面id
     * @return 返回结果
     */
    public String queryByTime(Long miningTime, Long workfaceId);

    /**
     * 获取剩余工作面长度
     * @param workfaceId 工作面id
     * @return 返回结果
     */
    BigDecimal getSurplusLength(Long workfaceId);
}