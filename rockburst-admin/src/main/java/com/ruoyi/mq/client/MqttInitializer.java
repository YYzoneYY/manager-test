package com.ruoyi.mq.client;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "mqtt")
public class MqttInitializer {

    // 1. 只注入 MqttPushClient，不再有其他注入
    @Autowired
    private MqttPushClient mqttPushClient;

    // 2. 所有配置属性由 Spring Boot 自动注入
    private String host;
    private String clientId;
    private String userName;
    private String password;
    private String topic;
    private int timeout;
    private int keepAlive;
    private boolean cleanSession = false;

    // 3. 动态生成唯一的 Client ID
    public String getClientId() {
        if (this.clientId == null || this.clientId.trim().isEmpty()) {
            return "mqtt-client-" + System.currentTimeMillis();
        }
        return this.clientId + "-" + System.currentTimeMillis();
    }

    /**
     * 在所有Bean都初始化完成后，执行连接和订阅
     */
    @PostConstruct
    public void init() {
        log.info("MQTT Initializer: 开始执行初始化...");
        try {
            // 调用connect方法，此时所有依赖都已注入
            mqttPushClient.connect(host, getClientId(), userName, password, timeout, keepAlive, cleanSession);

            // 调用subscribe方法
            mqttPushClient.subscribe(topic, 1);

            log.info("MQTT Initializer: 初始化成功完成。");
        } catch (Exception e) {
            log.error("MQTT Initializer: 初始化过程中发生严重错误！", e);
            throw new RuntimeException("Failed to initialize MQTT client.", e);
        }
    }

    /**
     * 应用关闭时，优雅地断开连接
     */
    @PreDestroy
    public void shutdown() {
        log.info("MQTT Initializer: 正在关闭MQTT连接...");
        mqttPushClient.disconnect();
    }
}
