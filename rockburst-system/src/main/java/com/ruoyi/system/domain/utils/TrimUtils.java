package com.ruoyi.system.domain.utils;

import cn.hutool.core.util.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * @author: shikai
 * @date: 2025/2/21
 * @description:
 */
public class TrimUtils {
    private static Logger log = LoggerFactory.getLogger(TrimUtils.class);

    public static void trimBean(Object model) {
        Class clazz = model.getClass();
        Field[] fields = ReflectUtil.getFields(clazz);
        for (Field field : fields) {
            if ("class java.lang.String".equals(field.getGenericType().toString())) {
                Object value = ReflectUtil.getFieldValue(model, field);
                if (value != null) {
                    value = String.valueOf(value).trim();
                    field.setAccessible(true);
                    try {
                        field.set(model, value);
                    } catch (IllegalAccessException e) {
                        log.info("查询条件去除空格异常");
                    }
                }
            }
        }
    }
}