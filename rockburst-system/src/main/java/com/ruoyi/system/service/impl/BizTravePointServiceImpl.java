package com.ruoyi.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.domain.BizDangerArea;
import com.ruoyi.system.domain.BizPresetPoint;
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.domain.BizTravePoint;
import com.ruoyi.system.domain.dto.BizTravePointDto;
import com.ruoyi.system.domain.vo.BizTravePointVo;
import com.ruoyi.system.mapper.BizDangerAreaMapper;
import com.ruoyi.system.mapper.BizPresetPointMapper;
import com.ruoyi.system.mapper.BizTravePointMapper;
import com.ruoyi.system.mapper.TunnelMapper;
import com.ruoyi.system.service.IBizTravePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    @Autowired
    private BizDangerAreaMapper bizDangerAreaMapper;
    @Autowired
    private BizPresetPointMapper bizPresetPointMapper;


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
       BizTravePoint end =  this.getById(endPoinId);
        QueryWrapper<BizTravePoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(BizTravePoint::getTunnelId,start.getTunnelId())
                .between(BizTravePoint::getNo, start.getNo(), end.getNo());

        return this.list(queryWrapper);
    }

    @Override
    public Long judgePointInArea(Long pointId, Double meter) {


        QueryWrapper<BizDangerArea> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .apply("FIND_IN_SET({0}, pointlist)", pointId);
        List<BizDangerArea> points = bizDangerAreaMapper.selectList(queryWrapper);
        if(points != null && points.size() > 0){
            return points.get(0).getDangerAreaId();
        }
        queryWrapper.clear();

        queryWrapper.lambda().eq(BizDangerArea::getEndPointId,pointId);
        List<BizDangerArea> endPoints = bizDangerAreaMapper.selectList(queryWrapper);

        BigDecimal meterBigDecimal = new BigDecimal(meter);
        if(endPoints != null && endPoints.size() > 0){
            for (BizDangerArea endPoint : endPoints) {
                BizTravePoint currentPoint =  this.getById(endPoint.getEndPointId());
                if(endPoint.getStartPointId() == endPoint.getEndPointId()){
                    BigDecimal start = new BigDecimal(endPoint.getStartMeter()) ;
                    BigDecimal end = new BigDecimal(endPoint.getEndMeter()) ;
                    if(meterBigDecimal.compareTo(end) == -1 && meterBigDecimal.compareTo(start) == 1){
                        return endPoint.getDangerAreaId();
                    }
                }else{
                    currentPoint.getPrePointDistance();
                    BigDecimal start = new BigDecimal(currentPoint.getPrePointDistance()).negate() ;
                    BigDecimal end = new BigDecimal(endPoint.getEndMeter()) ;
                    if(meterBigDecimal.compareTo(start) == 1 && meterBigDecimal.compareTo(end) == -1){
                        return endPoint.getDangerAreaId();
                    }
                }

            }
        }

        queryWrapper.clear();
        queryWrapper.lambda().eq(BizDangerArea::getStartPointId,pointId);
        List<BizDangerArea> startPoints = bizDangerAreaMapper.selectList(queryWrapper);
        if(startPoints != null && startPoints.size() > 0){
            for (BizDangerArea startPoint : startPoints) {
                if(startPoint.getStartPointId() == startPoint.getEndPointId()){
                    BigDecimal start = new BigDecimal(startPoint.getStartMeter()) ;
                    BigDecimal end = new BigDecimal(startPoint.getEndMeter()) ;
                    if(meterBigDecimal.compareTo(end) == -1 && meterBigDecimal.compareTo(start) == 1){
                        return startPoint.getDangerAreaId();
                    }
                }else{
                    BigDecimal start = new BigDecimal(startPoint.getStartMeter()) ;
                    if(meterBigDecimal.compareTo(start) == 1 || meterBigDecimal.compareTo(start) == 0){
                        return startPoint.getDangerAreaId();
                    }
                }

            }
        }

        return null;
    }

    @Override
    public BizPresetPoint getPointPre(Long pointId, Double meter) {
        if(meter > 0){
            BizTravePoint point = this.getNextPoint(pointId);
            BigDecimal meterBigDecimal = new BigDecimal(meter).setScale(2, RoundingMode.HALF_UP);
            BigDecimal prePointDistanceBigDecimal = new BigDecimal(point.getPrePointDistance()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal mmBigDecimal = meterBigDecimal.subtract(prePointDistanceBigDecimal).setScale(2, RoundingMode.HALF_UP);
//            Double mm =  meter - point.getPrePointDistance();
            BizPresetPoint presetPoint = new BizPresetPoint();
            presetPoint.setPointId(point.getPointId()).setMeter(mmBigDecimal.doubleValue());
            return presetPoint;
        }
        BizPresetPoint presetPoint = new BizPresetPoint();
        presetPoint.setPointId(pointId).setMeter(meter);
        return presetPoint;
    }

    @Override
    public BizPresetPoint getPointFront(Long pointId, Double meter) {
        if(meter < 0){
            BizTravePoint point = this.getById(pointId);
            BigDecimal meterBigDecimal = new BigDecimal(meter).setScale(2, RoundingMode.HALF_UP);
            BigDecimal prePointDistanceBigDecimal = new BigDecimal(point.getPrePointDistance()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal mmBigDecimal = prePointDistanceBigDecimal.subtract(meterBigDecimal).setScale(2, RoundingMode.HALF_UP);
//            Double mm =  meter - point.getPrePointDistance();
            BizPresetPoint presetPoint = new BizPresetPoint();
            BizTravePoint pointpre = this.getPrePoint(pointId);

            presetPoint.setPointId(pointpre.getPointId()).setMeter(mmBigDecimal.doubleValue());
            return presetPoint;
        }
        BizPresetPoint presetPoint = new BizPresetPoint();
        presetPoint.setPointId(pointId).setMeter(meter);
        return presetPoint;
    }

    public BizPresetPoint sssss(Double x, Integer jio, BizPresetPoint point){
        BigDecimal lonMove =  new BigDecimal(Math.sin(Math.toRadians(jio))).multiply(new BigDecimal(x)).setScale(8, RoundingMode.HALF_UP);
        BigDecimal latMove =  new BigDecimal(Math.cos(Math.toRadians(jio))).multiply(new BigDecimal(x)).setScale(8, RoundingMode.HALF_UP);
        BigDecimal lat =  new BigDecimal(point.getLatitude());
        point.setLatitudet(lat.add(latMove)+"");
        point.setLongitudet(new BigDecimal(point.getLongitude()).add(lonMove)+"");
        return point;
    }

    @Override
    public BizPresetPoint getPointLatLon(Long pointId, Double meter) {
        BizPresetPoint p = getPointPre(pointId,meter);
        QueryWrapper<BizPresetPoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BizPresetPoint::getPointId,p.getPointId()).eq(BizPresetPoint::getMeter,p.getMeter());
        List<BizPresetPoint> list = bizPresetPointMapper.selectList(queryWrapper);
        if(list != null && list.size() > 0){
            p.setPointId(p.getPointId()).setLongitude(list.get(0).getLongitude()).setLatitude(list.get(0).getLatitude());
            return p;
        }
        BizTravePoint currentPoint =  this.getById(p.getPointId());
        if(meter == 0){
            p.setLatitude(currentPoint.getLatitude()).setLongitude(currentPoint.getLongitude());
            return p;
        }

        BizPresetPoint point = setAxis(p.getPointId(),p.getMeter());
        p.setLongitude(point.getLongitude()+"").setLatitude(point.getLatitude()+"");
        return p;
    }




    public BizPresetPoint setAxis( Long currentPointId, Double meter) {

        BizPresetPoint point = new BizPresetPoint();

        point.setPointId(currentPointId).setMeter(new BigDecimal(meter).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());

        BizTravePoint currentPoint = this.getById(currentPointId);

        BizTravePoint prePoint = this.getPrePoint(currentPointId);
        BizTravePoint afterPoint = this.getNextPoint(currentPointId);

        //存在前一个导线点的情况
        if(prePoint != null && prePoint.getPointId() != null){
            //计算 坐标
            BigDecimal latSum =  axisSum(new BigDecimal(currentPoint.getLatitude()),new BigDecimal(prePoint.getLatitude()));
            BigDecimal lonSum =  axisSum(new BigDecimal(currentPoint.getLongitude()),new BigDecimal(prePoint.getLongitude()));


            BigDecimal latshang = latSum.divide(new BigDecimal(currentPoint.getPrePointDistance()),10,BigDecimal.ROUND_DOWN);
            BigDecimal lonshang = lonSum.divide(new BigDecimal(currentPoint.getPrePointDistance()),10,BigDecimal.ROUND_DOWN);
            BigDecimal latMove = latshang.multiply(new BigDecimal(point.getMeter()).abs());
            BigDecimal lonMove = lonshang.multiply(new BigDecimal(point.getMeter()).abs());


            BigDecimal lat = getAxis(new BigDecimal(currentPoint.getLatitude()),new BigDecimal(prePoint.getLatitude()),latMove);
            BigDecimal lon = getAxis(new BigDecimal(currentPoint.getLongitude()),new BigDecimal(prePoint.getLongitude()),lonMove);

            point.setLatitude(lat+"").setLongitude(lon+"");
            return point;
        }else if(afterPoint != null && afterPoint.getPointId() != null){

            BigDecimal latSum =  axisSum(new BigDecimal(currentPoint.getLatitude()),new BigDecimal(afterPoint.getLatitude()));
            BigDecimal lonSum =  axisSum(new BigDecimal(currentPoint.getLongitude()),new BigDecimal(afterPoint.getLongitude()));
            BigDecimal latshang = latSum.divide(new BigDecimal(afterPoint.getPrePointDistance()),10,BigDecimal.ROUND_DOWN);
            BigDecimal lonshang = lonSum.divide(new BigDecimal(afterPoint.getPrePointDistance()),10,BigDecimal.ROUND_DOWN);
            BigDecimal latMove = latshang.multiply(new BigDecimal(point.getMeter()).abs());
            BigDecimal lonMove = lonshang.multiply(new BigDecimal(point.getMeter()).abs());

            BigDecimal lat = getAxis1(new BigDecimal(currentPoint.getLatitude()),new BigDecimal(afterPoint.getLatitude()),latMove).setScale(10, BigDecimal.ROUND_DOWN);
            BigDecimal lon = getAxis1(new BigDecimal(currentPoint.getLongitude()),new BigDecimal(afterPoint.getLongitude()),lonMove).setScale(10, BigDecimal.ROUND_DOWN);

            point.setLatitude(lat+"").setLongitude(lon+"");
            return point;
        }
        return point;
    }


    /**
     * 坐标求和
     * @return
     */
    public BigDecimal axisSum(BigDecimal axis1, BigDecimal axis2){
//        if(axis2.signum() == 1 && axis1.signum() == 1){
//            return axis2.add(axis1);
//        }
//        if(axis2.signum() == -1 && axis1.signum() == -1){
//            return axis2.add(axis1).abs();
//        }
//        if(axis2.signum() == -1 && axis1.signum() == 1){
//            return axis2.abs().add(axis1);
//        }
//        if(axis2.signum() == 1 && axis1.signum() == -1){
//            return axis2.add(axis1.abs());
//        }
        return axis2.subtract(axis1).abs();
    }

    public BigDecimal getAxis(BigDecimal axisCurrent, BigDecimal axisPre, BigDecimal move){
        if(axisCurrent.compareTo(axisPre) == 1){
            return axisCurrent.subtract(move).setScale(10, BigDecimal.ROUND_DOWN);
        }
        if(axisCurrent.compareTo(axisPre) == -1){
            return axisCurrent.add(move).setScale(10, BigDecimal.ROUND_DOWN);
        }

        return axisCurrent.setScale(10, BigDecimal.ROUND_DOWN);
    }

    public BigDecimal getAxis1(BigDecimal axisCurrent, BigDecimal axisAfter, BigDecimal move){
        if(axisAfter.compareTo(axisCurrent) == 1){
            return axisCurrent.subtract(move).setScale(10, BigDecimal.ROUND_DOWN);
        }
        if(axisAfter.compareTo(axisCurrent) == -1){
            return axisCurrent.add(move).setScale(10, BigDecimal.ROUND_DOWN);
        }

        return axisCurrent;
    }
    @Override
    public BizPresetPoint getLatLontop(String lat, String lon, Double x, Integer jio) {

        BizPresetPoint point = new BizPresetPoint();
        BigDecimal lonMove =  new BigDecimal(Math.sin(Math.toRadians(jio))).multiply(new BigDecimal(x)).setScale(8, RoundingMode.HALF_UP);
        BigDecimal latMove =  new BigDecimal(Math.cos(Math.toRadians(jio))).multiply(new BigDecimal(x)).setScale(8, RoundingMode.HALF_UP);
        BigDecimal latitudet =  new BigDecimal(lat);
        BigDecimal longitudet =  new BigDecimal(lon);
        point.setLatitudet(latitudet.add(latMove)+"");
        point.setLongitudet(longitudet.add(lonMove)+"");
        return point;
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
    public List<Long> getInPointListNoStartEnd(Long startPointId, Double startMeter, Long endPointId, Double endMeter) {
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

        pointIds.remove(startPointId);

        pointIds.remove(endPointId);


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

//    @Override
//    public List<BizTravePoint> getQyPoint(Long workfaceId) {
//        QueryWrapper<TunnelEntity> queryWrapper = new QueryWrapper<>();
//        queryWrapper.lambda().eq(TunnelEntity::getWorkFaceId,workfaceId)
//                .eq(TunnelEntity::getTunnelType,"QY");
//        List<TunnelEntity> list = tunnelMapper.selectList(queryWrapper);
//        if(list == null || list.size() == 0){
//            return  null;
//        }
//        TunnelEntity tunnelEntity = list.get(0);
//        QueryWrapper<BizTravePoint> pointQueryWrapper = new QueryWrapper<>();
//        pointQueryWrapper.lambda()
//                .eq(BizTravePoint::getTunnelId,tunnelEntity.getTunnelId())
//                .eq(BizTravePoint::getIsVertex,true);
//        return bizTravePointMapper.selectList(pointQueryWrapper);
//    }
//
//    @Override
//    public Long getVertexCount(Long pointId, Long tunnelId, Boolean vertex) {
//        QueryWrapper<BizTravePoint> queryWrapper = new QueryWrapper<>();
//        queryWrapper.lambda()
//                .notIn(pointId != null, BizTravePoint::getPointId,pointId)
//                .eq(BizTravePoint::getTunnelId, tunnelId)
//                .eq(BizTravePoint::getIsVertex,vertex);
//        return bizTravePointMapper.selectCount(queryWrapper);
//    }

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
//
//    @Override
//    public BizTravePoint getPoint(BizTravePoint a, BizTravePoint b, Double distance ) {
//        double[] s = null;
//        if(a.getNo() < b.getNo()){
//            s = MathUtil.getPointAtDistance(Double.parseDouble(a.getAxisx()),Double.parseDouble(a.getAxisy()),Double.parseDouble(a.getAxisz())
//                    ,Double.parseDouble(b.getAxisx()),Double.parseDouble(b.getAxisy()),Double.parseDouble(b.getAxisz()),-distance);
//        }else {
//            s = MathUtil.getPointAtDistance(Double.parseDouble(a.getAxisx()),Double.parseDouble(a.getAxisy()),Double.parseDouble(a.getAxisz())
//                    ,Double.parseDouble(b.getAxisx()),Double.parseDouble(b.getAxisy()),Double.parseDouble(b.getAxisz()),distance);
//        }
//        BizTravePoint point = new BizTravePoint();
//        point.setAxisx(String.valueOf(s[0]));
//        point.setAxisy(String.valueOf(s[1]));
//        point.setAxisz(String.valueOf(s[2]));
//        return point;
//    }


//
//    @Override
//    public void doit(BizTravePoint point) {
////        List<BizTravePoint> qyList =  this.getQyPoint(point.getWorkfaceId());
//        MPJLambdaWrapper<BizTravePoint> queryWrapper = new MPJLambdaWrapper<BizTravePoint>();
//        queryWrapper
//                .leftJoin(TunnelEntity.class,TunnelEntity::getTunnelId,BizTravePoint::getTunnelId)
//                .eq(BizTravePoint::getWorkfaceId,point.getWorkfaceId())
////                .eq(BizTravePoint::getIsVertex,true)
//                .in(TunnelEntity::getTunnelType,BizBaseConstant.TUNNEL_SH);
//        List<BizTravePoint> shpointList = this.getBaseMapper().selectJoinList(queryWrapper);
//
//        queryWrapper.clear();
//        queryWrapper
//                .leftJoin(TunnelEntity.class,TunnelEntity::getTunnelId,BizTravePoint::getTunnelId)
//                .eq(BizTravePoint::getWorkfaceId,point.getWorkfaceId())
////                .eq(BizTravePoint::getIsVertex,true)
//                .in(TunnelEntity::getTunnelType,BizBaseConstant.TUNNEL_XH);
//        List<BizTravePoint> xhpointList = this.getBaseMapper().selectJoinList(queryWrapper);
//
//        Double q = new Double("0");
//        Long shId = null;
//        for (int i = 0; i < shpointList.size(); i++) {
//            BizTravePoint shpoint = shpointList.get(i);
//            Double qq = getMath(zhuanhuan(shpointList.get(i)),zhuanhuan(point));
//            if(i == 0){
//                q = qq;
//                shId = shpointList.get(i).getPointId();
//            }
//            if(q >= qq){
//                q = qq;
//                shId = shpointList.get(i).getPointId();
//            }
//            shpoint.setDistance(qq);
//        }
//        Double q1 = new Double("0");
//        Long xhId = null;
//        for (int i = 0; i < xhpointList.size(); i++) {
//            BizTravePoint xhpoint = xhpointList.get(i);
//            Double qq = getMath(zhuanhuan(xhpointList.get(i)),zhuanhuan(point));
//            if(i == 0){
//                q1 = qq;
//                xhId = xhpointList.get(i).getPointId();
//            }
//            if(q >= qq){
//                q1 = qq;
//                xhId = xhpointList.get(i).getPointId();
//            }
//            xhpoint.setDistance(qq);
//        }
//        if(q > q1){
//            this.updateBatchById(xhpointList);
//            UpdateWrapper<BizTravePoint> updateWrapper = new UpdateWrapper<>();
//            updateWrapper.lambda().set(BizTravePoint::getIsVertex,true)
//                    .set(BizTravePoint::getBestNearPointId,point.getPointId())
//                    .eq(BizTravePoint::getPointId,xhId);
//            this.update(updateWrapper);
//        }else {
//            this.updateBatchById(shpointList);
//            UpdateWrapper<BizTravePoint> updateWrapper = new UpdateWrapper<>();
//            updateWrapper.lambda().set(BizTravePoint::getIsVertex,true)
//                    .set(BizTravePoint::getBestNearPointId,point.getPointId())
//                    .eq(BizTravePoint::getPointId,shId);
//            this.update(updateWrapper);
//        }
//
//    }

//    private Double getMath(Point a, Point n){
//        BigDecimal ax = a.getX().subtract(n.getX());
//        BigDecimal ay = a.getY().subtract(n.getY());
//        BigDecimal az = a.getZ().subtract(n.getZ());
//
//        ax = ax.multiply(ax);
//        ay = ay.multiply(ay);
//        az = az.multiply(az);
//
//        Double an = Math.sqrt(ax.add(ay).add(az).doubleValue());
//        return an;
//    }

//    private Point zhuanhuan(BizTravePoint bizTravePoint){
//        Point point = new Point();
//        point.setX(new BigDecimal(bizTravePoint.getAxisx()));
//        point.setY(new BigDecimal(bizTravePoint.getAxisy()));
//        point.setZ(new BigDecimal(bizTravePoint.getAxisz()));
//        return point;
//    }

}
