package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.ruoyi.system.domain.Entity.ConstructDocumentEntity;
import com.ruoyi.system.domain.dto.DropDownListDTO;
import com.ruoyi.system.domain.dto.SelectDocumentDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/19
 * @description:
 */

@Mapper
public interface ConstructDocumentMapper extends BaseMapper<ConstructDocumentEntity> {

    Page<ConstructDocumentEntity> queryByPage();

    List<DropDownListDTO> selectDropDownList();
}