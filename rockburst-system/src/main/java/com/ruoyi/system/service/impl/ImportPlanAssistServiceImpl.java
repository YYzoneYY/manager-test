package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.BizTravePoint;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.PlanEntity;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.IBizPresetPointService;
import com.ruoyi.system.service.IBizTravePointService;
import com.ruoyi.system.service.ImportPlanAssistService;
import com.ruoyi.system.service.PlanAreaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: shikai
 * @date: 2025/2/21
 * @description:
 */

@Service
@Validated
@Transactional
public class ImportPlanAssistServiceImpl implements ImportPlanAssistService {

    @Resource
    private PlanMapper planMapper;

    @Resource
    private BizWorkfaceMapper bizWorkfaceMapper;

    @Resource
    private TunnelMapper tunnelMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private IBizTravePointService bizTravePointService;

    @Resource
    private PlanAreaService planAreaService;

    @Resource
    private IBizPresetPointService bizPresetPointService;

    @Resource
    private SysDictDataMapper sysDictDataMapper;

    @Resource
    private BizTravePointMapper bizTravePointMapper;


    @Override
    public int importDataAdd(ImportPlanDTO importPlanDTO, Long mineId) throws ParseException {
        int flag = 0;
        PlanEntity planEntity = new PlanEntity();
        planEntity.setPlanName(importPlanDTO.getPlanName());
        planEntity.setWorkFaceId(getBizWorkFaceId(importPlanDTO.getWorkFaceName()));
        planEntity.setAnnual(dicValue(ConstantsInfo.YEAR_DICT_TYPE, importPlanDTO.getAnnual())); // 年度
        planEntity.setPlanType(dicValue(ConstantsInfo.PLAN_TYPE_DICT_TYPE, importPlanDTO.getPlanType())); // 计划类型
        planEntity.setType(dicValue(ConstantsInfo.TYPE_DICT_TYPE, importPlanDTO.getType())); // 类型
        planEntity.setDrillType(dicValue(ConstantsInfo.DRILL_TYPE_DICT_TYPE, importPlanDTO.getDrillType())); // 钻孔类型
        planEntity.setTotalDrillNumber(Integer.valueOf(importPlanDTO.getTotalDrillNumber()));
        planEntity.setTotalHoleDepth(new BigDecimal(importPlanDTO.getTotalHoleDepth()));
        planEntity.setStartTime(DateUtils.getDateByTime(importPlanDTO.getStartTime()));
        planEntity.setEndTime(DateUtils.getDateByTime(importPlanDTO.getEndTime()));
        planEntity.setState(ConstantsInfo.AUDIT_STATUS_DICT_VALUE);
        planEntity.setCreateTime(System.currentTimeMillis());
        planEntity.setCreateBy(SecurityUtils.getUserId());
        planEntity.setShieldStatus(ConstantsInfo.UN_SHIELD_STATUS);
        planEntity.setMineId(mineId);
        SysUser sysUser = sysUserMapper.selectUserById(SecurityUtils.getUserId());
        planEntity.setDeptId(sysUser.getDeptId());
        planEntity.setDelFlag(ConstantsInfo.ZERO_DEL_FLAG);
        flag = planMapper.insert(planEntity);
        if (flag > 0) {
            List<PlanAreaDTO> planAreaDTOS = new ArrayList<>();
            List<TraversePointGatherDTO> traversePointGatherDTOS = new ArrayList<>();
            Long tunnelId = getTunnelId(planEntity.getWorkFaceId(), importPlanDTO.getTunnelName());
            PlanAreaDTO planAreaDTO = assembleDTO(tunnelId, getPointId(tunnelId, importPlanDTO.getStartPoint()),
                    importPlanDTO.getStartDistance(),
                    getPointId(tunnelId, importPlanDTO.getEndPoint()), importPlanDTO.getEndDistance());
            planAreaDTOS.add(planAreaDTO);
            // 获取计划区域内所有的导线点
            List<Long> pointList = bizTravePointService.getInPointList(planAreaDTO.getStartTraversePointId(),
                    Double.valueOf(planAreaDTO.getStartDistance()),
                    planAreaDTO.getEndTraversePointId(), Double.valueOf(planAreaDTO.getEndDistance()));
            if (ObjectUtil.isNotNull(pointList) && !pointList.isEmpty()) {
                for (Long point : pointList) {
                    TraversePointGatherDTO traversePointGatherDTO = new TraversePointGatherDTO();
                    traversePointGatherDTO.setTraversePointId(point);
                    traversePointGatherDTOS.add(traversePointGatherDTO);
                }
            }
            boolean insert = planAreaService.insert(planEntity.getPlanId(), planEntity.getWorkFaceId(), planEntity.getType(), planAreaDTOS, traversePointGatherDTOS);
            if (!insert) {
                throw new RuntimeException("发生未知异常,计划添加失败！！");
            }
        } else {
            throw new ServiceException("保存数据失败！！");
        }
        return flag;
    }


    @Override
    public int importDataAddTwo(ImportPlanTwoDTO importPlanTwoDTO, Long mineId) throws ParseException {
        int flag = 0;
        PlanEntity planEntity = new PlanEntity();
        planEntity.setPlanName(importPlanTwoDTO.getPlanName());
        planEntity.setWorkFaceId(getBizWorkFaceId(importPlanTwoDTO.getWorkFaceName()));
        planEntity.setAnnual(dicValue(ConstantsInfo.YEAR_DICT_TYPE, importPlanTwoDTO.getAnnual())); // 年度
        planEntity.setPlanType(dicValue(ConstantsInfo.PLAN_TYPE_DICT_TYPE, importPlanTwoDTO.getPlanType())); // 计划类型
        planEntity.setType(dicValue(ConstantsInfo.TYPE_DICT_TYPE, importPlanTwoDTO.getType())); // 类型
        planEntity.setDrillType(dicValue(ConstantsInfo.DRILL_TYPE_DICT_TYPE, importPlanTwoDTO.getDrillType())); // 钻孔类型
        planEntity.setTotalDrillNumber(Integer.valueOf(importPlanTwoDTO.getTotalDrillNumber()));
        planEntity.setTotalHoleDepth(new BigDecimal(importPlanTwoDTO.getTotalHoleDepth()));
        planEntity.setStartTime(DateUtils.getDateByTime(importPlanTwoDTO.getStartTime()));
        planEntity.setEndTime(DateUtils.getDateByTime(importPlanTwoDTO.getEndTime()));
        planEntity.setState(ConstantsInfo.AUDIT_STATUS_DICT_VALUE);
        planEntity.setCreateTime(System.currentTimeMillis());
        planEntity.setCreateBy(SecurityUtils.getUserId());
        planEntity.setShieldStatus(ConstantsInfo.UN_SHIELD_STATUS);
        SysUser sysUser = sysUserMapper.selectUserById(SecurityUtils.getUserId());
        planEntity.setDeptId(sysUser.getDeptId());
        planEntity.setDelFlag(ConstantsInfo.ZERO_DEL_FLAG);
        planEntity.setMineId(mineId);
        flag = planMapper.insert(planEntity);
        if (flag > 0) {
            List<PlanAreaBatchDTO> planAreaBatchDTOS = new ArrayList<>();
            Long tunnelId = getTunnelId(planEntity.getWorkFaceId(), importPlanTwoDTO.getTunnelName());
            Long tunnelIdT = getTunnelId(planEntity.getWorkFaceId(), importPlanTwoDTO.getTunnelNameTwo());
            List<AddDTO> addDTOS = assembleDTOs(importPlanTwoDTO, tunnelId, tunnelIdT);
            if (ObjectUtil.isNotNull(addDTOS) && !addDTOS.isEmpty()) {
                addDTOS.forEach(addDTO -> {
                    PlanAreaBatchDTO planAreaBatchDTO = new PlanAreaBatchDTO();
                    BeanUtils.copyProperties(addDTO, planAreaBatchDTO);
                    planAreaBatchDTO.setPlanId(planEntity.getPlanId());
                    planAreaBatchDTO.setWorkFaceId(planEntity.getWorkFaceId());
                    planAreaBatchDTO.setType(planEntity.getType());
                    planAreaBatchDTOS.add(planAreaBatchDTO);
                });
            }
//            List<PlanAreaDTO> planAreaDTOS = assembleAreaDTOs(addDTOS);
            boolean batchInsert = planAreaService.batchInsert(planAreaBatchDTOS);
            if (!batchInsert) {
                throw new RuntimeException("发生未知异常,计划添加失败！！");
            }
        } else {
            throw new ServiceException("保存数据失败！！");
        }
        return flag;
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
     * 组装计划区域DTOs
     */
    private List<PlanAreaDTO> assembleAreaDTOs(List<AddDTO> addDTOS) {
        List<PlanAreaDTO> planAreaDTOS = new ArrayList<>();
        addDTOS.forEach(addDTO -> {
            PlanAreaDTO planAreaDTO = new PlanAreaDTO();
            BeanUtils.copyProperties(addDTO, planAreaDTO);
            planAreaDTOS.add(planAreaDTO);
        });
        return planAreaDTOS;
    }

    /**
     * 组装辅助AddDTOs
     */
    private List<AddDTO> assembleDTOs(ImportPlanTwoDTO importPlanTwoDTO, Long tunnelId, Long tunnelIdT) {
        List<AddDTO> addDTOS = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        AddDTO addDTO = new AddDTO();
        addDTO.setTunnelId(tunnelId);
        addDTO.setStartTraversePointId(getPointId(tunnelId, importPlanTwoDTO.getStartPoint()));
        addDTO.setStartDistance(importPlanTwoDTO.getStartDistance());
        addDTO.setEndTraversePointId(getPointId(tunnelId, importPlanTwoDTO.getEndPoint()));
        addDTO.setEndDistance(importPlanTwoDTO.getEndDistance());
        List<TraversePointGatherDTO> traversePointGatherDTOS = assembleTraversePointGatherDTOS(getPointId(tunnelId, importPlanTwoDTO.getStartPoint()),
                importPlanTwoDTO.getStartDistance(), getPointId(tunnelId, importPlanTwoDTO.getEndPoint()),
                importPlanTwoDTO.getEndDistance());
        try {
            String gather = objectMapper.writeValueAsString(traversePointGatherDTOS);
            addDTO.setTraversePointGather(gather);
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
        addDTOS.add(addDTO);
        AddDTO addDTOTwo = new AddDTO();
        addDTOTwo.setTunnelId(tunnelIdT);
        addDTOTwo.setStartTraversePointId(getPointId(tunnelIdT, importPlanTwoDTO.getStartPointTwo()));
        addDTOTwo.setStartDistance(importPlanTwoDTO.getStartDistanceTwo());
        addDTOTwo.setEndTraversePointId(getPointId(tunnelIdT, importPlanTwoDTO.getEndPointTwo()));
        addDTOTwo.setEndDistance(importPlanTwoDTO.getEndDistanceTwo());
        List<TraversePointGatherDTO> traversePointGatherDTOST = assembleTraversePointGatherDTOS(getPointId(tunnelIdT, importPlanTwoDTO.getStartPointTwo()),
                importPlanTwoDTO.getStartDistanceTwo(), getPointId(tunnelIdT, importPlanTwoDTO.getEndPointTwo()),
                importPlanTwoDTO.getEndDistanceTwo());
        try {
            String gather = objectMapper.writeValueAsString(traversePointGatherDTOST);
            addDTOTwo.setTraversePointGather(gather);
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
        addDTOS.add(addDTOTwo);
        return addDTOS;
    }

    /**
     * 组装导线集合
     */
    private List<TraversePointGatherDTO> assembleTraversePointGatherDTOS(Long sPointId, String sDistance,
                                                                         Long ePointId, String eDistance) {
        List<TraversePointGatherDTO> traversePointGatherDTOS = new ArrayList<>();
        // 获取计划区域内所有的导线点
        List<Long> pointList = bizTravePointService.getInPointList(sPointId, Double.valueOf(sDistance), ePointId, Double.valueOf(eDistance));
        if (ObjectUtil.isNotNull(pointList) && !pointList.isEmpty()) {
            for (Long point : pointList) {
                TraversePointGatherDTO traversePointGatherDTO = new TraversePointGatherDTO();
                traversePointGatherDTO.setTraversePointId(point);
                traversePointGatherDTOS.add(traversePointGatherDTO);
            }
        }
        return traversePointGatherDTOS;
    }

    /**
     * 根据导线名称和巷道id获取导线点id
     */
    private Long getPointId(Long tunnelId, String pointName) {
        Long pointId = null;
        BizTravePoint bizTravePoint = bizTravePointMapper.selectOne(new LambdaQueryWrapper<BizTravePoint>()
                .eq(BizTravePoint::getTunnelId, tunnelId)
                .eq(BizTravePoint::getPointName, pointName)
                .eq(BizTravePoint::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(bizTravePoint)) {
            throw new RuntimeException("获取导线点异常！");
        }
        pointId = bizTravePoint.getPointId();
        return pointId;
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
     * 根据工作面名称获取工作面id
     */
    private Long getBizWorkFaceId(String workFaceName) {
        Long bizWorkFaceId = null;
        BizWorkface bizWorkface = bizWorkfaceMapper.selectOne(new LambdaQueryWrapper<BizWorkface>()
                .eq(BizWorkface::getWorkfaceName, workFaceName)
                .eq(BizWorkface::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        return bizWorkFaceId = bizWorkface.getWorkfaceId();
    }

    /**
     * 根据工作面id和巷道名称获取巷道id
     */
    private Long getTunnelId(Long faceId, String tunnelName) {
        Long tunnelId = null;
        TunnelEntity tunnel = tunnelMapper.selectOne(new LambdaQueryWrapper<TunnelEntity>()
                        .eq(TunnelEntity::getWorkFaceId, faceId)
                .eq(TunnelEntity::getTunnelName, tunnelName)
                .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        return tunnelId = tunnel.getTunnelId();
    }
}