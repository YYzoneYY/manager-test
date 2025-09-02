package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.EsMapper.MeasureActualMapper;
import com.ruoyi.system.EsMapper.WarnMessageMapper;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.MultiplePlanEntity;
import com.ruoyi.system.domain.Entity.WarnHandleEntity;
import com.ruoyi.system.domain.EsEntity.MeasureActualEntity;
import com.ruoyi.system.domain.EsEntity.WarnMessageEntity;
import com.ruoyi.system.domain.dto.actual.*;
import com.ruoyi.system.domain.utils.ObtainDateUtils;
import com.ruoyi.system.domain.utils.validatePageUtils;
import com.ruoyi.system.domain.vo.WarnMessageVO;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.MultiplePlanService;
import com.ruoyi.system.service.ResponseOperateService;
import com.ruoyi.system.service.WarnHandleService;
import com.ruoyi.system.service.WarnMessageService;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.elasticsearch.client.WarningsHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: shikai
 * @date: 2025/8/18
 * @description:
 */

@Transactional
@Service
public class WarnMessageServiceImpl implements WarnMessageService {

    @Resource
    private WarnMessageMapper warnMessageMapper;

    @Resource
    private MeasureActualMapper measureActualMapper;

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
    private MultiplePlanService multiplePlanService;

    @Resource
    private MultiplePlanMapper multiplePlanMapper;

    @Resource
    private WarnHandleService warnHandleService;

    @Resource
    private WarnHandleMapper warnHandleMapper;

    @Resource
    private ResponseOperateService responseOperateService;

    @Resource
    private SysDictDataMapper sysDictDataMapper;


    @Override
    public Boolean createIndex() {
        boolean flag = false;
        flag = warnMessageMapper.createIndex();
        return flag;
    }

    @Override
    public int insertWarnMessage(WarnMessageDTO warnMessageDTO, Long mineId) {
        int flag = 0;
        WarnMessageEntity warnMessageEntity = new WarnMessageEntity();
        BeanUtils.copyProperties(warnMessageDTO, warnMessageEntity);
        String warnInstanceNum = generateWarnInstanceNum();
        warnMessageEntity.setWarnInstanceNum(warnInstanceNum);
        warnMessageEntity.setMineId(mineId);
        flag = warnMessageMapper.insert(warnMessageEntity);
        return flag;
    }

    @Override
    public int updateWarnMessage(WarnMessageDTO warnMessageDTO, Long mineId) {
        int flag = 0;

        LambdaEsQueryWrapper<WarnMessageEntity> queryWrapper = new LambdaEsQueryWrapper<>();
        queryWrapper.eq(WarnMessageEntity::getWarnInstanceNum, warnMessageDTO.getWarnInstanceNum());
        WarnMessageEntity warnMessageEntity = warnMessageMapper.selectOne(queryWrapper);

        BeanUtils.copyProperties(warnMessageDTO, warnMessageEntity);
        flag = warnMessageMapper.updateById(warnMessageEntity);
        return flag;
    }

    @Override
    public TableData warnMessagePage(WarnSelectDTO warnSelectDTO, Long mineId, Integer pageNum, Integer pageSize) {
        if (warnSelectDTO == null) {
            throw new IllegalArgumentException("参数 actualSelectDTO 不允许为空!");
        }
        EsPageInfo<WarnMessageEntity> pageInfo = queryEsPageData(warnSelectDTO, mineId, pageNum, pageSize);
        // 将WarnMessageEntity转换为WarnMessageVO
        List<WarnMessageVO> voList = pageInfo.getList().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        // 返回TableData对象，包含VO列表和总记录数
        return new TableData(voList, (int) pageInfo.getTotal());
    }

    @Override
    public WarnMessageDTO detail(String warnInstanceNum, Long mineId) {
        if (ObjectUtil.isNull(warnInstanceNum)) {
            throw new IllegalArgumentException("参数错误！警情编号不允许为空!");
        }
        LambdaEsQueryWrapper<WarnMessageEntity> queryWrapper = new LambdaEsQueryWrapper<>();
        queryWrapper.eq(WarnMessageEntity::getWarnInstanceNum, warnInstanceNum)
                .eq(ObjectUtil.isNotNull(mineId), WarnMessageEntity::getMineId, mineId);
        WarnMessageEntity warnMessageEntity = warnMessageMapper.selectOne(queryWrapper);
        if (ObjectUtil.isNull(warnMessageEntity)) {
            throw new IllegalArgumentException("此预警信息不存在！！");
        }
        WarnMessageDTO warnMessageDTO = new WarnMessageDTO();
        BeanUtils.copyProperties(warnMessageEntity, warnMessageDTO);
        String startTimeFmt = warnMessageEntity.getStartTime() == null ? null : DateUtils.getDateStrByTime(warnMessageEntity.getStartTime());
        String endTimeFmt = warnMessageEntity.getEndTime() == null ? null : DateUtils.getDateStrByTime(warnMessageEntity.getEndTime());
        warnMessageDTO.setStartTimeFmt(startTimeFmt);
        warnMessageDTO.setEndTimeFmt(endTimeFmt);
        warnMessageDTO.setMonitorItems(warnMessageEntity.getTag());
        // 已废弃
//        String monitorValue = obtainMonitorValue(warnMessageEntity);
        warnMessageDTO.setMonitorValue(String.valueOf(warnMessageEntity.getMonitoringValue()));
        // 构建预警内容，增加空值检查避免空指针异常
        String warnContent = buildWarnContent(warnMessageEntity, startTimeFmt);
        warnMessageDTO.setWarnContent(warnContent);

        List<LineChartDTO> lineChartDTOs = new ArrayList<>();
        Long warnStartTime = warnMessageEntity.getStartTime();
        Long startTime = ObtainDateUtils.getThirtyMinutesTime(warnStartTime);

        long endTime;
        if (warnMessageEntity.getWarnStatus().equals(ConstantsInfo.WARNING) ||
                warnMessageEntity.getWarnStatus().equals(ConstantsInfo.WARNING_HANDLED) ||
                warnMessageEntity.getWarnStatus().equals(ConstantsInfo.WARNING_RESPONSE)) {
            endTime = System.currentTimeMillis();
        } else if (warnMessageEntity.getWarnStatus().equals(ConstantsInfo.WARNING_END)) {
            Long currentTime = System.currentTimeMillis();
            Long thirtyMinutesAfterTime = ObtainDateUtils.getThirtyMinutesAfterTime(warnStartTime);
            boolean isOverTime = ObtainDateUtils.isOverThirtyMinutesAfterTime(currentTime, thirtyMinutesAfterTime);
            endTime = isOverTime ? currentTime : thirtyMinutesAfterTime;
        } else {
            // 其他状态不处理图表数据
            warnMessageDTO.setLineChartDTOs(lineChartDTOs);
            return warnMessageDTO;
        }

        lineChartDTOs = getLineChartData(startTime, endTime, mineId, warnMessageEntity.getMeasureNum());
        warnMessageDTO.setLineChartDTOs(lineChartDTOs);

        List<ParamAnalyzeDTO> paramAnalyzeDTOS = obtainParamAnalyzeData(warnMessageEntity.getWarnInstanceNum(), startTime, endTime, mineId);
        warnMessageDTO.setParamAnalyzeDTOs(paramAnalyzeDTOS);
        return warnMessageDTO;
    }

    private List<ParamAnalyzeDTO> obtainParamAnalyzeData(String warnInstanceNum, Long startTime, Long endTime, Long mineId) {
        List<MultiplePlanEntity> multiplePlanEntities = multiplePlanMapper.selectList(new LambdaQueryWrapper<MultiplePlanEntity>()
                .eq(MultiplePlanEntity::getWarnInstanceNum, warnInstanceNum)
                .eq(MultiplePlanEntity::getMineId, mineId));
        List<ParamAnalyzeDTO> paramAnalyzeDTOS = new ArrayList<>();
        if (ListUtils.isNotNull(multiplePlanEntities)) {
            multiplePlanEntities.forEach(multiplePlanEntity -> {
                ParamAnalyzeDTO paramAnalyzeDTO = new ParamAnalyzeDTO();
                paramAnalyzeDTO.setSensorType(multiplePlanEntity.getSensorType());
                paramAnalyzeDTO.setMeasureNum(multiplePlanEntity.getMeasureNum());
                List<LineChartDTO> lineChartDTOS = getLineChartDataT(startTime, endTime, mineId, multiplePlanEntity.getMeasureNum());
                paramAnalyzeDTO.setLineChartDTOs(lineChartDTOS);
                paramAnalyzeDTOS.add(paramAnalyzeDTO);
            });
        }
        return paramAnalyzeDTOS;
    }

    @Override
    public TableData referenceQuantityPage(String type, String keyword, Long mineId, Integer pageNum, Integer pageSize) {
        if (ObjectUtil.isNull(type)) {
            throw new IllegalArgumentException("参数错误！type 不允许为空!");
        }
        int validPageNum = validatePageUtils.validateAndSetDefaultPageNum(pageNum);
        int validPageSize = validatePageUtils.validateAndSetDefaultPageSize(pageSize);

        PageHelper.startPage(validPageNum, validPageSize);
        Page<ParameterDTO> parameterDTOS = null;

        // 根据type值查询不同的表
        switch (type) {
            case "1":
                // 工作面支架阻力
                parameterDTOS = supportResistanceMapper.selectParameterPage(keyword, mineId);
                break;
            case "2":
                // 钻孔应力
                parameterDTOS = drillingStressMapper.selectParameterPage(keyword, mineId);
                break;
            case "3":
                // 锚杆/索应力
                parameterDTOS = anchorCableStressMapper.selectParameterPage(keyword, mineId);
                break;
            case "4":
                // 顶板离层
                parameterDTOS = roofAbscissionMapper.selectParameterPage(keyword, mineId);
                break;
            case "5":
                // 巷道位移
                parameterDTOS = laneDisplacementMapper.selectParameterPage(keyword, mineId);
                break;
            case "6":
                // 电磁辐射
                parameterDTOS = elecRadiationMapper.selectParameterPage(keyword, mineId);
                break;
            default:
                throw new IllegalArgumentException("不支持的查询类型: " + type);
        }
        Page<ParameterDTO> dtoPage = getListFmt(parameterDTOS);

        // 返回统一的TableData结果
        return new TableData(dtoPage.getResult(), (int) dtoPage.getTotal());
    }

    @Override
    public boolean saveMultipleParamPlan(String warnInstanceNum, List<MultipleParamPlanDTO> multipleParamPlanDTOs, Long mineId) {
        return multiplePlanService.saveBatch(warnInstanceNum, multipleParamPlanDTOs, mineId);
    }

//    @Override
//    public boolean updateMultipleParamPlan(String warnInstanceNum, String location, List<MultipleParamPlanDTO> multipleParamPlanDTOs, Long mineId) {
//        return multiplePlanService.updateBatchById(warnInstanceNum, location, multipleParamPlanDTOs, mineId);
//    }

    @Override
    public List<MultipleParamPlanVO> obtainMultipleParamPlan(String warnInstanceNum, Long mineId) {
        return multiplePlanService.getMultipleParamPlanList(warnInstanceNum, mineId);
    }

    @Override
    public int warnHand(String warnInstanceNum, WarnHandleDTO warnHandleDTO, Long mineId) {
        int flag = 0;
        LambdaEsQueryWrapper<WarnMessageEntity> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.eq(WarnMessageEntity::getWarnInstanceNum, warnInstanceNum)
                .eq(WarnMessageEntity::getMineId, mineId);
        WarnMessageEntity warnMessageEntity = warnMessageMapper.selectOne(wrapper);
        if (ObjectUtil.isNull(warnMessageEntity)) {
            throw new RuntimeException("警情编号不存在！");
        }
        WarnMessageEntity warnMessage = new WarnMessageEntity();
        BeanUtils.copyProperties(warnMessageEntity, warnMessage);
        warnMessage.setHandStatus(warnHandleDTO.getHandStatus());
        // 处理状态为“误报警”时，警情状态改为“结束”，其他改为“处理中”
        if (warnHandleDTO.getHandStatus().equals(ConstantsInfo.FALSE_WARN)) {
            warnMessage.setWarnStatus(ConstantsInfo.WARNING_END);
            warnMessage.setEndTime(System.currentTimeMillis());
        } else {
            warnMessage.setWarnStatus(ConstantsInfo.WARNING_HANDLED);
        }
        flag = warnMessageMapper.updateById(warnMessage);
        if (flag > 0) {
            warnHandleService.addWarnHandle(warnInstanceNum, warnHandleDTO, mineId);
        } else {
            throw new RuntimeException("处理失败,请联系管理员");
        }
        return flag;
    }

    @Override
    public int ResponseOperate(String warnInstanceNum, ResponseOperateDTO responseOperateDTO, Long mineId) {
        int flag = 0;
        LambdaEsQueryWrapper<WarnMessageEntity> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.eq(WarnMessageEntity::getWarnInstanceNum, warnInstanceNum)
                .eq(WarnMessageEntity::getMineId, mineId);
        WarnMessageEntity warnMessageEntity = warnMessageMapper.selectOne(wrapper);
        if (ObjectUtil.isNull(warnMessageEntity)) {
            throw new RuntimeException("警情编号不存在！");
        }
        WarnMessageEntity warnMessage = new WarnMessageEntity();
        BeanUtils.copyProperties(warnMessageEntity, warnMessage);
        warnMessage.setWarnStatus(ConstantsInfo.WARNING_RESPONSE);
        flag = warnMessageMapper.updateById(warnMessage);
        if (flag > 0) {
            responseOperateService.addResponseOperate(warnInstanceNum, responseOperateDTO, mineId);
        } else {
            throw new RuntimeException("发布响应失败,请联系管理员");
        }
        return flag;
    }

    @Override
    public TableData singlePointWarnInfo(SingleWarnSelectDTO singleWarnSelectDTO, String measureNum, String sensorType,
                                         Long mineId, Integer pageNum, Integer pageSize) {
        if (ObjectUtil.isNull(measureNum)) {
            throw new RuntimeException("测点编码不能为空！");
        }
        if (ObjectUtil.isNull(singleWarnSelectDTO)) {
            throw new RuntimeException("参数不能为空！");
        }
        long st = 0L;
        long et = 0L;
        long currentTimeMillis = System.currentTimeMillis();
        if (ObjectUtil.isNull(singleWarnSelectDTO.getRange())) {
            st = singleWarnSelectDTO.getStartTime();
            et = singleWarnSelectDTO.getEndTime();
        } else {
            switch (singleWarnSelectDTO.getRange()) {
                case "1":
                    st = ObtainDateUtils.getCurrentZoneTime(currentTimeMillis);
                    et = ObtainDateUtils.getCurrentTwentyFourHoursTime(currentTimeMillis);
                    break;
                case "2":
                    st = ObtainDateUtils.getCurrentWeekStartTime(currentTimeMillis);
                    et = ObtainDateUtils.getCurrentWeekEndTime(currentTimeMillis);
                    break;
                case "3":
                    st = ObtainDateUtils.getCurrentMonthStartTime(currentTimeMillis);
                    et = ObtainDateUtils.getCurrentMonthEndTime(currentTimeMillis);
                    break;
                case "4":
                    st = singleWarnSelectDTO.getStartTime();
                    et = singleWarnSelectDTO.getEndTime();
                    break;
            }
        }
        EsPageInfo<WarnMessageEntity> esPageInfo = singlePageInfo(measureNum, st, et,  sensorType, mineId, pageNum, pageSize);

        List<AlarmInfoDTO> voList = esPageInfo.getList().stream()
                .map(this::convertToAlarmInfoDTO)
                .collect(Collectors.toList());
        // 返回TableData对象，包含DTO列表和总记录数
        return new TableData(voList, (int) esPageInfo.getTotal());
    }

    private EsPageInfo<WarnMessageEntity> singlePageInfo(String measureNum, Long startTime, Long endTime, String sensorType,
                                                         Long mineId, Integer pageNum, Integer pageSize) {
        // 分页参数校验与默认值设置
        int validPageNum = validatePageUtils.validateAndSetDefaultPageNum(pageNum);
        int validPageSize = validatePageUtils.validateAndSetDefaultPageSize(pageSize);

        LambdaEsQueryWrapper<WarnMessageEntity> queryWrapper = new LambdaEsQueryWrapper<>();
        queryWrapper.eq(WarnMessageEntity::getMeasureNum, measureNum)
                .eq(WarnMessageEntity::getSensorType, sensorType)
                .eq(ObjectUtil.isNotNull(mineId), WarnMessageEntity::getMineId, mineId)
                .between(ObjectUtil.isNotNull(startTime),
                        WarnMessageEntity::getStartTime,
                        startTime, endTime)
                .orderByDesc(WarnMessageEntity::getStartTime);
        return warnMessageMapper.pageQuery(queryWrapper, validPageNum, validPageSize);
    }

    private Page<ParameterDTO> getListFmt(Page<ParameterDTO> parameterDTOS) {
        if (ListUtils.isNotNull(parameterDTOS.getResult())) {
            parameterDTOS.getResult().forEach(parameterDTO -> {
                String sensorType = parameterDTO.getSensorType();
                if (sensorType != null && sensorType.equals(ConstantsInfo.DRILL_STRESS_TYPE)) {
                    parameterDTO.setMonitorItems(ConstantsInfo.DRILL_STRESS);
                } else if (sensorType != null && sensorType.equals(ConstantsInfo.ANCHOR_STRESS_TYPE)) {
                    parameterDTO.setMonitorItems(ConstantsInfo.ANCHOR_STRESS);
                } else if (sensorType != null && sensorType.equals(ConstantsInfo.ANCHOR_CABLE_STRESS_TYPE)) {
                    parameterDTO.setMonitorItems(ConstantsInfo.ANCHOR_CABLE_STRESS);
                } else if (sensorType != null && sensorType.equals(ConstantsInfo.ROOF_ABSCISSION_TYPE_TYPE)) {
                    parameterDTO.setMonitorItems(ConstantsInfo.SHALLOW_DEEP_RESISTANCE);
                } else if (sensorType != null && sensorType.equals(ConstantsInfo.LANE_DISPLACEMENT_TYPE)) {
                    parameterDTO.setMonitorItems(ConstantsInfo.LANE_DISPLACEMENT);
                } else if (sensorType != null && sensorType.equals(ConstantsInfo.ELECTROMAGNETIC_RADIATION_TYPE)) {
                    parameterDTO.setMonitorItems(ConstantsInfo.ELE_INTENSITY_PULSE);
                } else if (sensorType != null && sensorType.equals(ConstantsInfo.SUPPORT_RESISTANCE_TYPE)) {
                    parameterDTO.setMonitorItems(ConstantsInfo.SUPPORT_RESISTANCE);
                }
                parameterDTO.setWorkFaceName(getWorkFaceName(parameterDTO.getWorkFaceId()));
            });
        }
        return parameterDTOS;
    }

    /**
     * 获取折线图数据
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param mineId 矿井ID
     * @param measureNum 测点编码
     * @return 折线图数据列表
     */
    private List<LineChartDTO> getLineChartData(Long startTime, Long endTime, Long mineId, String measureNum) {
        List<LineChartDTO> lineChartDTOs = new ArrayList<>();
        LambdaEsQueryWrapper<MeasureActualEntity> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.between(MeasureActualEntity::getDataTime, startTime, endTime)
                .eq(MeasureActualEntity::getMineId, mineId)
                .eq(MeasureActualEntity::getMeasureNum, measureNum);
        List<MeasureActualEntity> measureActualEntities = measureActualMapper.selectList(wrapper);
        if (!measureActualEntities.isEmpty()) {
            measureActualEntities.forEach(measureActualEntity -> {
                LineChartDTO lineChartDTO = new LineChartDTO();
                lineChartDTO.setMonitoringValue(measureActualEntity.getMonitoringValue());
                // 已废弃
//                if (measureActualEntity.getSensorType().equals(ConstantsInfo.SUPPORT_RESISTANCE_TYPE) ||
//                        measureActualEntity.getSensorType().equals(ConstantsInfo.DRILL_STRESS_TYPE) ||
//                        measureActualEntity.getSensorType().equals(ConstantsInfo.ANCHOR_STRESS_TYPE) ||
//                        measureActualEntity.getSensorType().equals(ConstantsInfo.ANCHOR_CABLE_STRESS_TYPE)
//                        || measureActualEntity.getSensorType().equals(ConstantsInfo.LANE_DISPLACEMENT_TYPE)) {
//                    lineChartDTO.setMonitoringValue(measureActualEntity.getMonitoringValue());
//                } else if (measureActualEntity.getSensorType().equals(ConstantsInfo.ROOF_ABSCISSION_TYPE_TYPE)) {
//                    lineChartDTO.setMonitoringValue(measureActualEntity.getValueShallow());
//                    lineChartDTO.setValueDeep(measureActualEntity.getValueDeep());
//                } else if (measureActualEntity.getSensorType().equals(ConstantsInfo.ELECTROMAGNETIC_RADIATION_TYPE)) {
//                    lineChartDTO.setEleMaxValue(measureActualEntity.getEleMaxValue());
//                    lineChartDTO.setElePulse(measureActualEntity.getElePulse());
//                }
                lineChartDTO.setDataTime(measureActualEntity.getDataTime());
                lineChartDTOs.add(lineChartDTO);
            });
        }
        return lineChartDTOs;
    }


    /**
     * 获取折线图数据(多参量)
     */
    private List<LineChartDTO> getLineChartDataT(Long startTime, Long endTime, Long mineId, String measureNum) {
        List<LineChartDTO> lineChartDTOs = new ArrayList<>();
        LambdaEsQueryWrapper<MeasureActualEntity> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.between(MeasureActualEntity::getDataTime, startTime, endTime)
                .eq(MeasureActualEntity::getMineId, mineId)
                .eq(MeasureActualEntity::getMeasureNum, measureNum);
        List<MeasureActualEntity> measureActualEntities = measureActualMapper.selectList(wrapper);
        if (!measureActualEntities.isEmpty()) {
            measureActualEntities.forEach(measureActualEntity -> {
                LineChartDTO lineChartDTO = new LineChartDTO();
                if (measureActualEntity.getSensorType().equals(ConstantsInfo.SUPPORT_RESISTANCE_TYPE) ||
                        measureActualEntity.getSensorType().equals(ConstantsInfo.DRILL_STRESS_TYPE) ||
                        measureActualEntity.getSensorType().equals(ConstantsInfo.ANCHOR_STRESS_TYPE) ||
                        measureActualEntity.getSensorType().equals(ConstantsInfo.ANCHOR_CABLE_STRESS_TYPE)
                        || measureActualEntity.getSensorType().equals(ConstantsInfo.LANE_DISPLACEMENT_TYPE)) {
                    lineChartDTO.setMonitoringValue(measureActualEntity.getMonitoringValue());
                } else if (measureActualEntity.getSensorType().equals(ConstantsInfo.ROOF_ABSCISSION_TYPE_TYPE)) {
                    lineChartDTO.setValueShallow(measureActualEntity.getValueShallow());
                    lineChartDTO.setValueDeep(measureActualEntity.getValueDeep());
                } else if (measureActualEntity.getSensorType().equals(ConstantsInfo.ELECTROMAGNETIC_RADIATION_TYPE)) {
                    lineChartDTO.setEleMaxValue(measureActualEntity.getEleMaxValue());
                    lineChartDTO.setElePulse(measureActualEntity.getElePulse());
                }
                lineChartDTO.setDataTime(measureActualEntity.getDataTime());
                lineChartDTOs.add(lineChartDTO);
            });
        }
        return lineChartDTOs;
    }

    private EsPageInfo<WarnMessageEntity> queryEsPageData(WarnSelectDTO warnSelectDTO, Long mineId, Integer pageNum, Integer pageSize) {
        // 分页参数校验与默认值设置
        int validPageNum = validatePageUtils.validateAndSetDefaultPageNum(pageNum);
        int validPageSize = validatePageUtils.validateAndSetDefaultPageSize(pageSize);

        LambdaEsQueryWrapper<WarnMessageEntity> queryWrapper = new LambdaEsQueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotNull(warnSelectDTO.getWorkFaceId()), WarnMessageEntity::getWorkFaceId, warnSelectDTO.getWorkFaceId())
                .eq(StrUtil.isNotEmpty(warnSelectDTO.getWarnType()), WarnMessageEntity::getWarnType, warnSelectDTO.getWarnType())
                .eq(StrUtil.isNotEmpty(warnSelectDTO.getWarnLevel()), WarnMessageEntity::getWarnLevel, warnSelectDTO.getWarnLevel())
                .eq(StrUtil.isNotEmpty(warnSelectDTO.getWarnStatus()), WarnMessageEntity::getWarnStatus, warnSelectDTO.getWarnStatus())
                .eq(StrUtil.isNotEmpty(warnSelectDTO.getHandStatus()), WarnMessageEntity::getHandStatus, warnSelectDTO.getHandStatus())
                .eq(ObjectUtil.isNotNull(mineId), WarnMessageEntity::getMineId, mineId)
                .between(ObjectUtil.isNotNull(warnSelectDTO.getStartTime()),
                        WarnMessageEntity::getStartTime,
                        warnSelectDTO.getStartTime(),
                        warnSelectDTO.getEndTime())
                .orderByDesc(WarnMessageEntity::getStartTime);

        return warnMessageMapper.pageQuery(queryWrapper, validPageNum, validPageSize);
    }

    private WarnMessageVO convertToVO(WarnMessageEntity entity) {
        WarnMessageVO vo = new WarnMessageVO();
        // 使用BeanUtils复制同名属性
        BeanUtils.copyProperties(entity, vo);
        // 格式化时间
        String startTimeFmt = entity.getStartTime() == null ? null : DateUtils.getDateStrByTime(entity.getStartTime());
        String endTimeFmt = entity.getEndTime() == null ? null : DateUtils.getDateStrByTime(entity.getEndTime());
        vo.setStartTimeFmt(startTimeFmt);
        vo.setEndTimeFmt(endTimeFmt);

        // 已废弃
//        String monitorValue = obtainMonitorValue(entity);
//        vo.setMonitoringValue(monitorValue);
//
//        String monitorItems = obtainMonitorItems(entity.getSensorType());
//        vo.setMonitorItems(monitorItems);
        vo.setMonitorItems(entity.getTag());

        // 构建预警内容，增加空值检查避免空指针异常
        String warnContent = buildWarnContent(entity, startTimeFmt);
        vo.setWarnContent(warnContent);

        String warnStatusFmt = obtainDicLabel(ConstantsInfo.MONITOR_WARN_STATUS_DICT_TYPE, entity.getWarnStatus());
        vo.setWarnStatusFmt(warnStatusFmt);
        String warnLevelFmt = obtainDicLabel(ConstantsInfo.WARN_LEVEL_DICT_TYPE, entity.getWarnLevel());
        vo.setWarnLevelFmt(warnLevelFmt);
        String warnTypeFmt = obtainDicLabel(ConstantsInfo.WARN_TYPE_DICT_TYPE, entity.getWarnType());
        vo.setWarnTypeFmt(warnTypeFmt);
        String handStatusFmt = obtainDicLabel(ConstantsInfo.WARN_HANDLE_STATUS_DICT_TYPE, entity.getHandStatus());
        vo.setHandStatusFmt(handStatusFmt);

        // 判断是否执行应急响应
        if (entity.getWarnStatus().equals(ConstantsInfo.WARNING_HANDLED)) {
            WarnHandleEntity warnHandleEntity = warnHandleMapper.selectOne(new LambdaQueryWrapper<WarnHandleEntity>()
                    .eq(WarnHandleEntity::getWarnInstanceNum, entity.getWarnInstanceNum()));
            if (warnHandleEntity.getIsResponse().equals(ConstantsInfo.YES_RESPONSE)) {
                vo.setIsResponse(ConstantsInfo.YES_RESPONSE);
            }
        }
        return vo;
    }


    private AlarmInfoDTO convertToAlarmInfoDTO(WarnMessageEntity entity) {
        AlarmInfoDTO alarmInfoDTO = new AlarmInfoDTO();
        BeanUtils.copyProperties(entity, alarmInfoDTO);

        // 格式化时间
        String startTimeFmt = entity.getStartTime() == null ? null : DateUtils.getDateStrByTime(entity.getStartTime());
        String endTimeFmt = entity.getEndTime() == null ? null : DateUtils.getDateStrByTime(entity.getEndTime());
        alarmInfoDTO.setStartTimeFmt(startTimeFmt);
        alarmInfoDTO.setEndTimeFmt(endTimeFmt);

        // 已废弃
//        String monitorValue = obtainMonitorValue(entity);
//        alarmInfoDTO.setMonitorValue(monitorValue);

        // 构建预警内容，增加空值检查避免空指针异常
        String warnContent = buildWarnContent(entity, startTimeFmt);
        alarmInfoDTO.setWarnContent(warnContent);

        String warnLevelFmt = obtainDicLabel(ConstantsInfo.WARN_LEVEL_DICT_TYPE, entity.getWarnLevel());
        alarmInfoDTO.setWarnLevelFmt(warnLevelFmt);
        String warnTypeFmt = obtainDicLabel(ConstantsInfo.WARN_TYPE_DICT_TYPE, entity.getWarnType());
        alarmInfoDTO.setWarnTypeFmt(warnTypeFmt);
        return alarmInfoDTO;
    }


//    private String obtainMonitorItems(String sensorType) {
//        String monitorItems = "";
//        if (sensorType.equals(ConstantsInfo.DRILL_STRESS_TYPE)) {
//            monitorItems = ConstantsInfo.DRILL_STRESS;
//        } else if (sensorType.equals(ConstantsInfo.ANCHOR_STRESS_TYPE)) {
//            monitorItems = ConstantsInfo.ANCHOR_STRESS;
//        } else if (sensorType.equals(ConstantsInfo.ANCHOR_CABLE_STRESS_TYPE)) {
//            monitorItems = ConstantsInfo.ANCHOR_CABLE_STRESS;
//        } else if (sensorType.equals(ConstantsInfo.ROOF_ABSCISSION_TYPE_TYPE)) {
//            monitorItems = ConstantsInfo.SHALLOW_DEEP_RESISTANCE;
//        } else if (sensorType.equals(ConstantsInfo.LANE_DISPLACEMENT_TYPE)) {
//            monitorItems = ConstantsInfo.LANE_DISPLACEMENT;
//        } else if (sensorType.equals(ConstantsInfo.ELECTROMAGNETIC_RADIATION_TYPE)) {
//            monitorItems = ConstantsInfo.ELE_INTENSITY_PULSE;
//        } else if (sensorType.equals(ConstantsInfo.SUPPORT_RESISTANCE_TYPE)) {
//            monitorItems = ConstantsInfo.SUPPORT_RESISTANCE;
//        }
//        return monitorItems;
//    }

    /**
     * 构建预警内容
     *
     * @param entity 实体对象
     * @param startTimeFmt 格式化后的开始时间
     * @return 预警内容
     */
    private String buildWarnContent(WarnMessageEntity entity, String startTimeFmt) {
        if (entity.getMeasureNum() == null || entity.getWarnType() == null ||
                entity.getWarnLevel() == null || entity.getMonitoringValue() == null) {
            return ""; // 如果关键字段为空，返回空字符串而不是null
        }

        String warnType = obtainDicLabel(ConstantsInfo.WARN_TYPE_DICT_TYPE, entity.getWarnType());
        String warnLevel = obtainDicLabel(ConstantsInfo.WARN_LEVEL_DICT_TYPE, entity.getWarnLevel());

        StringBuilder warnContentBuilder = new StringBuilder();
        warnContentBuilder.append(entity.getMeasureNum())
                .append("测点在")
                .append(startTimeFmt == null ? "" : startTimeFmt)
                .append(",发生")
                .append(warnType)
                .append(warnLevel)
                .append("预警，值为")
                .append(entity.getMonitoringValue());

        return warnContentBuilder.toString();
    }

    /**
     * 获取字典标签
     */
    private String obtainDicLabel(String dictType, String dictValue) {
        return sysDictDataMapper.selectDictLabel(dictType, dictValue);
    }

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


//    private String obtainMonitorValue(WarnMessageEntity entity) {
//        String monitorValue = "";
//        String sensorType = entity.getSensorType();
//        if (sensorType != null && (sensorType.equals(ConstantsInfo.SUPPORT_RESISTANCE_TYPE) || sensorType.equals(ConstantsInfo.DRILL_STRESS_TYPE)
//                || sensorType.equals(ConstantsInfo.ANCHOR_STRESS_TYPE) || sensorType.equals(ConstantsInfo.ANCHOR_CABLE_STRESS_TYPE)
//                || sensorType.equals(ConstantsInfo.LANE_DISPLACEMENT_TYPE))) {
//            if (entity.getMonitoringValue() != null) {
//                monitorValue = entity.getMonitoringValue().toString();
//            }
//        } else if (Objects.equals(sensorType, ConstantsInfo.ROOF_ABSCISSION_TYPE_TYPE)) {
//            String valueShallow = entity.getValueShallow() != null ? entity.getValueShallow().toString() : "";
//            String valueDeep = entity.getValueDeep() != null ? entity.getValueDeep().toString() : "";
//            monitorValue = "浅基点:" + valueShallow + ",深基点:" + valueDeep;
//        } else if (Objects.equals(sensorType, ConstantsInfo.ELECTROMAGNETIC_RADIATION_TYPE)) {
//            String eleMaxValue = entity.getEleMaxValue() != null ? entity.getEleMaxValue().toString() : "";
//            String elePulse = entity.getElePulse() != null ? entity.getElePulse().toString() : "";
//            monitorValue = "电磁强度极大值:" + eleMaxValue + ",电磁脉冲:" + elePulse;
//        }
//        return monitorValue;
//    }


    /**
     * 生成警情编号
     * 格式: BJ + 当前时间戳（毫秒） + 四位顺序码
     * 例如: BJ17556594858900001
     * @return 警情编号
     */
    private String generateWarnInstanceNum() {
        long currentTimeMillis = System.currentTimeMillis();
        // 构造时间戳前缀
        String timePrefix = "BJ" + currentTimeMillis;
        // 查询当前时间戳下最大的警情编号
        LambdaEsQueryWrapper<WarnMessageEntity> queryWrapper = new LambdaEsQueryWrapper<>();
        queryWrapper.likeRight(WarnMessageEntity::getWarnInstanceNum, timePrefix)
                .orderByDesc(WarnMessageEntity::getWarnInstanceNum)
                .limit(1);
        WarnMessageEntity latestEntity = warnMessageMapper.selectOne(queryWrapper);
        int sequence = 1;
        if (latestEntity != null && latestEntity.getWarnInstanceNum() != null) {
            String latestNum = latestEntity.getWarnInstanceNum();
            // 确保编号长度足够，然后提取最后四位顺序码并加1
            if (latestNum.length() >= 4) {
                String sequenceStr = latestNum.substring(latestNum.length() - 4);
                try {
                    sequence = Integer.parseInt(sequenceStr) + 1;
                } catch (NumberFormatException e) {
                    sequence = 1;
                }
            }
        }
        // 如果没有找到当前时间戳下的记录，默认sequence为1，格式化后为0001
        // 格式化为4位数字，不足补0
        return timePrefix + String.format("%04d", sequence);
    }

}