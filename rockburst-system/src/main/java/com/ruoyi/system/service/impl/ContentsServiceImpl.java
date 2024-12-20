package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.system.domain.Entity.ContentsEntity;
import com.ruoyi.system.domain.Entity.PlanContentsMappingEntity;
import com.ruoyi.system.domain.dto.ContentsTreeDTO;
import com.ruoyi.system.mapper.ContentsMapper;
import com.ruoyi.system.mapper.PlanContentsMappingMapper;
import com.ruoyi.system.service.ContentsService;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: shikai
 * @date: 2024/12/8
 * @description:
 */

@Service
@Transactional
public class ContentsServiceImpl extends ServiceImpl<ContentsMapper, ContentsEntity> implements ContentsService {

    @Resource
    private ContentsMapper contentsMapper;

    @Resource
    private PlanContentsMappingMapper planContentsMappingMapper;

    @Override
    public int addContents(ContentsEntity contentsEntity) {
        int flag = 0;
        if (ObjectUtil.isNull(contentsEntity)) {
            throw new RuntimeException("参数不能为空!");
        }
        if (ObjectUtil.isNull(contentsEntity.getContentsName())) {
            throw new RuntimeException("目录名称不能为空!");
        }
        List<PlanContentsMappingEntity> planContentsMappingEntities = planContentsMappingMapper.selectList(null);
        if (ListUtils.isNotNull(planContentsMappingEntities)) {
            if (ObjectUtil.isNull(contentsEntity.getSuperId())) {
                throw new RuntimeException("上级层级不能为空!");
            }
        }
        Long selectCount = contentsMapper.selectCount(new LambdaQueryWrapper<ContentsEntity>()
                .eq(ContentsEntity::getContentsName, contentsEntity.getContentsName())
                .eq(ContentsEntity::getSuperId, contentsEntity.getSuperId()));
        if (selectCount > 0) {
            throw new RuntimeException("目录名称不能重复!");
        }
        flag = contentsMapper.insert(contentsEntity);
        if (flag < 1) {
            throw new RuntimeException("添加目录失败!");
        }
        return flag;
    }

    @Override
    public int updateContents(ContentsEntity contentsEntity) {
        int flag = 0;
        if (ObjectUtil.isNull(contentsEntity)) {
            throw new RuntimeException("参数不能为空!");
        }
        if (ObjectUtil.isNull(contentsEntity.getContentsId())) {
            throw new RuntimeException("目录id不能为空!");
        }
        if (ObjectUtil.isNull(contentsEntity.getContentsName())) {
            throw new RuntimeException("目录名称不能为空!");
        }
        ContentsEntity selectContents = contentsMapper.selectById(contentsEntity.getContentsId());
        if (ObjectUtil.isNull(selectContents)) {
            throw new RuntimeException("未找到此目录!");
        }
        if (!selectContents.getSuperId().equals(contentsEntity.getSuperId())) {
            throw new RuntimeException("父级目录不允许修改!");
        }
        Long selectCount = contentsMapper.selectCount(new LambdaQueryWrapper<ContentsEntity>()
                .eq(ContentsEntity::getContentsName, contentsEntity.getContentsName())
                .eq(ContentsEntity::getSuperId, contentsEntity.getSuperId())
                .ne(ContentsEntity::getContentsId, contentsEntity.getContentsId()));

        if (selectCount > 0) {
            throw new RuntimeException("目录名称不能重复!");
        }
        flag = contentsMapper.updateById(contentsEntity);
        if (flag < 1) {
            throw new RuntimeException("修改目录失败!");
        }
        return flag;
    }

    @Override
    public boolean deleteById(Long[] contentsIds) {
        boolean flag = false;
        if (contentsIds.length == 0) {
            throw new RuntimeException("请选择要删除的数据!");
        }
        List<Long> ids = Arrays.asList(contentsIds);
        Long selectCount = contentsMapper.selectCount(new LambdaQueryWrapper<ContentsEntity>()
                .in(ContentsEntity::getSuperId, ids));
        if (selectCount > 0) {
            throw new RuntimeException("当前层级下有子目录无法进行删除，请先删除子目录!");
        }
        List<PlanContentsMappingEntity> planContentsMappingEntities = planContentsMappingMapper.selectList(new LambdaQueryWrapper<PlanContentsMappingEntity>()
                .in(PlanContentsMappingEntity::getContentsId, ids));
        List<Long> planIdList = new ArrayList<Long>();
        if (ListUtils.isNotNull(planContentsMappingEntities)) {
            planContentsMappingEntities.forEach(planContentsMappingEntity -> {
                planIdList.add(planContentsMappingEntity.getPlanId());
            });
        }
        if (ListUtils.isNotNull(planIdList) || planIdList.size() > 0) {
            throw new RuntimeException("当前目录下有计划无法进行删除，请先删除计划!");
        }
        flag = this.removeBatchByIds(ids);
        return flag;
    }

    @Override
    public List<Long> queryByCondition(Long contentsId) {
        List<Long> ids = contentsMapper.findAllByIdRecursive(contentsId)
                .stream().map(ContentsEntity::getContentsId).collect(Collectors.toList());
        if (ids.isEmpty()) {
           throw new RuntimeException("层级id错误！请联系管理员！");
        }
        List<PlanContentsMappingEntity> planContentsMappingEntities = planContentsMappingMapper.selectList(new LambdaQueryWrapper<PlanContentsMappingEntity>()
                .in(PlanContentsMappingEntity::getContentsId, ids));
        List<Long> planIdList = new ArrayList<Long>();
        if (ListUtils.isNotNull(planContentsMappingEntities) && planContentsMappingEntities.size() > 0) {
            planContentsMappingEntities.forEach(planContentsMappingEntity -> {
                planIdList.add(planContentsMappingEntity.getPlanId());
            });
        }
        return planIdList;
    }

    /**
     * 获取所有目录树形结构
     */
    @Override
    public List<ContentsTreeDTO> queryAllContentsTree() {
        List<ContentsEntity> contents = contentsMapper.findAllContents();
        List<ContentsTreeDTO> contentsTreeDTOS = new ArrayList<>();
        if (ListUtils.isNotNull(contents)) {
            contents.stream().filter(contentsEntity -> contentsEntity.getSuperId() == null)
                    .peek(contentsEntity -> contentsTreeDTOS.add(new ContentsTreeDTO(contentsEntity.getLabel(),
                            String.valueOf(contentsEntity.getValue()), contentsEntity.isDisable(),
                            ContentsTreeDTO.treeRecursive(contentsEntity.getValue(), contents))))
                    .collect(Collectors.toList());
        }
        Set<ContentsTreeDTO> treeDTOSet = new HashSet<>(contentsTreeDTOS);
        TreeSet<ContentsTreeDTO> sortSet = new TreeSet<>(new Comparator<ContentsTreeDTO>() {
            @Override
            public int compare(ContentsTreeDTO o1, ContentsTreeDTO o2) {
                return Integer.parseInt(o2.getValue()) - Integer.parseInt(o1.getValue());
            }
        });
        sortSet.addAll(treeDTOSet);
        return new ArrayList<ContentsTreeDTO>(sortSet);
    }

    /**
     * 获取子目录树形结构（包含自己）
     * @return 返回结果
     */
    @Override
    public List<ContentsTreeDTO> getContentsTree(Long contentsId, String contentsName) {
        List<ContentsEntity> contentsEntities = contentsMapper.findAllByContentsIdRecursive(contentsId);
        List<ContentsTreeDTO> contentsTreeDTOS = new ArrayList<>();
        contentsEntities.stream().filter(contentsEntity -> contentsEntity.getContentsId().equals(contentsId))
                .peek(contentsEntity -> contentsTreeDTOS.add(new ContentsTreeDTO(contentsEntity.getLabel(),
                        String.valueOf(contentsEntity.getValue()), contentsEntity.isDisable(),
                        ContentsTreeDTO.treeRecursive(contentsEntity.getValue(), contentsEntities))))
                .collect(Collectors.toList());
        if (contentsName != null && contentsName.isEmpty()) {
            treeMatch(contentsTreeDTOS, contentsName);
        }
        return contentsTreeDTOS;
    }

    /**
     * 条件筛选
     */
    private List<ContentsTreeDTO> treeMatch(List<ContentsTreeDTO> contentsTreeDTOS, String contentsName) {
        Iterator<ContentsTreeDTO> iterator = contentsTreeDTOS.iterator();
        while (iterator.hasNext()) {
            ContentsTreeDTO contentsTreeDTO = iterator.next();
            if (!contentsTreeDTO.getLabel().contains(contentsName)) { //不包含
                if (!CollectionUtils.isEmpty(contentsTreeDTO.getChildren())) { //子不为空
                    contentsTreeDTO.setChildren(treeMatch(contentsTreeDTO.getChildren(), contentsName));
                    if (CollectionUtils.isEmpty(contentsTreeDTO.getChildren())) {
                        iterator.remove();
                    }
                } else {
                    iterator.remove();
                }
            }
        }
        return contentsTreeDTOS;
    }
}