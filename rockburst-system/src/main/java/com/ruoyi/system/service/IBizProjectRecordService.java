package com.ruoyi.system.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.BizDrillRecord;
import com.ruoyi.system.domain.BizProjectRecord;

/**
 * 工程填报记录Service接口
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
public interface IBizProjectRecordService  extends IService<BizProjectRecord>
{
    /**
     * 查询工程填报记录
     * 
     * @param projectId 工程填报记录主键
     * @return 工程填报记录
     */
    List<BizProjectRecord> getlist(BizProjectRecord bizProjectRecord);

    /**
     * 查询工程填报记录列表
     * 
     * @param bizProjectRecord 工程填报记录
     * @return 工程填报记录集合
     */
//    public List<BizProjectRecord> selectBizProjectRecordList(BizProjectRecord bizProjectRecord);

    /**
     * 新增工程填报记录
     * 
     * @param bizProjectRecord 工程填报记录
     * @return 结果
     */
//    public int insertBizProjectRecord(BizProjectRecord bizProjectRecord);

    /**
     * 修改工程填报记录
     * 
     * @param bizProjectRecord 工程填报记录
     * @return 结果
     */
//    public int updateBizProjectRecord(BizProjectRecord bizProjectRecord);

    /**
     * 批量删除工程填报记录
     * 
     * @param projectIds 需要删除的工程填报记录主键集合
     * @return 结果
     */
//    public int deleteBizProjectRecordByProjectIds(Long[] projectIds);

    /**
     * 删除工程填报记录信息
     * 
     * @param projectId 工程填报记录主键
     * @return 结果
     */
//    public int deleteBizProjectRecordByProjectId(Long projectId);
}
