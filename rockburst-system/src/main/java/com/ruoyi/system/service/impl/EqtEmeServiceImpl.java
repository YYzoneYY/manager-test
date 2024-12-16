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
import com.ruoyi.system.domain.EqtEme;
import com.ruoyi.system.domain.dto.EqtEmeDto;
import com.ruoyi.system.domain.dto.EqtSearchDto;
import com.ruoyi.system.mapper.EqtEmeMapper;
import com.ruoyi.system.service.IEqtEmeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 矿井管理Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
@Service
public class EqtEmeServiceImpl extends ServiceImpl<EqtEmeMapper, EqtEme> implements IEqtEmeService
{

    @Autowired
    private EqtEmeMapper eqtEmeMapper;

    @Override
    public EqtEme selectDeepById(Long displacementId) {
        return eqtEmeMapper.selectById(displacementId);
    }

    @Override
    public MPage<EqtEme> selectPageList(EqtSearchDto dto, Pagination pagination) {
        QueryWrapper<EqtEme> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(dto.getStatus() != null, EqtEme::getStatus, dto.getStatus())
                .eq(dto.getWorkfaceId() != null, EqtEme::getWorkFaceId, dto.getWorkfaceId())
//                .eq(dto.getSurveyAreaId() != null, EqtEme::getSurveyAreaId, dto.getSurveyAreaId())
                .between(dto.getStartTime() != null && dto.getEndTime() !=null , EqtEme::getInstallTime , dto.getStartTime(), dto.getEndTime())
                .between(StrUtil.isNotEmpty(dto.getStartTimeStr()) && StrUtil.isNotEmpty(dto.getEndTimeStr()) , EqtEme::getInstallTime , dto.getStartTime(), dto.getEndTime());
        IPage<EqtEme> list = eqtEmeMapper.selectPage(pagination, queryWrapper);
        return new MPage<>(list);
    }

    @Override
    public int insertEntity(EqtEmeDto dto) {
        EqtEme entity = new EqtEme();
        BeanUtil.copyProperties(dto, entity);
        int i = eqtEmeMapper.insert(entity);
        if(i > 0){
            //新增预警方案记录把
        }
        return 1;
    }

    @Override
    public int updateMById(EqtEmeDto dto) {
        EqtEme entity = new EqtEme();
        BeanUtil.copyProperties(dto, entity);
        int i = eqtEmeMapper.updateById(entity);
        if(i > 0){

        }
        return 1;
    }

    @Override
    public int deleteMByIds(Long[] emeIds) {
        //todo 先删除 预警方案 再搞 删除主体
        UpdateWrapper<EqtEme> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(EqtEme::getDelFlag, BizBaseConstant.DELFLAG_Y)
                .in(EqtEme::getEmeId, emeIds);
        return eqtEmeMapper.update(new EqtEme(),updateWrapper);
    }

    @Override
    public int deleteMById(Long emeId) {
        //todo 先删除 预警方案 再搞 删除主体
        UpdateWrapper<EqtEme> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(EqtEme::getDelFlag, BizBaseConstant.DELFLAG_Y)
                .eq(EqtEme::getEmeId, emeId);
        return eqtEmeMapper.update(new EqtEme(),updateWrapper);
    }
}
