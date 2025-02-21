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
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.Entity.PlanContentsMappingEntity;
import com.ruoyi.system.domain.Entity.PlanAuditEntity;
import com.ruoyi.system.domain.Entity.PlanEntity;
import com.ruoyi.system.domain.dto.PlanAuditDTO;
import com.ruoyi.system.domain.dto.PlanDTO;
import com.ruoyi.system.domain.dto.SelectPlanDTO;
import com.ruoyi.system.domain.vo.PlanVO;
import com.ruoyi.system.mapper.PlanContentsMappingMapper;
import com.ruoyi.system.mapper.PlanAuditMapper;
import com.ruoyi.system.mapper.PlanMapper;
import com.ruoyi.system.mapper.SysDictDataMapper;
import com.ruoyi.system.service.ContentsService;
import com.ruoyi.system.service.PlanAuditService;
import com.ruoyi.system.service.RelatesInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/23
 * @description:
 */

@Transactional
@Service
public class PlanAuditServiceImpl extends ServiceImpl<PlanAuditMapper, PlanAuditEntity> implements PlanAuditService {

    @Resource
    private PlanAuditMapper planAuditMapper;

    @Resource
    private PlanMapper planMapper;

    @Resource
    private SysDictDataMapper sysDictDataMapper;

    @Resource
    private PlanServiceImpl planService;

    @Resource
    private ContentsService contentsService;

    @Resource
    private PlanContentsMappingMapper planContentsMappingMapper;

    @Resource
    private RelatesInfoService relatesInfoService;

    @Override
    public PlanVO audit(Long planId) {
        if (ObjectUtil.isNull(planId)) {
            throw new RuntimeException("参数错误");
        }
        PlanEntity planEntity = planMapper.selectOne(new LambdaQueryWrapper<PlanEntity>()
                .eq(PlanEntity::getPlanId, planId)
                .eq(PlanEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(planEntity)) {
            throw new RuntimeException("未找到此计划,无法进行审核");
        }
        planEntity.setState(ConstantsInfo.IN_REVIEW_DICT_VALUE);
        int update = planMapper.updateById(planEntity);
        if (update > 0) {
            PlanVO planVO = new PlanVO();
            PlanVO dto = planService.queryById(planId);
            BeanUtils.copyProperties(dto, planVO);
            PlanContentsMappingEntity planContentsMappingEntity = planContentsMappingMapper.selectOne(
                    new LambdaQueryWrapper<PlanContentsMappingEntity>()
                            .eq(PlanContentsMappingEntity::getPlanId, planId));
//            if (ObjectUtil.isNotNull(planContentsMappingEntity)) {
//                planDTO.setContentsId(planContentsMappingEntity.getContentsId());
//            }
            // 关联信息
//            List<RelatesInfoDTO> relatesInfoDTOS = relatesInfoService.getByPlanId(planId);
//            planDTO.setRelatesInfoDTOS(relatesInfoDTOS);
            if (ObjectUtil.isNotNull(planEntity.getStartTime())) {
                planVO.setStartTimeFmt(DateUtils.getDateStrByTime(planEntity.getStartTime()));
            }
            if (ObjectUtil.isNotNull(planEntity.getEndTime())) {
                planVO.setEndTimeFmt(DateUtils.getDateStrByTime(planEntity.getEndTime()));
            }
            return planVO;
        } else {
            throw new RuntimeException("审核失败,请联系管理员");
        }
    }

    @Override
    public int addAudit(PlanAuditDTO planAuditDTO) {
        int flag = 0;
        PlanEntity planEntity = planMapper.selectOne(new LambdaQueryWrapper<PlanEntity>()
                .eq(PlanEntity::getPlanId, planAuditDTO.getPlanId())
                .eq(PlanEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(planEntity)) {
            throw new RuntimeException("未找到此计划,无法进行审核");
        }
        Integer i = planAuditMapper.selectMaxNumber(planAuditDTO.getPlanId());
        PlanAuditEntity planAuditEntity = new PlanAuditEntity();
        if (i.equals(0)) {
            planAuditEntity.setNumber(ConstantsInfo.NUMBER);
        }
        int newNumber = i + ConstantsInfo.NUMBER;
        if (newNumber < 0) {
            throw new RuntimeException("该计划审核次数过多,请联系管理员");
        }
        planAuditEntity.setNumber(newNumber);
        planAuditEntity.setPlanId(planAuditDTO.getPlanId());
        planAuditEntity.setAuditResult(planAuditDTO.getAuditResult());
        planAuditEntity.setRejectionReason(planAuditDTO.getRejectionReason());
        planAuditEntity.setCreateBy(SecurityUtils.getUserId());
        planAuditEntity.setCreateTime(System.currentTimeMillis());
        planAuditEntity.setDelFlag(ConstantsInfo.ZERO_DEL_FLAG);
        flag = planAuditMapper.insert(planAuditEntity);
        if (flag <= 0) {
            throw new RuntimeException("审核失败,请联系管理员");
        } else {
            if (ConstantsInfo.AUDIT_SUCCESS.equals(planAuditDTO.getAuditResult())) {
                planEntity.setState(ConstantsInfo.AUDITED_DICT_VALUE);
            } else {
                planEntity.setState(ConstantsInfo.REJECTED);
            }
            PlanEntity plan = new PlanEntity();
            BeanUtils.copyProperties(planEntity, plan);
            int update = planMapper.updateById(plan);
            if (update <= 0) {
                throw new RuntimeException("审核失败,请联系管理员");
            }
        }
        return flag;
    }

    /**
     * 分页查询
     * @param selectPlanDTO 查询参数DTO
     * @param pageNum 当前页码
     * @param pageSize 条数
     * @return 返回结果
     */
    @DataScopeSelf
    @Override
    public TableData queryPage(BasePermission permission, SelectPlanDTO selectPlanDTO, Integer pageNum, Integer pageSize) {
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
        Page<PlanVO> page = planAuditMapper.queryPage(selectPlanDTO, permission.getDeptIds(), permission.getDateScopeSelf(), currentUser.getUserName());
        Page<PlanVO> planVOPage = getPlanListFmt(page);
        result.setTotal(planVOPage.getTotal());
        result.setRows(planVOPage.getResult());
        return result;
    }

    /**
     * 审核历史分页查询
     * @param permission 权限
     * @param selectPlanDTO 查询参数DTO
     * @param pageNum 当前页码
     * @param pageSize 条数
     * @return 返回结果
     */
    @DataScopeSelf
    @Override
    public TableData auditHistoryPage(BasePermission permission, SelectPlanDTO selectPlanDTO, Integer pageNum, Integer pageSize) {
        TableData result = new TableData();
        if (null == pageNum || pageNum < 1) {
            pageNum = 1;
        }
        if (null == pageSize || pageSize < 1) {
            pageSize = 10;
        }
        List<Long> panIds = contentsService.queryByCondition(selectPlanDTO.getContentsId());
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        PageHelper.startPage(pageNum, pageSize);
        if (ObjectUtil.isNotNull(panIds) && !panIds.isEmpty()) {
            Page<PlanVO> page = planAuditMapper.auditHistoryPage(selectPlanDTO, panIds, permission.getDeptIds(), permission.getDateScopeSelf(), currentUser.getUserName());
            Page<PlanVO> planVOPage = getPlanListFmt(page);
            result.setTotal(planVOPage.getTotal());
            result.setRows(planVOPage.getResult());
        } else {
            result.setTotal(0L);
            result.setRows(new ArrayList<>());
        }
        return result;
    }

    /**
     * VO格式化
     */
    private Page<PlanVO> getPlanListFmt(Page<PlanVO> list) {
        if (ListUtils.isNotNull(list.getResult())) {
            list.getResult().forEach(planVO -> {
                planVO.setStartTimeFmt(DateUtils.getDateStrByTime(planVO.getStartTime()));
                planVO.setEndTimeFmt(DateUtils.getDateStrByTime(planVO.getEndTime()));
                //审核状态字典lable
                String auditStatus = sysDictDataMapper.selectDictLabel(ConstantsInfo.AUDIT_STATUS_DICT_TYPE, planVO.getState());
                planVO.setStatusFmt(auditStatus);
                if (planVO.getState().equals(ConstantsInfo.REJECTED)) {
                    // 获取驳回原因
                    String rejectReason = getRejectReason(planVO.getPlanId());
                    planVO.setRejectReason(rejectReason);
                }
            });
        }
        return list;
    }

    /**
     * 获取驳回原因
     */
    private String getRejectReason(Long planId) {
        // 在查询中进行排序和限制，只取最新的一条审核记录
        PlanAuditEntity planAuditEntity = planAuditMapper.selectOne(
                new LambdaQueryWrapper<PlanAuditEntity>()
                        .eq(PlanAuditEntity::getPlanId, planId)
                        .eq(PlanAuditEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
                        .orderByDesc(PlanAuditEntity::getCreateTime)
                        .last("LIMIT 1")
        );
        if (ObjectUtil.isNull(planAuditEntity)) {
            throw new RuntimeException("未找到此计划，计划ID: " + planId);
        }
        return planAuditEntity.getAuditResult();
    }
}