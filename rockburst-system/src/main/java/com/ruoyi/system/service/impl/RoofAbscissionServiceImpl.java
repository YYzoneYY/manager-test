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
import com.ruoyi.system.domain.Entity.RoofAbscissionEntity;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.Entity.WarnSchemeSeparateEntity;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.utils.ConvertUtils;
import com.ruoyi.system.domain.utils.NumberGeneratorUtils;
import com.ruoyi.system.domain.utils.ObtainWarnSchemeUtils;
import com.ruoyi.system.domain.vo.DisplacementVO;
import com.ruoyi.system.domain.vo.RoofAbscissionVO;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.RoofAbscissionService;
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
public class RoofAbscissionServiceImpl extends ServiceImpl<RoofAbscissionMapper, RoofAbscissionEntity> implements RoofAbscissionService {

    @Resource
    private RoofAbscissionMapper roofAbscissionMapper;

    @Resource
    private BizWorkfaceMapper bizWorkfaceMapper;

    @Resource
    private TunnelMapper tunnelMapper;

    @Resource
    private WarnSchemeSeparateMapper warnSchemeSeparateMapper;

    @Resource
    private WarnSchemeMapper warnSchemeMapper;

    @Override
    public int addMeasure(RoofAbscissionDTO roofAbscissionDTO, Long mineId) {
        int flag = 0;
        if (ObjectUtil.isNull(roofAbscissionDTO)) {
            throw new RuntimeException("参数错误,不能为空");
        }
        RoofAbscissionEntity roofAbscissionEntity = new RoofAbscissionEntity();
        BeanUtils.copyProperties(roofAbscissionDTO, roofAbscissionEntity);
        String maxMeasureNum = roofAbscissionMapper.selectMaxMeasureNum(mineId);
        if (maxMeasureNum.equals("0")) {
            roofAbscissionEntity.setMeasureNum(ConstantsInfo.ROOF_ABSCISSION_TYPE_INITIAL_VALUE);
        } else {
            String nextValue = NumberGeneratorUtils.getNextValue(maxMeasureNum);
            roofAbscissionEntity.setMeasureNum(nextValue);
        }
        roofAbscissionEntity.setCreateTime(System.currentTimeMillis());
        roofAbscissionEntity.setCreateBy(SecurityUtils.getUserId());
        roofAbscissionEntity.setMineId(mineId);
        roofAbscissionEntity.setTag(ConstantsInfo.MANUALLY_ADD);
        flag = roofAbscissionMapper.insert(roofAbscissionEntity);
        if (flag <= 0) {
            throw new RuntimeException("测点新增失败,请联系管理员");
        }
        return flag;
    }

    @Override
    public int updateMeasure(RoofAbscissionDTO roofAbscissionDTO) {
        int flag = 0;
        if (ObjectUtil.isNull(roofAbscissionDTO)) {
            throw new RuntimeException("参数错误,不能为空");
        }
        RoofAbscissionEntity roofAbscissionEntity = roofAbscissionMapper.selectOne(new LambdaQueryWrapper<RoofAbscissionEntity>()
                .eq(RoofAbscissionEntity::getRoofAbscissionId, roofAbscissionDTO.getRoofAbscissionId())
                .eq(RoofAbscissionEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(roofAbscissionEntity)) {
            throw new RuntimeException("测点不存在！");
        }
        Long roofAbscissionId = roofAbscissionEntity.getRoofAbscissionId();
        BeanUtils.copyProperties(roofAbscissionDTO, roofAbscissionEntity);
        if (ObjectUtil.isNull(roofAbscissionDTO.getMeasureNum())) {
            throw new RuntimeException("测点编码不允许为空");
        }
        if (!roofAbscissionDTO.getMeasureNum().equals(roofAbscissionEntity.getMeasureNum())) {
            throw new RuntimeException("测点编码不允许修改");
        }
        roofAbscissionEntity.setRoofAbscissionId(roofAbscissionId);
        roofAbscissionEntity.setUpdateTime(System.currentTimeMillis());
        roofAbscissionEntity.setUpdateBy(SecurityUtils.getUserId());
        flag = roofAbscissionMapper.updateById(roofAbscissionEntity);
        if (flag <= 0) {
            if (ObjectUtil.isNotNull(roofAbscissionDTO.getWarnSchemeDTO())) {
                int update = updateAloneWarnScheme(roofAbscissionDTO.getMeasureNum(), roofAbscissionDTO.getWarnSchemeDTO());
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
    public RoofAbscissionDTO detail(Long roofAbscissionId) {
        if (ObjectUtil.isNull(roofAbscissionId)) {
            throw new RuntimeException("参数错误,id不能为空");
        }
        RoofAbscissionEntity roofAbscissionEntity = roofAbscissionMapper.selectOne(new LambdaQueryWrapper<RoofAbscissionEntity>()
                .eq(RoofAbscissionEntity::getRoofAbscissionId, roofAbscissionId)
                .eq(RoofAbscissionEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(roofAbscissionEntity)) {
            throw new RuntimeException("未找到id为" + roofAbscissionId + "的数据");
        }
        RoofAbscissionDTO roofAbscissionDTO = new RoofAbscissionDTO();
        BeanUtils.copyProperties(roofAbscissionEntity, roofAbscissionDTO);
        WarnSchemeDTO warnSchemeDTO = ObtainWarnSchemeUtils.getObtainWarnScheme(roofAbscissionDTO.getMeasureNum(), roofAbscissionDTO.getSensorType(),
                roofAbscissionDTO.getWorkFaceId(), warnSchemeMapper, warnSchemeSeparateMapper);
        roofAbscissionDTO.setWarnSchemeDTO(warnSchemeDTO);
        return roofAbscissionDTO;
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
        Page<RoofAbscissionVO> page = roofAbscissionMapper.selectQueryPage(measureSelectDTO, mineId);
        Page<RoofAbscissionVO> roofAbscissionVOPage = getListFmt(page);
        result.setTotal(roofAbscissionVOPage.getTotal());
        result.setRows(roofAbscissionVOPage.getResult());
        return result;
    }

    @Override
    public boolean deleteByIds(Long[] roofAbscissionIds) {
        boolean flag = false;
        if (roofAbscissionIds.length == 0) {
            throw new RuntimeException("请选择要删除的数据!");
        }
        List<Long> ids = Arrays.asList(roofAbscissionIds);
        ids.forEach(roofAbscissionId -> {
            RoofAbscissionEntity roofAbscissionEntity = roofAbscissionMapper.selectOne(new LambdaQueryWrapper<RoofAbscissionEntity>()
                    .eq(RoofAbscissionEntity::getRoofAbscissionId, roofAbscissionId)
                    .eq(RoofAbscissionEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ObjectUtil.isNull(roofAbscissionEntity)) {
                throw new RuntimeException("未找到id为" + roofAbscissionId + "的数据");
            }
            if (StringUtils.equals(ConstantsInfo.ENABLE, roofAbscissionEntity.getStatus())) {
                throw new RuntimeException("该测点已启用,无法删除");
            }
        });
        flag = roofAbscissionMapper.deleteBatchIds(ids) > 0;
        return flag;
    }

    @Override
    public int batchEnableDisable(Long[] roofAbscissionIds) {
        int flag = 0;
        if (roofAbscissionIds.length == 0) {
            throw new RuntimeException("请选择要禁用的数据!");
        }
        List<Long> ids = Arrays.asList(roofAbscissionIds);
        ids.forEach(roofAbscissionId -> {
            RoofAbscissionEntity roofAbscissionEntity = roofAbscissionMapper.selectOne(new LambdaQueryWrapper<RoofAbscissionEntity>()
                    .eq(RoofAbscissionEntity::getRoofAbscissionId, roofAbscissionId)
                    .eq(RoofAbscissionEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ObjectUtil.isNull(roofAbscissionEntity)) {
                throw new RuntimeException("未找到id为" + roofAbscissionId + "的数据");
            }
            if (StringUtils.equals(ConstantsInfo.ENABLE, roofAbscissionEntity.getStatus())) {
                roofAbscissionEntity.setStatus(ConstantsInfo.DISABLE);
            } else {
                roofAbscissionEntity.setStatus(ConstantsInfo.ENABLE);
            }
            roofAbscissionMapper.updateById(roofAbscissionEntity);
        });
        return flag;
    }

    private Page<RoofAbscissionVO> getListFmt(Page<RoofAbscissionVO> list) {
        if (ListUtils.isNotNull(list.getResult())) {
            list.getResult().forEach(roofAbscissionVO -> {
                roofAbscissionVO.setWorkFaceName(getWorkFaceName(roofAbscissionVO.getWorkFaceId()));
                roofAbscissionVO.setTunnelName(getTunnelName(roofAbscissionVO.getTunnelId()));
                roofAbscissionVO.setInstallTimeFmt(DateUtils.getDateStrByTime(roofAbscissionVO.getInstallTime()));
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