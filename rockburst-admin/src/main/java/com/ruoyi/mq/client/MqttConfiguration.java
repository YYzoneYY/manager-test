//package com.ruoyi.mq.client;
//
//import lombok.Setter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.stereotype.Component;
//
//@Component
//@Configuration
//@ConfigurationProperties(MqttConfiguration.PREFIX)
//public class MqttConfiguration {
//
//    @Autowired
//    private MqttPushClient mqttPushClient; // 注入我们重构后的MqttPushClient
//
//    public static final String PREFIX = "mqtt";
//    private String host;
//    private String clientId;
//    private String userName;
//    private String password;
//    private String topic;
//    private int timeout;
//    private int keepAlive;
//
//    // 新增 cleanSession 属性,默认为 false，这是保证离线消息不丢失的关键
//    @Setter
//    private boolean cleanSession = false;
//
//    public boolean getCleanSession() {
//        return cleanSession;
//    }
//
//    public String getClientid() {
//        // 在getter中动态添加随机后缀
//        if (this.clientId == null || this.clientId.trim().isEmpty()) {
//            // 如果配置文件没写，生成一个完全随机的
//            return "mqtt-client-" + System.currentTimeMillis();
//        }
//        // 如果配置了，就在后面加上随机后缀，保证每次重启都不同
//        return this.clientId + "-" + System.currentTimeMillis();
//    }
//
//    public void setClientid(String clientid) {
//        this.clientId = clientid;
//    }
//
//    public String getUsername() {
//        return userName;
//    }
//
//    public void setUsername(String username) {
//        this.userName = username;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public String getTopic() {
//        return topic;
//    }
//
//    public void setTopic(String topic) {
//        this.topic = topic;
//    }
//
//    public int getTimeout() {
//        return timeout;
//    }
//
//    public void setTimeout(int timeout) {
//        this.timeout = timeout;
//    }
//
//    public int getKeepalive() {
//        return keepAlive;
//    }
//
//    public void setKeepalive(int keepalive) {
//        this.keepAlive = keepalive;
//    }
//
//    public String getHost() {
//        return host;
//    }
//
//    public void setHost(String host) {
//        this.host = host;
//    }
//
//    /**
//     * 连接至mqtt服务器，获取mqtt连接
//     * @return
//     */
//    @Bean
//    public MqttPushClient getMqttPushClient() {
//        // a. 调用重构后的 connect 方法，并传入 cleanSession
//        mqttPushClient.connect(host, clientId, userName, password, timeout, keepAlive, this.getCleanSession());
//        // b. 调用重构后新增的 subscribe 方法，并使用 QoS 1
//        mqttPushClient.subscribe(topic, 1);
//        // c. 返回配置好的 mqttPushClient 实例
//        return mqttPushClient;
//    }
//}
