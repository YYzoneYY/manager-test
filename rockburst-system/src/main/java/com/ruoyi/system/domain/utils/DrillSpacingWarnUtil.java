package com.ruoyi.system.domain.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.Entity.CacheDataEntity;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.CoordinatePointDTO;
import com.ruoyi.system.domain.dto.WarningDTO;
import com.ruoyi.system.mapper.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

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
                                                SysDictDataMapper sysDictDataMapper,
                                                TunnelMapper tunnelMapper, BizWorkfaceMapper bizWorkfaceMapper) {
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
                        bizTunnelBarMapper, bizTravePointMapper, sysConfigMapper, tunnelMapper, bizWorkfaceMapper);
                warnings.addAll(results);
            }
        }

        List<Pair<BizDangerArea, BizDangerArea>> dangerAreaPairs = getAdjacentDangerAreaPairs(bizDangerAreas);
        for (Pair<BizDangerArea, BizDangerArea> pair : dangerAreaPairs) {
            BizDangerArea prevArea = pair.getLeft();
            BizDangerArea nextArea = pair.getRight();

            if (!prevArea.getLevel().equals(nextArea.getLevel())) {
                List<WarningDTO> crossWarnings = crossAreaRuleCheck(
                        projectId,
                        prevArea,
                        nextArea,
                        x,
                        bizProjectRecordMapper,
                        cacheDataMapper,
                        bizDangerLevelMapper,
                        tunnelMapper,
                        bizWorkfaceMapper,
                        bizTunnelBarMapper,
                        bizTravePointMapper,
                        sysConfigMapper
                );
                warnings.addAll(crossWarnings);
            }
        }

        return warnings;
    }

    private static List<WarningDTO> algorithm(List<BizProjectRecord> bizProjectRecords, BizDangerArea bizDangerArea, Long projectId, String[] currentCoordinate,
                                              CacheDataMapper cacheDataMapper, BizProjectRecordMapper bizProjectRecordMapper,
                                              BizDangerLevelMapper bizDangerLevelMapper, SysDictDataMapper sysDictDataMapper,
                                              BizTunnelBarMapper bizTunnelBarMapper, BizTravePointMapper bizTravePointMapper,
                                              SysConfigMapper sysConfigMapper, TunnelMapper tunnelMapper, BizWorkfaceMapper bizWorkfaceMapper) {
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
                    WarningDTO dto = buildWarningDTO(currentProject, priorRecord, levelName, spaced, distance, bizWorkfaceMapper, tunnelMapper);
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
                    WarningDTO dto = buildWarningDTO(currentProject, nextRecord, levelName, spaced, distance, bizWorkfaceMapper, tunnelMapper);
                    dto.setBetweenDrills(isBetweenDrills); // 设置标识字段
                    warnings.add(dto);
                }
            }
        }

        // 10. 缓存当前钻孔（带排序插入）
        addCacheData(projectId, bizDangerArea.getDangerAreaId(), cacheDataMapper, drillInfos);

        return warnings;
    }

    /**
     * 获取相邻危险区对 (生成相邻BizDangerArea对象的配对列表,
     * 遍历allAreas列表，将每两个相邻元素构造成一个Pair，存入结果列表中返回。)
     * @param allAreas 危险区列表
     * @return 配对列表
     */
    private static List<Pair<BizDangerArea, BizDangerArea>> getAdjacentDangerAreaPairs(List<BizDangerArea> allAreas) {
        List<Pair<BizDangerArea, BizDangerArea>> pairs = new ArrayList<>();
        for (int i = 0; i < allAreas.size() - 1; i++) {
            pairs.add(new ImmutablePair<>(allAreas.get(i), allAreas.get(i + 1)));
        }
        return pairs;
    }

    /**
     * 跨危险区边界钻孔间距预警
     */
    private static List<WarningDTO> crossAreaRuleCheck(
            Long currentProjectId,
            BizDangerArea prevArea,
            BizDangerArea nextArea,
            double currentX,
            BizProjectRecordMapper bizProjectRecordMapper,
            CacheDataMapper cacheDataMapper,
            BizDangerLevelMapper bizDangerLevelMapper,
            TunnelMapper tunnelMapper,
            BizWorkfaceMapper bizWorkfaceMapper,
            BizTunnelBarMapper bizTunnelBarMapper,
            BizTravePointMapper bizTravePointMapper,
            SysConfigMapper sysConfigMapper
    ) {
        List<WarningDTO> warnings = new ArrayList<>();

        // 获取当前钻孔实体
        BizProjectRecord currentProject = bizProjectRecordMapper.selectOne(
                new LambdaQueryWrapper<BizProjectRecord>().eq(BizProjectRecord::getProjectId, currentProjectId)
        );

        if (currentProject == null) {
            return warnings;
        }

        // 获取前一个和后一个危险区的安全间距
        String prevLevel = prevArea.getLevel();
        String nextLevel = nextArea.getLevel();

        if (StringUtils.isAnyBlank(prevLevel, nextLevel)) {
            return warnings;
        }
        Double prevSpaced = getSpacingByLevel(prevLevel, bizDangerLevelMapper);
        Double nextSpaced = getSpacingByLevel(nextLevel, bizDangerLevelMapper);

        if (prevSpaced == null || nextSpaced == null) {
            return warnings;
        }

        // 获取前一个危险区最后一个历史钻孔（限制只取一条）
        CacheDataEntity latestCacheRecord = cacheDataMapper.selectOne(
                new LambdaQueryWrapper<CacheDataEntity>()
                        .eq(CacheDataEntity::getDangerAreaId, prevArea.getDangerAreaId())
                        .orderByDesc(CacheDataEntity::getNo)
        );
        if (latestCacheRecord == null) return warnings;

        Long lastDrillId = latestCacheRecord.getProjectId();
        BizProjectRecord lastDrillRecord = bizProjectRecordMapper.selectOne(
                new LambdaQueryWrapper<BizProjectRecord>().eq(BizProjectRecord::getProjectId, lastDrillId)
        );
        if (lastDrillRecord == null) {
            return warnings;
        }
        // 获取前最后一个历史钻孔的坐标
        String coord = AlgorithmUtils.obtainCoordinate(
                lastDrillRecord.getWorkfaceId(),
                lastDrillRecord.getTunnelId(),
                lastDrillRecord.getTravePointId(),
                lastDrillRecord.getConstructRange(),
                bizTunnelBarMapper, bizTravePointMapper, sysConfigMapper
        );
        if (StringUtils.isBlank(coord)) {
            return warnings;
        }

        String[] parts = StringUtils.split(coord, ',');
        if (parts.length < 2) {
            return warnings;
        }

        double lastDrillX;
        try {
            lastDrillX = Double.parseDouble(parts[0]);
        } catch (NumberFormatException e) {
            return warnings;
        }

        // 获取下一个危险区生产帮开始点 X 坐标
        String scbStartx = nextArea.getScbStartx();
        if (StringUtils.isBlank(scbStartx)) return warnings;

        double nextStartX;
        try {
            nextStartX = Double.parseDouble(scbStartx);
        } catch (NumberFormatException e) {
            return warnings;
        }

        // 计算距离
        double distanceBetween = DataJudgeUtils.doingPoorly(lastDrillX, nextStartX);

        // Rule 1: 前低等级 → 后高等级
        if (prevSpaced > nextSpaced) {
            if (distanceBetween > nextSpaced) {
                if (Math.abs(currentX - nextStartX) > 0.001) {
                    WarningDTO dto = buildCrossWarningDTO(currentProject, lastDrillRecord, "Rule1A", nextSpaced, distanceBetween, bizWorkfaceMapper, tunnelMapper);
                    warnings.add(dto);
                }
            } else if (distanceBetween < nextSpaced) {
                double expectedX = lastDrillX + nextSpaced;
                if (Math.abs(currentX - expectedX) > 0.001) {
                    WarningDTO dto = buildCrossWarningDTO(currentProject, lastDrillRecord, "Rule1B", nextSpaced, distanceBetween, bizWorkfaceMapper, tunnelMapper);
                    dto.setExpectedPosition(expectedX);
                    warnings.add(dto);
                }
            }
        }
        // Rule 2: 前高等级 → 后低等级
        else if (prevSpaced < nextSpaced) {
            if (distanceBetween < prevSpaced) {
                if (Math.abs(currentX - nextStartX) > 0.001) {
                    WarningDTO dto = buildCrossWarningDTO(currentProject, lastDrillRecord, "Rule2", prevSpaced, distanceBetween, bizWorkfaceMapper, tunnelMapper);
                    warnings.add(dto);
                }
            }
        }

        return warnings;
    }

    /**
     * 根据危险等级获取间距
     * @param level 等级
     * @param mapper 危险等级Mapper
     * @return 返回结果
     */
    private static Double getSpacingByLevel(String level, BizDangerLevelMapper mapper) {
        BizDangerLevel dangerLevel = mapper.selectOne(
                new LambdaQueryWrapper<BizDangerLevel>().eq(BizDangerLevel::getLevel, level)
        );
        return dangerLevel != null ? dangerLevel.getSpaced() : 0.0;
    }

    /**
     * 构建跨区域警告信息
     * @param current 当前钻孔
     * @param lastDrill 前一个钻孔
     * @param ruleType 规则类型
     * @param requiredSpaced 危险等级的间距
     * @param actualDistance 实际距离
     * @return 警告信息
     */
    private static WarningDTO buildCrossWarningDTO(BizProjectRecord current, BizProjectRecord lastDrill,
                                                   String ruleType, Double requiredSpaced, Double actualDistance,
                                                   BizWorkfaceMapper bizWorkfaceMapper,TunnelMapper tunnelMapper) {
        WarningDTO dto = new WarningDTO();
        dto.setCurrentProjectId(current.getProjectId());
        dto.setCurrentDrillNum(current.getDrillNum());
        dto.setRelatedDrillNum(lastDrill.getDrillNum());
        dto.setRelatedProjectId(lastDrill.getProjectId());
        dto.setRuleType(ruleType); // 可选字段用于标识是哪种规则触发
        dto.setSpaced(requiredSpaced);
        dto.setActualDistance(actualDistance);
        dto.setAlarmTime(System.currentTimeMillis());
        dto.setTunnelName(tunnelMapper.selectOne(new LambdaQueryWrapper<TunnelEntity>()
                .eq(TunnelEntity::getTunnelId, current.getTunnelId())
                .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
        ).getTunnelName());
        dto.setWorkFaceName(bizWorkfaceMapper.selectOne(new LambdaQueryWrapper<BizWorkface>()
                .eq(BizWorkface::getWorkfaceId, current.getWorkfaceId())
                .eq(BizWorkface::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
        ).getWorkfaceName());
        dto.setBetweenDrills(false);
        return dto;
    }

    private static WarningDTO buildWarningDTO(BizProjectRecord current, BizProjectRecord other,
                                              String levelName, Double spaced, Double actual,BizWorkfaceMapper bizWorkfaceMapper,
                                              TunnelMapper tunnelMapper) {
        WarningDTO dto = new WarningDTO();
        dto.setCurrentProjectId(current.getProjectId());
        dto.setCurrentDrillNum(current.getDrillNum());
        dto.setRelatedDrillNum(other.getDrillNum());
        dto.setRelatedProjectId(other.getProjectId());
        dto.setDangerLevelName(levelName);
        dto.setSpaced(spaced);
        dto.setActualDistance(actual);
        dto.setAlarmTime(System.currentTimeMillis());
        BizWorkface bizWorkface = bizWorkfaceMapper.selectOne(new LambdaQueryWrapper<BizWorkface>()
                .eq(BizWorkface::getWorkfaceId, current.getWorkfaceId())
                .eq(BizWorkface::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        dto.setWorkFaceName(bizWorkface.getWorkfaceName());
        TunnelEntity tunnelEntity = tunnelMapper.selectOne(new LambdaQueryWrapper<TunnelEntity>()
                .eq(TunnelEntity::getTunnelId, current.getTunnelId())
                .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        dto.setTunnelName(tunnelEntity.getTunnelName());
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