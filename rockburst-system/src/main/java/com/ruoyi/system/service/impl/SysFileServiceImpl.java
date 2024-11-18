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

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;

import com.amazonaws.services.s3.model.S3Object;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

//import com.google.common.net.HttpHeaders;

import com.ruoyi.common.core.FileProperties;
import com.ruoyi.common.core.FileTemplate;
import com.ruoyi.common.core.OssTemplate;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.SysFileItem;
import com.ruoyi.system.mapper.SysFileMapper;
import com.ruoyi.system.service.SysFileService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.apache.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
//import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

/**
 * 文件管理
 *
 * @author Luckly
 * @date 2019-06-18 17:18:42
 */
@Slf4j
@Service
@AllArgsConstructor
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFileItem> implements SysFileService {

	private final FileTemplate fileTemplate;

//	private final SysFileGroupMapper fileGroupMapper;

	private final FileProperties properties;

	private final OssTemplate ossTemplate;



	/**
	 * 上传文件
	 *
	 * @param file
	 * @param groupId
	 * @param type
	 * @return
	 */
	@Override
	public R uploadFile(MultipartFile file, Long groupId, String type) {
		String fileName = IdUtil.simpleUUID() + StrUtil.DOT + FileUtil.extName(file.getOriginalFilename());
		Map<String, Object> resultMap = new HashMap<>(4);
		resultMap.put("bucketName", properties.getBucketName());
		resultMap.put("fileName", fileName);
		String originalFilename = new String(
				Objects.requireNonNull(file.getOriginalFilename()).getBytes(StandardCharsets.ISO_8859_1),
				StandardCharsets.UTF_8);
		resultMap.put("original", originalFilename);
		resultMap.put("url", String.format("/admin/sys-file/%s/%s", properties.getBucketName(), fileName));

		try (InputStream inputStream = file.getInputStream()) {
			fileTemplate.putObject(properties.getBucketName(), fileName, inputStream, file.getContentType());
			// 文件管理数据记录,收集管理追踪文件
			SysFileItem sysFileItem = fileLog(file, fileName, groupId, type);
			resultMap.put("id", sysFileItem.getFileId());
			resultMap.put("type", sysFileItem.getType());
			resultMap.put("fileSize", sysFileItem.getFileSize());
		} catch (Exception e) {
			log.error("上传失败", e);
			return R.fail(e.getLocalizedMessage());
		}

		return R.ok(resultMap);
	}







	/**
	 * 读取文件
	 *
	 * @param bucket
	 * @param fileName
	 * @param response
	 */
	@Override
	public void getFile(String bucket, String fileName, HttpServletResponse response) {
		try (S3Object s3Object = fileTemplate.getObject(bucket, fileName)) {
			response.setContentType("application/octet-stream; charset=UTF-8");
			IoUtil.copy(s3Object.getObjectContent(), response.getOutputStream());
		} catch (Exception e) {
			log.error("文件读取异常: {}", e.getLocalizedMessage());
		}
	}

	@Override
	public byte[] getStreamInner(Long fileId) {
		SysFileItem fileLog = this.getById(fileId);
		S3Object s3Object = fileTemplate.getObject(fileLog.getBucketName(), fileLog.getFileName());
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		IoUtil.copy(s3Object.getObjectContent(), outputStream);
		byte[] resultByteArray = outputStream.toByteArray();
		return resultByteArray;
//		return outputStream;
	}



	@Override
	public List<SysFileItem> getFileChildById(Long fileId) {

		QueryWrapper<SysFileItem> queryWrapper = new QueryWrapper<SysFileItem>();
		queryWrapper.eq("org_file_id", fileId);
		List<SysFileItem> list =  this.baseMapper.selectList(queryWrapper);
		return list;
	}


	@Override
	public void getFileShow(String bucket, String fileName, HttpServletResponse response) {
		try (S3Object s3Object = fileTemplate.getObject(bucket, fileName)) {
			response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);
			IoUtil.copy(s3Object.getObjectContent(), response.getOutputStream());
		} catch (Exception e) {
			log.error("文件读取异常: {}", e.getLocalizedMessage());
		}
	}


	/**
	 * 删除文件
	 *
	 * @param id
	 * @return
	 */
	@Override
	@SneakyThrows
	@Transactional(rollbackFor = Exception.class)
	public Boolean deleteFile(Long id) {
		SysFileItem file = this.getById(id);
		if (Objects.isNull(file)) {
			return Boolean.FALSE;
		}
		fileTemplate.removeObject(properties.getBucketName(), file.getFileName());
		return this.removeById(id);
	}
//
//	@Override
//	public List<Tree<Long>> listFileGroup(SysFileGroup fileGroup) {
//		// 从数据库查询文件组列表
//		List<TreeNode<Long>> treeNodeList = fileGroupMapper.selectList(Wrappers.query(fileGroup)).stream()
//				.map(group -> {
//					TreeNode<Long> treeNode = new TreeNode<>();
//					treeNode.setName(group.getName());
//					treeNode.setId(group.getId());
//					treeNode.setParentId(group.getPid());
//					return treeNode;
//				}).collect(Collectors.toList());
//
//		// 构建树形结构
//		List<Tree<Long>> treeList = TreeUtil.build(treeNodeList, CommonConstants.MENU_TREE_ROOT_ID);
//		return CollUtil.isEmpty(treeList) ? new ArrayList<>() : treeList;
//	}
//
//	/**
//	 * 查询文件组列表
//	 *
//	 * @param fileGroup SysFileGroup对象，用于筛选条件
//	 * @return 包含文件组树形结构列表的List对象
//	 */
//	@Override
//	public List<Tree<Long>> listFileGroup(SysFileGroup fileGroup) {
//		// 从数据库查询文件组列表
//		List<TreeNode<Long>> treeNodeList = fileGroupMapper.selectList(Wrappers.query(fileGroup)).stream()
//				.map(group -> {
//					TreeNode<Long> treeNode = new TreeNode<>();
//					treeNode.setName(group.getName());
//					treeNode.setId(group.getId());
//					treeNode.setParentId(group.getPid());
//					return treeNode;
//				}).collect(Collectors.toList());
//
//		// 构建树形结构
//		List<Tree<Long>> treeList = TreeUtil.build(treeNodeList, CommonConstants.MENU_TREE_ROOT_ID);
//		return CollUtil.isEmpty(treeList) ? new ArrayList<>() : treeList;
//	}
//
//	/**
//	 * 添加或更新文件组
//	 *
//	 * @param fileGroup SysFileGroup对象，要添加或更新的文件组信息
//	 * @return 添加或更新成功返回true，否则返回false
//	 */
//	@Override
//	public Boolean saveOrUpdateGroup(SysFileGroup fileGroup) {
//		if (Objects.isNull(fileGroup.getId())) {
//			// 插入文件组
//			fileGroupMapper.insert(fileGroup);
//		} else {
//			// 更新文件组
//			fileGroupMapper.updateById(fileGroup);
//		}
//		return Boolean.TRUE;
//	}
//
//	/**
//	 * 删除文件组
//	 *
//	 * @param id 待删除文件组的ID
//	 * @return 删除成功返回true，否则返回false
//	 */
//	@Override
//	public Boolean deleteGroup(Long id) {
//		// 根据ID删除文件组
//		fileGroupMapper.deleteById(id);
//		return Boolean.TRUE;
//	}
//
//	@Override
//	public Boolean moveFileGroup(SysFileGroupDTO fileGroupDTO) {
//		// 创建SysFile对象并设置groupId属性
//		SysFile file = new SysFile();
//		file.setGroupId(fileGroupDTO.getGroupId());
//
//		// 根据IDS更新对应的SysFile记录
//		baseMapper.update(file, Wrappers.<SysFile>lambdaQuery().in(SysFile::getId, fileGroupDTO.getIds()));
//		return Boolean.TRUE;
//	}
//	/**
//	 * 移动文件组
//	 *
//	 * @param fileGroupDTO SysFileGroupDTO对象，要移动的文件组信息
//	 * @return 移动成功返回true，否则返回false
//	 */
//	@Override
//	public Boolean moveFileGroup(SysFileGroupDTO fileGroupDTO) {
//		// 创建SysFile对象并设置groupId属性
//		SysFile file = new SysFile();
//		file.setGroupId(fileGroupDTO.getGroupId());
//
//		// 根据IDS更新对应的SysFile记录
//		baseMapper.update(file, Wrappers.<SysFile>lambdaQuery().in(SysFile::getId, fileGroupDTO.getIds()));
//		return Boolean.TRUE;
//	}




//
//	@Override
//	public String getFileUrl(String bucket, String fileName) {
//		return ossTemplate.getObjectURL(bucket, fileName, 7);
//	}



	/**
	 * 文件管理数据记录，收集管理追踪文件
	 *
	 * @param file     上传的文件格式
	 * @param fileName 文件名
	 * @param groupId  文件组ID
	 * @param type     文件类型
	 */
	private SysFileItem fileLog(MultipartFile file, String fileName, Long groupId, String type) {
		// 创建SysFile对象并设置相关属性
		SysFileItem sysFileItem = new SysFileItem();
		sysFileItem.setFileName(fileName);
		// 对原始文件名进行编码转换
		String originalFilename = new String(
				Objects.requireNonNull(file.getOriginalFilename()).getBytes(StandardCharsets.ISO_8859_1),
				StandardCharsets.UTF_8);
		sysFileItem.setOriginal(originalFilename);
		sysFileItem.setFileSize(file.getSize());
		sysFileItem.setBucketName(properties.getBucketName());
		sysFileItem.setType(type);
		sysFileItem.setGroupId(groupId);
		// 调用save方法保存SysFile对象
		this.save(sysFileItem);
		return sysFileItem;
	}



}
