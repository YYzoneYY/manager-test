package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.ruoyi.system.domain.Entity.ClassesEntity;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.SelectTunnelDTO;
import com.ruoyi.system.domain.vo.TunnelVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: shikai
 * @date: 2024/11/19
 * @description:
 */

@Mapper
public interface TunnelMapper extends BaseMapper<TunnelEntity> {

    /**
     * 分页查询
     */
    Page<TunnelVO> selectTunnelList(SelectTunnelDTO selectTunnelDTO);
}