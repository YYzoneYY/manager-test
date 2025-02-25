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
     * 根据 起始和结束导线点获取 所有 导线点 列表 包含 起始和结束
     * @param startPointId
     * @param endPoinId
     * @return
     */
    List<BizTravePoint> getPointByRange(Long startPointId,Long endPoinId);

    /**
     * 给定 导线点加距离,判断是在 哪个 危险区内
     * @param pointId
     * @param meter
     * @return
     */
    Long judgePointInArea(Long pointId,Double meter);


    /**
     * 给定 导线点加距离, 转换成 负方向距离
     * @param pointId
     * @param meter
     * @return
     */
    BizPresetPoint getPointPre(Long pointId,Double meter);


    /**
     * 给定 导线点加距离, 转换 坐标
     * @param pointId
     * @param meter
     * @return
     */
    BizPresetPoint getPointLatLon(Long pointId,Double meter);

    /**
     * 给定  经纬度坐标,打孔深度,打孔角度 获取  顶点坐标
     * @param x
     * @param jio
     * @return
     */
    BizPresetPoint getLatLontop(String lat, String lon ,Double x,Integer jio);

    /**
     * 给定起始结束  加距离 获取 区间内导线点
     * @param startPointId
     * @param startMeter
     * @param endPointId
     * @param endMeter
     * @return
     */
    List<Long> getInPointList(Long startPointId,Double startMeter,Long endPointId,Double endMeter);

    /**
     * 传入导线点id 获取下一个导线点
     * @param currentPoint
     * @return
     */
    BizTravePoint getNextPoint(Long currentPoint);

    /**
     * 传入导线点id 获取上一个导线点
     * @param currentPoint
     * @return
     */
    BizTravePoint getPrePoint(Long currentPoint);

    BizTravePoint getLatLon(Long point,Double meter);

    MPage<BizTravePointVo> geRuleList(Long locationId,String constructType,Pagination pagination);

    BizTravePoint getPrePointDistance(BizTravePointDto dto);

//    List<BizTravePoint>  getQyPoint(Long workfaceId);

//    Long getVertexCount(Long pointId, Long tunnelId, Boolean vertex);

    BizTravePoint getNearPoint(BizTravePoint point);

//    BizTravePoint getPoint(BizTravePoint a, BizTravePoint b, Double direction );

//    void  doit(BizTravePoint point);
}
