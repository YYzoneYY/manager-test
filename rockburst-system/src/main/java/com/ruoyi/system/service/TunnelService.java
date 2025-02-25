package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.SelectTunnelDTO;
import com.ruoyi.system.domain.dto.TunnelChoiceListDTO;
import com.ruoyi.system.domain.dto.TunnelDTO;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/21
 * @description:
 */
public interface TunnelService extends IService<TunnelEntity> {

    /**
     * 新增巷道
     * @param tunnelDTO 参数DTO
     * @return 返回结果
     */
    int insertTunnel(TunnelDTO tunnelDTO);

    /**
     * 巷道编辑
     * @param tunnelDTO 参数DTO
     * @return 返回结果
     */
    int updateTunnel(TunnelDTO tunnelDTO);

    /**
     * 根据id查询
     * @param tunnelId 巷道id
     * @return 返回结果
     */
    TunnelDTO detail(Long tunnelId);

    /**
     * 分页查询
     * @param selectTunnelDTO 查询参数DTO
     * @param pageNum 当前页码
     * @param pageSize 条数
     * @return 返回结果
     */
    TableData pageQueryList(SelectTunnelDTO selectTunnelDTO, Integer pageNum, Integer pageSize);

    /**
     * 删除/批量删除
     * @param tunnelIds 巷道id数组
     * @return 返回结果
     */
    boolean deleteByIds(Long[] tunnelIds);

    /**
     * 获取巷道下拉框
     * @param faceId 工作面id
     * @return 返回结果
     */
    List<TunnelChoiceListDTO> getTunnelChoiceList(Long faceId);

    /**
     * 获取巷道下拉框二
     * @param faceId 工作面id
     * @return 返回结果
     */
    List<TunnelChoiceListDTO> getTunnelChoiceListTwo(Long faceId);
}