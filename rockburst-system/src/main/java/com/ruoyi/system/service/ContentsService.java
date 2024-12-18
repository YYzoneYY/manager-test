package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.Entity.ContentsEntity;
import com.ruoyi.system.domain.dto.ContentsTreeDTO;

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

    /**
     * 获取所有目录树形结构
     */
    List<ContentsTreeDTO> queryAllContentsTree();

    /**
     * 获取子目录树形结构（包含自己）
     * @return 返回结果
     */
    List<ContentsTreeDTO> getContentsTree(Long contentsId, String contentsName);
}