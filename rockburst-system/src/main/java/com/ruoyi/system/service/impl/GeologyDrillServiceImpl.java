package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.Entity.GeologyDrillEntity;
import com.ruoyi.system.domain.dto.GeologyDrillDTO;
import com.ruoyi.system.mapper.GeologyDrillMapper;
import com.ruoyi.system.service.GeologyDrillService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author: shikai
 * @date: 2025/6/17
 * @description:
 */

@Service
@Transactional
public class GeologyDrillServiceImpl extends ServiceImpl<GeologyDrillMapper, GeologyDrillEntity> implements GeologyDrillService {

    @Resource
    private GeologyDrillMapper geologyDrillMapper;

    @Override
    public boolean batchInsert(List<GeologyDrillDTO> geologyDrillDTOList) {
        // 1. 参数校验
        if (CollectionUtils.isEmpty(geologyDrillDTOList)) {
            return false;
        }
        // 2. 准备实体列表
        List<GeologyDrillEntity> entityList = new ArrayList<>(geologyDrillDTOList.size());

        // 3. 批量查询已存在的数据（优化为一次查询）
        List<String> dataNames = geologyDrillDTOList.stream()
                .map(GeologyDrillDTO::getDataName)
                .collect(Collectors.toList());

        Map<String, GeologyDrillEntity> existingMap = geologyDrillMapper.selectList(
                new LambdaQueryWrapper<GeologyDrillEntity>()
                        .in(GeologyDrillEntity::getDataName, dataNames)
                        .eq(GeologyDrillEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
        ).stream().collect(Collectors.toMap(GeologyDrillEntity::getDataName, Function.identity()));

        // 4. 处理每条数据
        for (GeologyDrillDTO dto : geologyDrillDTOList) {
            GeologyDrillEntity entity = existingMap.getOrDefault(dto.getDataName(), new GeologyDrillEntity());

            // 如果是新增数据，设置删除标志
            if (entity.getGeologyDrillId() == null) {
                entity.setDelFlag(ConstantsInfo.ZERO_DEL_FLAG);
            }
            // 属性拷贝
            BeanUtils.copyProperties(dto, entity);
            entityList.add(entity);
        }
        // 5. 批量保存或更新
        return this.saveOrUpdateBatch(entityList);
    }

}