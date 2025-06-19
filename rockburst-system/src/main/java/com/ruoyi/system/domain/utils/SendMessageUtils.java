package com.ruoyi.system.domain.utils;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.system.domain.dto.largeScreen.AlarmMessage;

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

    public static <T extends AlarmMessage> String sendMessage(List<T> dataList) throws IOException {
        if (dataList == null || dataList.isEmpty()) {
            throw new IllegalArgumentException("dataList cannot be null or empty");
        }

        long timestamp = System.currentTimeMillis();

        List<Map<String, Object>> wrappedData = new ArrayList<>(dataList.size());

        for (T item : dataList) {
            Map<String, Object> map;

            if (item instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> rawMap = (Map<String, Object>) item;
                map = new HashMap<>(rawMap);
            } else {
                map = BeanUtil.beanToMap(item);
            }

            map.put("timestamp", timestamp);
            wrappedData.add(map);
        }

        Map<String, Object> message = new HashMap<>();
        message.put("msgType", "alarm");
        message.put("data", wrappedData);

        return JSON.toJSONString(message);
    }
}