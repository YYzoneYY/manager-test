package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.BizPresetPoint;
import com.ruoyi.system.domain.dto.BizPlanPrePointDto;
import com.ruoyi.system.domain.dto.BizPresetPointDto;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
public interface IBizPresetPointService extends IService<BizPresetPoint>
{

    public  BigDecimal[] getExtendedPoint(
            String xa, String ya,
            double angleDeg,
            double i,
            double scale
    );


    public BizPresetPoint selectEntityById(Long id);


    public MPage<BizPresetPoint> selectEntityList(BizPresetPointDto dto, Pagination pagination);


    public int insertEntity(BizPresetPointDto dto);


    public int updateEntity(BizPresetPointDto dto);


    /**
     * 计划预设点
     * @param planId
     * @param dtos
     * @return
     */
    public boolean setPlanPrePoint(Long planId,List<BizPlanPrePointDto> dtos);

    /**
     * 计划预设点(新)
     * @param planId
     * @param dtos
     * @return
     */
    public boolean setPlanPrePointNew(Long planId,List<BizPlanPrePointDto> dtos);

    /**
     * 根据开始结束导线点获取区间内所有的预设点
     * @param startPointId
     * @param endPointId
     * @return
     */
    public List<BizPresetPoint> getPrePointByPointRange(Long startPointId,Long endPointId,String drillType);


    /**
     * 根据导线点加距离获取区间内所有的预设点
     * @remark 可以传入 导线点  +  正向距离和 负向距离 以及 0 都可以正常返回
     * @param pointId
     * @param meter
     * @return
     */
    public List<BizPresetPoint> getPrePointByPointMeterstart(Long pointId,Double meter,String drillType);


    public List<BizPresetPoint> getPrePointByPointMeterend(Long pointId,Double meter,String drillType);




    int savebarPresetPoint(BizPresetPoint dto);


}
