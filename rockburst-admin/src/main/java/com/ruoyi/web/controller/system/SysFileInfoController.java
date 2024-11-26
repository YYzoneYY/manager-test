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

import com.google.common.collect.HashMultimap;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.config.CustomMinioClient;
import com.ruoyi.common.config.MinioConfig;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.SysFileInfo;
import com.ruoyi.system.service.SysFileInfoService;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.UploadPartResponse;
import io.minio.errors.*;
import io.minio.http.Method;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping(value = "/fileInfo")
@Api(tags = "文件模块")
public class SysFileInfoController {
    @Resource
    private SysFileInfoService sysFileInfoService;
    @Resource
    private MinioClient minioClient;


    @Resource
    private MinioConfig minioConfig;

    static final long CHUNK_SIZE = 5 * 1024 * 1024;

    @Anonymous
    @ApiOperation("chunk")
    @PostMapping("/chunk")
    public ResponseEntity<String> uploadChunk(@RequestParam("file") MultipartFile file,
                                              @RequestParam("chunkIndex") int chunkIndex,
                                              @RequestParam("totalChunks") int totalChunks,
                                              @RequestParam("fileName") String fileName) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

//        try {
        CustomMinioClient customMinioClient = new CustomMinioClient(minioClient);
        String contentType = "application/octet-stream";
        HashMultimap<String, String> headers = HashMultimap.create();
        headers.put("Content-Type", contentType);
        String uploadId = customMinioClient.initMultiPartUpload(minioConfig.getDefaultBucketName(), null, fileName, headers, null);
        int bytesRead = (int)file.getSize();
        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("uploadId", uploadId);
        reqParams.put("partNumber", String.valueOf(chunkIndex));
        String uploadUrl = customMinioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.PUT)
                        .bucket(minioConfig.getDefaultBucketName())
                        .object(fileName)
                        .expiry(1, TimeUnit.DAYS)
                        .extraQueryParams(reqParams)
                        .build());
            processChunk(file, chunkIndex,bytesRead, uploadId,fileName);
            return ResponseEntity.ok(uploadUrl);

    }


    private void processChunk(MultipartFile file,int chunkIndex, int bytesRead, String uploadId,String fileName) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, XmlParserException, InvalidResponseException, InternalException {
        CustomMinioClient customMinioClient = new CustomMinioClient(minioClient);
        // 可控制并发数和分片大小以防止OOM
        byte[] buffer = new byte[bytesRead];
            String contentType = "application/octet-stream";
            HashMultimap<String, String> headers = HashMultimap.create();
            headers.put("Content-Type", contentType);
            UploadPartResponse uploadPartResponse = customMinioClient.uploadMultiPart(minioConfig.getDefaultBucketName(), null, fileName,
                    file.getBytes(), bytesRead,
                    uploadId, chunkIndex, headers, null);
            System.out.println("chunk[" + chunkIndex + "] buffer size: [" + buffer.length + " Byte] upload etag: [" + uploadPartResponse.etag() + "]");

    }

//    @Anonymous
//    @ApiOperation("merge")
//    @PostMapping("/merge")
//    public ResponseEntity<String> mergeChunks(@RequestParam("fileName") String fileName,
//                                              @RequestParam("uploadId") String uploadId,
//                                              @RequestParam("totalChunks") int totalChunks) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
//        CustomMinioClient customMinioClient = new CustomMinioClient(minioClient);
//        ObjectWriteResponse objectWriteResponse = customMinioClient.mergeMultipartUpload(minioConfig.getDefaultBucketName(), null,fileName, uploadId, parts, null, null);
//        return ResponseEntity.ok("File merged successfully");
//
//
//    }


    @PostMapping(value = "/upload")
    @ApiOperation(value = "文件上传接口", notes = "文件上传接口")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "file", value = "文件", required = true, dataType = "file"),
            @ApiImplicitParam(name = "bucketName", value = "桶名称", required = false, dataType = "String"),
            @ApiImplicitParam(name = "isTemplate", value = "是否是导入模版：0否，1是，默认否", required = false, dataType = "String")
    })
    public R<SysFileInfo> fileUpload(@RequestParam(value = "file") MultipartFile file,
                                     @RequestParam(value = "bucketName", required = false) String bucketName,
                                     @RequestParam(value = "isTemplate", required = false) String isTemplate) {
        return R.ok(sysFileInfoService.upload(file, bucketName, isTemplate));
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
