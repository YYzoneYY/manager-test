package com.ruoyi.mq.client;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.system.EsMapper.MeasureActualMapper;
import com.ruoyi.system.EsMapper.WarnMessageMapper;
import com.ruoyi.system.domain.Entity.WarnSchemeSeparateEntity;
import com.ruoyi.system.domain.EsEntity.MeasureActualEntity;
import com.ruoyi.system.domain.EsEntity.WarnMessageEntity;
import com.ruoyi.system.domain.dto.GrowthRateConfigDTO;
import com.ruoyi.system.domain.dto.IncrementConfigDTO;
import com.ruoyi.system.domain.dto.ThresholdConfigDTO;
import com.ruoyi.system.domain.dto.WarnSchemeDTO;
import com.ruoyi.system.domain.utils.ObtainDateUtils;
import com.ruoyi.system.domain.utils.ObtainWarnSchemeUtils;
import com.ruoyi.system.mapper.WarnSchemeMapper;
import com.ruoyi.system.mapper.WarnSchemeSeparateMapper;
import lombok.extern.slf4j.Slf4j;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EsBatchConsumer implements InitializingBean, DisposableBean { // 1. 实现生命周期接口

    @Resource
    private MeasureActualMapper measureActualMapper;

    @Resource
    private WarnMessageMapper warnMessageMapper;

    @Resource
    private WarnSchemeSeparateMapper warnSchemeSeparateMapper;

    @Resource
    private WarnSchemeMapper warnSchemeMapper;

    private final BlockingQueue<JSONObject> messageQueue = new LinkedBlockingQueue<>(10000);

    // 2. 创建一个线程来执行消费任务
    private Thread consumerThread;

    // 3. 创建一个volatile标志位来控制线程的运行状态
    private volatile boolean running = true;

    /**
     * 将消息放入缓冲队列 (此方法保持不变)
     */
    public boolean offer(JSONObject messageJson) {
        if (!running) {
            log.warn("消费者线程已停止，无法添加新消息。");
            return false;
        }
        boolean success = messageQueue.offer(messageJson);
        if (!success) {
            log.warn("ES消费缓冲队列已满，消息可能被丢弃！Content: {}", messageJson.toString());
        }
        return success;
    }

    /**
     * Bean初始化时，启动消费者线程 (替代@Scheduled)
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("正在初始化ES批量消费者线程...");
        this.consumerThread = new Thread(this::consumeAndBulkInsertLoop, "es-batch-consumer-thread");
        // 设置为守护线程，这样即使主程序退出，它也会随之退出
        this.consumerThread.setDaemon(true);
        this.consumerThread.start();
        log.info("ES批量消费者线程已成功启动。");
    }

    /**
     * 核心消费逻辑，现在是一个永久循环 (替代@Scheduled方法体)
     */
    private void consumeAndBulkInsertLoop() {
        while (running) {
            try {
                int batchSize = 500;
                List<JSONObject> jsonList = new ArrayList<>(batchSize);

                // 从队列中批量取出数据
                // 使用带超时的poll，避免在队列为空时CPU空转
                JSONObject firstMessage = messageQueue.poll(1, TimeUnit.SECONDS);
                if (firstMessage != null) {
                    jsonList.add(firstMessage);
                    // 尝试取出更多数据，填满一个批次
                    messageQueue.drainTo(jsonList, batchSize - 1);
                } else {
                    // 如果1秒内没有新消息，继续下一次循环
                    continue;
                }

                if (jsonList.isEmpty()) {
                    continue;
                }

                log.info("开始批量处理 {} 条数据到ES...", jsonList.size());

                // b. 转换并批量插入
                List<MeasureActualEntity> esEntities = new ArrayList<>();
                for (JSONObject json : jsonList) {
                    MeasureActualEntity entity = new MeasureActualEntity();
                    String tag = json.getStr("tag");
                    entity.setMineId(2L);
                    entity.setMeasureNum(json.getStr("monitoringCode"));
                    entity.setSensorType(json.getStr("sensorType"));
                    entity.setSensorLocation(json.getStr("sensorLocation"));
                    entity.setSensorStatus(json.getStr("monitoringStatus"));
                    entity.setDataTime(json.getLong("dataUploadTime"));
                    if (tag.equals("ZKSS") || tag.equals("MGSS") || tag.equals("WYSS") || tag.equals("ZJSS")) {
                        entity.setMonitoringValue(json.getBigDecimal("monitoringValue"));
                        entity.setSensorNum(json.getStr("sensorCode"));
                    } else if (tag.equals("LCSS")) {
                        entity.setValueShallow(json.getBigDecimal("valueShallow"));
                        entity.setValueDeep(json.getBigDecimal("valueSecond"));
                    } else if (tag.equals("DCSS")) {
                        entity.setEleMaxValue(json.getBigDecimal("electromagnetismMaxValue"));
                        entity.setElePulse(json.getBigDecimal("electromMagneticPulse"));
                    }
                    esEntities.add(entity);

                    LambdaEsQueryWrapper<WarnMessageEntity> queryWrapper = new LambdaEsQueryWrapper<>();
                    queryWrapper.eq(WarnMessageEntity::getMeasureNum, json.getStr("monitoringCode"))
                            .eq(WarnMessageEntity::getSensorType, json.getStr("sensorType"))
                            .eq(WarnMessageEntity::getMineId, 2L)
                            .eq(WarnMessageEntity::getWarnStatus,"1");
                    List<WarnMessageEntity> warnMessageEntities = warnMessageMapper.selectList(queryWrapper);
                    if (!warnMessageEntities.isEmpty()) {

                    }


                }
                Integer insertBatch = measureActualMapper.insertBatch(esEntities);
                if (insertBatch > 0) {
                    log.info("成功批量插入 {} 条数据到ES。", insertBatch);
                } else {
                    log.error("批量插入ES失败！数据量: {}", esEntities.size());
                }

            } catch (InterruptedException e) {
                // 当线程被中断时，恢复中断状态并退出循环
                log.info("ES消费者线程被中断，准备退出。");
                Thread.currentThread().interrupt();
                break; // 退出while循环
            } catch (Exception e) {
                // 捕获其他所有异常，防止线程因意外错误而终止
                log.error("ES批量消费任务发生未知异常，将继续运行。", e);
                // 等待一小段时间，避免因连续报错而刷屏
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        log.info("ES批量消费者线程已停止。");
    }

    /**
     * Bean销毁时，优雅地停止消费者线程
     */
    @Override
    public void destroy() throws Exception {
        log.info("正在停止ES批量消费者线程...");
        this.running = false; // 1. 设置停止标志
        if (consumerThread != null) {
            consumerThread.interrupt(); // 中断线程，使其从poll的等待中唤醒
        }
        log.info("已发送停止信号到ES批量消费者线程。");
    }


    private int updateWarnMessage(JSONObject json) {
        String tag = json.getStr("tag");
        String monitoringCode = json.getStr("monitoringCode");
        String sensorType = json.getStr("sensorType");
        Long dataUploadTime = json.getLong("dataUploadTime");

        // 查找当前正在预警的数据
        LambdaEsQueryWrapper<WarnMessageEntity> queryWrapper = new LambdaEsQueryWrapper<>();
        queryWrapper.eq(WarnMessageEntity::getMeasureNum, monitoringCode)
                .eq(WarnMessageEntity::getSensorType, sensorType)
                .eq(WarnMessageEntity::getMineId, 2L)
                .eq(WarnMessageEntity::getWarnStatus, ConstantsInfo.WARNING); // 预警中状态
        List<WarnMessageEntity> warnMessageEntities = warnMessageMapper.selectList(queryWrapper);

        if (warnMessageEntities.isEmpty()) {
            // 没有正在预警的数据，直接返回
            return 0;
        }

        // 根据tag类型判断是否需要结束预警
        if (tag.equals("LCSS")) {
            // 处理顶板离层位移传感器(深基点和浅基点)
            BigDecimal valueSecond = json.getBigDecimal("valueSecond");
            BigDecimal valueShallow = json.getBigDecimal("valueShallow");

            // 检查深基点和浅基点是否还需要预警
            checkAndEndWarning(warnMessageEntities, monitoringCode, sensorType, dataUploadTime,
                    valueSecond, valueShallow, ConstantsInfo.DEEP, ConstantsInfo.SHALLOW, "1", "2");

        } else if (tag.equals("DCSS")) {
            // 处理电磁辐射传感器(强度和脉冲)
            BigDecimal eleMaxValue = json.getBigDecimal("electromagnetismMaxValue");
            BigDecimal elePulseValue = json.getBigDecimal("electromMagneticPulse");

            // 检查电磁强度和电磁脉冲是否还需要预警
            checkAndEndWarning(warnMessageEntities, monitoringCode, sensorType, dataUploadTime,
                    eleMaxValue, elePulseValue, ConstantsInfo.INTENSITY, ConstantsInfo.PULSE, "3", "4");

        } else {
            // 处理其他单值传感器类型
            BigDecimal monitoringValue = json.getBigDecimal("monitoringValue");
            checkAndEndSingleWarning(warnMessageEntities, monitoringCode, sensorType, dataUploadTime, monitoringValue, tag);
        }

        return warnMessageEntities.size();
    }

    /**
     * 检查并结束双值类型的预警
     */
    private void checkAndEndWarning(List<WarnMessageEntity> warnEntities, String monitoringCode,
                                    String sensorType, Long dataUploadTime, BigDecimal firstValue, BigDecimal secondValue,
                                    String firstTag, String secondTag, String firstMark, String secondMark) {

        Long twentyFourHoursTime = ObtainDateUtils.getTwentyFourHoursTime(dataUploadTime);

        // 检查第一个值是否还需要预警
        checkSingleValueWarning(warnEntities, monitoringCode, sensorType, dataUploadTime,
                firstValue, twentyFourHoursTime, firstTag, firstMark);

        // 检查第二个值是否还需要预警
        checkSingleValueWarning(warnEntities, monitoringCode, sensorType, dataUploadTime,
                secondValue, twentyFourHoursTime, secondTag, secondMark);
    }

    /**
     * 检查并结束单值类型的预警
     */
    private void checkAndEndSingleWarning(List<WarnMessageEntity> warnEntities, String monitoringCode,
                                          String sensorType, Long dataUploadTime, BigDecimal monitoringValue, String tag) {

        Long twentyFourHoursTime = ObtainDateUtils.getTwentyFourHoursTime(dataUploadTime);

        // 根据tag和sensorType确定预警类型标记
        String warnTag = getWarnTagBySensorType(tag, sensorType);

        checkSingleValueWarning(warnEntities, monitoringCode, sensorType, dataUploadTime,
                monitoringValue, twentyFourHoursTime, warnTag, "5");
    }

    /**
     * 检查单个值是否还需要预警，不需要则结束预警
     */
    private void checkSingleValueWarning(List<WarnMessageEntity> warnEntities, String monitoringCode,
                                         String sensorType, Long dataUploadTime, BigDecimal currentValue,
                                         Long twentyFourHoursTime, String tag, String mark) {

        // 获取预警方案
        WarnSchemeDTO warnSchemeDTO = ObtainWarnSchemeUtils.getObtainWarnSchemeT(
                monitoringCode, sensorType, tag, warnSchemeMapper, warnSchemeSeparateMapper);

        List<ThresholdConfigDTO> thresholdDTOS = warnSchemeDTO.getThresholdConfigDTOS();
        List<IncrementConfigDTO> incrementDTOS = warnSchemeDTO.getIncrementConfigDTOS();
        List<GrowthRateConfigDTO> growthRateDTOS = warnSchemeDTO.getGrowthRateConfigDTOS();

        BigDecimal minValue = obtainValue(monitoringCode, sensorType, twentyFourHoursTime, dataUploadTime, mark);
        BigDecimal avgValue = minValue.divide(BigDecimal.valueOf(24), 2, RoundingMode.HALF_UP);

        // 判断三种预警类型是否还满足条件
        String thresholdWarn = isWarnGeneric(thresholdDTOS, currentValue);
        String incrementWarn = isWarnGeneric(incrementDTOS, minValue);
        String growthWarn = isWarnGeneric(growthRateDTOS, avgValue);

        // 遍历所有正在预警的实体，检查是否需要结束预警
        for (WarnMessageEntity entity : warnEntities) {
            // 匹配相同测点编码、传感器类型和标签的预警
            if (monitoringCode.equals(entity.getMeasureNum())
                    && sensorType.equals(entity.getSensorType())
                    && tag.equals(entity.getTag())) {

                boolean shouldContinueWarning = false;

                // 根据预警类型判断是否还需要继续预警
                if ("0".equals(entity.getWarnType())) {
                    // 阈值预警
                    shouldContinueWarning = !thresholdWarn.isEmpty();
                } else if ("1".equals(entity.getWarnType())) {
                    // 增量预警
                    shouldContinueWarning = !incrementWarn.isEmpty();
                } else if ("2".equals(entity.getWarnType())) {
                    // 增速预警
                    shouldContinueWarning = !growthWarn.isEmpty();
                }

                // 如果不需要继续预警，则结束该预警
                if (!shouldContinueWarning) {
                    entity.setWarnStatus(ConstantsInfo.WARNING_END); // 预警结束
                    entity.setEndTime(dataUploadTime); // 设置结束时间
                    warnMessageMapper.updateById(entity);
                }
            }
        }
    }


    private List<WarnMessageEntity> judgmentWarn(JSONObject json, Long workFaceId) {
        List<WarnMessageEntity> warnMessageEntityList = new ArrayList<>();
        String tag = json.getStr("tag");
        String monitoringCode = json.getStr("monitoringCode");
        String sensorType = json.getStr("sensorType");
        String sensorLocation = json.getStr("sensorLocation");
        Long dataUploadTime = json.getLong("dataUploadTime");

        if (tag.equals("LCSS")) {
            // 处理顶板离层位移传感器(深基点和浅基点)
            BigDecimal valueSecond = json.getBigDecimal("valueSecond");
            BigDecimal valueShallow = json.getBigDecimal("valueShallow");

            processValuePairWarning(warnMessageEntityList, monitoringCode, sensorType, sensorLocation,
                    dataUploadTime, workFaceId, valueSecond, valueShallow,
                    ConstantsInfo.DEEP, ConstantsInfo.SHALLOW, "1", "2");

        } else if (tag.equals("DCSS")) {
            // 处理电磁辐射传感器(强度和脉冲)
            BigDecimal eleMaxValue = json.getBigDecimal("electromagnetismMaxValue");
            BigDecimal elePulseValue = json.getBigDecimal("electromMagneticPulse");

            processValuePairWarning(warnMessageEntityList, monitoringCode, sensorType, sensorLocation,
                    dataUploadTime, workFaceId, eleMaxValue, elePulseValue,
                    ConstantsInfo.INTENSITY, ConstantsInfo.PULSE, "3", "4");

        } else {
            // 处理其他单值传感器类型
            BigDecimal monitoringValue = json.getBigDecimal("monitoringValue");
            processSingleValueWarning(warnMessageEntityList, monitoringCode, sensorType, sensorLocation,
                    dataUploadTime, workFaceId, monitoringValue, tag);
        }

        return warnMessageEntityList;
    }

    /**
     * 处理双值类型的预警判断（如顶板离层、电磁辐射等）
     */
    private void processValuePairWarning(List<WarnMessageEntity> warnList, String monitoringCode,
                                         String sensorType, String sensorLocation, Long dataUploadTime,
                                         Long workFaceId, BigDecimal firstValue, BigDecimal secondValue,
                                         String firstTag, String secondTag, String firstMark, String secondMark) {

        Long twentyFourHoursTime = ObtainDateUtils.getTwentyFourHoursTime(dataUploadTime);

        // 处理第一个值的预警
        processWarningForValue(warnList, monitoringCode, sensorType, sensorLocation, dataUploadTime,
                workFaceId, firstValue, twentyFourHoursTime, firstTag, firstMark);

        // 处理第二个值的预警
        processWarningForValue(warnList, monitoringCode, sensorType, sensorLocation, dataUploadTime,
                workFaceId, secondValue, twentyFourHoursTime, secondTag, secondMark);
    }

    /**
     * 处理单值类型的预警判断
     */
    private void processSingleValueWarning(List<WarnMessageEntity> warnList, String monitoringCode,
                                           String sensorType, String sensorLocation, Long dataUploadTime,
                                           Long workFaceId, BigDecimal monitoringValue, String tag) {

        Long twentyFourHoursTime = ObtainDateUtils.getTwentyFourHoursTime(dataUploadTime);

        // 根据tag和sensorType确定预警类型标记
        String warnTag = getWarnTagBySensorType(tag, sensorType);

        processWarningForValue(warnList, monitoringCode, sensorType, sensorLocation, dataUploadTime,
                workFaceId, monitoringValue, twentyFourHoursTime, warnTag, "5");
    }

    /**
     * 根据传感器类型获取预警标记
     */
    private String getWarnTagBySensorType(String tag, String sensorType) {
        if (tag.equals("ZKSS")) {
            return ConstantsInfo.DRILL;
        } else if (tag.equals("MGSS")) {
            if (sensorType.equals("1301")) {
                return ConstantsInfo.ANCHOR_ROD;
            } else if (sensorType.equals("1302")) {
                return ConstantsInfo.ANCHOR_CABLE;
            }
        } else if (tag.equals("WYSS")) {
            return ConstantsInfo.LANE;
        } else if (tag.equals("ZJSS")) {
            return ConstantsInfo.SUPPORT;
        }
        return "";
    }

    /**
     * 对单个监测值进行三种预警判断
     */
    private void processWarningForValue(List<WarnMessageEntity> warnList, String monitoringCode,
                                        String sensorType, String sensorLocation, Long dataUploadTime,
                                        Long workFaceId, BigDecimal currentValue, Long twentyFourHoursTime,
                                        String tag, String mark) {

        WarnSchemeDTO warnSchemeDTO = ObtainWarnSchemeUtils.getObtainWarnSchemeT(
                monitoringCode, sensorType, tag, warnSchemeMapper, warnSchemeSeparateMapper);

        List<ThresholdConfigDTO> thresholdDTOS = warnSchemeDTO.getThresholdConfigDTOS();
        List<IncrementConfigDTO> incrementDTOS = warnSchemeDTO.getIncrementConfigDTOS();
        List<GrowthRateConfigDTO> growthRateDTOS = warnSchemeDTO.getGrowthRateConfigDTOS();

        BigDecimal minValue = obtainValue(monitoringCode, sensorType, twentyFourHoursTime, dataUploadTime, mark);
        BigDecimal avgValue = minValue.divide(BigDecimal.valueOf(24), 2, RoundingMode.HALF_UP);

        // 阈值预警判断
        String thresholdWarn = isWarnGeneric(thresholdDTOS, currentValue);
        if (!thresholdWarn.isEmpty()) {
            WarnMessageEntity entity = createWarnMessageEntity(monitoringCode, sensorType, sensorLocation,
                    dataUploadTime, workFaceId, currentValue, "0", thresholdWarn, tag);
            warnList.add(entity);
        }

        // 增量预警判断
        String incrementWarn = isWarnGeneric(incrementDTOS, minValue);
        if (!incrementWarn.isEmpty()) {
            WarnMessageEntity entity = createWarnMessageEntity(monitoringCode, sensorType, sensorLocation,
                    dataUploadTime, workFaceId, currentValue, "1", incrementWarn, tag);
            warnList.add(entity);
        }

        // 增速预警判断
        String growthWarn = isWarnGeneric(growthRateDTOS, avgValue);
        if (!growthWarn.isEmpty()) {
            WarnMessageEntity entity = createWarnMessageEntity(monitoringCode, sensorType, sensorLocation,
                    dataUploadTime, workFaceId, currentValue, "2", growthWarn, tag);
            warnList.add(entity);
        }
    }

    /**
     * 创建预警消息实体
     */
    private WarnMessageEntity createWarnMessageEntity(String monitoringCode, String sensorType,
                                                      String sensorLocation, Long dataUploadTime,
                                                      Long workFaceId, BigDecimal monitoringValue,
                                                      String warnType, String warnLevel, String tag) {
        WarnMessageEntity entity = new WarnMessageEntity();
        entity.setMineId(2L);
        entity.setMeasureNum(monitoringCode);
        entity.setWorkFaceId(workFaceId);
        entity.setSensorType(sensorType);
        entity.setMonitoringValue(monitoringValue);
        entity.setWarnType(warnType);
        entity.setWarnLevel(warnLevel);
        entity.setWarnLocation(sensorLocation);
        entity.setStartTime(dataUploadTime);
        entity.setWarnStatus(ConstantsInfo.WARNING);
        entity.setTag(tag);
        return entity;
    }


    /**
     * 获取最小值（24H）
     */
    private BigDecimal obtainValue(String monitoringCode, String sensorType, Long  startTime, Long endTime, String flag) {
        BigDecimal mainValue = new BigDecimal("0.0");
        LambdaEsQueryWrapper<MeasureActualEntity> queryWrapper = new LambdaEsQueryWrapper<>();
        queryWrapper
                .eq(MeasureActualEntity::getSensorType, sensorType)
                .eq(MeasureActualEntity::getMeasureNum, monitoringCode)
                .eq(MeasureActualEntity::getMineId, 2L)
                .between(MeasureActualEntity::getDataTime, startTime, endTime);
        List<MeasureActualEntity> measureActualEntities = measureActualMapper.selectList(queryWrapper);
        if (flag.equals("1")) {
            mainValue = measureActualEntities.stream()
                    .map(MeasureActualEntity::getValueDeep)
                    .filter(Objects::nonNull)
                    .min(BigDecimal::compareTo)
                    .orElse(null);
        } else if (flag.equals("2")) {
            mainValue = measureActualEntities.stream()
                    .map(MeasureActualEntity::getValueShallow)
                    .filter(Objects::nonNull)
                    .min(BigDecimal::compareTo)
                    .orElse(null);
        } else if (flag.equals("3")) {
            mainValue = measureActualEntities.stream()
                    .map(MeasureActualEntity::getEleMaxValue)
                    .filter(Objects::nonNull)
                    .min(BigDecimal::compareTo)
                    .orElse(null);
        } else if (flag.equals("4")) {
            mainValue = measureActualEntities.stream()
                    .map(MeasureActualEntity::getElePulse)
                    .filter(Objects::nonNull)
                    .min(BigDecimal::compareTo)
                    .orElse(null);
        } else if (flag.equals("5")) {
            mainValue = measureActualEntities.stream()
                    .map(MeasureActualEntity::getMonitoringValue)
                    .filter(Objects::nonNull)
                    .min(BigDecimal::compareTo)
                    .orElse(null);
        }
        return mainValue;
    }


    /**
     * 判断阈值是否预警（通用方法，基于反射）
     *
     * @param configs 配置列表，元素需包含getNumberValue(), getCompareType(), getGrade()方法
     * @param monitoringValue  监测值
     * @return 预警等级
     */
    public String isWarnGeneric(List<?> configs, BigDecimal monitoringValue) {
        String grade = "";
        if (configs == null || monitoringValue == null) {
            return grade;
        }
        // 过滤有效配置
        List<Object> validConfigs = new ArrayList<>();
        for (Object config : configs) {
            if (config != null) {
                try {
                    // 检查对象是否包含必需的方法
                    config.getClass().getMethod("getNumberValue");
                    config.getClass().getMethod("getCompareType");
                    config.getClass().getMethod("getGrade");
                    validConfigs.add(config);
                } catch (NoSuchMethodException e) {
                    // 忽略不包含必需方法的对象
                }
            }
        }

        // 存储满足条件的预警级别
        List<Object> triggeredConfigs = new ArrayList<>();

        // 检查哪些预警条件被触发
        for (Object config : validConfigs) {
            try {
                BigDecimal numberValue = (BigDecimal) config.getClass().getMethod("getNumberValue").invoke(config);
                String compareType = (String) config.getClass().getMethod("getCompareType").invoke(config);

                if (numberValue == null || compareType == null) {
                    continue;
                }

                boolean isTriggered = false;
                switch (compareType) {
                    case ">":
                        isTriggered = monitoringValue.compareTo(numberValue) > 0;
                        break;
                    case ">=":
                        isTriggered = monitoringValue.compareTo(numberValue) >= 0;
                        break;
                    case "<":
                        isTriggered = monitoringValue.compareTo(numberValue) < 0;
                        break;
                    case "<=":
                        isTriggered = monitoringValue.compareTo(numberValue) <= 0;
                        break;
                    case "==":
                        isTriggered = monitoringValue.compareTo(numberValue) == 0;
                        break;
                    default:
                        break;
                }

                if (isTriggered) {
                    triggeredConfigs.add(config);
                }
            } catch (Exception e) {
                // 忽略反射调用异常
            }
        }

        // 如果没有触发任何预警，返回空字符串
        if (triggeredConfigs.isEmpty()) {
            return grade;
        }

        // 如果只有一个预警被触发，直接返回该预警级别
        if (triggeredConfigs.size() == 1) {
            try {
                return (String) triggeredConfigs.get(0).getClass().getMethod("getGrade").invoke(triggeredConfigs.get(0));
            } catch (Exception e) {
                return grade;
            }
        }

        // 如果有多个预警被触发，按阈值排序后返回最高级别的预警
        // 排序规则：
        // 1. 对于">"和">="类型，数值越大优先级越高
        // 2. 对于"<"和"<="类型，数值越小优先级越高
        // 3. ">"和">="类型优先级高于"<"和"<="类型
        triggeredConfigs.sort((a, b) -> {
            try {
                BigDecimal aValue = (BigDecimal) a.getClass().getMethod("getNumberValue").invoke(a);
                BigDecimal bValue = (BigDecimal) b.getClass().getMethod("getNumberValue").invoke(b);
                String aCompareType = (String) a.getClass().getMethod("getCompareType").invoke(a);
                String bCompareType = (String) b.getClass().getMethod("getCompareType").invoke(b);

                boolean isGreaterA = aCompareType.equals(">") || aCompareType.equals(">=");
                boolean isGreaterB = bCompareType.equals(">") || bCompareType.equals(">=");
                boolean isLessA = aCompareType.equals("<") || aCompareType.equals("<=");
                boolean isLessB = bCompareType.equals("<") || bCompareType.equals("<=");

                // 如果都是大于类型或都是小于类型
                if ((isGreaterA && isGreaterB) || (isLessA && isLessB)) {
                    int comparison = aValue.compareTo(bValue);
                    // 对于大于类型，值越大优先级越高（降序）
                    if (isGreaterA) {
                        return -comparison;
                    }
                    // 对于小于类型，值越小优先级越高（升序）
                    else {
                        return comparison;
                    }
                }
                // 如果一个是大于类型，一个是小于类型
                else if (isGreaterA && isLessB) {
                    return -1; // 大于类型优先级更高
                } else if (isLessA && isGreaterB) {
                    return 1; // 大于类型优先级更高
                }
            } catch (Exception e) {
                // 发生异常时保持原有顺序
                return 0;
            }

            return 0;
        });

        try {
            return (String) triggeredConfigs.get(0).getClass().getMethod("getGrade").invoke(triggeredConfigs.get(0));
        } catch (Exception e) {
            return grade;
        }
    }


    public static void main(String[] args) {
        // 创建类实例以调用非静态方法
        EsBatchConsumer consumer = new EsBatchConsumer();

        List<ThresholdConfigDTO> thresholdConfigDTOS = new ArrayList<>();
        ThresholdConfigDTO thresholdConfigDTO1 = new ThresholdConfigDTO();
        thresholdConfigDTO1.setNumberValue(new BigDecimal("10"));
        thresholdConfigDTO1.setCompareType(">=");
        thresholdConfigDTO1.setGrade("1");
        thresholdConfigDTO1.setUnit("");
        thresholdConfigDTOS.add(thresholdConfigDTO1);

        ThresholdConfigDTO thresholdConfigDTO2 = new ThresholdConfigDTO();
        thresholdConfigDTO2.setNumberValue(new BigDecimal("20"));
        thresholdConfigDTO2.setCompareType(">=");
        thresholdConfigDTO2.setGrade("2");
        thresholdConfigDTO2.setUnit("");
        thresholdConfigDTOS.add(thresholdConfigDTO2);

        ThresholdConfigDTO thresholdConfigDTO3 = new ThresholdConfigDTO();
        thresholdConfigDTO3.setNumberValue(new BigDecimal("30"));
        thresholdConfigDTO3.setCompareType(">=");
        thresholdConfigDTO3.setGrade("3");
        thresholdConfigDTO3.setUnit("");
        thresholdConfigDTOS.add(thresholdConfigDTO3);

        ThresholdConfigDTO thresholdConfigDTO4 = new ThresholdConfigDTO();
        thresholdConfigDTO4.setNumberValue(new BigDecimal("5"));
        thresholdConfigDTO4.setCompareType("<");
        thresholdConfigDTO4.setGrade("4");
        thresholdConfigDTO4.setUnit("");
        thresholdConfigDTOS.add(thresholdConfigDTO4);

        // 调用方法进行测试
        String result = consumer.isWarnGeneric(thresholdConfigDTOS, new BigDecimal("6"));
        System.out.println("预警等级: " + result);
    }

}
