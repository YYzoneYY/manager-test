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

package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.SysFileInfo;
import com.ruoyi.system.service.SysFileInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;


@RestController
@RequestMapping(value = "/fileInfo")
@Api(tags = "文件模块")
public class SysFileInfoController {
    @Resource
    private SysFileInfoService sysFileInfoService;

    private final String fastApiBaseUrl = "http://192.168.31.155:7000"; // 替换为实际 IP
    private static final String UPLOAD_DIR = "/home/imgfask/dxf_to_contour_map/";
//    private static final String UPLOAD_DIR = "F:\\download\\";
    @PostMapping(value = "/upload")
    @ApiOperation(value = "文件上传接口", notes = "文件上传接口")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "file", value = "文件", required = true, dataType = "file"),
            @ApiImplicitParam(name = "bucketName", value = "桶名称", required = false, dataType = "String"),
            @ApiImplicitParam(name = "isTemplate", value = "是否是导入模版：0否，1是，默认否", required = false, dataType = "String")
    })
    public R<SysFileInfo> fileUpload(@RequestParam(value = "file") MultipartFile file,
                                     @RequestParam(value = "bucketName", required = false) String bucketName,
                                     @RequestParam(value = "isTemplate", required = false) String isTemplate) throws IOException {
        SysFileInfo in = sysFileInfoService.upload(file, bucketName, isTemplate);
        String currentDir = System.getProperty("user.dir");

// 构造保存路径（当前目录/static/文件名）
        String tempPath = currentDir + File.separator + "static" + File.separator + file.getOriginalFilename();

// 确保 static 目录存在
        File staticDir = new File(currentDir + File.separator + "static");
        if (!staticDir.exists()) {
            staticDir.mkdirs();
        }

// 保存文件
        File tempFile = new File(tempPath);
        file.transferTo(tempFile);
        // 2. 调用上传服务
//        ExternalFileUploadService service = new ExternalFileUploadService();
        String result = uploadFileToExternalServer(tempPath);

        return R.ok(in);
    }

    public String uploadFileToExternalServer(String filePath) {
        String url = fastApiBaseUrl+"/upload"; // 外部接口地址

        // 1. 构建文件资源
        File file = new File(filePath);
        if (!file.exists()) {
            return "文件不存在";
        }

        FileSystemResource resource = new FileSystemResource(file);

        // 2. 构建请求体
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", resource); // 外部接口字段是 file，如果不是请改成实际字段名

        // 3. 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 4. 发送请求
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        return response.getBody(); // 返回结果
    }

    @ApiOperation(value = "根据文件id查询文件信息接口", notes = "根据文件id查询文件信息接口")
    @ApiImplicitParam(name = "fileId", value = "文件id", required = true, dataType = "long")
    @GetMapping(value = "/getByFileId")
    public R<SysFileInfo> getByFileId(@RequestParam(value = "fileId") Long fileId) {
        return R.ok(sysFileInfoService.getById(fileId));
    }

    @ApiOperation(value = "批量文件id逻辑删除接口", notes = "批量文件id逻辑删除接口")
    @ApiImplicitParam(name = "fileIds", value = "文件id", required = true, dataType = "long[]")
    @GetMapping(value = "/batchLogicalDelete")
    public R<SysFileInfo> batchLogicalDelete(@RequestParam(value = "fileIds") Long[] fileIds) {
        sysFileInfoService.batchLogicalDelete(fileIds);
        return R.ok();
    }

    @ApiOperation(value = "批量文件id物理删除接口", notes = "批量文件id物理删除接口")
    @ApiImplicitParam(name = "fileIds", value = "文件id", required = true, dataType = "long[]")
    @GetMapping(value = "/batchDelete")
    public R<SysFileInfo> batchDelete(@RequestParam(value = "fileIds") Long[] fileIds) {
        sysFileInfoService.batchDelete(fileIds);
        return R.ok();
    }
}
