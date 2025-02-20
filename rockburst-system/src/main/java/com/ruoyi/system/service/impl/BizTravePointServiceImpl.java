package com.ruoyi.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.common.utils.MathUtil;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.domain.BizPresetPoint;
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.domain.BizTravePoint;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.Point;
import com.ruoyi.system.domain.dto.BizTravePointDto;
import com.ruoyi.system.domain.vo.BizTravePointVo;
import com.ruoyi.system.mapper.BizTravePointMapper;
import com.ruoyi.system.mapper.TunnelMapper;
import com.ruoyi.system.service.IBizTravePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 矿井管理Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
@Service
public class BizTravePointServiceImpl extends ServiceImpl<BizTravePointMapper, BizTravePoint> implements IBizTravePointService
{

    //此 service 不能引用 其他service




    @Autowired
    private BizTravePointMapper bizTravePointMapper;

    @Autowired
    private TunnelMapper tunnelMapper;

    @Override
    public BizPresetPoint getPresetPoint(Long pointId,Double meter,Double spaced) {
        Double rest = meter + spaced;
        if(rest <= 0){
            BizPresetPoint bizPresetPoint = new BizPresetPoint();
            bizPresetPoint.setPointId(pointId).setMeter(rest);
            return bizPresetPoint;
        }
        //获取下一个间隔距离的导线点
        BizPresetPoint okkPoint = getNextSpaced(pointId,rest);
        return okkPoint;
    }


    @Override
    public List<BizTravePoint> getPointByRange(Long startPointId, Long endPoinId) {
       BizTravePoint start =  this.getById(startPointId);
       BizTravePoint end =  this.getById(startPointId);
        QueryWrapper<BizTravePoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(BizTravePoint::getTunnelId,start.getTunnelId())
                .between(BizTravePoint::getNo, start.getNo(), end.getNo());

        return this.list(queryWrapper);
    }

    @Override
    public Long judgePointInArea(Long pointId, Double meter) {
        BizTravePoint point =  this.getById(pointId);

        return 1l;
    }

    /**
     * 获取下一个间隔的导线点加 前距离
     * @param pointId
     * @param rest
     * @return
     */
    BizPresetPoint getNextSpaced(Long pointId,Double rest){
        BizTravePoint nextPoint = getNextPoint(pointId);
        if(nextPoint == null || nextPoint.getPointId() == null){
            return null;
        }
        rest = rest - nextPoint.getPrePointDistance();
        if(rest <= 0){
            BizPresetPoint point = new BizPresetPoint();
            point.setPointId(nextPoint.getPointId()).setMeter(rest);
            return point;
        }
        return getNextSpaced(nextPoint.getPointId(),rest);
    }

    @Override
    public List<Long> getInPointList(Long startPointId, Double startMeter, Long endPointId, Double endMeter) {
        BizTravePoint startPoint = bizTravePointMapper.selectById(startPointId);
        BizTravePoint endPoint = bizTravePointMapper.selectById(endPointId);
        QueryWrapper<BizTravePoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BizTravePoint::getTunnelId, startPoint.getTunnelId())
                .between(BizTravePoint::getNo, startPoint.getNo(), endPoint.getNo());
        List<BizTravePoint> points = bizTravePointMapper.selectList(queryWrapper);
        List<Long> pointIds = new ArrayList<>();
        if(points != null && points.size() > 0){
            pointIds = points.stream().map(BizTravePoint::getPointId).collect(Collectors.toList());
        }
        if(startMeter > 0){
            pointIds.remove(startPointId);
        }
        if(endMeter < 0){
            pointIds.remove(endPointId);
        }

        return pointIds;
    }

    @Override
    public BizTravePoint getNextPoint(Long currentPointId) {
        BizTravePoint current = this.getById(currentPointId);
        QueryWrapper<BizTravePoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BizTravePoint::getTunnelId, current.getTunnelId())
                .eq(BizTravePoint::getNo, current.getNo()+1);
        List<BizTravePoint> points = bizTravePointMapper.selectList(queryWrapper);
        if(points != null && points.size() == 1){
            return points.get(0);
        }
        return null;
    }


    @Override
    public BizTravePoint getPrePoint(Long currentPointId) {
        BizTravePoint current = this.getById(currentPointId);
        QueryWrapper<BizTravePoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BizTravePoint::getTunnelId, current.getTunnelId())
                .eq(BizTravePoint::getNo, current.getNo()-1);
        List<BizTravePoint> points = bizTravePointMapper.selectList(queryWrapper);
        if(points != null && points.size() == 1){
            return points.get(0);
        }
        return null;
    }

    @Override
    public BizTravePoint getLatLon(Long point, Double meter) {

        return null;
    }

    @Override
    public MPage<BizTravePointVo> geRuleList(Long locationId,String constructType, Pagination pagination) {
        MPJLambdaWrapper<BizTravePoint> queryWrapper = new MPJLambdaWrapper<BizTravePoint>();
        queryWrapper.leftJoin(BizProjectRecord.class,BizProjectRecord::getTravePointId,BizTravePoint::getPointId)
                .selectSum(BizProjectRecord::getProjectId,BizTravePointVo::getDid)
                .selectAll(BizTravePoint.class)
                .eq(StrUtil.isNotEmpty(constructType) , BizProjectRecord::getConstructType,constructType)
                .eq(StrUtil.isNotEmpty(constructType) && locationId != null && BizBaseConstant.CONSTRUCT_TYPE_H.equals(constructType)  , BizTravePoint::getWorkfaceId, locationId)
                .eq(StrUtil.isNotEmpty(constructType) && locationId != null && BizBaseConstant.CONSTRUCT_TYPE_J.equals(constructType)  , BizTravePoint::getTunnelId, locationId)
                .eq(BizTravePoint::getDelFlag, BizBaseConstant.DELFLAG_N);
        IPage<BizTravePointVo> list = bizTravePointMapper.selectJoinPage(pagination,BizTravePointVo.class,queryWrapper);
        return new MPage<>(list);
    }

    @Override
    public BizTravePoint getPrePointDistance(BizTravePointDto dto) {
        QueryWrapper<BizTravePoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BizTravePoint::getTunnelId, dto.getTunnelId());
        List<BizTravePoint> list = this.getBaseMapper().selectList(queryWrapper);
        BizTravePoint  maxNoEntity = new BizTravePoint();
        if (list != null && list.size() > 0) {
            Optional<BizTravePoint> maxPoint = list.stream().max(Comparator.comparing(BizTravePoint::getNo));
            if (maxPoint.isPresent()) {
                maxNoEntity = maxPoint.get();
            }
        }else {
            maxNoEntity = new BizTravePoint();
            maxNoEntity.setNo(1l);
            return maxNoEntity;
        }
        if (maxNoEntity != null) {
            maxNoEntity.setNo(maxNoEntity.getNo()+1);
            return maxNoEntity;
        }
        return maxNoEntity;
    }

    @Override
    public List<BizTravePoint> getQyPoint(Long workfaceId) {
        QueryWrapper<TunnelEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TunnelEntity::getWorkFaceId,workfaceId)
                .eq(TunnelEntity::getTunnelType,"QY");
        List<TunnelEntity> list = tunnelMapper.selectList(queryWrapper);
        if(list == null || list.size() == 0){
            return  null;
        }
        TunnelEntity tunnelEntity = list.get(0);
        QueryWrapper<BizTravePoint> pointQueryWrapper = new QueryWrapper<>();
        pointQueryWrapper.lambda()
                .eq(BizTravePoint::getTunnelId,tunnelEntity.getTunnelId())
                .eq(BizTravePoint::getIsVertex,true);
        return bizTravePointMapper.selectList(pointQueryWrapper);
    }

    @Override
    public Long getVertexCount(Long pointId, Long tunnelId, Boolean vertex) {
        QueryWrapper<BizTravePoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .notIn(pointId != null, BizTravePoint::getPointId,pointId)
                .eq(BizTravePoint::getTunnelId, tunnelId)
                .eq(BizTravePoint::getIsVertex,vertex);
        return bizTravePointMapper.selectCount(queryWrapper);
    }

    @Override
    public BizTravePoint getNearPoint(BizTravePoint point) {
        QueryWrapper<BizTravePoint> queryWrapper = new QueryWrapper<>();
        if(point.getNo() != null && point.getNo() == 1){
            queryWrapper.lambda().eq(BizTravePoint::getTunnelId,point.getTunnelId()).eq(BizTravePoint::getNo,point.getNo()+1);
        }else {
            queryWrapper.lambda().eq(BizTravePoint::getTunnelId,point.getTunnelId()).eq(BizTravePoint::getNo,point.getNo()-1);
        }
        return bizTravePointMapper.selectOne(queryWrapper);
    }

    @Override
    public BizTravePoint getPoint(BizTravePoint a, BizTravePoint b, Double distance ) {
        double[] s = null;
        if(a.getNo() < b.getNo()){
            s = MathUtil.getPointAtDistance(Double.parseDouble(a.getAxisx()),Double.parseDouble(a.getAxisy()),Double.parseDouble(a.getAxisz())
                    ,Double.parseDouble(b.getAxisx()),Double.parseDouble(b.getAxisy()),Double.parseDouble(b.getAxisz()),-distance);
        }else {
            s = MathUtil.getPointAtDistance(Double.parseDouble(a.getAxisx()),Double.parseDouble(a.getAxisy()),Double.parseDouble(a.getAxisz())
                    ,Double.parseDouble(b.getAxisx()),Double.parseDouble(b.getAxisy()),Double.parseDouble(b.getAxisz()),distance);
        }
        BizTravePoint point = new BizTravePoint();
        point.setAxisx(String.valueOf(s[0]));
        point.setAxisy(String.valueOf(s[1]));
        point.setAxisz(String.valueOf(s[2]));
        return point;
    }



    @Override
    public void doit(BizTravePoint point) {
//        List<BizTravePoint> qyList =  this.getQyPoint(point.getWorkfaceId());
        MPJLambdaWrapper<BizTravePoint> queryWrapper = new MPJLambdaWrapper<BizTravePoint>();
        queryWrapper
                .leftJoin(TunnelEntity.class,TunnelEntity::getTunnelId,BizTravePoint::getTunnelId)
                .eq(BizTravePoint::getWorkfaceId,point.getWorkfaceId())
//                .eq(BizTravePoint::getIsVertex,true)
                .in(TunnelEntity::getTunnelType,BizBaseConstant.TUNNEL_SH);
        List<BizTravePoint> shpointList = this.getBaseMapper().selectJoinList(queryWrapper);

        queryWrapper.clear();
        queryWrapper
                .leftJoin(TunnelEntity.class,TunnelEntity::getTunnelId,BizTravePoint::getTunnelId)
                .eq(BizTravePoint::getWorkfaceId,point.getWorkfaceId())
//                .eq(BizTravePoint::getIsVertex,true)
                .in(TunnelEntity::getTunnelType,BizBaseConstant.TUNNEL_XH);
        List<BizTravePoint> xhpointList = this.getBaseMapper().selectJoinList(queryWrapper);

        Double q = new Double("0");
        Long shId = null;
        for (int i = 0; i < shpointList.size(); i++) {
            BizTravePoint shpoint = shpointList.get(i);
            Double qq = getMath(zhuanhuan(shpointList.get(i)),zhuanhuan(point));
            if(i == 0){
                q = qq;
                shId = shpointList.get(i).getPointId();
            }
            if(q >= qq){
                q = qq;
                shId = shpointList.get(i).getPointId();
            }
            shpoint.setDistance(qq);
        }
        Double q1 = new Double("0");
        Long xhId = null;
        for (int i = 0; i < xhpointList.size(); i++) {
            BizTravePoint xhpoint = xhpointList.get(i);
            Double qq = getMath(zhuanhuan(xhpointList.get(i)),zhuanhuan(point));
            if(i == 0){
                q1 = qq;
                xhId = xhpointList.get(i).getPointId();
            }
            if(q >= qq){
                q1 = qq;
                xhId = xhpointList.get(i).getPointId();
            }
            xhpoint.setDistance(qq);
        }
        if(q > q1){
            this.updateBatchById(xhpointList);
            UpdateWrapper<BizTravePoint> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().set(BizTravePoint::getIsVertex,true)
                    .set(BizTravePoint::getBestNearPointId,point.getPointId())
                    .eq(BizTravePoint::getPointId,xhId);
            this.update(updateWrapper);
        }else {
            this.updateBatchById(shpointList);
            UpdateWrapper<BizTravePoint> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().set(BizTravePoint::getIsVertex,true)
                    .set(BizTravePoint::getBestNearPointId,point.getPointId())
                    .eq(BizTravePoint::getPointId,shId);
            this.update(updateWrapper);
        }

    }

    private Double getMath(Point a, Point n){
        BigDecimal ax = a.getX().subtract(n.getX());
        BigDecimal ay = a.getY().subtract(n.getY());
        BigDecimal az = a.getZ().subtract(n.getZ());

        ax = ax.multiply(ax);
        ay = ay.multiply(ay);
        az = az.multiply(az);

        Double an = Math.sqrt(ax.add(ay).add(az).doubleValue());
        return an;
    }

    private Point zhuanhuan(BizTravePoint bizTravePoint){
        Point point = new Point();
        point.setX(new BigDecimal(bizTravePoint.getAxisx()));
        point.setY(new BigDecimal(bizTravePoint.getAxisy()));
        point.setZ(new BigDecimal(bizTravePoint.getAxisz()));
        return point;
    }

}
