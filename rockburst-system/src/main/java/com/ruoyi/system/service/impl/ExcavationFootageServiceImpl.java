package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.common.enums.MiningFootageEnum;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.Entity.ExcavationFootageEntity;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.ExcavationFootageDTO;
import com.ruoyi.system.domain.dto.ExcavationSelectDTO;
import com.ruoyi.system.mapper.ExcavationFootageMapper;
import com.ruoyi.system.mapper.ExcavationRecordMapper;
import com.ruoyi.system.mapper.TunnelMapper;
import com.ruoyi.system.service.ExcavationFootageService;
import com.ruoyi.system.service.ExcavationRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: shikai
 * @date: 2024/12/23
 * @description:
 */

@Service
public class ExcavationFootageServiceImpl extends ServiceImpl<ExcavationFootageMapper, ExcavationFootageEntity> implements ExcavationFootageService {

    @Resource
    private ExcavationFootageMapper excavationFootageMapper;

    @Resource
    private TunnelMapper tunnelMapper;

    @Resource
    private ExcavationRecordService excavationRecordService;

    /**
     * 新增掘进进尺
     * @param excavationFootageDTO 参数DTO
     * @return 返回结果
     */
    @Override
    public ExcavationFootageDTO insertExcavationFootage(ExcavationFootageDTO excavationFootageDTO) {
        if (excavationFootageDTO.getExcavationPace().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("输入的掘进进尺不能小于0");
        }
        TunnelEntity tunnelEntity = tunnelMapper.selectOne(new LambdaQueryWrapper<TunnelEntity>()
                .eq(TunnelEntity::getTunnelId, excavationFootageDTO.getTunnelId())
                .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(tunnelEntity)) {
            throw new RuntimeException("选择的巷道不存在，请重新选择");
        }
        BigDecimal surplusTunnelTotal = surplusTunnelTotal(excavationFootageDTO.getTunnelId(), tunnelEntity.getTunnelLength(), BigDecimal.ZERO);
        if (excavationFootageDTO.getExcavationPace().compareTo(surplusTunnelTotal) > 0) {
            throw new RuntimeException("掘进进尺不能大于剩余巷道长度" + surplusTunnelTotal + "米");
        }
        if (excavationFootageDTO.getExcavationPace().compareTo(BigDecimal.ZERO) == 0) {
            excavationFootageDTO.setFlag(MiningFootageEnum.Not_FILLED_IN.getIndex());
        } else {
            excavationFootageDTO.setFlag(MiningFootageEnum.NORMAL.getIndex());
        }
        long ts = System.currentTimeMillis();
        excavationFootageDTO.setCreateTime(ts);
        excavationFootageDTO.setCreateBy(1L);
        excavationFootageDTO.setDelFlag(ConstantsInfo.ZERO_DEL_FLAG);
        ExcavationFootageEntity excavationFootageEntity = new ExcavationFootageEntity();
        BeanUtils.copyProperties(excavationFootageDTO,excavationFootageEntity);
        int insert = excavationFootageMapper.insert(excavationFootageEntity);
        if (insert > 0) {
            BeanUtils.copyProperties(excavationFootageEntity,excavationFootageDTO);
        }
        return excavationFootageDTO;
    }

    /**
     * 修改掘进进尺
     * @param excavationFootageDTO 参数DTO
     * @return 返回结果
     */
    @Override
    public ExcavationFootageEntity updateExcavationFootage(ExcavationFootageDTO excavationFootageDTO) {
        if (ObjectUtil.isNull(excavationFootageDTO.getExcavationFootageId())) {
            throw new RuntimeException("掘进进尺id不能为空");
        }
        ExcavationFootageEntity excavationFootageEntity = excavationFootageMapper.selectOne(new LambdaQueryWrapper<ExcavationFootageEntity>()
                .eq(ExcavationFootageEntity::getExcavationFootageId, excavationFootageDTO.getExcavationFootageId())
                .eq(ExcavationFootageEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(excavationFootageEntity)) {
            throw new RuntimeException("掘进进尺不存在");
        }
        TunnelEntity tunnelEntity = tunnelMapper.selectOne(new LambdaQueryWrapper<TunnelEntity>()
                .eq(TunnelEntity::getTunnelId, excavationFootageEntity.getTunnelId())
                .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        BigDecimal surplusTunnelTotal = surplusTunnelTotal(excavationFootageDTO.getTunnelId(), tunnelEntity.getTunnelLength(), BigDecimal.ZERO);
        if (excavationFootageDTO.getExcavationPace().compareTo(surplusTunnelTotal) > 0) {
            throw new RuntimeException("掘进进尺不能大于剩余巷道长度" + surplusTunnelTotal + "米");
        }
        excavationFootageEntity.setExcavationPace(excavationFootageDTO.getExcavationPaceEdit());
        excavationFootageEntity.setFlag(MiningFootageEnum.REVISE.getIndex());
        excavationFootageEntity.setUpdateTime(System.currentTimeMillis());
        // TODO 后续需要修改
        excavationFootageEntity.setUpdateBy(1L);
        excavationFootageMapper.updateById(excavationFootageEntity);
        boolean b = this.updateById(excavationFootageEntity);
        if (b) {
            excavationFootageDTO.setFlag(MiningFootageEnum.REVISE.getIndex());
            excavationFootageDTO.setTunnelId(excavationFootageEntity.getTunnelId());
            excavationFootageDTO.setExcavationTime(excavationFootageEntity.getExcavationTime());
            Long ts = System.currentTimeMillis();
            excavationFootageDTO.setCreateTime(ts);
            excavationFootageDTO.setCreateBy(1L);
            excavationFootageDTO.setUpdateTime(ts);
            excavationFootageDTO.setUpdateBy(1L);
            excavationRecordService.insertExcavationRecord(excavationFootageDTO);
        }
        return excavationFootageDTO;
    }

    /**
     * 擦除
     * @param excavationFootageDTO 参数DTO
     * @return 返回结果
     */
    @Override
    public int clear(ExcavationFootageDTO excavationFootageDTO) {
        if (ObjectUtil.isNull(excavationFootageDTO.getExcavationFootageId())) {
            throw new RuntimeException("请选择数据");
        }
        ExcavationFootageEntity excavationFootageEntity = excavationFootageMapper.selectOne(new LambdaQueryWrapper<ExcavationFootageEntity>()
                .eq(ExcavationFootageEntity::getExcavationFootageId, excavationFootageDTO.getExcavationFootageId())
                .eq(ExcavationFootageEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(excavationFootageEntity)) {
            throw new RuntimeException("未找到数据");
        }
        LambdaUpdateWrapper<ExcavationFootageEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ExcavationFootageEntity::getExcavationFootageId, excavationFootageDTO.getExcavationFootageId())
                .set(ExcavationFootageEntity::getExcavationPace, 0)
                .set(ExcavationFootageEntity::getFlag, MiningFootageEnum.ERASE.getIndex());
        int update = excavationFootageMapper.update(null, updateWrapper);
        if (update > 0) {
            excavationFootageDTO.setTunnelId(excavationFootageEntity.getTunnelId());
            excavationFootageDTO.setExcavationTime(excavationFootageEntity.getExcavationTime());
            excavationFootageDTO.setFlag(MiningFootageEnum.ERASE.getIndex());
            excavationFootageDTO.setExcavationPaceEdit(BigDecimal.ZERO);
            excavationFootageDTO.setUpdateTime(System.currentTimeMillis());
            excavationRecordService.insertExcavationRecord(excavationFootageDTO);
        }
        return update;
    }

    /**
     * 分页查询
     * @param excavationSelectDTO 参数DTO
     * @param pageNum 当前页
     * @param pageSize 每页显示条数
     * @return 返回结果
     */
    @Override
    public TableData pageQueryList(ExcavationSelectDTO excavationSelectDTO, Integer pageNum, Integer pageSize) {
        TableData result = new TableData();
        if (null == pageNum || pageNum < 1) {
            pageNum = 1;
        }
        if (null == pageSize || pageSize < 1) {
            pageSize = 10;
        }
        List<ExcavationFootageEntity> excavationFootageEntities = excavationFootageMapper.selectList(new LambdaQueryWrapper<ExcavationFootageEntity>()
                .eq(ExcavationFootageEntity::getTunnelId, excavationSelectDTO.getTunnelId()));
        if (excavationFootageEntities.isEmpty()) {
            return null;
        }
        PageHelper.startPage(pageNum, pageSize);
        Page<ExcavationFootageDTO> excavationFootageDTOPage = excavationFootageMapper.selectQueryPage(excavationSelectDTO);
        if (ListUtils.isNotNull(excavationFootageDTOPage.getResult())) {
            List<Long> collect = excavationFootageDTOPage.getResult().stream().collect(Collectors.groupingBy(ExcavationFootageEntity::getExcavationTime, Collectors.counting()))
                    .entrySet().stream()
                    .filter(entry -> entry.getValue() > 1)
                    .map(entry -> entry.getKey())
                    .collect(Collectors.toList());

            excavationFootageDTOPage.getResult().forEach(excavationFootageDTO -> {
                BigDecimal bigDecimal = excavationPaceSum(excavationFootageDTO.getTunnelId(), excavationFootageDTO.getExcavationTime());
                excavationFootageDTO.setExcavationPaceSum(bigDecimal);
                collect.forEach(c -> {
                    if (excavationFootageDTO.getExcavationTime().equals(c)) {
                        excavationFootageDTO.setFlag(MiningFootageEnum.SAME_TIME.getIndex());
                    }
                });
            });
        }
        result.setTotal(excavationFootageDTOPage.getTotal());
        result.setRows(excavationFootageDTOPage.getResult());
        return result;
    }

    /**
     * 查询时间相同的数据
     * @param excavationTime 掘进时间
     * @param tunnelId 巷道id
     * @return 返回结果
     */
    @Override
    public String queryByTime(Long excavationTime, Long tunnelId) {
        List<ExcavationFootageEntity> excavationFootageEntities = excavationFootageMapper.selectList(new LambdaQueryWrapper<ExcavationFootageEntity>()
                .eq(ExcavationFootageEntity::getExcavationTime, excavationTime)
                .eq(ExcavationFootageEntity::getTunnelId, tunnelId)
                .eq(ExcavationFootageEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (!excavationFootageEntities.isEmpty()) {
            return MiningFootageEnum.SAME_TIME.getIndex();
        }
        return MiningFootageEnum.DIFFERENT_TIME.getIndex();
    }

    @Override
    public BigDecimal getSurplusLength(Long tunnelId) {
        Long selectCount = excavationFootageMapper.selectCount(new LambdaQueryWrapper<ExcavationFootageEntity>()
                .eq(ExcavationFootageEntity::getTunnelId, tunnelId));
        if (selectCount == 0) {
            TunnelEntity tunnelEntity = tunnelMapper.selectOne(new LambdaQueryWrapper<TunnelEntity>()
                    .eq(TunnelEntity::getTunnelId, tunnelId)
                    .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ObjectUtil.isNull(tunnelEntity)) {
                throw new RuntimeException("未找到巷道信息");
            }
            return tunnelEntity.getTunnelLength();
        }
        BigDecimal excavationTotalLength = excavationFootageMapper.excavationLength(tunnelId);
        TunnelEntity tunnelEntity = tunnelMapper.selectOne(new LambdaQueryWrapper<TunnelEntity>()
                .eq(TunnelEntity::getTunnelId, tunnelId)
                .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNotEmpty(tunnelEntity)) {
            BigDecimal tunnelLength = tunnelEntity.getTunnelLength(); // 巷道总长度
            BigDecimal subtract = tunnelLength.subtract(excavationTotalLength); // 剩余可掘进的巷道长度

            TunnelEntity tunnel = new TunnelEntity();
            BeanUtils.copyProperties(tunnelEntity, tunnel);
//            if (subtract.equals(BigDecimal.ZERO) || subtract.compareTo(BigDecimal.ZERO) < 0) {
//                tunnel.setStatus(TunnelStatusEnum.kcw.getCode());
//            } else {
//                tunnel.setStatus(TunnelStatusEnum.kcz.getCode());
//            }
            return subtract;
        } else {
            throw new RuntimeException("未找到巷道信息");
        }
    }

    /**
     *按照时间查询每次掘进进度之和
     * @param tunnelId 巷道id
     * @param time 时间
     * @return 返回结果
     */
    public BigDecimal excavationPaceSum(Long tunnelId, Long time) {
        return excavationFootageMapper.excavationPaceSum(tunnelId, time);
    }

    /**
     *
     * @param tunnelId 巷道id
     * @param tunnelLength 巷道长度
     * @param currentLength 当前这条数据的源数据掘进长度
     * @return 返回结果
     */
    private BigDecimal surplusTunnelTotal(Long tunnelId, BigDecimal tunnelLength, BigDecimal currentLength) {
        // 已掘进总长度
        BigDecimal excavationTotalLength = excavationFootageMapper.excavationLength(tunnelId);
        BigDecimal subtractTunnelLength;
        // 判断巷道是否有值
        if (excavationTotalLength != null) {
            subtractTunnelLength = tunnelLength.subtract(excavationTotalLength).add(currentLength);
        } else {
            subtractTunnelLength = tunnelLength.subtract(currentLength);
        }
        return subtractTunnelLength;
    }
}