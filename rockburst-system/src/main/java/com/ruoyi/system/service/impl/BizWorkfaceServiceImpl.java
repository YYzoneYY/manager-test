package com.ruoyi.system.service.impl;

import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.domain.BizWorkfaceDto;
import com.ruoyi.system.mapper.BizProjectRecordMapper;
import com.ruoyi.system.service.IBizProjectRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.BizWorkfaceMapper;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.service.IBizWorkfaceService;

/**
 * 工作面管理Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
@Service
public class BizWorkfaceServiceImpl  extends ServiceImpl<BizWorkfaceMapper, BizWorkface> implements IBizWorkfaceService
{
    @Autowired
    private BizWorkfaceMapper bizWorkfaceMapper;

    /**
     * 查询工作面管理
     *
     * @param workfaceId 工作面管理主键
     * @return 工作面管理
     */
    @Override
    public BizWorkface selectBizWorkfaceByWorkfaceId(Long workfaceId)
    {
        return bizWorkfaceMapper.selectById(workfaceId);
    }

    /**
     * 查询工作面管理列表
     *
     * @param dto 工作面管理
     * @return 工作面管理
     */
    @Override
    public List<BizWorkface> selectBizWorkfaceList(BizWorkfaceDto dto)
    {
        QueryWrapper<BizWorkface> queryWrapper = new QueryWrapper<BizWorkface>();
        queryWrapper.lambda().eq(dto.getAreaId()!= null, BizWorkface::getAreaId, dto.getAreaId())
                .like(StrUtil.isNotEmpty(dto.getWorkfaceName()), BizWorkface::getWorkfaceName, dto.getWorkfaceName())
                .eq(StrUtil.isNotEmpty(dto.getType()), BizWorkface::getType, dto.getType())
                .eq(dto.getStatus()!= null, BizWorkface::getStatus, dto.getStatus());
        return bizWorkfaceMapper.selectList(queryWrapper);
    }

    /**
     * 新增工作面管理
     *
     * @param bizWorkface 工作面管理
     * @return 结果
     */
    @Override
    public int insertBizWorkface(BizWorkface bizWorkface)
    {
        bizWorkface.setCreateTime(DateUtils.getNowDate());
        return 1;
    }

    /**
     * 修改工作面管理
     *
     * @param bizWorkface 工作面管理
     * @return 结果
     */
    @Override
    public int updateBizWorkface(BizWorkface bizWorkface)
    {
        bizWorkface.setUpdateTime(DateUtils.getNowDate());
        return 1;
    }

    /**
     * 批量删除工作面管理
     *
     * @param workfaceIds 需要删除的工作面管理主键
     * @return 结果
     */
    @Override
    public int deleteBizWorkfaceByWorkfaceIds(Long[] workfaceIds)
    {
        return 1;
    }

    /**
     * 删除工作面管理信息
     *
     * @param workfaceId 工作面管理主键
     * @return 结果
     */
    @Override
    public int deleteBizWorkfaceByWorkfaceId(Long workfaceId)
    {
        return 1;
    }
}
