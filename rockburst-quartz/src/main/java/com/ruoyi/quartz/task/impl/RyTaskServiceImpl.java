package com.ruoyi.quartz.task.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.message.WebSocketServer;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.quartz.task.IRyTask;
import com.ruoyi.system.constant.ModelFlaskConstant;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.Entity.*;
import com.ruoyi.system.domain.aimodel.TaskStatus;
import com.ruoyi.system.domain.aimodel.TaskStatusResponse;
import com.ruoyi.system.domain.dto.RuleDTO;
import com.ruoyi.system.domain.dto.largeScreen.PlanPushDTO;
import com.ruoyi.system.domain.dto.largeScreen.SpaceAlarmPushDTO;
import com.ruoyi.system.domain.utils.SendMessageUtils;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.*;
import com.ruoyi.system.service.impl.handle.AiModelHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 定时任务调度日志信息 服务层
 * 
 * @author ruoyi
 */
@Component("ryTask")
public class RyTaskServiceImpl implements IRyTask
{
    @Resource
    PlanAlarmMapper planAlarmMapper;

    @Autowired
    PlanMapper planMapper;


    @Autowired
    ProjectWarnSchemeMapper projectWarnSchemeMapper;

    @Autowired
    BizProjectRecordMapper projectRecordMapper;

    @Autowired
    PlanAreaMapper planAreaMapper;

    @Autowired
    private ISysDictTypeService dictDataService;

    @Autowired
    IBizTravePointService bizTravePointService;

    @Resource
    private RestTemplate restTemplate;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private BizVideoMapper bizVideoMapper;

    @Autowired
    AiModelHandle aiModelHandle;

    @Autowired
    private ISysConfigService configService;

    @Resource
    private AlarmRecordMapper alarmRecordMapper;

    @Resource
    private AlarmRecordService alarmRecordService;

    @Resource
    private BizWorkfaceMapper bizWorkfaceMapper;

    @Resource
    private AlarmHandleHistoryMapper alarmHandleHistoryMapper;

    @Resource
    private AlarmHandleHistoryService alarmHandleHistoryService;

    @Resource
    private TunnelMapper tunnelMapper;

    private static final Logger log = LoggerFactory.getLogger(RyTaskServiceImpl.class);


    @Override
    public void ryMultipleParams(String s, Boolean b, Long l, Double d, Integer i) {

    }

    @Override
    public void ryParams(String params) {

    }

    @Override
    public void ryNoParams() {

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

    @Transactional
    @Override
    public void alarmProject() {
        long timestamp = DateUtil.date().getTime();

        List<PlanEntity> list = planMapper.selectList(new LambdaQueryWrapper<PlanEntity>()
                .isNotNull(PlanEntity::getProjectWarnSchemeId)
                .eq(PlanEntity::getState, ConstantsInfo.AUDITED_DICT_VALUE)
                .lt(PlanEntity::getStartTime, timestamp)
                .and(qw -> qw.isNull(PlanEntity::getAlarmCaptime)
                        .or()
                        .gt(PlanEntity::getAlarmCaptime, timestamp)));

        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        // 批量获取 projectWarnScheme
        Set<Long> schemeIds = list.stream()
                .map(PlanEntity::getProjectWarnSchemeId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<ProjectWarnSchemeEntity> schemes = projectWarnSchemeMapper.selectBatchIds(schemeIds);
        Map<Long, ProjectWarnSchemeEntity> schemeMap = schemes.stream()
                .filter(s -> !Objects.equals(s.getStatus(), ConstantsInfo.DISABLE))
                .collect(Collectors.toMap(ProjectWarnSchemeEntity::getProjectWarnSchemeId, Function.identity()));

        for (PlanEntity entity : list) {
            if (entity.getProjectWarnSchemeId() == null) {
                continue;
            }

            ProjectWarnSchemeEntity projectWarnScheme = projectWarnSchemeMapper.selectById(entity.getProjectWarnSchemeId());
            if (projectWarnScheme == null || Objects.equals(projectWarnScheme.getStatus(), ConstantsInfo.DISABLE)) {
                continue;
            }

            if (entity.getAlarmCaptime() != null && entity.getAlarmCaptime() > timestamp) {
                continue;
            }

            List<RuleDTO> rules = parseAndSortRules(projectWarnScheme.getWorkloadRule());
            BigDecimal current = calculateProgress(entity.getStartTime(), entity.getEndTime(), timestamp);

            // 找到最大满足条件的规则索引
            int matchedIndex = -1; // 初始化为 -1，表示尚未匹配到任何规则
            for (int i = 0; i < rules.size(); i++) {
                RuleDTO rule = rules.get(i);
                // 当前时间 >= 规则时间比例 → 匹配成功
                if (current.compareTo(rule.getTimeProportion()) >= 0) {
                    matchedIndex = i; // 更新为当前索引
                } else {
                    break; // 后续规则更大，无需继续遍历
                }
            }
            // 如果没有匹配到任何规则，跳过这个工程计划
            if (matchedIndex == -1) {
                continue;
            }
            // 获取匹配的规则对象
            RuleDTO matchedRule = rules.get(matchedIndex);
            // 执行统计 projectId 的逻辑
            List<PlanAreaEntity> areaEntitys = planAreaMapper.selectList(new LambdaQueryWrapper<PlanAreaEntity>()
                    .eq(PlanAreaEntity::getPlanId, entity.getPlanId()));

            Set<Long> projectIds = new HashSet<>();
            for (PlanAreaEntity areaEntity : areaEntitys) {
                List<Long> points = bizTravePointService.getInPointListNoStartEnd(
                        areaEntity.getStartTraversePointId(),
                        Double.valueOf(areaEntity.getStartDistance()),
                        areaEntity.getEndTraversePointId(),
                        Double.valueOf(areaEntity.getEndDistance())
                );

                List<BizProjectRecord> records1 = getSatrtEndPoint(points,
                        DateUtil.date(entity.getStartTime()), DateUtil.date(entity.getEndTime()));
                List<BizProjectRecord> records2 = getSatrtDistance(areaEntity.getStartTraversePointId(),
                        DateUtil.date(entity.getStartTime()), DateUtil.date(entity.getEndTime()),
                        new BigDecimal(areaEntity.getStartDistance()));
                List<BizProjectRecord> records3 = getEndDistance(areaEntity.getEndTraversePointId(),
                        DateUtil.date(entity.getStartTime()), DateUtil.date(entity.getEndTime()),
                        new BigDecimal(areaEntity.getEndDistance()));

                List<Long> ids = Stream.concat(Stream.concat(records1.stream(), records2.stream()), records3.stream())
                        .map(BizProjectRecord::getProjectId)
                        .distinct()
                        .collect(Collectors.toList());
                projectIds.addAll(ids);
            }

            // 获取计划下所有工程量
            Integer totalDrillNumber = entity.getTotalDrillNumber();
            // 计算实际完成比例
            BigDecimal actualWorkloadRatio = calculateWorkloadRatio(projectIds.size(), totalDrillNumber);
            // 判断是否满足报警条件
            if (matchedRule.getWorkloadProportion().compareTo(actualWorkloadRatio) > 0) {
                handleTriggerAlarm(entity, matchedRule, projectIds.size(), timestamp);
            } else {
                handleCloseAlarm(entity, matchedRule);
            }
        }
    }

    @Override
    public void alarmAgainPush() {
        final List<String> validHandleStatus = Arrays.asList("0", "1");

        // 合并查询，减少数据库访问次数
        List<AlarmRecordEntity> recordEntities = alarmRecordMapper.selectList(new LambdaQueryWrapper<AlarmRecordEntity>()
                .eq(AlarmRecordEntity::getAlarmType, ConstantsInfo.DRILL_SPACE_ALARM)
                .or()
                .eq(AlarmRecordEntity::getAlarmType, ConstantsInfo.QUANTITY_ALARM)
                .in(AlarmRecordEntity::getHandleStatus, validHandleStatus));

        List<PlanPushDTO> planPushDTOS = new ArrayList<>();
        List<SpaceAlarmPushDTO> spaceAlarmPushDTOS = new ArrayList<>();

        if (ListUtils.isNotNull(recordEntities)) {
            for (AlarmRecordEntity recordEntity : recordEntities) {
                String alarmType = recordEntity.getAlarmType();
                if (ConstantsInfo.DRILL_SPACE_ALARM.equals(alarmType)) {
                    SpaceAlarmPushDTO dto = new SpaceAlarmPushDTO();
                    dto.setAlarmId(recordEntity.getAlarmId());
                    dto.setAlarmType(ConstantsInfo.DRILL_SPACE_ALARM);
                    dto.setAlarmTime(recordEntity.getStartTime());
                    dto.setCurrentProjectId(recordEntity.getProjectId());
                    dto.setCurrentDrillNum(recordEntity.getCurrentDrillNum());
                    dto.setContrastDrillNum(recordEntity.getContrastDrillNum());
                    dto.setAlarmContent(recordEntity.getAlarmContent());
                    dto.setSpaced(recordEntity.getSpaced());
                    dto.setActualDistance(recordEntity.getActualDistance());

                    String workFaceName = getWorkFaceName(recordEntity.getProjectId(), "project");
                    dto.setWorkFaceName(workFaceName);
                    dto.setTunnelName(getTunnelName(recordEntity.getProjectId()));
                    spaceAlarmPushDTOS.add(dto);
                } else if (ConstantsInfo.QUANTITY_ALARM.equals(alarmType)) {
                    PlanPushDTO dto = new PlanPushDTO();
                    dto.setAlarmId(recordEntity.getAlarmId());
                    dto.setAlarmType(ConstantsInfo.QUANTITY_ALARM);
                    dto.setAlarmTime(recordEntity.getStartTime());
                    dto.setAlarmContent(recordEntity.getAlarmContent());
                    dto.setPlanId(recordEntity.getPlanId());

                    String workFaceName = getWorkFaceName(recordEntity.getPlanId(), "plan");
                    dto.setWorkFaceName(workFaceName);
                    dto.setPlanQuantity(recordEntity.getQuantityTotal());
                    dto.setActualCompleteQuantity(recordEntity.getQuantityAlarmValue());
                    planPushDTOS.add(dto);
                }
            }
        }
        // 分开处理发送逻辑，便于异常定位
        if (!spaceAlarmPushDTOS.isEmpty()) {
            try {
                String message = SendMessageUtils.sendMessage(ConstantsInfo.DRILL_SPACE_ALARM, spaceAlarmPushDTOS);
                WebSocketServer.sendInfoAll(message);
            } catch (IOException e) {
                log.error("钻孔间距WebSocket推送失败", e);
            }
        }
        if (!planPushDTOS.isEmpty()) {
            try {
                String message = SendMessageUtils.sendMessage(ConstantsInfo.QUANTITY_ALARM, planPushDTOS);
                WebSocketServer.sendInfoAll(message);
            } catch (IOException e) {
                log.error("工程量报警WebSocket推送失败", e);
            }
        }
    }


    public  BigDecimal calculateProgress(long startTime, long endTime, long currentTime) {
        long totalDuration = endTime - startTime;
        long passedDuration = currentTime - startTime;

        if (totalDuration <= 0) {
            return BigDecimal.ZERO;
        }

        // 构造 BigDecimal
        BigDecimal total = BigDecimal.valueOf(totalDuration);
        BigDecimal passed = BigDecimal.valueOf(passedDuration);

        // 百分比 = (passed / total) * 100，保留2位小数，四舍五入
        BigDecimal percent = passed.divide(total, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        // 限制范围在 0 ~ 100 之间
        if (percent.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        } else if (percent.compareTo(BigDecimal.valueOf(100)) > 0) {
            return BigDecimal.valueOf(100);
        }

        return percent.setScale(0,RoundingMode.DOWN); // 保留2位小数
    }


    @Override
    public void ai_model() {
        //查询状态
        TaskStatusResponse statusResponse =  aiModelHandle.getTaskList();
        aiModelHandle.updateTaskRedis(statusResponse);
        QueryWrapper<BizVideo> videoQueryWrapper = new QueryWrapper<>();
        videoQueryWrapper.lambda().in(BizVideo::getStatus, ModelFlaskConstant.ai_model_pending,ModelFlaskConstant.ai_model_processing);
        List<BizVideo> videos =  bizVideoMapper.selectList(videoQueryWrapper);
        String uploadUrl = configService.selectConfigByKey(ModelFlaskConstant.pre_file_url);
        for (BizVideo video : videos) {
            TaskStatus taskStatus = aiModelHandle.getRedisTaskIdStatus(video.getTaskId());
            if(taskStatus != null && StrUtil.isNotEmpty(taskStatus.getStatus()) &&  taskStatus.getStatus().equals(ModelFlaskConstant.ai_model_done)){
                SysFileInfo sysFileInfo = aiModelHandle.uploadVieoMinio(ModelFlaskConstant.bucket_name, uploadUrl+ModelFlaskConstant.static_video_url+taskStatus.getOutput_path());
                video.setAiFileUrl(sysFileInfo.getFileUrl()).setStatus(ModelFlaskConstant.ai_model_done);
                bizVideoMapper.updateById(video);
            }
        }

    }

    /**
     * 解析并排序规则
     */
    private List<RuleDTO> parseAndSortRules(String json) {
        JSONArray array = JSONUtil.parseArray(json);
        // 将规则转为列表并排序
        List<RuleDTO> rules = new ArrayList<>();
        for (Object obj : array) {
            JSONObject ruleJson = JSONUtil.parseObj(obj);
            RuleDTO rule = new RuleDTO();
            rule.setTimeProportion(new BigDecimal(ruleJson.getInt("timeProportion")));
            rule.setWorkloadProportion(new BigDecimal(ruleJson.getInt("workloadProportion")));
            rules.add(rule);
        }
        // 按 timeProportion 升序排序, 便于后续逐条匹配
        rules.sort(Comparator.comparing(RuleDTO::getTimeProportion));
        return rules;
    }

    /**
     * 处理触发报警
     */
    private void handleTriggerAlarm(PlanEntity entity, RuleDTO matchedRule, int actualCompleted, long timestamp) {
        // 1. 准备基础数据
        String workFaceName = getWorkFaceName(entity.getPlanId(), "plan");
        String planStartTime = DateUtils.getDateStrByTime(entity.getAlarmCaptime());
        String planEndTime = DateUtils.getDateStrByTime(entity.getEndTime());
        String alarmContent = String.format("%s：在%s~%s期间，计划完成总钻孔量为:%d个。 规定在%s的时间内，完成总数的:%.2f。当前实际完成:%d个，没有达到预期计划，触发报警",
                workFaceName,planStartTime, planEndTime,entity.getTotalDrillNumber(), calculateProgress(entity.getStartTime(), entity.getEndTime(), timestamp),
                matchedRule.getWorkloadProportion(), actualCompleted);

        // 2. 检查是否已存在相同报警记录
        boolean existsSameRecord = alarmRecordMapper.exists(new LambdaQueryWrapper<AlarmRecordEntity>()
                .eq(AlarmRecordEntity::getPlanId, entity.getPlanId())
                .eq(AlarmRecordEntity::getQuantityAlarmThreshold, matchedRule.getWorkloadProportion())
                .eq(AlarmRecordEntity::getQuantityAlarmValue, actualCompleted));

        if (existsSameRecord)  {
            return; // 存在相同记录，不再处理
        }
        // 查找相同 planId + quantityAlarmThreshold 的最新一条记录，并更新其状态为关闭
        AlarmRecordEntity latestRecord = alarmRecordMapper.selectOne(new LambdaQueryWrapper<AlarmRecordEntity>()
                .eq(AlarmRecordEntity::getPlanId, entity.getPlanId())
                .eq(AlarmRecordEntity::getQuantityAlarmThreshold, matchedRule.getWorkloadProportion())
                .orderByDesc(AlarmRecordEntity::getCreateTime)
                .last("LIMIT 1"));

        if (latestRecord != null && !ConstantsInfo.ALARM_END.equals(latestRecord.getAlarmStatus())) {
            // 更新上一条记录的状态为“已关闭”
            latestRecord.setAlarmStatus(ConstantsInfo.ALARM_END);
            alarmRecordMapper.updateById(latestRecord);
        }

        // 3. 构建报警记录实体
        AlarmRecordEntity alarmRecord = new AlarmRecordEntity()
                .setAlarmType(ConstantsInfo.QUANTITY_ALARM)
                .setPlanId(entity.getPlanId())
                .setQuantityTotal(entity.getTotalDrillNumber())
                .setQuantityAlarmValue(actualCompleted)
                .setQuantityAlarmThreshold(matchedRule.getWorkloadProportion())
                .setAlarmContent(alarmContent)
                .setStartTime(timestamp)
                .setCreateTime(timestamp)
                .setAlarmStatus(ConstantsInfo.ALARM_IN)
                .setHandleStatus(ConstantsInfo.UNTREATED);
        // 4. 确定并设置报警序号
        Integer maxNum = alarmRecordMapper.selectMaxNumber(entity.getPlanId(), matchedRule.getWorkloadProportion());
        alarmRecord.setNum(maxNum == null ? 1 : maxNum + 1);
        // 5. 插入新记录
        int insert = alarmRecordMapper.insert(alarmRecord);
        if (insert > 0) {
            // 6. 发送WebSocket消息
            sendWebSocketMessage(alarmRecord);
        }
    }

    /**
     * 处理关闭报警
     */
    private void handleCloseAlarm(PlanEntity entity, RuleDTO matchedRule) {
        // 当数据不符合报警触发阈值时，结束报警，更新报警状态
        final List<String> validStatuses = Arrays.asList("1", "2");
        final String alarmEndStatus = ConstantsInfo.ALARM_END;
        final long endTime = System.currentTimeMillis();

        List<AlarmRecordEntity> recordsToUpdate = alarmRecordMapper.selectList(
                new LambdaQueryWrapper<AlarmRecordEntity>()
                        .eq(AlarmRecordEntity::getPlanId, entity.getPlanId())
                        .eq(AlarmRecordEntity::getQuantityAlarmThreshold, matchedRule.getWorkloadProportion())
                        .in(AlarmRecordEntity::getAlarmStatus, validStatuses));

        List<AlarmHandleHistoryEntity> handleHistoryEntities = new ArrayList<>(recordsToUpdate.size());
        if (!CollectionUtils.isEmpty(recordsToUpdate)) {
            recordsToUpdate.forEach(record -> {
                record.setAlarmStatus(alarmEndStatus);
                record.setEndTime(endTime);
                record.setHandleStatus(ConstantsInfo.TURN_OFF_ALARM); // 将处理状态设置成“关闭报警”
                // 构建处理历史实体
                AlarmHandleHistoryEntity history = new AlarmHandleHistoryEntity();
                history.setAlarmId(record.getAlarmId());
                history.setHandlePerson(ConstantsInfo.ALARM_SYSTEM); // 设置处理人ID为“系统”
                history.setHandleTime(endTime);
                history.setOperate(ConstantsInfo.TURN_OFF_ALARM);
                history.setRemarks(ConstantsInfo.REMARKS_SYSTEM); // 设置处理备注为“系统处理”
                handleHistoryEntities.add(history);
            });
            // 保存处理历史
            if (alarmRecordService.updateBatchById(recordsToUpdate)) {
                alarmHandleHistoryService.saveBatch(handleHistoryEntities);
            }
        }
    }
    /**
     * 发送WebSocket消息
     */
    private void sendWebSocketMessage(AlarmRecordEntity alarmRecord) {
        String message = "";
        PlanPushDTO planPushDTO = new PlanPushDTO();
        planPushDTO.setAlarmId(alarmRecord.getAlarmId());
        planPushDTO.setAlarmType(alarmRecord.getAlarmType());
        planPushDTO.setAlarmTime(alarmRecord.getStartTime());
        planPushDTO.setPlanId(alarmRecord.getPlanId());
        planPushDTO.setWorkFaceName(getWorkFaceName(alarmRecord.getPlanId(), "plan"));
        planPushDTO.setPlanQuantity(alarmRecord.getQuantityTotal());
        planPushDTO.setActualCompleteQuantity(alarmRecord.getQuantityAlarmValue());
        planPushDTO.setAlarmContent(alarmRecord.getAlarmContent());

        List<PlanPushDTO> planPushDTOS = Collections.singletonList(planPushDTO);
        try {
            message = SendMessageUtils.sendMessage(ConstantsInfo.QUANTITY_ALARM, planPushDTOS);
            WebSocketServer.sendInfoAll(message);
        } catch (IOException e) {
            log.error("WebSocket消息推送失败，内容：{}", message, e);
        }
    }

    private String getWorkFaceName(Long id, String t){
        String workfaceName = "";
        Long workFaceId = null;
        if (t.equals("plan")) {
            PlanEntity planEntity = planMapper.selectOne(new LambdaQueryWrapper<PlanEntity>()
                    .eq(PlanEntity::getPlanId, id)
                    .eq(PlanEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            workFaceId = planEntity.getWorkFaceId();
        }
        if (t.equals("project")) {
            BizProjectRecord bizProjectRecord = projectRecordMapper.selectOne(new LambdaQueryWrapper<BizProjectRecord>()
                    .eq(BizProjectRecord::getProjectId, id)
                    .eq(BizProjectRecord::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            workFaceId = bizProjectRecord.getWorkfaceId();
        }
        QueryWrapper<BizWorkface> workfaceQueryWrapper = new QueryWrapper<>();
        workfaceQueryWrapper.lambda().eq(BizWorkface::getWorkfaceId, workFaceId)
                .eq(BizWorkface::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG);
        BizWorkface bizWorkface = bizWorkfaceMapper.selectOne(workfaceQueryWrapper);
        if (ObjectUtil.isNotNull(bizWorkface)) {
            workfaceName = bizWorkface.getWorkfaceName();
        }
        return workfaceName;
    }

    private String getTunnelName(Long id){
        String tunnelName = "";
        QueryWrapper<TunnelEntity> tunnelQueryWrapper = new QueryWrapper<>();
        tunnelQueryWrapper.lambda().eq(TunnelEntity::getTunnelId, id)
                .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG);
        TunnelEntity tunnelEntity = tunnelMapper.selectOne(tunnelQueryWrapper);
        if (ObjectUtil.isNotNull(tunnelEntity)) {
            tunnelName = tunnelEntity.getTunnelName();
        }
        return tunnelName;
    }

    public static BigDecimal calculateWorkloadRatio(int actualCompleted, Integer total) {
        if (total == null || total <= 0) {
            return BigDecimal.ZERO;
        }

        // 计算完成比例：(actualCompleted / total) * 100
        return new BigDecimal(actualCompleted)
                .divide(new BigDecimal(total), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.DOWN); // 保留两位小数，向下取整
    }
}
