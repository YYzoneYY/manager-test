package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.system.domain.Entity.*;
import com.ruoyi.system.domain.dto.DeviceCountDTO;
import com.ruoyi.system.domain.dto.QuantityDTO;
import com.ruoyi.system.domain.dto.SenSorCountDTO;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.DeviceCountService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: shikai
 * @date: 2025/8/26
 * @description:
 */

@Service
public class DeviceCountServiceImpl implements DeviceCountService {

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

    @Override
    public DeviceCountDTO obtainDeviceCount(Long mineId) {
        return drillingStressMapper.getDeviceCountStatistics(mineId);
    }

    @Override
    public List<QuantityDTO> obtainDeviceCountByType(Long mineId) {
        List<QuantityDTO> quantityDTOS = new ArrayList<>();

        // 支架阻力
        quantityDTOS.add(buildQuantityDTO("支架阻力", supportResistanceMapper, mineId));

        // 钻孔应力
        quantityDTOS.add(buildQuantityDTO("钻孔应力", drillingStressMapper, mineId));

        // 锚杆(索)应力
        quantityDTOS.add(buildQuantityDTO("锚杆(索)应力", anchorCableStressMapper, mineId));

        // 顶板离层位移
        quantityDTOS.add(buildQuantityDTO("顶板离层位移", roofAbscissionMapper, mineId));

        // 巷道位移
        quantityDTOS.add(buildQuantityDTO("巷道位移", laneDisplacementMapper, mineId));

        // 电磁辐射
        quantityDTOS.add(buildQuantityDTO("电磁辐射", elecRadiationMapper, mineId));

        return quantityDTOS;
    }

    @Override
    public List<SenSorCountDTO> obtainSenSorCount(Long mineId) {
        List<SenSorCountDTO> senSorCountDTOS = new ArrayList<>();

        Long sCount = supportResistanceMapper.selectCount(new LambdaQueryWrapper<SupportResistanceEntity>()
                .eq(SupportResistanceEntity::getMineId, mineId)
                .eq(SupportResistanceEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        SenSorCountDTO sCountDTO = new SenSorCountDTO();
        sCountDTO.setType("支架阻力");
        sCountDTO.setCount(sCount.intValue());
        senSorCountDTOS.add(sCountDTO);

        Long dCount = drillingStressMapper.selectCount(new LambdaQueryWrapper<DrillingStressEntity>()
                .eq(DrillingStressEntity::getMineId, mineId)
                .eq(DrillingStressEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        SenSorCountDTO dCountDTO = new SenSorCountDTO();
        dCountDTO.setType("钻孔应力");
        dCountDTO.setCount(dCount.intValue());
        senSorCountDTOS.add(dCountDTO);

        Long acount = anchorCableStressMapper.selectCount(new LambdaQueryWrapper<AnchorCableStressEntity>()
                .eq(AnchorCableStressEntity::getMineId, mineId)
                .eq(AnchorCableStressEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        SenSorCountDTO aCountDTO = new SenSorCountDTO();
        aCountDTO.setType("锚杆应力");
        aCountDTO.setCount(acount.intValue());
        senSorCountDTOS.add(aCountDTO);

        Long rCount = roofAbscissionMapper.selectCount(new LambdaQueryWrapper<RoofAbscissionEntity>()
                .eq(RoofAbscissionEntity::getMineId, mineId)
                .eq(RoofAbscissionEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        SenSorCountDTO rCountDTO = new SenSorCountDTO();
        rCountDTO.setType("顶板离层位移");
        rCountDTO.setCount(rCount.intValue());
        senSorCountDTOS.add(rCountDTO);

        Long lCount = laneDisplacementMapper.selectCount(new LambdaQueryWrapper<LaneDisplacementEntity>()
                .eq(LaneDisplacementEntity::getMineId, mineId)
                .eq(LaneDisplacementEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        SenSorCountDTO lCountDTO = new SenSorCountDTO();
        lCountDTO.setType("巷道位移");
        lCountDTO.setCount(lCount.intValue());

        Long eCount = elecRadiationMapper.selectCount(new LambdaQueryWrapper<ElecRadiationEntity>()
                .eq(ElecRadiationEntity::getMineId, mineId)
                .eq(ElecRadiationEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        SenSorCountDTO eCountDTO = new SenSorCountDTO();
        eCountDTO.setType("电磁辐射");
        eCountDTO.setCount(eCount.intValue());
        senSorCountDTOS.add(lCountDTO);
        return senSorCountDTOS;
    }

    /**
     * 构建设备类型统计信息
     *
     * @param type 设备类型名称
     * @param baseMapper 对应的Mapper
     * @param mineId 矿井ID
     * @return QuantityDTO
     */
    private <T> QuantityDTO buildQuantityDTO(String type, BaseMapper<T> baseMapper, Long mineId) {
        QuantityDTO quantityDTO = new QuantityDTO();
        quantityDTO.setType(type);

        // 在线数量
        int onlineCount = getDeviceCount(baseMapper, mineId, ConstantsInfo.ENABLE);

        // 离线数量
        int offlineCount = getDeviceCount(baseMapper, mineId, ConstantsInfo.DISABLE);

        quantityDTO.setOnlineNumber(onlineCount);
        quantityDTO.setOfflineNumber(offlineCount);

        return quantityDTO;
    }

    /**
     * 获取指定状态的设备数量
     *
     * @param baseMapper 对应的Mapper
     * @param mineId 矿井ID
     * @param status 状态 (ConstantsInfo.ENABLE 或 ConstantsInfo.DISABLE)
     * @param <T> 实体类型
     * @return 设备数量
     */
    private <T> int getDeviceCount(BaseMapper<T> baseMapper, Long mineId, String status) {
        // 通过反射判断具体Mapper类型并执行相应查询
        if (baseMapper instanceof SupportResistanceMapper) {
            return ((SupportResistanceMapper) baseMapper).selectCount(
                    new LambdaQueryWrapper<SupportResistanceEntity>()
                            .eq(SupportResistanceEntity::getMineId, mineId)
                            .eq(SupportResistanceEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
                            .eq(SupportResistanceEntity::getStatus, status)).intValue();
        } else if (baseMapper instanceof DrillingStressMapper) {
            return ((DrillingStressMapper) baseMapper).selectCount(
                    new LambdaQueryWrapper<DrillingStressEntity>()
                            .eq(DrillingStressEntity::getMineId, mineId)
                            .eq(DrillingStressEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
                            .eq(DrillingStressEntity::getStatus, status)).intValue();
        } else if (baseMapper instanceof AnchorCableStressMapper) {
            return ((AnchorCableStressMapper) baseMapper).selectCount(
                    new LambdaQueryWrapper<AnchorCableStressEntity>()
                            .eq(AnchorCableStressEntity::getMineId, mineId)
                            .eq(AnchorCableStressEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
                            .eq(AnchorCableStressEntity::getStatus, status)).intValue();
        } else if (baseMapper instanceof RoofAbscissionMapper) {
            return ((RoofAbscissionMapper) baseMapper).selectCount(
                    new LambdaQueryWrapper<RoofAbscissionEntity>()
                            .eq(RoofAbscissionEntity::getMineId, mineId)
                            .eq(RoofAbscissionEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
                            .eq(RoofAbscissionEntity::getStatus, status)).intValue();
        } else if (baseMapper instanceof LaneDisplacementMapper) {
            return ((LaneDisplacementMapper) baseMapper).selectCount(
                    new LambdaQueryWrapper<LaneDisplacementEntity>()
                            .eq(LaneDisplacementEntity::getMineId, mineId)
                            .eq(LaneDisplacementEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
                            .eq(LaneDisplacementEntity::getStatus, status)).intValue();
        } else if (baseMapper instanceof ElecRadiationMapper) {
            return ((ElecRadiationMapper) baseMapper).selectCount(
                    new LambdaQueryWrapper<ElecRadiationEntity>()
                            .eq(ElecRadiationEntity::getMineId, mineId)
                            .eq(ElecRadiationEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
                            .eq(ElecRadiationEntity::getStatus, status)).intValue();
        }

        return 0;
    }

}