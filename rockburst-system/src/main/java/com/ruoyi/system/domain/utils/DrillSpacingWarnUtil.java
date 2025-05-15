package com.ruoyi.system.domain.utils;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.Entity.CacheDataEntity;
import com.ruoyi.system.domain.dto.CoordinatePointDTO;
import com.ruoyi.system.mapper.*;

import java.util.ArrayList;
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
    public static String warningLogic(Long projectId, BizProjectRecordMapper bizProjectRecordMapper,
                                      BizDangerAreaMapper bizDangerAreaMapper,
                                      BizTunnelBarMapper bizTunnelBarMapper,
                                      BizTravePointMapper bizTravePointMapper,
                                      SysConfigMapper sysConfigMapper,
                                      CacheDataMapper cacheDataMapper,
                                      BizDangerLevelMapper bizDangerLevelMapper,
                                      SysDictDataMapper sysDictDataMapper) {
        StringBuilder tag = new StringBuilder();
        BizProjectRecord projectRecord = bizProjectRecordMapper.selectOne(new LambdaQueryWrapper<BizProjectRecord>()
                .eq(BizProjectRecord::getProjectId, projectId));

        if (projectRecord == null) {
            return "";
        }

        // 获取当前施工钻孔坐标
        String obtainCoordinate = AlgorithmUtils.obtainCoordinate(projectRecord.getWorkfaceId(), projectRecord.getTunnelId(),
                projectRecord.getTravePointId(), projectRecord.getConstructRange(), bizTunnelBarMapper, bizTravePointMapper, sysConfigMapper);

        if (StringUtils.isBlank(obtainCoordinate)) {
            return "";
        }

        String[] coordinate = StringUtils.split(obtainCoordinate, ',');
        if (coordinate.length < 2) {
            // 坐标格式错误
            return "";
        }

        double x;
        double y;
        try {
            x = Double.parseDouble(coordinate[0]);
            y = Double.parseDouble(coordinate[1]);
        } catch (NumberFormatException e) {
            // 解析失败
            return "";
        }
        ;
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
            return "";
        }

        for (BizDangerArea bizDangerArea : bizDangerAreas) {
            List<CoordinatePointDTO> coordinatePointDTOS = buildCoordinatePoints(bizDangerArea);
            // 判断施工钻孔是否在此危险区之内
            boolean pointInPolygon = AlgorithmUtils.isPointInPolygon(coordinatePointDTOS, new CoordinatePointDTO(x, y));
            if (pointInPolygon) {
                String result = algorithm(bizProjectRecords, bizDangerArea, projectId, coordinate, cacheDataMapper,
                        bizProjectRecordMapper, bizDangerLevelMapper, sysDictDataMapper,
                        bizTunnelBarMapper, bizTravePointMapper, sysConfigMapper);
                tag.append(result);
            }
        }
        return tag.toString();
    }

    private static String algorithm(List<BizProjectRecord> bizProjectRecords, BizDangerArea bizDangerArea, Long projectId, String[] currentCoordinate,
                                    CacheDataMapper cacheDataMapper, BizProjectRecordMapper bizProjectRecordMapper,
                                    BizDangerLevelMapper bizDangerLevelMapper, SysDictDataMapper sysDictDataMapper,
                                    BizTunnelBarMapper bizTunnelBarMapper, BizTravePointMapper bizTravePointMapper,
                                    SysConfigMapper sysConfigMapper) {
        String tag = "";
        if (ObjectUtil.isNull(bizProjectRecords)) {
            // todo 后续与开门口进行对比，开门口处于危险区内则进行对比，在危险区外则与危险区开始边界进行对比
            // method()

            addCacheData(projectId, bizDangerArea.getDangerAreaId(), cacheDataMapper);
        } else {
            // 该钻孔处于当前危险区前一个钻孔
            CacheDataEntity cacheDataEntity = cacheDataMapper.selectOne(
                    new LambdaQueryWrapper<CacheDataEntity>()
                            .eq(CacheDataEntity::getDangerAreaId, bizDangerArea.getDangerAreaId())
                            .orderByDesc(CacheDataEntity::getNo)
                            .last("LIMIT 1"));

            if (cacheDataEntity == null) {
                addCacheData(projectId, bizDangerArea.getDangerAreaId(), cacheDataMapper);
                return tag;
            }

            // 前一个孔
            Long priorProjectId = cacheDataEntity.getProjectId();
            BizProjectRecord priorRecord = bizProjectRecordMapper.selectOne(new LambdaQueryWrapper<BizProjectRecord>()
                    .eq(BizProjectRecord::getProjectId, priorProjectId));

            String obtainPriorCoordinate = AlgorithmUtils.obtainCoordinate(priorRecord.getWorkfaceId(), priorRecord.getTunnelId(),
                    priorRecord.getTravePointId(), priorRecord.getConstructRange(), bizTunnelBarMapper, bizTravePointMapper, sysConfigMapper);
            // 前一个孔坐标(x,y)
            String[] priorCoordinate = StringUtils.split(obtainPriorCoordinate, ',');
            double priorX = Double.parseDouble(priorCoordinate[0]); // 前一个孔x坐标

            // 当前孔
            BizProjectRecord currentProject = bizProjectRecordMapper.selectOne(new LambdaQueryWrapper<BizProjectRecord>()
                    .eq(BizProjectRecord::getProjectId, projectId));
            double currentX = Double.parseDouble(currentCoordinate[0]); // 当前孔x坐标

            // 获取危险区等级
            String level = bizDangerArea.getLevel();
            // 获取危险区名称
            String levelName = sysDictDataMapper.selectDictLabel(ConstantsInfo.DANGER_AREA_LEVEL_DICT_TYPE, level);
            BizDangerLevel bizDangerLevel = bizDangerLevelMapper.selectOne(new LambdaQueryWrapper<BizDangerLevel>()
                    .eq(BizDangerLevel::getLevel, level));
            // 获取规定危险区间隔
            Double spaced = bizDangerLevel.getSpaced();
            // 当前钻孔是否在前一个钻孔的后方 ?
            if (currentX > priorX) {
                double doingPoorly = DataJudgeUtils.doingPoorly(currentX, priorX);
                if (doingPoorly > spaced) {
                    tag = buildTagMessage(currentProject, priorRecord, levelName, spaced);
                }
            } else {
                tag = buildTagMessage(currentProject, priorRecord, levelName, spaced);
            }
            // 将当前钻孔存储到缓冲表中，方便下次使用
            addCacheData(projectId, bizDangerArea.getDangerAreaId(), cacheDataMapper);
        }
        return tag;
    }

    private static void addCacheData(Long projectId, Long dangerAreaId, CacheDataMapper cacheDataMapper) {
        CacheDataEntity cacheDataEntity = new CacheDataEntity();
        cacheDataEntity.setProjectId(projectId);
        cacheDataEntity.setDangerAreaId(dangerAreaId);
        Integer i = cacheDataMapper.selectMaxNumber(dangerAreaId);
        if (i.equals(0)) {
            cacheDataEntity.setNo(ConstantsInfo.NUMBER);
        } else {
            cacheDataEntity.setNo(i + ConstantsInfo.NUMBER);
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


    private static String buildTagMessage(BizProjectRecord currentProject, BizProjectRecord priorRecord,
                                          String levelName, Double spaced) {
        return currentProject.getDrillNum() + ConstantsInfo.CUE_WORD_ONE +
                priorRecord.getDrillNum() + ConstantsInfo.CUE_WORD_TWO +
                levelName + ConstantsInfo.CUE_WORD_THREE +
                spaced + ConstantsInfo.CUE_WORD_FOUR;
    }
}