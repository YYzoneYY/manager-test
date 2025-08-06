package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.system.domain.BizDrillRecord;
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.ClassesEntity;
import com.ruoyi.system.domain.Entity.ConstructionPersonnelEntity;
import com.ruoyi.system.domain.Entity.ConstructionUnitEntity;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.PressureHoleImportDTO;
import com.ruoyi.system.domain.dto.ReportFormsDTO;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.PressureHoleFormsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: shikai
 * @date: 2025/2/11
 * @description:
 */

@Service
public class PressureHoleFormsServiceImpl implements PressureHoleFormsService {

    @Resource
    private BizProjectRecordMapper bizProjectRecordMapper;

    @Resource
    private BizDrillRecordMapper bizDrillRecordMapper;

    @Resource
    private SysDictDataMapper sysDictDataMapper;

    @Resource
    private ConstructionPersonnelMapper constructionPersonnelMapper;

    @Resource
    private ConstructionUnitMapper constructionUnitMapper;

    @Resource
    private ClassesMapper classesMapper;

    @Resource
    private TunnelMapper tunnelMapper;

    @Resource
    private BizWorkfaceMapper bizWorkfaceMapper;

    /**
     * 根据时间导出卸压孔报表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 返回结果
     */
    @Override
    public List<PressureHoleImportDTO> ExportPressureHoleForms(Date startTime, Date endTime) {
        List<PressureHoleImportDTO> importDTOList = new ArrayList<>();
        if (ObjectUtil.isNull(startTime) || ObjectUtil.isNull(endTime)) {
            throw new RuntimeException("开始时间或结束时间不能为空!!");
        }
        List<String> collect = sysDictDataMapper.selectDictDataByType(ConstantsInfo.DRILL_TYPE_DICT_TYPE)
                .stream().map(SysDictData::getDictValue)
                .collect(Collectors.toList());
        collect.removeIf(c -> c.equals("CD"));
        List<BizProjectRecord> bizProjectRecords = bizProjectRecordMapper.selectList(new LambdaQueryWrapper<BizProjectRecord>()
                .in(BizProjectRecord::getDrillType, collect)
                .between(BizProjectRecord::getConstructTime, startTime, endTime)
                .orderByDesc(BizProjectRecord::getConstructTime));
        bizProjectRecords.forEach(bizProjectRecord -> {
            String constructionPersonnel = "";
            String inspector = "";
            PressureHoleImportDTO pressureHoleImportDTO = new PressureHoleImportDTO();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            pressureHoleImportDTO.setConstructTime(dateFormat.format(bizProjectRecord.getConstructTime()));
            pressureHoleImportDTO.setDrillHoleType(sysDictDataMapper.selectDictLabel(ConstantsInfo.DRILL_TYPE_DICT_TYPE, bizProjectRecord.getDrillType())); // 钻孔类型
            ConstructionUnitEntity constructionUnitEntity = constructionUnitMapper.selectOne(new LambdaQueryWrapper<ConstructionUnitEntity>()
                    .eq(ConstructionUnitEntity::getConstructionUnitId, bizProjectRecord.getConstructUnitId())
                    .eq(ConstructionUnitEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ObjectUtil.isNull(constructionUnitEntity)) {
                throw new RuntimeException("未找到施工单位信息,无法进行导出");
            } else {
                pressureHoleImportDTO.setConstructUnit(constructionUnitEntity.getConstructionUnitName());
            }
            ClassesEntity classesEntity = classesMapper.selectOne(new LambdaQueryWrapper<ClassesEntity>()
                    .eq(ClassesEntity::getClassesId, bizProjectRecord.getConstructShiftId())
                    .eq(ClassesEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ObjectUtil.isNull(classesEntity)) {
                throw new RuntimeException("未找到班次信息,无法进行导出");
            }
            pressureHoleImportDTO.setConstructShift(classesEntity.getClassesName());
            pressureHoleImportDTO.setConstructType(sysDictDataMapper.selectDictLabel(ConstantsInfo.TYPE_DICT_TYPE, bizProjectRecord.getConstructType())); // 获取施工类型
            pressureHoleImportDTO.setLocation(getLocation(bizProjectRecord.getConstructType(), bizProjectRecord.getLocationId()));
            BizDrillRecord bizDrillRecord = bizDrillRecordMapper.selectOne(new LambdaQueryWrapper<BizDrillRecord>()
                    .eq(BizDrillRecord::getProjectId, bizProjectRecord.getProjectId())
                    .eq(BizDrillRecord::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ObjectUtil.isNull(bizDrillRecord)) {
                throw new RuntimeException("未找到钻孔信息,无法进行导出");
            }
            if (ObjectUtil.isNull(bizProjectRecord.getDrillNum())) {
                pressureHoleImportDTO.setDrillNum("");
            } else {
                pressureHoleImportDTO.setDrillNum(bizProjectRecord.getDrillNum());
            }
            if (ObjectUtil.isNull(bizDrillRecord.getHeight())) {
                pressureHoleImportDTO.setHeight("");
            } else {
                pressureHoleImportDTO.setHeight(String.valueOf(bizDrillRecord.getHeight()));
            }
            if (ObjectUtil.isNull(bizDrillRecord.getRealDeep())) {
                pressureHoleImportDTO.setRealDeep("");
            } else {
                pressureHoleImportDTO.setRealDeep(String.valueOf(bizDrillRecord.getRealDeep()));
            }
            if (ObjectUtil.isNull(bizDrillRecord.getDiameter())) {
                pressureHoleImportDTO.setDiameter("");
            } else {
                pressureHoleImportDTO.setDiameter(String.valueOf(bizDrillRecord.getDiameter()));
            }
            if (ObjectUtil.isNotNull(bizProjectRecord.getWorker())){
                constructionPersonnel = getPersonalName("1", bizProjectRecord.getWorker());
            }
            pressureHoleImportDTO.setWorker(constructionPersonnel); // 获取施工人员名称
            if (ObjectUtil.isNotNull(bizProjectRecord.getAccepter())) {
                inspector = getPersonalName("2", bizProjectRecord.getAccepter());
            }
            pressureHoleImportDTO.setAccepter(inspector); // 获取验收人员名称
            String d = sysDictDataMapper.selectDictLabel(ConstantsInfo.DRILL_DEVICE_DICT_TYPE, bizDrillRecord.getBorer()); // 获取钻孔设备(施工工具)
            pressureHoleImportDTO.setBorer(d);
            importDTOList.add(pressureHoleImportDTO);
        });
        return importDTOList;
    }

    /**
     * 分页查询卸压孔报表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 返回结果
     */
    @Override
    public TableData queryPage(Date startTime, Date endTime, Integer pageNum, Integer pageSize) {
        TableData result = new TableData();
        if (null == pageNum || pageNum < 1) {
            pageNum = 1;
        }
        if (null == pageSize || pageSize < 1) {
            pageSize = 10;
        }
        List<String> drillTypes = sysDictDataMapper.selectDictDataByType(ConstantsInfo.DRILL_TYPE_DICT_TYPE)
                .stream().map(SysDictData::getDictValue)
                .collect(Collectors.toList());
        drillTypes.removeIf(c -> c.equals("CD"));
        PageHelper.startPage(pageNum, pageSize);
        Page<ReportFormsDTO> page = new Page<>();
        try {
             page = bizProjectRecordMapper.queryDateByPage(startTime, endTime, drillTypes);

        }catch (Exception e){
            e.printStackTrace();
        }
        if (ListUtils.isNotNull(page.getResult())) {
            page.getResult().forEach(reportFormsDTO -> {
                String constructionPersonnel = "";
                String inspector = "";
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                reportFormsDTO.setConstructTimeFmt(dateFormat.format(reportFormsDTO.getConstructTime()));
                reportFormsDTO.setDrillHoleTypeFmt(sysDictDataMapper.selectDictLabel(ConstantsInfo.DRILL_TYPE_DICT_TYPE, reportFormsDTO.getDrillType())); // 钻孔类型
                reportFormsDTO.setConstructUnitFmt(getUnitName(reportFormsDTO.getConstructUnitId())); // 施工单位
                reportFormsDTO.setConstructShiftFmt(getClassesName(reportFormsDTO.getConstructShiftId())); // 班次
                reportFormsDTO.setConstructTypeFmt(sysDictDataMapper.selectDictLabel(ConstantsInfo.TYPE_DICT_TYPE, reportFormsDTO.getConstructType())); // 施工类型
                reportFormsDTO.setLocationFmt(getLocationT(reportFormsDTO.getConstructType(), reportFormsDTO.getLocationId()));
                // 钻孔信息
                BizDrillRecord bizDrillRecord = bizDrillRecordMapper.selectOne(new LambdaQueryWrapper<BizDrillRecord>()
                        .eq(BizDrillRecord::getProjectId, reportFormsDTO.getProjectId())
                        .eq(BizDrillRecord::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
                if (ObjectUtil.isNotNull(bizDrillRecord)) {
                    reportFormsDTO.setHeight(bizDrillRecord.getHeight());
                    reportFormsDTO.setRealDeep(bizDrillRecord.getRealDeep());
                    reportFormsDTO.setDiameter(bizDrillRecord.getDiameter());
                }
                if (ObjectUtil.isNotNull(reportFormsDTO.getWorker())){
                    constructionPersonnel = getPersonalNameT("1", reportFormsDTO.getWorker());
                }
                reportFormsDTO.setWorkerFmt(constructionPersonnel); // 施工人员名称
                if (ObjectUtil.isNotNull(reportFormsDTO.getAccepter())) {
                    inspector = getPersonalNameT("2", reportFormsDTO.getAccepter());
                }
                reportFormsDTO.setAccepterFmt(inspector); // 验收人员名称
                reportFormsDTO.setBorer(sysDictDataMapper.selectDictLabel(ConstantsInfo.DRILL_DEVICE_DICT_TYPE, bizDrillRecord.getBorer())); // 钻孔设备(施工工具)
            });
        }
        result.setTotal(page.getTotal());
        result.setRows(page.getResult());
        return result;
    }

    /**
     * 获取施工单位名称
     */
    private String getUnitName(Long constructUnitId) {
        String unitName = "";
        ConstructionUnitEntity constructionUnitEntity = constructionUnitMapper.selectOne(new LambdaQueryWrapper<ConstructionUnitEntity>()
                .eq(ConstructionUnitEntity::getConstructionUnitId, constructUnitId)
                .eq(ConstructionUnitEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(constructionUnitEntity)) {
            return unitName;
        }
        unitName = constructionUnitEntity.getConstructionUnitName();
        return unitName;
    }

    /**
     * 获取班次名称
     */
    private String getClassesName(Long classesId) {
        String classesName = "";
        ClassesEntity classesEntity = classesMapper.selectOne(new LambdaQueryWrapper<ClassesEntity>()
                .eq(ClassesEntity::getClassesId, classesId)
                .eq(ClassesEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(classesEntity)) {
            return classesName;
        }
        classesName = classesEntity.getClassesName();
        return classesName;
    }

    private String getPersonalName(String tag, Long personalId) {
        String personalName = "";
        if (tag.equals(ConstantsInfo.CONSTRUCTION_WORKER)) {
            ConstructionPersonnelEntity constructionPersonnelEntity = constructionPersonnelMapper.selectOne(new LambdaQueryWrapper<ConstructionPersonnelEntity>()
                    .eq(ConstructionPersonnelEntity::getConstructionPersonnelId, personalId)
                    .eq(ConstructionPersonnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ObjectUtil.isNull(constructionPersonnelEntity)) {
                throw new RuntimeException("施工人员转化异常，导出失败！");
            }
            personalName = constructionPersonnelEntity.getName();
        }
        if (tag.equals(ConstantsInfo.INSPECTOR)) {
            if (ObjectUtil.isNotNull(personalId)) {
                ConstructionPersonnelEntity constructionPersonnelEntity = constructionPersonnelMapper.selectOne(new LambdaQueryWrapper<ConstructionPersonnelEntity>()
                        .eq(ConstructionPersonnelEntity::getConstructionPersonnelId, personalId)
                        .eq(ConstructionPersonnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
                if (ObjectUtil.isNull(constructionPersonnelEntity)) {
                    throw new RuntimeException("验收员转化异常，导出失败");
                }
                personalName = constructionPersonnelEntity.getName();
            } else {
                personalName = "";
            }
        }
        return personalName;
    }

    private String getPersonalNameT(String tag, Long personalId) {
        String personalName = "";
        if (tag.equals(ConstantsInfo.CONSTRUCTION_WORKER)) {
            ConstructionPersonnelEntity constructionPersonnelEntity = constructionPersonnelMapper.selectOne(new LambdaQueryWrapper<ConstructionPersonnelEntity>()
                    .eq(ConstructionPersonnelEntity::getConstructionPersonnelId, personalId)
                    .eq(ConstructionPersonnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ObjectUtil.isNull(constructionPersonnelEntity)) {
                return personalName;
            }
            personalName = constructionPersonnelEntity.getName();
        }
        if (tag.equals(ConstantsInfo.INSPECTOR)) {
            if (ObjectUtil.isNotNull(personalId)) {
                ConstructionPersonnelEntity constructionPersonnelEntity = constructionPersonnelMapper.selectOne(new LambdaQueryWrapper<ConstructionPersonnelEntity>()
                        .eq(ConstructionPersonnelEntity::getConstructionPersonnelId, personalId)
                        .eq(ConstructionPersonnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
                if (ObjectUtil.isNull(constructionPersonnelEntity)) {
                    return personalName;
                }
                personalName = constructionPersonnelEntity.getName();
            } else {
                personalName = "";
            }
        }
        return personalName;
    }

    /**
     * 获取施工位置
     */
    private String getLocation(String type, Long locationId) {
        String location = "";
        // 判断施工类型 回采or掘进
        if (type.equals(ConstantsInfo.TUNNELING)) {
            TunnelEntity tunnelEntity = tunnelMapper.selectOne(new LambdaQueryWrapper<TunnelEntity>()
                    .eq(TunnelEntity::getTunnelId, locationId)
                    .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ObjectUtil.isNull(tunnelEntity)) {
                throw new RuntimeException("巷道转化异常，导出失败");
            }
            location = tunnelEntity.getTunnelName();
        }
        if (type.equals(ConstantsInfo.STOPE)) {
            BizWorkface bizWorkface = bizWorkfaceMapper.selectOne(new LambdaQueryWrapper<BizWorkface>()
                    .eq(BizWorkface::getWorkfaceId, locationId)
                    .eq(BizWorkface::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ObjectUtil.isNull(bizWorkface)) {
                throw new RuntimeException("工作面转化异常，导出失败");
            }
            location = bizWorkface.getWorkfaceName();
        }
        return location;
    }

    /**
     * 获取施工位置T
     */
    private String getLocationT(String type, Long locationId) {
        String location = "";
        // 判断施工类型 回采or掘进
        if (type.equals(ConstantsInfo.TUNNELING)) {
            TunnelEntity tunnelEntity = tunnelMapper.selectOne(new LambdaQueryWrapper<TunnelEntity>()
                    .eq(TunnelEntity::getTunnelId, locationId)
                    .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ObjectUtil.isNull(tunnelEntity)) {
                return location;
            }
            location = tunnelEntity.getTunnelName();
        }
        if (type.equals(ConstantsInfo.STOPE)) {
            BizWorkface bizWorkface = bizWorkfaceMapper.selectOne(new LambdaQueryWrapper<BizWorkface>()
                    .eq(BizWorkface::getWorkfaceId, locationId)
                    .eq(BizWorkface::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ObjectUtil.isNull(bizWorkface)) {
                return location;
            }
            location = bizWorkface.getWorkfaceName();
        }
        return location;
    }
}