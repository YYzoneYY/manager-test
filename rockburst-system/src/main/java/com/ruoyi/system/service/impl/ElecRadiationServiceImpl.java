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
import com.ruoyi.system.domain.Entity.ElecRadiationEntity;
import com.ruoyi.system.domain.Entity.WarnSchemeSeparateEntity;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.utils.ConvertUtils;
import com.ruoyi.system.domain.utils.NumberGeneratorUtils;
import com.ruoyi.system.domain.utils.ObtainWarnSchemeUtils;
import com.ruoyi.system.domain.vo.ElecRadiationVO;
import com.ruoyi.system.mapper.BizWorkfaceMapper;
import com.ruoyi.system.mapper.ElecRadiationMapper;
import com.ruoyi.system.mapper.WarnSchemeMapper;
import com.ruoyi.system.mapper.WarnSchemeSeparateMapper;
import com.ruoyi.system.service.ElecRadiationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author: shikai
 * @date: 2025/8/16
 * @description:
 */

@Transactional
@Service
public class ElecRadiationServiceImpl extends ServiceImpl<ElecRadiationMapper, ElecRadiationEntity> implements ElecRadiationService {

    @Resource
    private ElecRadiationMapper elecRadiationMapper;

    @Resource
    private BizWorkfaceMapper bizWorkfaceMapper;

    @Resource
    private WarnSchemeSeparateMapper warnSchemeSeparateMapper;

    @Resource
    private WarnSchemeMapper warnSchemeMapper;

    @Override
    public int addMeasure(ElecRadiationDTO elecRadiationDTO, Long mineId) {
        int flag = 0;
        if (ObjectUtil.isNull(elecRadiationDTO)) {
            throw new RuntimeException("参数错误,不能为空");
        }
        ElecRadiationEntity elecRadiationEntity = new ElecRadiationEntity();
        BeanUtils.copyProperties(elecRadiationDTO, elecRadiationEntity);
        String maxMeasureNum = elecRadiationMapper.selectMaxMeasureNum(mineId);
        if (maxMeasureNum.equals("0")) {
            elecRadiationEntity.setMeasureNum(ConstantsInfo.ELECTROMAGNETIC_RADIATION_INITIAL_VALUE);
        } else {
            String nextValue = NumberGeneratorUtils.getNextValue(maxMeasureNum);
            elecRadiationEntity.setMeasureNum(nextValue);
        }
        elecRadiationEntity.setCreateTime(System.currentTimeMillis());
        elecRadiationEntity.setCreateBy(SecurityUtils.getUserId());
        elecRadiationEntity.setTag(ConstantsInfo.MANUALLY_ADD);
        elecRadiationEntity.setMineId(mineId);
        elecRadiationEntity.setDelFlag(ConstantsInfo.ZERO_DEL_FLAG);
        flag = elecRadiationMapper.insert(elecRadiationEntity);
        if (flag <= 0) {
            throw new RuntimeException("测点新增失败,请联系管理员");
        }
        return flag;
    }

    @Override
    public int updateMeasure(ElecRadiationDTO elecRadiationDTO) {
        int flag = 0;
        if (ObjectUtil.isNull(elecRadiationDTO)) {
            throw new RuntimeException("参数错误,不能为空");
        }
        ElecRadiationEntity elecRadiationEntity = elecRadiationMapper.selectOne(new LambdaQueryWrapper<ElecRadiationEntity>()
                .eq(ElecRadiationEntity::getRadiationId, elecRadiationDTO.getRadiationId())
                .eq(ElecRadiationEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(elecRadiationEntity)) {
            throw new RuntimeException("测点不存在");
        }
        Long radiationId = elecRadiationEntity.getRadiationId();
        BeanUtils.copyProperties(elecRadiationDTO, elecRadiationEntity);
        if (ObjectUtil.isNull(elecRadiationDTO.getMeasureNum())) {
            throw new RuntimeException("测点编码不允许为空");
        }
        if (!elecRadiationDTO.getMeasureNum().equals(elecRadiationEntity.getMeasureNum())) {
            throw new RuntimeException("测点编码不允许修改");
        }
        elecRadiationEntity.setRadiationId(radiationId);
        elecRadiationEntity.setUpdateTime(System.currentTimeMillis());
        elecRadiationEntity.setUpdateBy(SecurityUtils.getUserId());
        flag = elecRadiationMapper.updateById(elecRadiationEntity);
        if (flag <= 0) {
            if (ObjectUtil.isNotNull(elecRadiationDTO.getWarnSchemeDTO())) {
                int update = updateAloneWarnScheme(elecRadiationDTO.getMeasureNum(), elecRadiationDTO.getWarnSchemeDTO());
                if (update <= 0) {
                    throw new RuntimeException("预警方案修改失败,请联系管理员");
                }
            }
        } else {
            throw new RuntimeException("测点修改失败,请联系管理员");
        }
        return flag;
    }

    @Override
    public ElecRadiationDTO detail(Long radiationId) {
        if (ObjectUtil.isNull(radiationId)) {
            throw new RuntimeException("参数错误,id不能为空");
        }
        ElecRadiationEntity elecRadiationEntity = elecRadiationMapper.selectOne(new LambdaQueryWrapper<ElecRadiationEntity>()
                .eq(ElecRadiationEntity::getRadiationId, radiationId)
                .eq(ElecRadiationEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(elecRadiationEntity)) {
            throw new RuntimeException("未找到id为" + radiationId + "的测点数据");
        }
        ElecRadiationDTO elecRadiationDTO = new ElecRadiationDTO();
        BeanUtils.copyProperties(elecRadiationEntity, elecRadiationDTO);
        WarnSchemeDTO warnSchemeDTO = ObtainWarnSchemeUtils.getObtainWarnScheme(elecRadiationDTO.getMeasureNum(), elecRadiationDTO.getSensorType(),
                elecRadiationDTO.getWorkFaceId(), warnSchemeMapper, warnSchemeSeparateMapper);
        elecRadiationDTO.setWarnSchemeDTO(warnSchemeDTO);
        return elecRadiationDTO;
    }

    @Override
    public TableData pageQueryList(MeasureTSelectDTO measureTSelectDTO, Long mineId, Integer pageNum, Integer pageSize) {
        TableData result = new TableData();
        if (null == pageNum || pageNum < 1) {
            pageNum = 1;
        }
        if (null == pageSize || pageSize < 1) {
            pageSize = 10;
        }
        PageHelper.startPage(pageNum, pageSize);
        Page<ElecRadiationVO> page = elecRadiationMapper.selectQueryPage(measureTSelectDTO, mineId);
        Page<ElecRadiationVO> radiationVOPage = getListFmt(page);
        result.setTotal(radiationVOPage.getTotal());
        result.setRows(radiationVOPage.getResult());
        return result;
    }

    @Override
    public boolean deleteByIds(Long[] radiationIds) {
        boolean flag = false;
        if (radiationIds.length == 0) {
            throw new RuntimeException("请选择要删除的数据!");
        }
        List<Long> ids = Arrays.asList(radiationIds);
        ids.forEach(radiationId -> {
            ElecRadiationEntity elecRadiationEntity = elecRadiationMapper.selectOne(new LambdaQueryWrapper<ElecRadiationEntity>()
                    .eq(ElecRadiationEntity::getRadiationId, radiationId)
                    .eq(ElecRadiationEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ObjectUtil.isNull(elecRadiationEntity)) {
                throw new RuntimeException("未找到id为" + radiationId + "的测点数据");
            }
            if (StringUtils.equals(ConstantsInfo.ENABLE, elecRadiationEntity.getStatus())) {
                throw new RuntimeException("该测点已启用,无法删除");
            }
        });
        flag = this.removeBatchByIds(ids);
        return flag;
    }

    @Override
    public int batchEnableDisable(Long[] radiationIds) {
        int flag = 0;
        if (radiationIds.length == 0) {
            throw new RuntimeException("请选择要禁用的数据!");
        }
        List<Long> ids = Arrays.asList(radiationIds);
        ids.forEach(radiationId -> {
            ElecRadiationEntity elecRadiationEntity = elecRadiationMapper.selectOne(new LambdaQueryWrapper<ElecRadiationEntity>()
                    .eq(ElecRadiationEntity::getRadiationId, radiationId)
                    .eq(ElecRadiationEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ObjectUtil.isNull(elecRadiationEntity)) {
                throw new RuntimeException("未找到id为" + radiationId + "的测点数据");
            }
            if (StringUtils.equals(ConstantsInfo.ENABLE, elecRadiationEntity.getStatus())) {
                elecRadiationEntity.setStatus(ConstantsInfo.DISABLE);
            } else {
                elecRadiationEntity.setStatus(ConstantsInfo.ENABLE);
            }
            elecRadiationMapper.updateById(elecRadiationEntity);
        });
        return flag;
    }

    private Page<ElecRadiationVO> getListFmt(Page<ElecRadiationVO> list) {
        if (ListUtils.isNotNull(list.getResult())) {
            list.getResult().forEach(elecRadiationVO -> {
                elecRadiationVO.setWorkFaceName(getWorkFaceName(elecRadiationVO.getWorkFaceId()));
                elecRadiationVO.setInstallTimeFmt(DateUtils.getDateStrByTime(elecRadiationVO.getInstallTime()));
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