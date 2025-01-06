package com.ruoyi.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.domain.BizTravePoint;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.Point;
import com.ruoyi.system.domain.vo.BizTravePointVo;
import com.ruoyi.system.mapper.BizTravePointMapper;
import com.ruoyi.system.mapper.TunnelMapper;
import com.ruoyi.system.service.IBizTravePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 矿井管理Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
@Service
public class BizTravePointServiceImpl extends ServiceImpl<BizTravePointMapper, BizTravePoint> implements IBizTravePointService
{

    @Autowired
    private BizTravePointMapper bizTravePointMapper;

    @Autowired
    private TunnelMapper tunnelMapper;

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
    public void doit(BizTravePoint point) {
        List<BizTravePoint> qyList =  this.getQyPoint(point.getWorkfaceId());
        MPJLambdaWrapper<BizTravePoint> queryWrapper = new MPJLambdaWrapper<BizTravePoint>();
        queryWrapper
                .leftJoin(TunnelEntity.class,TunnelEntity::getTunnelId,BizTravePoint::getTunnelId)
                .eq(BizTravePoint::getWorkfaceId,point.getWorkfaceId())
//                .eq(BizTravePoint::getIsVertex,true)
                .in(TunnelEntity::getTunnelType,"SH");
        List<BizTravePoint> shpointList = this.getBaseMapper().selectJoinList(queryWrapper);
        queryWrapper.clear();
        queryWrapper
                .leftJoin(TunnelEntity.class,TunnelEntity::getTunnelId,BizTravePoint::getTunnelId)
                .eq(BizTravePoint::getWorkfaceId,point.getWorkfaceId())
//                .eq(BizTravePoint::getIsVertex,true)
                .in(TunnelEntity::getTunnelType,"XH");
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
