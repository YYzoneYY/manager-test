package com.ruoyi.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.domain.BasePermission;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.BizTravePoint;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.*;
import com.ruoyi.system.domain.SysFileInfo;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.utils.AreaAlgorithmUtils;
import com.ruoyi.system.domain.utils.DataJudgeUtils;
import com.ruoyi.system.domain.utils.TrimUtils;
import com.ruoyi.system.domain.vo.PlanVO;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.io.InputStream;
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
    IBizProjectRecordService iBizProjectRecordService;

    @Resource
    protected Validator validator;

    @Resource
    private TunnelMapper tunnelMapper;

    @Resource
    private ImportPlanAssistService importPlanAssistService;

    @Resource
    private SysFileInfoMapper sysFileInfoMapper;

    @Override
    public int insertPlan(PlanDTO planDTO) {
        int flag = 0;
        if (ObjectUtil.isNull(planDTO)) {
            throw new RuntimeException("参数错误,参数不能为空");
        }
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
                planDTO.getPlanAreaDTOS().forEach(planAreaDTO -> {
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
                boolean insert = planAreaService.insert(planEntity.getPlanId(), planEntity.getType(),
                        planDTO.getPlanAreaDTOS(), traversePointGatherDTOS);
                if (!insert) {
                    throw new RuntimeException("发生未知异常,计划添加失败！！");
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
        checkParameter(planDTO);

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
                planDTO.getPlanAreaDTOS().forEach(planAreaDTO -> {
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
                boolean insert = planAreaService.insert(planDTO.getPlanId(), planDTO.getType(),
                        planDTO.getPlanAreaDTOS(), traversePointGatherDTOS);
                if (!insert) {
                    throw new RuntimeException("发生未知异常,计划修改失败！！");
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
    public PlanDTO queryById(Long planId) {
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
        return planDTO;
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
        entity.setState(ConstantsInfo.TO_BE_SUBMITTED);
        int update = planMapper.updateById(entity);
        if (update > 0) {
            iBizProjectRecordService.deletePlan(planId);
            flag = "撤回成功";
        } else {
            flag = "撤回失败,请联系管理员";
        }
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
            PlanEntity planEntity = planMapper.selectOne(new LambdaQueryWrapper<PlanEntity>()
                    .eq(PlanEntity::getPlanId, planId)
                    .eq(PlanEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (planEntity.getState().equals(ConstantsInfo.IN_REVIEW_DICT_VALUE)) {
                throw new RuntimeException("该计划正在审核中,不能删除");
            }
            if (planEntity.getState().equals(ConstantsInfo.AUDITED_DICT_VALUE)) {
                throw new RuntimeException("该计划已审核通过,不能删除");
            }
            iBizProjectRecordService.deletePlan(planId);
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

    /**
     * 导入
     * @param tag 标签 tag :1 掘进 2:回采
     * @param file 文件
     * @return 返回结果
     * @throws Exception 异常
     */
    @Override
    @Transactional(rollbackFor = ServiceException.class)
    public String importPlan(String tag, MultipartFile file) throws Exception {
        if (ObjectUtil.isNull(tag)) {
            throw new ServiceException("模板类型不能为空,请先选择模板类型！");
        }
        try (InputStream inputStream = file.getInputStream()) {
            if (tag.equals(ConstantsInfo.TUNNELING)) {
                return importData(inputStream, ImportPlanDTO.class, this::checkParam, importPlanAssistService::importDataAdd);
            } else if (tag.equals(ConstantsInfo.STOPE)) {
                return importData(inputStream, ImportPlanTwoDTO.class, this::checkParamTwo, importPlanAssistService::importDataAddTwo);
            } else {
                throw new ServiceException("不支持的模板类型");
            }
        }
    }

    private <T> String importData(InputStream inputStream, Class<T> dtoClass, PlanServiceImpl.ParamChecker<T> paramChecker,
                                  PlanServiceImpl.DataImporter<T> dataImporter) throws Exception {
        ExcelUtil<T> util = new ExcelUtil<>(dtoClass);
        List<T> list = util.importExcel(inputStream);
        if (CollUtil.isEmpty(list)) {
            throw new ServiceException("导入数据内容不能为空");
        }
        int errorLine = 2; // 第一行通常是表头，所以从第二行开始计数
        for (T dto : list) {
            try {
                if (ObjectUtil.isNull(dto)) {
                    throw new ServiceException("数据解析失败,请使用规定模板");
                }
                TrimUtils.trimBean(dto);
                paramChecker.check(dto);
                try {
                    dataImporter.importData(dto);
                } catch (ConstraintViolationException e) {
                    throw new ServiceException(e.getConstraintViolations().iterator().next().getMessage());
                }
                errorLine++;
            } catch (Exception e) {
                throw new ServiceException("导入第(" + errorLine + ")行失败！失败原因：" + e.getMessage());
            }
        }
        return "导入成功";
    }

    @FunctionalInterface
    interface ParamChecker<T> {
        void check(T dto) throws Exception;
    }
    @FunctionalInterface
    interface DataImporter<T> {
        void importData(T dto) throws Exception;
    }

    /**
     * 根据巷道id和类型获取区域集合
     * @param tunnelIds 巷道ids
     * @param type 类型
     * @return 返回结果
     */
    @Override
    public List<ReturnDTO> getSketchMap(List<Long> tunnelIds, String type) {
        if (tunnelIds == null || tunnelIds.isEmpty() || type == null || type.isEmpty()) {
            throw new RuntimeException("参数不能为空");
        }
        List<ReturnDTO> returnDTOS = new ArrayList<>();
        try {
            List<PlanAreaEntity> planAreaEntities = planAreaMapper.selectList(new LambdaQueryWrapper<PlanAreaEntity>()
                    .eq(PlanAreaEntity::getType, type)
                    .in(PlanAreaEntity::getTunnelId, tunnelIds));
            if (planAreaEntities != null && !planAreaEntities.isEmpty()) {
                Set<Long> planIdSet = planAreaEntities.stream()
                        .map(PlanAreaEntity::getPlanId)
                        .collect(Collectors.toSet());
                List<PlanEntity> planEntities = planMapper.selectBatchIds(planIdSet.stream()
                        .filter(id -> ConstantsInfo.ZERO_DEL_FLAG.equals(
                                planMapper.selectOne(new LambdaQueryWrapper<PlanEntity>()
                                        .eq(PlanEntity::getPlanId, id))
                                        .getDelFlag()))
                        .collect(Collectors.toList()));
                List<PlanAreaEntity> allPlanAreaEntities = planAreaMapper.selectList(new LambdaQueryWrapper<PlanAreaEntity>()
                        .in(PlanAreaEntity::getPlanId, planIdSet));
                for (Long planId : planIdSet) {
                    ReturnDTO returnDTO = new ReturnDTO();
                    Optional<PlanEntity> planEntityOpt = planEntities.stream().filter(e -> e.getPlanId().equals(planId)).findFirst();
                    if (planEntityOpt.isPresent()) {
                        PlanEntity planEntity = planEntityOpt.get();
                        returnDTO.setPlanId(planId);
                        returnDTO.setPlanName(planEntity.getPlanName());
                        returnDTO.setType(type);
                        List<PlanAreaDTO> planAreaDTOS = allPlanAreaEntities.stream()
                                .filter(entity -> entity.getPlanId().equals(planId))
                                .map(entity -> {
                                    PlanAreaDTO planAreaDTO = new PlanAreaDTO();
                                    planAreaDTO.setTunnelId(entity.getTunnelId());
                                    planAreaDTO.setStartTraversePointId(entity.getStartTraversePointId());
                                    planAreaDTO.setStartDistance(entity.getStartDistance());
                                    planAreaDTO.setEndTraversePointId(entity.getEndTraversePointId());
                                    planAreaDTO.setEndDistance(entity.getEndDistance());
                                    return planAreaDTO;
                                })
                                .collect(Collectors.toList());
                        returnDTO.setPlanAreaDTOS(planAreaDTOS);
                        returnDTOS.add(returnDTO);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch sketch map", e);
        }
        return returnDTOS;
    }


    /**
     * 获取模板文件URL
     * @param fileId 文件id
     * @return 返回结果
     */
    @Override
    public String getFileUrl(String fileId, String type) {
        String url = "";
        if (ObjectUtil.isNull(fileId) || ObjectUtil.isNull(type)) {
            throw new RuntimeException("参数错误,不能为空");
        }
        if (fileId.equals("209") && type.equals(ConstantsInfo.TUNNELING)) {
            SysFileInfo sysFileInfo = sysFileInfoMapper.selectOne(new LambdaQueryWrapper<SysFileInfo>()
                    .eq(SysFileInfo::getFileId, Long.parseLong(fileId)));
            if (ObjectUtil.isNull(sysFileInfo)) {
                throw new RuntimeException("模板不存在,下载失败,请联系管理员！");
            }
            url = sysFileInfo.getFileUrl();
        }
        if (fileId.equals("210") && type.equals(ConstantsInfo.STOPE)) {
            SysFileInfo sysFileInfo = sysFileInfoMapper.selectOne(new LambdaQueryWrapper<SysFileInfo>()
                    .eq(SysFileInfo::getFileId, Long.parseLong(fileId)));
            if (ObjectUtil.isNull(sysFileInfo)) {
                throw new RuntimeException("模板不存在,下载失败,请联系管理员！");
            }
            url = sysFileInfo.getFileUrl();
        }
        return url;
    }

    /**
     * 校验参数（掘进）
     * @param importPlanDTO 实体类
     */
    private void checkParam(ImportPlanDTO importPlanDTO) {
        //校验实体类中的判断
        BeanValidators.validateWithException(validator, importPlanDTO);
        // 校验工作面名称
        BizWorkface bizWorkface = bizWorkfaceMapper.selectOne(new LambdaQueryWrapper<BizWorkface>()
                .eq(BizWorkface::getWorkfaceName, importPlanDTO.getWorkFaceName())
                .eq(BizWorkface::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(bizWorkface)) {
            throw new ServiceException("不存在名称为" + importPlanDTO.getWorkFaceName() + "的工作面,请填写正确的工作面名称");
        }
        // 校验巷道名称
        Long workfaceId = bizWorkface.getWorkfaceId();
        TunnelEntity tunnelEntity = tunnelMapper.selectOne(new LambdaQueryWrapper<TunnelEntity>()
                .eq(TunnelEntity::getWorkFaceId, workfaceId)
                .eq(TunnelEntity::getTunnelName, importPlanDTO.getTunnelName())
                .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(tunnelEntity)) {
            throw new ServiceException("不存在名称为" + importPlanDTO.getTunnelName() + "的巷道,请填写正确的巷道名称");
        }
        // 校验起始导线点名称
        Long startPoint = checkPoint(tunnelEntity.getTunnelId(), importPlanDTO.getStartPoint());
        // 校验结束导线点名称
        Long endPoint = checkPoint(tunnelEntity.getTunnelId(), importPlanDTO.getEndPoint());
        // 校验字典项
        checkDicTwo(importPlanDTO.getAnnual(), importPlanDTO.getPlanType(), importPlanDTO.getType(), importPlanDTO.getDrillType());
        // 组装DTO
        String type = dicValue(ConstantsInfo.TYPE_DICT_TYPE, importPlanDTO.getType());
        String planType = dicValue(ConstantsInfo.PLAN_TYPE_DICT_TYPE, importPlanDTO.getPlanType());// 计划类型
        PlanAreaDTO planAreaDTO = assembleDTO(tunnelEntity.getTunnelId(), startPoint,
                importPlanDTO.getStartDistance(), endPoint, importPlanDTO.getEndDistance());
        List<PlanAreaEntity> planAreaEntities = planAreaMapper.selectList(new LambdaQueryWrapper<PlanAreaEntity>()
                .eq(PlanAreaEntity::getTunnelId, tunnelEntity.getTunnelId()).eq(PlanAreaEntity::getType, type));
        // 月计划、临时计划区域不可重复选择校验
        if (planType.equals(ConstantsInfo.Month_PLAN) ||
                planType.equals(ConstantsInfo.TEMPORARY_PLAN)) {
            // 区域校验
            importCheckArea(importPlanDTO.getType(), planAreaDTO, planAreaEntities);
        }
    }

    /**
     * 校验参数（回采）
     * @param importPlanTwoDTO 实体类
     */
    private void checkParamTwo(ImportPlanTwoDTO importPlanTwoDTO) {
        //校验实体类中的判断
        BeanValidators.validateWithException(validator, importPlanTwoDTO);
        // 校验工作面名称
        BizWorkface bizWorkface = bizWorkfaceMapper.selectOne(new LambdaQueryWrapper<BizWorkface>()
                .eq(BizWorkface::getWorkfaceName, importPlanTwoDTO.getWorkFaceName())
                .eq(BizWorkface::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(bizWorkface)) {
            throw new ServiceException("不存在名称为" + importPlanTwoDTO.getWorkFaceName() + "的工作面,请填写正确的工作面名称");
        }
        // 校验巷道名称
        Long workFaceId = bizWorkface.getWorkfaceId();
        TunnelEntity tunnelEntity = tunnelMapper.selectOne(new LambdaQueryWrapper<TunnelEntity>()
                .eq(TunnelEntity::getWorkFaceId, workFaceId)
                .eq(TunnelEntity::getTunnelName, importPlanTwoDTO.getTunnelName())
                .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(tunnelEntity)) {
            throw new ServiceException("不存在名称为" + importPlanTwoDTO.getTunnelName() + "的巷道,请填写正确的巷道(1)名称");
        }
        TunnelEntity entity = tunnelMapper.selectOne(new LambdaQueryWrapper<TunnelEntity>()
                .eq(TunnelEntity::getWorkFaceId, workFaceId)
                .eq(TunnelEntity::getTunnelName, importPlanTwoDTO.getTunnelNameTwo())
                .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(entity)) {
            throw new ServiceException("不存在名称为" + importPlanTwoDTO.getTunnelNameTwo() + "的巷道(2)名称");
        }
        // 校验起始导线点(1)名称
        Long startPoint = checkPoint(tunnelEntity.getTunnelId(), importPlanTwoDTO.getStartPoint());
        // 校验结束导线点(1)名称
        Long endPoint = checkPoint(tunnelEntity.getTunnelId(), importPlanTwoDTO.getEndPoint());
        // 校验起始导线点(2)名称
        Long startPointTwo = checkPoint(entity.getTunnelId(), importPlanTwoDTO.getStartPointTwo());
        // 校验结束导线点(2)名称
        Long endPointTwo = checkPoint(entity.getTunnelId(), importPlanTwoDTO.getEndPointTwo());
        // 校验字典项
        checkDicTwo(importPlanTwoDTO.getAnnual(), importPlanTwoDTO.getPlanType(),
                importPlanTwoDTO.getType(), importPlanTwoDTO.getDrillType());
        // 组装DTOs
        PlanAreaDTO planAreaDTO = assembleDTO(tunnelEntity.getTunnelId(), startPoint,
                importPlanTwoDTO.getStartDistance(), endPoint, importPlanTwoDTO.getEndDistance());
        PlanAreaDTO dto = assembleDTO(tunnelEntity.getTunnelId(), startPointTwo,
                importPlanTwoDTO.getStartDistanceTwo(), endPointTwo, importPlanTwoDTO.getEndDistanceTwo());
        List<PlanAreaDTO> planAreaDTOS = new ArrayList<>();
        planAreaDTOS.add(planAreaDTO);
        planAreaDTOS.add(dto);
        String type = dicValue(ConstantsInfo.TYPE_DICT_TYPE, importPlanTwoDTO.getType());
        String planType = dicValue(ConstantsInfo.PLAN_TYPE_DICT_TYPE, importPlanTwoDTO.getPlanType());// 计划类型
        List<PlanAreaEntity> planAreaEntities = planAreaMapper.selectList(new LambdaQueryWrapper<PlanAreaEntity>()
                .eq(PlanAreaEntity::getTunnelId, tunnelEntity.getTunnelId()).eq(PlanAreaEntity::getType, type));
        // 月计划、临时计划区域不可重复选择校验
        if (planType.equals(ConstantsInfo.Month_PLAN) ||
                planType.equals(ConstantsInfo.TEMPORARY_PLAN)) {
            // 区域校验
            importCheckAreaT(importPlanTwoDTO.getType(), planAreaDTOS, planAreaEntities);
        }
    }

    /**
     * 导入时区域校验（掘进）
     */
    private void importCheckArea(String type, PlanAreaDTO planAreaDTO, List<PlanAreaEntity> planAreaEntities) {
        planAreaEntities.forEach(planAreaEntity -> {
            try {
                AreaAlgorithmUtils.areaCheck(type, planAreaDTO, planAreaEntity, planAreaEntities, planAreaMapper, bizTravePointMapper);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    /**
     * 导入时区域校验（回采）
     */
    private void importCheckAreaT(String type, List<PlanAreaDTO> planAreaDTOS, List<PlanAreaEntity> planAreaEntities) {
        planAreaDTOS.forEach(planAreaDTO -> {
            planAreaEntities.forEach(planAreaEntity -> {
                try {
                    AreaAlgorithmUtils.areaCheck(type, planAreaDTO, planAreaEntity, planAreaEntities, planAreaMapper, bizTravePointMapper);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            });
        });
    }

    /**
     * 组装DTO
     */
    private PlanAreaDTO assembleDTO(Long tunnelId, Long sPoint, String sDistance, Long ePoint, String eDistance) {
        PlanAreaDTO planAreaDTO = new PlanAreaDTO();
        planAreaDTO.setTunnelId(tunnelId);
        planAreaDTO.setStartTraversePointId(sPoint);
        planAreaDTO.setStartDistance(sDistance);
        planAreaDTO.setEndTraversePointId(ePoint);
        planAreaDTO.setEndDistance(eDistance);
        return planAreaDTO;
    }

    /**
     * 校验导线点名称
     */
    private Long checkPoint(Long tunnelId, String pointName) {
        Long pointId = null;
        BizTravePoint bizTravePoint = bizTravePointMapper.selectOne(new LambdaQueryWrapper<BizTravePoint>()
                .eq(BizTravePoint::getTunnelId, tunnelId)
                .eq(BizTravePoint::getPointName, pointName)
                .eq(BizTravePoint::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNotNull(bizTravePoint)) {
            pointId = bizTravePoint.getPointId();
        } else {
            throw new ServiceException("不存在称名称" + pointName + "的导线点,请填写正确的导线点名称");
        }
        return pointId;
    }

    /**
     * 校验字典项一
     */
    private boolean checkDic(String dicType, String dicLab) {
        boolean flag = false;
        List<String> collect = sysDictDataMapper.selectDictDataByType(dicType)
                .stream()
                .map(SysDictData::getDictLabel)
                .collect(Collectors.toList());
        flag = collect.contains(dicLab);
        return flag;
    }
    /**
     * 校验字典项二
     */
    private void checkDicTwo(String name1, String name2, String name3, String name4) {
        // 校验年度
        boolean a = checkDic(ConstantsInfo.YEAR_DICT_TYPE, name1);
        if (!a) {
            throw new ServiceException("导入数据失败，未查询到字典为" + name1 + "的字典项，请联系管理员");
        }
        // 校验计划类型
        boolean P = checkDic(ConstantsInfo.PLAN_TYPE_DICT_TYPE, name2);
        if (!P) {
            throw new ServiceException("导入数据失败，未查询到字典为" + name2 + "的字典项，请联系管理员");
        }
        // 校验类型(掘进or回采)
        boolean t = checkDic(ConstantsInfo.TYPE_DICT_TYPE, name3);
        if (!t) {
            throw new ServiceException("导入数据失败，未查询到字典为" + name3 + "的字典项，请联系管理员");
        }
        // 校验钻孔类型
        boolean d = checkDic(ConstantsInfo.DRILL_TYPE_DICT_TYPE, name4);
        if (!d) {
            throw new ServiceException("导入数据失败，未查询到字典为" + name4 + "的字典项，请联系管理员");
        }
    }

    private void checkParameter(PlanDTO planDTO) {
        if (ListUtils.isNull(planDTO.getPlanAreaDTOS())) {
            throw new RuntimeException("区域信息不能为空");
        }
        // 月计划、临时计划区域不可重复选择校验
        if (planDTO.getPlanType().equals(ConstantsInfo.Month_PLAN) ||
                planDTO.getPlanType().equals(ConstantsInfo.TEMPORARY_PLAN)) {
            checkArea(planDTO.getPlanId(),planDTO.getType(), planDTO.getPlanAreaDTOS());
        }
    }

    /**
     * 区域信息校验
     */
    private void checkArea(Long planId, String type, List<PlanAreaDTO> planAreaDTOS) {
        if (ListUtils.isNull(planAreaDTOS) ||planAreaDTOS.isEmpty()) {
            throw new RuntimeException("区域信息不能为空");
        }
        planAreaDTOS.forEach(planAreaDTO -> {
            LambdaQueryWrapper<PlanAreaEntity> queryWrapper = new LambdaQueryWrapper<PlanAreaEntity>()
                    .eq(PlanAreaEntity::getTunnelId, planAreaDTO.getTunnelId())
                    .eq(PlanAreaEntity::getType, type);
            // 判断是否是修改
            if (ObjectUtil.isNotNull(planId)) {
                queryWrapper.ne(PlanAreaEntity::getPlanId,planId);
            }
            List<PlanAreaEntity> planAreaEntities = planAreaMapper.selectList(queryWrapper);
            if (ListUtils.isNotNull(planAreaEntities)) {
                planAreaEntities.forEach(planAreaEntity -> {
                    try {
                        // 检查距离字符串是否为空或长度不足
                        if (planAreaDTO.getStartDistance() == null || planAreaDTO.getStartDistance().isEmpty()) {
                            throw new IllegalArgumentException("StartDistance 不能为空");
                        }
                        if (planAreaDTO.getEndDistance() == null || planAreaDTO.getEndDistance().isEmpty()) {
                            throw new IllegalArgumentException("EndDistance 不能为空");
                        }
                        Long startTraversePointId = planAreaDTO.getStartTraversePointId();
                        Long endTraversePointId = planAreaDTO.getEndTraversePointId();
                        if (startTraversePointId.equals(endTraversePointId)) {
                            if (planAreaDTO.getStartDistance().charAt(0) != '-') {
                                if (planAreaDTO.getEndDistance().charAt(0) == '-') {
                                    throw new IllegalArgumentException("起始导线点与终始导线点相同，起始距离为正数，终始距离不能为负");
                                }
                            }
                        }
                        AreaAlgorithmUtils.areaCheck(type, planAreaDTO, planAreaEntity, planAreaEntities, planAreaMapper, bizTravePointMapper);
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage());
                    }
                });
            }
        });
    }


    /**
     * 根据字典类型和字典标签获取字典值
     */
    private String dicValue(String dicType, String dicLab) {
        String dicValue = "";
        SysDictData sysDictData = sysDictDataMapper.selectOne(new LambdaQueryWrapper<SysDictData>()
                .eq(SysDictData::getDictType, dicType)
                .eq(SysDictData::getDictLabel, dicLab));
        if (ObjectUtil.isNull(sysDictData)) {
            throw new RuntimeException("字典转化失败，请联系管理员");
        }
        dicValue = sysDictData.getDictValue();
        return dicValue;
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
        return planAuditEntity.getRejectionReason();
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