package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.ConstructionUnitEntity;
import com.ruoyi.system.domain.Entity.DepartmentAuditEntity;
import com.ruoyi.system.domain.Entity.TeamAuditEntity;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.AppAuditDetailDTO;
import com.ruoyi.system.domain.dto.SelectDTO;
import com.ruoyi.system.domain.vo.BizProjectRecordDetailVo;
import com.ruoyi.system.domain.vo.ProjectVO;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.AppAuditService;
import com.ruoyi.system.service.IBizProjectRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: shikai
 * @date: 2025/1/13
 * @description:
 */

@Service
public class AppAuditServiceImpl implements AppAuditService {

    @Resource
    private TeamAuditMapper teamAuditMapper;

    @Resource
    private DepartmentAuditMapper departmentAuditMapper;

    @Resource
    private SysDictDataMapper sysDictDataMapper;

    @Resource
    private ConstructionUnitMapper constructionUnitMapper;

    @Resource
    private TunnelMapper tunnelMapper;

    @Resource
    private BizWorkfaceMapper bizWorkfaceMapper;

    @Resource
    private IBizProjectRecordService bizProjectRecordService;

    @Resource
    private SysUserMapper sysUserMapper;

    /**
     * 待审批-分页查询(区队审核)
     * @param selectDTO 查询参数DTO
     * @param pageNum 当前页码
     * @param pageSize 每页显示条数
     * @return 返回结果
     */
    @Override
    public TableData teamAuditByPage(SelectDTO selectDTO, Integer pageNum, Integer pageSize) {
        TableData result = new TableData();
        if (null == pageNum || pageNum < 1) {
            pageNum = 1;
        }
        if (null == pageSize || pageSize < 1) {
            pageSize = 10;
        }
        PageHelper.startPage(pageNum, pageSize);
        Page<ProjectVO> page = teamAuditMapper.queryByPageForApp(selectDTO.getFillingType(), selectDTO.getConstructionUnitId());
        Page<ProjectVO> projectVOPage = getProjectListFmt(page);
        result.setTotal(projectVOPage.getTotal());
        result.setRows(projectVOPage.getResult());
        return result;
    }

//    /**
//     * 已审批-分页查询(区队审核)
//     * @param selectDTO 查询参数DTO
//     * @param userId 用户id
//     * @param pageNum 当前页码
//     * @param pageSize 每页显示条数
//     * @return 返回结果
//     */
//    @Override
//    public TableData teamApprovedByPage(SelectDTO selectDTO, Long userId, Pagination pagination) {
//
//        if (ObjectUtil.isNull(userId)) {
//            throw new RuntimeException("用户id不能为空!");
//        }
//        List<Long> projectIds = teamAuditMapper.selectList(new LambdaQueryWrapper<TeamAuditEntity>()
//                        .eq(TeamAuditEntity::getCreateBy, userId)
//                        .eq(TeamAuditEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG))
//                .stream()
//                .map(TeamAuditEntity::getProjectId)
//                .distinct()
//                .collect(Collectors.toList());
//        MPJLambdaWrapper<BizProjectRecord> queryWrapper = new MPJLambdaWrapper<>();
//        queryWrapper.in(BizProjectRecordListVo::getProjectId, projectIds);
//        IPage<BizProjectRecordListVo> page = bizProjectRecordService.pageDeep(pagination , queryWrapper);
//        if (ListUtils.isNotNull(page.getRecords())) {
//            page.getResult().forEach(projectVO -> {
//                // 根据区队审核结果，动态模拟审核状态
//                TeamAuditEntity teamAuditEntity = teamAuditMapper.selectOne(new LambdaQueryWrapper<TeamAuditEntity>()
//                        .eq(TeamAuditEntity::getProjectId, projectVO.getProjectId())
//                        .eq(TeamAuditEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
//                        .orderByDesc(TeamAuditEntity::getCreateTime)
//                        .last("LIMIT 1"));
//                if (ObjectUtil.isNotNull(teamAuditEntity)) {
//                    if (teamAuditEntity.getAuditResult().equals(ConstantsInfo.AUDIT_SUCCESS)) {
//                        if (teamAuditEntity.getDepartAuditState().equals(ConstantsInfo.AUDITED_DICT_VALUE)) { //科室审批通过则通过
//                            projectVO.setStatusFmt("已通过");
//                        } else if (teamAuditEntity.getDepartAuditState().equals(ConstantsInfo.REJECTED)) { //科室审批驳回则驳回
//                            projectVO.setStatusFmt("已驳回");
//                        } else if (teamAuditEntity.getDepartAuditState().equals(ConstantsInfo.AUDIT_STATUS_DICT_VALUE) ||
//                                teamAuditEntity.getDepartAuditState().equals(ConstantsInfo.IN_REVIEW_DICT_VALUE)) { //科室审批为待审核/审核中时则为已提交
//                            projectVO.setStatusFmt("已提交");
//                        }
//                    }
//                    if (teamAuditEntity.getAuditResult().equals(ConstantsInfo.AUDIT_REJECT)) {
//                        projectVO.setStatusFmt("已驳回");
//                        projectVO.setRejectReason(teamAuditEntity.getRejectionReason()); //驳回原因
//                    }
//                }
//            });
//        }
//        result.setTotal(page.getTotal());
//        result.setRows(page.getResult());
//        return result;
//    }

    /**
     * 待审批-分页查询(科室审核)
     * @param selectDTO 查询参数DTO
     * @param pageNum 当前页码
     * @param pageSize 每页显示条数
     * @return 返回结果
     */
    @Override
    public TableData departAuditByPage(SelectDTO selectDTO, Integer pageNum, Integer pageSize) {
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
        Page<ProjectVO> page = departmentAuditMapper.queryByPageForApp(selectDTO.getFillingType(), selectDTO.getConstructionUnitId(), projectIds);
        Page<ProjectVO> projectVOPage = getProjectListFmtTwo(page);
        result.setTotal(projectVOPage.getTotal());
        result.setRows(projectVOPage.getResult());
        return result;
    }

    /**
     * 已审批-分页查询(科室审核)
     * @param selectDTO 查询参数DTO
     * @param userId 用户id
     * @param pageNum 当前页码
     * @param pageSize 每页显示条数
     * @return 返回结果
     */
    @Override
    public TableData departApprovedByPage(SelectDTO selectDTO, Long userId, Integer pageNum, Integer pageSize) {
        TableData result = new TableData();
        if (null == pageNum || pageNum < 1) {
            pageNum = 1;
        }
        if (null == pageSize || pageSize < 1) {
            pageSize = 10;
        }
        if (ObjectUtil.isNull(userId)) {
            throw new RuntimeException("用户id不能为空!");
        }
        List<Long> projectIds = departmentAuditMapper.selectList(new LambdaQueryWrapper<DepartmentAuditEntity>()
                        .eq(DepartmentAuditEntity::getCreateBy, userId)
                        .eq(DepartmentAuditEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG))
                .stream()
                .map(DepartmentAuditEntity::getProjectId)
                .distinct()
                .collect(Collectors.toList());
        PageHelper.startPage(pageNum, pageSize);
        Page<ProjectVO> page = departmentAuditMapper.approvedQueryByPageForApp(projectIds, selectDTO.getFillingType(), selectDTO.getConstructionUnitId());
        if (ListUtils.isNotNull(page.getResult())) {
            page.getResult().forEach(projectVO -> {
                projectVO.setConstructionUnitName(getConstructionUnitName(projectVO.getConstructUnitId()));
                projectVO.setConstructSiteFmt(getConstructSite(projectVO.getConstructSiteId(), projectVO.getConstructType()));
                //审核状态字典lable
                String auditStatus = sysDictDataMapper.selectDictLabel(ConstantsInfo.AUDIT_STATUS_DICT_TYPE, projectVO.getStatus());
                projectVO.setStatusFmt(auditStatus);
                if (projectVO.getStatus().equals(ConstantsInfo.REJECTED)) {
                    // 获取驳回原因
                    DepartmentAuditEntity departmentAuditEntity = departmentAuditMapper.selectOne(new LambdaQueryWrapper<DepartmentAuditEntity>()
                            .eq(DepartmentAuditEntity::getProjectId, projectVO.getProjectId())
                            .eq(DepartmentAuditEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
                            .orderByDesc(DepartmentAuditEntity::getCreateTime)
                            .last("LIMIT 1"));
                    if (ObjectUtil.isNull(departmentAuditEntity)) {
                        throw new DepartmentAuditServiceImpl.DepartmentAuditException("未找到此填报审核记录， 填报ID：" + projectVO.getProjectId());
                    }
                    projectVO.setRejectReason(departmentAuditEntity.getRejectionReason());
                }
            });
        }
        result.setTotal(page.getTotal());
        result.setRows(page.getResult());
        return result;
    }

    /**
     * 审批详情
     * @param projectId 工程填报id
     * @param tag 审批类型标识(1-区队审核、2-科室审核)
     * @return 返回结果
     */
    @Override
    public AppAuditDetailDTO detail(Long projectId, String tag) {
        if (ObjectUtil.isNull(projectId)) {
            throw new RuntimeException("参数错误,工程填报id不能为空");
        }
        if (ObjectUtil.isNull(tag)) {
            throw new RuntimeException("参数错误,tag标识不能为空");
        }
        AppAuditDetailDTO appAuditDetailDTO = new AppAuditDetailDTO();
        String auditResult = "";
        String rejectionReason = "";
        Long reviewerId = null;
        BizProjectRecordDetailVo projectRecordDetailVo = bizProjectRecordService.selectById(projectId);
        appAuditDetailDTO.setBizProjectRecordDetailVo(projectRecordDetailVo);
        if (tag.equals(ConstantsInfo.TEAM_AUDIT)) {
            TeamAuditEntity teamAuditEntity = teamAuditMapper.selectOne(new LambdaQueryWrapper<TeamAuditEntity>()
                    .eq(TeamAuditEntity::getProjectId, projectId)
                    .eq(TeamAuditEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
                    .orderByDesc(TeamAuditEntity::getCreateTime)
                    .last("LIMIT 1"));
            if (ObjectUtil.isNull(teamAuditEntity)) {
                throw new RuntimeException("未找到此填报审核记录，填报ID：" + projectId);
            }
            auditResult = teamAuditEntity.getAuditResult();
            if (auditResult.equals(ConstantsInfo.AUDIT_REJECT)) {
                rejectionReason = teamAuditEntity.getRejectionReason();
                appAuditDetailDTO.setAuditResultFmt("已驳回");
            } else {
                appAuditDetailDTO.setAuditResultFmt("已通过");
            }
            reviewerId = teamAuditEntity.getCreateBy();
        } else if (tag.equals(ConstantsInfo.DEPART_AUDIT)) {
            DepartmentAuditEntity departmentAuditEntity = departmentAuditMapper.selectOne(new LambdaQueryWrapper<DepartmentAuditEntity>()
                    .eq(DepartmentAuditEntity::getProjectId, projectId)
                    .eq(DepartmentAuditEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
                    .orderByDesc(DepartmentAuditEntity::getCreateTime)
                    .last("LIMIT 1"));
            if (ObjectUtil.isNull(departmentAuditEntity)) {
                throw new RuntimeException("未找到此填报审核记录，填报ID：" + projectId);
            }
            auditResult = departmentAuditEntity.getAuditResult();
            if (auditResult.equals(ConstantsInfo.AUDIT_REJECT)) {
                rejectionReason = departmentAuditEntity.getRejectionReason();
                appAuditDetailDTO.setAuditResultFmt("已驳回");
            } else {
                appAuditDetailDTO.setAuditResultFmt("已通过");
            }
            reviewerId = departmentAuditEntity.getCreateBy();
        }
        appAuditDetailDTO.setAuditResult(auditResult);
        appAuditDetailDTO.setRejectionReason(rejectionReason);
        appAuditDetailDTO.setReviewer(sysUserMapper.selectNameById(reviewerId));
        return appAuditDetailDTO;
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
            });
        }
        return list;
    }

    /**
     * VO格式化
     */
    private Page<ProjectVO> getProjectListFmtTwo(Page<ProjectVO> list) {
        if (ListUtils.isNotNull(list.getResult())) {
            list.getResult().forEach(projectVO -> {
                String auditStatusFmt = "";
                projectVO.setConstructionUnitName(getConstructionUnitName(projectVO.getConstructUnitId()));
                projectVO.setConstructSiteFmt(getConstructSite(projectVO.getConstructSiteId(), projectVO.getConstructType()));

                // 当区队审核通过之后，提交给科室审核，若没有点击’审核按钮‘此时科室审核状态为"待审核"
                TeamAuditEntity teamAuditEntity = teamAuditMapper.selectOne(new LambdaQueryWrapper<TeamAuditEntity>()
                        .eq(TeamAuditEntity::getProjectId, projectVO.getProjectId())
                        .eq(TeamAuditEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
                        .eq(TeamAuditEntity::getAuditResult, ConstantsInfo.AUDIT_SUCCESS)
                        .orderByDesc(TeamAuditEntity::getCreateTime)
                        .last("LIMIT 1"));
                if (ObjectUtil.isNull(teamAuditEntity)) {
                    throw new DepartmentAuditServiceImpl.DepartmentAuditException("未找到此填报审核记录， 填报ID：" + projectVO.getProjectId());
                }
                String departAuditState = teamAuditEntity.getDepartAuditState();
                if (departAuditState.equals(ConstantsInfo.AUDIT_STATUS_DICT_VALUE)) {
                    auditStatusFmt = sysDictDataMapper.selectDictLabel(ConstantsInfo.AUDIT_STATUS_DICT_TYPE, projectVO.getStatus());
                    projectVO.setStatus(departAuditState);
                }
                auditStatusFmt = sysDictDataMapper.selectDictLabel(ConstantsInfo.AUDIT_STATUS_DICT_TYPE, projectVO.getStatus());
                projectVO.setStatusFmt(auditStatusFmt);
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
}