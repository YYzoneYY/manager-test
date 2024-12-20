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
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.*;
import com.ruoyi.system.domain.dto.AreaDTO;
import com.ruoyi.system.domain.dto.PlanDTO;
import com.ruoyi.system.domain.dto.RelatesInfoDTO;
import com.ruoyi.system.domain.dto.SelectPlanDTO;
import com.ruoyi.system.domain.vo.PlanVO;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.ContentsService;
import com.ruoyi.system.service.PlanService;
import com.ruoyi.system.service.RelatesInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/22
 * @description:
 */

@Service
@Transactional
public class PlanServiceImpl extends ServiceImpl<PlanMapper, PlanEntity> implements PlanService {

    @Resource
    private PlanMapper planMapper;

    @Resource
    private RelatesInfoMapper relatesInfoMapper;

    @Resource
    private PlanContentsMappingMapper planContentsMappingMapper;

    @Resource
    private RelatesInfoService relatesInfoService;

    @Resource
    private PlanAuditMapper planAuditMapper;

    @Resource
    private SysDictDataMapper sysDictDataMapper;

    @Resource
    private ContentsService contentsService;

    @Resource
    private BizProjectRecordMapper bizProjectRecordMapper;

    /**
     * 工程计划新增
     * @param planDTO 参数DTO
     * @return 返回结果
     */
    @Override
    public int insertPlan(PlanDTO planDTO) {
        int flag = 0;
        // 参数校验
        checkParameter(planDTO);
        if (ObjectUtil.isNotNull(planDTO.getPlanName())) {
            // 计划名称不能重复
            Long selectCount = planMapper.selectCount(new LambdaQueryWrapper<PlanEntity>()
                    .eq(PlanEntity::getPlanName, planDTO.getPlanName())
                    .eq(PlanEntity::getType, planDTO.getType())
                    .eq(PlanEntity::getPlanType, planDTO.getPlanType())
                    .eq(PlanEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (selectCount > 0) {
                throw new RuntimeException("计划名称已存在");
            }
        }
        PlanEntity planEntity = new PlanEntity();
        BeanUtils.copyProperties(planDTO, planEntity);
        planEntity.setCreateBy(1L);
        planEntity.setCreateTime(System.currentTimeMillis());
        planEntity.setState(ConstantsInfo.AUDIT_STATUS_DICT_VALUE);
        planEntity.setDelFlag(ConstantsInfo.ZERO_DEL_FLAG);
        flag = planMapper.insert(planEntity);
        if (flag > 0) {
            // 关联信息
            if (ObjectUtil.isNotNull(planDTO.getRelatesInfoDTOS()) && !planDTO.getRelatesInfoDTOS().isEmpty()) {
                relatesInfoService.insert(planEntity.getPlanId(), planDTO.getPlanType(),
                        planDTO.getType(), planDTO.getRelatesInfoDTOS());
            }
            // 目录计划映射
            PlanContentsMappingEntity planContentsMappingEntity = new PlanContentsMappingEntity();
            planContentsMappingEntity.setContentsId(planDTO.getContentsId());
            planContentsMappingEntity.setPlanId(planEntity.getPlanId());
            planContentsMappingMapper.insert(planContentsMappingEntity);
        } else {
            throw new RuntimeException("计划添加失败");
        }
        return flag;
    }

    /**
     * 工程计划编辑
     * @param planDTO 参数DTO
     * @return 返回结果
     */
    @Override
    public int updatePlan(PlanDTO planDTO) {
        int flag = 0;
        // 参数校验
        checkParameter(planDTO);
        PlanEntity planEntity = planMapper.selectOne(new LambdaQueryWrapper<PlanEntity>()
                .eq(PlanEntity::getPlanId, planDTO.getPlanId())
                .eq(PlanEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isEmpty(planEntity)) {
            throw new RuntimeException("该计划不存在");
        }
        Long selectCount = planMapper.selectCount(new LambdaQueryWrapper<PlanEntity>()
                .eq(PlanEntity::getPlanName, planDTO.getPlanName())
                .eq(PlanEntity::getType, planDTO.getType())
                .eq(PlanEntity::getPlanType, planDTO.getPlanType())
                .eq(PlanEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
                .ne(PlanEntity::getPlanId, planDTO.getPlanId()));
        if (selectCount > 0) {
            throw new RuntimeException("计划名称已存在");
        }
        Long planId = planEntity.getPlanId();
        BeanUtils.copyProperties(planDTO, planEntity);
        planEntity.setPlanId(planId);
        planEntity.setUpdateTime(System.currentTimeMillis());
        planEntity.setUpdateBy(1L);
        flag = planMapper.updateById(planEntity);
        if (flag > 0) {
            // 关联信息修改
            if (ObjectUtil.isNotNull(planDTO.getRelatesInfoDTOS()) && !planDTO.getRelatesInfoDTOS().isEmpty()) {
                List<Long> planIdList = new ArrayList<>();
                planIdList.add(planId);
                relatesInfoService.deleteById(planIdList);
                relatesInfoService.insert(planEntity.getPlanId(), planDTO.getPlanType(),
                        planDTO.getType(), planDTO.getRelatesInfoDTOS());
            }
        } else {
            throw new RuntimeException("计划编辑失败");
        }
        return flag;
    }

    /**
     * 根据id查询
     * @param planId 计划id
     * @return 返回结果
     */
    @Override
    public PlanDTO queryById(Long planId) {
        if (ObjectUtil.isNull(planId)) {
            throw new RuntimeException("参数错误,主键不能为空");
        }
        PlanEntity planEntity = planMapper.selectOne(new LambdaQueryWrapper<PlanEntity>()
                .eq(PlanEntity::getPlanId, planId)
                .eq(PlanEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(planEntity)) {
            throw new RuntimeException("未找到此计划");
        }
        PlanDTO planDTO = new PlanDTO();
        BeanUtils.copyProperties(planEntity, planDTO);
        PlanContentsMappingEntity planContentsMappingEntity = planContentsMappingMapper.selectOne(
                new LambdaQueryWrapper<PlanContentsMappingEntity>()
                .eq(PlanContentsMappingEntity::getPlanId, planId));
        if (ObjectUtil.isNotNull(planContentsMappingEntity)) {
            planDTO.setContentsId(planContentsMappingEntity.getContentsId());
        }
        // 关联信息
        List<RelatesInfoDTO> relatesInfoDTOS = relatesInfoService.getByPlanId(planId);
        planDTO.setRelatesInfoDTOS(relatesInfoDTOS);
        if (ObjectUtil.isNotNull(planEntity.getStartTime())) {
            planDTO.setStartTimeFmt(DateUtils.getDateStrByTime(planEntity.getStartTime()));
        }
        if (ObjectUtil.isNotNull(planEntity.getEndTime())) {
            planDTO.setEndTimeFmt(DateUtils.getDateStrByTime(planEntity.getEndTime()));
        }
        if (planEntity.getState().equals(ConstantsInfo.REJECTED)) {
            // 获取驳回原因
            planDTO.setRejectReason(getRejectReason(planEntity.getPlanId()));
        }
        return planDTO;
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
        List<Long> panIds = contentsService.queryByCondition(selectPlanDTO.getContentsId());
        PageHelper.startPage(pageNum, pageSize);
        if (ObjectUtil.isNotNull(panIds) && panIds.size() > 0) {
            Page<PlanVO> page = planMapper.queryPage(selectPlanDTO, panIds);
            if (ListUtils.isNotNull(page.getResult())) {
                page.getResult().forEach(planVO -> {
                    List<RelatesInfoDTO> relatesInfoDTOS = relatesInfoService.getByPlanId(planVO.getPlanId());
                    planVO.setRelatesInfoDTOS(relatesInfoDTOS);
                    planVO.setStartTimeFmt(DateUtils.getDateStrByTime(planVO.getStartTime()));
                    planVO.setEndTimeFmt(DateUtils.getDateStrByTime(planVO.getEndTime()));
                    //审核状态字典lable
                    String auditStatus = sysDictDataMapper.selectDictLabel(ConstantsInfo.AUDIT_STATUS_DICT_TYPE, planVO.getState());
                    planVO.setStatusFmt(auditStatus);
                    if (planVO.getState().equals(ConstantsInfo.REJECTED)) {
                        // 获取驳回原因
                        planVO.setRejectReason(getRejectReason(planVO.getPlanId()));
                    }
                });
            }
            result.setTotal(page.getTotal());
            result.setRows(page.getResult());
        } else {
            result.setTotal(0L);
            result.setRows(new ArrayList<>());
        }
        return result;
    }

    /**
     * 撤回
     * @param planId 计划id
     * @return 返回结果
     */
    @Override
    public String withdraw(Long planId) {
        String flag = "";
        if (ObjectUtil.isEmpty(planId)) {
            throw new RuntimeException("参数错误,主键不能为空");
        }
        PlanEntity entity = planMapper.selectOne(new LambdaQueryWrapper<PlanEntity>()
                .eq(PlanEntity::getPlanId, planId)
                .eq(PlanEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(entity)) {
            throw new RuntimeException("未找到此计划");
        }
        checkStatus(entity.getState());
        PlanEntity planEntity = new PlanEntity();
        BeanUtils.copyProperties(entity, planEntity);
        planEntity.setState(ConstantsInfo.AUDIT_STATUS_DICT_VALUE);
        flag = planMapper.updateById(planEntity) > 0 ? "撤回成功" :  "撤回失败,请联系管理员";
        return flag;
    }

    /**
     * 批量删除
     * @param planIds 主键id数组
     * @return 返回结果
     */
    @Override
    public boolean deletePlan(Long[] planIds) {
        boolean flag = false;
        if (planIds.length == 0) {
            throw new RuntimeException("请选择要删除的数据!");
        }
        List<Long > planIdList = Arrays.asList(planIds);
        planIdList.forEach(planId -> {
            List<BizProjectRecord> bizProjectRecords = bizProjectRecordMapper.selectList(new LambdaQueryWrapper<BizProjectRecord>()
                    .eq(BizProjectRecord::getPlanId, planId)
                    .eq(BizProjectRecord::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ListUtils.isNotNull(bizProjectRecords)) {
                throw new RuntimeException("该计划下有未完成的项目,不能删除");
            }
        });
        flag = this.removeBatchByIds(planIdList);
        if (flag) {
            relatesInfoService.deleteById(planIdList);
            planContentsMappingMapper.deleteBatchIds(planIdList);
        }
        return flag;
    }

    /**
     * 获取驳回原因
     */
    private String getRejectReason(Long planId) {
        PlanAuditEntity planAuditEntity = planAuditMapper.selectOne(new LambdaQueryWrapper<PlanAuditEntity>()
                .eq(PlanAuditEntity::getPlanId, planId)
                .eq(PlanAuditEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(planAuditEntity)) {
            throw new RuntimeException("未找到此计划");
        }
        return planAuditEntity.getAuditResult();
    }

    /**
     * 参数校验
     */
    private void checkParameter(PlanDTO planDTO) {
        if (ObjectUtil.isNull(planDTO)) {
            throw new RuntimeException("参数错误,不能为空");
        }
        if (ObjectUtil.isNull(planDTO.getContentsId())) {
            throw new RuntimeException("目录id不能为空");
        }
        if (ListUtils.isNull(planDTO.getRelatesInfoDTOS())) {
            throw new RuntimeException("关联信息不能为空");
        }
        // 关联信息校验
        planDTO.getRelatesInfoDTOS().forEach(relatesInfoDTO -> {
            List<AreaDTO> areaDTOS = relatesInfoDTO.getAreaDTOS();
            String areaDTOFmt = areaDTOS.toString();
            // 判断同一类型是否有重复的区域
            Long selectCount = relatesInfoMapper.selectCount(new LambdaQueryWrapper<RelatesInfoEntity>()
                    .eq(RelatesInfoEntity::getType, planDTO.getType())
                    .eq(RelatesInfoEntity::getArea, areaDTOFmt));
            if (selectCount > 0) {
                throw new RuntimeException("同一类型,区域不可重复!");
            }
            // 判断除特殊计划之外是否有重复的区域
            if (!planDTO.getPlanType().equals(ConstantsInfo.SPECIAL_plan)) {
                Long aLong = relatesInfoMapper.selectCount(new LambdaQueryWrapper<RelatesInfoEntity>()
                        .eq(RelatesInfoEntity::getArea, areaDTOFmt));
                if (aLong > 0) {
                    throw new RuntimeException("除特殊计划外，区域不可重复!");
                }
            }
        });
    }

    /**
     * 状态校验
     */
    private void checkStatus(String status) {
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