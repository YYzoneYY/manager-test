package com.ruoyi.system.service;

import com.github.yulichang.extension.mapping.base.MPJDeepService;
import com.ruoyi.common.core.domain.BasePermission;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.dto.project.BizCardVDto;
import com.ruoyi.system.domain.dto.project.BizWashProofDto;
import com.ruoyi.system.domain.vo.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * 工程填报记录Service接口
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
public interface IBizProjectRecordService  extends MPJDeepService<BizProjectRecord>
{

    /*******************************************************基础***********************************************************/
    MPage<BizProjectRecordListVo> getlist(BasePermission permission, BizProjectRecordDto dto , Pagination pagination);


    Long saveRecord(BizProjectRecordAddDto dto);

    int saveRecordApp(BizProjectRecordAddDto dto);


    int updateRecord(BizProjectRecordAddDto dto);

    int removeByProId(Long projectId);


    int removeByProIds(Long[] projectIds);


    BizProjectRecordDetailVo selectById(Long bizProjectRecordId);






    /********************************************************防冲 -  统计 **********************************************************/


    MPage<BizProjectRecordListVo> selectproList(BasePermission permission, BizWashProofDto dto , Pagination pagination);


    BizProStatsVo statsProject(BasePermission permission, BizWashProofDto dto);




    /******************************************************** 牌版 **********************************************************/


    MPage<BizProjectRecordPaibanVo> selectPaiList(BasePermission permission, BizCardVDto dto, Pagination pagination);



    /******************************************************** 牌版 **********************************************************/



    MPage<Map<String, Object>> monitorProject(BizPlanDto dto, Pagination pagination);



    List<BizProjectRecordListVo> auditList(BizProjectRecord bizProjectRecord);


    /**
     * 根据 帮偏移量和给定角度 获取  实际地图偏移量
     * @param barAngle   帮偏移量
     * @param bearAngle  填写角度
     * @return
     */
    int getAngle(Integer barAngle, Integer bearAngle);

    int firstAudit(BizProjectRecordDto projectRecordDto);

    int secondAudit(BizProjectRecordDto projectRecordDto);



    void getReport(BizProjectRecordDto1 dto, HttpServletResponse response);


    void getDayReport(Long mineId, String statsDate , Long deptId, HttpServletResponse response) throws UnsupportedEncodingException;


    void get444(BizProjectRecordDto1 dto, HttpServletResponse response) throws IOException;


    void get999(String startTime, String endTime, Long tunnelId, Long workfaceId,String  constructType, HttpServletResponse response) throws IOException;


    void sss555(HttpServletResponse response);


    /**
     * 删除计划时调用
     * @param planId
     */
    void deletePlan(Long planId);

    /**
     * 提交审核
     * @param projectId 工程填报id
     * @return 返回结果
     */
    String submitForReview(Long projectId);

    /**
     * 撤回
     * @param projectId 工程填报id
     * @return 返回结果
     */
    String withdraw(Long projectId);

    /**
     * 获取驳回原因
     * @param projectId 工程填报id
     * @return 返回结果
     */
    ReturnReasonDTO getReason(Long projectId);
}
