package com.ruoyi.system.domain.utils;

/**
 * @author: shikai
 * @date: 2025/8/14
 * @description:
 */
public class validatePageUtils {

    // 校验并设置默认页码
    public static int validateAndSetDefaultPageNum(Integer pageNum) {
        if (pageNum == null || pageNum < 1) {
            return 1; // 默认页码为1
        }
        return pageNum;
    }

    // 校验并设置默认每页大小，限制最大值
    public static int validateAndSetDefaultPageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return 10; // 默认每页大小为10
        }
        return Math.min(pageSize, 100); // 最大每页大小限制为100
    }
}