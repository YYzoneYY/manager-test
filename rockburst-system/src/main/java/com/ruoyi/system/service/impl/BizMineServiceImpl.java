package com.ruoyi.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.domain.BizMine;
import com.ruoyi.system.domain.dto.BizMineDto;
import com.ruoyi.system.mapper.BizMineMapper;
import com.ruoyi.system.service.IBizMineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 矿井管理Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
@Service
public class BizMineServiceImpl  extends ServiceImpl<BizMineMapper, BizMine> implements IBizMineService
{
    @Autowired
    private BizMineMapper bizMineMapper;

    /**
     * 查询矿井管理
     * 
     * @param mineId 矿井管理主键
     * @return 矿井管理
     */
    @Override
    public BizMine selectBizMineByMineId(Long mineId)
    {
        return bizMineMapper.selectById(mineId);
    }

    /**
     * 查询矿井管理列表
     * 
     * @param bizMine 矿井管理
     * @return 矿井管理
     */
    @Override
    public MPage<BizMine> selectBizMineList(BizMineDto bizMine, Pagination pagination)
    {
        QueryWrapper<BizMine> queryWrapper = new QueryWrapper<BizMine>();
        queryWrapper.lambda()
                .eq(StrUtil.isNotEmpty(bizMine.getStatus()),BizMine::getStatus,bizMine.getStatus())
                .like(StrUtil.isNotEmpty(bizMine.getMineCode()),BizMine::getMineCode,bizMine.getMineCode())
                .like(StrUtil.isNotEmpty(bizMine.getSocialCreditCode()),BizMine::getSocialCreditCode,bizMine.getSocialCreditCode())
                .like(StrUtil.isNotEmpty( bizMine.getMineName()), BizMine::getMineName, bizMine.getMineName())
                .eq(BizMine::getDelFlag, BizBaseConstant.DELFLAG_N);
        IPage<BizMine> list = bizMineMapper.selectPage(pagination,queryWrapper);
        return new MPage<>(list);
    }

    /**
     * 新增矿井管理
     * 
     * @param bizMine 矿井管理
     * @return 结果
     */
    @Override
    public int insertBizMine(BizMine bizMine)
    {

        return bizMineMapper.insert(bizMine);
    }

    /**
     * 修改矿井管理
     * 
     * @param bizMine 矿井管理
     * @return 结果
     */
    @Override
    public int updateBizMine(BizMine bizMine)
    {

        return bizMineMapper.updateById(bizMine);
    }

    /**
     * 批量删除矿井管理
     * 
     * @param mineIds 需要删除的矿井管理主键
     * @return 结果
     */
    @Override
    public int deleteBizMineByMineIds(Long[] mineIds)
    {
        return bizMineMapper.deleteBatchIds(Arrays.asList(mineIds));
    }

    /**
     * 删除矿井管理信息
     * 
     * @param mineId 矿井管理主键
     * @return 结果
     */
    @Override
    public int deleteBizMineByMineId(Long mineId)
    {
        return bizMineMapper.deleteById(mineId);
    }
}
