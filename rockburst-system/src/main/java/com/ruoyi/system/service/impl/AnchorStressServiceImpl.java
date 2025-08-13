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
import com.ruoyi.system.domain.Entity.AnchorCableStressEntity;
import com.ruoyi.system.domain.Entity.SurveyAreaEntity;
import com.ruoyi.system.domain.Entity.WarnSchemeSeparateEntity;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.utils.ConvertUtils;
import com.ruoyi.system.domain.utils.NumberGeneratorUtils;
import com.ruoyi.system.domain.utils.ObtainWarnSchemeUtils;
import com.ruoyi.system.domain.vo.AnchorCableStressVO;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.AnchorStressService;
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
public class AnchorStressServiceImpl extends ServiceImpl<AnchorCableStressMapper, AnchorCableStressEntity> implements AnchorStressService {

    @Resource
    private AnchorCableStressMapper anchorCableStressMapper;

    @Resource
    private BizWorkfaceMapper bizWorkfaceMapper;

    @Resource
    private SurveyAreaMapper surveyAreaMapper;

    @Resource
    private WarnSchemeSeparateMapper warnSchemeSeparateMapper;

    @Resource
    private WarnSchemeMapper warnSchemeMapper;


    /**
     * 测点新增
     * @param anchorCableStressDTO 参数DTO
     * @return 返回结果
     */
    @Override
    public int addMeasure(AnchorCableStressDTO anchorCableStressDTO, Long mineId) {
        int flag = 0;
        if (ObjectUtil.isNull(anchorCableStressDTO)) {
            throw new RuntimeException("参数错误,不能为空");
        }
        AnchorCableStressEntity anchorCableStressEntity = new AnchorCableStressEntity();
        BeanUtils.copyProperties(anchorCableStressDTO, anchorCableStressEntity);
        String maxMeasureNum = anchorCableStressMapper.selectMaxMeasureNum(anchorCableStressDTO.getSensorType(), mineId);

        if (maxMeasureNum.equals("0")) {
            if (anchorCableStressDTO.getSensorType().equals(ConstantsInfo.ANCHOR_STRESS_TYPE)) {
                anchorCableStressEntity.setMeasureNum(ConstantsInfo.ANCHOR_STRESS_INITIAL_VALUE);
            }
            if (anchorCableStressDTO.getSensorType().equals(ConstantsInfo.ANCHOR_CABLE_STRESS_TYPE)) {
                anchorCableStressEntity.setMeasureNum(ConstantsInfo.ANCHOR_CABLE_STRESS_INITIAL_VALUE);
            }
        } else {
            String nextValue = NumberGeneratorUtils.getNextValue(maxMeasureNum);
            anchorCableStressEntity.setMeasureNum(nextValue);
        }
        anchorCableStressEntity.setCreateTime(System.currentTimeMillis());
        anchorCableStressEntity.setCreateBy(SecurityUtils.getUserId());
        anchorCableStressEntity.setMineId(mineId);
        anchorCableStressEntity.setTag(ConstantsInfo.MANUALLY_ADD);
        anchorCableStressEntity.setDelFlag(ConstantsInfo.ZERO_DEL_FLAG);
        flag = anchorCableStressMapper.insert(anchorCableStressEntity);
        if (flag <= 0) {
            throw new RuntimeException("测点新增失败,请联系管理员");
        }
        return flag;
    }

    /**
     * 测点修改
     * @param anchorCableStressDTO 参数DTO
     * @return 返回结果
     */
    @Override
    public int updateMeasure(AnchorCableStressDTO anchorCableStressDTO) {
        int flag = 0;
        if (ObjectUtil.isNull(anchorCableStressDTO)) {
            throw new RuntimeException("参数错误,不能为空");
        }
        AnchorCableStressEntity anchorCableStressEntity = anchorCableStressMapper.selectOne(new LambdaQueryWrapper<AnchorCableStressEntity>()
                .eq(AnchorCableStressEntity::getAnchorCableStressId, anchorCableStressDTO.getAnchorCableStressId())
                .eq(AnchorCableStressEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(anchorCableStressEntity)) {
            throw new RuntimeException("该测点不存在");
        }
        Long anchorCableStressId = anchorCableStressEntity.getAnchorCableStressId();
        BeanUtils.copyProperties(anchorCableStressDTO, anchorCableStressEntity);
        if (ObjectUtil.isNull(anchorCableStressDTO.getMeasureNum())) {
            throw new RuntimeException("测点编码不允许为空");
        }
        if (!anchorCableStressDTO.getMeasureNum().equals(anchorCableStressEntity.getMeasureNum())) {
            throw new RuntimeException("测点编码不允许修改");
        }
        anchorCableStressEntity.setAnchorCableStressId(anchorCableStressId);
        anchorCableStressEntity.setUpdateTime(System.currentTimeMillis());
        anchorCableStressEntity.setUpdateBy(SecurityUtils.getUserId());
        flag = anchorCableStressMapper.updateById(anchorCableStressEntity);
        if (flag > 0) {
            if (ObjectUtil.isNotNull(anchorCableStressDTO.getWarnSchemeDTO())) {
                int update = updateAloneWarnScheme(anchorCableStressDTO.getMeasureNum(), anchorCableStressDTO.getWarnSchemeDTO());
                if (update <= 0) {
                    throw new RuntimeException("预警方案修改失败,请联系管理员");
                }
            }
        } else {
            throw new RuntimeException("测点修改失败,请联系管理员");
        }
        return flag;
    }

    /**
     * 根据id查询
     * @param anchorCableStressId 主键id
     * @return 返回结果
     */
    @Override
    public AnchorCableStressDTO detail(Long anchorCableStressId) {
        if (ObjectUtil.isNull(anchorCableStressId)) {
            throw new RuntimeException("参数错误,id不能为空");
        }
        AnchorCableStressEntity anchorCableStressEntity = anchorCableStressMapper.selectOne(new LambdaQueryWrapper<AnchorCableStressEntity>()
                .eq(AnchorCableStressEntity::getAnchorCableStressId, anchorCableStressId)
                .eq(AnchorCableStressEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(anchorCableStressEntity)) {
            throw new RuntimeException("未找到此测点信息");
        }
        AnchorCableStressDTO anchorCableStressDTO = new AnchorCableStressDTO();
        BeanUtils.copyProperties(anchorCableStressEntity, anchorCableStressDTO);
        WarnSchemeDTO warnSchemeDTO = ObtainWarnSchemeUtils.getObtainWarnScheme(anchorCableStressDTO.getMeasureNum(), anchorCableStressDTO.getSensorType(),
                anchorCableStressDTO.getWorkFaceId(), warnSchemeMapper, warnSchemeSeparateMapper);
        anchorCableStressDTO.setWarnSchemeDTO(warnSchemeDTO);
        return anchorCableStressDTO;
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
        Page<AnchorCableStressVO> page = anchorCableStressMapper.selectQueryPage(measureSelectDTO, mineId);
        Page<AnchorCableStressVO> resistanceVOPage = getListFmt(page);
        result.setTotal(resistanceVOPage.getTotal());
        result.setRows(resistanceVOPage.getResult());
        return result;
    }

    /**
     * (批量)删除
     * @param anchorCableStressIds 主键id数组
     * @return 返回结果
     */
    @Override
    public boolean deleteByIds(Long[] anchorCableStressIds) {
        boolean flag = false;
        if (anchorCableStressIds.length == 0) {
            throw new RuntimeException("请选择要删除的数据!");
        }
        List<Long> ids = Arrays.asList(anchorCableStressIds);
        ids.forEach(anchorCableStressId -> {
            AnchorCableStressEntity anchorCableStressEntity = anchorCableStressMapper.selectById(anchorCableStressId);
            if (ObjectUtil.isNull(anchorCableStressEntity)) {
                throw new RuntimeException("未找到id为" + anchorCableStressId + "的数据");
            }
            if (StringUtils.equals(ConstantsInfo.ENABLE, anchorCableStressEntity.getStatus())) {
                throw new RuntimeException("该测点已启用,不能删除");
            }
        });
        flag = this.removeBatchByIds(ids);
        return flag;
    }

    /**
     * (批量)启用/禁用
     * @param anchorCableStressIds 主键id数组
     * @return 返回结果
     */
    @Override
    public int batchEnableDisable(Long[] anchorCableStressIds) {
        int flag = 0;
        if (anchorCableStressIds.length == 0) {
            throw new RuntimeException("请选择要禁用的数据!");
        }
        List<Long> ids = Arrays.asList(anchorCableStressIds);
        ids.forEach(anchorCableStressId -> {
            AnchorCableStressEntity anchorCableStressEntity = anchorCableStressMapper.selectOne(new LambdaQueryWrapper<AnchorCableStressEntity>()
                    .eq(AnchorCableStressEntity::getAnchorCableStressId, anchorCableStressId)
                    .eq(AnchorCableStressEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ObjectUtil.isNull(anchorCableStressEntity)) {
                throw new RuntimeException("未找到id为" + anchorCableStressId + "的数据");
            }
            if (StringUtils.equals(ConstantsInfo.ENABLE, anchorCableStressEntity.getStatus())) {
                anchorCableStressEntity.setStatus(ConstantsInfo.DISABLE);
            } else {
                anchorCableStressEntity.setStatus(ConstantsInfo.ENABLE);
            }
            anchorCableStressMapper.updateById(anchorCableStressEntity);
        });
        return flag;
    }

    /**
     * VO格式化
     */
    private Page<AnchorCableStressVO> getListFmt(Page<AnchorCableStressVO> list) {
        if (ListUtils.isNotNull(list.getResult())) {
            list.getResult().forEach(anchorCableStressVO -> {
                anchorCableStressVO.setWorkFaceName(getWorkFaceName(anchorCableStressVO.getWorkFaceId()));
                anchorCableStressVO.setInstallTimeFmt(DateUtils.getDateStrByTime(anchorCableStressVO.getInstallTime()));
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