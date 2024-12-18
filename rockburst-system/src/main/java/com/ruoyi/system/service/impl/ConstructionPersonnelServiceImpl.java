package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.common.enums.ProfessionEnums;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.Entity.ConstructionPersonnelEntity;
import com.ruoyi.system.domain.Entity.ConstructionUnitEntity;
import com.ruoyi.system.domain.dto.ConstructPersonnelDTO;
import com.ruoyi.system.domain.dto.PersonnelChoiceListDTO;
import com.ruoyi.system.domain.dto.PersonnelSelectDTO;
import com.ruoyi.system.domain.vo.ConstructPersonnelVO;
import com.ruoyi.system.mapper.ConstructionPersonnelMapper;
import com.ruoyi.system.mapper.ConstructionUnitMapper;
import com.ruoyi.system.service.ConstructionPersonnelService;
import com.ruoyi.system.service.ConstructionUnitService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: shikai
 * @date: 2024/11/15
 * @description:
 */
@Service
@Transactional
public class ConstructionPersonnelServiceImpl extends ServiceImpl<ConstructionPersonnelMapper, ConstructionPersonnelEntity> implements ConstructionPersonnelService {

    @Resource
    private ConstructionPersonnelMapper constructionPersonnelMapper;

    @Resource
    private ConstructionUnitMapper constructionUnitMapper;

    /**
     * 新增施工人员
     * @param constructPersonnelDTO 参数DTO
     * @return 返回结果
     */
    @Override
    public ConstructPersonnelDTO insertConstructionPersonnel(ConstructPersonnelDTO constructPersonnelDTO) {
        LambdaQueryWrapper<ConstructionPersonnelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConstructionPersonnelEntity::getName,constructPersonnelDTO.getName());
        Long selectCount = constructionPersonnelMapper.selectCount(queryWrapper);
        if (selectCount > 0) {
            throw new RuntimeException("施工人员名称不能重复！");
        }
        ConstructionPersonnelEntity constructionPersonnelEntity = new ConstructionPersonnelEntity();
        BeanUtils.copyProperties(constructPersonnelDTO,constructionPersonnelEntity);
        long ts = System.currentTimeMillis();
        constructionPersonnelEntity.setCreateTime(ts);
        constructionPersonnelEntity.setUpdateTime(ts);
        // TODO: 2024/11/11 系统暂时去掉token,最后统一做鉴权；userId会从token取
        constructionPersonnelEntity.setCreateBy(1L);
        constructionPersonnelEntity.setUpdateBy(1L);
        constructionPersonnelEntity.setDelFlag(ConstantsInfo.ZERO_DEL_FLAG);
        constructionPersonnelMapper.insert(constructionPersonnelEntity);
        BeanUtils.copyProperties(constructionPersonnelEntity,constructPersonnelDTO);
        return constructPersonnelDTO;
    }

    /**
     * 施工人员编辑
     * @param constructPersonnelDTO 参数DTO
     * @return 返回结果
     *
     */
    @Override
    public ConstructPersonnelDTO updateConstructionPersonnel(ConstructPersonnelDTO constructPersonnelDTO) {
        if (ObjectUtil.isEmpty(constructPersonnelDTO.getConstructionPersonnelId())) {
            throw new RuntimeException("施工人员id不能为空！");
        }
        ConstructionPersonnelEntity constructionPersonnelEntity = constructionPersonnelMapper.selectOne(
                new LambdaQueryWrapper<ConstructionPersonnelEntity>()
                        .eq(ConstructionPersonnelEntity::getConstructionPersonnelId,constructPersonnelDTO.getConstructionPersonnelId())
                        .eq(ConstructionPersonnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isEmpty(constructionPersonnelEntity)) {
            throw new RuntimeException("施工人员不存在！");
        }
        LambdaQueryWrapper<ConstructionPersonnelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConstructionPersonnelEntity::getName,constructPersonnelDTO.getName())
                .ne(ConstructionPersonnelEntity::getConstructionPersonnelId,constructPersonnelDTO.getConstructionPersonnelId());
        Long selectCount = constructionPersonnelMapper.selectCount(queryWrapper);
        if (selectCount > 0) {
            throw new RuntimeException("施工人员名称不能重复！");
        }
        Long constructionPersonnelId = constructionPersonnelEntity.getConstructionPersonnelId();
        BeanUtils.copyProperties(constructPersonnelDTO,constructionPersonnelEntity);
        constructionPersonnelEntity.setConstructionPersonnelId(constructionPersonnelId);
        constructionPersonnelEntity.setUpdateTime(System.currentTimeMillis());
        constructionPersonnelEntity.setUpdateBy(1L);
        constructionPersonnelMapper.updateById(constructionPersonnelEntity);
        BeanUtils.copyProperties(constructionPersonnelEntity,constructPersonnelDTO);
        return constructPersonnelDTO;
    }

    /**
     * 批量删除
     * @param constructionPersonnelIds 施工人员id数组
     * @return 返回结果
     */
    @Override
    public boolean deleteConstructionPersonnel(Long[] constructionPersonnelIds) {
        boolean flag = false;
        if (constructionPersonnelIds.length == 0) {
            throw new RuntimeException("请选择要删除的数据!");
        }
        List<Long> ids = Arrays.asList(constructionPersonnelIds);
        flag = this.removeBatchByIds(ids);
        return flag;
    }

    /**
     * 根据id查询
     * @param constructionPersonnelId 施工人员id
     * @return 返回结果
     */
    @Override
    public ConstructPersonnelDTO getConstructionPersonnelById(Long constructionPersonnelId) {
        ConstructionPersonnelEntity constructionPersonnelEntity = constructionPersonnelMapper.selectOne(new LambdaQueryWrapper<ConstructionPersonnelEntity>()
                .eq(ConstructionPersonnelEntity::getConstructionPersonnelId,constructionPersonnelId)
                .eq(ConstructionPersonnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNotEmpty(constructionPersonnelEntity)) {
            ConstructPersonnelDTO constructPersonnelDTO = new ConstructPersonnelDTO();
            BeanUtils.copyProperties(constructionPersonnelEntity,constructPersonnelDTO);
            return constructPersonnelDTO;
        } else {
            throw new RuntimeException("施工人员不存在！");
        }
    }

    /**
     * 分页查询
     * @param personnelSelectDTO 查询参数DTO
     * @param pageNum 当前页
     * @param pageSize 每页显示条数
     * @return 返回结果
     */
    @Override
    public TableData pageQueryList(PersonnelSelectDTO personnelSelectDTO, Integer pageNum, Integer pageSize) {        TableData result = new TableData();
        if (null == pageNum || pageNum < 1) {
            pageNum = 1;
        }
        if (null == pageSize || pageSize < 1) {
            pageSize = 10;
        }
        PageHelper.startPage(pageNum, pageSize);
        Page<ConstructPersonnelVO> page = constructionPersonnelMapper.selectConstructionPersonnelByPage(personnelSelectDTO);
        if (ListUtils.isNotNull(page.getResult())) {
            page.getResult().forEach(constructPersonnelVO -> {
                ConstructionUnitEntity constructionUnitEntity = constructionUnitMapper.selectOne(
                        new LambdaQueryWrapper<ConstructionUnitEntity>()
                                .eq(ConstructionUnitEntity::getConstructionUnitId,constructPersonnelVO.getConstructionUnitId())
                                .eq(ConstructionUnitEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
                if (ObjectUtil.isNotEmpty(constructionUnitEntity)) {
                    constructPersonnelVO.setConstructionUnitFmt(constructionUnitEntity.getConstructionUnitName());
                }
                constructPersonnelVO.setProfessionFmt(ProfessionEnums.getInfo(constructPersonnelVO.getProfession()));
                constructPersonnelVO.setCreateTimeFmt(DateUtils.getDateStrByTime(constructPersonnelVO.getCreateTime()));
                constructPersonnelVO.setUpdateTimeFmt(DateUtils.getDateStrByTime(constructPersonnelVO.getUpdateTime()));
            });
        }
        result.setTotal(page.getTotal());
        result.setRows(page.getResult());
        return result;
    }

    /**
     * 获取施工人员下拉列表
     * @param constructionUnitId 单位id
     * @param profession 工种标识
     * @return 返回结果
     */
    @Override
    public List<PersonnelChoiceListDTO> getPersonnelChoiceList(Long constructionUnitId, String profession) {
        if (ObjectUtil.isEmpty(constructionUnitId)) {
            throw new RuntimeException("施工单位id不能为空！");
        }
        List<PersonnelChoiceListDTO> personnelChoiceListDTOS = new ArrayList<>();
        if (profession.isEmpty()) {
            try {
                List<ConstructionPersonnelEntity> constructionPersonnelEntities = constructionPersonnelMapper.selectList(new LambdaQueryWrapper<ConstructionPersonnelEntity>()
                        .eq(ConstructionPersonnelEntity::getConstructionUnitId, constructionUnitId)
                        .ne(ConstructionPersonnelEntity::getDelFlag, ConstantsInfo.TWO_DEL_FLAG));
                if (ListUtils.isNotNull(constructionPersonnelEntities)) {
                    personnelChoiceListDTOS = constructionPersonnelEntities.stream().map(this::convertToDTO).collect(Collectors.toList());
                }
            } catch (Exception e){
                e.printStackTrace();
            }

        } else {
            try {
                personnelChoiceListDTOS = constructionPersonnelMapper.selectChoiceList(constructionUnitId, profession);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return personnelChoiceListDTOS;
    }

    private PersonnelChoiceListDTO convertToDTO(ConstructionPersonnelEntity entity) {
        PersonnelChoiceListDTO dto = new PersonnelChoiceListDTO();
        dto.setLabel(entity.getName());
        dto.setValue(entity.getConstructionPersonnelId());
        return dto;
    }
}