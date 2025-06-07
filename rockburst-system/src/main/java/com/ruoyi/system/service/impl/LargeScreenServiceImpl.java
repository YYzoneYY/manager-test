package com.ruoyi.system.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.system.domain.BizPresetPoint;
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.PlanAreaEntity;
import com.ruoyi.system.domain.Entity.PlanEntity;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.largeScreen.PlanCountDTO;
import com.ruoyi.system.domain.dto.largeScreen.ProjectDTO;
import com.ruoyi.system.domain.dto.largeScreen.ProjectTypeDTO;
import com.ruoyi.system.domain.dto.largeScreen.Select1DTO;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.IBizTravePointService;
import com.ruoyi.system.service.LargeScreenService;
import com.ruoyi.system.service.PlanAreaService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author: shikai
 * @date: 2025/5/29
 * @description:
 */

@Service
public class LargeScreenServiceImpl implements LargeScreenService {

    @Resource
    private BizProjectRecordMapper bizProjectRecordMapper;

    @Resource
    private TunnelMapper tunnelMapper;

    @Resource
    private BizWorkfaceMapper bizWorkfaceMapper;

    @Resource
    private SysDictDataMapper sysDictDataMapper;

    @Resource
    private PlanMapper planMapper;

    @Resource
    private BizProjectRecordMapper projectRecordMapper;

    @Resource
    private IBizTravePointService bizTravePointService;

    @Resource
    private PlanAreaService planAreaService;


    @Override
    public List<ProjectDTO> obtainProject(String tag, Select1DTO select1DTO) {
        List<ProjectDTO> projectDTOS;

        if ("1".equals(tag)) {
            projectDTOS = bizProjectRecordMapper.queryProjectOfAudit(select1DTO);
        } else {
            projectDTOS = bizProjectRecordMapper.queryProjectOfUnaudited(select1DTO);
        }

        return enrichProjectDTOsWithConstructionSite(projectDTOS);
    }

    @Override
    public List<ProjectTypeDTO> obtainProjectType(Long startTime, Long endTime) {
        List<ProjectTypeDTO> projectTypeDTOS = bizProjectRecordMapper.queryProjectType(startTime, endTime);

        if (ObjectUtil.isNotEmpty(projectTypeDTOS)) {
            // 收集所有 drillType
            List<String> drillTypes = projectTypeDTOS.stream()
                    .map(ProjectTypeDTO::getDrillType)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            // 批量查询字典标签
            Map<String, String> dictLabelMap = sysDictDataMapper.selectDictLabels(ConstantsInfo.DRILL_TYPE_DICT_TYPE, drillTypes);
            // 设置格式化字段
            projectTypeDTOS.forEach(projectTypeDTO -> {
                String drillType = projectTypeDTO.getDrillType();
                if (drillType != null) {
                    projectTypeDTO.setDrillTypeFmt(dictLabelMap.getOrDefault(drillType, ""));
                }
            });
        }
        return projectTypeDTOS;
    }

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    @Override
    public List<PlanCountDTO> obtainPlanCount() {
        List<PlanCountDTO> planCountDTOS = planMapper.queryPlanCount();
        if (planCountDTOS != null && !planCountDTOS.isEmpty()) {
            for (PlanCountDTO planCountDTO : planCountDTOS) {
                int i = 0;
                List<Long> planIds = planCountDTO.getPlanIds();
                if (planIds != null && !planIds.isEmpty()) {
                    planCountDTO.setPlanTotal(planIds.size());

                    // 批量查询 PlanEntity
                    List<PlanEntity> planEntities = planMapper.selectList(new LambdaQueryWrapper<PlanEntity>()
                            .in(PlanEntity::getPlanId, planIds)
                            .eq(PlanEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));

                    Map<Long, PlanEntity> planMap = planEntities.stream()
                            .collect(Collectors.toMap(PlanEntity::getPlanId, Function.identity()));

                    for (Long planId : planIds) {
                        PlanEntity planEntity = planMap.get(planId);
                        if (planEntity == null) {
                            continue; // 跳过无效 planId
                        }

                        BigDecimal schedule = getSchedule(
                                planId,
                                planEntity.getStartTime(),
                                planEntity.getEndTime(),
                                planEntity.getTotalDrillNumber()
                        );

                        if (schedule.compareTo(HUNDRED) == 0) {
                            i++;
                        }
                    }
                }
                planCountDTO.setPlanCompleted(i);
            }
        }
        return planCountDTOS;
    }


    private List<ProjectDTO> enrichProjectDTOsWithConstructionSite(List<ProjectDTO> projectDTOS) {
        if (projectDTOS == null || projectDTOS.isEmpty()) {
            return projectDTOS;
        }

        // 收集需要查询的 ID
        List<Long> tunnelIds = new ArrayList<>();
        List<Long> workFaceIds = new ArrayList<>();

        for (ProjectDTO projectDTO : projectDTOS) {
            String constructType = projectDTO.getConstructType();
            if (ConstantsInfo.TUNNELING.equals(constructType)) {
                tunnelIds.add(projectDTO.getTunnelId());
            } else if (ConstantsInfo.STOPE.equals(constructType)) {
                workFaceIds.add(projectDTO.getWorkFaceId());
            }
        }

        // 批量查询 TunnelEntity 和 BizWorkface
        List<TunnelEntity> tunnels = tunnelMapper.selectList(new LambdaQueryWrapper<TunnelEntity>()
                .in(TunnelEntity::getTunnelId, tunnelIds)
                .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        Map<Long, String> tunnelMap = tunnels.stream()
                .collect(Collectors.toMap(
                        TunnelEntity::getTunnelId,
                        TunnelEntity::getTunnelName,
                        (existing, replacement) -> existing));

        List<BizWorkface> workfaces = bizWorkfaceMapper.selectList(new LambdaQueryWrapper<BizWorkface>()
                .in(BizWorkface::getWorkfaceId, workFaceIds)
                .eq(BizWorkface::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        Map<Long, String> workfaceMap = workfaces.stream()
                .collect(Collectors.toMap(
                        BizWorkface::getWorkfaceId,
                        BizWorkface::getWorkfaceName,
                        (existing, replacement) -> existing));

        // 设置 ConstructionSite
        for (ProjectDTO projectDTO : projectDTOS) {
            String constructType = projectDTO.getConstructType();
            String constructionSite = "";
            if (ConstantsInfo.TUNNELING.equals(constructType)) {
                constructionSite = tunnelMap.getOrDefault(projectDTO.getTunnelId(), "");
            } else if (ConstantsInfo.STOPE.equals(constructType)) {
                constructionSite = workfaceMap.getOrDefault(projectDTO.getWorkFaceId(), "");
            }
            projectDTO.setConstructionSite(constructionSite);
        }

        return projectDTOS;
    }


    /**
     * 计算计划进度
     */
    private BigDecimal getSchedule(Long planId, Long startTime, Long endTime, Integer totalDrillNumber) {
        BigDecimal schedule = new BigDecimal(0);
        List<Long> projectIds = new ArrayList<>();
        QueryWrapper<PlanAreaEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PlanAreaEntity::getPlanId,planId);
        List<PlanAreaEntity> areaEntities =  planAreaService.list(queryWrapper);
        if(areaEntities != null &&  areaEntities.size() > 0){
            for (PlanAreaEntity areaEntity : areaEntities) {
                List<Long> points = bizTravePointService.getInPointListNoStartEnd(areaEntity.getStartTraversePointId(),Double.valueOf(areaEntity.getStartDistance()),areaEntity.getEndTraversePointId(),Double.valueOf(areaEntity.getEndDistance()));
                List<BizProjectRecord> records1 = getSatrtEndPoint(points, DateUtil.date(startTime),DateUtil.date(startTime));
                List<BizProjectRecord> records2 = getSatrtDistance(areaEntity.getStartTraversePointId(),DateUtil.date(startTime),DateUtil.date(endTime),new BigDecimal(areaEntity.getStartDistance()));
                List<BizProjectRecord> records3 = getEndDistance(areaEntity.getEndTraversePointId(),DateUtil.date(startTime),DateUtil.date(endTime),new BigDecimal(areaEntity.getEndDistance()));
                records1.addAll(records2);
                records1.addAll(records3);
                records1 = records1.stream()
                        .collect(Collectors.collectingAndThen(
                                Collectors.toMap(BizProjectRecord::getProjectId, r -> r, (r1, r2) -> r1),
                                map -> new ArrayList<>(map.values())
                        ));
                projectIds.addAll(records1.stream().map(BizProjectRecord::getProjectId).collect(Collectors.toList()));
            }
        }
        schedule = BigDecimal.valueOf(projectIds.size()).divide(BigDecimal.valueOf(totalDrillNumber), 2, RoundingMode.HALF_UP);
        return schedule;
    }


    private List<BizProjectRecord> getSatrtEndPoint(List<Long> points, Date startDate, Date endDate){
        if (points == null || points.isEmpty()){
            return new ArrayList<>();
        }
        QueryWrapper<BizProjectRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(points != null && !points.isEmpty(),BizProjectRecord::getTravePointId, points)
                .between(BizProjectRecord::getConstructTime, startDate, endDate);
        List<BizProjectRecord> list = projectRecordMapper.selectList(queryWrapper);
        return list;
    }

    private List<BizProjectRecord> getSatrtDistance(Long point,Date startDate, Date endDate, BigDecimal distance){
        if(distance.compareTo(BigDecimal.ZERO) > 0){
            QueryWrapper<BizProjectRecord> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(BizProjectRecord::getTravePointId, point)
                    .between(BizProjectRecord::getConstructTime, startDate, endDate)
                    .gt(BizProjectRecord::getConstructRange, distance);
            List<BizProjectRecord> lista = projectRecordMapper.selectList(queryWrapper);

            BizPresetPoint nextPoint =  bizTravePointService.getPointPre(point,distance.doubleValue());
            if(nextPoint == null){
                return lista;
            }
            queryWrapper.clear();
            queryWrapper.lambda().eq(BizProjectRecord::getTravePointId, nextPoint.getPointId())
                    .between(BizProjectRecord::getConstructTime, startDate, endDate)
                    .gt(BizProjectRecord::getConstructRange, distance);
            List<BizProjectRecord> listb = projectRecordMapper.selectList(queryWrapper);
            lista.addAll(listb);
            return lista;
        }
        if(distance.compareTo(BigDecimal.ZERO) < 0){
            QueryWrapper<BizProjectRecord> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(BizProjectRecord::getTravePointId, point)
                    .between(BizProjectRecord::getConstructTime, startDate, endDate)
                    .gt(BizProjectRecord::getConstructRange, distance);
            List<BizProjectRecord> lista = projectRecordMapper.selectList(queryWrapper);

            BizPresetPoint nextPoint =  bizTravePointService.getPointFront(point,distance.doubleValue());
            if(nextPoint == null){
                return lista;
            }
            queryWrapper.clear();
            queryWrapper.lambda().eq(BizProjectRecord::getTravePointId, nextPoint.getPointId())
                    .between(BizProjectRecord::getConstructTime, startDate, endDate)
                    .lt(BizProjectRecord::getConstructRange, distance)
                    .gt(BizProjectRecord::getConstructRange, 0);
            List<BizProjectRecord> listb = projectRecordMapper.selectList(queryWrapper);
            lista.addAll(listb);
            return lista;
        }
        return new ArrayList<>();
    }


    private List<BizProjectRecord> getEndDistance(Long point, Date startDate, Date endDate, BigDecimal distance){
        if(distance.compareTo(BigDecimal.ZERO) > 0){
            QueryWrapper<BizProjectRecord> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(BizProjectRecord::getTravePointId, point)
                    .between(BizProjectRecord::getConstructTime, startDate, endDate)
                    .lt(BizProjectRecord::getConstructRange, distance);
            List<BizProjectRecord> lista = projectRecordMapper.selectList(queryWrapper);

            BizPresetPoint nextPoint =  bizTravePointService.getPointPre(point,distance.doubleValue());
            if(nextPoint == null){
                return lista;
            }
            queryWrapper.clear();
            queryWrapper.lambda().eq(BizProjectRecord::getTravePointId, nextPoint.getPointId())
                    .between(BizProjectRecord::getConstructTime, startDate, endDate)
                    .lt(BizProjectRecord::getConstructRange, distance);
            List<BizProjectRecord> listb = projectRecordMapper.selectList(queryWrapper);
            lista.addAll(listb);
            return lista;
        }
        if(distance.compareTo(BigDecimal.ZERO) < 0){
            QueryWrapper<BizProjectRecord> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(BizProjectRecord::getTravePointId, point)
                    .between(BizProjectRecord::getConstructTime, startDate, endDate)
                    .lt(BizProjectRecord::getConstructRange, distance);
            List<BizProjectRecord> lista = projectRecordMapper.selectList(queryWrapper);

            BizPresetPoint nextPoint =  bizTravePointService.getPointFront(point,distance.doubleValue());
            if(nextPoint == null){
                return lista;
            }
            queryWrapper.clear();
            queryWrapper.lambda().eq(BizProjectRecord::getTravePointId, nextPoint.getPointId())
                    .between(BizProjectRecord::getConstructTime, startDate, endDate)
                    .lt(BizProjectRecord::getConstructRange, distance)
                    .gt(BizProjectRecord::getConstructRange, 0);
            List<BizProjectRecord> listb = projectRecordMapper.selectList(queryWrapper);
            lista.addAll(listb);
            return lista;
        }
        return new ArrayList<>();
    }


}