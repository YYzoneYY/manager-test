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

package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@ApiModel("文件信息表")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_file_info")
public class SysFileInfo extends BusinessBaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty("文件id")
    @TableId(value = "file_id", type = IdType.AUTO)
    private Long fileId;

    @ApiModelProperty(value = "原文件名")
	@TableField(value = "file_old_name")
    private String fileOldName;

    @ApiModelProperty(value = "新文件名")
	@TableField(value = "file_new_name")
    private String fileNewName;

    @ApiModelProperty(value = "文件大小")
	@TableField(value = "file_size")
    private Long fileSize;

    @ApiModelProperty(value = "文件url")
	@TableField(value = "file_url")
    private String fileUrl;

    @ApiModelProperty(value = "文件路径")
	@TableField(value = "file_path")
    private String filePath;

    @ApiModelProperty(value = "文件后缀")
	@TableField(value = "file_suffix")
    private String fileSuffix;

    @ApiModelProperty(value = "文件所在桶名称")
	@TableField(value = "bucket_name")
    private String bucketName;

    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
	@TableLogic(value = "0", delval = "2")
	@TableField("del_flag")
    private String delFlag;
}
