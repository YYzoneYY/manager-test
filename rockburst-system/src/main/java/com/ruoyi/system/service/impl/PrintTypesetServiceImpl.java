package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.system.domain.BizDrillRecord;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.ConstructionPersonnelEntity;
import com.ruoyi.system.domain.Entity.ConstructionUnitEntity;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.PrintListDTO;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.PrintTypesetService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author: shikai
 * @date: 2025/8/28
 * @description:
 */

@Service
public class PrintTypesetServiceImpl implements PrintTypesetService {

    @Resource
    private BizProjectRecordMapper bizProjectRecordMapper;

    @Resource
    private ConstructionUnitMapper constructionUnitMapper;

    @Resource
    private TunnelMapper tunnelMapper;

    @Resource
    private BizWorkfaceMapper bizWorkfaceMapper;

    @Resource
    private ConstructionPersonnelMapper constructionPersonnelMapper;

    @Resource
    private BizDrillRecordMapper bizDrillRecordMapper;

    @Resource
    private SysDictDataMapper sysDictDataMapper;

    @Override
    public TableData queryPage(Date startTime, Date endTime, String drillNum, Long mineId, Integer pageNum, Integer pageSize) {
        TableData result = new TableData();
        if (null == pageNum || pageNum < 1) {
            pageNum = 1;
        }
        if (null == pageSize || pageSize < 1) {
            pageSize = 10;
        }
        PageHelper.startPage(pageNum, pageSize);
        Page<PrintListDTO> page = bizProjectRecordMapper.queryPrintList(startTime, endTime, drillNum, mineId);
        Page<PrintListDTO> fmt = getListFmt(page);
        result.setTotal(fmt.getTotal());
        result.setRows(fmt.getResult());
        return result;
    }

    private Page<PrintListDTO> getListFmt(Page<PrintListDTO>  page) {
        if (ListUtils.isNotNull(page.getResult())) {
            page.getResult().forEach(printListDTO -> {
                printListDTO.setDrillTypeFmt(sysDictDataMapper.selectDictLabel(ConstantsInfo.DRILL_TYPE_DICT_TYPE, printListDTO.getDrillType())); // 钻孔类型
                printListDTO.setConstructTypeFmt(sysDictDataMapper.selectDictLabel(ConstantsInfo.TYPE_DICT_TYPE, printListDTO.getConstructType())); // 施工类型
                String site = "";
                String constructionPersonnel = "";
                String inspector = "";
                if (printListDTO.getConstructType().equals(ConstantsInfo.TUNNELING)) {
                    site = getConstructSite(printListDTO.getTunnelId(), printListDTO.getConstructType());
                } else if (printListDTO.getConstructType().equals(ConstantsInfo.STOPE)) {
                    site = getConstructSite(printListDTO.getWorkfaceId(), printListDTO.getConstructType());
                }
                printListDTO.setConstructionSite(site);
                printListDTO.setConstructUnitName(getConstructionUnitName(printListDTO.getConstructUnitId()));

                if (ObjectUtil.isNotNull(printListDTO.getWorker())){
                    constructionPersonnel = getPersonalName("1", printListDTO.getWorker());
                }
                printListDTO.setWorkerName(constructionPersonnel); // 获取施工人员名称

                if (ObjectUtil.isNotNull(printListDTO.getAccepter())) {
                    inspector = getPersonalName("2", printListDTO.getAccepter());
                }
                printListDTO.setAccepterName(inspector); // 获取验收人员名称

                BizDrillRecord bizDrillRecord = bizDrillRecordMapper.selectOne(new LambdaQueryWrapper<BizDrillRecord>()
                        .eq(BizDrillRecord::getProjectId, printListDTO.getProjectId())
                        .eq(BizDrillRecord::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));

                if (ObjectUtil.isNull(bizDrillRecord.getRealDeep())) {
                    printListDTO.setRealDeep(null);
                } else {
                    printListDTO.setRealDeep(bizDrillRecord.getRealDeep());
                }
                if (ObjectUtil.isNull(bizDrillRecord.getDiameter())) {
                    printListDTO.setDiameter(null);
                } else {
                    printListDTO.setDiameter(bizDrillRecord.getDiameter());
                }
                if (ObjectUtil.isNull(bizDrillRecord.getPlanDeep())) {
                    printListDTO.setPlanDeep(null);
                } else {
                    printListDTO.setPlanDeep(bizDrillRecord.getPlanDeep());
                }
                if (ObjectUtil.isNull(bizDrillRecord.getRemark())) {
                    printListDTO.setDynamicPhenomenon("");
                } else {
                    printListDTO.setDynamicPhenomenon(bizDrillRecord.getRemark());
                }
            });
        }
        return page;
    }


    /**
     * 获取施工单位名称
     */
    public String getConstructionUnitName(Long constructionUnitId) {
        String constructionUnitName = null;
        ConstructionUnitEntity constructionUnitEntity = constructionUnitMapper.selectOne(new LambdaQueryWrapper<ConstructionUnitEntity>()
                .eq(ConstructionUnitEntity::getConstructionUnitId, constructionUnitId)
                .eq(ConstructionUnitEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(constructionUnitEntity)) {
            return null;
        }
        constructionUnitName = constructionUnitEntity.getConstructionUnitName();
        return constructionUnitName;
    }


    /**
     * 获取施工地点
     */
    public String getConstructSite(Long constructSite, String type) {
        String constructSiteName = null;
        if (ObjectUtil.equals(type, ConstantsInfo.TUNNELING)) {
            TunnelEntity tunnelEntity = tunnelMapper.selectOne(new LambdaQueryWrapper<TunnelEntity>()
                    .eq(TunnelEntity::getTunnelId, constructSite)
                    .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ObjectUtil.isNull(tunnelEntity)) {
                return null;
            }
            constructSiteName = tunnelEntity.getTunnelName();
        } else if (ObjectUtil.equals(type, ConstantsInfo.STOPE)) {
            BizWorkface bizWorkface = bizWorkfaceMapper.selectOne(new LambdaQueryWrapper<BizWorkface>()
                    .eq(BizWorkface::getWorkfaceId, constructSite)
                    .eq(BizWorkface::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ObjectUtil.isNull(bizWorkface)) {
                return null;
            }
            constructSiteName = bizWorkface.getWorkfaceName();
        }
        return constructSiteName;
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
}