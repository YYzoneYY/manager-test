package com.ruoyi.es.util;

import co.elastic.clients.elasticsearch.core.BulkRequest;

import java.util.List;
import java.util.Map;

/**
 * @author: shikai
 * @date: 2025/1/2
 * @description:
 */
public class BulkCreateUtil {

    public void bulkCreate(List<Map<String, Object>> mapList) {
        BulkRequest.Builder br = new BulkRequest.Builder();
        for (Map<String, Object> map : mapList) {

        }

    }
}