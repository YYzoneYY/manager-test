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
}