package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.ruoyi.system.domain.Entity.ClassesEntity;
import com.ruoyi.system.domain.dto.ClassesSelectDTO;
import com.ruoyi.system.domain.vo.ClassesVO;
import org.springframework.stereotype.Repository;

/**
 * @author: shikai
 * @date: 2024/11/16
 * @description:
 */

@Repository
public interface ClassesMapper extends BaseMapper<ClassesEntity> {

    Page<ClassesVO> selectClassesList(ClassesSelectDTO classesSelectDTO);
}