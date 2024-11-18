/*
 *    Copyright (c) 2018-2025, whzb All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of the pig4cloud.com developer nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * Author: whzb
 */
package com.ruoyi.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.ruoyi.common.config.MinioConfig;
import com.ruoyi.common.utils.file.FileTypeUtils;
import com.ruoyi.common.utils.file.NewFileUploadUtils;
import com.ruoyi.system.domain.SysFileInfo;
import com.ruoyi.system.mapper.SysFileInfoMapper;
import com.ruoyi.system.service.SysFileInfoService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class SysFileInfoServiceImpl extends ServiceImpl<SysFileInfoMapper, SysFileInfo> implements SysFileInfoService {

    @Resource
    private SysFileInfoMapper sysFileInfoMapper;

    @Resource
    private MinioConfig minioConfig;

    @Resource
    private MinioClient minioClient;

    @Override
    public SysFileInfo upload(MultipartFile file, String bucketName, String isTemplate) {
        String defaultBucketName = minioConfig.getDefaultBucketName();
        if (StrUtil.isNotBlank(bucketName)) {
            defaultBucketName = bucketName;
        }
        SysFileInfo sysFileInfo = new SysFileInfo();
        sysFileInfo.setFileOldName(file.getOriginalFilename());
        String fileName;
        if ("1".equals(isTemplate)) {
            fileName = NewFileUploadUtils.extractFilenameTemplate(file);
        } else {
            fileName = NewFileUploadUtils.extractFilename(file);
        }
        sysFileInfo.setFileNewName(fileName.substring(fileName.lastIndexOf("/") + 1));
        sysFileInfo.setFilePath(fileName.substring(0, fileName.lastIndexOf("/") + 1));
        sysFileInfo.setFileSuffix(FileTypeUtils.getExtension(file));
        sysFileInfo.setFileSize(file.getSize());
        sysFileInfo.setBucketName(defaultBucketName);
        PutObjectArgs args;
        try {
            args = PutObjectArgs.builder()
                    .bucket(defaultBucketName)
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    //.region("cn-a")
                    .build();
            minioClient.putObject(args);
        } catch (Exception e) {
            log.error("文件上传失败!" + e.getMessage(), e);
            throw new RuntimeException("文件上传失败!");
        }
        String url = minioConfig.getEndpoint() + "/" + defaultBucketName + "/" + fileName;
        sysFileInfo.setFileUrl(url);
        sysFileInfo.setCreateTime(System.currentTimeMillis());
        // todo 最后统一鉴权 SecurityUtils.getUserId()
        sysFileInfo.setCreateBy(1L);
        if (sysFileInfoMapper.insert(sysFileInfo) < 1) {
            throw new RuntimeException("文件信息插入库失败!");
        }
        return sysFileInfoMapper.selectById(sysFileInfo.getFileId());
    }

    @Override
    public SysFileInfo getById(Long fileId) {
        SysFileInfo sysFileInfo = sysFileInfoMapper.selectById(fileId);
        sysFileInfo.setFileUrl(sysFileInfo.getFileUrl().replaceFirst("http://.*:\\d+", minioConfig.getEndpoint()));
        return sysFileInfo;
    }

    @Override
    public void batchLogicalDelete(Long[] fileIds) {
        SysFileInfo sysFileInfo = new SysFileInfo();
        sysFileInfo.setDelFlag("2");
        // todo 最后统一鉴权 SecurityUtils.getUserId()
        sysFileInfo.setCreateBy(1L);
        sysFileInfo.setUpdateBy(1L);
        sysFileInfo.setUpdateTime(System.currentTimeMillis());
        for (Long fileId : fileIds) {
            sysFileInfo.setFileId(fileId);
            sysFileInfoMapper.updateById(sysFileInfo);
        }
    }

    @Override
    public void batchDelete(Long[] fileIds) {
  if (null != fileIds && fileIds.length > 0) {
            List<Long> fileIdList = Arrays.asList(fileIds);
            List<SysFileInfo> fileNewNames = sysFileInfoMapper.selectByFileIds(fileIdList);
            if (fileNewNames.size() > 0) {
                fileNewNames.forEach(f -> {
                    try {
                        minioClient.removeObject(RemoveObjectArgs.builder().bucket(f.getBucketName()).object(f.getFilePath() + f.getFileNewName()).build());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    sysFileInfoMapper.deleteById(f.getFileId());
                });
            }
        }
    }
}
