package com.ruoyi.system.domain.dto;

/**
 * @author: shikai
 * @date: 2024/12/18
 * @description:
 */
public interface ContentsTreeEntity<T, V> {

    V getValue();

    String getLabel();

    boolean isDisable();

    T getSuperId();

    T getContentsId();
}