package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ruoyi.common.annotation.DataScopeSelf;
import com.ruoyi.common.core.domain.BasePermission;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.dto.BizProjectRecordAddDto;
import com.ruoyi.system.domain.dto.BizProjectRecordDto;
import com.ruoyi.system.domain.vo.BizProStatsVo;
import com.ruoyi.system.domain.vo.BizProjectRecordListVo;
import com.ruoyi.system.domain.vo.BizProjectRecordVo;
import com.ruoyi.system.mapper.*;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.service.IBizProjectRecordService;

/**
 * 工程填报记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Service
public class BizProjectRecordServiceImpl extends ServiceImpl<BizProjectRecordMapper, BizProjectRecord> implements IBizProjectRecordService
{
    @Autowired
    private BizProjectRecordMapper bizProjectRecordMapper;

    @Autowired
    private BizProjectAuditMapper bizProjectAuditMapper;

    @Autowired
    private BizVideoMapper bizVideoMapper;

    @Autowired
    private BizDrillRecordMapper bizDrillRecordMapper;
    @Autowired
    private MinioClient getMinioClient;


    @DataScopeSelf
    public MPage<BizProjectRecordListVo> getlist(BasePermission permission, BizProjectRecordDto dto, Pagination pagination){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        MPJLambdaWrapper<BizProjectRecord> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper
                .selectAll(BizMine.class)
//todo                .leftJoin(,BizProjectRecord::getConstructUnitId)
//todo               .leftJoin(,BizProjectRecord::getConstructLocationId)
//todo               .leftJoin(,BizProjectRecord::getConstructShiftId)
                .select(BizProjectRecord::getProjectId,BizProjectRecord::getDeptId)
                .leftJoin(BizMine.class,BizMine::getMineId,BizProjectRecord::getDeptId)
                .and(permission.getDeptIds() != null && permission.getDeptIds().size()>0 ,i->i.in(permission.getDeptIds() != null && permission.getDeptIds().size()>0 , BizProjectRecord::getDeptId,permission.getDeptIds())
                        .or().eq(permission.getDateScopeSelf() == 5,BizProjectRecord::getCreateBy,currentUser.getUserName()))
                .eq(dto.getConstructUnitId()!=null,BizProjectRecord::getConstructUnitId,dto.getConstructUnitId())
                .eq(dto.getConstructLocationId()!=null,BizProjectRecord::getConstructLocationId,dto.getConstructLocationId())
                .eq(dto.getDrillType()!=null,BizProjectRecord::getDrillType,dto.getDrillType())
                .eq(dto.getShiftId()!=null,BizProjectRecord::getConstructShiftId,dto.getShiftId())
                .eq(dto.getStatus()!=null,BizProjectRecord::getStatus,dto.getStatus())
                .between(StrUtil.isNotEmpty(dto.getStartTime()),BizProjectRecord::getConstructTime,DateUtils.parseDate(dto.getStartTime()),DateUtils.parseDate(dto.getEndTime()))
                .eq(dto.getStatus()!=null,BizProjectRecord::getStatus,dto.getStatus());
        IPage<BizProjectRecordListVo> sss = bizProjectRecordMapper.selectJoinPage(pagination , BizProjectRecordListVo.class, queryWrapper);
        return new MPage<>(sss);
    }


    @Override
    public List<BizProjectRecordListVo> selectproList(BasePermission permission, BizProjectRecordDto dto) {

        Date currentDate = null;
        Date startDate = null;
        if(dto.getDayNum() != null ){
            currentDate = new Date();
            startDate = DateUtil.offsetDay(currentDate, -dto.getDayNum());
        }else {
            currentDate = DateUtils.parseDate(dto.getStartTime());
            startDate = DateUtils.parseDate(dto.getEndTime());
        }
        MPJLambdaWrapper<BizProjectRecord> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper
                .selectAll(BizProjectRecord.class)
//todo                .leftJoin(,BizProjectRecord::getConstructUnitId)
//               .leftJoin(,BizProjectRecord::getConstructLocationId)
//               .leftJoin(,BizProjectRecord::getConstructShiftId)
//                .select(BizProjectRecord::getProjectId,BizProjectRecordVo::getConstructLocationName)
//                .select(BizProjectRecord::getProjectId,BizProjectRecordVo::getConstructUnitName)
//                .select(BizProjectRecord::getProjectId,BizProjectRecordVo::getConstructShiftName)
                .between(BizProjectRecord::getConstructTime,startDate,currentDate);
        List<BizProjectRecordListVo> sss = bizProjectRecordMapper.selectJoinList(BizProjectRecordListVo.class, queryWrapper);
        return sss;
    }


    @Override
    public BizProStatsVo statsProject(BasePermission permission, BizProjectRecordDto dto) {
        Date currentDate = null;
        Date startDate = null;
        if(dto.getDayNum() != null ){
            currentDate = new Date();
            startDate = DateUtil.offsetDay(currentDate, -dto.getDayNum());
        }else {
            currentDate = DateUtils.parseDate(dto.getStartTime());
            startDate = DateUtils.parseDate(dto.getEndTime());
        }

        MPJLambdaWrapper<BizProjectRecord> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper
//                .selectAs(::getDeptName,"name")
                .selectCount(BizProjectRecord::getProjectId,"value")
                .between(BizProjectRecord::getConstructTime,startDate,currentDate)
                .leftJoin(SysDept.class,SysDept::getDeptId,BizProjectRecord::getDeptId)
//                .leftJoin(BizProjectRecord::getConstructLocationId)
                .groupBy(BizProjectRecord::getDeptId);
        Map<String,Object> locationMap = bizProjectRecordMapper.selectJoinMap(queryWrapper);

        queryWrapper.clear();
        queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper
                .selectAs(SysDictData::getDictLabel,"name")
                .selectCount(BizProjectRecord::getProjectId,"value")
                .leftJoin(SysDictData.class,SysDictData::getDictValue,BizProjectRecord::getDrillType)
                .between(BizProjectRecord::getConstructTime,startDate,currentDate)
                .groupBy(BizProjectRecord::getDrillType);
        Map<String,Object> drillTypeMap = bizProjectRecordMapper.selectJoinMap(queryWrapper);

        queryWrapper.clear();
        queryWrapper = new MPJLambdaWrapper<>();

        queryWrapper
//                .selectAs(SysDictData::getDictLabel,"name")
                .selectCount(BizProjectRecord::getProjectId,"value")
//                .leftJoin(.class,,BizProjectRecord::getConstructUnitId)
                .between(BizProjectRecord::getConstructTime,startDate,currentDate)
                .groupBy(BizProjectRecord::getConstructUnitId);
        Map<String,Object> unitMap = bizProjectRecordMapper.selectJoinMap(queryWrapper);

        BizProStatsVo vo = new BizProStatsVo();
        vo.setLocationMap(unitMap).setTypeMap(drillTypeMap).setUnitMap(unitMap);
        return vo;
    }

    @Override
    public BizProjectRecordVo selectById(Long bizProjectRecordId) {

        MPJLambdaWrapper<BizProjectRecord> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper
                .selectAll(BizProjectRecord.class)
//todo                .leftJoin(,BizProjectRecord::getConstructUnitId)
//               .leftJoin(,BizProjectRecord::getConstructLocationId)
//               .leftJoin(,BizProjectRecord::getConstructShiftId)
//                .select(BizProjectRecord::getProjectId,BizProjectRecordVo::getConstructLocationName)
//                .select(BizProjectRecord::getProjectId,BizProjectRecordVo::getConstructUnitName)
//                .select(BizProjectRecord::getProjectId,BizProjectRecordVo::getConstructShiftName)
                .leftJoin(BizMine.class,BizMine::getMineId,BizProjectRecord::getDeptId)
                .eq(BizProjectRecord::getProjectId,bizProjectRecordId);
        return  bizProjectRecordMapper.selectJoinOne(BizProjectRecordVo.class, queryWrapper);
    }

    @Override
    public List<BizProjectRecordListVo> auditList(BizProjectRecord bizProjectRecord) {
        return null;
    }

    @Override
    public int saveRecord(BizProjectRecordAddDto dto) {
        BizProjectRecord entity = new BizProjectRecord();
        BeanUtil.copyProperties(dto, entity);
        entity.setStatus(BizBaseConstant.FILL_STATUS_PEND).setIsRead(0);

        long  projectId =  this.getBaseMapper().insert(entity);
        dto.getDrillRecords().forEach(drillRecord -> {
            drillRecord.setProjectId(projectId);
            bizDrillRecordMapper.insert(drillRecord);
        });
        dto.getVideos().forEach(bizVideo -> {
            bizVideo.setProjectId(projectId);
            bizVideoMapper.insert(bizVideo);
        });
        BizProjectAudit audit = new BizProjectAudit();
        audit.setProjectId(dto.getProjectId())
                .setMsg("提交")
                .setLevel("AUTHOR")
                .setStatus(0);
        bizProjectAuditMapper.insert(audit);
        return 1;
    }

    @Override
    public int updateRecordById(BizProjectRecordAddDto dto) {
        BizProjectRecord entity = new BizProjectRecord();
        BeanUtil.copyProperties(dto, entity);
        entity.setStatus(BizBaseConstant.FILL_STATUS_PEND).setIsRead(0);
        bizProjectRecordMapper.updateById(entity);
        BizProjectAudit audit = new BizProjectAudit();
        audit.setProjectId(dto.getProjectId())
                .setMsg("提交")
                .setLevel("AUTHOR")
                .setStatus(0);
        bizProjectAuditMapper.insert(audit);
        return 1;
    }

    @Override
    public int firstAudit(BizProjectRecordDto dto) {
        BizProjectAudit audit = new BizProjectAudit();
        audit.setProjectId(dto.getProjectId())
                .setMsg(dto.getMsg())
                .setLevel("TEAM")
                .setStatus(dto.getAudit());
        bizProjectAuditMapper.insert(audit);
        BizProjectRecord entity = new BizProjectRecord();
        entity.setProjectId(dto.getProjectId())
                        .setStatus(dto.getStatus() == 1 ? BizBaseConstant.FILL_STATUS_TEAM_PASS:BizBaseConstant.FILL_STATUS_TEAM_BACK);
        bizProjectRecordMapper.updateById(entity);
        return 1;
    }


    @Override
    public int secondAudit(BizProjectRecordDto dto) {
        BizProjectAudit audit = new BizProjectAudit();
        audit.setProjectId(dto.getProjectId())
                .setMsg(dto.getMsg())
                .setLevel("DEPT")
                .setStatus(dto.getAudit());
        bizProjectAuditMapper.insert(audit);
        BizProjectRecord entity = new BizProjectRecord();
        entity.setProjectId(dto.getProjectId())
                .setStatus(dto.getStatus() == 1 ? BizBaseConstant.FILL_STATUS_DEPART_PASS:BizBaseConstant.FILL_STATUS_DEPART_BACK);
        bizProjectRecordMapper.updateById(entity);
        return 1;
    }
}







