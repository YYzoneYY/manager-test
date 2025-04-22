package com.ruoyi.push;

import com.getui.push.v2.sdk.ApiHelper;
import com.getui.push.v2.sdk.GtApiConfiguration;
import com.getui.push.v2.sdk.api.PushApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 个推配置类
 **/
@Configuration
public class GeTuiConfig {
    @Value("${getui.baseUrl}")
    private String baseUrl;

    @Value("${getui.appId}")
    private String appId;

    @Value("${getui.appKey}")
    private String appKey;

    @Value("${getui.appSecret}")
    private String appSecret;

    @Value("${getui.masterSecret}")
    private String masterSecret;

    @Bean(name = "myPushApi")
    public PushApi pushApi() {
        // 设置httpClient最大连接数，当并发较大时建议调大此参数。或者启动参数加上 -Dhttp.maxConnections=200
        System.setProperty("http.maxConnections", "200");
        GtApiConfiguration apiConfiguration = new GtApiConfiguration();
        //填写应用配置
        apiConfiguration.setAppId(appId);
        apiConfiguration.setAppKey(appKey);
        apiConfiguration.setMasterSecret(masterSecret);
        // 接口调用前缀，请查看文档: 接口调用规范 -> 接口前缀, 可不填写appId
        apiConfiguration.setDomain(baseUrl);
        // 实例化ApiHelper对象，用于创建接口对象
        ApiHelper apiHelper = ApiHelper.build(apiConfiguration);
        // 创建对象，建议复用。目前有PushApi、StatisticApi、UserApi
        return apiHelper.creatApi(PushApi.class);
    }
}
