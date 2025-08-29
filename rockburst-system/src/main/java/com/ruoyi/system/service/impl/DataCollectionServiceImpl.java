package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.*;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.DataCollectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author: shikai
 * @date: 2025/8/28
 * @description:
 */

@Transactional
@Service
public class DataCollectionServiceImpl implements DataCollectionService {

    private static final Logger logger = LoggerFactory.getLogger(DataCollectionServiceImpl.class);

    @Resource
    private SupportResistanceMapper supportResistanceMapper;

    @Resource
    private DrillingStressMapper drillingStressMapper;

    @Resource
    private AnchorCableStressMapper anchorCableStressMapper;

    @Resource
    private RoofAbscissionMapper roofAbscissionMapper;

    @Resource
    private LaneDisplacementMapper laneDisplacementMapper;

    @Resource
    private ElecRadiationMapper elecRadiationMapper;

    @Resource
    private BizWorkfaceMapper bizWorkfaceMapper;

    @Resource
    private TunnelMapper tunnelMapper;

    @Override
    public int dataCollectionSave(Map<String, Object> map) {
        int flag = 0;
        String type = (String) map.get("type");
        switch (type) {
            case "ZJ":
                flag = addZJ(map);
                break;
            case "ZK":
                flag = addZK(map);
                break;
            case "MG":
                flag = addMG(map);
                break;
            case "DB":
                flag = addDB(map);
                break;
            case "HD":
                flag = addHD(map);
                break;
            case "DC":
                flag = addDC(map);
                break;
            default:
                logger.warn("未知的数据类型: {}", type);
                break;
        }
        return flag;
    }

    private int addZJ(Map<String, Object> map) {
        String measureNum = (String) map.get("measureNum");
        String monitorAreaName = (String) map.get("monitorAreaName");
        String sensorType = (String) map.get("sensorType");
        String sensorNum = (String) map.get("sensorNum");
        String columnNum = getFirstPart(sensorNum);
        String columnName = getLastPart(sensorNum);

        SupportResistanceEntity supportResistanceEntity = new SupportResistanceEntity();
        // 设置通用字段
        setCommonFields(supportResistanceEntity, map, measureNum, monitorAreaName, sensorType);

        // 设置特定字段
        supportResistanceEntity.setSubstationNum((String) map.get("siteNum"));
        supportResistanceEntity.setColumnNum(columnNum);
        supportResistanceEntity.setColumnName(columnName);
        supportResistanceEntity.setSensorLocation((String) map.get("sensorLocation"));
        supportResistanceEntity.setPressureReliefValue(convertToBigDecimal(map.get("pressureNumTwo")));
        supportResistanceEntity.setSettingLoad(convertToBigDecimal(map.get("settingLoadTwo")));
        supportResistanceEntity.setWorkResistance(convertToBigDecimal(map.get("workResistanceTwo")));

        return supportResistanceMapper.insert(supportResistanceEntity);
    }

    private int addZK(Map<String, Object> map) {
        String measureNum = (String) map.get("measureNum");
        String monitorAreaName = (String) map.get("monitorAreaName");
        String sensorType = (String) map.get("sensorType");

        DrillingStressEntity drillingStressEntity = new DrillingStressEntity();
        // 设置通用字段
        setCommonFields(drillingStressEntity, map, measureNum, monitorAreaName, sensorType);

        // 设置特定字段
        drillingStressEntity.setSensorLocation((String) map.get("sensorLocation"));
        drillingStressEntity.setInstallTime(convertToLong(map.get("installTime")));
        drillingStressEntity.setInstallDepth(convertToBigDecimal(map.get("installDepth")));
        drillingStressEntity.setInstallDirection((String) map.get("detectorDirection"));
        drillingStressEntity.setInitialStress(convertToBigDecimal(map.get("initialStress")));

        return drillingStressMapper.insert(drillingStressEntity);
    }

    private int addMG(Map<String, Object> map) {
        String measureNum = (String) map.get("measureNum");
        String monitorAreaName = (String) map.get("monitorAreaName");
        String sensorType = (String) map.get("sensorType");

        AnchorCableStressEntity anchorCableStressEntity = new AnchorCableStressEntity();
        // 设置通用字段
        setCommonFields(anchorCableStressEntity, map, measureNum, monitorAreaName, sensorType);

        // 设置特定字段
        anchorCableStressEntity.setSensorLocation((String) map.get("sensorLocation"));
        anchorCableStressEntity.setInstallTime(convertToLong(map.get("installTime")));
        anchorCableStressEntity.setBreakingValue(convertToBigDecimal(map.get("breakingValue")));

        return anchorCableStressMapper.insert(anchorCableStressEntity);
    }

    private int addDB(Map<String, Object> map) {
        String measureNum = (String) map.get("measureNum");
        String monitorAreaName = (String) map.get("monitorAreaName");
        String sensorType = (String) map.get("sensorType");
        String tunnelName = (String) map.get("tunnelName");

        RoofAbscissionEntity roofAbscissionEntity = new RoofAbscissionEntity();
        // 设置通用字段
        setCommonFields(roofAbscissionEntity, map, measureNum, monitorAreaName, sensorType);

        // 设置特定字段
        roofAbscissionEntity.setSensorLocation((String) map.get("sensorLocation"));
        roofAbscissionEntity.setInstallTime(convertToLong(map.get("installTime")));
        roofAbscissionEntity.setDeepInitDepth(convertToBigDecimal(map.get("deepDepth")));
        roofAbscissionEntity.setShallowInitDepth(convertToBigDecimal(map.get("shallowDepth")));
        roofAbscissionEntity.setOriginalTunnelName(tunnelName);
        roofAbscissionEntity.setTunnelId(obtainTunnelId(tunnelName, 2L));

        return roofAbscissionMapper.insert(roofAbscissionEntity);
    }

    private int addHD(Map<String, Object> map) {
        String measureNum = (String) map.get("measureNum");
        String monitorAreaName = (String) map.get("monitorAreaName");
        String sensorType = (String) map.get("sensorType");
        String tunnelName = (String) map.get("tunnelName");

        LaneDisplacementEntity laneDisplacementEntity = new LaneDisplacementEntity();
        // 设置通用字段
        setCommonFields(laneDisplacementEntity, map, measureNum, monitorAreaName, sensorType);

        // 设置特定字段
        laneDisplacementEntity.setSensorLocation((String) map.get("sensorLocation"));
        laneDisplacementEntity.setInstallTime(convertToLong(map.get("installTime")));
        laneDisplacementEntity.setOriginalTunnelName(tunnelName);
        laneDisplacementEntity.setTunnelId(obtainTunnelId(tunnelName, 2L));

        return laneDisplacementMapper.insert(laneDisplacementEntity);
    }

    private int addDC(Map<String, Object> map) {
        String measureNum = (String) map.get("measureNum");
        String sensorType = (String) map.get("sensorType");
        String workFaceName = (String) map.get("workFaceName");

        ElecRadiationEntity elecRadiationEntity = new ElecRadiationEntity();
        // 设置通用字段（部分）
        elecRadiationEntity.setMeasureNum(measureNum);
        elecRadiationEntity.setSensorType(sensorType);
        elecRadiationEntity.setSensorLocation((String) map.get("sensorLocation"));
        elecRadiationEntity.setInstallTime(convertToLong(map.get("installTime")));
        elecRadiationEntity.setStatus(ConstantsInfo.ENABLE);
        elecRadiationEntity.setTag(ConstantsInfo.AUTOMATIC_ACCESS);
        elecRadiationEntity.setMineId(2L);
        elecRadiationEntity.setDelFlag(ConstantsInfo.ZERO_DEL_FLAG);
        elecRadiationEntity.setCreateTime(System.currentTimeMillis());
        elecRadiationEntity.setWorkFaceId(obtainWorkFaceId2(workFaceName, 2L));
        elecRadiationEntity.setOriginalWorkFaceName(workFaceName);

        return elecRadiationMapper.insert(elecRadiationEntity);
    }

    /**
     * 设置通用字段
     */
    private <T> void setCommonFields(T entity, Map<String, Object> map, String measureNum,
                                     String monitorAreaName, String sensorType) {
        try {
            // 使用反射设置通用字段
            entity.getClass().getMethod("setMeasureNum", String.class).invoke(entity, measureNum);
            entity.getClass().getMethod("setSurveyAreaName", String.class).invoke(entity, monitorAreaName);
            entity.getClass().getMethod("setSensorType", String.class).invoke(entity, sensorType);

            entity.getClass().getMethod("setXAxis", String.class).invoke(entity, (String) map.get("xaxis"));
            entity.getClass().getMethod("setYAxis", String.class).invoke(entity, (String) map.get("yaxis"));
            entity.getClass().getMethod("setZAxis", String.class).invoke(entity, (String) map.get("zaxis"));

            entity.getClass().getMethod("setStatus", String.class).invoke(entity, ConstantsInfo.ENABLE);
            entity.getClass().getMethod("setTag", String.class).invoke(entity, ConstantsInfo.AUTOMATIC_ACCESS);
            entity.getClass().getMethod("setMineId", Long.class).invoke(entity, 2L);
            entity.getClass().getMethod("setDelFlag", String.class).invoke(entity, ConstantsInfo.ZERO_DEL_FLAG);
            entity.getClass().getMethod("setCreateTime", Long.class).invoke(entity, System.currentTimeMillis());

            Long workFaceId = obtainWorkFaceId(monitorAreaName);
            entity.getClass().getMethod("setWorkFaceId", Long.class).invoke(entity, workFaceId);

            String originalString = obtainOriginalString(monitorAreaName);
            entity.getClass().getMethod("setOriginalWorkFaceName", String.class).invoke(entity, originalString);

        } catch (Exception e) {
            // 处理反射异常
            logger.error("设置通用字段时发生异常", e);
        }
    }


    /**
     * 将对象转换为Long
     * @param obj 待转换的对象
     * @return Long值
     */
    private Long convertToLong(Object obj) {
        if (obj == null) {
            return null;
        }

        if (obj instanceof String) {
            try {
                return Long.valueOf((String) obj);
            } catch (NumberFormatException e) {
                return null;
            }
        } else if (obj instanceof Long) {
            return (Long) obj;
        } else if (obj instanceof Integer) {
            return ((Integer) obj).longValue();
        }

        return null;
    }

    /**
     * 将对象转换为BigDecimal
     * @param obj 待转换的对象
     * @return BigDecimal值
     */
    private BigDecimal convertToBigDecimal(Object obj) {
        if (obj == null) {
            return BigDecimal.ZERO;
        }

        if (obj instanceof Integer) {
            return BigDecimal.valueOf((Integer) obj);
        } else if (obj instanceof Double) {
            return BigDecimal.valueOf((Double) obj);
        } else if (obj instanceof Long) {
            return BigDecimal.valueOf((Long) obj);
        } else if (obj instanceof String) {
            try {
                return new BigDecimal((String) obj);
            } catch (NumberFormatException e) {
                return BigDecimal.ZERO;
            }
        }

        // 默认返回0
        return BigDecimal.ZERO;
    }


    private Long obtainWorkFaceId(String monitorAreaName) {
        Long workFaceId = null;
        List<String> strings = splitStringByHyphen(monitorAreaName);
        if (strings.size() == 3) {
            String s = strings.get(1);
            BizWorkface bizWorkface = bizWorkfaceMapper.selectOne(new LambdaQueryWrapper<BizWorkface>()
                    .eq(BizWorkface::getWorkfaceName, s));
            if (ObjectUtil.isNotNull(bizWorkface)) {
                workFaceId = bizWorkface.getWorkfaceId();
            }
        }
        if (strings.size() == 2) {
            String s = strings.get(0);
            BizWorkface bizWorkface = bizWorkfaceMapper.selectOne(new LambdaQueryWrapper<BizWorkface>()
                    .eq(BizWorkface::getWorkfaceName, s));
            if (ObjectUtil.isNotNull(bizWorkface)) {
                workFaceId = bizWorkface.getWorkfaceId();
            }
        }
        return workFaceId;
    }

    private Long obtainTunnelId(String tunnelName, Long mineId) {
        Long tunnelId = null;
        TunnelEntity tunnelEntity = tunnelMapper.selectOne(new LambdaQueryWrapper<TunnelEntity>()
                .eq(TunnelEntity::getTunnelName, tunnelName)
                .eq(TunnelEntity::getMineId, mineId)
                .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNotNull(tunnelEntity)) {
            tunnelId = tunnelEntity.getTunnelId();
        }
        return tunnelId;
    }

    private Long obtainWorkFaceId2(String workFaceName, Long mineId) {
        Long workFaceId = null;
        BizWorkface bizWorkface = bizWorkfaceMapper.selectOne(new LambdaQueryWrapper<BizWorkface>()
                .eq(BizWorkface::getWorkfaceName, workFaceName)
                .eq(BizWorkface::getMineId, mineId)
                .eq(BizWorkface::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNotNull(bizWorkface)) {
            workFaceId = bizWorkface.getWorkfaceId();
        }
        return workFaceId;
    }


    private String obtainOriginalString(String monitorAreaName) {
        String originalString = "";
        List<String> strings = splitStringByHyphen(monitorAreaName);
        if (strings.size() == 3) {
            originalString = strings.get(1);
        }
        if (strings.size() == 2) {
            originalString = strings.get(0);
        }
        return originalString;
    }



    /**
     * 截取字符串的前三位
     * @param str 输入字符串
     * @return 前三位字符
     */
    private static String getFirstPart(String str) {
        if (str == null || str.length() < 4) {
            return str;
        }
        return str.substring(0, 3);
    }

    /**
     * 截取字符串的最后一位
     * @param str 输入字符串
     * @return 最后一位字符
     */
    private static String getLastPart(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(3);
    }

    /**
     * 按照"-"字符分割字符串并存储到List中
     * @param str 输入字符串
     * @return 分割后的字符串列表
     */
    public static List<String> splitStringByHyphen(String str) {
        if (str == null || str.isEmpty()) {
            return List.of();
        }
        // 使用split方法按"-"分割字符串
        String[] parts = str.split("-");

        // 转换为List并返回
        return Arrays.asList(parts);
    }

}