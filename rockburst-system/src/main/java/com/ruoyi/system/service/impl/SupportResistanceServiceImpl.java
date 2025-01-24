package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.common.utils.*;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.SupportResistanceEntity;
import com.ruoyi.system.domain.Entity.SurveyAreaEntity;
import com.ruoyi.system.domain.Entity.WarnSchemeSeparateEntity;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.utils.ConvertUtils;
import com.ruoyi.system.domain.utils.NumberGeneratorUtils;
import com.ruoyi.system.domain.utils.ObtainWarnSchemeUtils;
import com.ruoyi.system.domain.vo.SupportResistanceVO;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.SupportResistanceService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: shikai
 * @date: 2024/11/27
 * @description:
 */

@Transactional
@Service
public class SupportResistanceServiceImpl extends ServiceImpl<SupportResistanceMapper, SupportResistanceEntity> implements SupportResistanceService {

    @Resource
    private SupportResistanceMapper supportResistanceMapper;

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
     * @param supportResistanceDTO 参数DTO
     * @return 返回结果
     */
    @Override
    public int addMeasure(SupportResistanceDTO supportResistanceDTO) {
        int flag = 0;
        if (ObjectUtil.isNull(supportResistanceDTO)) {
            throw new RuntimeException("参数错误,不能为空");
        }
        Long selectCount = supportResistanceMapper.selectCount(new LambdaQueryWrapper<SupportResistanceEntity>()
                .eq(SupportResistanceEntity::getColumnNum, supportResistanceDTO.getColumnNum())
                .eq(SupportResistanceEntity::getColumnName, supportResistanceDTO.getColumnName())
                .eq(SupportResistanceEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (selectCount > 0) {
            throw new RuntimeException("同一架号,立柱名称不能重复!");
        }
        SupportResistanceEntity supportResistanceEntity = new SupportResistanceEntity();
        BeanUtils.copyProperties(supportResistanceDTO, supportResistanceEntity);
        String maxMeasureNum = supportResistanceMapper.selectMaxMeasureNum();
        if (maxMeasureNum.equals("0")) {
            supportResistanceEntity.setMeasureNum(ConstantsInfo.SUPPORT_RESISTANCE_INITIAL_VALUE);
        } else {
            String nextValue = NumberGeneratorUtils.getNextValue(maxMeasureNum);
            supportResistanceEntity.setMeasureNum(nextValue);
        }
        supportResistanceEntity.setCreateTime(System.currentTimeMillis());
        supportResistanceEntity.setCreateBy(SecurityUtils.getUserId());
        supportResistanceEntity.setTag(ConstantsInfo.MANUALLY_ADD);
        supportResistanceEntity.setDelFlag(ConstantsInfo.ZERO_DEL_FLAG);
        flag = supportResistanceMapper.insert(supportResistanceEntity);
        if (flag <= 0) {
            throw new RuntimeException("测点新增失败,请联系管理员");
        }
        return flag;
    }

    /**
     * 测点修改
     * @param supportResistanceDTO 参数DTO
     * @return 返回结果
     */
    @Override
    public int updateMeasure(SupportResistanceDTO supportResistanceDTO) {
        int flag = 0;
        SupportResistanceEntity supportResistanceEntity = supportResistanceMapper.selectOne(new LambdaQueryWrapper<SupportResistanceEntity>()
                .eq(SupportResistanceEntity::getSupportResistanceId, supportResistanceDTO.getSupportResistanceId())
                .eq(SupportResistanceEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(supportResistanceEntity)) {
            throw new RuntimeException("该测点不存在");
        }
        Long selectCount = supportResistanceMapper.selectCount(new LambdaQueryWrapper<SupportResistanceEntity>()
                .eq(SupportResistanceEntity::getColumnNum, supportResistanceDTO.getColumnNum())
                .eq(SupportResistanceEntity::getColumnName, supportResistanceDTO.getColumnName())
                .ne(SupportResistanceEntity::getSupportResistanceId, supportResistanceDTO.getSupportResistanceId())
                .eq(SupportResistanceEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (selectCount > 0) {
            throw new RuntimeException("同一架号,立柱名称不能重复!");
        }
        Long supportResistanceId = supportResistanceEntity.getSupportResistanceId();
        BeanUtils.copyProperties(supportResistanceDTO, supportResistanceEntity);
        if (ObjectUtil.isNull(supportResistanceDTO.getMeasureNum())) {
            throw new RuntimeException("测点编码不允许为空");
        }
        if (!supportResistanceDTO.getMeasureNum().equals(supportResistanceEntity.getMeasureNum())) {
            throw new RuntimeException("测点编码不允许修改");
        }
        supportResistanceEntity.setSupportResistanceId(supportResistanceId);
        supportResistanceEntity.setUpdateTime(System.currentTimeMillis());
        supportResistanceEntity.setUpdateBy(SecurityUtils.getUserId());
        flag = supportResistanceMapper.updateById(supportResistanceEntity);
        if (flag > 0) {
            if (ObjectUtil.isNotNull(supportResistanceDTO.getWarnSchemeDTO())) {
                int update = updateAloneWarnScheme(supportResistanceDTO.getMeasureNum(), supportResistanceDTO.getWarnSchemeDTO());
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
     * 分页查询
     * @param measureSelectDTO 查询参数DTO
     * @param pageNum 当前页码
     * @param pageSize 条数
     * @return 返回结果
     */
    @Override
    public TableData pageQueryList(MeasureSelectDTO measureSelectDTO, Integer pageNum, Integer pageSize) {
        TableData result = new TableData();
        if (null == pageNum || pageNum < 1) {
            pageNum = 1;
        }
        if (null == pageSize || pageSize < 1) {
            pageSize = 10;
        }
        PageHelper.startPage(pageNum, pageSize);
        Page<SupportResistanceVO> page = supportResistanceMapper.selectQueryPage(measureSelectDTO);
        Page<SupportResistanceVO> resistanceVOPage = getListFmt(page);
        result.setTotal(resistanceVOPage.getTotal());
        result.setRows(resistanceVOPage.getResult());
        return result;
    }

    /**
     * 根据id查询
     * @param supportResistanceId id
     * @return 返回结果
     */
    @SneakyThrows
    @Override
    public SupportResistanceDTO detail(Long supportResistanceId) {
        if (ObjectUtil.isNull(supportResistanceId)) {
            throw new RuntimeException("参数错误,id不能为空");
        }
        SupportResistanceEntity supportResistanceEntity = supportResistanceMapper.selectOne(new LambdaQueryWrapper<SupportResistanceEntity>()
                .eq(SupportResistanceEntity::getSupportResistanceId, supportResistanceId)
                .eq(SupportResistanceEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(supportResistanceEntity)) {
            throw new RuntimeException("该测点不存在");
        }
        SupportResistanceDTO supportResistanceDTO = new SupportResistanceDTO();
        BeanUtils.copyProperties(supportResistanceEntity, supportResistanceDTO);
        supportResistanceDTO.setSensorNum(montageSensorNum(supportResistanceEntity.getSubstationNum(), supportResistanceEntity.getColumnNum()));
        // 获取预警方案基本信息
        WarnSchemeDTO warnSchemeDTO = ObtainWarnSchemeUtils.getObtainWarnScheme(supportResistanceEntity.getMeasureNum(), supportResistanceDTO.getSensorType(),
                supportResistanceEntity.getWorkFaceId(), warnSchemeMapper, warnSchemeSeparateMapper);
        supportResistanceDTO.setWarnSchemeDTO(warnSchemeDTO);
        return supportResistanceDTO;
    }

    /**
     * 删除/批量删除
     * @param supportResistanceIds id数组
     * @return 返回结果
     */
    @Override
    public boolean deleteByIds(Long[] supportResistanceIds) {
        boolean flag = false;
        if (supportResistanceIds.length == 0) {
            throw new RuntimeException("请选择要删除的数据!");
        }
        List<Long> ids = Arrays.asList(supportResistanceIds);
        ids.forEach(supportResistanceId -> {
            SupportResistanceEntity supportResistanceEntity = supportResistanceMapper.selectById(supportResistanceId);
            if (ObjectUtil.isNull(supportResistanceEntity)) {
                throw new RuntimeException("未找到id为" + supportResistanceId + "的数据");
            }
            if (StringUtils.equals(ConstantsInfo.ENABLE, supportResistanceEntity.getStatus())) {
                throw new RuntimeException("测点" + supportResistanceEntity.getMeasureNum() + "已启用,无法删除");
            }
        });
        flag = this.removeBatchByIds(ids);
        return flag;
    }

    /**
     * (批量)启用/禁用
     * @param supportResistanceIds id数组
     * @return 返回结果
     */
    @Override
    public int batchEnableDisable(Long[] supportResistanceIds) {
        int flag = 0;
        if (supportResistanceIds.length == 0) {
            throw new RuntimeException("请选择要禁用的数据!");
        }
        List<Long> ids = Arrays.asList(supportResistanceIds);
        ids.forEach(supportResistanceId -> {
            SupportResistanceEntity supportResistanceEntity = supportResistanceMapper.selectOne(new LambdaQueryWrapper<SupportResistanceEntity>()
                    .eq(SupportResistanceEntity::getSupportResistanceId, supportResistanceId)
                    .eq(SupportResistanceEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ObjectUtil.isNull(supportResistanceEntity)) {
                throw new RuntimeException("未找到id为" + supportResistanceId + "的数据");
            }
            if (supportResistanceEntity.getStatus().equals(ConstantsInfo.ENABLE)) {
                supportResistanceEntity.setStatus(ConstantsInfo.DISABLE);
            }
            if (supportResistanceEntity.getStatus().equals(ConstantsInfo.DISABLE)) {
                supportResistanceEntity.setStatus(ConstantsInfo.ENABLE);
            }
            supportResistanceMapper.updateById(supportResistanceEntity);
        });
        return flag;
    }

    /**
     * VO格式化
     */
    private Page<SupportResistanceVO> getListFmt(Page<SupportResistanceVO> list) {
        if (ListUtils.isNotNull(list.getResult())) {
            list.getResult().forEach(supportResistanceVO -> {
                supportResistanceVO.setWorkFaceName(getWorkFaceName(supportResistanceVO.getWorkFaceId()));
                supportResistanceVO.setSensorNum(montageSensorNum(supportResistanceVO.getColumnName(), supportResistanceVO.getColumnNum()));
                supportResistanceVO.setDataTimeFmt(DateUtils.getDateStrByTime(supportResistanceVO.getDataTime()));
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
     * 拼接传感器编号
     */
    private String montageSensorNum(String columnNum, String columnName) {
        String sensorNum = "";
        StringBuilder builder = new StringBuilder();
        StringBuilder append = builder.append(columnName).append(columnNum);
        sensorNum = append.toString();
        return sensorNum;
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