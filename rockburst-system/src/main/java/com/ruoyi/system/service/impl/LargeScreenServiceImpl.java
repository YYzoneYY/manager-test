package com.ruoyi.system.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.BizPresetPoint;
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.domain.BizVideo;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.*;
import com.ruoyi.system.domain.dto.TunnelChoiceListDTO;
import com.ruoyi.system.domain.dto.largeScreen.*;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.IBizTravePointService;
import com.ruoyi.system.service.LargeScreenService;
import com.ruoyi.system.service.PlanAreaService;
import com.ruoyi.system.service.TunnelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Resource
    private TunnelService tunnelService;

    @Resource
    private BizVideoMapper bizVideoMapper;

    @Resource
    private ConstructionUnitMapper constructionUnitMapper;

    @Resource
    private AlarmRecordMapper alarmRecordMapper;

    @Resource
    private AlarmHandleHistoryMapper alarmHandleHistoryMapper;


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
    public List<ProjectTypeDTO> obtainProjectType(Date startTime, Date endTime) {
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
                    projectTypeDTO.setDrillTypeFmt(dictLabelMap.getOrDefault("dict_label", ""));
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

    @Override
    public DataDTO obtainUrl(Long projectId) {
        DataDTO dataDTO = new DataDTO();
        List<BizVideo> bizVideos = bizVideoMapper.selectList(new LambdaQueryWrapper<BizVideo>()
                .eq(BizVideo::getProjectId, projectId)
                .eq(BizVideo::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));

        List<String> urls = new ArrayList<>();
        List<String> aiUrls = new ArrayList<>();

        if (bizVideos != null && !bizVideos.isEmpty()) {
            for (BizVideo video : bizVideos) {
                urls.add(video.getFileUrl());
                aiUrls.add(video.getAiFileUrl());
            }
        }

        dataDTO.setUrls(urls);
        dataDTO.setAIUrls(aiUrls);
        return dataDTO;
    }

    @Override
    public List<AlarmRecordDTO> obtainAlarmRecord(String alarmType, Long startTime, Long endTime) {
        List<AlarmRecordDTO> alarmRecordDTOS = alarmRecordMapper.selectAlarmRecord(alarmType, startTime, endTime);
        if (ListUtils.isNotNull(alarmRecordDTOS)) {
            for (AlarmRecordDTO alarmRecordDTO : alarmRecordDTOS) {
                String at = alarmRecordDTO.getAlarmType() != null
                        ? obtainDicLabel(ConstantsInfo.ALARM_TYPE, alarmRecordDTO.getAlarmType())
                        : null;
                alarmRecordDTO.setAlarmTypeFmt(at);

                String as = alarmRecordDTO.getAlarmStatus() != null
                        ? obtainDicLabel(ConstantsInfo.ALARM_STATUS, alarmRecordDTO.getAlarmStatus())
                        : null;
                alarmRecordDTO.setAlarmStatusFmt(as);
            }
        }
        return alarmRecordDTOS;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean alarmHandle(HandleDTO handleDTO) {
        if (ObjectUtil.isNull(handleDTO)) {
            throw new IllegalArgumentException("参数错误：参数不能为空！");
        }
        if (ObjectUtil.isNull(handleDTO.getAlarmId())) {
            throw new IllegalArgumentException("参数错误：alarmId 不能为空！");
        }
        if (StrUtil.isBlank(handleDTO.getHandleStatus())) {
            throw new IllegalArgumentException("参数错误：handleStatus 不能为空！");
        }
        if (handleDTO.getHandleStatus().equals(ConstantsInfo.TURN_OFF_ALARM)) {
            if (ObjectUtil.isNull(handleDTO.getRemarks())) {
                throw new IllegalArgumentException("参数错误：remarks 不能为空！");
            }
        }

        Long userId = SecurityUtils.getUserId();
        if (ObjectUtil.isNull(userId)) {
            throw new IllegalArgumentException("当前用户未登录，无法进行操作");
        }

        LambdaUpdateWrapper<AlarmRecordEntity> wrapper = new LambdaUpdateWrapper<AlarmRecordEntity>()
                .eq(AlarmRecordEntity::getAlarmId, handleDTO.getAlarmId());

        AlarmRecordEntity updateEntity = new AlarmRecordEntity()
                .setHandleStatus(handleDTO.getHandleStatus());
        if (handleDTO.getHandleStatus().equals(ConstantsInfo.TURN_OFF_ALARM)) {
            updateEntity.setAlarmStatus(ConstantsInfo.ALARM_END);
        }

        int update = alarmRecordMapper.update(updateEntity, wrapper);
        if (update <= 0) {
            return false;
        }

        AlarmHandleHistoryEntity historyEntity = new AlarmHandleHistoryEntity()
                .setAlarmId(handleDTO.getAlarmId())
                .setHandlePerson(userId)
                .setHandleTime(System.currentTimeMillis())
                .setOperate(handleDTO.getHandleStatus())
                .setRemarks(handleDTO.getRemarks());

        int insert = alarmHandleHistoryMapper.insert(historyEntity);
        if (insert <= 0) {
            throw new RuntimeException("插入告警处理历史记录失败");
        }

        return true;
    }



    @Override
    public List<SimpleTreeDTO> obtainProjectTree() {
        List<ReturnTreeDTO> returnTreeDTOS = new ArrayList<>();
        List<BizWorkface> bizWorkFaces = bizWorkfaceMapper.selectList(new LambdaQueryWrapper<BizWorkface>()
                .eq(BizWorkface::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));

        if (CollectionUtils.isEmpty(bizWorkFaces)) {
            return Collections.emptyList();
        }

        // 获取所有 workFaceId 的 tunnelChoiceList 并扁平化为 map
        Map<Long, List<TunnelChoiceListDTO>> tunnelMap = new HashMap<>();
        for (BizWorkface bizWorkface : bizWorkFaces) {
            List<TunnelChoiceListDTO> tunnels = tunnelService.getTunnelChoiceList(bizWorkface.getWorkfaceId());
            tunnelMap.put(bizWorkface.getWorkfaceId(), tunnels);
        }

        // 所有 tunnelId 收集用于批量查询
        List<Long> allTunnelIds = tunnelMap.values().stream()
                .flatMap(List::stream)
                .map(TunnelChoiceListDTO::getValue)
                .distinct().collect(Collectors.toList());

        // 批量查询所有 middleDTOS
        Map<Long, List<MiddleDTO>> tunnelToMiddleMap = new HashMap<>();
        if (!allTunnelIds.isEmpty()) {
            List<MiddleDTO> allMiddleDTOS = bizProjectRecordMapper.queryProjectCountBatch(allTunnelIds);
            for (MiddleDTO dto : allMiddleDTOS) {
                tunnelToMiddleMap.computeIfAbsent(dto.getTunnelId(), k -> new ArrayList<>()).add(dto);
            }
        }

        // 构建 ReturnTreeDTO 结构
        for (BizWorkface bizWorkface : bizWorkFaces) {
            ReturnTreeDTO returnTreeDTO = new ReturnTreeDTO();
            returnTreeDTO.setWorkFaceId(bizWorkface.getWorkfaceId());
            returnTreeDTO.setWorkFaceName(bizWorkface.getWorkfaceName());

            List<TunnelChoiceListDTO> tunnelChoiceList = tunnelMap.getOrDefault(bizWorkface.getWorkfaceId(), Collections.emptyList());
            List<TunnelReturnDTO> tunnelReturnDTOS = new ArrayList<>();

            for (TunnelChoiceListDTO tunnelChoiceListDTO : tunnelChoiceList) {
                TunnelReturnDTO tunnelReturnDTO = new TunnelReturnDTO();
                tunnelReturnDTO.setTunnelId(tunnelChoiceListDTO.getValue());
                tunnelReturnDTO.setTunnelName(tunnelChoiceListDTO.getLabel());

                List<MiddleDTO> middleDTOS = tunnelToMiddleMap.getOrDefault(tunnelChoiceListDTO.getValue(), Collections.emptyList());

                List<ProjectDataDTO> miningProjects = new ArrayList<>();
                List<ProjectDataDTO> excavationProjects = new ArrayList<>();

                if (CollectionUtils.isNotEmpty(middleDTOS)) {
                    // 收集所有 projectIds
                    Set<Long> allProjectIds = middleDTOS.stream()
                            .map(MiddleDTO::getProjectIds)
                            .flatMap(List::stream)
                            .collect(Collectors.toSet());

                    Map<Long, List<MiddleDTO>> projectIdToMiddleMap = new HashMap<>();
                    for (MiddleDTO middleDTO : middleDTOS) {
                        for (Long projectId : middleDTO.getProjectIds()) {
                            projectIdToMiddleMap.computeIfAbsent(projectId, k -> new ArrayList<>()).add(middleDTO);
                        }
                    }

                    List<DataDTO> dataDTOS = fetchDataDTOsByProjectIds(new ArrayList<>(allProjectIds));
                    Map<Long, DataDTO> dataDTOMap = dataDTOS.stream()
                            .collect(Collectors.toMap(DataDTO::getProjectId, d -> d));

                    for (MiddleDTO middleDTO : middleDTOS) {
                        String constructType = middleDTO.getConstructType();
                        ProjectDataDTO projectDataDTO = new ProjectDataDTO();
                        projectDataDTO.setProjectType(constructType);

                        List<DataDTO> filteredData = middleDTO.getProjectIds().stream()
                                .map(dataDTOMap::get)
                                .filter(Objects::nonNull).collect(Collectors.toList());

                        projectDataDTO.setData(filteredData);

                        if (constructType.equals(ConstantsInfo.STOPE)) {
                            miningProjects.add(projectDataDTO);
                        } else if (constructType.equals(ConstantsInfo.TUNNELING)) {
                            excavationProjects.add(projectDataDTO);
                        }
                    }
                }

                tunnelReturnDTO.setMiningProjects(miningProjects);
                tunnelReturnDTO.setExcavationProjects(excavationProjects);
                tunnelReturnDTOS.add(tunnelReturnDTO);
            }

            returnTreeDTO.setTunnelReturnDTOS(tunnelReturnDTOS);
            returnTreeDTOS.add(returnTreeDTO);
        }

        return buildUnifiedSimpleTreeStructure(returnTreeDTOS);
    }

    private List<DataDTO> fetchDataDTOsByProjectIds(List<Long> projectIds) {
        if (CollectionUtils.isEmpty(projectIds)) {
            return Collections.emptyList();
        }

        List<DataDTO> dataDTOS = new ArrayList<>();

        // 批量查询 BizProjectRecord
        List<BizProjectRecord> records = bizProjectRecordMapper.selectList(new LambdaQueryWrapper<BizProjectRecord>()
                .in(BizProjectRecord::getProjectId, projectIds)
                .eq(BizProjectRecord::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));

        Map<Long, String> drillNumMap = new HashMap<>();
        Map<Long, Date> constructTimeMap = new HashMap<>();

        for (BizProjectRecord record : records) {
            Long projectId = record.getProjectId();
            drillNumMap.put(projectId, record.getDrillNum());
            constructTimeMap.put(projectId, record.getConstructTime());
        }

        // 批量查询 BizVideo
        List<BizVideo> videos = bizVideoMapper.selectList(new LambdaQueryWrapper<BizVideo>()
                .in(BizVideo::getProjectId, projectIds)
                .eq(BizVideo::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));

        Map<Long, List<String>> urlMap = new HashMap<>();
        Map<Long, List<String>> aiUrlMap = new HashMap<>();

        for (BizVideo video : videos) {
            Long projectId = video.getProjectId();
            urlMap.computeIfAbsent(projectId, k -> new ArrayList<>()).add(video.getFileUrl());
            aiUrlMap.computeIfAbsent(projectId, k -> new ArrayList<>()).add(video.getAiFileUrl());
        }

        for (Long projectId : projectIds) {
            DataDTO dataDTO = new DataDTO();
            dataDTO.setProjectId(projectId);
            dataDTO.setDrillNum(drillNumMap.getOrDefault(projectId, "0"));
            dataDTO.setConstructTime(constructTimeMap.getOrDefault(projectId, null));
            dataDTO.setUrls(urlMap.getOrDefault(projectId, Collections.emptyList()));
            dataDTO.setAIUrls(aiUrlMap.getOrDefault(projectId, Collections.emptyList()));
            dataDTOS.add(dataDTO);
        }

        return dataDTOS;
    }

    private List<SimpleTreeDTO> buildUnifiedSimpleTreeStructure(List<ReturnTreeDTO> returnTreeDTOS) {
        List<SimpleTreeDTO> result = new ArrayList<>();

        for (ReturnTreeDTO returnTreeDTO : returnTreeDTOS) {
            SimpleTreeDTO workFaceNode = new SimpleTreeDTO(
                    returnTreeDTO.getWorkFaceName(),
                    returnTreeDTO.getWorkFaceId()
            );

            for (TunnelReturnDTO tunnelReturnDTO : returnTreeDTO.getTunnelReturnDTOS()) {
                SimpleTreeDTO tunnelNode = new SimpleTreeDTO(
                        tunnelReturnDTO.getTunnelName(),
                        tunnelReturnDTO.getTunnelId()
                );

                // 始终保留“回采”和“掘进”节点，即使它们的 children 为空
                processProjectType(tunnelReturnDTO.getMiningProjects(), tunnelNode, "回采", ConstantsInfo.STOPE);
                processProjectType(tunnelReturnDTO.getExcavationProjects(), tunnelNode, "掘进", ConstantsInfo.TUNNELING);

                workFaceNode.getChildren().add(tunnelNode); // 始终添加 tunnelNode
            }

            if (!workFaceNode.getChildren().isEmpty()) {
                result.add(workFaceNode);
            }
        }

        return result;
    }

    private void processProjectType(List<ProjectDataDTO> projects, SimpleTreeDTO parentNode, String typeName, String typeValue) {
        SimpleTreeDTO projectTypeNode = new SimpleTreeDTO(typeName, typeValue);

        if (CollectionUtils.isNotEmpty(projects)) {
            for (ProjectDataDTO project : projects) {
                for (DataDTO dataDTO : project.getData()) {
                    String dateStr = "";
                    if (dataDTO.getConstructTime() != null) {
                        dateStr = DateUtil.format(dataDTO.getConstructTime(), "yyyy-MM-dd");
                    }
                    String label = String.format("%s, 编号: %s", dateStr, dataDTO.getDrillNum());
                    SimpleTreeDTO projectNode = new SimpleTreeDTO(label, dataDTO.getProjectId());
                    projectTypeNode.getChildren().add(projectNode);
                }
            }
        }

        parentNode.getChildren().add(projectTypeNode); // 始终添加该类型节点
    }


    private List<ProjectDTO> enrichProjectDTOsWithConstructionSite(List<ProjectDTO> projectDTOS) {
        if (CollectionUtils.isEmpty(projectDTOS)) {
            return projectDTOS;
        }

        List<Long> tunnelIds = new ArrayList<>();
        List<Long> workFaceIds = new ArrayList<>();

        for (ProjectDTO projectDTO : projectDTOS) {
            String constructType = projectDTO.getConstructType();
            if (ConstantsInfo.TUNNELING.equals(constructType)) {
                tunnelIds.add(projectDTO.getTunnelId());
            } else if (ConstantsInfo.STOPE.equals(constructType)) {
                workFaceIds.add(projectDTO.getWorkFaceId());
            }
            ConstructionUnitEntity constructionUnitEntity = constructionUnitMapper.selectOne(new LambdaQueryWrapper<ConstructionUnitEntity>()
                    .eq(ConstructionUnitEntity::getConstructionUnitId, projectDTO.getConstructUnitId())
                    .eq(ConstructionUnitEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (constructionUnitEntity != null) {
                projectDTO.setConstructUnitFmt(constructionUnitEntity.getConstructionUnitName());
            }
            String drillType = obtainDicLabel(ConstantsInfo.DRILL_TYPE_DICT_TYPE, projectDTO.getDrillType());
            projectDTO.setDrillTypeFmt(drillType);
            projectDTO.setConstructTypeFmt(obtainDicLabel(ConstantsInfo.TYPE_DICT_TYPE, projectDTO.getConstructType()));
        }

        Map<Long, String> tunnelMap = buildTunnelMap(tunnelIds);
        Map<Long, String> workfaceMap = buildWorkfaceMap(workFaceIds);

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

    private Map<Long, String> buildTunnelMap(List<Long> tunnelIds) {
        if (CollectionUtils.isEmpty(tunnelIds)) {
            return Collections.emptyMap();
        }

        List<TunnelEntity> tunnels = tunnelMapper.selectList(new LambdaQueryWrapper<TunnelEntity>()
                .in(TunnelEntity::getTunnelId, tunnelIds)
                .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));

        return tunnels.stream()
                .collect(Collectors.toMap(
                        TunnelEntity::getTunnelId,
                        TunnelEntity::getTunnelName,
                        (existing, replacement) -> existing));
    }

    private Map<Long, String> buildWorkfaceMap(List<Long> workFaceIds) {
        if (CollectionUtils.isEmpty(workFaceIds)) {
            return Collections.emptyMap();
        }

        List<BizWorkface> workfaces = bizWorkfaceMapper.selectList(new LambdaQueryWrapper<BizWorkface>()
                .in(BizWorkface::getWorkfaceId, workFaceIds)
                .eq(BizWorkface::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));

        return workfaces.stream()
                .collect(Collectors.toMap(
                        BizWorkface::getWorkfaceId,
                        BizWorkface::getWorkfaceName,
                        (existing, replacement) -> existing));
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

    /**
     * 获取字典标签
     */
    private String obtainDicLabel(String dicType, String dicValue) {
        String label = "";
        label = sysDictDataMapper.selectDictLabel(dicType, dicValue);
        return label == null ? "" : label;
    }

}