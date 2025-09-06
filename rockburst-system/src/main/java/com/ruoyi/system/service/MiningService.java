package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.Entity.MiningEntity;
import com.ruoyi.system.domain.EqtRoofSeparat;
import com.ruoyi.system.domain.dto.FootageReturnDTO;
import com.ruoyi.system.domain.dto.MiningFootageNewDTO;
import com.ruoyi.system.domain.dto.MiningSelectNewDTO;
import com.ruoyi.system.domain.dto.ShowWayChoiceListDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: shikai
 * @date: 2025/2/24
 * @description:
 */
public interface MiningService extends IService<MiningEntity> {

    /**
     * 新增回采进尺
     * @param miningFootageNewDTO 参数实体类
     * @return 返回结果
     */
    int insertMiningFootage(MiningFootageNewDTO miningFootageNewDTO);

    /**
     * 修改回采进尺
     * @param miningFootageNewDTO 参数实体类
     * @return 返回结果
     */
    boolean updateMiningFootage(MiningFootageNewDTO miningFootageNewDTO);

    /**
     * 擦除
     * @param miningFootageNewDTO 参数DTO
     * @return 返回结果
     */
    int clear(MiningFootageNewDTO miningFootageNewDTO);

    /**
     * 分页查询
     * @param miningSelectNewDTO 参数DTO
     * @param pageNum 页数
     * @param pageSize 条数
     * @return 返回结果
     */
    MPage<MiningFootageNewDTO> pageQueryList(MiningSelectNewDTO miningSelectNewDTO, String displayForm, Integer pageNum, Integer pageSize);

    /**
     * 查询时间相同的数据
     * @param miningTime 回采时间
     * @param tunnelId 巷道id
     * @return 返回结果
     */
    String queryByTime(Long miningTime, Long tunnelId);

    /**
     * 获取剩余巷道长度
     * @param tunnelId 巷道id
     * @return 返回结果
     */
    BigDecimal getSurplusLength(Long workFaceId, Long tunnelId);

    /**
     * 展示方式下拉框
     * @param workFaceId 工作面id
     * @return 返回结果
     */
    List<ShowWayChoiceListDTO> getShowWayChoiceList(Long workFaceId);

    /**
     * 根据当前累计进尺计算目前回采进度处于危险区信息
     * @param workFaceId 工作面id
     * @return 返回结果
     */
    List<FootageReturnDTO> getFootageReturnDTO(Long workFaceId);

    /**
     * 初始化数据
     * @param workFaceId 工作面id
     * @param tunnelId 巷道id
     * @param time 回采时间
     * @param sumPace 累计
     * @return 返回结果
     */
    int initData(Long workFaceId, Long tunnelId,  Long time, BigDecimal sumPace);

    /**
     * 定时任务使用，查询当天是否有数据
     */
    public List<MiningEntity> queryCurrentDay(Long startTime, Long endTime);
}