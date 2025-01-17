package com.ruoyi.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.domain.BizMine;
import com.ruoyi.system.domain.BizMiningArea;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.dto.BizWorkfaceDto;
import com.ruoyi.system.domain.vo.BizWorkfaceVo;
import com.ruoyi.system.mapper.BizWorkfaceMapper;
import com.ruoyi.system.service.IBizWorkfaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public BizWorkfaceVo selectBizWorkfaceByWorkfaceId(Long workfaceId)
    {
        MPJLambdaWrapper<BizWorkface> queryWrapper = new MPJLambdaWrapper<BizWorkface>();
        queryWrapper
                .leftJoin(BizMine.class, BizMine::getMineId,BizWorkfaceVo::getMineId)
                .leftJoin(BizMiningArea.class, BizMiningArea::getMiningAreaId,BizWorkface::getMiningAreaId)
                .selectAll(BizWorkface.class)
                .selectAs(BizMine::getMineName,BizWorkfaceVo::getMineName)
                .selectAs(BizMiningArea::getMiningAreaName,BizWorkfaceVo::getMiningAreaName)
                .eq(BizWorkface::getWorkfaceId,workfaceId)
                .eq(BizWorkface::getDelFlag, BizBaseConstant.DELFLAG_N);
        return bizWorkfaceMapper.selectJoinOne(BizWorkfaceVo.class,queryWrapper);
    }

    /**
     * 查询工作面管理列表
     *
     * @param dto 工作面管理
     * @return 工作面管理
     */
    @Override
    public MPage<BizWorkfaceVo> selectBizWorkfaceList(BizWorkfaceDto dto, Pagination pagination)
    {
        MPJLambdaWrapper<BizWorkface> queryWrapper = new MPJLambdaWrapper<BizWorkface>();
        queryWrapper
                .leftJoin(BizMine.class, BizMine::getMineId,BizWorkfaceVo::getMineId)
                .leftJoin(BizMiningArea.class, BizMiningArea::getMiningAreaId,BizWorkface::getMiningAreaId)
                .selectAll(BizWorkface.class)
                .selectAs(BizMine::getMineName,BizWorkfaceVo::getMineName)
                .selectAs(BizMiningArea::getMiningAreaName,BizWorkfaceVo::getMiningAreaName)
                .eq(dto.getMineId() != null,BizWorkface::getMineId,dto.getMineId())
                .eq(dto.getMiningAreaId()!= null, BizWorkface::getMiningAreaId, dto.getMiningAreaId())
                .like(StrUtil.isNotEmpty(dto.getWorkfaceName()), BizWorkface::getWorkfaceName, dto.getWorkfaceName())
                .eq(StrUtil.isNotEmpty(dto.getType()), BizWorkface::getType, dto.getType())
                .eq(BizWorkface::getDelFlag, BizBaseConstant.DELFLAG_N)
                .eq(dto.getStatus()!= null, BizWorkface::getStatus, dto.getStatus());
        IPage<BizWorkfaceVo> list = bizWorkfaceMapper.selectJoinPage(pagination,BizWorkfaceVo.class,queryWrapper);
        return new MPage<>(list);
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

        return bizWorkfaceMapper.insert(bizWorkface);
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
