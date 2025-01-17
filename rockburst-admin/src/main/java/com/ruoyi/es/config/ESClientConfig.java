//package com.ruoyi.es.config;
//
//
//import co.elastic.clients.elasticsearch.ElasticsearchClient;
//import co.elastic.clients.json.jackson.JacksonJsonpMapper;
//import co.elastic.clients.transport.rest_client.RestClientTransport;
//import lombok.Setter;
//import org.apache.http.HttpHost;
//import org.elasticsearch.client.RestClient;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.util.StringUtils;
//
///**
// * @author: shikai
// * @date: 2024/12/21
// * @description:
// */
//@Setter
//@Configuration
//public class ESClientConfig {
//
//    @Value("${spring.elasticsearch.rest.uris}")
//    private String hosts;
//
//    //超时时间设置
//    public static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 10000;
//    public static final int DEFAULT_SOCKET_TIMEOUT_MILLIS = 300000;
//    public static final int DEFAULT_CONNECT_REQUEST_TIMEOUT_MILLIS = 1000;
//
//    @Bean
//    @Lazy
//    public ElasticsearchClient elasticsearchClient() {
//        return new ElasticsearchClient(getTransport());
//    }
//
//    /**
//     * 获取客户端 RestClientTransport
//     */
//    private RestClientTransport getTransport() {
//        HttpHost[] httpHosts = toHttpHost();
//        RestClient restClient = getRestClient(httpHosts);
//        return new RestClientTransport(restClient, new JacksonJsonpMapper());
//    }
//
//    /**
//     * 获取客户端RestClient
//     * @param httpHosts http数组
//     */
//    private RestClient getRestClient(HttpHost[] httpHosts) {
//        return RestClient.builder(httpHosts)
//                .setRequestConfigCallback(requestConfigBuilder -> {
//                    requestConfigBuilder.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS);
//                    requestConfigBuilder.setSocketTimeout(DEFAULT_SOCKET_TIMEOUT_MILLIS);
//                    requestConfigBuilder.setConnectionRequestTimeout(DEFAULT_CONNECT_REQUEST_TIMEOUT_MILLIS);
//                    return requestConfigBuilder;
//                }).build();
//    }
//
//    /**
//     * 解析配置的字符串hosts，转为HttpHost对象数组
//     */
//    private HttpHost[] toHttpHost() {
//        if (!StringUtils.hasLength(hosts)) {
//            throw new RuntimeException("invalid elasticsearch configuration. elasticsearch.hosts不能为空！");
//        }
//        // 多个IP逗号隔开
//        String[] hostArray = hosts.split(",");
//        HttpHost[] httpHosts = new HttpHost[hostArray.length];
//        HttpHost httpHost;
//        for (int i = 0; i < hostArray.length; i++) {
//            String[] strings = hostArray[i].split(":");
//            httpHost = new HttpHost(strings[0], Integer.parseInt(strings[1]), "http");
//            httpHosts[i] = httpHost;
//        }
//        return httpHosts;
//    }
//
//}