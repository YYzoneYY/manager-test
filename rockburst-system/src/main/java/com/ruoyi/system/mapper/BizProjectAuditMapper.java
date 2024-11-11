package com.ruoyi.system.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.BizDrillRecord;
import com.ruoyi.system.domain.BizProjectAudit;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工程填报审核记录Mapper接口
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Mapper
public interface BizProjectAuditMapper  extends BaseMapper<BizProjectAudit>
{
    /**
     * 查询工程填报审核记录
     * 
     * @param projectAuditId 工程填报审核记录主键
     * @return 工程填报审核记录
     */
//    public BizProjectAudit selectBizProjectAuditByProjectAuditId(Long projectAuditId);

    /**
     * 查询工程填报审核记录列表
     * 
     * @param bizProjectAudit 工程填报审核记录
     * @return 工程填报审核记录集合
     */
//    public List<BizProjectAudit> selectBizProjectAuditList(BizProjectAudit bizProjectAudit);

    /**
     * 新增工程填报审核记录
     * 
     * @param bizProjectAudit 工程填报审核记录
     * @return 结果
     */
//    public int insertBizProjectAudit(BizProjectAudit bizProjectAudit);

    /**
     * 修改工程填报审核记录
     * 
     * @param bizProjectAudit 工程填报审核记录
     * @return 结果
     */
//    public int updateBizProjectAudit(BizProjectAudit bizProjectAudit);

    /**
     * 删除工程填报审核记录
     * 
     * @param projectAuditId 工程填报审核记录主键
     * @return 结果
     */
//    public int deleteBizProjectAuditByProjectAuditId(Long projectAuditId);

    /**
     * 批量删除工程填报审核记录
     * 
     * @param projectAuditIds 需要删除的数据主键集合
     * @return 结果
     */
//    public int deleteBizProjectAuditByProjectAuditIds(Long[] projectAuditIds);
}
