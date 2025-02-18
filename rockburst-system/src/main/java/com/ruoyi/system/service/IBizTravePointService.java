package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.BizPresetPoint;
import com.ruoyi.system.domain.BizTravePoint;
import com.ruoyi.system.domain.dto.BizTravePointDto;
import com.ruoyi.system.domain.vo.BizTravePointVo;

import java.util.List;

/**
 * 矿井管理Service接口
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
public interface IBizTravePointService extends IService<BizTravePoint>
{

    //导线点加距离,第几个,间隔,返回导线点加距离
    BizPresetPoint getPresetPoint(Long pointId,Double meter,Double spaced);

    /**
     * 给定 导线点加距离,判断是在 哪个 危险区内
     * @param pointId
     * @param meter
     * @return
     */
    Long judgePointInArea(Long pointId,Double meter);

    List<Long> getInPointList(Long startPointId,Double startMeter,Long endPointId,Double endMeter);

    BizTravePoint getNextPoint(Long currentPoint);

    BizTravePoint getPrePoint(Long currentPoint);

    BizTravePoint getLatLon(Long point,Double meter);

    MPage<BizTravePointVo> geRuleList(Long locationId,String constructType,Pagination pagination);

    BizTravePoint getPrePointDistance(BizTravePointDto dto);

    List<BizTravePoint>  getQyPoint(Long workfaceId);

    Long getVertexCount(Long pointId, Long tunnelId, Boolean vertex);

    BizTravePoint getNearPoint(BizTravePoint point);

    BizTravePoint getPoint(BizTravePoint a, BizTravePoint b, Double direction );

    void  doit(BizTravePoint point);
}
