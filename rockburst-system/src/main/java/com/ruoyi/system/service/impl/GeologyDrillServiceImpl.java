package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.Entity.DrillMappingEntity;
import com.ruoyi.system.domain.Entity.GeologyDrillEntity;
import com.ruoyi.system.domain.dto.DrillPropertiesDTO;
import com.ruoyi.system.domain.dto.GeologyDrillDTO;
import com.ruoyi.system.domain.dto.GeologyDrillInfoDTO;
import com.ruoyi.system.domain.dto.ImportDrillMappingDTO;
import com.ruoyi.system.domain.utils.TrimUtils;
import com.ruoyi.system.domain.vo.GeologyDrillVO;
import com.ruoyi.system.mapper.DrillMappingMapper;
import com.ruoyi.system.mapper.GeologyDrillMapper;
import com.ruoyi.system.service.DrillMappingService;
import com.ruoyi.system.service.DrillingStressService;
import com.ruoyi.system.service.GeologyDrillService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
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

    @Resource
    private DrillMappingMapper drillMappingMapper;

    @Resource
    private DrillMappingService drillMappingService;

    @Resource
    private GeologyDrillService geologyDrillService;

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
        ).stream().collect(Collectors.toMap(GeologyDrillEntity::getDataName, Function.identity()));

        // 4. 处理每条数据
        for (GeologyDrillDTO dto : geologyDrillDTOList) {
            GeologyDrillEntity entity = existingMap.getOrDefault(dto.getDataName(), new GeologyDrillEntity());

            // 属性拷贝
            BeanUtils.copyProperties(dto, entity);
            entityList.add(entity);
        }
        // 5. 批量保存或更新
        return this.saveOrUpdateBatch(entityList);
    }

    @Override
    public GeologyDrillInfoDTO obtainGeologyDrillInfo(String drillName) {
        if (ObjectUtil.isNull(drillName)) {
            throw new RuntimeException("参数错误,钻孔名称不能为空");
        }
        GeologyDrillEntity geologyDrillEntity = geologyDrillMapper.selectOne(new LambdaQueryWrapper<GeologyDrillEntity>()
                .eq(GeologyDrillEntity::getDataName, drillName));
        if (ObjectUtil.isNull(geologyDrillEntity)) {
            throw new RuntimeException("钻孔不存在");
        }
        GeologyDrillInfoDTO geologyDrillInfoDTO = new GeologyDrillInfoDTO();
        BeanUtils.copyProperties(geologyDrillEntity, geologyDrillInfoDTO);
        List<DrillPropertiesDTO> drillProperties = drillMappingService.getDrillProperties(geologyDrillEntity.getGeologyDrillId());
        geologyDrillInfoDTO.setDrillPropertiesDTOS(drillProperties);
        return geologyDrillInfoDTO;
    }

    @Override
    public TableData pageQueryList(String drillName, Integer pageNum, Integer pageSize) {
        TableData result = new TableData();
        if (null == pageNum || pageNum < 1) {
            pageNum = 1;
        }
        if (null == pageSize || pageSize < 1) {
            pageSize = 10;
        }
        PageHelper.startPage(pageNum, pageSize);
        Page<GeologyDrillVO> page = geologyDrillMapper.queryByPage(drillName);
        if (ListUtils.isNotNull(page.getResult())) {
            page.getResult().forEach(geologyDrillVO -> {
                geologyDrillVO.setDrillPropertiesDTOS(drillMappingService.getDrillProperties(geologyDrillVO.getGeologyDrillId()));
            });
        }
        result.setTotal(page.getTotal());
        result.setRows(page.getResult());
        return result;
    }

    @Override
    public List<GeologyDrillVO> obtainGeologyDrillList(Long mineId) {
        List<GeologyDrillVO> geologyDrillVOList = new ArrayList<>();
        List<GeologyDrillEntity> geologyDrillEntities = geologyDrillMapper.selectList(new LambdaQueryWrapper<GeologyDrillEntity>()
                .eq(GeologyDrillEntity::getMineId, mineId));
        if (ListUtils.isNotNull(geologyDrillEntities)) {
            geologyDrillEntities.forEach(geologyDrillEntity -> {
                GeologyDrillVO geologyDrillVO = new GeologyDrillVO();
                BeanUtils.copyProperties(geologyDrillEntity, geologyDrillVO);
                geologyDrillVO.setDrillPropertiesDTOS(drillMappingService.getDrillProperties(geologyDrillEntity.getGeologyDrillId()));
                geologyDrillVOList.add(geologyDrillVO);
            });
        }
        return geologyDrillVOList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String importData(MultipartFile file, Long geologyDrillId) throws Exception {
        ExcelUtil<ImportDrillMappingDTO> util = new ExcelUtil<>(ImportDrillMappingDTO.class);
        List<ImportDrillMappingDTO> list;
        try (InputStream inputStream = file.getInputStream()) {
            list = util.importExcel(inputStream);
        }
        if (CollUtil.isEmpty(list)) {
            throw new RuntimeException("导入内容不能为空！！");
        }
        int currentLine = 2;
        List<DrillMappingEntity> entities = new ArrayList<>();

        for (ImportDrillMappingDTO dto : list) {
            try {
                TrimUtils.trimBean(dto);

                DrillMappingEntity drillMappingEntity = new DrillMappingEntity();
                BeanUtils.copyProperties(dto, drillMappingEntity);
                drillMappingEntity.setGeologyDrillId(geologyDrillId);
                entities.add(drillMappingEntity);
                currentLine++;
            } catch (Exception e) {
                throw new RuntimeException("导入第(" + currentLine + ")行失败！失败原因：" + e.getMessage(), e);
            }
        }
        // 批量插入
        if (!entities.isEmpty()) {
            // 1. 删除旧数据
            drillMappingMapper.delete(new LambdaQueryWrapper<DrillMappingEntity>()
                    .eq(DrillMappingEntity::getGeologyDrillId, geologyDrillId));
            // 2. 批量插入新数据
            drillMappingService.saveBatch(entities);
        }
        return "导入成功";
    }

    @Override
    public boolean oneClickDelete() {
        try {
            geologyDrillMapper.truncateTable();
            return true;
        } catch (Exception e) {
            log.error("清空地质钻孔表并重置自增失败", e);
            throw new RuntimeException("清空数据失败，请联系管理员");
        }
    }
}