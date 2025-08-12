package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.ruoyi.system.domain.Entity.WarnSchemeEntity;
import com.ruoyi.system.domain.dto.WarnSchemeSelectDTO;
import com.ruoyi.system.domain.vo.WarnSchemeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author: shikai
 * @date: 2024/11/29
 * @description:
 */

@Mapper
public interface WarnSchemeMapper extends BaseMapper<WarnSchemeEntity> {

    Page<WarnSchemeVO> queryPage(@Param("warnSchemeSelectDTO") WarnSchemeSelectDTO warnSchemeSelectDTO,
                                 @Param("mineId") Long mineId);
}