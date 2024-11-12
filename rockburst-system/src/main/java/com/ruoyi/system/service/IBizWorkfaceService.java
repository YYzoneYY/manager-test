package com.ruoyi.system.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.BizWorkfaceDto;

/**
 * 工作面管理Service接口
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
public interface IBizWorkfaceService  extends IService<BizWorkface>
{
    /**
     * 查询工作面管理
     * 
     * @param workfaceId 工作面管理主键
     * @return 工作面管理
     */
    public BizWorkface selectBizWorkfaceByWorkfaceId(Long workfaceId);

    /**
     * 查询工作面管理列表
     * 
     * @param bizWorkface 工作面管理
     * @return 工作面管理集合
     */
    public List<BizWorkface> selectBizWorkfaceList(BizWorkfaceDto bizWorkface);

    /**
     * 新增工作面管理
     * 
     * @param bizWorkface 工作面管理
     * @return 结果
     */
    public int insertBizWorkface(BizWorkface bizWorkface);

    /**
     * 修改工作面管理
     * 
     * @param bizWorkface 工作面管理
     * @return 结果
     */
    public int updateBizWorkface(BizWorkface bizWorkface);

    /**
     * 批量删除工作面管理
     * 
     * @param workfaceIds 需要删除的工作面管理主键集合
     * @return 结果
     */
    public int deleteBizWorkfaceByWorkfaceIds(Long[] workfaceIds);

    /**
     * 删除工作面管理信息
     * 
     * @param workfaceId 工作面管理主键
     * @return 结果
     */
    public int deleteBizWorkfaceByWorkfaceId(Long workfaceId);
}
