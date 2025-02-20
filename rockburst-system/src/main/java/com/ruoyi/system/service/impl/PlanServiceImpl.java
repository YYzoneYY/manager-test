package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.domain.BizTravePoint;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.*;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.utils.DataJudgeUtils;
import com.ruoyi.system.domain.vo.NewPlanVo;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.IBizPresetPointService;
import com.ruoyi.system.service.PlanAreaService;
import com.ruoyi.system.service.PlanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
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
    private PlanAreaService planAreaService;

    @Resource
    private BizWorkfaceMapper bizWorkfaceMapper;

    @Resource
    private IBizPresetPointService bizPresetPointService;

    @Override
    public int insertPlan(PlanDTO planDTO) {
        int flag = 0;
        // 参数校验



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
//            bizPresetPointService.setPlanPrePoint(planEntity.getPlanId(),)
            // 区域信息
            if (ObjectUtil.isNotNull(planDTO.getPlanAreaDTOS()) && !planDTO.getPlanAreaDTOS().isEmpty()) {

            }
        } else {
            throw new RuntimeException("计划添加失败");
        }
        return flag;
    }

    @Override
    public int updatePlan(PlanDTO planDTO) {
        return 0;
    }

    /**
     * 根据id查询
     * @param planId 计划id
     * @return 返回结果
     */
    @Override
    public NewPlanVo queryById(Long planId) {
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
        Page<NewPlanVo> page = planMapper.queryPage(selectNewPlanDTO, permission.getDeptIds(), permission.getDateScopeSelf(), currentUser.getUserName());
        if (ListUtils.isNotNull(page.getResult())) {
            page.getResult().forEach(newPlanVO -> {
                List<PlanAreaDTO> planAreaDTOS = planAreaService.getByPlanId(newPlanVO.getPlanId());
                newPlanVO.setPlanAreaDTOS(planAreaDTOS);
                newPlanVO.setStartTimeFmt(DateUtils.getDateStrByTime(newPlanVO.getStartTime()));
                newPlanVO.setEndTimeFmt(DateUtils.getDateStrByTime(newPlanVO.getEndTime()));
                // 工作面名称
                String workFaceName = getWorkFaceName(newPlanVO.getWorkFaceId());
                newPlanVO.setWorkFaceName(workFaceName);
                //审核状态字典lable
                String auditStatus = sysDictDataMapper.selectDictLabel(ConstantsInfo.AUDIT_STATUS_DICT_TYPE, newPlanVO.getState());
                newPlanVO.setStatusFmt(auditStatus);
                if (newPlanVO.getState().equals(ConstantsInfo.REJECTED)) {
                    // 获取驳回原因
                    newPlanVO.setRejectReason(getRejectReason(newPlanVO.getPlanId()));
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
            List<BizProjectRecord> bizProjectRecords = bizProjectRecordMapper.selectList(new LambdaQueryWrapper<BizProjectRecord>()
                    .eq(BizProjectRecord::getPlanId, planId)
                    .eq(BizProjectRecord::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ListUtils.isNotNull(bizProjectRecords)) {
                throw new RuntimeException("该计划下有填报信息,不能删除");
            }
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

    private void checkParameter(PlanDTO planDTO) {
        if (ObjectUtil.isNull(planDTO)) {
            throw new RuntimeException("参数错误,参数不能为空");
        }
        if (ListUtils.isNull(planDTO.getPlanAreaDTOS())) {
            throw new RuntimeException("区域信息不能为空");
        }
    }

    private void checkArea(PlanDTO planDTO) {
        if (ListUtils.isNull(planDTO.getPlanAreaDTOS()) || planDTO.getPlanAreaDTOS().isEmpty()) {
            throw new RuntimeException("区域信息不能为空");
        }
        planDTO.getPlanAreaDTOS().forEach(planAreaDTO -> {
            List<PlanAreaEntity> planAreaEntities = planAreaMapper.selectList(new LambdaQueryWrapper<PlanAreaEntity>()
                    .eq(PlanAreaEntity::getTunnelId, planAreaDTO.getTunnelId()));
            if (ListUtils.isNotNull(planAreaEntities)) {
                planAreaEntities.forEach(planAreaEntity -> {
                    // 判断输入起始导线点与对照体起始导线点是否相同
                    if (Objects.equals(planAreaDTO.getStartTraversePointId(), planAreaEntity.getStartTraversePointId())) {
                        char firstChar = planAreaEntity.getStartDistance().charAt(0);
                        char charAt = planAreaDTO.getStartDistance().charAt(0);

                        // 判断输入的起始距离与对照体的起始距离方向是否一致
                        if (charAt != firstChar) {
                            throw new RuntimeException("输入的区域与之前计划区域有重叠,请重新输入");
                        } else {
                            boolean b = DataJudgeUtils.compare(planAreaDTO.getStartDistance(), planAreaEntity.getStartDistance());
                            // 判断是否 输入的起始距离 < 对照体的起始距离
                            if (b) {
                                BizTravePoint bizTravePoint = getBizTravePoint(planAreaEntity.getStartTraversePointId());
                                Long no = bizTravePoint.getNo();
                                BizTravePoint eTraPoint = getBizTravePoint(planAreaDTO.getEndTraversePointId());
                                BizTravePoint sTraPoint = getBizTravePoint(planAreaDTO.getStartTraversePointId());
                                // 判断对照体起始距离方向
                                if (firstChar == '-') {
                                    // 判断是否 输入的终始点 < 对照体起始点下一个导线点 and 输入的起始点 < 输入的终始点
                                    if (eTraPoint.getNo() < no + 1L && sTraPoint.getNo() < eTraPoint.getNo()) {
                                        // 判断输入终始点是否与对照体的起始点相同
                                        if (Objects.equals(planAreaDTO.getEndTraversePointId(), planAreaEntity.getStartTraversePointId())) {
                                            // 判断输入的终始距离方向是否与起始距离方向一致
                                            if (planAreaDTO.getEndDistance().charAt(0) == '-') {
                                                boolean compare = DataJudgeUtils.compare(planAreaDTO.getStartDistance(), planAreaDTO.getEndDistance());
                                                boolean compareTwo = DataJudgeUtils.compareTwo(planAreaDTO.getEndDistance(), planAreaEntity.getStartDistance());
                                                // 判断是否 输入的终始距离 > 输入起始距离 and 输入的终始距离 <= 对照体的起始距离
                                                if (!compare && compareTwo) {
                                                    throw new RuntimeException("输入的区域与之前计划区域有重叠,请重新输入");
                                                }
                                            } else {
                                                throw new RuntimeException("输入的区域与之前计划区域有重叠,请重新输入");
                                            }
                                        }
                                    } else {
                                        throw new RuntimeException("输入的区域不符合[起始导线点小于终始导线点]的规则!!");
                                    }
                                } else {
                                    // 判断是否 输入的终始点 <= 对照体起始点下一个导线点 and 输入的起始点 < 输入的终始点
                                    if (eTraPoint.getNo() <= no + 1L && sTraPoint.getNo() < eTraPoint.getNo()) {
                                        // 判断输入终始点是否与对照体的起始点相同
                                        if (Objects.equals(planAreaDTO.getEndTraversePointId(), planAreaEntity.getStartTraversePointId())) {
                                            // 判断输入的终始距离方向是否与起始距离方向一致
                                            if (planAreaDTO.getEndDistance().charAt(0) == '-') {
                                                boolean compare = DataJudgeUtils.compare(planAreaDTO.getStartDistance(), planAreaDTO.getEndDistance());
                                                boolean compareTwo = DataJudgeUtils.compareTwo(planAreaDTO.getEndDistance(), planAreaEntity.getStartDistance());
                                                // 判断是否 输入的终始距离 > 输入起始距离 and 输入的终始距离 <= 对照体的起始距离
                                                if (!compare && compareTwo) {
                                                    throw new RuntimeException("输入的区域与之前计划区域有重叠,请重新输入");
                                                }
                                            } else {
                                                throw new RuntimeException("输入的区域不符合[起始导线点小于终始导线点]的规则!!");
                                            }
                                        }
                                    } else {
                                        throw new RuntimeException("输入的区域不符合[起始导线点小于终始导线点]的规则!!");
                                    }
                                }
                            } else {
                                throw new RuntimeException("输入的区域与之前计划区域有重叠,请重新输入");
                            }
                        }
                    }
                    // 判断输入起始导线点与对照体终始导线点是否相同
                    if (Objects.equals(planAreaDTO.getStartTraversePointId(), planAreaEntity.getEndTraversePointId())) {
                        char firstChar = planAreaEntity.getEndDistance().charAt(0);
                        char charAt = planAreaDTO.getStartDistance().charAt(0);
                        char endCharAt = planAreaDTO.getEndDistance().charAt(0);
                        // 判断输入的起始距离与对照体的终始距离方向是否一致
                        if (charAt != firstChar) {
                            throw new RuntimeException("输入的区域与之前计划区域有重叠,请重新输入");
                        } else {
                            boolean compare = DataJudgeUtils.compare(planAreaDTO.getStartDistance(), planAreaEntity.getEndDistance());
                            // 判断输入的起始距离 > 对照体终始距离
                            if (compare) {
                                Long eNo = getBizTravePoint(planAreaDTO.getEndTraversePointId()).getNo();
                                Long sNo = getBizTravePoint(planAreaDTO.getStartTraversePointId()).getNo();
                                // 判断 输入的终始点 >= 输入的起始点
                                if (eNo >= sNo) {
                                    // 判断 输入的终始点 = 输入的起始点
                                    if (eNo.equals(sNo)) {
                                        boolean b = DataJudgeUtils.compare(planAreaDTO.getStartDistance(), planAreaDTO.getEndDistance());
                                        // 判断 输入的起始距离 < 输入的终始距离
                                        if (!b) {
                                            throw new RuntimeException("输入的区域不符合[起始点小于终始点]的规则!!");
                                        }
                                    }
                                    Long initialNo = getBizTravePoint(planAreaEntity.getEndTraversePointId()).getNo();
                                    Integer maxNo = bizTravePointMapper.selectMaxNo(planAreaDTO.getTunnelId());
                                    AtomicReference<Long> planAreaId = new AtomicReference<>(0L);
                                    ObjectMapper objectMapper = new ObjectMapper();
                                    try {
                                        int startNo = Math.toIntExact(initialNo) + 1;
                                        for (int no = startNo; no <= maxNo; no++) {
                                            for (PlanAreaEntity pae : planAreaEntities) {
                                                if (no == pae.getStartTraversePointId()) {
                                                    if (no == eNo) {
                                                        planAreaId.set(pae.getPlanId());
                                                    }
                                                } else {
                                                    String traversePointGather = pae.getTraversePointGather();
                                                    if (traversePointGather != null && !traversePointGather.isEmpty()) {
                                                        List<TraversePointGatherDTO> tPointGatherDTOS = objectMapper.readValue(traversePointGather,
                                                                new TypeReference<List<TraversePointGatherDTO>>() {
                                                                });
                                                        if (tPointGatherDTOS != null && !tPointGatherDTOS.isEmpty()) {
                                                            for (TraversePointGatherDTO tpg : tPointGatherDTOS) {
                                                                Long targetNo = getBizTravePoint(tpg.getTraversePointId()).getNo();
                                                                if (no == targetNo.intValue() && no == eNo) {
                                                                    if (planAreaDTO.getEndTraversePointId().equals(pae.getStartTraversePointId())) {
                                                                        planAreaId.set(pae.getPlanId());
                                                                    } else {
                                                                        throw new RuntimeException("输入的区域与之前计划区域有重叠,请重新输入");
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        BizTravePoint bizTravePoint = getBizTraPointTwo(pae.getTunnelId(), (long) no);
                                                        if (pae.getStartTraversePointId().equals(bizTravePoint.getPointId())) {
                                                            planAreaId.set(pae.getPlanId());
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } catch (ArithmeticException e) {
                                        throw new IllegalArgumentException("initialNo 超出 int 范围", e);
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException("JSON 解析失败: " + e.getMessage(), e);
                                    }
                                    PlanAreaEntity planArea = planAreaMapper.selectOne(new LambdaQueryWrapper<PlanAreaEntity>()
                                            .eq(PlanAreaEntity::getPlanAreaId, planAreaId.get()));
                                    String conditionDistance = planArea.getStartDistance();
                                    char symbol = conditionDistance.charAt(0);
                                    // 判断标志点起始距离方向是否为'-'
                                    if (symbol == '-') {
                                        // 判断输入的终始导线点 = 标志点起始导线点
                                        if (planAreaDTO.getEndTraversePointId().equals(planArea.getStartTraversePointId())) {
                                            // 判断输入的终始距离与标记点起始距离方向是否相同
                                            if (endCharAt == symbol) {
                                                boolean compared = DataJudgeUtils.compareTwo(planAreaDTO.getEndDistance(), conditionDistance);
                                                // 判断输入的终始距离 <= 标记点起始距离
                                                if (!compared) {
                                                    throw new RuntimeException("输入的区域不符合[终始区域大于起始区域的规则]!!");
                                                }
                                            } else {
                                                throw new RuntimeException("输入的区域不符合[终始区域大于起始区域的规则]!!");
                                            }
                                        } else {
                                            // 判断输入的终始距离方向
                                            if (endCharAt == '-') {
                                                // 获取输入终始导线点的距前一个导线点的距离
                                                Double prePointDistance = getPrePointDistance(planAreaDTO.getEndTraversePointId());
                                                // 获取距离差
                                                double v = DataJudgeUtils.doingPoorly(prePointDistance, Double.valueOf(planAreaDTO.getStartDistance()));
                                                // 判断输入的终始距离是否小于距离差
                                                boolean avc = DataJudgeUtils.absoluteValueCompareTwo(v, planAreaDTO.getEndDistance());
                                                if (!avc) {
                                                    throw new RuntimeException("输入的区域不符合[终始区域大于起始区域的规则]!!");
                                                }
                                            } else {
                                                // 获取标志起始导线点与上一个导线点的距离
                                                Double prePointDistance = getPrePointDistance(planArea.getStartTraversePointId());
                                                // 获取去除标志起始距离的距离
                                                double a = DataJudgeUtils.doingPoorly(prePointDistance, Double.valueOf(conditionDistance));
                                                String ed = planAreaDTO.getEndDistance();
                                                boolean b = DataJudgeUtils.absoluteValueCompare(String.valueOf(a), ed);
                                                if (!b) {
                                                    throw new RuntimeException("输入的区域与之前计划区域有重叠,请重新输入");
                                                }
                                            }
                                        }
                                    } else {
                                        // 判断输入的终始导线点 = 标志点起始导线点
                                        if (planAreaDTO.getEndTraversePointId().equals(planArea.getStartTraversePointId())) {
                                            // 判断输入的终始距离与标记点起始距离方向是否相同
                                            if (endCharAt == symbol) {
                                                boolean compared = DataJudgeUtils.compareTwo(planAreaDTO.getEndDistance(), conditionDistance);
                                                // 判断输入的终始距离 <= 标记点起始距离
                                                if (!compared) {
                                                    throw new RuntimeException("输入的区域不符合[终始区域大于起始区域的规则]!!");
                                                }
                                            } else {
                                                // 获取输入终始导线点的距前一个导线点的距离
                                                Double prePointDistance = getPrePointDistance(planAreaDTO.getEndTraversePointId());
                                                // 获取距离差
                                                double v = DataJudgeUtils.doingPoorly(prePointDistance, Double.valueOf(planAreaDTO.getStartDistance()));
                                                // 判断输入的终始距离是否小于距离差
                                                boolean avc = DataJudgeUtils.absoluteValueCompareTwo(v, planAreaDTO.getEndDistance());
                                                if (!avc) {
                                                    throw new RuntimeException("输入的区域不符合[终始区域大于起始区域的规则]!!");
                                                }
                                            }
                                        } else {
                                            if (endCharAt == '-') {
                                                BizTravePoint travePoint = getBizTravePoint(planArea.getStartTraversePointId());
                                                // 获取标记点开始点下一个导线点
                                                BizTravePoint bizTravePoint = getBizTraPointTwo(planArea.getTunnelId(), travePoint.getNo());
                                                // 获取两个导线点距离
                                                Double distance = getPrePointDistance(bizTravePoint.getPointId());
                                                // 获取总距离-标记点开始点距离的差值
                                                double doingPoorly = DataJudgeUtils.doingPoorly(distance, Double.valueOf(planArea.getStartDistance()));
                                                String s = "-" + doingPoorly;
                                                boolean compared = DataJudgeUtils.compare(planAreaDTO.getEndDistance(), s);
                                                // 判断输入的终始距离是否小于标记点起始距离的反转距离
                                                if (!compared) {
                                                    throw new RuntimeException("输入的区域与之前计划区域有重叠,请重新输入");
                                                }
                                            }
                                            throw new RuntimeException("输入的区域与之前计划区域有重叠,请重新输入");
                                        }
                                    }
                                } else {
                                    throw new RuntimeException("输入的区域不符合[起始导线点小于终始导线点]的规则!!");
                                }

                            } else {
                                throw new RuntimeException("输入的区域与之前计划区域有重叠,请重新输入");
                            }
                        }
                    }
                });
            }
        });

    }

    private BizTravePoint getBizTravePoint(Long pointId) {
        BizTravePoint bizTravePoint = bizTravePointMapper.selectOne(new LambdaQueryWrapper<BizTravePoint>()
                .eq(BizTravePoint::getPointId, pointId)
                .eq(BizTravePoint::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(bizTravePoint)) {
            throw new RuntimeException("发生未知异常，请联系管理员!");
        }
        return bizTravePoint;
    }

    private BizTravePoint getBizTraPointTwo(Long tunnelId, Long no) {
        BizTravePoint bizTravePoint = new BizTravePoint();
        bizTravePoint = bizTravePointMapper.selectOne(new LambdaQueryWrapper<BizTravePoint>()
                .eq(BizTravePoint::getTunnelId, tunnelId)
                .eq(BizTravePoint::getNo, no + 1)
                .eq(BizTravePoint::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(bizTravePoint)) {
            throw new RuntimeException("获取导线点异常！");
        }
        return bizTravePoint;
    }

    /**
     * 获取距前一个导线点的距离
     */
    private Double getPrePointDistance(Long pointId) {
        BizTravePoint bizTravePoint = bizTravePointMapper.selectOne(new LambdaQueryWrapper<BizTravePoint>()
                .eq(BizTravePoint::getPointId, pointId)
                .eq(BizTravePoint::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(bizTravePoint)) {
            throw new RuntimeException("获取导线点异常！");
        }
        return bizTravePoint.getPrePointDistance();
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