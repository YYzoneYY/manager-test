package com.ruoyi.es.util;

import cn.hutool.json.JSONObject;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import com.ruoyi.es.Constant.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author: shikai
 * @date: 2025/1/2
 * @description:
 */
@Component
public class BulkCreateUtil {

    private static final Logger log = LoggerFactory.getLogger(BulkCreateUtil.class);

    //同步客户端
    private final ElasticsearchClient elasticsearchClient;

    public BulkCreateUtil(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    public void bulkCreate(JSONObject jsonObject) throws IOException {
        BulkRequest.Builder br = new BulkRequest.Builder();
        String tag = (String) jsonObject.get("tag");
        switch (tag) {
            case Constant.DRILLING_STRESS_REAL_TIME:
            case Constant.BOLT_REAL_TIME:
            case Constant.ROOF_ABSCISSION_LAYER_REAL_TIME:
            case Constant.SUPPORT_RESISTANCE_REAL_TIME:
            case Constant.LANE_DISPLACEMENT_REAL_TIME:
                indexDocument(Constant.MINE_PRESSURE_INDEX, jsonObject, br);
                break;
        }
        elasticsearchClient.bulk(br.build());
    }

    /**
     * 创建ES文档
     */
    private void indexDocument(String indexName, JSONObject document, BulkRequest.Builder br) {
        try {
            br.operations(op -> op.index(idx -> idx
                    .index(indexName)
                    .document(document)));
        } catch (Exception e) {
            log.error("Error indexing document: {}", e.getMessage());
        }
    }
}