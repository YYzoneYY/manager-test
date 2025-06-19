package com.ruoyi.system.domain.utils;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: shikai
 * @date: 2025/6/18
 * @description:
 */
public class SendMessageUtils {

    public static <T> String sendMessage(String msgType, List<T> dataList) throws IOException {
        if (dataList == null) {
            throw new IllegalArgumentException("dataList cannot be null");
        }
        List<Map<String, Object>> wrappedData = new ArrayList<>(dataList.size());
        long timestamp = System.currentTimeMillis();

        for (T item : dataList) {
            Map<String, Object> map;

            if (item instanceof Map) {
                // 安全地转换 Map<?, ?>
                Map<?, ?> rawMap = (Map<?, ?>) item;
                map = new HashMap<>();
                for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                    if (entry.getKey() instanceof String) {
                        map.put((String) entry.getKey(), entry.getValue());
                    }
                }
            } else {
                map = BeanUtil.beanToMap(item);
            }

            // 移除 alarmType 字段（无论是否存在）
            map.remove("alarmType");
            // 添加时间戳到每个 data 对象
            map.put("timestamp", timestamp);
            wrappedData.add(map);
        }

        // 构造最终消息体
        Map<String, Object> message = new HashMap<>(4);
        message.put("msgType", msgType);
        message.put("data", wrappedData);
        return JSON.toJSONString(message);
    }
}