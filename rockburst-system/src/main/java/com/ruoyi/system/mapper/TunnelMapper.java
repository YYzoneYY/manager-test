package com.ruoyi.system.mapper;

import com.github.pagehelper.Page;
import com.github.yulichang.base.MPJBaseMapper;
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
public interface TunnelMapper extends MPJBaseMapper<TunnelEntity> {

    /**
     * 分页查询
     */
    Page<TunnelVO> selectTunnelList(SelectTunnelDTO selectTunnelDTO);
}