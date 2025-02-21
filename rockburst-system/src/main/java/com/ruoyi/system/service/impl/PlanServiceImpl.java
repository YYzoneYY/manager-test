package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.domain.BasePermission;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.*;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.utils.AreaAlgorithmUtils;
import com.ruoyi.system.domain.vo.PlanVO;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private PlanAreaMapper planAreaMapper;

    @Resource
    private PlanAuditMapper planAuditMapper;

    @Resource
    private SysDictDataMapper sysDictDataMapper;

    @Resource
    private BizProjectRecordMapper bizProjectRecordMapper;

    @Resource
    private ProjectWarnSchemeMapper projectWarnSchemeMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private BizTravePointMapper bizTravePointMapper;

    @Resource
    private IBizTravePointService bizTravePointService;

    @Resource
    private PlanAreaService planAreaService;

    @Resource
    private BizWorkfaceMapper bizWorkfaceMapper;

    @Resource
    private IBizPresetPointService bizPresetPointService;

    @Resource
    IBizProjectRecordService iBizProjectRecordService;

    @Override
    public int insertPlan(PlanDTO planDTO) {
        int flag = 0;
        if (ObjectUtil.isNull(planDTO)) {
            throw new RuntimeException("参数错误,参数不能为空");
        }
        // 参数校验
        checkParameter(planDTO, planAreaMapper, bizTravePointMapper);
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
        planEntity.setCreateTime(System.currentTimeMillis());
        planEntity.setCreateBy(SecurityUtils.getUserId());
        planEntity.setState(ConstantsInfo.AUDIT_STATUS_DICT_VALUE);
        SysUser sysUser = sysUserMapper.selectUserById(SecurityUtils.getUserId());
        planEntity.setDeptId(sysUser.getDeptId());
        planEntity.setDelFlag(ConstantsInfo.ZERO_DEL_FLAG);
        flag = planMapper.insert(planEntity);
        if (flag > 0) {
            // 区域信息
            if (ObjectUtil.isNotNull(planDTO.getPlanAreaDTOS()) && !planDTO.getPlanAreaDTOS().isEmpty()) {
                List<TraversePointGatherDTO> traversePointGatherDTOS = new ArrayList<>();
                List<BizPlanPrePointDto> bizPlanPrePointDtos = new ArrayList<>();
                planDTO.getPlanAreaDTOS().forEach(planAreaDTO -> {
                    BizPlanPrePointDto bizPlanPrePointDto = getBizPlanPrePointDto(planAreaDTO);
                    bizPlanPrePointDtos.add(bizPlanPrePointDto);
                    // 获取计划区域内所有的导线点
                    List<Long> pointList = bizTravePointService.getInPointList(planAreaDTO.getStartTraversePointId(), Double.valueOf(planAreaDTO.getStartDistance()),
                            planAreaDTO.getEndTraversePointId(), Double.valueOf(planAreaDTO.getEndDistance()));
                    if (ObjectUtil.isNotNull(pointList) && !pointList.isEmpty()) {
                       for (Long point : pointList) {
                           TraversePointGatherDTO traversePointGatherDTO = new TraversePointGatherDTO();
                           traversePointGatherDTO.setTraversePointId(point);
                           traversePointGatherDTOS.add(traversePointGatherDTO);
                       }
                    }
                });
                boolean insert = planAreaService.insert(planEntity.getPlanId(), planDTO.getPlanAreaDTOS(), traversePointGatherDTOS);
                if (insert) {
                    bizPresetPointService.setPlanPrePoint(planEntity.getPlanId(),bizPlanPrePointDtos);
                }
            }
        } else {
            throw new RuntimeException("计划添加失败");
        }
        return flag;
    }

    @Override
    public int updatePlan(PlanDTO planDTO) {
        int flag = 0;
        if (ObjectUtil.isNull(planDTO)) {
            throw new RuntimeException("参数错误,参数不能为空");
        }
        PlanEntity planEntity = planMapper.selectOne(new LambdaQueryWrapper<PlanEntity>().eq(PlanEntity::getPlanId, planDTO.getPlanId())
                .eq(PlanEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isEmpty(planEntity)) {
            throw new RuntimeException("未找到此计划！");
        }
        // 参数校验
        checkParameter(planDTO, planAreaMapper, bizTravePointMapper);

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
            // 区域信息修改
            List<Long> planIdList = new ArrayList<>();
            planIdList.add(planId);
            planAreaService.deleteById(planIdList);
            if (ObjectUtil.isNotNull(planDTO.getPlanAreaDTOS()) && !planDTO.getPlanAreaDTOS().isEmpty()) {
                List<TraversePointGatherDTO> traversePointGatherDTOS = new ArrayList<>();
                List<BizPlanPrePointDto> bizPlanPrePointDtos = new ArrayList<>();
                planDTO.getPlanAreaDTOS().forEach(planAreaDTO -> {
                    BizPlanPrePointDto bizPlanPrePointDto = getBizPlanPrePointDto(planAreaDTO);
                    bizPlanPrePointDtos.add(bizPlanPrePointDto);
                    // 获取计划区域内所有的导线点
                    List<Long> pointList = bizTravePointService.getInPointList(planAreaDTO.getStartTraversePointId(), Double.valueOf(planAreaDTO.getStartDistance()),
                            planAreaDTO.getEndTraversePointId(), Double.valueOf(planAreaDTO.getEndDistance()));
                    if (ObjectUtil.isNotNull(pointList) && !pointList.isEmpty()) {
                        for (Long point : pointList) {
                            TraversePointGatherDTO traversePointGatherDTO = new TraversePointGatherDTO();
                            traversePointGatherDTO.setTraversePointId(point);
                            traversePointGatherDTOS.add(traversePointGatherDTO);
                        }
                    }
                });
                boolean insert = planAreaService.insert(planDTO.getPlanId(), planDTO.getPlanAreaDTOS(), traversePointGatherDTOS);
                if (insert) {
                    bizPresetPointService.setPlanPrePoint(planEntity.getPlanId(),bizPlanPrePointDtos);
                }
            }
        }
        return flag;
    }

    /**
     * 根据id查询
     * @param planId 计划id
     * @return 返回结果
     */
    @Override
    public PlanVO queryById(Long planId) {
        if (ObjectUtil.isNull(planId)) {
            throw new RuntimeException("参数错误,主键不能为空");
        }
        PlanEntity planEntity = planMapper.selectOne(new LambdaQueryWrapper<PlanEntity>()
                .eq(PlanEntity::getPlanId, planId)
                .eq(PlanEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(planEntity)) {
            throw new RuntimeException("未找到此计划！");
        }
        PlanDTO planDTO = new PlanDTO();
        BeanUtils.copyProperties(planEntity, planDTO);
        // 区域信息
        List<PlanAreaDTO> planAreaDTOS = planAreaService.getByPlanId(planId);
        planDTO.setPlanAreaDTOS(planAreaDTOS);
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
        return null;
    }

    /**
     * 分页查询
     * @param permission 权限
     * @param selectNewPlanDTO 查询参数DTO
     * @param pageNum 当前页码
     * @param pageSize 条数
     * @return 返回结果
     */
    @Override
    public TableData queryPage(BasePermission permission, SelectNewPlanDTO selectNewPlanDTO, Integer pageNum, Integer pageSize) {
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
        Page<PlanVO> page = planMapper.queryPage(selectNewPlanDTO, permission.getDeptIds(), permission.getDateScopeSelf(), currentUser.getUserName());
        if (ListUtils.isNotNull(page.getResult())) {
            page.getResult().forEach(planVO -> {
                List<PlanAreaDTO> planAreaDTOS = planAreaService.getByPlanId(planVO.getPlanId());
                planVO.setPlanAreaDTOS(planAreaDTOS);
                planVO.setStartTimeFmt(DateUtils.getDateStrByTime(planVO.getStartTime()));
                planVO.setEndTimeFmt(DateUtils.getDateStrByTime(planVO.getEndTime()));
                // 工作面名称
                String workFaceName = getWorkFaceName(planVO.getWorkFaceId());
                planVO.setWorkFaceName(workFaceName);
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
        PlanEntity planEntity = planMapper.selectOne(new LambdaQueryWrapper<PlanEntity>()
                .eq(PlanEntity::getPlanId, planId)
                .eq(PlanEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(planEntity)) {
            throw new RuntimeException("未找到此计划！");
        }
        checkStatus(planEntity.getState());
        PlanEntity entity = new PlanEntity();
        BeanUtils.copyProperties(planEntity, entity);
        entity.setState(ConstantsInfo.AUDIT_STATUS_DICT_VALUE);
        flag = planMapper.updateById(entity) > 0 ? "撤回成功" :  "撤回失败,请联系管理员";
        return flag;
    }

    @Override
    public boolean deletePlan(Long[] planIds) {
        boolean flag = false;
        if (planIds.length == 0) {
            throw new RuntimeException("请选择要删除的数据!");
        }
        List<Long > planIdList = Arrays.asList(planIds);
        planIdList.forEach(planId -> {
            iBizProjectRecordService.deletePlan(planId);
//            List<BizProjectRecord> bizProjectRecords = bizProjectRecordMapper.selectList(new LambdaQueryWrapper<BizProjectRecord>()
//                    .eq(BizProjectRecord::getPlanId, planId)
//                    .eq(BizProjectRecord::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
//            if (ListUtils.isNotNull(bizProjectRecords)) {
//                throw new RuntimeException("该计划下有填报信息,不能删除");
//            }
        });
        flag = this.removeBatchByIds(planIdList);
        if (flag) {
            // 删除区域信息
            planAreaService.deleteById(planIdList);
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

    private void checkParameter(PlanDTO planDTO, PlanAreaMapper planAreaMapper, BizTravePointMapper bizTravePointMapper) {
        if (ListUtils.isNull(planDTO.getPlanAreaDTOS())) {
            throw new RuntimeException("区域信息不能为空");
        }
        // 月计划、临时计划区域不可重复选择校验
        if (planDTO.getPlanType().equals(ConstantsInfo.Month_PLAN) ||
                planDTO.getPlanType().equals(ConstantsInfo.TEMPORARY_PLAN)) {
            checkArea(planDTO, planAreaMapper, bizTravePointMapper);
        }
    }

    private void checkArea(PlanDTO planDTO, PlanAreaMapper planAreaMapper, BizTravePointMapper bizTravePointMapper) {
        if (ListUtils.isNull(planDTO.getPlanAreaDTOS()) || planDTO.getPlanAreaDTOS().isEmpty()) {
            throw new RuntimeException("区域信息不能为空");
        }
        planDTO.getPlanAreaDTOS().forEach(planAreaDTO -> {
            LambdaQueryWrapper<PlanAreaEntity> queryWrapper = new LambdaQueryWrapper<PlanAreaEntity>()
                    .eq(PlanAreaEntity::getTunnelId, planAreaDTO.getTunnelId());
            // 判断是否是修改
            if (ObjectUtil.isNotNull(planDTO.getPlanId())) {
                queryWrapper.ne(PlanAreaEntity::getPlanId, planDTO.getPlanId());
            }
            List<PlanAreaEntity> planAreaEntities = planAreaMapper.selectList(queryWrapper);
            if (ListUtils.isNotNull(planAreaEntities)) {
                planAreaEntities.forEach(planAreaEntity -> {
                    try {
                        PlanEntity planEntity = planMapper.selectOne(new LambdaQueryWrapper<PlanEntity>()
                                .eq(PlanEntity::getPlanId, planAreaEntity.getPlanId())
                                .eq(PlanEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
                        if (ObjectUtil.isNull(planEntity)) {
                            throw new RuntimeException("区域校验时发生异常");
                        }
                        // 判断同一计划类型是否有重复的区域
                        if (planDTO.getPlanType().equals(planEntity.getPlanType())) {
                            AreaAlgorithmUtils.areaCheck(planAreaDTO, planAreaEntity, planAreaEntities, planAreaMapper, bizTravePointMapper);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage());
                    }
                });
            }
        });
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
     * 获取工面名称
     */
    private String getWorkFaceName(Long workFaceId) {
        String workFaceName = null;
        BizWorkface bizWorkface = bizWorkfaceMapper.selectOne(new LambdaQueryWrapper<BizWorkface>()
                .eq(BizWorkface::getWorkfaceId, workFaceId)
                .eq(BizWorkface::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(bizWorkface)) {
            return workFaceName;
        }
        workFaceName =  bizWorkface.getWorkfaceName();
        return workFaceName;
    }

    private BizPlanPrePointDto getBizPlanPrePointDto(PlanAreaDTO planAreaDTO) {
        BizPlanPrePointDto bizPlanPrePointDto = new BizPlanPrePointDto();
        bizPlanPrePointDto.setTunnelId(planAreaDTO.getTunnelId());
        bizPlanPrePointDto.setStartPointId(planAreaDTO.getStartTraversePointId());
        bizPlanPrePointDto.setEndPointId(planAreaDTO.getEndTraversePointId());
        bizPlanPrePointDto.setStartMeter(Double.valueOf(planAreaDTO.getStartDistance()));
        bizPlanPrePointDto.setEndMeter(Double.valueOf(planAreaDTO.getEndDistance()));
        return bizPlanPrePointDto;
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