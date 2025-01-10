package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.EqtDisplacement;
import com.ruoyi.system.domain.dto.EqtDisplacementDto;
import com.ruoyi.system.domain.dto.EqtSearchDto;

/**
 * 矿井管理Service接口
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
public interface IEqtDisplacementService extends IService<EqtDisplacement>
{
    /**
     * 查询矿井管理
     * 
     * @param displacementId 矿井管理主键
     * @return 矿井管理
     */
    public EqtDisplacement selectDeepById(Long displacementId);

    /**
     * 查询矿井管理列表
     * 
     * @param dto 矿井管理
     * @return 矿井管理集合
     */
    public MPage<EqtDisplacement> selectPageList(EqtSearchDto dto, Pagination pagination);

    /**
     * 新增矿井管理
     * 
     * @param dto 矿井管理
     * @return 结果
     */
    public int insertEntity(EqtDisplacementDto dto);

    /**
     * 修改矿井管理
     * 
     * @param bizMine 矿井管理
     * @return 结果
     */
    public int updateMById(EqtDisplacementDto dto);

    /**
     * 批量删除矿井管理
     * 
     * @param mineIds 需要删除的矿井管理主键集合
     * @return 结果
     */
    public int deleteMByIds(Long[] displacementIds);

    /**
     * 删除矿井管理信息
     * 
     * @param mineId 管理主键
     * @return 结果
     */
    public int deleteMById(Long displacementId);


    boolean checkMeasureNum(String measureNum,Long id);
}
