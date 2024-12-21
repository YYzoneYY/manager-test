package com.ruoyi.system.service;

import com.github.yulichang.extension.mapping.base.MPJDeepService;
import com.ruoyi.common.core.domain.BasePermission;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.domain.dto.BizPlanDto;
import com.ruoyi.system.domain.dto.BizProjectRecordAddDto;
import com.ruoyi.system.domain.dto.BizProjectRecordDto;
import com.ruoyi.system.domain.dto.BizProjectRecordDto1;
import com.ruoyi.system.domain.vo.BizProStatsVo;
import com.ruoyi.system.domain.vo.BizProjectRecordListVo;
import com.ruoyi.system.domain.vo.BizProjectRecordPaibanVo;
import com.ruoyi.system.domain.vo.BizProjectRecordVo;

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
    /**
     * 查询工程填报记录
     * 
     * @param
     * @return 工程填报记录
     */
    MPage<BizProjectRecordListVo> getlist(BasePermission permission, BizProjectRecordDto dto , Pagination pagination);



    int saveRecord(BizProjectRecordAddDto dto);


    int updateRecord(BizProjectRecordAddDto dto);

    int removeByProId(Long projectId);


    int removeByProIds(Long[] projectIds);


    BizProjectRecordVo selectById(Long bizProjectRecordId);






    MPage<BizProjectRecordListVo> selectproList(BasePermission permission, BizProjectRecordDto dto , Pagination pagination);


    BizProStatsVo statsProject(BasePermission permission, BizProjectRecordDto dto);


    MPage<BizProjectRecordPaibanVo> selectPaiList(BasePermission permission, BizProjectRecordDto dto, Pagination pagination);


    MPage<Map<String, Object>> monitorProject(BizPlanDto dto, Pagination pagination);



    List<BizProjectRecordListVo> auditList(BizProjectRecord bizProjectRecord);


     int firstAudit(BizProjectRecordDto projectRecordDto);

     int secondAudit(BizProjectRecordDto projectRecordDto);



    void getReport(BizProjectRecordDto1 dto, HttpServletResponse response);


    void getDayReport(Long mineId, String statsDate , Long deptId, HttpServletResponse response) throws UnsupportedEncodingException;


    void get444(HttpServletResponse response) throws IOException;


    void sss555(HttpServletResponse response);

}
