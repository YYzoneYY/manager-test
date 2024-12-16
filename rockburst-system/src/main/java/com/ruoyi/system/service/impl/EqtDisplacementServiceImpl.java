package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.domain.EqtDisplacement;
import com.ruoyi.system.domain.dto.EqtDisplacementDto;
import com.ruoyi.system.domain.dto.EqtSearchDto;
import com.ruoyi.system.mapper.EqtDisplacementMapper;
import com.ruoyi.system.service.IEqtDisplacementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 矿井管理Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
@Service
public class EqtDisplacementServiceImpl extends ServiceImpl<EqtDisplacementMapper, EqtDisplacement> implements IEqtDisplacementService
{

    @Autowired
    private EqtDisplacementMapper eqtDisplacementMapper;

    @Override
    public EqtDisplacement selectDeepById(Long displacementId) {
        return eqtDisplacementMapper.selectById(displacementId);
    }

    @Override
    public MPage<EqtDisplacement> selectPageList(EqtSearchDto dto, Pagination pagination) {
        QueryWrapper<EqtDisplacement> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(dto.getStatus() != null, EqtDisplacement::getStatus, dto.getStatus())
                .eq(dto.getWorkfaceId() != null, EqtDisplacement::getWorkFaceId, dto.getWorkfaceId())
                .eq(dto.getSurveyAreaId() != null, EqtDisplacement::getSurveyAreaId, dto.getSurveyAreaId())
                .between(dto.getStartTime() != null && dto.getEndTime() !=null , EqtDisplacement::getInstallTime , dto.getStartTime(), dto.getEndTime())
                .between(StrUtil.isNotEmpty(dto.getStartTimeStr()) && StrUtil.isNotEmpty(dto.getEndTimeStr()) , EqtDisplacement::getInstallTime , dto.getStartTime(), dto.getEndTime());
        IPage<EqtDisplacement> list = eqtDisplacementMapper.selectPage(pagination, queryWrapper);
        return new MPage<>(list);
    }

    @Override
    public int insertEntity(EqtDisplacementDto dto) {
        EqtDisplacement entity = new EqtDisplacement();
        BeanUtil.copyProperties(dto, entity);

        int i= eqtDisplacementMapper.insert(entity);
        if (i > 0  ){

        }
        return 1;
    }

    @Override
    public int updateMById(EqtDisplacementDto dto) {
        EqtDisplacement entity = new EqtDisplacement();
        BeanUtil.copyProperties(dto, entity);

        int i= eqtDisplacementMapper.updateById(entity);
        if (i > 0  ){

        }
        return 1;
    }

    @Override
    public int deleteMByIds(Long[] displacementIds) {
        //todo 先删除 预警方案 再搞 删除主体
        UpdateWrapper<EqtDisplacement> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(EqtDisplacement::getDelFlag, BizBaseConstant.DELFLAG_Y)
                .in(EqtDisplacement::getDisplacementId, displacementIds);
        return eqtDisplacementMapper.update(new EqtDisplacement(),updateWrapper);
    }

    @Override
    public int deleteMById(Long displacementId) {
        //todo 先删除 预警方案 再搞 删除主体
        UpdateWrapper<EqtDisplacement> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(EqtDisplacement::getDelFlag, BizBaseConstant.DELFLAG_Y)
                .eq(EqtDisplacement::getDisplacementId, displacementId);
        return eqtDisplacementMapper.update(new EqtDisplacement(),updateWrapper);
    }
}
