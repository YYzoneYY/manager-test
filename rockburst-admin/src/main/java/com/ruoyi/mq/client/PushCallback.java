package com.ruoyi.mq.client;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PushCallback implements MqttCallbackExtended {

    @Autowired
    private EsBatchConsumer esBatchConsumer;

    @Setter
    private MqttClient client;

    /**
     * 连接成功或重连成功后调用
     */
    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        log.info("MQTT连接成功. 是否为重连: {}, Server URI: {}", reconnect, serverURI);
        // 在这里重新订阅主题，确保连接恢复后能继续接收消息
        try {
            log.info("检测到重连，将执行重新订阅...");
            if (this.client != null && this.client.isConnected()) {
                log.info("Paho客户端将自动恢复订阅。");
            }
        } catch (Exception e) {
            log.error("重连后重新订阅主题失败", e);
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        // Paho客户端有自己的自动重连逻辑，这里主要用于打印日志，记录问题
        // 原有的手动重连逻辑可以移除或保留作为备用，但通常不需要
        log.error("MQTT连接丢失，将由客户端自动重连。原因: {}", cause.getMessage(), cause);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // 消息发送完成后的回调，对于消费者而言通常不重要
    }

    /**
     * 核心改造：订阅主题接收到消息后的处理方法
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        // 这个回调线程由Paho客户端管理，操作非常轻量，不会阻塞它
        try {
            // a. 快速解析JSON
            JSONObject jsonObject = JSONUtil.parseObj(new String(message.getPayload()));
            // b. 将JSON对象快速扔进缓冲队列
            boolean success = esBatchConsumer.offer(jsonObject);
            // c. 如果缓冲队列已满，可以选择抛出异常来触发重试
            if (!success) {
                // 抛出异常，告知Paho客户端不要发送ACK，以便Broker后续重发
                throw new Exception("本地消费缓冲队列已满，无法处理消息");
            }
            // d. 打印简明日志
            log.info("接收到消息并成功入队: Topic={}, QoS={}", topic, message.getQos());
        } catch (Exception e) {
            log.error("处理MQ消息时发生错误, 将触发重试 (如果QoS>0). Topic:{}, Content:{}", topic, new String(message.getPayload()), e);
            // 向上抛出异常，这是利用Paho机制实现重试的关键
            throw e;
        }
    }
}
