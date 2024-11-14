package com.ruoyi.system.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.yulichang.base.MPJBaseMapper;
import com.ruoyi.system.domain.BizDrillRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 钻孔参数记录Mapper接口
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Mapper
public interface BizDrillRecordMapper   extends MPJBaseMapper<BizDrillRecord>
{
    /**
     * 查询钻孔参数记录
     * 
     * @param drillRecordId 钻孔参数记录主键
     * @return 钻孔参数记录
     */
//    public BizDrillRecord selectBizDrillRecordByDrillRecordId(Long drillRecordId);

    /**
     * 查询钻孔参数记录列表
     * 
     * @param bizDrillRecord 钻孔参数记录
     * @return 钻孔参数记录集合
     */
//    public List<BizDrillRecord> selectBizDrillRecordList(BizDrillRecord bizDrillRecord);

    /**
     * 新增钻孔参数记录
     * 
     * @param bizDrillRecord 钻孔参数记录
     * @return 结果
     */
//    public int insertBizDrillRecord(BizDrillRecord bizDrillRecord);

    /**
     * 修改钻孔参数记录
     * 
     * @param bizDrillRecord 钻孔参数记录
     * @return 结果
     */
//    public int updateBizDrillRecord(BizDrillRecord bizDrillRecord);

    /**
     * 删除钻孔参数记录
     * 
     * @param drillRecordId 钻孔参数记录主键
     * @return 结果
     */
//    public int deleteBizDrillRecordByDrillRecordId(Long drillRecordId);

    /**
     * 批量删除钻孔参数记录
     * 
     * @param drillRecordIds 需要删除的数据主键集合
     * @return 结果
     */
//    public int deleteBizDrillRecordByDrillRecordIds(Long[] drillRecordIds);
}
