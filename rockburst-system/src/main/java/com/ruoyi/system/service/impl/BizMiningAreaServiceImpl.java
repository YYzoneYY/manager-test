package com.ruoyi.system.service.impl;

import java.util.Arrays;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.domain.BizMine;
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.domain.dto.BizMiningAreaDto;
import com.ruoyi.system.domain.vo.BizMiningAreaVo;
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
     * 查询采区管理列表
     * 
     * @param bizMiningArea 采区管理
     * @return 采区管理
     */
    @Override
    public MPage<BizMiningAreaVo> selectBizMiningAreaList(BizMiningAreaDto bizMiningArea, Pagination pagination)
    {
        MPJLambdaWrapper<BizMiningArea> queryWrapper = new MPJLambdaWrapper<BizMiningArea>();
        queryWrapper
                .selectAll(BizMiningArea.class)
                .selectAs(BizMine::getMineName,BizMiningAreaVo::getMineName)
                .like(StrUtil.isNotEmpty(bizMiningArea.getMiningAreaName()), BizMiningArea::getMiningAreaName, bizMiningArea.getMiningAreaName())
                .leftJoin(BizMine.class,BizMine::getMineId,BizMiningArea::getMineId)
                .eq(BizMiningArea::getDelFlag,BizBaseConstant.DELFLAG_N)
                .eq(bizMiningArea.getMineId() != null,BizMiningArea::getMineId, bizMiningArea.getMineId());
        IPage<BizMiningAreaVo> list =  bizMiningAreaMapper.selectJoinPage(pagination,BizMiningAreaVo.class,queryWrapper);
        return new MPage<>(list);
    }


    /**
     * 查询采区管理
     *
     * @param miningAreaId 采区管理主键
     * @return 采区管理
     */
    @Override
    public BizMiningAreaVo selectBizMiningAreaByMiningAreaId(Long miningAreaId)
    {
        MPJLambdaWrapper<BizMiningArea> queryWrapper = new MPJLambdaWrapper<BizMiningArea>();
        queryWrapper
                .leftJoin(BizMine.class,BizMine::getMineId,BizMiningArea::getMineId)
                .selectAll(BizMiningArea.class)
                .selectAs(BizMine::getMineName,BizMiningAreaVo::getMineName)
                .eq(BizMiningArea::getDelFlag, BizBaseConstant.DELFLAG_N)
                .eq(BizMiningArea::getMiningAreaId,miningAreaId);
        return bizMiningAreaMapper.selectJoinOne(BizMiningAreaVo.class,queryWrapper);
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
