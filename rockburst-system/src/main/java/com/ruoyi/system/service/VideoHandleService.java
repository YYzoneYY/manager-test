package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.Entity.VideoHandleEntity;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/3/7
 * @description:
 */
public interface VideoHandleService extends IService<VideoHandleEntity> {

    boolean insert(Long projectId, List<String> beforeVideoUrls);

    boolean deleteById(List<Long> projectIdList);
}