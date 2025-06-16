package com.ruoyi.system.domain.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.Entity.CacheDataEntity;
import com.ruoyi.system.domain.dto.CoordinatePointDTO;
import com.ruoyi.system.domain.dto.WarningDTO;
import com.ruoyi.system.mapper.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author: shikai
 * @date: 2025/5/15
 * @description:
 */
public class DrillSpacingWarnUtil {
    /**
     * 钻孔 spacing(间距) 危险判断逻辑
     */
    public static List<WarningDTO> warningLogic(Long projectId,
                                                BizProjectRecordMapper bizProjectRecordMapper,
                                                BizDangerAreaMapper bizDangerAreaMapper,
                                                BizTunnelBarMapper bizTunnelBarMapper,
                                                BizTravePointMapper bizTravePointMapper,
                                                SysConfigMapper sysConfigMapper,
                                                CacheDataMapper cacheDataMapper,
                                                BizDangerLevelMapper bizDangerLevelMapper,
                                                SysDictDataMapper sysDictDataMapper) {
        List<WarningDTO> warnings = new ArrayList<>();

        BizProjectRecord projectRecord = bizProjectRecordMapper.selectOne(new LambdaQueryWrapper<BizProjectRecord>()
                .eq(BizProjectRecord::getProjectId, projectId));

        if (projectRecord == null) {
            return warnings;
        }

        // 获取当前施工钻孔坐标
        String obtainCoordinate = AlgorithmUtils.obtainCoordinate(projectRecord.getWorkfaceId(), projectRecord.getTunnelId(),
                projectRecord.getTravePointId(), projectRecord.getConstructRange(), bizTunnelBarMapper, bizTravePointMapper, sysConfigMapper);

        if (StringUtils.isBlank(obtainCoordinate)) {
            return warnings;
        }

        String[] coordinate = StringUtils.split(obtainCoordinate, ',');
        if (coordinate.length < 2) {
            return warnings;
        }

        double x;
        double y;
        try {
            x = Double.parseDouble(coordinate[0]);
            y = Double.parseDouble(coordinate[1]);
        } catch (NumberFormatException e) {
            return warnings;
        }

        List<BizProjectRecord> bizProjectRecords = bizProjectRecordMapper.selectList(new LambdaQueryWrapper<BizProjectRecord>()
                .eq(BizProjectRecord::getStatus, ConstantsInfo.AUDITED_DICT_VALUE)
                .ne(BizProjectRecord::getProjectId, projectId)
                .eq(BizProjectRecord::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));

        MPJLambdaWrapper<BizDangerArea> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper.eq(BizDangerArea::getWorkfaceId, projectRecord.getWorkfaceId())
                .eq(BizDangerArea::getTunnelId, projectRecord.getTunnelId())
                .eq(BizDangerArea::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG);
        List<BizDangerArea> bizDangerAreas = bizDangerAreaMapper.selectJoinList(queryWrapper);

        if (bizDangerAreas == null || bizDangerAreas.isEmpty()) {
            return warnings;
        }

        for (BizDangerArea bizDangerArea : bizDangerAreas) {
            List<CoordinatePointDTO> coordinatePointDTOS = buildCoordinatePoints(bizDangerArea);
            boolean pointInPolygon = AlgorithmUtils.isPointInPolygon(coordinatePointDTOS, new CoordinatePointDTO(x, y));
            if (pointInPolygon) {
                List<WarningDTO> results = algorithm(bizProjectRecords, bizDangerArea, projectId, coordinate, cacheDataMapper,
                        bizProjectRecordMapper, bizDangerLevelMapper, sysDictDataMapper,
                        bizTunnelBarMapper, bizTravePointMapper, sysConfigMapper);
                warnings.addAll(results);
            }
        }

        return warnings;
    }

    private static List<WarningDTO> algorithm(List<BizProjectRecord> bizProjectRecords, BizDangerArea bizDangerArea, Long projectId, String[] currentCoordinate,
                                              CacheDataMapper cacheDataMapper, BizProjectRecordMapper bizProjectRecordMapper,
                                              BizDangerLevelMapper bizDangerLevelMapper, SysDictDataMapper sysDictDataMapper,
                                              BizTunnelBarMapper bizTunnelBarMapper, BizTravePointMapper bizTravePointMapper,
                                              SysConfigMapper sysConfigMapper) {
        List<WarningDTO> warnings = new ArrayList<>();

        // 1. 获取当前危险区内的所有缓存记录（按 no 升序）
        List<CacheDataEntity> allCacheRecords = cacheDataMapper.selectList(
                new LambdaQueryWrapper<CacheDataEntity>()
                        .eq(CacheDataEntity::getDangerAreaId, bizDangerArea.getDangerAreaId())
                        .orderByAsc(CacheDataEntity::getNo)
        );

        // 2. 构建当前钻孔坐标
        double currentX = Double.parseDouble(currentCoordinate[0]);

        // 3. 创建 DrillInfo 列表，包含所有历史钻孔 + 当前钻孔
        List<DrillInfo> drillInfos = new ArrayList<>();

        // 添加历史钻孔
        for (CacheDataEntity entity : allCacheRecords) {
            BizProjectRecord record = bizProjectRecordMapper.selectOne(
                    new LambdaQueryWrapper<BizProjectRecord>()
                            .eq(BizProjectRecord::getProjectId, entity.getProjectId())
            );
            if (record == null) continue;

            String coord = AlgorithmUtils.obtainCoordinate(
                    record.getWorkfaceId(), record.getTunnelId(),
                    record.getTravePointId(), record.getConstructRange(),
                    bizTunnelBarMapper, bizTravePointMapper, sysConfigMapper
            );

            if (StringUtils.isBlank(coord)) continue;

            String[] parts = StringUtils.split(coord, ',');
            if (parts.length < 2) continue;

            try {
                double x = Double.parseDouble(parts[0]);
                drillInfos.add(new DrillInfo(entity.getProjectId(), x));
            } catch (NumberFormatException e) {
                continue;
            }
        }

        // 添加当前钻孔
        drillInfos.add(new DrillInfo(projectId, currentX));

        // 4. 按 x 坐标排序
        drillInfos.sort(Comparator.comparingDouble(d -> d.x));

        // 5. 找出当前钻孔的位置索引
        int insertIndex = -1;
        for (int i = 0; i < drillInfos.size(); i++) {
            if (drillInfos.get(i).projectId.equals(projectId)) {
                insertIndex = i;
                break;
            }
        }

        if (insertIndex == -1) {
            return warnings;
        }

        // 6. 获取当前钻孔实体
        BizProjectRecord currentProject = bizProjectRecordMapper.selectOne(
                new LambdaQueryWrapper<BizProjectRecord>()
                        .eq(BizProjectRecord::getProjectId, projectId)
        );

        // 7. 获取安全规则
        String level = bizDangerArea.getLevel();
        String levelName = sysDictDataMapper.selectDictLabel(ConstantsInfo.DANGER_AREA_LEVEL_DICT_TYPE, level);
        BizDangerLevel dangerLevel = bizDangerLevelMapper.selectOne(
                new LambdaQueryWrapper<BizDangerLevel>().eq(BizDangerLevel::getLevel, level)
        );
        Double spaced = dangerLevel.getSpaced();

        // 标记是否是两孔之间的插入
        boolean isBetweenDrills = insertIndex > 0 && insertIndex < drillInfos.size() - 1;

        // 8. 判断前一个钻孔
        if (insertIndex > 0) {
            DrillInfo priorDrill = drillInfos.get(insertIndex - 1);
            BizProjectRecord priorRecord = bizProjectRecordMapper.selectOne(
                    new LambdaQueryWrapper<BizProjectRecord>()
                            .eq(BizProjectRecord::getProjectId, priorDrill.projectId)
            );
            if (priorRecord != null) {
                double distance = DataJudgeUtils.doingPoorly(currentX, priorDrill.x);
                if (distance > spaced) {
                    WarningDTO dto = buildWarningDTO(currentProject, priorRecord, levelName, spaced, distance);
                    dto.setBetweenDrills(isBetweenDrills); // 设置标识字段
                    warnings.add(dto);
                }
            }
        }

        // 9. 判断后一个钻孔：只在中间插入时才判断
        if (insertIndex < drillInfos.size() - 1 && insertIndex > 0) {
            DrillInfo nextDrill = drillInfos.get(insertIndex + 1);
            BizProjectRecord nextRecord = bizProjectRecordMapper.selectOne(
                    new LambdaQueryWrapper<BizProjectRecord>()
                            .eq(BizProjectRecord::getProjectId, nextDrill.projectId)
            );
            if (nextRecord != null) {
                double distance = DataJudgeUtils.doingPoorly(nextDrill.x, currentX);
                if (distance > spaced) {
                    WarningDTO dto = buildWarningDTO(currentProject, nextRecord, levelName, spaced, distance);
                    dto.setBetweenDrills(isBetweenDrills); // 设置标识字段
                    warnings.add(dto);
                }
            }
        }

        // 10. 缓存当前钻孔（带排序插入）
        addCacheData(projectId, bizDangerArea.getDangerAreaId(), cacheDataMapper, drillInfos);

        return warnings;
    }

    private static WarningDTO buildWarningDTO(BizProjectRecord current, BizProjectRecord other,
                                              String levelName, Double spaced, Double actual) {
        WarningDTO dto = new WarningDTO();
        dto.setCurrentProjectId(current.getProjectId());
        dto.setCurrentDrillNum(current.getDrillNum());
        dto.setRelatedDrillNum(other.getDrillNum());
        dto.setRelatedProjectId(other.getProjectId());
        dto.setDangerLevelName(levelName);
        dto.setSpaced(spaced);
        dto.setActualDistance(actual);
        dto.setAlarmTime(System.currentTimeMillis());
        return dto;
    }

    private static void addCacheData(Long projectId, Long dangerAreaId, CacheDataMapper cacheDataMapper, List<DrillInfo> drillInfos) {
        CacheDataEntity existing = cacheDataMapper.selectOne(
                new LambdaQueryWrapper<CacheDataEntity>()
                        .eq(CacheDataEntity::getProjectId, projectId)
                        .eq(CacheDataEntity::getDangerAreaId, dangerAreaId)
        );
        if (existing != null){
            return;
        }

        CacheDataEntity cacheDataEntity = new CacheDataEntity();
        cacheDataEntity.setProjectId(projectId);
        cacheDataEntity.setDangerAreaId(dangerAreaId);

        if (drillInfos == null || drillInfos.isEmpty()) {
            cacheDataEntity.setNo(ConstantsInfo.NUMBER);
        } else {
            int insertIndex = -1;
            for (int i = 0; i < drillInfos.size(); i++) {
                if (drillInfos.get(i).projectId.equals(projectId)) {
                    insertIndex = i;
                    break;
                }
            }

            if (insertIndex != -1) {
                cacheDataEntity.setNo(insertIndex + ConstantsInfo.NUMBER);
            } else {
                Integer maxNo = cacheDataMapper.selectMaxNumber(dangerAreaId);
                cacheDataEntity.setNo(maxNo == null ? ConstantsInfo.NUMBER : maxNo + ConstantsInfo.NUMBER);
            }
        }

        cacheDataMapper.insert(cacheDataEntity);
    }

    private static List<CoordinatePointDTO> buildCoordinatePoints(BizDangerArea area) {
        List<CoordinatePointDTO> points = new ArrayList<>();
        points.add(new CoordinatePointDTO(Double.parseDouble(area.getScbStartx()), Double.parseDouble(area.getScbStarty())));
        points.add(new CoordinatePointDTO(Double.parseDouble(area.getScbEndx()), Double.parseDouble(area.getScbEndy())));
        points.add(new CoordinatePointDTO(Double.parseDouble(area.getFscbStartx()), Double.parseDouble(area.getFscbStarty())));
        points.add(new CoordinatePointDTO(Double.parseDouble(area.getFscbEndx()), Double.parseDouble(area.getFscbEndy())));
        return points;
    }

    // 辅助类：存储钻孔 ID 和 x 坐标
    private static class DrillInfo {
        Long projectId;
        double x;

        public DrillInfo(Long projectId, double x) {
            this.projectId = projectId;
            this.x = x;
        }
    }
}