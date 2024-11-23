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
import com.ruoyi.system.domain.BizProjectAudit;
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.ConstructionUnitEntity;
import com.ruoyi.system.domain.Entity.EngineeringPlanEntity;
import com.ruoyi.system.domain.Entity.PlanAuditEntity;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.EngineeringPlanDTO;
import com.ruoyi.system.domain.dto.SelectPlanDTO;
import com.ruoyi.system.domain.vo.EngineeringPlanVO;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.EngineeringPlanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/22
 * @description:
 */

@Service
@Transactional
public class EngineeringPlanServiceImpl extends ServiceImpl<EngineeringPlanMapper, EngineeringPlanEntity> implements EngineeringPlanService {

    @Resource
    private EngineeringPlanMapper engineeringPlanMapper;

    @Resource
    private PlanAuditMapper planAuditMapper;

    @Resource
    private SysDictDataMapper sysDictDataMapper;

    @Resource
    private ConstructionUnitMapper constructionUnitMapper;

    @Resource
    private TunnelMapper tunnelMapper;

    @Resource
    private BizWorkfaceMapper bizWorkfaceMapper;

    @Resource
    private BizProjectRecordMapper bizProjectRecordMapper;

    /**
     * 工程计划新增
     * @param engineeringPlanDTO 参数DTO
     * @return 返回结果
     */
    @Override
    public int insertPlan(EngineeringPlanDTO engineeringPlanDTO) {
        int flag = 0;
        // 同类型的计划名称不能重复
        Long selectCount = engineeringPlanMapper.selectCount(new LambdaQueryWrapper<EngineeringPlanEntity>()
                .eq(EngineeringPlanEntity::getPlanName, engineeringPlanDTO.getPlanName())
                .eq(EngineeringPlanEntity::getType, engineeringPlanDTO.getType())
                .eq(EngineeringPlanEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (selectCount > 0) {
            throw new RuntimeException("计划名称已存在");
        }
        EngineeringPlanEntity engineeringPlanEntity = new EngineeringPlanEntity();
        BeanUtils.copyProperties(engineeringPlanDTO, engineeringPlanEntity);
        engineeringPlanEntity.setCreateBy(1L);
        engineeringPlanEntity.setCreateTime(System.currentTimeMillis());
        engineeringPlanEntity.setState(ConstantsInfo.TO_BE_SUBMITTED);
        engineeringPlanEntity.setDelFlag(ConstantsInfo.ZERO_DEL_FLAG);
        flag = engineeringPlanMapper.insert(engineeringPlanEntity);
        if (flag <= 0) {
            throw new RuntimeException("计划添加失败");
        }
        return flag;
    }

    /**
     * 工程计划编辑
     * @param engineeringPlanDTO 参数DTO
     * @return 返回结果
     */
    @Override
    public int updatePlan(EngineeringPlanDTO engineeringPlanDTO) {
        int flag = 0;
        EngineeringPlanEntity engineeringPlanEntity = engineeringPlanMapper.selectById(engineeringPlanDTO.getEngineeringPlanId());
        if (ObjectUtil.isEmpty(engineeringPlanEntity)) {
            throw new RuntimeException("该计划不存在");
        }
        Long selectCount = engineeringPlanMapper.selectCount(new LambdaQueryWrapper<EngineeringPlanEntity>()
                .eq(EngineeringPlanEntity::getPlanName, engineeringPlanDTO.getPlanName())
                .eq(EngineeringPlanEntity::getType, engineeringPlanDTO.getType())
                .eq(EngineeringPlanEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
                .ne(EngineeringPlanEntity::getEngineeringPlanId, engineeringPlanDTO.getEngineeringPlanId()));
        if (selectCount > 0) {
            throw new RuntimeException("计划名称已存在");
        }
        Long engineeringPlanId = engineeringPlanEntity.getEngineeringPlanId();
        BeanUtils.copyProperties(engineeringPlanDTO, engineeringPlanEntity);
        engineeringPlanEntity.setEngineeringPlanId(engineeringPlanId);
        engineeringPlanEntity.setUpdateTime(System.currentTimeMillis());
        engineeringPlanEntity.setUpdateBy(1L);
        flag = engineeringPlanMapper.updateById(engineeringPlanEntity);
        if (flag <= 0) {
            throw new RuntimeException("计划编辑失败");
        }
        return flag;
    }

    /**
     * 根据id查询
     * @param engineeringPlanId 计划id
     * @return 返回结果
     */
    @Override
    public EngineeringPlanDTO queryById(Long engineeringPlanId) {
        if (ObjectUtil.isNull(engineeringPlanId)) {
            throw new RuntimeException("参数错误,主键不能为空");
        }
        EngineeringPlanEntity engineeringPlanEntity = engineeringPlanMapper.selectById(engineeringPlanId);
        if (ObjectUtil.isNull(engineeringPlanEntity)) {
            throw new RuntimeException("未找到此计划");
        }
        EngineeringPlanDTO engineeringPlanDTO = new EngineeringPlanDTO();
        BeanUtils.copyProperties(engineeringPlanEntity, engineeringPlanDTO);
        engineeringPlanDTO.setConstructionUnitName(getConstructionUnitName(engineeringPlanEntity.getConstructionUnitId()));
        engineeringPlanDTO.setConstructSiteFmt(getConstructSite(engineeringPlanEntity.getConstructSite(), engineeringPlanEntity.getType()));
        engineeringPlanDTO.setStartTimeFmt(DateUtils.getDateStrByTime(engineeringPlanEntity.getStartTime()));
        engineeringPlanDTO.setEndTimeFmt(DateUtils.getDateStrByTime(engineeringPlanEntity.getEndTime()));
        if (engineeringPlanEntity.getState().equals(ConstantsInfo.REJECTED)) {
            // 获取驳回原因
            engineeringPlanDTO.setRejectReason(getRejectReason(engineeringPlanEntity.getEngineeringPlanId()));
        }
        return engineeringPlanDTO;
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
        Page<EngineeringPlanVO> page = engineeringPlanMapper.queryPage(selectPlanDTO);
        if (ListUtils.isNotNull(page.getResult())) {
            page.getResult().forEach(engineeringPlanVO -> {
                engineeringPlanVO.setConstructionUnitName(getConstructionUnitName(engineeringPlanVO.getConstructionUnitId()));
                engineeringPlanVO.setConstructSiteFmt(getConstructSite(engineeringPlanVO.getConstructSite(), engineeringPlanVO.getType()));
                engineeringPlanVO.setStartTimeFmt(DateUtils.getDateStrByTime(engineeringPlanVO.getStartTime()));
                engineeringPlanVO.setEndTimeFmt(DateUtils.getDateStrByTime(engineeringPlanVO.getEndTime()));
                //审核状态字典lable
                String auditStatus = sysDictDataMapper.selectDictLabel(ConstantsInfo.AUDIT_STATUS_DICT_TYPE, engineeringPlanVO.getState());
                engineeringPlanVO.setStatusFmt(auditStatus);
                if (engineeringPlanVO.getState().equals(ConstantsInfo.REJECTED)) {
                    // 获取驳回原因
                    engineeringPlanVO.setRejectReason(getRejectReason(engineeringPlanVO.getEngineeringPlanId()));
                }
            });
        }
        result.setTotal(page.getTotal());
        result.setRows(page.getResult());
        return result;
    }

    /**
     * 提交审核
     * @param engineeringPlanId 计划id
     * @return 返回结果
     */
    @Override
    public String submitForReview(Long engineeringPlanId) {
        String flag = "";
        if (ObjectUtil.isNull(engineeringPlanId)) {
            throw new RuntimeException("参数错误,主键不能为空");
        }
        EngineeringPlanEntity engineeringPlanEntity = engineeringPlanMapper.selectById(engineeringPlanId);
        if (ObjectUtil.isNull(engineeringPlanEntity)) {
            throw new RuntimeException("未找到此计划");
        }
        EngineeringPlanEntity planEntity = new EngineeringPlanEntity();
        BeanUtils.copyProperties(engineeringPlanEntity, planEntity);
        planEntity.setState(ConstantsInfo.AUDIT_STATUS_DICT_VALUE);
        flag = engineeringPlanMapper.updateById(planEntity) > 0 ? "提交审核成功" : "提交审核失败,请联系管理员";
        return flag;
    }

    /**
     * 撤回
     * @param engineeringPlanId 计划id
     * @return 返回结果
     */
    @Override
    public String withdraw(Long engineeringPlanId) {
        String flag = "";
        if (ObjectUtil.isEmpty(engineeringPlanId)) {
            throw new RuntimeException("参数错误,主键不能为空");
        }
        EngineeringPlanEntity engineeringPlanEntity = engineeringPlanMapper.selectById(engineeringPlanId);
        if (ObjectUtil.isNull(engineeringPlanEntity)) {
            throw new RuntimeException("未找到此计划");
        }
        checkStatus(engineeringPlanEntity.getState());
        EngineeringPlanEntity planEntity = new EngineeringPlanEntity();
        BeanUtils.copyProperties(engineeringPlanEntity, planEntity);
        planEntity.setState(ConstantsInfo.TO_BE_SUBMITTED);
        flag = engineeringPlanMapper.updateById(planEntity) > 0 ? "撤回成功" :  "撤回失败,请联系管理员";
        return flag;
    }

    /**
     * 批量删除
     * @param engineeringPlanIds 主键id数组
     * @return 返回结果
     */
    @Override
    public boolean deletePlan(Long[] engineeringPlanIds) {
        boolean flag = false;
        if (engineeringPlanIds.length == 0) {
            throw new RuntimeException("请选择要删除的数据!");
        }
        List<Long > planIds = Arrays.asList(engineeringPlanIds);
//        planIds.forEach(planId -> {
//            List<BizProjectRecord> bizProjectRecords = bizProjectRecordMapper.selectList(new LambdaQueryWrapper<BizProjectRecord>()
//                    .eq(BizProjectRecord::getPlanId, planId)
//                    .eq(BizProjectRecord::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
//            if (ListUtils.isNotNull(bizProjectRecords)) {
//                throw new RuntimeException("该计划下有未完成的项目,不能删除");
//            }
//        });
        flag = this.removeBatchByIds(planIds);
        return flag;
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
    private String getRejectReason(Long engineeringPlanId) {
        PlanAuditEntity planAuditEntity = planAuditMapper.selectById(engineeringPlanId);
        if (ObjectUtil.isNull(planAuditEntity)) {
            throw new RuntimeException("未找到此计划");
        }
        return planAuditEntity.getAuditResult();
    }

    /**
     * 状态校验
     */
    private void checkStatus(String status) {
        if (status.equals(ConstantsInfo.TO_BE_SUBMITTED)) {
            throw new RuntimeException("此计划还未提交审核,无法撤回");
        }
        if (status.equals(ConstantsInfo.IN_REVIEW_DICT_VALUE)) {
            throw new RuntimeException("此计划正在审核中,无法撤回");
        }
        if (status.equals(ConstantsInfo.AUDITED_DICT_VALUE)) {
            throw new RuntimeException("此计划已审核通过,无法撤回");
        }
        if (status.equals(ConstantsInfo.REJECTED)) {
            throw new RuntimeException("此计划已驳回,无法撤回");
        }
    }
}