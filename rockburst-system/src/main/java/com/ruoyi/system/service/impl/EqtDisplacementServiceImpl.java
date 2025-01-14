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
import com.ruoyi.system.domain.EqtDisplacement;
import com.ruoyi.system.domain.dto.EqtDisplacementDto;
import com.ruoyi.system.domain.dto.EqtSearchDto;
import com.ruoyi.system.domain.dto.WarnSchemeDTO;
import com.ruoyi.system.domain.utils.ObtainWarnSchemeUtils;
import com.ruoyi.system.domain.vo.EqtDisplacementVo;
import com.ruoyi.system.mapper.EqtDisplacementMapper;
import com.ruoyi.system.mapper.WarnSchemeMapper;
import com.ruoyi.system.mapper.WarnSchemeSeparateMapper;
import com.ruoyi.system.service.IEqtDisplacementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private WarnSchemeMapper warnSchemeMapper;

    @Autowired
    private WarnSchemeSeparateMapper warnSchemeSeparateMapper;

    @Override
    public EqtDisplacementVo selectDeepById(Long displacementId) {

        EqtDisplacement entity =  this.getBaseMapper().selectById(displacementId);
        EqtDisplacementVo vo = new EqtDisplacementVo();
        BeanUtils.copyProperties(entity, vo);
        // 获取预警方案基本信息
        WarnSchemeDTO warnSchemeDTO = ObtainWarnSchemeUtils.getObtainWarnScheme(vo.getMeasureNum(), vo.getSceneType(),
                vo.getWorkFaceId(), warnSchemeMapper, warnSchemeSeparateMapper);
        vo.setWarnSchemeDTO(warnSchemeDTO);
        return vo;
    }



    @Override
    public MPage<EqtDisplacement> selectPageList(EqtSearchDto dto, Pagination pagination) {
        QueryWrapper<EqtDisplacement> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(dto.getStatus() != null, EqtDisplacement::getStatus, dto.getStatus())
                .eq(dto.getWorkfaceId() != null, EqtDisplacement::getWorkFaceId, dto.getWorkfaceId())
                .like(StrUtil.isNotEmpty( dto.getSurveyArea()), EqtDisplacement::getSurveyArea, dto.getSurveyArea())
                .between(dto.getStartTime() != null && dto.getEndTime() !=null , EqtDisplacement::getInstallTime , dto.getStartTime(), dto.getEndTime())
                .between(StrUtil.isNotEmpty(dto.getStartTimeStr()) && StrUtil.isNotEmpty(dto.getEndTimeStr()) , EqtDisplacement::getInstallTime , dto.getStartTime(), dto.getEndTime());
        IPage<EqtDisplacement> list = eqtDisplacementMapper.selectPage(pagination, queryWrapper);
        return new MPage<>(list);
    }

    @Override
    public int insertEntity(EqtDisplacementDto dto) {
        Assert.isTrue(checkMeasureNum(dto.getMeasureNum(),null), "测点编码重复");
        EqtDisplacement entity = new EqtDisplacement();
        String maxMeasureNum = "";
        BeanUtil.copyProperties(dto, entity);
        QueryWrapper<EqtDisplacement> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .select(EqtDisplacement::getMeasureNum)
                .eq(EqtDisplacement::getTag,1)
                .eq(EqtDisplacement::getDelFlag, BizBaseConstant.DELFLAG_N);
        List<EqtDisplacement> eqtDisplacementList = eqtDisplacementMapper.selectList(queryWrapper);
        List<Integer> measureNums = new ArrayList<>();
        if(eqtDisplacementList != null && eqtDisplacementList.size() > 0){
            for (EqtDisplacement eqtDisplacement : eqtDisplacementList) {
                String measureStr = eqtDisplacement.getMeasureNum().substring(eqtDisplacement.getMeasureNum().length(),-4);
                Integer measureNum = Integer.parseInt(measureStr);
                measureNums.add(measureNum);
            }
            int maxValue = Collections.max(measureNums);
            maxMeasureNum =BizBaseConstant.getMeasurePre(BizBaseConstant.HDWY,maxValue);
        }else {
            maxMeasureNum =BizBaseConstant.getMeasurePre(BizBaseConstant.HDWY,1);
        }
        entity.setMeasureNum(maxMeasureNum);
        int i= eqtDisplacementMapper.insert(entity);
        WarnSchemeSeparateEntity warnEntity = new WarnSchemeSeparateEntity();
        BeanUtil.copyProperties(dto, warnEntity);

        warnEntity.setGrowthRateConfig(dto.getGrowthRateConfigDTOS());
        warnEntity.setIncrementConfig(dto.getIncrementConfigDTOS());
        warnEntity.setThresholdConfig(dto.getThresholdConfigDTOS());
        warnSchemeSeparateMapper.insert(warnEntity);
        return 1;
    }

    @Override
    public int updateMById(EqtDisplacementDto dto) {

        Assert.isTrue(checkMeasureNum(dto.getMeasureNum(),dto.getDisplacementId()), "测点编码重复");

        EqtDisplacement entity = new EqtDisplacement();
        BeanUtil.copyProperties(dto, entity);

        int i= eqtDisplacementMapper.updateById(entity);


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
    public int deleteMByIds(Long[] displacementIds) {

        List<EqtDisplacement> lists =  this.getBaseMapper().selectBatchIds(Arrays.asList(displacementIds));
        List<String> measureNum = new ArrayList<>();
        if(lists != null && lists.size() > 0){
            measureNum =  lists.stream().map(EqtDisplacement::getMeasureNum).collect(Collectors.toList());
        }
        QueryWrapper<WarnSchemeSeparateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(WarnSchemeSeparateEntity::getMeasureNum, measureNum);
        warnSchemeSeparateMapper.delete(queryWrapper);
        //todo 先删除 预警方案 再搞 删除主体
        UpdateWrapper<EqtDisplacement> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(EqtDisplacement::getDelFlag, BizBaseConstant.DELFLAG_Y)
                .in(EqtDisplacement::getDisplacementId, displacementIds);
        return eqtDisplacementMapper.update(new EqtDisplacement(),updateWrapper);
    }

    @Override
    public int deleteMById(Long displacementId) {
        List<EqtDisplacement> lists =  this.getBaseMapper().selectBatchIds(Arrays.asList(displacementId));
        List<String> measureNum = new ArrayList<>();
        if(lists != null && lists.size() > 0){
            measureNum =  lists.stream().map(EqtDisplacement::getMeasureNum).collect(Collectors.toList());
        }
        QueryWrapper<WarnSchemeSeparateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(WarnSchemeSeparateEntity::getMeasureNum, measureNum);
        warnSchemeSeparateMapper.delete(queryWrapper);
        //todo 先删除 预警方案 再搞 删除主体
        UpdateWrapper<EqtDisplacement> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(EqtDisplacement::getDelFlag, BizBaseConstant.DELFLAG_Y)
                .eq(EqtDisplacement::getDisplacementId, displacementId);
        return eqtDisplacementMapper.update(new EqtDisplacement(),updateWrapper);
    }

    @Override
    public boolean checkMeasureNum(String measureNum,Long id) {
        QueryWrapper<EqtDisplacement> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(EqtDisplacement::getMeasureNum, measureNum)
                .ne(id != null , EqtDisplacement::getDisplacementId,id)
                .eq(EqtDisplacement::getDelFlag, BizBaseConstant.DELFLAG_N);
        long count = this.getBaseMapper().selectCount(queryWrapper);
        if(count > 0 ){
            return false;
        }
        return true;
    }
}
