package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.common.utils.*;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.DrillingStressEntity;
import com.ruoyi.system.domain.Entity.SurveyAreaEntity;
import com.ruoyi.system.domain.Entity.WarnSchemeSeparateEntity;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.utils.ConvertUtils;
import com.ruoyi.system.domain.utils.NumberGeneratorUtils;
import com.ruoyi.system.domain.utils.ObtainWarnSchemeUtils;
import com.ruoyi.system.domain.vo.DrillingStressVO;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.DrillingStressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author: shikai
 * @date: 2024/12/3
 * @description:
 */

@Transactional
@Service
public class DrillingStressServiceImpl extends ServiceImpl<DrillingStressMapper, DrillingStressEntity> implements DrillingStressService {

    @Resource
    private DrillingStressMapper drillingStressMapper;

    @Resource
    private BizWorkfaceMapper bizWorkfaceMapper;

    @Resource
    private SurveyAreaMapper surveyAreaMapper;

    @Resource
    private WarnSchemeSeparateMapper warnSchemeSeparateMapper;

    @Resource
    private WarnSchemeMapper warnSchemeMapper;

    /**
     * 新增测点
     * @param drillingStressDTO 参数DTO
     * @return 返回结果
     */
    @Override
    public int addMeasure(DrillingStressDTO drillingStressDTO, Long mineId) {
        int flag = 0;
        if (ObjectUtil.isNull(drillingStressDTO)) {
            throw new RuntimeException("参数错误,不能为空");
        }
        DrillingStressEntity drillingStressEntity = new DrillingStressEntity();
        BeanUtils.copyProperties(drillingStressDTO, drillingStressEntity);
        String maxMeasureNum = drillingStressMapper.selectMaxMeasureNum(mineId);
        if (maxMeasureNum.equals("0")) {
            drillingStressEntity.setMeasureNum(ConstantsInfo.Drill_Stress_INITIAL_VALUE);
        } else {
            String nextValue = NumberGeneratorUtils.getNextValue(maxMeasureNum);
            drillingStressEntity.setMeasureNum(nextValue);
        }
        drillingStressEntity.setCreateTime(System.currentTimeMillis());
        drillingStressEntity.setCreateBy(SecurityUtils.getUserId());
        drillingStressEntity.setMineId(mineId);
        drillingStressEntity.setTag(ConstantsInfo.MANUALLY_ADD);
        drillingStressEntity.setDelFlag(ConstantsInfo.ZERO_DEL_FLAG);
        flag = drillingStressMapper.insert(drillingStressEntity);
        if (flag <= 0) {
            throw new RuntimeException("测点添加失败,请联系管理员");
        }
        return flag;
    }

    /**
     * 测点编辑
     * @param drillingStressDTO 参数DTO
     * @return 返回结果
     */
    @Override
    public int updateMeasure(DrillingStressDTO drillingStressDTO) {
        int flag = 0;
        if (ObjectUtil.isNull(drillingStressDTO)) {
            throw new RuntimeException("参数错误,不能为空");
        }
        DrillingStressEntity drillingStressEntity = drillingStressMapper.selectOne(new LambdaQueryWrapper<DrillingStressEntity>()
                .eq(DrillingStressEntity::getDrillingStressId, drillingStressDTO.getDrillingStressId())
                .eq(DrillingStressEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(drillingStressEntity)) {
            throw new RuntimeException("该测点不存在");
        }
        Long drillingStressId = drillingStressEntity.getDrillingStressId();
        BeanUtils.copyProperties(drillingStressDTO, drillingStressEntity);
        if (ObjectUtil.isNull(drillingStressDTO.getMeasureNum())) {
            throw new RuntimeException("测点编码不允许为空");
        }
        if (!drillingStressDTO.getMeasureNum().equals(drillingStressEntity.getMeasureNum())) {
            throw new RuntimeException("测点编码不允许修改");
        }
        drillingStressEntity.setDrillingStressId(drillingStressId);
        drillingStressEntity.setUpdateTime(System.currentTimeMillis());
        drillingStressEntity.setUpdateBy(SecurityUtils.getUserId());
        flag = drillingStressMapper.updateById(drillingStressEntity);
        if (flag > 0) {
            if (ObjectUtil.isNotNull(drillingStressDTO.getWarnSchemeDTO())) {
                int update = updateAloneWarnScheme(drillingStressDTO.getMeasureNum(), drillingStressDTO.getWarnSchemeDTO());
                if (update <= 0) {
                    throw new RuntimeException("预警方案修改失败,请联系管理员");
                }
            }
        } else {
            throw new RuntimeException("测点编辑失败,请联系管理员");
        }
        return flag;
    }

    /**
     * 根据Id查询
     * @param drillingStressId 测点id
     * @return 返回结果
     */
    @Override
    public DrillingStressDTO detail(Long drillingStressId) {
        if (ObjectUtil.isNull(drillingStressId)) {
            throw new RuntimeException("参数错误,id不能为空");
        }
        DrillingStressEntity drillingStressEntity = drillingStressMapper.selectOne(new LambdaQueryWrapper<DrillingStressEntity>()
                .eq(DrillingStressEntity::getDrillingStressId, drillingStressId)
                .eq(DrillingStressEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(drillingStressEntity)) {
            throw new RuntimeException("该测点不存在");
        }
        DrillingStressDTO drillingStressDTO = new DrillingStressDTO();
        BeanUtils.copyProperties(drillingStressEntity, drillingStressDTO);
        WarnSchemeDTO warnSchemeDTO = ObtainWarnSchemeUtils.getObtainWarnScheme(drillingStressDTO.getMeasureNum(), drillingStressDTO.getSensorType(),
                drillingStressDTO.getWorkFaceId(), warnSchemeMapper, warnSchemeSeparateMapper);
        drillingStressDTO.setWarnSchemeDTO(warnSchemeDTO);
        return drillingStressDTO;
    }

    /**
     * 分页查询
     * @param measureSelectDTO 查询参数DTO
     * @param pageNum 当前页码
     * @param pageSize 条数
     * @return 返回结果
     */
    @Override
    public TableData pageQueryList(MeasureSelectDTO measureSelectDTO, Long mineId, Integer pageNum, Integer pageSize) {
        TableData result = new TableData();
        if (null == pageNum || pageNum < 1) {
            pageNum = 1;
        }
        if (null == pageSize || pageSize < 1) {
            pageSize = 10;
        }
        PageHelper.startPage(pageNum, pageSize);
        Page<DrillingStressVO> page = drillingStressMapper.selectQueryPage(measureSelectDTO, mineId);
        Page<DrillingStressVO> resistanceVOPage = getListFmt(page);
        result.setTotal(resistanceVOPage.getTotal());
        result.setRows(resistanceVOPage.getResult());
        return result;
    }

    /**
     * 删除/批量删除
     * @param drillingStressIds id数组
     * @return 返回结果
     */
    @Override
    public boolean deleteByIds(Long[] drillingStressIds) {
        boolean flag = false;
        if (drillingStressIds.length == 0) {
            throw new RuntimeException("请选择要删除的数据!");
        }
        List<Long> ids = Arrays.asList(drillingStressIds);
        ids.forEach(drillingStressId -> {
            DrillingStressEntity drillingStressEntity = drillingStressMapper.selectById(drillingStressId);
            if (ObjectUtil.isNull(drillingStressEntity)) {
                throw new RuntimeException("未找到id为" + drillingStressId + "的数据");
            }
            if (StringUtils.equals(ConstantsInfo.ENABLE, drillingStressEntity.getStatus())) {
                throw new RuntimeException("该测点已启用,不能删除");
            }
        });
        flag = this.removeBatchByIds(ids);
        return flag;
    }

    /**
     * (批量)启用/禁用
     * @param drillingStressIds id数组
     * @return 返回结果
     */
    @Override
    public int batchEnableDisable(Long[] drillingStressIds) {
        int flag = 0;
        if (drillingStressIds.length == 0) {
            throw new RuntimeException("请选择要禁用的数据!");
        }
        List<Long> ids = Arrays.asList(drillingStressIds);
        ids.forEach(drillingStressId -> {
            DrillingStressEntity drillingStressEntity = drillingStressMapper.selectOne(new LambdaQueryWrapper<DrillingStressEntity>()
                    .eq(DrillingStressEntity::getDrillingStressId, drillingStressId)
                    .eq(DrillingStressEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ObjectUtil.isNull(drillingStressEntity)) {
                throw new RuntimeException("未找到id为" + drillingStressId + "的数据");
            }
            if (StringUtils.equals(ConstantsInfo.ENABLE, drillingStressEntity.getStatus())) {
                drillingStressEntity.setStatus(ConstantsInfo.DISABLE);
            } else {
                drillingStressEntity.setStatus(ConstantsInfo.ENABLE);
            }
            drillingStressMapper.updateById(drillingStressEntity);
        });
        return flag;
    }

    /**
     * VO格式化
     */
    private Page<DrillingStressVO> getListFmt(Page<DrillingStressVO> list) {
        if (ListUtils.isNotNull(list.getResult())) {
            list.getResult().forEach(drillingStressVO -> {
                drillingStressVO.setWorkFaceName(getWorkFaceName(drillingStressVO.getWorkFaceId()));
                drillingStressVO.setInstallTimeFmt(DateUtils.getDateStrByTime(drillingStressVO.getInstallTime()));
            });
        }
        return list;
    }

    /**
     * 获取工作面名称
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

    private int updateAloneWarnScheme(String measureNum, WarnSchemeDTO warnSchemeDTO) {
        WarnSchemeSeparateEntity warnSchemeSeparateEntity = new WarnSchemeSeparateEntity();
        List<ThresholdConfigDTO> thresholdConfigDTOS = warnSchemeDTO.getThresholdConfigDTOS();
        List<IncrementConfigDTO> incrementConfigDTOS = warnSchemeDTO.getIncrementConfigDTOS();
        List<GrowthRateConfigDTO> growthRateConfigDTOS = warnSchemeDTO.getGrowthRateConfigDTOS();
        if (ObjectUtil.isNotNull(thresholdConfigDTOS) && !thresholdConfigDTOS.isEmpty()) {
            List<Map<String, Object>> thresholdMap = ConvertUtils.convertThresholdMap(thresholdConfigDTOS);
            warnSchemeSeparateEntity.setThresholdConfig(thresholdMap);
        }
        if (ObjectUtil.isNotNull(incrementConfigDTOS) && !incrementConfigDTOS.isEmpty()) {
            List<Map<String, Object>> incrementMap = ConvertUtils.convertIncrementMap(incrementConfigDTOS);
            warnSchemeSeparateEntity.setIncrementConfig(incrementMap);
        }
        if (ObjectUtil.isNotNull(growthRateConfigDTOS) && !growthRateConfigDTOS.isEmpty()) {
            List<Map<String, Object>> growthRateMap = ConvertUtils.convertGrowthRateMap(growthRateConfigDTOS);
            warnSchemeSeparateEntity.setGrowthRateConfig(growthRateMap);
        }
        warnSchemeSeparateEntity.setWarnSchemeId(warnSchemeDTO.getWarnSchemeId());
        warnSchemeSeparateEntity.setWorkFaceId(warnSchemeDTO.getWorkFaceId());
        warnSchemeSeparateEntity.setSceneType(warnSchemeDTO.getSceneType());
        warnSchemeSeparateEntity.setMeasureNum(measureNum);
        warnSchemeSeparateEntity.setDelFlag(ConstantsInfo.ZERO_DEL_FLAG);
        return warnSchemeSeparateMapper.insert(warnSchemeSeparateEntity);
    }
}