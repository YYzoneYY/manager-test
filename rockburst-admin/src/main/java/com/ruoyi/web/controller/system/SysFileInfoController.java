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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
//import net.lingala.zip4j.model.ZipParameters;
//import net.lingala.zip4j.model.enums.AesKeyStrength;
//import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;


@RestController
@RequestMapping(value = "/fileInfo")
@Api(tags = "文件模块")
public class SysFileInfoController {
    @Resource
    private SysFileInfoService sysFileInfoService;


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
