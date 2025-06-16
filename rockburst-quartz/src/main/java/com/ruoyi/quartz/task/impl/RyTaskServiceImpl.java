package com.ruoyi.quartz.task.impl;

import cn.hutool.core.date.DateUtil;
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
import com.ruoyi.quartz.task.IRyTask;
import com.ruoyi.system.constant.ModelFlaskConstant;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.Entity.*;
import com.ruoyi.system.domain.aimodel.TaskStatus;
import com.ruoyi.system.domain.aimodel.TaskStatusResponse;
import com.ruoyi.system.domain.dto.largeScreen.PlanPushDTO;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.AlarmRecordService;
import com.ruoyi.system.service.IBizTravePointService;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysDictTypeService;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

        QueryWrapper<PlanEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .isNotNull(PlanEntity::getProjectWarnSchemeId)
                .eq(PlanEntity::getState, ConstantsInfo.AUDITED_DICT_VALUE)
                .lt(PlanEntity::getStartTime, timestamp)
                .and(qw -> qw.isNull(PlanEntity::getAlarmCaptime)
                        .or()
                        .gt(PlanEntity::getAlarmCaptime, timestamp));
        List<PlanEntity>  list = planMapper.selectList(queryWrapper);
        if(list == null || list.size() == 0){
            return;
        }
        for (PlanEntity entity : list) {
            if(entity.getProjectWarnSchemeId() == null ){
                continue;
            }
            ProjectWarnSchemeEntity projectWarnScheme =  projectWarnSchemeMapper.selectById(entity.getProjectWarnSchemeId());
            if(projectWarnScheme.getStatus() == ConstantsInfo.DISABLE){
                continue;
            }
            if( entity.getAlarmCaptime() != null && entity.getAlarmCaptime() > timestamp ){
                continue;
            }
            JSONArray array = JSONUtil.parseArray(projectWarnScheme.getWorkloadRule());
            boolean isreturn = true;
            BigDecimal  current = calculateProgress(entity.getStartTime(),entity.getEndTime(),timestamp);
            BigDecimal  workloadProportion = new BigDecimal(0);
            for (Object aoe : array) {
                JSONObject rule = JSONUtil.parseObj(aoe);
                BigDecimal  plan = new BigDecimal(rule.getInt("timeProportion"));
                workloadProportion = new BigDecimal(rule.getInt("workloadProportion"));
                if(current.compareTo(plan) != 0){
                    isreturn = false;
                }
            }
            if (isreturn) {
                // 当数据不符合报警触发阈值时，结束报警，更新报警状态
                final List<String> validStatuses = Arrays.asList("1", "2");
                final String alarmEndStatus = ConstantsInfo.ALARM_END;
                final long endTime = System.currentTimeMillis();
                List<AlarmRecordEntity> recordsToUpdate = alarmRecordMapper.selectList(
                        new LambdaQueryWrapper<AlarmRecordEntity>()
                                .eq(AlarmRecordEntity::getPlanId, entity.getPlanId())
                                .eq(AlarmRecordEntity::getQuantityAlarmThreshold, workloadProportion)
                                .in(AlarmRecordEntity::getAlarmStatus, validStatuses));
                if (!CollectionUtils.isEmpty(recordsToUpdate)) {
                    recordsToUpdate.forEach(record -> {
                        record.setAlarmStatus(alarmEndStatus);
                        record.setEndTime(endTime);
                    });
                    boolean updateSuccess = alarmRecordService.updateBatchById(recordsToUpdate);
                }
                continue;
            }
            QueryWrapper<PlanAreaEntity> areaEntityQueryWrapper = new QueryWrapper<>();
            areaEntityQueryWrapper.lambda().eq(PlanAreaEntity::getPlanId,entity.getPlanId());
            List<PlanAreaEntity> areaEntitys = planAreaMapper.selectList(areaEntityQueryWrapper);
            List<Long> projectIds = new ArrayList<>();
            for (PlanAreaEntity areaEntity : areaEntitys) {
                List<Long> points = bizTravePointService.getInPointListNoStartEnd(areaEntity.getStartTraversePointId(),Double.valueOf(areaEntity.getStartDistance()),areaEntity.getEndTraversePointId(),Double.valueOf(areaEntity.getEndDistance()));
                List<BizProjectRecord> records1 = getSatrtEndPoint(points,DateUtil.date(entity.getStartTime()),DateUtil.date(entity.getEndTime()));
                List<BizProjectRecord> records2 = getSatrtDistance(areaEntity.getStartTraversePointId(),DateUtil.date(entity.getStartTime()),DateUtil.date(entity.getEndTime()),new BigDecimal(areaEntity.getStartDistance()));
                List<BizProjectRecord> records3 = getEndDistance(areaEntity.getEndTraversePointId(),DateUtil.date(entity.getStartTime()),DateUtil.date(entity.getEndTime()),new BigDecimal(areaEntity.getEndDistance()));
                records1.addAll(records2);
                records1.addAll(records3);
                records1 = records1.stream()
                        .collect(Collectors.collectingAndThen(
                                Collectors.toMap(BizProjectRecord::getProjectId, r -> r, (r1, r2) -> r1),
                                map -> new ArrayList<>(map.values())
                        ));
                projectIds.addAll(records1.stream().map(BizProjectRecord::getProjectId).collect(Collectors.toList()));
            }
            projectIds = projectIds.stream()
                    .distinct()
                    .collect(Collectors.toList());

            // 报警数据插入 alarm_record 表
            // 1. 准备基础数据
            long currentTime = System.currentTimeMillis();
            int actualCompleted = projectIds.size();
            String alarmContent = String.format("总计划钻孔量为:%d 规定%s的时间， 计划完成总数的:%.2f ，实际完成:%d个，没有达到预期计划，触发报警",
                    entity.getTotalDrillNumber(), current, workloadProportion, actualCompleted);

            // 2. 检查是否已存在相同报警记录
            boolean existsSameRecord = alarmRecordMapper.exists(new LambdaQueryWrapper<AlarmRecordEntity>()
                    .eq(AlarmRecordEntity::getPlanId, entity.getPlanId())
                    .eq(AlarmRecordEntity::getQuantityAlarmThreshold, workloadProportion)
                    .eq(AlarmRecordEntity::getQuantityAlarmValue, actualCompleted));
            if (existsSameRecord) {
                return; // 存在相同记录，不再处理
            }
            // 3. 构建报警记录实体
            AlarmRecordEntity alarmRecord = new AlarmRecordEntity()
                    .setAlarmType(ConstantsInfo.QUANTITY_ALARM)
                    .setPlanId(entity.getPlanId())
                    .setQuantityAlarmValue(actualCompleted)
                    .setQuantityAlarmThreshold(workloadProportion)
                    .setAlarmContent(alarmContent)
                    .setStartTime(currentTime)
                    .setCreateTime(currentTime)
                    .setAlarmStatus(ConstantsInfo.ALARM_IN);
            // 4. 确定并设置报警序号
            Integer maxNum = alarmRecordMapper.selectMaxNumber(entity.getPlanId(), workloadProportion);
            alarmRecord.setNum(maxNum == null ? 1 : maxNum + 1);
            // 5. 插入新记录
            alarmRecordMapper.insert(alarmRecord);
            // 6. 发送WebSocket消息
            PlanPushDTO planPushDTO = new PlanPushDTO();
            planPushDTO.setAlarmType(ConstantsInfo.QUANTITY_ALARM);
            planPushDTO.setAlarmTime(System.currentTimeMillis());
            planPushDTO.setPlanId(entity.getPlanId());
            planPushDTO.setPlanStartTime(entity.getStartTime());
            planPushDTO.setPlanEndTime(entity.getEndTime());
            planPushDTO.setWorkFaceId(entity.getWorkFaceId());
            BizWorkface bizWorkface = bizWorkfaceMapper.selectOne(new LambdaQueryWrapper<BizWorkface>()
                    .eq(BizWorkface::getWorkfaceId, entity.getWorkFaceId())
                    .eq(BizWorkface::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            String workFaceName = "";
            if (bizWorkface != null) {
                workFaceName = bizWorkface.getWorkfaceName();
            }
            planPushDTO.setWorkFaceName(workFaceName);
            planPushDTO.setPlanQuantity(entity.getTotalDrillNumber());
            planPushDTO.setActualCompleteQuantity(actualCompleted);
            String message = JSON.toJSONString(planPushDTO); // 可根据需求添加 SerializerFeature 配置
            try {
                WebSocketServer.sendInfoAll(message);
            } catch (IOException e) {
                log.error("WebSocket消息推送失败，内容：{}", message, e);
            }

            PlanAlarm planAlarm = new PlanAlarm();
            planAlarm.setPlanId(entity.getPlanId())
                    .setStatus("报警")
                    .setDetail(current+"计划完成"+workloadProportion+"实际完成"+projectIds.size());
            QueryWrapper<PlanAlarm> planAlarmQueryWrapper = new QueryWrapper<>();
            planAlarmQueryWrapper.lambda().eq(PlanAlarm::getPlanId,entity.getPlanId());
            List<PlanAlarm> planAlarms = planAlarmMapper.selectList(planAlarmQueryWrapper);
            if(planAlarms == null || planAlarms.size() == 0){
                planAlarmMapper.insert(planAlarm);
            }else {
                planAlarm.setAlarmId(planAlarms.get(0).getAlarmId());
                planAlarmMapper.updateById(planAlarm);
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
}
