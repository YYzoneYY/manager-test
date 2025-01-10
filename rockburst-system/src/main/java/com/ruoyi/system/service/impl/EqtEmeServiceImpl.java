package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.domain.Entity.WarnSchemeSeparateEntity;
import com.ruoyi.system.domain.EqtEme;
import com.ruoyi.system.domain.dto.EqtEmeDto;
import com.ruoyi.system.domain.dto.EqtSearchDto;
import com.ruoyi.system.domain.dto.WarnSchemeDTO;
import com.ruoyi.system.domain.utils.ObtainWarnSchemeUtils;
import com.ruoyi.system.domain.vo.EqtEmeVo;
import com.ruoyi.system.mapper.EqtEmeMapper;
import com.ruoyi.system.mapper.WarnSchemeMapper;
import com.ruoyi.system.mapper.WarnSchemeSeparateMapper;
import com.ruoyi.system.service.IEqtEmeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private WarnSchemeMapper warnSchemeMapper;

    @Autowired
    private WarnSchemeSeparateMapper warnSchemeSeparateMapper;

    @Override
    public EqtEmeVo selectDeepById(Long displacementId) {

        EqtEme entity =  this.getBaseMapper().selectById(displacementId);
        EqtEmeVo vo = new EqtEmeVo();
        BeanUtils.copyProperties(entity, vo);
        WarnSchemeDTO warnSchemeDTO = ObtainWarnSchemeUtils.getObtainWarnScheme(vo.getMeasureNum(), vo.getSceneType(),
                vo.getWorkFaceId(), warnSchemeMapper, warnSchemeSeparateMapper);
        vo.setWarnSchemeDTO(warnSchemeDTO);
        return vo;
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
        Assert.isTrue(checkMeasureNum(dto.getMeasureNum(),null), "测点编码重复");

        EqtEme entity = new EqtEme();
        BeanUtil.copyProperties(dto, entity);
        int i = eqtEmeMapper.insert(entity);
        WarnSchemeSeparateEntity warnEntity = new WarnSchemeSeparateEntity();
        BeanUtil.copyProperties(dto, warnEntity);

        warnEntity.setGrowthRateConfig(dto.getGrowthRateConfigDTOS());
        warnEntity.setIncrementConfig(dto.getIncrementConfigDTOS());
        warnEntity.setThresholdConfig(dto.getThresholdConfigDTOS());
        warnSchemeSeparateMapper.insert(warnEntity);
        return 1;
    }
    @Override
    public boolean checkMeasureNum(String measureNum,Long id) {
        QueryWrapper<EqtEme> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(EqtEme::getMeasureNum, measureNum)
                .ne(id != null , EqtEme::getEmeId,id)
                .eq(EqtEme::getDelFlag, BizBaseConstant.DELFLAG_N);
        long count = this.getBaseMapper().selectCount(queryWrapper);
        if(count > 0 ){
            return false;
        }
        return true;
    }

    @Override
    public int updateMById(EqtEmeDto dto) {
        Assert.isTrue(checkMeasureNum(dto.getMeasureNum(),dto.getEmeId()), "测点编码重复");

        EqtEme entity = new EqtEme();
        BeanUtil.copyProperties(dto, entity);
        int i = eqtEmeMapper.updateById(entity);
        QueryWrapper<WarnSchemeSeparateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(WarnSchemeSeparateEntity::getMeasureNum, dto.getMeasureNum());
        warnSchemeSeparateMapper.delete(queryWrapper);
        WarnSchemeSeparateEntity warnEntity = new WarnSchemeSeparateEntity();
        BeanUtil.copyProperties(dto, warnEntity);

        warnEntity.setGrowthRateConfig(dto.getGrowthRateConfigDTOS());
        warnEntity.setIncrementConfig(dto.getIncrementConfigDTOS());
        warnEntity.setThresholdConfig(dto.getThresholdConfigDTOS());
        warnSchemeSeparateMapper.insert(warnEntity);
        return 1;
    }

    @Override
    public int deleteMByIds(Long[] emeIds) {
        //todo 先删除 预警方案 再搞 删除主体
        List<EqtEme> lists =  this.getBaseMapper().selectBatchIds(Arrays.asList(emeIds));
        List<String> measureNum = new ArrayList<>();
        if(lists != null && lists.size() > 0){
            measureNum =  lists.stream().map(EqtEme::getMeasureNum).collect(Collectors.toList());
        }
        QueryWrapper<WarnSchemeSeparateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(WarnSchemeSeparateEntity::getMeasureNum, measureNum);
        warnSchemeSeparateMapper.delete(queryWrapper);

        UpdateWrapper<EqtEme> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(EqtEme::getDelFlag, BizBaseConstant.DELFLAG_Y)
                .in(EqtEme::getEmeId, emeIds);
        return eqtEmeMapper.update(new EqtEme(),updateWrapper);
    }

    @Override
    public int deleteMById(Long emeId) {
        List<EqtEme> lists =  this.getBaseMapper().selectBatchIds(Arrays.asList(emeId));
        List<String> measureNum = new ArrayList<>();
        if(lists != null && lists.size() > 0){
            measureNum =  lists.stream().map(EqtEme::getMeasureNum).collect(Collectors.toList());
        }
        QueryWrapper<WarnSchemeSeparateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(WarnSchemeSeparateEntity::getMeasureNum, measureNum);
        warnSchemeSeparateMapper.delete(queryWrapper);
        //todo 先删除 预警方案 再搞 删除主体
        UpdateWrapper<EqtEme> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(EqtEme::getDelFlag, BizBaseConstant.DELFLAG_Y)
                .eq(EqtEme::getEmeId, emeId);
        return eqtEmeMapper.update(new EqtEme(),updateWrapper);
    }
}
