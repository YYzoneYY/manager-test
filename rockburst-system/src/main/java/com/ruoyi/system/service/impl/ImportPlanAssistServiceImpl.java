package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.BizTravePoint;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.PlanEntity;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.BizPlanPrePointDto;
import com.ruoyi.system.domain.dto.ImportPlanDTO;
import com.ruoyi.system.domain.dto.PlanAreaDTO;
import com.ruoyi.system.domain.dto.TraversePointGatherDTO;
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
    public int importDataAdd(ImportPlanDTO importPlanDTO) throws ParseException {
        int flag = 0;
        PlanEntity planEntity = new PlanEntity();
        planEntity.setPlanName(importPlanDTO.getPlanName());
        BizWorkface bizWorkface = bizWorkfaceMapper.selectOne(new LambdaQueryWrapper<BizWorkface>()
                .eq(BizWorkface::getWorkfaceName, importPlanDTO.getWorkFaceName())
                .eq(BizWorkface::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        planEntity.setWorkFaceId(bizWorkface.getWorkfaceId());
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
        SysUser sysUser = sysUserMapper.selectUserById(SecurityUtils.getUserId());
        planEntity.setDeptId(sysUser.getDeptId());
        planEntity.setDelFlag(ConstantsInfo.ZERO_DEL_FLAG);
        flag = planMapper.insert(planEntity);
        if (flag > 0) {
            List<PlanAreaDTO> planAreaDTOS = new ArrayList<>();
            List<TraversePointGatherDTO> traversePointGatherDTOS = new ArrayList<>();
            List<BizPlanPrePointDto> bizPlanPrePointDtos = new ArrayList<>();
            TunnelEntity tunnelEntity = tunnelMapper.selectOne(new LambdaQueryWrapper<TunnelEntity>()
                    .eq(TunnelEntity::getWorkFaceId, planEntity.getWorkFaceId())
                    .eq(TunnelEntity::getTunnelName, importPlanDTO.getTunnelName())
                    .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            Long tunnelId =tunnelEntity.getTunnelId();
            PlanAreaDTO planAreaDTO = assembleDTO(tunnelId, getPointId(tunnelId, importPlanDTO.getStartPoint()),
                    importPlanDTO.getStartDistance(),
                    getPointId(tunnelId, importPlanDTO.getEndPoint()), importPlanDTO.getEndDistance());
            planAreaDTOS.add(planAreaDTO);
            BizPlanPrePointDto bizPlanPrePointDto = getBizPlanPrePointDto(planAreaDTO);
            bizPlanPrePointDtos.add(bizPlanPrePointDto);
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
            boolean insert = planAreaService.insert(planEntity.getPlanId(), planAreaDTOS, traversePointGatherDTOS);
            if (insert) {
                bizPresetPointService.setPlanPrePoint(planEntity.getPlanId(),bizPlanPrePointDtos);
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
}