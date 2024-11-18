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

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.SysFileItem;
import com.ruoyi.system.service.SysFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
//import net.lingala.zip4j.model.ZipParameters;
//import net.lingala.zip4j.model.enums.AesKeyStrength;
//import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 文件管理
 *
 * @author Luckly
 * @date 2019-06-18 17:18:42
 */
@Api("文件管理")
@RestController
@AllArgsConstructor
@RequestMapping("/sys-file")
@Tag(description = "sys-file", name = "文件管理")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class SysFileController {

	@Autowired
	private final SysFileService sysFileService;

	/**
	 * 分页查询
	 * @param page 分页对象
	 * @param sysFileItem 文件管理
	 * @return
	 */

	@ApiOperation("分页查询")
	@Operation(summary = "分页查询", description = "分页查询")
	@GetMapping("/page")
	public R getSysFilePage(@ParameterObject Page page, @ParameterObject SysFileItem sysFileItem) {
		LambdaQueryWrapper<SysFileItem> wrapper = Wrappers.<SysFileItem>lambdaQuery()
				.eq(StrUtil.isNotBlank(sysFileItem.getType()), SysFileItem::getType, sysFileItem.getType())
				.eq(Objects.nonNull(sysFileItem.getGroupId()), SysFileItem::getGroupId, sysFileItem.getGroupId())
				.like(StrUtil.isNotBlank(sysFileItem.getOriginal()), SysFileItem::getOriginal, sysFileItem.getOriginal());
		return R.ok(sysFileService.page(page, wrapper));
	}

	/**
	 * 通过id删除文件管理
	 * @param ids id 列表
	 * @return R
	 */
	@ApiOperation("通过id删除文件管理")
	@Operation(summary = "通过id删除文件管理", description = "通过id删除文件管理")
	@Log(title = "删除文件管理")
	@DeleteMapping
	@PreAuthorize("@pms.hasPermission('sys_file_del')")
	public R removeById(@RequestBody Long[] ids) {
		for (Long id : ids) {
			sysFileService.deleteFile(id);
		}
		return R.ok();
	}

	/**
	 * 通过id删除文件管理
	 * @param ids id 列表
	 * @return R
	 */
	@ApiOperation("通过id删除文件管理")
	@Operation(summary = "通过id删除文件管理SELF", description = "通过id删除文件管理SELF")
	@Log(title = "删除文件管理")
	@DeleteMapping("/remove")
	public R removeByIdSelf(@RequestBody Long[] ids) {
		for (Long id : ids) {
			sysFileService.deleteFile(id);
		}
		return R.ok();
	}

	@ApiOperation("rename")
	@PutMapping("/rename")
	public R rename(@RequestBody SysFileItem sysFileItem) {
		return R.ok(sysFileService.updateById(sysFileItem));
	}

	/**
	 * 上传文件 文件名采用uuid,避免原始文件名中带"-"符号导致下载的时候解析出现异常
	 * @param file 资源
	 * @return R(/ admin / bucketName / filename)
	 */
	@ApiOperation("上传文件")
	@Operation(description = "上传文件", summary = "上传文件")
	@PostMapping(value = "/upload")
	public R upload(@RequestPart("file") MultipartFile file,
			@RequestParam(value = "groupId", required = false) Long groupId,
			@RequestParam(value = "type", required = false) String type) {
		return sysFileService.uploadFile(file, groupId, type);
	}









	@ApiOperation("获取文件预览")
	@Operation(description = "获取文件预览", summary = "获取文件预览")
	@GetMapping("/getFileShow/{bucket}/{fileName}")
	public void getFileShow(@PathVariable String bucket, @PathVariable String fileName, HttpServletResponse response) {
		sysFileService.getFileShow(bucket, fileName, response);
	}

	/**
	 * 获取本地（resources）文件
	 * @param fileName 文件名称
	 * @param response 本地文件
	 */
	@SneakyThrows
	@ApiOperation("获取本地（resources）文件")
	@GetMapping("/local/file/{fileName}")
	public void localFile(@PathVariable String fileName, HttpServletResponse response) {
		ClassPathResource resource = new ClassPathResource("file/" + fileName);
		response.setContentType("application/octet-stream; charset=UTF-8");
		IoUtil.copy(resource.getInputStream(), response.getOutputStream());
	}

//	/**
//	 * 查询文件组列表
//	 * @param fileGroup SysFileGroup对象，用于筛选条件
//	 * @return 包含文件组列表的R对象
//	 */
//	@ApiOperation("上传文件")
//	@GetMapping("/group/list")
//	public R listGroup(SysFileGroup fileGroup) {
//		return R.ok(sysFileService.listFileGroup(fileGroup));
//	}
//
//	/**
//	 * 添加文件组
//	 * @param fileGroup SysFileGroup对象，要添加的文件组信息
//	 * @return 包含添加结果的R对象
//	 */
//	@ApiOperation("上传文件")
//	@PostMapping("/group/add")
//	public R addGroup(@RequestBody SysFileGroup fileGroup) {
//		return R.ok(sysFileService.saveOrUpdateGroup(fileGroup));
//	}
//
//	/**
//	 * 更新文件组
//	 * @param fileGroup SysFileGroup对象，要更新的文件组信息
//	 * @return 包含更新结果的R对象
//	 */
//	@ApiOperation("上传文件")
//	@PutMapping("/group/update")
//	public R updateGroup(@RequestBody SysFileGroup fileGroup) {
//		return R.ok(sysFileService.saveOrUpdateGroup(fileGroup));
//	}
//
//	/**
//	 * 删除文件组
//	 * @param id 待删除文件组的ID
//	 * @return 包含删除结果的R对象
//	 */
//	@ApiOperation("上传文件")
//	@DeleteMapping("/group/delete/{id}")
//	public R updateGroup(@PathVariable Long id) {
//		return R.ok(sysFileService.deleteGroup(id));
//	}
//
//	/**
//	 * 移动文件组
//	 * @param fileGroupDTO SysFileGroupDTO对象，要移动的文件组信息
//	 * @return 包含移动结果的R对象
//	 */
//	@PutMapping("/group/move")
//	public R moveFileGroup(@RequestBody SysFileGroupDTO fileGroupDTO) {
//		return R.ok(sysFileService.moveFileGroup(fileGroupDTO));
//	}

}
