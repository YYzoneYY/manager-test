package com.ruoyi.system.domain.dto;

/**
 * @author: shikai
 * @date: 2024/11/20
 * @description:
 */
public interface NewTreeEntity<T,V> {
    V getValue();

    String getLabel();

    Long fileId();

    String documentName();

    Long createTime();

    Integer level();

    Long sort();

    boolean isDisable();

    T getSuperId();

    T getDataId();
}