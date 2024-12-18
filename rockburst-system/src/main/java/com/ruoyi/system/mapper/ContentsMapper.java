package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.Entity.ContentsEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author: shikai
 * @date: 2024/12/7
 * @description:
 */

@Mapper
public interface ContentsMapper extends BaseMapper<ContentsEntity> {

    List<ContentsEntity> findAllByIdRecursive(Long contentsId);

    /**
     * 获取树形结构
     * @return 返回结构
     */
    List<ContentsEntity> findAllContents();

    /**
     * 递归查询子目录（包含自身）
     */
    List<ContentsEntity> findAllByContentsIdRecursive(Long contentsId);
}