package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.Entity.ConstructionPersonnelEntity;
import com.ruoyi.system.domain.Entity.ConstructionUnitEntity;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.vo.ConstructionUnitVO;
import com.ruoyi.system.mapper.ConstructionPersonnelMapper;
import com.ruoyi.system.mapper.ConstructionUnitMapper;
import com.ruoyi.system.service.ConstructionUnitService;
import com.ruoyi.system.service.ISysDictDataService;
import com.ruoyi.system.service.ISysDictTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author: shikai
 * @date: 2024/11/15
 * @description:
 */
@Service
@Transactional
public class ConstructionUnitServiceImpl extends ServiceImpl<ConstructionUnitMapper, ConstructionUnitEntity> implements ConstructionUnitService {

    @Resource
    private ConstructionUnitMapper constructionUnitMapper;

    @Resource
    private ISysDictTypeService sysDictTypeService;

    @Resource
    private ISysDictDataService sysDictDataService;

    @Resource
    private ConstructionPersonnelMapper constructionPersonnelMapper;

    /**
     * 新增施工单位
     * @param constructionUnitDTO 参数DTO
     * @return 返回结果
     */
    @Override
    public ConstructionUnitDTO insertConstructionUnit(ConstructionUnitDTO constructionUnitDTO) {
        LambdaQueryWrapper<ConstructionUnitEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConstructionUnitEntity::getConstructionUnitName,constructionUnitDTO.getConstructionUnitName());
        Long selectCount = constructionUnitMapper.selectCount(queryWrapper);
        if (selectCount > 0) {
            throw new RuntimeException("施工单位名称不能重复！");
        }
        ConstructionUnitEntity constructionUnitEntity = new ConstructionUnitEntity();
        BeanUtils.copyProperties(constructionUnitDTO,constructionUnitEntity);
        long ts = System.currentTimeMillis();
        constructionUnitEntity.setCreateTime(ts);
        constructionUnitEntity.setUpdateTime(ts);
        // TODO: 2024/11/11 系统暂时去掉token,最后统一做鉴权；userId会从token取
        constructionUnitEntity.setCreateBy(1L);
        constructionUnitEntity.setUpdateBy(1L);
        constructionUnitMapper.insert(constructionUnitEntity);
        BeanUtils.copyProperties(constructionUnitEntity,constructionUnitDTO);
        return constructionUnitDTO;
    }

    /**
     * 修改施工单位
     * @param constructionUnitDTO 参数DTO
     * @return 返回结果
     */
    @Override
    public ConstructionUnitDTO updateConstructionUnit(ConstructionUnitDTO constructionUnitDTO) {
        if (ObjectUtil.isEmpty(constructionUnitDTO.getConstructionUnitId())) {
            throw new RuntimeException("施工单位id不能为空！");
        }
        ConstructionUnitEntity constructionUnitEntity = constructionUnitMapper.selectById(constructionUnitDTO.getConstructionUnitId());
        if (ObjectUtil.isEmpty(constructionUnitEntity)) {
            throw new RuntimeException("施工单位不存在！");
        }
        LambdaQueryWrapper<ConstructionUnitEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConstructionUnitEntity::getConstructionUnitName,constructionUnitDTO.getConstructionUnitName())
                .ne(ConstructionUnitEntity::getConstructionUnitId,constructionUnitDTO.getConstructionUnitId());
        Long selectCount = constructionUnitMapper.selectCount(queryWrapper);
        if (selectCount > 0) {
            throw new RuntimeException("施工单位名称不能重复！");
        }
        Long constructionUnitId = constructionUnitEntity.getConstructionUnitId();
        BeanUtils.copyProperties(constructionUnitDTO,constructionUnitEntity);
        constructionUnitEntity.setConstructionUnitId(constructionUnitId);
        constructionUnitEntity.setUpdateTime(System.currentTimeMillis());
        constructionUnitEntity.setUpdateBy(1L);
        constructionUnitMapper.updateById(constructionUnitEntity);
        BeanUtils.copyProperties(constructionUnitEntity,constructionUnitDTO);
        return constructionUnitDTO;
    }

    /**
     * 根据id查询施工单位
     * @param constructionUnitId id
     * @return 返回结果
     */
    @Override
    public ConstructionUnitDTO getConstructionUnitById(Long constructionUnitId) {
        ConstructionUnitEntity constructionUnitEntity = constructionUnitMapper.selectById(constructionUnitId);
        if (ObjectUtil.isNotEmpty(constructionUnitEntity)) {
            ConstructionUnitDTO constructionUnitDTO = new ConstructionUnitDTO();
            BeanUtils.copyProperties(constructionUnitEntity,constructionUnitDTO);
            return constructionUnitDTO;
        } else {
            throw new RuntimeException("施工单位不存在！");
        }
    }
    /**
     * 分页查询
     * @param constructUnitSelectDTO 查询参数DTO
     * @param pageNum 页数
     * @param pageSize 条数
     * @return 返回结果
     */

    @Override
    public TableData pageQueryList(ConstructUnitSelectDTO constructUnitSelectDTO, Integer pageNum, Integer pageSize) {
        TableData result = new TableData();
        if (null == pageNum || pageNum < 1) {
            pageNum = 1;
        }
        if (null == pageSize || pageSize < 1) {
            pageSize = 10;
        }
        PageHelper.startPage(pageNum, pageSize);
        Page<ConstructionUnitVO> page = constructionUnitMapper.selectConstructionUnitByPage(constructUnitSelectDTO);
        if (ListUtils.isNotNull(page.getResult())) {
            page.getResult().forEach(constructionUnitVO -> {
                constructionUnitVO.setCreateTimeFmt(DateUtils.getDateStrByTime(constructionUnitVO.getCreateTime()));
                constructionUnitVO.setUpdateTimeFmt(DateUtils.getDateStrByTime(constructionUnitVO.getUpdateTime()));
            });
        }
        result.setTotal(page.getTotal());
        result.setRows(page.getResult());
        return result;
    }

    /**
     * 批量删除
     * @param constructionUnitIds 施工单位id数组
     * @return 返回结果
     */
    @Override
    public boolean deleteConstructionUnit(Long[] constructionUnitIds) {
        boolean flag = false;
        if (constructionUnitIds.length == 0) {
            throw new ServiceException("请选择要删除的数据!");
        }
        List<Long> ids = Arrays.asList(constructionUnitIds);
        flag = this.removeBatchByIds(ids);
        return flag;
    }

    /**
     * 获取施工单位下拉列表
     * @return 返回结果
     */
    @Override
    public List<UnitChoiceListDTO> getUnitChoiceList() {
        ArrayList<UnitChoiceListDTO> unitChoiceListDTOS = new ArrayList<>();
        List<ConstructionUnitEntity> constructionUnitEntities = constructionUnitMapper.selectList(new LambdaQueryWrapper<ConstructionUnitEntity>()
                .ne(ConstructionUnitEntity::getDelFlag, ConstantsInfo.TWO_DEL_FLAG));
        if (ListUtils.isNotNull(constructionUnitEntities)) {
            constructionUnitEntities.forEach(constructionUnitEntity -> {
                UnitChoiceListDTO unitChoiceListDTO = new UnitChoiceListDTO();
                unitChoiceListDTO.setLabel(constructionUnitEntity.getConstructionUnitName());
                unitChoiceListDTO.setValue(constructionUnitEntity.getConstructionUnitId());
                unitChoiceListDTOS.add(unitChoiceListDTO);
            });
        }
        return unitChoiceListDTOS;
    }

    @Override
    public List<UnitDataDTO> getUnitDataListForApp() {
        try {
            // 获取工种列表
            List<String> profession = sysDictTypeService.selectDictDataByType(ConstantsInfo.PROFESSION_DICT_TYPE)
                    .stream()
                    .map(SysDictData::getDictValue)
                    .collect(Collectors.toList());

            if (profession.isEmpty()) {
                return Collections.emptyList();
            }
            // 获取未删除的施工单位列表
            List<ConstructionUnitEntity> constructionUnitEntities = constructionUnitMapper.selectList(
                    new LambdaQueryWrapper<ConstructionUnitEntity>()
                            .eq(ConstructionUnitEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));

            if (ObjectUtil.isEmpty(constructionUnitEntities)) {
                return Collections.emptyList();
            }
            // 构建单位数据DTO列表
            List<UnitDataDTO> unitDataDTOS = new ArrayList<>();
            Map<String, String> professionMap = profession.stream()
                    .collect(Collectors.toMap(
                            Function.identity(),
                            professionStr -> sysDictDataService.selectDictLabel(ConstantsInfo.PROFESSION_DICT_TYPE, professionStr)));
            for (ConstructionUnitEntity constructionUnitEntity : constructionUnitEntities) {
                UnitDataDTO unitDataDTO = new UnitDataDTO();
                unitDataDTO.setKey(constructionUnitEntity.getConstructionUnitName());
                unitDataDTO.setValue(constructionUnitEntity.getConstructionUnitId());
                List<ProfessionDTO> professionDTOS = new ArrayList<>();
                for (String professionStr : profession) {
                    ProfessionDTO professionDTO = new ProfessionDTO();
                    professionDTO.setProfessionName(professionMap.get(professionStr));
                    professionDTO.setProfessionValue(professionStr);
                    List<StaffDTO> staffDTOS = getStaffDTOs(constructionUnitEntity.getConstructionUnitId(), professionStr);
                    professionDTO.setStaffDTOS(staffDTOS);
                    professionDTOS.add(professionDTO);
                }
                unitDataDTO.setProfessionDTOS(professionDTOS);
                unitDataDTOS.add(unitDataDTO);
            }
            return unitDataDTOS;
        } catch (Exception e) {
            // 记录日志并返回空列表
            log.error("Error occurred while fetching unit data list for app", e);
            return Collections.emptyList();
        }
    }

    private List<StaffDTO> getStaffDTOs(Long constructionUnitId, String professionStr) {
        List<ConstructionPersonnelEntity> constructionPersonnelEntities = constructionPersonnelMapper.selectList(
                new LambdaQueryWrapper<ConstructionPersonnelEntity>()
                        .eq(ConstructionPersonnelEntity::getConstructionUnitId, constructionUnitId)
                        .eq(ConstructionPersonnelEntity::getProfession, professionStr)
                        .eq(ConstructionPersonnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));

        if (ObjectUtil.isEmpty(constructionPersonnelEntities)) {
            return Collections.emptyList();
        }
        List<StaffDTO> staffDTOS = new ArrayList<>();
        for (ConstructionPersonnelEntity constructionPersonnelEntity : constructionPersonnelEntities) {
            StaffDTO staffDTO = new StaffDTO();
            staffDTO.setStaffName(constructionPersonnelEntity.getName());
            staffDTO.setStaffId(constructionPersonnelEntity.getConstructionPersonnelId());
            staffDTOS.add(staffDTO);
        }
        return staffDTOS;
    }
}