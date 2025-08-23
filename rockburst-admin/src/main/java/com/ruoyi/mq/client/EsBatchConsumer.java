package com.ruoyi.mq.client;

import cn.hutool.json.JSONObject;

import com.ruoyi.system.EsMapper.MeasureActualMapper;
import com.ruoyi.system.EsMapper.WarnMessageMapper;
import com.ruoyi.system.domain.EsEntity.MeasureActualEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class EsBatchConsumer implements InitializingBean, DisposableBean { // 1. 实现生命周期接口

    @Resource
    private MeasureActualMapper measureActualMapper;

    @Resource
    private WarnMessageMapper warnMessageMapper;

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
                    if (tag.equals("ZKSS") || tag.equals("MGSS") || tag.equals("WYSS")) {
                        entity.setMonitoringValue(json.getBigDecimal("monitoringValue"));
                    } else if (tag.equals("LCSS")) {
                        entity.setValueShallow(json.getBigDecimal("valueShallow"));
                        entity.setValueDeep(json.getBigDecimal("valueSecond"));
                    } else if (tag.equals("ZJSS")) {
                        entity.setSensorNum(json.getStr("sensorCode"));
                    } else if (tag.equals("DCSS")) {
                        entity.setEleMaxValue(json.getBigDecimal("electromagnetismMaxValue"));
                        entity.setElePulse(json.getBigDecimal("electromMagneticPulse"));
                    }
                    esEntities.add(entity);
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
}
