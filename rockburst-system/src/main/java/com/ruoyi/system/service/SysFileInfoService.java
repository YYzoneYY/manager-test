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

package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.SysFileInfo;
import org.springframework.web.multipart.MultipartFile;


public interface SysFileInfoService extends IService<SysFileInfo> {

    SysFileInfo upload(MultipartFile file, String bucketName, String isTemplate);

    SysFileInfo getById(Long fileId);

    void batchLogicalDelete(Long[] fileIds);

    void batchDelete(Long[] fileIds);


}
