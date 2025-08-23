package com.ruoyi.mq.client;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MqttPushClient {

    // 只依赖 PushCallback
    @Autowired
    private PushCallback pushCallback;

    @Getter
    private MqttClient client;

    /**
     * 连接MQTT服务器的核心方法
     * 这个方法现在只负责创建和连接，不再处理订阅
     */
    public void connect(String host, String clientID, String username, String password, int timeout, int keepalive, boolean cleanSession) {
        try {
            //新实例赋值给成员变量 this.client
            this.client = new MqttClient(host, clientID, new MemoryPersistence());

            // 配置连接选项
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            options.setConnectionTimeout(timeout);
            options.setKeepAliveInterval(keepalive);
            // 关键可靠性配置
            options.setCleanSession(cleanSession); // 5. 使用从配置传入的cleanSession值
            options.setAutomaticReconnect(true);   // 6. 启用Paho客户端强大的自动重连功能
            // 7. 将client实例设置给PushCallback，以便在重连成功后重新订阅
            // 确保PushCallback中有setClient(MqttClient client)方法
            pushCallback.setClient(this.client);
            // 8. 设置回调。必须在connect之前设置
            this.client.setCallback(pushCallback);
            // 9. 连接
            this.client.connect(options);
            log.info("MqttPushClient: 成功连接到MQTT Broker。");
        } catch (MqttException e) {
            log.error("连接MQTT时发生严重错误，请检查配置和网络。", e);
            // 抛出运行时异常，可以让Spring Boot启动失败，以便及时发现问题
            throw new RuntimeException("MQTT Client connection failed", e);
        }
    }

    /**
     * 10. 新增：提供一个健壮的、带QoS的订阅方法
     *
     * @param topic 主题
     * @param qos   服务质量等级 (0, 1, 2)
     */
    public void subscribe(String topic, int qos) {
        if (this.client == null || !this.client.isConnected()) {
            log.error("MQTT客户端未连接，无法订阅主题: {}", topic);
            return;
        }
        try {
            log.info("正在订阅主题: {}, QoS: {}", topic, qos);
            this.client.subscribe(topic, qos);
            log.info("成功订阅主题: {}", topic);
        } catch (MqttException e) {
            log.error("订阅主题 {} 时发生错误", topic, e);
        }
    }


    /**
     * 发布消息
     *
     * @param qos         服务质量等级
     * @param retained    是否保留消息
     * @param topic       主题
     * @param pushMessage 消息内容
     */
    public void publish(int qos, boolean retained, String topic, String pushMessage) {
        if (this.client == null || !this.client.isConnected()) {
            log.error("MQTT客户端未连接，无法发布消息到主题: {}", topic);
            return;
        }
        try {
            MqttMessage message = new MqttMessage();
            message.setQos(qos);
            message.setRetained(retained);
            message.setPayload(pushMessage.getBytes());

            // 直接使用client发布，而不是getTopic().publish()，更直接
            this.client.publish(topic, message);
        } catch (MqttException e) {
            log.error("MQTT发布消息时发生异常:", e);
        }
    }

    /**
     * 重载的发布方法，提供默认值
     */
    public void publish(String topic, String pushMessage) {
        // 默认使用QoS 1 和 non-retained，更可靠
        publish(1, false, topic, pushMessage);
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (this.client != null && this.client.isConnected()) {
            try {
                this.client.disconnect();
                log.info("成功断开MQTT连接。");
            } catch (MqttException e) {
                log.error("断开MQTT连接时发生异常。", e);
            }
        }
    }
}
