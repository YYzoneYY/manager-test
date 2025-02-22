package com.ruoyi.system.service;

import com.github.yulichang.extension.mapping.base.MPJDeepService;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.dto.BizWorkfaceDto;
import com.ruoyi.system.domain.vo.BizWorkfaceVo;
import com.ruoyi.system.domain.vo.JsonVo;

import java.util.List;

/**
 * 工作面管理Service接口
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
public interface IBizWorkfaceService  extends MPJDeepService<BizWorkface>
{
    /**
     * 查询工作面管理
     * 
     * @param workfaceId 工作面管理主键
     * @return 工作面管理
     */
    public BizWorkfaceVo selectBizWorkfaceByWorkfaceId(Long workfaceId);


    public List<BizWorkfaceVo> selectWorkfaceVoList();


    public List<JsonVo> selectWorkfacejilianList();


    /**
     * 根据规划查询工作面
     * @return
     */
    public MPage<BizWorkface> getWorkfaceBySchemePage(String scheme,String workfaceName,Pagination pagination);


    public List<BizWorkface> getWorkfaceByScheme(String scheme,String workfaceName);

    /**
     * 查询工作面管理列表
     * 
     * @param bizWorkface 工作面管理
     * @return 工作面管理集合
     */
    public MPage<BizWorkfaceVo> selectBizWorkfaceList(BizWorkfaceDto dto, Pagination pagination);

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
