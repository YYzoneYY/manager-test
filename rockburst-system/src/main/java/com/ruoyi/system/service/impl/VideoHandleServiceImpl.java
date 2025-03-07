package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.system.domain.Entity.VideoHandleEntity;
import com.ruoyi.system.mapper.VideoHandleMapper;
import com.ruoyi.system.service.VideoHandleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: shikai
 * @date: 2025/3/7
 * @description:
 */

@Service
@Transactional
public class VideoHandleServiceImpl extends ServiceImpl<VideoHandleMapper, VideoHandleEntity> implements VideoHandleService {

    @Resource
    private VideoHandleMapper videoHandleMapper;

    @Override
    public boolean insert(Long projectId, List<String> beforeVideoUrls) {
        boolean flag = false;
        ArrayList<VideoHandleEntity> videoHandleEntities = new ArrayList<>();
        beforeVideoUrls.forEach(beforeVideoUrl -> {
            VideoHandleEntity videoHandleEntity = new VideoHandleEntity();
            videoHandleEntity.setProjectId(projectId);
            videoHandleEntity.setBeforeVideoUrl(beforeVideoUrl);
            videoHandleEntity.setStatus(ConstantsInfo.ZERO_IDENTIFY_STATUS);
            videoHandleEntities.add(videoHandleEntity);
        });
        flag = this.saveBatch(videoHandleEntities);
        return flag;
    }

    @Override
    public boolean update(Long projectId, List<String> beforeVideoUrls) {
        boolean flag = false;
        ArrayList<VideoHandleEntity> videoHandleEntities = new ArrayList<>();
        int delete = videoHandleMapper.delete(new LambdaQueryWrapper<VideoHandleEntity>()
                .eq(VideoHandleEntity::getProjectId, projectId));
        if (delete > 0) {
            beforeVideoUrls.forEach(beforeVideoUrl -> {
                VideoHandleEntity videoHandleEntity = new VideoHandleEntity();
                videoHandleEntity.setProjectId(projectId);
                videoHandleEntity.setBeforeVideoUrl(beforeVideoUrl);
                videoHandleEntity.setStatus(ConstantsInfo.ZERO_IDENTIFY_STATUS);
                videoHandleEntities.add(videoHandleEntity);
            });
            flag = this.saveBatch(videoHandleEntities);
        } else {
            return flag;
        }
        return flag;
    }

    @Override
    public boolean deleteById(List<Long> projectIdList) {
        boolean flag = false;
        flag = this.remove(new LambdaQueryWrapper<VideoHandleEntity>().in(VideoHandleEntity::getProjectId, projectIdList));
        return flag;
    }
}