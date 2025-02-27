package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.common.enums.MiningFootageEnum;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.MiningEntity;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.MiningFootageNewDTO;
import com.ruoyi.system.domain.dto.MiningSelectNewDTO;
import com.ruoyi.system.domain.dto.ShowWayChoiceListDTO;
import com.ruoyi.system.domain.dto.TunnelChoiceListDTO;
import com.ruoyi.system.domain.utils.ListPageSimple;
import com.ruoyi.system.mapper.BizWorkfaceMapper;
import com.ruoyi.system.mapper.MiningMapper;
import com.ruoyi.system.mapper.TunnelMapper;
import com.ruoyi.system.service.MiningRecordNewService;
import com.ruoyi.system.service.MiningService;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: shikai
 * @date: 2025/2/24
 * @description:
 */

@Service
@Transactional
public class MiningServiceImpl extends ServiceImpl<MiningMapper, MiningEntity> implements MiningService {

    @Resource
    private MiningMapper miningMapper;

    @Resource
    private MiningRecordNewService miningRecordService;

    @Resource
    private BizWorkfaceMapper bizWorkfaceMapper;

    @Resource
    private TunnelMapper tunnelMapper;

    private static final BigDecimal TWO = new BigDecimal("2");

    /**
     * 新增回采进尺
     * @param miningFootageNewDTO 参数实体类
     * @return 返回结果
     */
    @Override
    public int insertMiningFootage(MiningFootageNewDTO miningFootageNewDTO) {
        int flag = 0;
        if (miningFootageNewDTO.getMiningPace().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("输入的回采进尺不能或小于0");
        }
        BizWorkface bizWorkface = bizWorkfaceMapper.selectOne(new LambdaQueryWrapper<BizWorkface>()
                .eq(BizWorkface::getWorkfaceId, miningFootageNewDTO.getWorkFaceId())
                .eq(BizWorkface::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isEmpty(bizWorkface)) {
            throw new RuntimeException("选择的工作面不存在，请重新选择");
        }
        TunnelEntity tunnelEntity = tunnelMapper.selectOne(new LambdaQueryWrapper<TunnelEntity>()
                .eq(TunnelEntity::getTunnelId, miningFootageNewDTO.getTunnelId())
                .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isEmpty(tunnelEntity)) {
            throw new RuntimeException("选择的巷道不存在，请重新选择");
        }
        BigDecimal surplusTunnelTotal = surplusTunnelTotal(miningFootageNewDTO.getWorkFaceId(), miningFootageNewDTO.getTunnelId(),
                tunnelEntity.getTunnelLength(), BigDecimal.ZERO);
        if (miningFootageNewDTO.getMiningPace().compareTo(surplusTunnelTotal) > 0) {
            throw new RuntimeException("回采进尺不能大于剩余巷道长度" + surplusTunnelTotal + "米");
        }
        if (miningFootageNewDTO.getMiningPace().compareTo(BigDecimal.ZERO) == 0) {
            miningFootageNewDTO.setFlag(MiningFootageEnum.Not_FILLED_IN.getIndex());
        } else {
            miningFootageNewDTO.setFlag(MiningFootageEnum.NORMAL.getIndex());
        }
        long ts = System.currentTimeMillis();
        miningFootageNewDTO.setCreateTime(ts);
        miningFootageNewDTO.setCreateBy(SecurityUtils.getUserId());
        miningFootageNewDTO.setDelFlag(ConstantsInfo.ZERO_DEL_FLAG);
        MiningEntity miningEntity = new MiningEntity();
        BeanUtils.copyProperties(miningFootageNewDTO, miningEntity);
        flag = miningMapper.insert(miningEntity);
        if (flag <= 0) {
            throw new RuntimeException("回采进尺新增失败,请联系管理员");
        }
        return flag;
    }

    /**
     * 修改回采进尺
     * @param miningFootageNewDTO 参数实体类
     * @return 返回结果
     */
    @Override
    public boolean updateMiningFootage(MiningFootageNewDTO miningFootageNewDTO) {
        boolean flag = false;
        if (ObjectUtil.isNull(miningFootageNewDTO.getMiningFootageId())) {
            throw new RuntimeException("主键id不能为空");
        }
        MiningEntity miningEntity = miningMapper.selectOne(new LambdaQueryWrapper<MiningEntity>()
                .eq(MiningEntity::getMiningFootageId, miningFootageNewDTO.getMiningFootageId())
                .eq(MiningEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(miningEntity)) {
            throw new RuntimeException("回采进尺不存在");
        }
        TunnelEntity tunnelEntity = tunnelMapper.selectOne(new LambdaQueryWrapper<TunnelEntity>()
                .eq(TunnelEntity::getTunnelId, miningEntity.getTunnelId())
                .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        BigDecimal surplusTunnelTotal = surplusTunnelTotal(miningFootageNewDTO.getWorkFaceId(), miningFootageNewDTO.getTunnelId(),
                tunnelEntity.getTunnelLength(), BigDecimal.ZERO);
        if (miningFootageNewDTO.getMiningPaceEdit().compareTo(surplusTunnelTotal) > 0) {
            throw new RuntimeException("回采进尺不能大于剩余巷道长度" + surplusTunnelTotal + "米");
        }
        miningEntity.setMiningPace(miningFootageNewDTO.getMiningPaceEdit());
        miningEntity.setFlag(MiningFootageEnum.REVISE.getIndex());
        miningEntity.setUpdateTime(System.currentTimeMillis());
        miningEntity.setUpdateBy(SecurityUtils.getUserId());
        flag = this.updateById(miningEntity);
        if (flag) {
            miningFootageNewDTO.setFlag(MiningFootageEnum.REVISE.getIndex());
            miningFootageNewDTO.setWorkFaceId(miningEntity.getWorkFaceId());
            miningFootageNewDTO.setMiningTime(miningEntity.getMiningTime());
            Long ts = System.currentTimeMillis();
            miningFootageNewDTO.setCreateTime(ts);
            miningFootageNewDTO.setUpdateTime(ts);
            miningRecordService.insertMiningRecordNew(miningFootageNewDTO);
        }
        return flag;
    }

    /**
     * 擦除
     * @param miningFootageNewDTO 参数DTO
     * @return 返回结果
     */
    @Override
    public int clear(MiningFootageNewDTO miningFootageNewDTO) {
        int flag = 0;
        if (ObjectUtil.isNull(miningFootageNewDTO.getMiningFootageId())) {
            throw new RuntimeException("请选择数据");
        }
        MiningEntity miningEntity = miningMapper.selectOne(new LambdaQueryWrapper<MiningEntity>()
                .eq(MiningEntity::getMiningFootageId, miningFootageNewDTO.getMiningFootageId())
                .eq(MiningEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(miningEntity)) {
            throw new RuntimeException("未找到数据");
        }
        LambdaUpdateWrapper<MiningEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(MiningEntity::getMiningFootageId, miningFootageNewDTO.getMiningFootageId())
                .set(MiningEntity::getMiningPace, 0)
                .set(MiningEntity::getFlag, MiningFootageEnum.ERASE.getIndex()); //3-标识擦除
        flag = miningMapper.update(null, updateWrapper);
        if (flag > 0) {
            miningFootageNewDTO.setWorkFaceId(miningEntity.getWorkFaceId());
            miningFootageNewDTO.setTunnelId(miningEntity.getTunnelId());
            miningFootageNewDTO.setMiningTime(miningEntity.getMiningTime());
            miningFootageNewDTO.setFlag(MiningFootageEnum.ERASE.getIndex());// 3-标识擦除
            miningFootageNewDTO.setMiningPaceEdit(BigDecimal.ZERO);
            miningFootageNewDTO.setUpdateTime(System.currentTimeMillis());
            miningRecordService.insertMiningRecordNew(miningFootageNewDTO);
        }
        return flag;
    }

    /**
     * 分页查询
     * @param miningSelectNewDTO 参数DTO
     * @param pageNum 页数
     * @param pageSize 条数
     * @return 返回结果
     */
    @Override
    public TableData pageQueryList(MiningSelectNewDTO miningSelectNewDTO, String displayForm, Integer pageNum, Integer pageSize) {
        TableData result = new TableData();
        if (null == pageNum || pageNum < 1) {
            pageNum = 1;
        }
        if (null == pageSize || pageSize < 1) {
            pageSize = 10;
        }
        if (ObjectUtil.isNull(displayForm)) {
            throw new RuntimeException("展示方式不能为空,，请选择合理的展示方式");
        }
        long total = 0;
        List<?> rows = new ArrayList<>();
        if (!displayForm.equals("average")) {
            PageHelper.startPage(pageNum, pageSize);
            Page<MiningFootageNewDTO> page = miningMapper.selectMiningFootageByPage(miningSelectNewDTO, Long.valueOf(displayForm));
            if (page.getResult().isEmpty()) {
                List<Long> collect = page.getResult().stream()
                        .collect(Collectors.groupingBy(MiningFootageNewDTO::getMiningTime, Collectors.counting()))
                        .entrySet()
                        .stream()
                        .filter(entry -> entry.getValue() > 1)
                        .map(entry -> entry.getKey())
                        .collect(Collectors.toList());

                page.getResult().forEach(miningFootageNewDTO -> {
                    BigDecimal bigDecimal = miningPaceSum(miningFootageNewDTO.getTunnelId(), miningFootageNewDTO.getMiningTime());
                    miningFootageNewDTO.setMiningPaceSum(bigDecimal);
                    collect.forEach(c -> {
                        if (miningFootageNewDTO.getMiningTime().equals(c)) {
                            miningFootageNewDTO.setFlag(MiningFootageEnum.SAME_TIME.getIndex());
                        }
                    });
                });
            }
            total = page.getTotal();
            rows = page.getResult();
        } else {
            TableData list = getList(miningSelectNewDTO, pageNum, pageSize);
            total = list.getTotal();
            rows = list.getRows();
        }
        result.setTotal(total);
        result.setRows(rows);
        return result;
    }

    private TableData getList(MiningSelectNewDTO miningSelectNewDTO, Integer pageNum, Integer pageSize) {
        TableData result = new TableData();
        // 设置默认分页参数
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        try {
            PageHelper.startPage(pageNum, pageSize);
            Page<MiningFootageNewDTO> page = new Page<>();
            Page<MiningFootageNewDTO> footageNewDTOS = miningMapper.selectMining(miningSelectNewDTO);
            if (footageNewDTOS.isEmpty()) {
                result.setTotal(0);
                result.setRows(new ArrayList<>());
                return result;
            } else {
                Set<Long> tunnelIdSet = footageNewDTOS.getResult().stream()
                        .map(MiningFootageNewDTO::getTunnelId)
                        .collect(Collectors.toSet());// 将 List<Long> 转换为 Set<Long>

                Set<Long> times = footageNewDTOS.getResult().stream()
                        .map(MiningEntity::getMiningTime)
                        .collect(Collectors.toSet());

                // 批量查询所有需要的数据
                Map<Long, List<MiningEntity>> entitiesByTime = miningMapper.selectList(
                                new LambdaQueryWrapper<MiningEntity>()
                                        .in(MiningEntity::getTunnelId, tunnelIdSet) // 使用 Set<Long>
                                        .in(MiningEntity::getMiningTime, times)
                                        .eq(MiningEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
                        ).stream()
                        .collect(Collectors.groupingBy(MiningEntity::getMiningTime));
                for (Long t : times) {
                    List<MiningEntity> miningEntities = entitiesByTime.getOrDefault(t, Collections.emptyList());
                    if (!miningEntities.isEmpty()) {
                        double sum = miningEntities.stream()
                                .map(MiningEntity::getMiningPace)
                                .mapToDouble(BigDecimal::doubleValue)
                                .sum();
                        double avg = sum / miningEntities.size();
                        BigDecimal paceSum = miningEntities.stream()
                                .map(MiningEntity::getMiningPace)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                        BigDecimal paceSumAvg = paceSum.divide(TWO, 2, RoundingMode.HALF_UP);
                        MiningFootageNewDTO miningFootageNewDTO = new MiningFootageNewDTO();
                        miningFootageNewDTO.setMiningTime(t);
                        miningFootageNewDTO.setMiningPace(new BigDecimal(avg));
                        miningFootageNewDTO.setMiningPaceSum(paceSumAvg);
                        page.getResult().add(miningFootageNewDTO);
                    }
                }
                page.setTotal(times.size());
                result.setTotal(page.getTotal());
                result.setRows(page.getResult());
            }
        } catch (Exception e) {
            log.error("查询平均回采进尺失败", e);
        }
        return result;
    }



    @Override
    public String queryByTime(Long miningTime, Long tunnelId) {
        List<MiningEntity> miningEntities = miningMapper.selectList(new LambdaQueryWrapper<MiningEntity>()
                .eq(MiningEntity::getMiningTime, miningTime)
                .eq(MiningEntity::getTunnelId, tunnelId)
                .eq(MiningEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (!miningEntities.isEmpty()) {
            return MiningFootageEnum.SAME_TIME.getIndex();
        }
        return MiningFootageEnum.DIFFERENT_TIME.getIndex();
    }

    @Override
    public BigDecimal getSurplusLength(Long workFaceId, Long tunnelId) {
        Long selectCount = miningMapper.selectCount(new LambdaQueryWrapper<MiningEntity>().eq(MiningEntity::getTunnelId, tunnelId)
                .eq(MiningEntity::getWorkFaceId, workFaceId));
        if (selectCount == 0) {
            TunnelEntity tunnelEntity = tunnelMapper.selectOne(new LambdaQueryWrapper<TunnelEntity>()
                    .eq(TunnelEntity::getTunnelId, tunnelId)
                    .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ObjectUtil.isNull(tunnelEntity)) {
                throw new RuntimeException("未找到巷道信息");
            }
            return tunnelEntity.getTunnelLength();
        }
        BigDecimal excavationTotalLength = miningMapper.mineLength(workFaceId, tunnelId);
        TunnelEntity tunnelEntity = tunnelMapper.selectOne(new LambdaQueryWrapper<TunnelEntity>()
                .eq(TunnelEntity::getTunnelId, tunnelId)
                .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNotNull(tunnelEntity)) {
            BigDecimal tunnelLength = tunnelEntity.getTunnelLength(); // 巷道总长度
            BigDecimal subtract = tunnelLength.subtract(excavationTotalLength); // 剩余可掘进的巷道长度
            TunnelEntity tunnel = new TunnelEntity();
            BeanUtils.copyProperties(tunnelEntity, tunnel);
            return subtract;
        } else {
            throw new RuntimeException("未找到巷道信息");
        }
    }

    @Override
    public List<ShowWayChoiceListDTO> getShowWayChoiceList(Long workFaceId) {
        List<ShowWayChoiceListDTO> showWayChoiceListDTOS = new ArrayList<>();
        if (ObjectUtil.isNull(workFaceId)) {
            throw new RuntimeException("工作面id不能为空!!");
        }
        List<String> type = new ArrayList<>();
        type.add(ConstantsInfo.UPPER_TUNNEL);
        type.add(ConstantsInfo.BELOW_TUNNEL);
        List<TunnelEntity> tunnelEntities = tunnelMapper.selectList(new LambdaQueryWrapper<TunnelEntity>()
                .eq(TunnelEntity::getWorkFaceId, workFaceId).in(TunnelEntity::getTunnelType, type)
                .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ListUtils.isNotNull(tunnelEntities)) {
            showWayChoiceListDTOS = tunnelEntities.stream().map(tunnelEntity -> {
                ShowWayChoiceListDTO choiceListDTO = new ShowWayChoiceListDTO();
                choiceListDTO.setLabel(tunnelEntity.getTunnelName() + "进尺");
                choiceListDTO.setValue(String.valueOf(tunnelEntity.getTunnelId()));
                return choiceListDTO;
            }).collect(Collectors.toList());
        }
        ShowWayChoiceListDTO listDTO = new ShowWayChoiceListDTO();
        listDTO.setLabel("平均进尺");
        listDTO.setValue("average");
        showWayChoiceListDTOS.add(listDTO);
        return showWayChoiceListDTOS;
    }


    /**
     *按照时间查询每次开采进度之和
     * @param tunnelId 工作面id
     * @param time 时间
     * @return 返回结果
     */
    public BigDecimal miningPaceSum(Long tunnelId, Long time) {
        return miningMapper.miningPaceSum(tunnelId, time);
    }


    /**
     *
     * @param workFaceId 工作面id
     * @param tunnelId 巷道id
     * @param tunnelLength 巷道长度
     * @param currentLength 当前这条数据的源数据掘进长度
     * @return 返回结果
     */
    private BigDecimal surplusTunnelTotal(Long workFaceId, Long tunnelId, BigDecimal tunnelLength, BigDecimal currentLength) {
        // 已掘进总长度
        BigDecimal mineTotalLength = miningMapper.mineLength(workFaceId, tunnelId);
        BigDecimal subtractTunnelLength;
        // 判断巷道是否有值
        if (mineTotalLength != null) {
            subtractTunnelLength = tunnelLength.subtract(mineTotalLength).add(currentLength);
        } else {
            subtractTunnelLength = tunnelLength.subtract(currentLength);
        }
        return subtractTunnelLength;
    }
}