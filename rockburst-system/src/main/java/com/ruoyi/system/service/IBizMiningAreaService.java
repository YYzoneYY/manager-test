package com.ruoyi.system.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.BizMiningArea;
import com.ruoyi.system.domain.dto.BizMiningAreaDto;
import com.ruoyi.system.domain.vo.BizMiningAreaVo;

/**
 * 采区管理Service接口
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
public interface IBizMiningAreaService extends IService<BizMiningArea>
{
    /**
     * 查询采区管理
     * 
     * @param miningAreaId 采区管理主键
     * @return 采区管理
     */
    public BizMiningAreaVo selectBizMiningAreaByMiningAreaId(Long miningAreaId);

    /**
     * 查询采区管理列表
     * 
     * @param bizMiningArea 采区管理
     * @return 采区管理集合
     */
     MPage<BizMiningAreaVo> selectBizMiningAreaList(BizMiningAreaDto bizMiningArea, Pagination pagination);

    /**
     * 新增采区管理
     * 
     * @param bizMiningArea 采区管理
     * @return 结果
     */
    public int insertBizMiningArea(BizMiningArea bizMiningArea);

    /**
     * 修改采区管理
     * 
     * @param bizMiningArea 采区管理
     * @return 结果
     */
    public int updateBizMiningArea(BizMiningArea bizMiningArea);

    /**
     * 批量删除采区管理
     * 
     * @param miningAreaIds 需要删除的采区管理主键集合
     * @return 结果
     */
    public int deleteBizMiningAreaByMiningAreaIds(Long[] miningAreaIds);

    /**
     * 删除采区管理信息
     * 
     * @param miningAreaId 采区管理主键
     * @return 结果
     */
    public int deleteBizMiningAreaByMiningAreaId(Long miningAreaId);
}
