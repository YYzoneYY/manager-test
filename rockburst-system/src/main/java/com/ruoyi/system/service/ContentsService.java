package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.Entity.ContentsEntity;

import java.util.List;

/**
 * @author: shikai
 * @date: 2024/12/8
 * @description:
 */
public interface ContentsService extends IService<ContentsEntity> {

    /**
     * 新增目录
     * @param contentsEntity 参数实体类
     * @return 返回结果
     */
    int addContents(ContentsEntity contentsEntity);

    /**
     * 修改目录
     * @param contentsEntity 参数实体类
     * @return 返回参数
     */
    int updateContents(ContentsEntity contentsEntity);

    boolean deleteById(Long[] contentsIds);

    List<Long> queryByCondition(Long contentsId);
}