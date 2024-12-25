package com.ruoyi.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.domain.BizTravePoint;
import com.ruoyi.system.domain.vo.BizTravePointVo;
import com.ruoyi.system.mapper.BizTravePointMapper;
import com.ruoyi.system.service.IBizTravePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public MPage<BizTravePointVo> geRuleList(Long workfaceId,String constructType, Pagination pagination) {
        MPJLambdaWrapper<BizTravePoint> queryWrapper = new MPJLambdaWrapper<BizTravePoint>();
        queryWrapper.leftJoin(BizProjectRecord.class,BizProjectRecord::getTravePointId,BizTravePoint::getPointId)
                .selectSum(BizProjectRecord::getProjectId,BizTravePointVo::getDid)
                .selectAll(BizTravePoint.class)
                .eq(StrUtil.isNotEmpty(constructType) , BizProjectRecord::getConstructType,constructType)
                .eq(workfaceId != null , BizTravePoint::getWorkfaceId, workfaceId)
                .eq(BizTravePoint::getDelFlag, BizBaseConstant.DELFLAG_N);
        IPage<BizTravePointVo> list = bizTravePointMapper.selectJoinPage(pagination,BizTravePointVo.class,queryWrapper);
        return new MPage<>(list);
    }
}
