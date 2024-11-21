package com.ruoyi.system.domain.dto;

/**
 * @author: shikai
 * @date: 2024/11/20
 * @description: 说明 用于获取树形列表（用于其他模块下拉框）
 */
public interface TreeListEntity<T,V> {

    V getValue();

    String getLabel();

    T getSuperId();

    T getDataId();
}