package com.ruoyi.system.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.domain.BasePermission;
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.domain.dto.BizProjectRecordAddDto;
import com.ruoyi.system.domain.dto.BizProjectRecordDto;
import com.ruoyi.system.domain.vo.BizProStatsVo;
import com.ruoyi.system.domain.vo.BizProjectRecordListVo;
import com.ruoyi.system.domain.vo.BizProjectRecordVo;

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
    List<BizProjectRecordListVo> getlist(BasePermission permission, BizProjectRecordDto dto);


    List<BizProjectRecordListVo> selectproList(BasePermission permission, BizProjectRecordDto dto);


    BizProStatsVo statsProject(BasePermission permission, BizProjectRecordDto dto);



    List<BizProjectRecordListVo> auditList(BizProjectRecord bizProjectRecord);


    int saveRecord(BizProjectRecordAddDto dto);


    int updateRecordById(BizProjectRecordAddDto dto);

    BizProjectRecordVo selectById(Long bizProjectRecordId);


     int firstAudit(BizProjectRecordDto projectRecordDto);

     int secondAudit(BizProjectRecordDto projectRecordDto);
}
