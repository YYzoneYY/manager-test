package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.largeScreen.ProjectDTO;
import com.ruoyi.system.domain.dto.largeScreen.ProjectTypeDTO;
import com.ruoyi.system.domain.dto.largeScreen.Select1DTO;
import com.ruoyi.system.mapper.BizProjectRecordMapper;
import com.ruoyi.system.mapper.BizWorkfaceMapper;
import com.ruoyi.system.mapper.SysDictDataMapper;
import com.ruoyi.system.mapper.TunnelMapper;
import com.ruoyi.system.service.LargeScreenService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: shikai
 * @date: 2025/5/29
 * @description:
 */

@Service
public class LargeScreenServiceImpl implements LargeScreenService {

    @Resource
    private BizProjectRecordMapper bizProjectRecordMapper;

    @Resource
    private TunnelMapper tunnelMapper;

    @Resource
    private BizWorkfaceMapper bizWorkfaceMapper;

    @Resource
    private SysDictDataMapper sysDictDataMapper;


    @Override
    public List<ProjectDTO> obtainProject(String tag, Select1DTO select1DTO) {
        List<ProjectDTO> projectDTOS;

        if ("1".equals(tag)) {
            projectDTOS = bizProjectRecordMapper.queryProjectOfAudit(select1DTO);
        } else {
            projectDTOS = bizProjectRecordMapper.queryProjectOfUnaudited(select1DTO);
        }

        return enrichProjectDTOsWithConstructionSite(projectDTOS);
    }

    @Override
    public List<ProjectTypeDTO> obtainProjectType(Long startTime, Long endTime) {
        List<ProjectTypeDTO> projectTypeDTOS = bizProjectRecordMapper.queryProjectType(startTime, endTime);

        if (ObjectUtil.isNotEmpty(projectTypeDTOS)) {
            // 收集所有 drillType
            List<String> drillTypes = projectTypeDTOS.stream()
                    .map(ProjectTypeDTO::getDrillType)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            // 批量查询字典标签
            Map<String, String> dictLabelMap = sysDictDataMapper.selectDictLabels(ConstantsInfo.DRILL_TYPE_DICT_TYPE, drillTypes);
            // 设置格式化字段
            projectTypeDTOS.forEach(projectTypeDTO -> {
                String drillType = projectTypeDTO.getDrillType();
                if (drillType != null) {
                    projectTypeDTO.setDrillTypeFmt(dictLabelMap.getOrDefault(drillType, ""));
                }
            });
        }
        return projectTypeDTOS;
    }

    private List<ProjectDTO> enrichProjectDTOsWithConstructionSite(List<ProjectDTO> projectDTOS) {
        if (projectDTOS == null || projectDTOS.isEmpty()) {
            return projectDTOS;
        }

        // 收集需要查询的 ID
        List<Long> tunnelIds = new ArrayList<>();
        List<Long> workFaceIds = new ArrayList<>();

        for (ProjectDTO projectDTO : projectDTOS) {
            String constructType = projectDTO.getConstructType();
            if (ConstantsInfo.TUNNELING.equals(constructType)) {
                tunnelIds.add(projectDTO.getTunnelId());
            } else if (ConstantsInfo.STOPE.equals(constructType)) {
                workFaceIds.add(projectDTO.getWorkFaceId());
            }
        }

        // 批量查询 TunnelEntity 和 BizWorkface
        List<TunnelEntity> tunnels = tunnelMapper.selectList(new LambdaQueryWrapper<TunnelEntity>()
                .in(TunnelEntity::getTunnelId, tunnelIds)
                .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        Map<Long, String> tunnelMap = tunnels.stream()
                .collect(Collectors.toMap(
                        TunnelEntity::getTunnelId,
                        TunnelEntity::getTunnelName,
                        (existing, replacement) -> existing));

        List<BizWorkface> workfaces = bizWorkfaceMapper.selectList(new LambdaQueryWrapper<BizWorkface>()
                .in(BizWorkface::getWorkfaceId, workFaceIds)
                .eq(BizWorkface::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        Map<Long, String> workfaceMap = workfaces.stream()
                .collect(Collectors.toMap(
                        BizWorkface::getWorkfaceId,
                        BizWorkface::getWorkfaceName,
                        (existing, replacement) -> existing));

        // 设置 ConstructionSite
        for (ProjectDTO projectDTO : projectDTOS) {
            String constructType = projectDTO.getConstructType();
            String constructionSite = "";
            if (ConstantsInfo.TUNNELING.equals(constructType)) {
                constructionSite = tunnelMap.getOrDefault(projectDTO.getTunnelId(), "");
            } else if (ConstantsInfo.STOPE.equals(constructType)) {
                constructionSite = workfaceMap.getOrDefault(projectDTO.getWorkFaceId(), "");
            }
            projectDTO.setConstructionSite(constructionSite);
        }

        return projectDTOS;
    }


}