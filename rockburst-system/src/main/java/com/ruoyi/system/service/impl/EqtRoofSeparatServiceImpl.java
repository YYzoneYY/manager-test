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
import com.ruoyi.system.domain.EqtRoofSeparat;
import com.ruoyi.system.domain.dto.EqtRoofSeparatDto;
import com.ruoyi.system.domain.dto.EqtSearchDto;
import com.ruoyi.system.mapper.EqtRoofSeparatMapper;
import com.ruoyi.system.service.IEqtRoofSeparatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 工程视频Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Service
public class EqtRoofSeparatServiceImpl extends ServiceImpl<EqtRoofSeparatMapper, EqtRoofSeparat> implements IEqtRoofSeparatService
{
    @Autowired
    private EqtRoofSeparatMapper eqtRoofSeparatMapper;

    @Override
    public EqtRoofSeparat selectDeepById(Long displacementId) {
        return eqtRoofSeparatMapper.selectById(displacementId);
    }

    @Override
    public MPage<EqtRoofSeparat> selectPageList(EqtSearchDto dto, Pagination pagination) {
        QueryWrapper<EqtRoofSeparat> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(dto.getStatus() != null, EqtRoofSeparat::getStatus, dto.getStatus())
                .eq(dto.getWorkfaceId() != null, EqtRoofSeparat::getWorkFaceId, dto.getWorkfaceId())
                .eq(dto.getSurveyAreaId() != null, EqtRoofSeparat::getSurveyAreaId, dto.getSurveyAreaId())
                .between(dto.getStartTime() != null && dto.getEndTime() !=null , EqtRoofSeparat::getInstallTime , dto.getStartTime(), dto.getEndTime())
                .between(StrUtil.isNotEmpty(dto.getStartTimeStr()) && StrUtil.isNotEmpty(dto.getEndTimeStr()) , EqtRoofSeparat::getInstallTime , dto.getStartTime(), dto.getEndTime());
        IPage<EqtRoofSeparat> list = eqtRoofSeparatMapper.selectPage(pagination, queryWrapper);
        return new MPage<>(list);
    }

    @Override
    public int insertEntity(EqtRoofSeparatDto dto) {
        EqtRoofSeparat entity = new EqtRoofSeparat();
        BeanUtil.copyProperties(dto, entity);

        int i= eqtRoofSeparatMapper.insert(entity);
        if (i > 0  ){

        }
        return 1;
    }

    @Override
    public int updateMById(EqtRoofSeparatDto dto) {
        EqtRoofSeparat entity = new EqtRoofSeparat();
        BeanUtil.copyProperties(dto, entity);

        int i= eqtRoofSeparatMapper.updateById(entity);
        if (i > 0  ){

        }
        return 1;
    }

    @Override
    public int deleteMByIds(Long[] roofSeparatIds) {
        //todo 先删除 预警方案 再搞 删除主体
        UpdateWrapper<EqtRoofSeparat> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(EqtRoofSeparat::getDelFlag, BizBaseConstant.DELFLAG_Y)
                .in(EqtRoofSeparat::getRoofSeparatId, roofSeparatIds);
        return eqtRoofSeparatMapper.update(new EqtRoofSeparat(),updateWrapper);
    }

    @Override
    public int deleteMById(Long roofSeparatId) {
        //todo 先删除 预警方案 再搞 删除主体
        UpdateWrapper<EqtRoofSeparat> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(EqtRoofSeparat::getDelFlag, BizBaseConstant.DELFLAG_Y)
                .eq(EqtRoofSeparat::getRoofSeparatId, roofSeparatId);
        return eqtRoofSeparatMapper.update(new EqtRoofSeparat(),updateWrapper);
    }
}
