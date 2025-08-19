package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.Entity.MultiplePlanEntity;
import com.ruoyi.system.domain.dto.actual.MultipleParamPlanDTO;
import com.ruoyi.system.mapper.MultiplePlanMapper;
import com.ruoyi.system.service.MultiplePlanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: shikai
 * @date: 2025/8/19
 * @description:
 */

@Transactional
@Service
public class MultiplePlanServiceImpl extends ServiceImpl<MultiplePlanMapper, MultiplePlanEntity> implements MultiplePlanService {

    @Resource
    private MultiplePlanMapper multiplePlanMapper;

    @Override
    public boolean saveBatch(List<MultipleParamPlanDTO> multipleParamPlanDTOs, String location, Long mineId) {
        boolean flag = false;
        if (ObjectUtil.isNull(multipleParamPlanDTOs)) {
            throw new RuntimeException("参数不能为空");
        }
        if (ObjectUtil.isNull(location)) {
            throw new RuntimeException("位置参数不能为空");
        }
        ArrayList<MultiplePlanEntity> multiplePlanEntities = new ArrayList<>();
        multipleParamPlanDTOs.forEach(dto -> {
            MultiplePlanEntity entity = new MultiplePlanEntity();
            entity.setParamName(obtainParamName(dto, location));
            entity.setWarnInstanceNum(dto.getWarnInstanceNum());
            entity.setMeasureNum(dto.getMeasureNum());
            entity.setWorkFaceId(dto.getWorkFaceId());
            entity.setSensorType(dto.getSensorType());
            entity.setMineId(mineId);
            multiplePlanEntities.add(entity);
        });
        flag = this.saveBatch(multiplePlanEntities);
        return flag;
    }

    @Override
    public List<MultipleParamPlanDTO> getMultipleParamPlanList(String warnInstanceNum, Long mineId) {
        List<MultiplePlanEntity> multiplePlanEntities = multiplePlanMapper.selectList(new LambdaQueryWrapper<MultiplePlanEntity>()
                .eq(MultiplePlanEntity::getWarnInstanceNum, warnInstanceNum)
                .eq(MultiplePlanEntity::getMineId, mineId));

        if (multiplePlanEntities == null || multiplePlanEntities.isEmpty()) {
            return new ArrayList<>();
        }
        return multiplePlanEntities.stream()
                .map(multiplePlanEntity -> {
                    MultipleParamPlanDTO multipleParamPlanDTO = new MultipleParamPlanDTO();
                    BeanUtils.copyProperties(multiplePlanEntity, multipleParamPlanDTO);
                    return multipleParamPlanDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteByWarnInstanceNum(List<String> warnInstanceNums, Long mineId) {
        LambdaQueryWrapper<MultiplePlanEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(MultiplePlanEntity::getWarnInstanceNum, warnInstanceNums)
                .eq(MultiplePlanEntity::getMineId, mineId);
        return this.remove(queryWrapper);
    }


    private String obtainParamName(MultipleParamPlanDTO dto, String location) {
        String paramName = "";
        paramName = location + "-" + dto.getSensorType() + "-" + dto.getMonitorItems();
        return paramName.toUpperCase();
    }
}