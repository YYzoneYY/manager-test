package com.ruoyi.system.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.yulichang.base.MPJBaseMapper;
import com.ruoyi.system.domain.BizDrillRecord;
import com.ruoyi.system.domain.BizProjectRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工程填报记录Mapper接口
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Mapper
public interface BizProjectRecordMapper extends MPJBaseMapper<BizProjectRecord>
{
    /**
     * 查询工程填报记录
     * 
     * @param projectId 工程填报记录主键
     * @return 工程填报记录
     */
//    public BizProjectRecord selectBizProjectRecordByProjectId(Long projectId);

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
     * 删除工程填报记录
     * 
     * @param projectId 工程填报记录主键
     * @return 结果
     */
//    public int deleteBizProjectRecordByProjectId(Long projectId);

    /**
     * 批量删除工程填报记录
     * 
     * @param projectIds 需要删除的数据主键集合
     * @return 结果
     */
//    public int deleteBizProjectRecordByProjectIds(Long[] projectIds);
}
