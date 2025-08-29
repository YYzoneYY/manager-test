package com.ruoyi.system.mapper;

import com.github.pagehelper.Page;
import com.github.yulichang.base.MPJBaseMapper;
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.domain.dto.PrintListDTO;
import com.ruoyi.system.domain.dto.ReportFormsDTO;
import com.ruoyi.system.domain.dto.largeScreen.MiddleDTO;
import com.ruoyi.system.domain.dto.largeScreen.ProjectDTO;
import com.ruoyi.system.domain.dto.largeScreen.ProjectTypeDTO;
import com.ruoyi.system.domain.dto.largeScreen.Select1DTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

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

    /**
     * 用于卸压孔报表列表查询
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param drillTypes 钻孔类型
     */
    Page<ReportFormsDTO> queryDateByPage(@Param("startTime") Date startTime,
                                         @Param("endTime") Date endTime,
                                         @Param("drillTypes") List<String> drillTypes);

    List<ProjectDTO> queryProjectOfAudit(Select1DTO select1DTO);

    List<ProjectDTO> queryProjectOfUnaudited(Select1DTO select1DTO);

    List<ProjectTypeDTO> queryProjectType(@Param("startTime") Date startTime,
                                          @Param("endTime") Date endTime);

    List<MiddleDTO> queryProjectCount(@Param("tunnelId") Long tunnelId);

    List<MiddleDTO> queryProjectCountBatch(@Param("tunnelIds") List<Long> tunnelIds);

    Page<PrintListDTO> queryPrintList(@Param("startTime") Date startTime,
                                      @Param("endTime") Date endTime,
                                      @Param("drillNum") String drillNum,
                                      @Param("mineId") Long mineId);
}
