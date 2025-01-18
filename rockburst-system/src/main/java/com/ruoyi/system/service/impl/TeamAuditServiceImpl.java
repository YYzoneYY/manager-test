package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.annotation.DataScopeSelf;
import com.ruoyi.common.core.domain.BasePermission;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.ConstructionUnitEntity;
import com.ruoyi.system.domain.Entity.PlanAuditEntity;
import com.ruoyi.system.domain.Entity.TeamAuditEntity;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.SelectProjectDTO;
import com.ruoyi.system.domain.dto.TeamAuditDTO;
import com.ruoyi.system.domain.vo.BizProjectRecordDetailVo;
import com.ruoyi.system.domain.vo.ProjectVO;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.IBizProjectRecordService;
import com.ruoyi.system.service.TeamAuditService;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: shikai
 * @date: 2024/11/25
 * @description:
 */

@Service
public class TeamAuditServiceImpl extends ServiceImpl<TeamAuditMapper, TeamAuditEntity> implements TeamAuditService {

    @Resource
    private TeamAuditMapper teamAuditMapper;

    @Resource
    private BizProjectRecordMapper bizProjectRecordMapper;

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

    /**
     * 点击审核按钮
     * @param projectId 计划id
     * @return 返回结果
     */
    @Override
    public BizProjectRecordDetailVo audit(Long projectId) {
        if (ObjectUtil.isNull(projectId)) {
            throw new RuntimeException("参数错误");
        }
        BizProjectRecord bizProjectRecord = bizProjectRecordMapper.selectOne(new LambdaQueryWrapper<BizProjectRecord>()
                .eq(BizProjectRecord::getProjectId, projectId)
                .eq(BizProjectRecord::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(bizProjectRecord)) {
            throw new RuntimeException("未找到此工程填报信息,无法进行审核");
        }
        BizProjectRecordDetailVo bizProjectRecordDetailVo = new BizProjectRecordDetailVo();
        BizProjectRecord projectRecord = new BizProjectRecord();
        BeanUtils.copyProperties(bizProjectRecord, projectRecord);
        projectRecord.setStatus(Integer.valueOf(ConstantsInfo.IN_REVIEW_DICT_VALUE));
        int update = bizProjectRecordMapper.updateById(projectRecord);
        if (update > 0) {
            BizProjectRecordDetailVo projectRecordDetailVo = bizProjectRecordService.selectById(projectId);
            BeanUtils.copyProperties(projectRecordDetailVo, bizProjectRecordDetailVo);
            return bizProjectRecordDetailVo;
        } else {
            throw new RuntimeException("审核失败,请联系管理员");
        }
    }

    /**
     * 审核
     * @param teamAuditDTO 参数DTO
     * @return 返回结果
     */
    @Override
    public int addTeamAudit(TeamAuditDTO teamAuditDTO) {
        int flag = 0;
        BizProjectRecord bizProjectRecord = bizProjectRecordMapper.selectOne(new LambdaQueryWrapper<BizProjectRecord>()
                .eq(BizProjectRecord::getProjectId, teamAuditDTO.getProjectId())
                .eq(BizProjectRecord::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(bizProjectRecord)) {
            throw new RuntimeException("未找到此工程填报信息,无法进行审核");
        }
        Integer i = teamAuditMapper.selectMaxNumber(teamAuditDTO.getProjectId());
        TeamAuditEntity teamAuditEntity = new TeamAuditEntity();
        if (i.equals(0)) {
            teamAuditEntity.setNumber(ConstantsInfo.NUMBER);
        }
        int newNumber = i + ConstantsInfo.NUMBER;
        if (newNumber < 0) {
            throw new RuntimeException("该填报信息审核次数过多,请联系管理员");
        }
        teamAuditEntity.setNumber(newNumber);
        teamAuditEntity.setProjectId(teamAuditDTO.getProjectId());
        teamAuditEntity.setAuditResult(teamAuditDTO.getAuditResult());
        teamAuditEntity.setRejectionReason(teamAuditDTO.getRejectionReason());
        if (teamAuditDTO.getAuditResult().equals(ConstantsInfo.AUDIT_SUCCESS)) {
            teamAuditEntity.setDepartAuditState(ConstantsInfo.AUDIT_STATUS_DICT_VALUE);
        }
        teamAuditEntity.setCreateBy(SecurityUtils.getUserId());
        teamAuditEntity.setCreateTime(System.currentTimeMillis());
        teamAuditEntity.setDelFlag(ConstantsInfo.ZERO_DEL_FLAG);
        flag = teamAuditMapper.insert(teamAuditEntity);
        if (flag > 0) {
            if (ConstantsInfo.AUDIT_REJECT.equals(teamAuditDTO.getAuditResult())) {
                bizProjectRecord.setStatus(Integer.valueOf(ConstantsInfo.REJECTED));
                bizProjectRecordMapper.updateById(bizProjectRecord);
            }
        } else {
            throw new RuntimeException("审核失败,请联系管理员");
        }
        return flag;
    }

    /**
     * 分页查询
     * @param selectProjectDTO 查询参数DTO
     * @param pageNum 当前页码
     * @param pageSize 条数
     * @return 返回结果
     */
    @DataScopeSelf
    @Override
    public TableData queryByPage(BasePermission permission, SelectProjectDTO selectProjectDTO, Integer pageNum, Integer pageSize) {
        TableData result = new TableData();
        if (null == pageNum || pageNum < 1) {
            pageNum = 1;
        }
        if (null == pageSize || pageSize < 1) {
            pageSize = 10;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        PageHelper.startPage(pageNum, pageSize);
        Page<ProjectVO> page = teamAuditMapper.queryByPage(selectProjectDTO, permission.getDeptIds(), permission.getDateScopeSelf(), currentUser.getUserName());
        Page<ProjectVO> projectVOPage = getProjectListFmt(page);
        result.setTotal(projectVOPage.getTotal());
        result.setRows(projectVOPage.getResult());
        return result;
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