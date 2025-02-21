package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.Entity.PlanEntity;
import com.ruoyi.system.domain.Entity.ProjectWarnSchemeEntity;
import com.ruoyi.system.domain.dto.DistanceRuleDTO;
import com.ruoyi.system.domain.dto.ProjectWarnSchemeDTO;
import com.ruoyi.system.domain.dto.SelectProjectWarnDTO;
import com.ruoyi.system.domain.dto.WorkloadRuleDTO;
import com.ruoyi.system.domain.vo.ProjectWarnSchemeVO;
import com.ruoyi.system.mapper.PlanMapper;
import com.ruoyi.system.mapper.ProjectWarnSchemeMapper;
import com.ruoyi.system.service.ProjectWarnSchemeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author: shikai
 * @date: 2024/12/12
 * @description:
 */

@Service
@Transactional
public class ProjectWarnSchemeServiceImpl extends ServiceImpl<ProjectWarnSchemeMapper, ProjectWarnSchemeEntity> implements ProjectWarnSchemeService {

    @Resource
    private ProjectWarnSchemeMapper projectWarnSchemeMapper;

    @Resource
    private PlanMapper planMapper;

    /**
     * 新增工程预警方案
     * @param projectWarnSchemeDTO 参数实体类
     * @return 返回结果
     */
    @Override
    public int insert(ProjectWarnSchemeDTO projectWarnSchemeDTO) {
        int flag = 0;
        ObjectMapper objectMapper = new ObjectMapper();
        checkParam(projectWarnSchemeDTO);
        if (ListUtils.isNotNull(projectWarnSchemeDTO.getWorkloadRuleDTOS())) {
            checkWorkloadRule(projectWarnSchemeDTO.getWorkloadRuleDTOS(), projectWarnSchemeDTO.getPlanType());
        }
        if (ObjectUtil.isNotNull(projectWarnSchemeDTO.getDistanceRuleDTO())) {
            checkDistanceRule(projectWarnSchemeDTO.getDistanceRuleDTO(), projectWarnSchemeDTO.getPlanType());
        }
        Long selectCount = projectWarnSchemeMapper.selectCount(new LambdaQueryWrapper<ProjectWarnSchemeEntity>()
                .eq(ProjectWarnSchemeEntity::getSchemeName, projectWarnSchemeDTO.getSchemeName())
                .eq(ProjectWarnSchemeEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (selectCount > 0) {
            throw new RuntimeException("方案名称不能重复!");
        }
        ProjectWarnSchemeEntity projectWarnSchemeEntity = new ProjectWarnSchemeEntity();
        BeanUtils.copyProperties(projectWarnSchemeDTO, projectWarnSchemeEntity);
        try {
            String workloadRule = objectMapper.writeValueAsString(projectWarnSchemeDTO.getWorkloadRuleDTOS()).trim();
            String distanceRule = objectMapper.writeValueAsString(projectWarnSchemeDTO.getDistanceRuleDTO()).trim();
            projectWarnSchemeEntity.setWorkloadRule(workloadRule);
            projectWarnSchemeEntity.setDistanceRule(distanceRule);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        projectWarnSchemeEntity.setCreateTime(System.currentTimeMillis());
        projectWarnSchemeEntity.setCreateBy(SecurityUtils.getUserId());
        projectWarnSchemeEntity.setDelFlag(ConstantsInfo.ZERO_DEL_FLAG);
        flag = projectWarnSchemeMapper.insert(projectWarnSchemeEntity);
        if (flag <= 0) {
            throw new RuntimeException("新增预警方案失败");
        }
        return flag;
    }

    /**
     * 工程预警方案编辑
     * @param projectWarnSchemeDTO 参数实体类
     * @return 返回结果
     */
    @Override
    public int update(ProjectWarnSchemeDTO projectWarnSchemeDTO) {
        int flag = 0;
        ObjectMapper objectMapper = new ObjectMapper();
        checkParam(projectWarnSchemeDTO);
        if (ObjectUtil.isNull(projectWarnSchemeDTO.getProjectWarnSchemeId())) {
            throw new RuntimeException("参数错误,方案id不能为空！");
        }
        ProjectWarnSchemeEntity projectWarnSchemeEntity = projectWarnSchemeMapper.selectOne(new LambdaQueryWrapper<ProjectWarnSchemeEntity>()
                .eq(ProjectWarnSchemeEntity::getProjectWarnSchemeId, projectWarnSchemeDTO.getProjectWarnSchemeId())
                .eq(ProjectWarnSchemeEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(projectWarnSchemeEntity)) {
            throw new RuntimeException("参数错误,预警方案不存在！");
        }
        Long selectCount = projectWarnSchemeMapper.selectCount(new LambdaQueryWrapper<ProjectWarnSchemeEntity>()
                .eq(ProjectWarnSchemeEntity::getSchemeName, projectWarnSchemeDTO.getSchemeName())
                .ne(ProjectWarnSchemeEntity::getProjectWarnSchemeId, projectWarnSchemeDTO.getProjectWarnSchemeId())
                .eq(ProjectWarnSchemeEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (selectCount > 0) {
            throw new RuntimeException("方案名称已存在");
        }
        try {
            String workloadRule = objectMapper.writeValueAsString(projectWarnSchemeDTO.getWorkloadRuleDTOS()).trim();
            Long count = projectWarnSchemeMapper.selectCount(new LambdaQueryWrapper<ProjectWarnSchemeEntity>()
                    .eq(ProjectWarnSchemeEntity::getWorkloadRule, workloadRule)
                    .eq(ProjectWarnSchemeEntity::getPlanType, projectWarnSchemeDTO.getPlanType())
                    .ne(ProjectWarnSchemeEntity::getProjectWarnSchemeId, projectWarnSchemeDTO.getProjectWarnSchemeId())
                    .eq(ProjectWarnSchemeEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (count > 0) {
                throw new RuntimeException("已存在相同工作量规则的预警方案!!");
            }
            String distanceRule = objectMapper.writeValueAsString(projectWarnSchemeDTO.getDistanceRuleDTO()).trim();
            Long selectedCount = projectWarnSchemeMapper.selectCount(new LambdaQueryWrapper<ProjectWarnSchemeEntity>()
                    .eq(ProjectWarnSchemeEntity::getDistanceRule, distanceRule)
                    .eq(ProjectWarnSchemeEntity::getPlanType, projectWarnSchemeDTO.getPlanType())
                    .ne(ProjectWarnSchemeEntity::getProjectWarnSchemeId, projectWarnSchemeDTO.getProjectWarnSchemeId())
                    .eq(ProjectWarnSchemeEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (selectedCount > 0) {
                throw new RuntimeException("已存在相同距离规则的预警方案!!");
            }

            Long projectWarnSchemeId = projectWarnSchemeEntity.getProjectWarnSchemeId();
            BeanUtils.copyProperties(projectWarnSchemeDTO, projectWarnSchemeEntity);
            projectWarnSchemeEntity.setProjectWarnSchemeId(projectWarnSchemeId);
            projectWarnSchemeEntity.setWorkloadRule(workloadRule);
            projectWarnSchemeEntity.setDistanceRule(distanceRule);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        projectWarnSchemeEntity.setUpdateTime(System.currentTimeMillis());
        projectWarnSchemeEntity.setUpdateBy(SecurityUtils.getUserId());
        flag = projectWarnSchemeMapper.updateById(projectWarnSchemeEntity);
        if (flag <= 0) {
            throw new RuntimeException("工程预警方案修改失败");
        }
        return flag;
    }

    /**
     * 根据id查询
     * @param projectWarnSchemeId 工程预警方案id
     * @return 返回结果
     */
    @Override
    public ProjectWarnSchemeDTO detail(Long projectWarnSchemeId) {
        ObjectMapper objectMapper = new ObjectMapper();
        if (ObjectUtil.isNull(projectWarnSchemeId)) {
            throw new RuntimeException("参数错误,id不能为空");
        }
        ProjectWarnSchemeEntity projectWarnSchemeEntity = projectWarnSchemeMapper.selectOne(new LambdaQueryWrapper<ProjectWarnSchemeEntity>()
                .eq(ProjectWarnSchemeEntity::getProjectWarnSchemeId, projectWarnSchemeId)
                .eq(ProjectWarnSchemeEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(projectWarnSchemeEntity)) {
            throw new RuntimeException("未找到此工程预警方案");
        }
        ProjectWarnSchemeDTO projectWarnSchemeDTO = new ProjectWarnSchemeDTO();
        BeanUtils.copyProperties(projectWarnSchemeEntity, projectWarnSchemeDTO);
        String distanceRule = projectWarnSchemeEntity.getDistanceRule().trim();
        String workloadRule = projectWarnSchemeEntity.getWorkloadRule().trim();
        try {
            DistanceRuleDTO distanceRuleDTO = objectMapper.readValue(distanceRule, DistanceRuleDTO.class);
            List<WorkloadRuleDTO> workloadRuleDTOS = objectMapper.readValue(workloadRule, new TypeReference<List<WorkloadRuleDTO>>() {});
            projectWarnSchemeDTO.setDistanceRuleDTO(distanceRuleDTO);
            projectWarnSchemeDTO.setWorkloadRuleDTOS(workloadRuleDTOS);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return projectWarnSchemeDTO;
    }

    /**
     * 分页查询
     * @param selectProjectWarnDTO 查询参数DTO
     * @param pageNum 页数
     * @param pageSize 条数
     * @return 返回结果
     */
    @Override
    public TableData queryByPage(SelectProjectWarnDTO selectProjectWarnDTO, Integer pageNum, Integer pageSize) {
        TableData result = new TableData();
        ObjectMapper objectMapper = new ObjectMapper();
        if (null == pageNum || pageNum < 1) {
            pageNum = 1;
        }
        if (null == pageSize || pageSize < 1) {
            pageSize = 10;
        }
        PageHelper.startPage(pageNum, pageSize);
        Page<ProjectWarnSchemeVO> page = projectWarnSchemeMapper.selectQueryByPage(selectProjectWarnDTO);
        if (ListUtils.isNotNull(page.getResult())) {
            page.getResult().forEach(projectWarnSchemeVO -> {
                String distanceRule= projectWarnSchemeVO.getDistanceRule().trim();
                String workloadRule = projectWarnSchemeVO.getWorkloadRule().trim();
                String warnType = getWarnType(distanceRule, workloadRule);
                projectWarnSchemeVO.setWarnType(warnType);
                try {
                    DistanceRuleDTO distanceRuleDTO = objectMapper.readValue(distanceRule, DistanceRuleDTO.class);
                    List<WorkloadRuleDTO> workloadRuleDTOS = objectMapper.readValue(workloadRule, new TypeReference<List<WorkloadRuleDTO>>() {});
                    projectWarnSchemeVO.setDistanceRuleDTO(distanceRuleDTO);
                    projectWarnSchemeVO.setWorkloadRuleDTOS(workloadRuleDTOS);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
        result.setTotal(page.getTotal());
        result.setRows(page.getResult());
        return result;
    }

    /**
     * 删除
     * @param projectWarnSchemeIds 预警方案id
     * @return 返回结果
     */
    @Override
    public boolean deleteById(Long[] projectWarnSchemeIds) {
        boolean flag = false;
        if (projectWarnSchemeIds.length == 0) {
            throw new RuntimeException("请选择要删除的数据!");
        }
        List<Long> projectWarnSchemeIdList = Arrays.asList(projectWarnSchemeIds);
        projectWarnSchemeIdList.forEach(projectWarnSchemeId -> {
            ProjectWarnSchemeEntity projectWarnSchemeEntity = projectWarnSchemeMapper.selectOne(new LambdaQueryWrapper<ProjectWarnSchemeEntity>()
                    .eq(ProjectWarnSchemeEntity::getProjectWarnSchemeId, projectWarnSchemeId)
                    .eq(ProjectWarnSchemeEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ObjectUtil.isNull(projectWarnSchemeEntity)) {
                throw new RuntimeException("未找到id为" + projectWarnSchemeId + "的数据");
            }
            if (StringUtils.equals(ConstantsInfo.ENABLE, projectWarnSchemeEntity.getStatus())) {
                throw new RuntimeException("该预警方案已启用,无法删除");
            }
            List<PlanEntity> planEntities = planMapper.selectList(new LambdaQueryWrapper<PlanEntity>()
                    .eq(PlanEntity::getProjectWarnSchemeId, projectWarnSchemeId)
                    .eq(PlanEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ListUtils.isNotNull(planEntities) && !planEntities.isEmpty()) {
                throw new RuntimeException("该预警方案已被使用,无法删除");
            }
        });
        flag = this.removeBatchByIds(projectWarnSchemeIdList);
        return flag;
    }

    @Override
    public int batchEnableDisable(Long[] projectWarnSchemeIds) {
        int flag = 0;
        if (projectWarnSchemeIds.length == 0) {
            throw new RuntimeException("请选择要禁用的数据!");
        }
        List<Long> projectWarnSchemeIdList = Arrays.asList(projectWarnSchemeIds);
        projectWarnSchemeIdList.forEach(projectWarnSchemeId -> {
            ProjectWarnSchemeEntity projectWarnSchemeEntity = projectWarnSchemeMapper.selectOne(new LambdaQueryWrapper<ProjectWarnSchemeEntity>()
                    .eq(ProjectWarnSchemeEntity::getProjectWarnSchemeId, projectWarnSchemeId)
                    .eq(ProjectWarnSchemeEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ObjectUtil.isNull(projectWarnSchemeEntity)) {
                throw new RuntimeException("未找到id为" + projectWarnSchemeId + "的数据");
            }
            if (StringUtils.equals(ConstantsInfo.ENABLE, projectWarnSchemeEntity.getStatus())) {
                projectWarnSchemeEntity.setStatus(ConstantsInfo.DISABLE);
            } else {
                projectWarnSchemeEntity.setStatus(ConstantsInfo.ENABLE);
            }
            projectWarnSchemeMapper.updateById(projectWarnSchemeEntity);
        });
        return flag;
    }

    /**
     * 校验参数
     */
    private void checkParam(ProjectWarnSchemeDTO projectWarnSchemeDTO) {
        if (ObjectUtil.isNull(projectWarnSchemeDTO)) {
            throw new RuntimeException("参数错误,参数不能为空！");
        }
        if (ObjectUtil.isNull(projectWarnSchemeDTO.getSchemeName())) {
            throw new RuntimeException("方案名称不能为空！");
        }
        if (ObjectUtil.isNull(projectWarnSchemeDTO.getPlanType())) {
            throw new RuntimeException("计划类型不能为空！");
        }
    }

    /**
     * 校验工作量规则
     */
    private void checkWorkloadRule(List<WorkloadRuleDTO> workloadRuleDTOS, String planType) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String workloadRule = objectMapper.writeValueAsString(workloadRuleDTOS);
            String workloadRuleTrim = workloadRule.trim();
            Long count = projectWarnSchemeMapper.selectCount(new LambdaQueryWrapper<ProjectWarnSchemeEntity>()
                    .eq(ProjectWarnSchemeEntity::getWorkloadRule, workloadRuleTrim)
                    .eq(ProjectWarnSchemeEntity::getPlanType, planType)
                    .eq(ProjectWarnSchemeEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (count > 0) {
                throw new RuntimeException("已存在相同工作量规则的预警方案!!");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 校验距离规则
     */
    private void checkDistanceRule(DistanceRuleDTO distanceRuleDTO, String planType) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String distanceRule = objectMapper.writeValueAsString(distanceRuleDTO);
            String distanceRuleTrim = distanceRule.trim();
            Long count = projectWarnSchemeMapper.selectCount(new LambdaQueryWrapper<ProjectWarnSchemeEntity>()
                    .eq(ProjectWarnSchemeEntity::getDistanceRule, distanceRuleTrim)
                    .eq(ProjectWarnSchemeEntity::getPlanType, planType)
                    .eq(ProjectWarnSchemeEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (count > 0) {
                throw new RuntimeException("已存在相同距离规则的预警方案!!");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取预警类型
     */
    private String getWarnType(String distanceRule, String workloadRule) {
        String warnType = "";
        if (!distanceRule.trim().equals("null") && !workloadRule.trim().equals("null")) {
            warnType = ConstantsInfo.WORKLOAD_DISTANCE;
        }
        if (distanceRule.trim().equals("null") && !workloadRule.trim().equals("null")) {
            warnType = ConstantsInfo.WORKLOAD;
        }
        if (!distanceRule.trim().equals("null") && workloadRule.trim().equals("null")) {
            warnType = ConstantsInfo.DISTANCE;
        }
        return warnType;
    }
}