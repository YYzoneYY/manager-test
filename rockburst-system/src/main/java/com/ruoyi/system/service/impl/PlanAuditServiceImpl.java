package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.Entity.EngineeringPlanEntity;
import com.ruoyi.system.domain.Entity.PlanAuditEntity;
import com.ruoyi.system.domain.dto.PlanAuditDTO;
import com.ruoyi.system.domain.dto.SelectPlanDTO;
import com.ruoyi.system.domain.vo.EngineeringPlanVO;
import com.ruoyi.system.mapper.EngineeringPlanMapper;
import com.ruoyi.system.mapper.PlanAuditMapper;
import com.ruoyi.system.mapper.SysDictDataMapper;
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
    private EngineeringPlanMapper engineeringPlanMapper;

    @Resource
    private SysDictDataMapper sysDictDataMapper;

    @Resource
    private EngineeringPlanServiceImpl engineeringPlanService;

    @Override
    public int audit(Long engineeringPlanId) {
        int flag = 0;
        if (ObjectUtil.isNull(engineeringPlanId)) {
            throw new RuntimeException("参数错误");
        }
        EngineeringPlanEntity engineeringPlanEntity = engineeringPlanMapper.selectOne(new LambdaQueryWrapper<EngineeringPlanEntity>()
                .eq(EngineeringPlanEntity::getEngineeringPlanId, engineeringPlanId)
                .eq(EngineeringPlanEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(engineeringPlanEntity)) {
            throw new RuntimeException("未找到此计划,无法进行审核");
        }
        EngineeringPlanEntity planEntity = new EngineeringPlanEntity();
        BeanUtils.copyProperties(engineeringPlanEntity, planEntity);
        planEntity.setState(ConstantsInfo.IN_REVIEW_DICT_VALUE);
        planEntity.setUpdateBy(1L);
        planEntity.setUpdateTime(System.currentTimeMillis());
        flag = engineeringPlanMapper.updateById(planEntity);
        if (flag <= 0) {
            throw new RuntimeException("审核失败,请联系管理员");
        }
        return flag;
    }

    @Override
    public int addAudit(PlanAuditDTO planAuditDTO) {
        int flag = 0;
        EngineeringPlanEntity engineeringPlanEntity = engineeringPlanMapper.selectOne(new LambdaQueryWrapper<EngineeringPlanEntity>()
                .eq(EngineeringPlanEntity::getEngineeringPlanId, planAuditDTO.getEngineeringPlanId())
                .eq(EngineeringPlanEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(engineeringPlanEntity)) {
            throw new RuntimeException("未找到此计划,无法进行审核");
        }
        Integer i = planAuditMapper.selectMaxNumber(planAuditDTO.getEngineeringPlanId());
        PlanAuditEntity planAuditEntity = new PlanAuditEntity();
        if (i.equals(0)) {
            planAuditEntity.setNumber(ConstantsInfo.NUMBER);
        }
        int newNumber = i + ConstantsInfo.NUMBER;
        if (newNumber < 0) {
            throw new RuntimeException("该计划审核次数过多,请联系管理员");
        }
        planAuditEntity.setNumber(newNumber);
        planAuditEntity.setEngineeringPlanId(planAuditDTO.getEngineeringPlanId());
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
            EngineeringPlanEntity planEntity = new EngineeringPlanEntity();
            BeanUtils.copyProperties(engineeringPlanEntity, planEntity);
            int update = engineeringPlanMapper.updateById(planEntity);
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
        Page<EngineeringPlanVO> page = planAuditMapper.queryPage(selectPlanDTO);
        Page<EngineeringPlanVO> planVOPage = getPlanListFmt(page);
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
        PageHelper.startPage(pageNum, pageSize);
        Page<EngineeringPlanVO> page = planAuditMapper.auditHistoryPage(selectPlanDTO);
        Page<EngineeringPlanVO> planVOPage = getPlanListFmt(page);
        result.setTotal(planVOPage.getTotal());
        result.setRows(planVOPage.getResult());
        return result;
    }

    /**
     * VO格式化
     */
    private Page<EngineeringPlanVO> getPlanListFmt(Page<EngineeringPlanVO> list) {
        if (ListUtils.isNotNull(list.getResult())) {
            list.getResult().forEach(engineeringPlanVO -> {
                engineeringPlanVO.setConstructionUnitName(this.engineeringPlanService.getConstructionUnitName(engineeringPlanVO.getConstructionUnitId()));
                engineeringPlanVO.setConstructSiteFmt(this.engineeringPlanService.getConstructSite(engineeringPlanVO.getConstructSite(), engineeringPlanVO.getType()));
                engineeringPlanVO.setStartTimeFmt(DateUtils.getDateStrByTime(engineeringPlanVO.getStartTime()));
                engineeringPlanVO.setEndTimeFmt(DateUtils.getDateStrByTime(engineeringPlanVO.getEndTime()));
                //审核状态字典lable
                String auditStatus = sysDictDataMapper.selectDictLabel(ConstantsInfo.AUDIT_STATUS_DICT_TYPE, engineeringPlanVO.getState());
                engineeringPlanVO.setStatusFmt(auditStatus);
                if (engineeringPlanVO.getState().equals(ConstantsInfo.REJECTED)) {
                    // 获取驳回原因
                    String rejectReason = getRejectReason(engineeringPlanVO.getEngineeringPlanId());
                    engineeringPlanVO.setRejectReason(rejectReason);
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