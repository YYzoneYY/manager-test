package com.ruoyi.system.service.impl;

import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.mapper.BizProjectRecordMapper;
import com.ruoyi.system.service.IBizProjectRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.BizMineMapper;
import com.ruoyi.system.domain.BizMine;
import com.ruoyi.system.service.IBizMineService;

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
    public List<BizMine> selectBizMineList(BizMine bizMine)
    {
        QueryWrapper<BizMine> queryWrapper = new QueryWrapper<BizMine>();
        queryWrapper.lambda().like(BizMine::getMineName, bizMine.getMineName());
        return bizMineMapper.selectListDeep(queryWrapper);
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

        bizMine.setStatus(BizBaseConstant.MINE_STATUS_ON);
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
