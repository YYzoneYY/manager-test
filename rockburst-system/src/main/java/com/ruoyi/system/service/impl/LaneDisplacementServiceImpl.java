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
import com.ruoyi.system.domain.Entity.LaneDisplacementEntity;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.Entity.WarnSchemeSeparateEntity;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.utils.ConvertUtils;
import com.ruoyi.system.domain.utils.NumberGeneratorUtils;
import com.ruoyi.system.domain.utils.ObtainWarnSchemeUtils;
import com.ruoyi.system.domain.vo.DisplacementVO;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.LaneDisplacementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author: shikai
 * @date: 2025/8/15
 * @description:
 */

@Transactional
@Service
public class LaneDisplacementServiceImpl extends ServiceImpl<LaneDisplacementMapper, LaneDisplacementEntity> implements LaneDisplacementService {

    @Resource
    private LaneDisplacementMapper laneDisplacementMapper;

    @Resource
    private BizWorkfaceMapper bizWorkfaceMapper;

    @Resource
    private TunnelMapper tunnelMapper;

    @Resource
    private WarnSchemeSeparateMapper warnSchemeSeparateMapper;

    @Resource
    private WarnSchemeMapper warnSchemeMapper;

    @Override
    public int addMeasure(LaneDisplacementDTO laneDisplacementDTO, Long mineId) {
        int flag = 0;
        if (ObjectUtil.isNull(laneDisplacementDTO)) {
            throw new RuntimeException("参数错误,不能为空");
        }
        LaneDisplacementEntity laneDisplacementEntity = new LaneDisplacementEntity();
        BeanUtils.copyProperties(laneDisplacementDTO, laneDisplacementEntity);
        String maxMeasureNum = laneDisplacementMapper.selectMaxMeasureNum(mineId);
        if (maxMeasureNum.equals("0")) {
            laneDisplacementEntity.setMeasureNum(ConstantsInfo.LANE_DISPLACEMENT_INITIAL_VALUE);
        } else {
            String nextValue = NumberGeneratorUtils.getNextValue(maxMeasureNum);
            laneDisplacementEntity.setMeasureNum(nextValue);
        }
        laneDisplacementEntity.setCreateTime(System.currentTimeMillis());
        laneDisplacementEntity.setCreateBy(SecurityUtils.getUserId());
        laneDisplacementEntity.setMineId(mineId);
        laneDisplacementEntity.setTag(ConstantsInfo.MANUALLY_ADD);
        laneDisplacementEntity.setDelFlag(ConstantsInfo.ZERO_DEL_FLAG);
        flag = laneDisplacementMapper.insert(laneDisplacementEntity);
        if (flag <= 0) {
            throw new RuntimeException("测点新增失败,请联系管理员");
        }
        return flag;
    }

    @Override
    public int updateMeasure(LaneDisplacementDTO laneDisplacementDTO) {
        int flag = 0;
        if (ObjectUtil.isNull(laneDisplacementDTO)) {
            throw new RuntimeException("参数错误,不能为空！");
        }
        LaneDisplacementEntity laneDisplacementEntity = laneDisplacementMapper.selectOne(new LambdaQueryWrapper<LaneDisplacementEntity>()
                .eq(LaneDisplacementEntity::getDisplacementId, laneDisplacementDTO.getDisplacementId())
                .eq(LaneDisplacementEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(laneDisplacementEntity)) {
            throw new RuntimeException("该测点不存在！");
        }
        Long displacementId = laneDisplacementEntity.getDisplacementId();
        BeanUtils.copyProperties(laneDisplacementDTO, laneDisplacementEntity);
        if (ObjectUtil.isNull(laneDisplacementDTO.getMeasureNum())) {
            throw new RuntimeException("测点编码不允许为空");
        }
        if (!laneDisplacementDTO.getMeasureNum().equals(laneDisplacementEntity.getMeasureNum())) {
            throw new RuntimeException("测点编码不允许修改");
        }
        laneDisplacementEntity.setDisplacementId(displacementId);
        laneDisplacementEntity.setUpdateTime(System.currentTimeMillis());
        laneDisplacementEntity.setUpdateBy(SecurityUtils.getUserId());
        flag = laneDisplacementMapper.updateById(laneDisplacementEntity);
        if (flag <= 0) {
            if (ObjectUtil.isNotNull(laneDisplacementDTO.getWarnSchemeDTO())) {
                int update = updateAloneWarnScheme(laneDisplacementDTO.getMeasureNum(), laneDisplacementDTO.getWarnSchemeDTO());
                if (update <= 0) {
                    throw new RuntimeException("预警方案修改失败,请联系管理员");
                }
            }
        } else {
            throw new RuntimeException("测点编辑失败,请联系管理员");
        }
        return flag;
    }

    @Override
    public LaneDisplacementDTO detail(Long displacementId) {
        if (ObjectUtil.isNull(displacementId)) {
            throw new RuntimeException("参数错误,id不能为空");
        }
        LaneDisplacementEntity laneDisplacementEntity = laneDisplacementMapper.selectOne(new LambdaQueryWrapper<LaneDisplacementEntity>()
                .eq(LaneDisplacementEntity::getDisplacementId, displacementId)
                .eq(LaneDisplacementEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(laneDisplacementEntity)) {
            throw new RuntimeException("未找到id为" + displacementId + "的测点数据");
        }
        LaneDisplacementDTO laneDisplacementDTO = new LaneDisplacementDTO();
        BeanUtils.copyProperties(laneDisplacementEntity, laneDisplacementDTO);
        WarnSchemeDTO warnSchemeDTO = ObtainWarnSchemeUtils.getObtainWarnScheme(laneDisplacementDTO.getMeasureNum(), laneDisplacementDTO.getSensorType(),
                laneDisplacementDTO.getWorkFaceId(), warnSchemeMapper, warnSchemeSeparateMapper);
        laneDisplacementDTO.setWarnSchemeDTO(warnSchemeDTO);
        return laneDisplacementDTO;
    }

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
        Page<DisplacementVO> page = laneDisplacementMapper.selectQueryPage(measureSelectDTO, mineId);
        Page<DisplacementVO> displacementVOPage = getListFmt(page);
        result.setTotal(displacementVOPage.getTotal());
        result.setRows(displacementVOPage.getResult());
        return result;
    }

    @Override
    public boolean deleteByIds(Long[] displacementIds) {
        boolean flag = false;
        if (displacementIds.length == 0) {
            throw new RuntimeException("请选择要删除的数据!");
        }
        List<Long> ids = Arrays.asList(displacementIds);
        ids.forEach(displacementId -> {
            LaneDisplacementEntity laneDisplacementEntity = laneDisplacementMapper.selectOne(new LambdaQueryWrapper<LaneDisplacementEntity>()
                    .eq(LaneDisplacementEntity::getDisplacementId, displacementId)
                    .eq(LaneDisplacementEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ObjectUtil.isNull(laneDisplacementEntity)) {
                throw new RuntimeException("未找到id为" + displacementId + "的数据");
            }
            if (StringUtils.equals(ConstantsInfo.ENABLE, laneDisplacementEntity.getStatus())) {
                throw new RuntimeException("该测点已启用,无法删除");
            }
        });
        flag = this.removeBatchByIds(ids);
        return flag;
    }

    @Override
    public int batchEnableDisable(Long[] displacementIds) {
        int flag = 0;
        if (displacementIds.length == 0) {
            throw new RuntimeException("请选择要禁用的数据!");
        }
        List<Long> ids = Arrays.asList(displacementIds);
        ids.forEach(displacementId -> {
            LaneDisplacementEntity laneDisplacementEntity = laneDisplacementMapper.selectOne(new LambdaQueryWrapper<LaneDisplacementEntity>()
                    .eq(LaneDisplacementEntity::getDisplacementId, displacementId)
                    .eq(LaneDisplacementEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ObjectUtil.isNull(laneDisplacementEntity)) {
                throw new RuntimeException("未找到id为" + displacementId + "的数据");
            }
            if (StringUtils.equals(ConstantsInfo.ENABLE, laneDisplacementEntity.getStatus())) {
                laneDisplacementEntity.setStatus(ConstantsInfo.DISABLE);
            } else {
                laneDisplacementEntity.setStatus(ConstantsInfo.ENABLE);
            }
            laneDisplacementMapper.updateById(laneDisplacementEntity);
        });
        return flag;
    }

    private Page<DisplacementVO> getListFmt(Page<DisplacementVO> list) {
        if (ListUtils.isNotNull(list.getResult())) {
            list.getResult().forEach(displacementVO -> {
                displacementVO.setWorkFaceName(getWorkFaceName(displacementVO.getWorkFaceId()));
                displacementVO.setTunnelName(getTunnelName(displacementVO.getTunnelId()));
                displacementVO.setInstallTimeFmt(DateUtils.getDateStrByTime(displacementVO.getInstallTime()));
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

    /**
     * 获取巷道名称
     */
    private String getTunnelName(Long tunnelId) {
        String tunnelName = null;
        TunnelEntity tunnelEntity = tunnelMapper.selectOne(new LambdaQueryWrapper<TunnelEntity>()
                .eq(TunnelEntity::getTunnelId, tunnelId)
                .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(tunnelEntity)) {
            return tunnelName;
        }
        tunnelName = tunnelEntity.getTunnelName();
        return tunnelName;
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