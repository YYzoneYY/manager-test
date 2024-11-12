package com.ruoyi.system.service.impl;

import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.mapper.BizProjectRecordMapper;
import com.ruoyi.system.service.IBizProjectRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.BizMiningAreaMapper;
import com.ruoyi.system.domain.BizMiningArea;
import com.ruoyi.system.service.IBizMiningAreaService;

/**
 * 采区管理Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
@Service
public class BizMiningAreaServiceImpl  extends ServiceImpl<BizMiningAreaMapper, BizMiningArea> implements IBizMiningAreaService
{
    @Autowired
    private BizMiningAreaMapper bizMiningAreaMapper;

    /**
     * 查询采区管理
     * 
     * @param miningAreaId 采区管理主键
     * @return 采区管理
     */
    @Override
    public BizMiningArea selectBizMiningAreaByMiningAreaId(Long miningAreaId)
    {
        return bizMiningAreaMapper.selectById(miningAreaId);
    }

    /**
     * 查询采区管理列表
     * 
     * @param bizMiningArea 采区管理
     * @return 采区管理
     */
    @Override
    public List<BizMiningArea> selectBizMiningAreaList(BizMiningArea bizMiningArea)
    {
        QueryWrapper<BizMiningArea> queryWrapper = new QueryWrapper<BizMiningArea>();
        queryWrapper.lambda().like(BizMiningArea::getMiningAreaName, bizMiningArea.getMiningAreaName())
                .eq(bizMiningArea.getMineId() != null,BizMiningArea::getMineId, bizMiningArea.getMineId());
        return bizMiningAreaMapper.selectList(queryWrapper);
    }

    /**
     * 新增采区管理
     * 
     * @param bizMiningArea 采区管理
     * @return 结果
     */
    @Override
    public int insertBizMiningArea(BizMiningArea bizMiningArea)
    {
        return bizMiningAreaMapper.insert(bizMiningArea);
    }

    /**
     * 修改采区管理
     * 
     * @param bizMiningArea 采区管理
     * @return 结果
     */
    @Override
    public int updateBizMiningArea(BizMiningArea bizMiningArea)
    {
        return bizMiningAreaMapper.updateById(bizMiningArea);
    }

    /**
     * 批量删除采区管理
     * 
     * @param miningAreaIds 需要删除的采区管理主键
     * @return 结果
     */
    @Override
    public int deleteBizMiningAreaByMiningAreaIds(Long[] miningAreaIds)
    {
        return bizMiningAreaMapper.deleteBatchIds(Arrays.asList(miningAreaIds));
    }

    /**
     * 删除采区管理信息
     * 
     * @param miningAreaId 采区管理主键
     * @return 结果
     */
    @Override
    public int deleteBizMiningAreaByMiningAreaId(Long miningAreaId)
    {
        return bizMiningAreaMapper.deleteById(miningAreaId);
    }
}
