package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.BizPresetPoint;
import com.ruoyi.system.domain.dto.BizPresetPointDto;

import java.util.List;

/**
 *
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
public interface IBizPresetPointService extends IService<BizPresetPoint>
{


    public BizPresetPoint selectEntityById(Long id);


    public MPage<BizPresetPoint> selectEntityList(BizPresetPointDto dto, Pagination pagination);


    public int insertEntity(BizPresetPointDto dto);


    public int updateEntity(BizPresetPointDto dto);


    /**
     * 计划预设导线点
     * @param planId
     * @param dtos
     * @return
     */
    public boolean setPlanPrePoint(Long planId,List<BizPresetPointDto> dtos);



    int savebarPresetPoint(BizPresetPoint dto);


}
