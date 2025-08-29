package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.DrillingStressEntity;
import com.ruoyi.system.domain.Entity.SupportResistanceEntity;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.DataCollectionService;
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

    @Override
    public int dataCollectionSave(Map<String, Object> map) {
        int flag = 0;
        String type = (String) map.get("type");
        switch (type) {
            case "ZJ":
                flag = addZJ( map);
                break;
            case "ZK":
                flag = addZK( map);
                break;
        }
        return flag;
    }


    private int addZJ(Map<String, Object> map) {
        int flag = 0;
        String measureNum = (String) map.get("measureNum");
        String monitorAreaName = (String) map.get("monitorAreaName");
        String sensorType = (String) map.get("sensorType");
        String sensorNum = (String) map.get("sensorNum");
        String columnNum = getFirstPart(sensorNum);
        String columnName = getLastPart(sensorNum);
        SupportResistanceEntity supportResistanceEntity = new SupportResistanceEntity();
        supportResistanceEntity.setMeasureNum(measureNum);
        supportResistanceEntity.setSurveyAreaName(monitorAreaName);
        supportResistanceEntity.setSensorType(sensorType);
        supportResistanceEntity.setSubstationNum((String) map.get("siteNum"));
        supportResistanceEntity.setColumnNum(columnNum);
        supportResistanceEntity.setColumnName(columnName);
        supportResistanceEntity.setSensorLocation((String) map.get("sensorLocation"));
        // 使用辅助方法处理数值转换
        supportResistanceEntity.setPressureReliefValue(convertToBigDecimal(map.get("pressureNumTwo")));
        supportResistanceEntity.setSettingLoad(convertToBigDecimal(map.get("settingLoadTwo")));
        supportResistanceEntity.setWorkResistance(convertToBigDecimal(map.get("workResistanceTwo")));
        supportResistanceEntity.setXAxis((String) map.get("xaxis"));
        supportResistanceEntity.setYAxis((String) map.get("yaxis"));
        supportResistanceEntity.setZAxis((String) map.get("zaxis"));
        Object dataTimeObj = map.get("dataTime");
        if (dataTimeObj instanceof String) {
            supportResistanceEntity.setDataTime(Long.valueOf((String) dataTimeObj));
        } else if (dataTimeObj instanceof Long) {
            supportResistanceEntity.setDataTime((Long) dataTimeObj);
        }
        supportResistanceEntity.setStatus(ConstantsInfo.ENABLE);
        supportResistanceEntity.setTag(ConstantsInfo.AUTOMATIC_ACCESS);
        supportResistanceEntity.setMineId(2L);
        supportResistanceEntity.setDelFlag(ConstantsInfo.ZERO_DEL_FLAG);
        supportResistanceEntity.setCreateTime(System.currentTimeMillis());
        Long selectCount = obtainWorkFaceId(monitorAreaName);
        supportResistanceEntity.setWorkFaceId(selectCount);
        String originalString = obtainOriginalString(monitorAreaName);
        supportResistanceEntity.setOriginalWorkFaceName(originalString);
        flag = supportResistanceMapper.insert(supportResistanceEntity);
        return flag;
    }

    private int addZK(Map<String, Object> map) {
        int flag = 0;
        String measureNum = (String) map.get("measureNum");
        String monitorAreaName = (String) map.get("monitorAreaName");
        String sensorType = (String) map.get("sensorType");
        DrillingStressEntity drillingStressEntity = new DrillingStressEntity();
        drillingStressEntity.setMeasureNum(measureNum);
        drillingStressEntity.setSurveyAreaName(monitorAreaName);
        drillingStressEntity.setSensorType(sensorType);
        drillingStressEntity.setSensorLocation((String) map.get("sensorLocation"));
        Object installTimeObj = map.get("installTime");
        if (installTimeObj instanceof String) {
            drillingStressEntity.setInstallTime(Long.valueOf((String) installTimeObj));
        } else if (installTimeObj instanceof Long) {
            drillingStressEntity.setInstallTime((Long) installTimeObj);
        }
        drillingStressEntity.setInstallDepth(convertToBigDecimal(map.get("installDepth")));
        drillingStressEntity.setInstallDirection((String) map.get("detectorDirection"));
        drillingStressEntity.setInitialStress(convertToBigDecimal(map.get("initialStress")));
        drillingStressEntity.setXAxis((String) map.get("xaxis"));
        drillingStressEntity.setYAxis((String) map.get("yaxis"));
        drillingStressEntity.setZAxis((String) map.get("zaxis"));
        drillingStressEntity.setStatus(ConstantsInfo.ENABLE);
        drillingStressEntity.setTag(ConstantsInfo.AUTOMATIC_ACCESS);
        drillingStressEntity.setMineId(2L);
        drillingStressEntity.setDelFlag(ConstantsInfo.ZERO_DEL_FLAG);
        drillingStressEntity.setCreateTime(System.currentTimeMillis());
        Long selectCount = obtainWorkFaceId(monitorAreaName);
        drillingStressEntity.setWorkFaceId(selectCount);
        String originalString = obtainOriginalString(monitorAreaName);
        drillingStressEntity.setOriginalWorkFaceName(originalString);
        flag = drillingStressMapper.insert(drillingStressEntity);
        return flag;
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