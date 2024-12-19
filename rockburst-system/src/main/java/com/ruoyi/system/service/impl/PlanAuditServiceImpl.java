package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.Entity.PlanEntity;
import com.ruoyi.system.domain.Entity.PlanAuditEntity;
import com.ruoyi.system.domain.dto.PlanAuditDTO;
import com.ruoyi.system.domain.dto.RelatesInfoDTO;
import com.ruoyi.system.domain.dto.SelectPlanDTO;
import com.ruoyi.system.domain.vo.PlanVO;
import com.ruoyi.system.mapper.PlanMapper;
import com.ruoyi.system.mapper.PlanAuditMapper;
import com.ruoyi.system.mapper.SysDictDataMapper;
import com.ruoyi.system.service.ContentsService;
import com.ruoyi.system.service.PlanAuditService;
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

    @Override
    public int audit(Long planId) {
        int flag = 0;
        if (ObjectUtil.isNull(planId)) {
            throw new RuntimeException("参数错误");
        }
        PlanEntity engineeringPlanEntity = planMapper.selectOne(new LambdaQueryWrapper<PlanEntity>()
                .eq(PlanEntity::getPlanId, planId)
                .eq(PlanEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(engineeringPlanEntity)) {
            throw new RuntimeException("未找到此计划,无法进行审核");
        }
        PlanEntity planEntity = new PlanEntity();
        BeanUtils.copyProperties(engineeringPlanEntity, planEntity);
        planEntity.setState(ConstantsInfo.IN_REVIEW_DICT_VALUE);
        flag = planMapper.updateById(planEntity);
        if (flag <= 0) {
            throw new RuntimeException("审核失败,请联系管理员");
        }
        return flag;
    }

    @Override
    public int addAudit(PlanAuditDTO planAuditDTO) {
        int flag = 0;
        PlanEntity engineeringPlanEntity = planMapper.selectOne(new LambdaQueryWrapper<PlanEntity>()
                .eq(PlanEntity::getPlanId, planAuditDTO.getPlanId())
                .eq(PlanEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(engineeringPlanEntity)) {
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
        planAuditEntity.setCreateBy(1L);
        planAuditEntity.setCreateTime(System.currentTimeMillis());
        planAuditEntity.setDelFlag(ConstantsInfo.ZERO_DEL_FLAG);
        flag = planAuditMapper.insert(planAuditEntity);
        if (flag <= 0) {
            throw new RuntimeException("审核失败,请联系管理员");
        } else {
            if (ConstantsInfo.AUDIT_SUCCESS.equals(planAuditDTO.getAuditResult())) {
                engineeringPlanEntity.setState(ConstantsInfo.AUDITED_DICT_VALUE);
            }
            engineeringPlanEntity.setState(ConstantsInfo.REJECTED);
            PlanEntity planEntity = new PlanEntity();
            BeanUtils.copyProperties(engineeringPlanEntity, planEntity);
            int update = planMapper.updateById(planEntity);
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
    @Override
    public TableData queryPage(SelectPlanDTO selectPlanDTO, Integer pageNum, Integer pageSize) {
        TableData result = new TableData();
        if (null == pageNum || pageNum < 1) {
            pageNum = 1;
        }
        if (null == pageSize || pageSize < 1) {
            pageSize = 10;
        }
        PageHelper.startPage(pageNum, pageSize);
        Page<PlanVO> page = planAuditMapper.queryPage(selectPlanDTO);
        Page<PlanVO> planVOPage = getPlanListFmt(page);
        result.setTotal(planVOPage.getTotal());
        result.setRows(planVOPage.getResult());
        return result;
    }

    @Override
    public TableData auditHistoryPage(SelectPlanDTO selectPlanDTO, Integer pageNum, Integer pageSize) {
        TableData result = new TableData();
        if (null == pageNum || pageNum < 1) {
            pageNum = 1;
        }
        if (null == pageSize || pageSize < 1) {
            pageSize = 10;
        }
        List<Long> panIds = contentsService.queryByCondition(selectPlanDTO.getContentsId());
        PageHelper.startPage(pageNum, pageSize);
        if (ObjectUtil.isNotNull(panIds) && panIds.size() > 0) {
            Page<PlanVO> page = planAuditMapper.auditHistoryPage(selectPlanDTO, panIds);
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
    private String getRejectReason(Long engineeringPlanId) {
        PlanAuditEntity planAuditEntity = planAuditMapper.selectById(engineeringPlanId);
        if (ObjectUtil.isNull(planAuditEntity)) {
            throw new RuntimeException("未找到此计划");
        }
        return planAuditEntity.getAuditResult();
    }
}