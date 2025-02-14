package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.domain.BizTravePoint;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.*;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.utils.DataJudgeUtils;
import com.ruoyi.system.domain.vo.PlanVO;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.ContentsService;
import com.ruoyi.system.service.PlanService;
import com.ruoyi.system.service.RelatesInfoService;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

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

    @Resource
    private ProjectWarnSchemeMapper projectWarnSchemeMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private BizTravePointMapper bizTravePointMapper;

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
        planEntity.setCreateBy(SecurityUtils.getUserId());
        planEntity.setCreateTime(System.currentTimeMillis());
        planEntity.setState(ConstantsInfo.AUDIT_STATUS_DICT_VALUE);
        SysUser sysUser = sysUserMapper.selectUserById(SecurityUtils.getUserId());
        planEntity.setDeptId(sysUser.getDeptId());
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
        if (planEntity.getState().equals(ConstantsInfo.IN_REVIEW_DICT_VALUE)) {
            throw new RuntimeException("该计划正在审核中，无法编辑");
        }
        if (planEntity.getState().equals(ConstantsInfo.AUDITED_DICT_VALUE)) {
            throw new RuntimeException("该计划已审核通过，无法编辑");
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
        planEntity.setState(ConstantsInfo.AUDIT_STATUS_DICT_VALUE);
        planEntity.setUpdateTime(System.currentTimeMillis());
        planEntity.setUpdateBy(SecurityUtils.getUserId());
        flag = planMapper.updateById(planEntity);
        if (flag > 0) {
            // 关联信息修改
            List<Long> planIdList = new ArrayList<>();
            planIdList.add(planId);
            relatesInfoService.deleteById(planIdList);
            relatesInfoService.insert(planEntity.getPlanId(), planDTO.getPlanType(),
                    planDTO.getType(), planDTO.getRelatesInfoDTOS());
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
        if (planEntity.getState().equals(ConstantsInfo.AUDITED_DICT_VALUE) || planEntity.getState().equals(ConstantsInfo.REJECTED)) {
            // 获取审核结果
            planDTO.setAuditResult(getAuditResult(planEntity.getPlanId()));
        }
        if (planEntity.getState().equals(ConstantsInfo.REJECTED)) {
            // 获取驳回原因
            planDTO.setRejectReason(getRejectReason(planEntity.getPlanId()));
        }
        return planDTO;
    }

    /**
     * 分页查询
     * @param permission 权限
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
        List<Long> panIds = contentsService.queryByCondition(selectPlanDTO.getContentsId());
        PageHelper.startPage(pageNum, pageSize);
        if (ObjectUtil.isNotNull(panIds) && !panIds.isEmpty()) {
            Page<PlanVO> page = planMapper.queryPage(selectPlanDTO, panIds, permission.getDeptIds(), permission.getDateScopeSelf(), currentUser.getUserName());
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
     * 获取工程预警方案下拉列表
     * @return 返回结果
     */
    @Override
    public List<ProjectWarnChoiceListDTO> getProjectWarnChoiceList() {
        List<ProjectWarnChoiceListDTO> projectWarnChoiceListDTOS = new ArrayList<>();
        List<ProjectWarnSchemeEntity> projectWarnSchemeEntities = projectWarnSchemeMapper.selectList(new LambdaQueryWrapper<ProjectWarnSchemeEntity>()
                .eq(ProjectWarnSchemeEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ListUtils.isNotNull(projectWarnSchemeEntities)) {
            projectWarnChoiceListDTOS = projectWarnSchemeEntities.stream().map(projectWarnSchemeEntity -> {
                ProjectWarnChoiceListDTO projectWarnChoiceListDTO = new ProjectWarnChoiceListDTO();
                projectWarnChoiceListDTO.setLabel(projectWarnSchemeEntity.getSchemeName());
                projectWarnChoiceListDTO.setValue(projectWarnSchemeEntity.getProjectWarnSchemeId());
                return projectWarnChoiceListDTO;
            }).collect(Collectors.toList());
        }
        return projectWarnChoiceListDTOS;
    }

    @Override
    public List<Long> getPlanByPoint(String traversePoint, String distance) {
        Set<Long> traversePointIdSet = new HashSet<>();
        List<RelatesInfoEntity> relatesInfoEntities = relatesInfoMapper.selectList(new LambdaQueryWrapper<RelatesInfoEntity>());
        if (ListUtils.isNotNull(relatesInfoEntities)) {
            relatesInfoEntities.forEach(relatesInfoEntity -> {
                if (ObjectUtil.isNotNull(relatesInfoEntity.getArea())) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        List<AreaDTO> areaDTOS = objectMapper.readValue(relatesInfoEntity.getArea(),
                                new TypeReference<List<AreaDTO>>() {});
                        areaDTOS.forEach(areaDTO -> {
                            Long tunnelId = areaDTO.getTunnelId();
                            BizTravePoint bizTravePoint = bizTravePointMapper.selectOne(new LambdaQueryWrapper<BizTravePoint>()
                                    .eq(BizTravePoint::getPointId, Long.valueOf(traversePoint))
                                    .eq(BizTravePoint::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
                            if (ObjectUtil.isNull(bizTravePoint)) {
                                throw new RuntimeException("未找到此导线点");
                            }
                            if (bizTravePoint.getTunnelId().equals(tunnelId)) {
                                Long startPointId =Long.valueOf(areaDTO.getStartTraversePoint());
                                Long sPointNo = getPointNo(startPointId);
                                Long eP = Long .valueOf(areaDTO.getEndTraversePoint());
                                Long ePointNo = getPointNo(eP);
                                String startDistance = areaDTO.getStartDistance();
                                String endDistance = areaDTO.getEndDistance();
                                if (sPointNo < bizTravePoint.getNo() && bizTravePoint.getNo() < ePointNo) {
                                    traversePointIdSet.add(relatesInfoEntity.getPlanId());
                                }
                                if (bizTravePoint.getNo().equals(sPointNo) && !bizTravePoint.getNo().equals(ePointNo)) {
                                    if (distance.charAt(0) == '-') {
                                        boolean b = DataJudgeUtils.greaterThan(distance, startDistance);
                                        if (b) {
                                            traversePointIdSet.add(relatesInfoEntity.getPlanId());
                                        }
                                    } else {
                                        traversePointIdSet.add(relatesInfoEntity.getPlanId());
                                    }
                                }
                                if (bizTravePoint.getNo().equals(ePointNo) && !bizTravePoint.getNo().equals(sPointNo)) {
                                    if (distance.charAt(0) == ' ') {
                                        boolean b = DataJudgeUtils.lessThan(distance, endDistance);
                                        if (b) {
                                            traversePointIdSet.add(relatesInfoEntity.getPlanId());
                                        }
                                    } else {
                                        traversePointIdSet.add(relatesInfoEntity.getPlanId());
                                    }
                                }
                                if (bizTravePoint.getNo().equals(sPointNo) && bizTravePoint.getNo().equals(ePointNo)) {
                                    boolean b = DataJudgeUtils.isInRange(distance, startDistance, endDistance);
                                    if (b) {
                                        traversePointIdSet.add(relatesInfoEntity.getPlanId());
                                    }
                                }
                            }
                        });
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        return new ArrayList<>(traversePointIdSet);
    }

    private Long getPointNo(Long pointId) {
        Long pointNo = 0L;
        BizTravePoint bizTravePoint = bizTravePointMapper.selectOne(new LambdaQueryWrapper<BizTravePoint>()
                .eq(BizTravePoint::getPointId, pointId)
                .eq(BizTravePoint::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(bizTravePoint)) {
            throw new RuntimeException("未找到此导线点");
        }
        pointNo = bizTravePoint.getNo();
        return pointNo;
    }

    /**
     * 获取审核结果
     */
    private String getAuditResult(Long planId) {
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
        // 月计划、临时计划导线点不可重复选择校验
        if (planDTO.getPlanType().equals(ConstantsInfo.Month_PLAN) ||
                planDTO.getPlanType().equals(ConstantsInfo.TEMPORARY_PLAN)) {
            checkTraversePoint(planDTO);
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
            if (!planDTO.getPlanType().equals(ConstantsInfo.SPECIAL_PLAN)) {
                Long aLong = relatesInfoMapper.selectCount(new LambdaQueryWrapper<RelatesInfoEntity>()
                        .eq(RelatesInfoEntity::getArea, areaDTOFmt));
                if (aLong > 0) {
                    throw new RuntimeException("除特殊计划外，区域不可重复!");
                }
            }
        });
    }

    /**
     * 月计划、临时计划导线点不可重复选择校验
     */
    private void checkTraversePoint(PlanDTO planDTO) {
       if (ObjectUtil.isNull(planDTO.getRelatesInfoDTOS()) || planDTO.getRelatesInfoDTOS().isEmpty()) {
           throw new RuntimeException("关联信息不能为空");
       }
       if (ObjectUtil.isNull(planDTO.getPlanId())) {
           planDTO.getRelatesInfoDTOS().forEach(relatesInfoDTO -> {
               if (ObjectUtil.isNotNull(relatesInfoDTO.getAreaDTOS()) && !relatesInfoDTO.getAreaDTOS().isEmpty()) {
                   relatesInfoDTO.getAreaDTOS().forEach(areaDTO -> {
                       List<Long> traversePoint = relatesInfoService.getTraversePoint(planDTO.getPlanType(), planDTO.getType(),
                               areaDTO.getTunnelId());
                       Long startTraversePoint = Long.valueOf(areaDTO.getStartTraversePoint());
                       Long endTraversePoint = Long.valueOf(areaDTO.getEndTraversePoint());
                       boolean s = traversePoint.contains(startTraversePoint);
                       boolean e = traversePoint.contains(endTraversePoint);
                       if (s) {
                           throw new RuntimeException("该导线点已存在于之前的计划区域中，不可在进行选择！导线点id为:" + startTraversePoint);
                       }
                       if (e) {
                           throw new RuntimeException("该导线点已存在于之前的计划区域中，不可在进行选择！导线点id为:" + endTraversePoint);
                       }
                   });
               }
           });
       } else {
           List<RelatesInfoEntity> relatesInfoEntities = relatesInfoMapper.selectList(new LambdaQueryWrapper<RelatesInfoEntity>()
                   .eq(RelatesInfoEntity::getPlanId, planDTO.getPlanId())
                   .eq(RelatesInfoEntity::getType, planDTO.getType())
                   .eq(RelatesInfoEntity::getPlanType, planDTO.getPlanType()));
           planDTO.getRelatesInfoDTOS().forEach(relatesInfoDTO -> {
               relatesInfoDTO.getAreaDTOS().forEach(areaDTO -> {
                   relatesInfoEntities.forEach(relatesInfoEntity -> {
                       if (ObjectUtil.isNotNull(relatesInfoEntity.getArea()) && !relatesInfoEntity.getArea().isEmpty()) {
                           List<AreaDTO> areaDTOS = JSON.parseArray(relatesInfoEntity.getArea(), AreaDTO.class);
                           areaDTOS.forEach( areaDTO1-> {
                               if (!areaDTO.getStartTraversePoint().equals(areaDTO1.getStartTraversePoint())
                                       && areaDTO.getEndTraversePoint().equals(areaDTO1.getEndTraversePoint())) {
                                   check(planDTO.getPlanType(),planDTO.getType(),areaDTO.getTunnelId(),areaDTO, "1");
                               }
                               if (!areaDTO.getEndTraversePoint().equals(areaDTO1.getEndTraversePoint())
                                       && areaDTO.getStartTraversePoint().equals(areaDTO1.getStartTraversePoint())) {
                                   check(planDTO.getPlanType(),planDTO.getType(),areaDTO.getTunnelId(),areaDTO, "2");
                               }
                               if (!areaDTO.getStartTraversePoint().equals(areaDTO1.getStartTraversePoint()) &&
                                       !areaDTO.getEndTraversePoint().equals(areaDTO1.getEndTraversePoint())) {
                                   check(planDTO.getPlanType(),planDTO.getType(),areaDTO.getTunnelId(),areaDTO, "3");
                               }
                           });
                       }
                   });
               });
           });
       }
    }

    private void check(String planType, String type, Long tunnelId, AreaDTO areaDTO, String tag) {
        List<Long> traversePoint = relatesInfoService.getTraversePoint(planType,type,
                tunnelId);
        Long startTraversePoint = Long.valueOf(areaDTO.getStartTraversePoint());
        Long endTraversePoint = Long.valueOf(areaDTO.getEndTraversePoint());
        boolean s = traversePoint.contains(startTraversePoint);
        boolean e = traversePoint.contains(endTraversePoint);
        if (tag.equals(ConstantsInfo.ONE_TYPE)) {
            if (s) {
                throw new RuntimeException("该导线点已存在于之前的计划区域中，不可在进行选择！导线点id为:" + startTraversePoint);
            }
        }
        if (tag.equals(ConstantsInfo.TWO_TYPE)) {
            if (e) {
                throw new RuntimeException("该导线点已存在于之前的计划区域中，不可在进行选择！导线点id为:" + endTraversePoint);
            }
        }
        if (tag.equals(ConstantsInfo.THREE_TYPE)) {
            if (s) {
                throw new RuntimeException("该导线点已存在于之前的计划区域中，不可在进行选择！导线点id为:" + startTraversePoint);
            }
            if (e) {
                throw new RuntimeException("该导线点已存在于之前的计划区域中，不可在进行选择！导线点id为:" + endTraversePoint);
            }
        }
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