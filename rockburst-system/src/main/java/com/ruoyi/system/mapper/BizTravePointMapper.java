package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.BizTravePoint;
import com.github.yulichang.base.MPJBaseMapper;
import com.ruoyi.system.domain.BizMine;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 矿井管理Mapper接口
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
@Mapper
public interface BizTravePointMapper extends MPJBaseMapper<BizTravePoint>
{

    /**
     * 获取同一巷道，最大的导线点编号
     */
    Integer selectMaxNo(@Param("tunnelId") Long tunnelId);

    /**
     * 获取同一巷道，最小的导线点编号
     */
    Integer selectMinNo(@Param("tunnelId") Long tunnelId);

}
