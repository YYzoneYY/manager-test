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
import com.ruoyi.system.domain.EqtRoofSeparat;
import com.ruoyi.system.domain.dto.EqtRoofSeparatDto;
import com.ruoyi.system.domain.dto.EqtSearchDto;
import com.ruoyi.system.domain.dto.WarnSchemeDTO;
import com.ruoyi.system.domain.utils.ObtainWarnSchemeUtils;
import com.ruoyi.system.domain.vo.EqtRoofSeparatVo;
import com.ruoyi.system.mapper.EqtRoofSeparatMapper;
import com.ruoyi.system.mapper.WarnSchemeMapper;
import com.ruoyi.system.mapper.WarnSchemeSeparateMapper;
import com.ruoyi.system.service.IEqtRoofSeparatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private WarnSchemeMapper warnSchemeMapper;

    @Autowired
    private WarnSchemeSeparateMapper warnSchemeSeparateMapper;


    @Override
    public EqtRoofSeparatVo selectDeepById(Long displacementId) {

        EqtRoofSeparat entity =  this.getBaseMapper().selectById(displacementId);
        EqtRoofSeparatVo vo = new EqtRoofSeparatVo();
        BeanUtils.copyProperties(entity, vo);


        WarnSchemeDTO warnSchemeDTO = ObtainWarnSchemeUtils.getObtainWarnScheme(vo.getMeasureNum(), vo.getSceneType(),
                vo.getWorkFaceId(), warnSchemeMapper, warnSchemeSeparateMapper);
        vo.setWarnSchemeDTO(warnSchemeDTO);
        return vo;



    }

    @Override
    public MPage<EqtRoofSeparat> selectPageList(EqtSearchDto dto, Pagination pagination) {
        QueryWrapper<EqtRoofSeparat> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(dto.getStatus() != null, EqtRoofSeparat::getStatus, dto.getStatus())
                .eq(dto.getWorkfaceId() != null, EqtRoofSeparat::getWorkFaceId, dto.getWorkfaceId())
                .like(StrUtil.isNotEmpty( dto.getSurveyArea()), EqtRoofSeparat::getSurveyArea, dto.getSurveyArea())
                .between(dto.getStartTime() != null && dto.getEndTime() !=null , EqtRoofSeparat::getInstallTime , dto.getStartTime(), dto.getEndTime())
                .between(StrUtil.isNotEmpty(dto.getStartTimeStr()) && StrUtil.isNotEmpty(dto.getEndTimeStr()) , EqtRoofSeparat::getInstallTime , dto.getStartTime(), dto.getEndTime());
        IPage<EqtRoofSeparat> list = eqtRoofSeparatMapper.selectPage(pagination, queryWrapper);
        return new MPage<>(list);
    }

    @Override
    public int insertEntity(EqtRoofSeparatDto dto) {
        Assert.isTrue(checkMeasureNum(dto.getMeasureNum(),null), "测点编码重复");

        EqtRoofSeparat entity = new EqtRoofSeparat();
        BeanUtil.copyProperties(dto, entity);

        String maxMeasureNum = "";
        QueryWrapper<EqtRoofSeparat> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .select(EqtRoofSeparat::getMeasureNum)
                .eq(EqtRoofSeparat::getTag,1)
                .eq(EqtRoofSeparat::getDelFlag, BizBaseConstant.DELFLAG_N);
        List<EqtRoofSeparat> eqtRoofSeparats = eqtRoofSeparatMapper.selectList(queryWrapper);
        List<Integer> measureNums = new ArrayList<>();
        if(eqtRoofSeparats != null && eqtRoofSeparats.size() > 0){
            for (EqtRoofSeparat eqtRoofSeparat : eqtRoofSeparats) {
                String measureStr = eqtRoofSeparat.getMeasureNum().substring(eqtRoofSeparat.getMeasureNum().length(),-4);
                Integer measureNum = Integer.parseInt(measureStr);
                measureNums.add(measureNum);
            }
            int maxValue = Collections.max(measureNums);
            maxMeasureNum =BizBaseConstant.getMeasurePre(dto.getSceneType(),maxValue);
        }else {
            maxMeasureNum =BizBaseConstant.getMeasurePre(dto.getSceneType(),1);
        }
        entity.setMeasureNum(maxMeasureNum);

        int i= eqtRoofSeparatMapper.insert(entity);
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
        QueryWrapper<EqtRoofSeparat> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(EqtRoofSeparat::getMeasureNum, measureNum)
                .ne(id != null , EqtRoofSeparat::getRoofSeparatId,id)
                .eq(EqtRoofSeparat::getDelFlag, BizBaseConstant.DELFLAG_N);
        long count = this.getBaseMapper().selectCount(queryWrapper);
        if(count > 0 ){
            return false;
        }
        return true;
    }

    @Override
    public int updateMById(EqtRoofSeparatDto dto) {
        Assert.isTrue(checkMeasureNum(dto.getMeasureNum(),dto.getRoofSeparatId()), "测点编码重复");

        EqtRoofSeparat entity = new EqtRoofSeparat();
        BeanUtil.copyProperties(dto, entity);

        int i= eqtRoofSeparatMapper.updateById(entity);
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
    public int deleteMByIds(Long[] roofSeparatIds) {
        //todo 先删除 预警方案 再搞 删除主体

        List<EqtRoofSeparat> lists =  this.getBaseMapper().selectBatchIds(Arrays.asList(roofSeparatIds));
        List<String> measureNum = new ArrayList<>();
        if(lists != null && lists.size() > 0){
            measureNum =  lists.stream().map(EqtRoofSeparat::getMeasureNum).collect(Collectors.toList());
        }
        QueryWrapper<WarnSchemeSeparateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(WarnSchemeSeparateEntity::getMeasureNum, measureNum);
        warnSchemeSeparateMapper.delete(queryWrapper);

        UpdateWrapper<EqtRoofSeparat> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(EqtRoofSeparat::getDelFlag, BizBaseConstant.DELFLAG_Y)
                .in(EqtRoofSeparat::getRoofSeparatId, roofSeparatIds);
        return eqtRoofSeparatMapper.update(new EqtRoofSeparat(),updateWrapper);
    }

    @Override
    public int deleteMById(Long roofSeparatId) {
        //todo 先删除 预警方案 再搞 删除主体
        List<EqtRoofSeparat> lists =  this.getBaseMapper().selectBatchIds(Arrays.asList(roofSeparatId));
        List<String> measureNum = new ArrayList<>();
        if(lists != null && lists.size() > 0){
            measureNum =  lists.stream().map(EqtRoofSeparat::getMeasureNum).collect(Collectors.toList());
        }
        QueryWrapper<WarnSchemeSeparateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(WarnSchemeSeparateEntity::getMeasureNum, measureNum);
        warnSchemeSeparateMapper.delete(queryWrapper);

        UpdateWrapper<EqtRoofSeparat> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(EqtRoofSeparat::getDelFlag, BizBaseConstant.DELFLAG_Y)
                .eq(EqtRoofSeparat::getRoofSeparatId, roofSeparatId);
        return eqtRoofSeparatMapper.update(new EqtRoofSeparat(),updateWrapper);
    }
}
