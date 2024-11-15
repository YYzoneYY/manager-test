package com.ruoyi.system.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.BizMine;
import com.ruoyi.system.domain.dto.BizMineDto;

/**
 * 矿井管理Service接口
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
public interface IBizMineService extends IService<BizMine>
{
    /**
     * 查询矿井管理
     * 
     * @param mineId 矿井管理主键
     * @return 矿井管理
     */
    public BizMine selectBizMineByMineId(Long mineId);

    /**
     * 查询矿井管理列表
     * 
     * @param bizMine 矿井管理
     * @return 矿井管理集合
     */
    public MPage<BizMine> selectBizMineList(BizMineDto bizMine, Pagination pagination);

    /**
     * 新增矿井管理
     * 
     * @param bizMine 矿井管理
     * @return 结果
     */
    public int insertBizMine(BizMine bizMine);

    /**
     * 修改矿井管理
     * 
     * @param bizMine 矿井管理
     * @return 结果
     */
    public int updateBizMine(BizMine bizMine);

    /**
     * 批量删除矿井管理
     * 
     * @param mineIds 需要删除的矿井管理主键集合
     * @return 结果
     */
    public int deleteBizMineByMineIds(Long[] mineIds);

    /**
     * 删除矿井管理信息
     * 
     * @param mineId 矿井管理主键
     * @return 结果
     */
    public int deleteBizMineByMineId(Long mineId);
}
