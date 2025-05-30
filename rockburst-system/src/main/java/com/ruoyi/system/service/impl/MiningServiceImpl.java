package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.common.enums.MiningFootageEnum;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.BizDangerArea;
import com.ruoyi.system.domain.BizTunnelBar;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.MiningEntity;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.utils.AlgorithmUtils;
import com.ruoyi.system.domain.utils.GeometryUtil;
import com.ruoyi.system.domain.utils.ListPageSimple;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.MiningRecordNewService;
import com.ruoyi.system.service.MiningService;
import com.ruoyi.system.service.TunnelService;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

    @Resource
    private BizTunnelBarMapper bizTunnelBarMapper;

    @Resource
    private SysConfigMapper sysConfigMapper;

    @Resource
    private TunnelService tunnelService;

    @Resource
    private BizDangerAreaMapper bizDangerAreaMapper;

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
            if (!page.getResult().isEmpty()) {
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
            List<MiningFootageNewDTO> aggregatedResults = new ArrayList<>();
            Long startTime = 0L;
            Long endTime = 0L;
            if (ObjectUtil.isNull(miningSelectNewDTO.getStartTime()) || ObjectUtil.isNull(miningSelectNewDTO.getEndTime())) {
                startTime =  miningMapper.selectMinMiningTime(miningSelectNewDTO.getWorkFaceId());
                endTime = miningMapper.selectMaxMiningTime(miningSelectNewDTO.getWorkFaceId());
            } else {
                startTime = miningSelectNewDTO.getStartTime();
                endTime = miningSelectNewDTO.getEndTime();
            }
            if (startTime != null && endTime != null) {
                List<TimeDTO> timeList = getTimeList(startTime, endTime);
                for (TimeDTO timeDTO : timeList) {
                    List<MiningFootageNewDTO> footageNewDTOS = miningMapper.selectMining(timeDTO.startOfDayTimestamp, timeDTO.endOfDayTimestamp,
                            miningSelectNewDTO.getPace(), miningSelectNewDTO.getWorkFaceId());

                    if (ObjectUtil.isNotEmpty(footageNewDTOS)) {
                        Set<Long> tunnelIdSet = footageNewDTOS.stream()
                                .map(MiningFootageNewDTO::getTunnelId)
                                .collect(Collectors.toSet());

                        if (!tunnelIdSet.isEmpty()) {
                            List<Long> tunnelIds = new ArrayList<>(tunnelIdSet);
                            BigDecimal num = BigDecimal.valueOf(tunnelIdSet.size());
                            BigDecimal paceSum = footageNewDTOS.stream()
                                    .map(MiningEntity::getMiningPace)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                            BigDecimal paceSumFmt = paceSum.divide(num, 2, RoundingMode.HALF_UP);  // 平均回采进尺
                            BigDecimal cumulativePace = getBatchCumulativePace(tunnelIds, timeDTO.endOfDayTimestamp);
                            BigDecimal cumulativePaceFmt  = cumulativePace.divide(num, 2, RoundingMode.HALF_UP); //平均累计回采进尺
                            MiningFootageNewDTO miningFootageNewDTO = createMiningFootageNewDTO(timeDTO.startOfDayTimestamp, paceSumFmt, cumulativePaceFmt);
                            aggregatedResults.add(miningFootageNewDTO);
                        }
                    }
                }
            } else {
                aggregatedResults.add(new MiningFootageNewDTO());
            }

            aggregatedResults.sort(Comparator.comparingLong(MiningFootageNewDTO::getMiningTime).reversed());
            int total = aggregatedResults.size();
            int fromIndex = (pageNum - 1) * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, total);
            List<MiningFootageNewDTO> pagedList = fromIndex >= total ? Collections.emptyList() : aggregatedResults.subList(fromIndex, toIndex);
            result.setTotal(total);
            result.setRows(pagedList);
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

    @Override
    public List<FootageReturnDTO> getFootageReturnDTO(Long workFaceId) {

        List<FootageReturnDTO> footageReturnDTOS = new ArrayList<>();
        List<TunnelChoiceListDTO> tunnelChoiceListTwo = tunnelService.getTunnelChoiceListTwo(workFaceId);

        if (tunnelChoiceListTwo == null || tunnelChoiceListTwo.isEmpty()) {
            return footageReturnDTOS;
        }

        // 获取比例因子
        String key = sysConfigMapper.selectOne(new LambdaQueryWrapper<SysConfig>()
                        .eq(SysConfig::getConfigKey, "bili"))
                .getConfigValue();

        for (TunnelChoiceListDTO choiceListDTO : tunnelChoiceListTwo) {
            FootageReturnDTO footageReturnDTO = new FootageReturnDTO();
            footageReturnDTO.setTunnelId(choiceListDTO.getValue());

            BizTunnelBar bizTunnelBar = bizTunnelBarMapper.selectOne(new LambdaQueryWrapper<BizTunnelBar>()
                    .eq(BizTunnelBar::getWorkfaceId, workFaceId)
                    .eq(BizTunnelBar::getTunnelId, choiceListDTO.getValue())
                    .eq(BizTunnelBar::getType, "scb")
                    .eq(BizTunnelBar::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));

            if (bizTunnelBar == null) {
                continue;
            }

            BigDecimal miningPaceSum = miningMapper.mineLength(workFaceId, choiceListDTO.getValue());

            if (miningPaceSum == null) {
                footageReturnDTOS.add(footageReturnDTO);
                continue;
            }

                // 转换
                double reverseDistance = -miningPaceSum.doubleValue();

                String endX = bizTunnelBar.getEndx();
                String endY = bizTunnelBar.getEndy();

                // 巷道走向
                Double towardAngle = bizTunnelBar.getTowardAngle();
                double angle = towardAngle + 180;
                // 比例

                // 当前累计进尺坐标
                BigDecimal[] extendedPoint = GeometryUtil.getExtendedPoint(endX, endY, angle, reverseDistance, Double.parseDouble(key));
                String footageCoordinates = extendedPoint[0] + "," + extendedPoint[1];
                footageReturnDTO.setFootageCoordinates(footageCoordinates);

                MPJLambdaWrapper<BizDangerArea> queryWrapper = new MPJLambdaWrapper<>();
                queryWrapper.eq(BizDangerArea::getWorkfaceId, workFaceId)
                        .eq(BizDangerArea::getTunnelId, choiceListDTO.getValue())
                        .eq(BizDangerArea::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG);
                List<BizDangerArea> bizDangerAreas = bizDangerAreaMapper.selectJoinList(queryWrapper);

                Long dangerAreaId = null;
                for (BizDangerArea bizDangerArea : bizDangerAreas) {
                    List<CoordinatePointDTO> coordinatePointDTOS = buildCoordinatePoints(bizDangerArea);

                    CoordinatePointDTO target = new CoordinatePointDTO(extendedPoint[0].doubleValue(), extendedPoint[1].doubleValue());

                    if (AlgorithmUtils.isPointInPolygon(coordinatePointDTOS, target)) {
                        dangerAreaId = bizDangerArea.getDangerAreaId();
                        break; // 找到后跳出循环
                    }
                }

                footageReturnDTO.setDangerAreaId(dangerAreaId); // 可能为 null 或实际值

                if (dangerAreaId != null) {
                    BizDangerArea currentArea = null;
                    for (BizDangerArea area : bizDangerAreas) {
                        if (area.getDangerAreaId().equals(dangerAreaId)) {
                            currentArea = area;
                            break;
                        }
                    }

                    if (currentArea == null) {
                        continue;
                    }

                    String scbStartX = currentArea.getScbStartx();
                    String scbStartY = currentArea.getScbStarty();
                    if (scbStartX == null || scbStartY == null) {
                        continue;
                    }

                    BigDecimal[] startPoint = new BigDecimal[] {
                            new BigDecimal(scbStartX),
                            new BigDecimal(scbStartY)
                    };
                    BigDecimal[] tunnelBarEnd = new BigDecimal[] {
                            new BigDecimal(endX),
                            new BigDecimal(endY)
                    };

                    BigDecimal spacing = GeometryUtil.calculateDistance(tunnelBarEnd, startPoint);
                    // 当前危险区剩余长度
                    BigDecimal remainingLength = spacing.subtract(miningPaceSum);
                    footageReturnDTO.setCurrentRemainingLength(remainingLength.doubleValue());

                    Integer no = currentArea.getNo();
                    if (no == null || no <= 1) {
                        footageReturnDTOS.add(footageReturnDTO);
                        continue;
                    }
                    // 前一个危险区
                    BizDangerArea frontDangerArea = bizDangerAreaMapper.selectOne(new LambdaQueryWrapper<BizDangerArea>()
                            .eq(BizDangerArea::getWorkfaceId, workFaceId)
                            .eq(BizDangerArea::getTunnelId, choiceListDTO.getValue())
                            .eq(BizDangerArea::getNo, no - 1)
                            .eq(BizDangerArea::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));

                    if (frontDangerArea == null) {
                        footageReturnDTOS.add(footageReturnDTO);
                        continue;
                    }
                    // 前一个危险区结束坐标
                    BigDecimal[] frontEndPoint = new BigDecimal[]{
                            new BigDecimal(frontDangerArea.getScbEndx()),
                            new BigDecimal(frontDangerArea.getScbEndy())
                    };
                    // 当前危险区与前一个危险区之间的距离
                    BigDecimal calculated = GeometryUtil.calculateDistance(startPoint, frontEndPoint);
                    // 距下一个危险区的距离
                    BigDecimal distance = remainingLength.add(calculated);
                    footageReturnDTO.setNextDangerAreaDistance(distance.doubleValue());
                    footageReturnDTOS.add(footageReturnDTO);
                }
        }
        return footageReturnDTOS;
    }

    @Override
    public int initData(Long workFaceId, Long tunnelId, Long time, BigDecimal sumPace) {
        int flag = 0;
        MiningEntity miningEntity = new MiningEntity();
        miningEntity.setWorkFaceId(workFaceId);
        miningEntity.setTunnelId(tunnelId);
        miningEntity.setMiningTime(time);
        miningEntity.setMiningPace(sumPace);
        miningEntity.setFlag(MiningFootageEnum.NORMAL.getIndex());
        miningEntity.setDelFlag(ConstantsInfo.ZERO_DEL_FLAG);
        flag = miningMapper.insert(miningEntity);
        return flag;
    }

    private static List<CoordinatePointDTO> buildCoordinatePoints(BizDangerArea area) {
        List<CoordinatePointDTO> points = new ArrayList<>();
        points.add(new CoordinatePointDTO(Double.parseDouble(area.getScbStartx()), Double.parseDouble(area.getScbStarty())));
        points.add(new CoordinatePointDTO(Double.parseDouble(area.getScbEndx()), Double.parseDouble(area.getScbEndy())));
        points.add(new CoordinatePointDTO(Double.parseDouble(area.getFscbStartx()), Double.parseDouble(area.getFscbStarty())));
        points.add(new CoordinatePointDTO(Double.parseDouble(area.getFscbEndx()), Double.parseDouble(area.getFscbEndy())));
        return points;
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


    /**
     * 通过一段时间获取其包含的日期
     */
    private List<TimeDTO> getTimeList(Long startTime, Long endTime) {
        ZonedDateTime startDate = Instant.ofEpochMilli(startTime).atZone(ZoneId.systemDefault());
        ZonedDateTime endDate = Instant.ofEpochMilli(endTime).atZone(ZoneId.systemDefault());
        // 获取开始和结束日期的LocalDate
        LocalDate startDateLocal = startDate.toLocalDate();
        LocalDate endDateLocal = endDate.toLocalDate();
        List<TimeDTO> timeList = new ArrayList<>();
        // 遍历日期范围并输出每一天的开始和结束时间戳
        for (LocalDate date = startDateLocal; !date.isAfter(endDateLocal); date = date.plusDays(1)) {
            ZonedDateTime startOfDay = date.atStartOfDay(ZoneId.systemDefault());
            ZonedDateTime endOfDay = date.atTime(23, 59, 59, 999999999).atZone(ZoneId.systemDefault());
            long startOfDayTimestamp = startOfDay.toInstant().toEpochMilli();
            long endOfDayTimestamp = endOfDay.toInstant().toEpochMilli();
            TimeDTO timeDTO = new TimeDTO();
            timeDTO.setStartOfDayTimestamp(startOfDayTimestamp);
            timeDTO.setEndOfDayTimestamp(endOfDayTimestamp);
            timeList.add(timeDTO);
        }
        return timeList;
    }

    /**
     * 根据工作面查询改工作面下所有巷道累计回采进尺之和
     */
    private BigDecimal getBatchCumulativePace(List<Long> tunnelIds, long endOfDayTimestamp) {
        return miningMapper.miningPaceSumT(tunnelIds, endOfDayTimestamp);
    }

    /**
     * 组装 MiningFootageNewDTO
     */
    private MiningFootageNewDTO createMiningFootageNewDTO(long miningTime, BigDecimal miningPace, BigDecimal miningPaceSum) {
        MiningFootageNewDTO dto = new MiningFootageNewDTO();
        dto.setMiningTime(miningTime);
        dto.setMiningPace(miningPace);
        dto.setMiningPaceSum(miningPaceSum);
        return dto;
    }
}