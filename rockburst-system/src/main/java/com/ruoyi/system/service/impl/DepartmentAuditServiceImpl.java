package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.ConstructionUnitEntity;
import com.ruoyi.system.domain.Entity.DepartmentAuditEntity;
import com.ruoyi.system.domain.Entity.TeamAuditEntity;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.DepartAuditDTO;
import com.ruoyi.system.domain.dto.SelectDeptAuditDTO;
import com.ruoyi.system.domain.dto.SelectProjectDTO;
import com.ruoyi.system.domain.dto.project.DepartmentAuditDTO;
import com.ruoyi.system.domain.vo.BizProjectRecordDetailVo;
import com.ruoyi.system.domain.vo.ProjectVO;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.DepartmentAuditService;
import com.ruoyi.system.service.IBizProjectRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: shikai
 * @date: 2024/11/26
 * @description:
 */

@Transactional
@Service
public class DepartmentAuditServiceImpl extends ServiceImpl<DepartmentAuditMapper, DepartmentAuditEntity> implements DepartmentAuditService {

    @Resource
    private DepartmentAuditMapper departmentAuditMapper;

    @Resource
    private TeamAuditMapper teamAuditMapper;

    @Resource
    private BizProjectRecordMapper bizProjectRecordMapper;

    @Resource
    private TunnelMapper tunnelMapper;

    @Resource
    private BizWorkfaceMapper bizWorkfaceMapper;

    @Resource
    private ConstructionUnitMapper constructionUnitMapper;

    @Resource
    private SysDictDataMapper sysDictDataMapper;

    @Resource
    private IBizProjectRecordService bizProjectRecordService;

    @Resource
    private SysUserMapper sysUserMapper;

    /**
     * 点击审核按钮
     * @param projectId 计划id
     * @return 返回结果
     */
    @Override
    public DepartmentAuditDTO clickAudit(Long projectId) {
        if (ObjectUtil.isNull(projectId)) {
            throw new DepartmentAuditException("参数错误");
        }
        List<String> departAuditStates = new ArrayList<>();
        departAuditStates.add(ConstantsInfo.AUDIT_STATUS_DICT_VALUE);
        departAuditStates.add(ConstantsInfo.IN_REVIEW_DICT_VALUE);
        TeamAuditEntity teamAuditEntity = teamAuditMapper.selectOne(new LambdaQueryWrapper<TeamAuditEntity>()
                .eq(TeamAuditEntity::getProjectId, projectId)
                .in(TeamAuditEntity::getDepartAuditState, departAuditStates)
                .eq(TeamAuditEntity::getAuditResult, ConstantsInfo.AUDIT_SUCCESS)
                .eq(TeamAuditEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(teamAuditEntity)) {
            throw new RuntimeException("此填报信息没有通过区队审核，暂无法进行审核");
        }
        BizProjectRecordDetailVo bizProjectRecordDetailVo = new BizProjectRecordDetailVo();
        TeamAuditEntity teamAudit = new TeamAuditEntity();
        BeanUtils.copyProperties(teamAuditEntity, teamAudit);
        teamAudit.setDepartAuditState(ConstantsInfo.IN_REVIEW_DICT_VALUE);
        int update = teamAuditMapper.updateById(teamAudit);
        if (update <= 0) {
            throw new DepartmentAuditException("科室审核失败,请联系管理员");
        }
        BizProjectRecordDetailVo projectRecordDetailVo = bizProjectRecordService.selectById(projectId);
        BeanUtils.copyProperties(projectRecordDetailVo, bizProjectRecordDetailVo);
        DepartmentAuditDTO departmentAuditDTO = new DepartmentAuditDTO();
        departmentAuditDTO.setProjectRecordDetailVo(bizProjectRecordDetailVo);
        String teamAuditPeople = teamAuditPeople(projectId);
        departmentAuditDTO.setTeamAuditPeople(teamAuditPeople);
        return departmentAuditDTO;
    }

    /**
     * 科室审核
     * @param departAuditDTO 参数DTO
     * @return 返回结果
     */
    @Override
    public int departmentAudit(DepartAuditDTO departAuditDTO) {
        int flag = 0;
        BizProjectRecord bizProjectRecord = bizProjectRecordMapper.selectOne(new LambdaQueryWrapper<BizProjectRecord>()
                .eq(BizProjectRecord::getProjectId, departAuditDTO.getProjectId())
                .eq(BizProjectRecord::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(bizProjectRecord)) {
            throw new DepartmentAuditException("未找到此工程填报信息,无法进行审核");
        }
        TeamAuditEntity teamAuditEntity = checkTeamAudit(departAuditDTO.getProjectId());
        Integer maxNumber = departmentAuditMapper.selectMaxNumber(departAuditDTO.getProjectId());
        DepartmentAuditEntity departmentAuditEntity = new DepartmentAuditEntity();
        if (maxNumber == null) {
            maxNumber = 0;
        }
        if (maxNumber.equals(0)) {
            departmentAuditEntity.setNumber(ConstantsInfo.NUMBER);
        }
        int newNumber = maxNumber + ConstantsInfo.NUMBER;
        if (newNumber < 0) {
            throw new DepartmentAuditException("该填报信息审核次数过多,请联系管理员");
        }
        departmentAuditEntity.setNumber(newNumber);
        departmentAuditEntity.setProjectId(departAuditDTO.getProjectId());
        departmentAuditEntity.setAuditResult(departAuditDTO.getAuditResult());
        departmentAuditEntity.setRejectionReason(departAuditDTO.getRejectionReason());
        departmentAuditEntity.setTeamAuditId(teamAuditEntity.getTeamAuditId());
        departmentAuditEntity.setCreateBy(1L);
        departmentAuditEntity.setCreateTime(System.currentTimeMillis());
        departmentAuditEntity.setDelFlag(ConstantsInfo.ZERO_DEL_FLAG);
        flag = departmentAuditMapper.insert(departmentAuditEntity);
        if (flag <= 0) {
            throw new DepartmentAuditException("审核失败,请联系管理员");
        } else {
            if (ConstantsInfo.AUDIT_SUCCESS.equals(departAuditDTO.getAuditResult())) {
                teamAuditEntity.setDepartAuditState(ConstantsInfo.AUDITED_DICT_VALUE);
                bizProjectRecord.setStatus(Integer.valueOf(ConstantsInfo.AUDITED_DICT_VALUE));
            }
            teamAuditEntity.setDepartAuditState(ConstantsInfo.REJECTED);
            bizProjectRecord.setStatus(Integer.valueOf(ConstantsInfo.REJECTED));
            TeamAuditEntity teamAudit = new TeamAuditEntity();
            BeanUtils.copyProperties(teamAuditEntity, teamAudit);
            BizProjectRecord projectRecord = new BizProjectRecord();
            BeanUtils.copyProperties(bizProjectRecord, projectRecord);
            int t = teamAuditMapper.updateById(teamAudit);
            int b = bizProjectRecordMapper.updateById(projectRecord);
            if (t <= 0 || b <= 0) {
                throw new DepartmentAuditException("审核失败,请联系管理员");
            }
        }
        return flag;
    }

    /**
     * 分页查询
     * @param selectDeptAuditDTO 查询参数DTO
     * @param pageNum 当前页码
     * @param pageSize 条数
     * @return 返回结果
     */
    @Override
    public TableData queryByPage(SelectDeptAuditDTO selectDeptAuditDTO, Integer pageNum, Integer pageSize) {
        TableData result = new TableData();
        if (null == pageNum || pageNum < 1) {
            pageNum = 1;
        }
        if (null == pageSize || pageSize < 1) {
            pageSize = 10;
        }
        List<String> departAuditStates = new ArrayList<>();
        departAuditStates.add(ConstantsInfo.IN_REVIEW_DICT_VALUE);
        departAuditStates.add(ConstantsInfo.AUDIT_STATUS_DICT_VALUE);
        List<Long> projectIds = teamAuditMapper.selectList(new LambdaQueryWrapper<TeamAuditEntity>()
                        .in(TeamAuditEntity::getDepartAuditState, departAuditStates)
                        .eq(TeamAuditEntity::getAuditResult, ConstantsInfo.AUDITED_DICT_VALUE)
                        .eq(TeamAuditEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG))
                .stream()
                .map(TeamAuditEntity::getProjectId)
                .collect(Collectors.toList());
        PageHelper.startPage(pageNum, pageSize);
        Page<ProjectVO> page = departmentAuditMapper.queryByPage(selectDeptAuditDTO, projectIds);
        Page<ProjectVO> projectVOPage = getProjectListFmt(page);
        result.setTotal(projectVOPage.getTotal());
        result.setRows(projectVOPage.getResult());
        return result;
    }

    @Override
    public TableData auditHistoryPage(SelectProjectDTO selectProjectDTO, Integer pageNum, Integer pageSize) {
        TableData result = new TableData();
        if (null == pageNum || pageNum < 1) {
            pageNum = 1;
        }
        if (null == pageSize || pageSize < 1) {
            pageSize = 10;
        }
        PageHelper.startPage(pageNum, pageSize);
        Page<ProjectVO> page = departmentAuditMapper.auditHistoryPage(selectProjectDTO);
        Page<ProjectVO> planVOPage = getProjectListFmt(page);
        result.setTotal(planVOPage.getTotal());
        result.setRows(planVOPage.getResult());
        return result;
    }

    private TeamAuditEntity checkTeamAudit(Long projectId) {
        TeamAuditEntity teamAuditEntity = teamAuditMapper.selectOne(new LambdaQueryWrapper<TeamAuditEntity>()
                .eq(TeamAuditEntity::getProjectId, projectId)
                .eq(TeamAuditEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(teamAuditEntity)) {
            throw new DepartmentAuditException("区队暂未审核,请先进行区队审核！");
        }
        if (!teamAuditEntity.getAuditResult().equals(ConstantsInfo.AUDIT_SUCCESS)) {
            throw new DepartmentAuditException("区队审核未通过,无法进行科室审核！");
        }
        return teamAuditEntity;
    }

    /**
     * VO格式化
     */
    private Page<ProjectVO> getProjectListFmt(Page<ProjectVO> list) {
        if (ListUtils.isNotNull(list.getResult())) {
            list.getResult().forEach(projectVO -> {
                projectVO.setConstructionUnitName(getConstructionUnitName(projectVO.getConstructUnitId()));
                projectVO.setConstructSiteFmt(getConstructSite(projectVO.getConstructSiteId(), projectVO.getConstructType()));
                //审核状态字典lable
                String auditStatus = sysDictDataMapper.selectDictLabel(ConstantsInfo.AUDIT_STATUS_DICT_TYPE, projectVO.getStatus());
                projectVO.setStatusFmt(auditStatus);
                if (projectVO.getStatus().equals(ConstantsInfo.REJECTED)) {
                    // 获取驳回原因
                    String rejectReason = getRejectReason(projectVO.getProjectId());
                    projectVO.setRejectReason(rejectReason);
                }
            });
        }
        return list;
    }

    /**
     * 获取施工单位名称
     */
    public String getConstructionUnitName(Long constructionUnitId) {
        String constructionUnitName = null;
        ConstructionUnitEntity constructionUnitEntity = constructionUnitMapper.selectOne(new LambdaQueryWrapper<ConstructionUnitEntity>()
                .eq(ConstructionUnitEntity::getConstructionUnitId, constructionUnitId)
                .eq(ConstructionUnitEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(constructionUnitEntity)) {
            return null;
        }
        constructionUnitName = constructionUnitEntity.getConstructionUnitName();
        return constructionUnitName;
    }

    /**
     * 获取施工地点
     */
    public String getConstructSite(Long constructSite, String type) {
        String constructSiteName = null;
        if (ObjectUtil.equals(type, ConstantsInfo.TUNNELING)) {
            TunnelEntity tunnelEntity = tunnelMapper.selectOne(new LambdaQueryWrapper<TunnelEntity>()
                    .eq(TunnelEntity::getTunnelId, constructSite)
                    .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ObjectUtil.isNull(tunnelEntity)) {
                return null;
            }
            constructSiteName = tunnelEntity.getTunnelName();
        } else if (ObjectUtil.equals(type, ConstantsInfo.STOPE)) {
            BizWorkface bizWorkface = bizWorkfaceMapper.selectOne(new LambdaQueryWrapper<BizWorkface>()
                    .eq(BizWorkface::getWorkfaceId, constructSite)
                    .eq(BizWorkface::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ObjectUtil.isNull(bizWorkface)) {
                return null;
            }
            constructSiteName = bizWorkface.getWorkfaceName();
        }
        return constructSiteName;
    }

    /**
     * 获取驳回原因
     */
    private String getRejectReason(Long projectId) {
        // 区队审核记录
        TeamAuditEntity teamAuditEntity = teamAuditMapper.selectOne(new LambdaQueryWrapper<TeamAuditEntity>()
                .eq(TeamAuditEntity::getProjectId, projectId)
                .eq(TeamAuditEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
                .eq(TeamAuditEntity::getAuditResult, ConstantsInfo.REJECTED)
                .orderByDesc(TeamAuditEntity::getCreateTime)
                .last("LIMIT 1"));
        // 科室审核记录
        DepartmentAuditEntity departmentAuditEntity = departmentAuditMapper.selectOne(new LambdaQueryWrapper<DepartmentAuditEntity>()
                .eq(DepartmentAuditEntity::getProjectId, projectId)
                .eq(DepartmentAuditEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
                .orderByDesc(DepartmentAuditEntity::getCreateTime)
                .last("LIMIT 1"));
        if (ObjectUtil.isNull(teamAuditEntity)) {
            if (ObjectUtil.isNull(departmentAuditEntity)) {
                throw new DepartmentAuditException("未找到此填报审核记录， 填报ID：" + projectId);
            }
            return departmentAuditEntity.getRejectionReason();
        } else {
            return teamAuditEntity.getRejectionReason();
        }
    }

    public static class DepartmentAuditException extends RuntimeException {
        public DepartmentAuditException(String message) {
            super(message);
        }
    }

    private String teamAuditPeople(Long projectId) {
        TeamAuditEntity teamAuditEntity = teamAuditMapper.selectOne(new LambdaQueryWrapper<TeamAuditEntity>()
                .eq(TeamAuditEntity::getProjectId, projectId)
                .eq(TeamAuditEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
                .eq(TeamAuditEntity::getAuditResult, "1")
                .orderByDesc(TeamAuditEntity::getCreateTime)
                .last("LIMIT 1"));
        if (ObjectUtil.isNull(teamAuditEntity)) {
            throw new DepartmentAuditException("未找到此填报审核记录， 填报ID：" + projectId);
        }
        Long createBy = teamAuditEntity.getCreateBy();
        return sysUserMapper.selectNameById(createBy);
    }
}