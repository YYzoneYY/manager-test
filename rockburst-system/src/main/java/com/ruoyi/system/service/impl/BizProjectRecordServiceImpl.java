package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.util.MapUtils;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ruoyi.common.annotation.DataScopeSelf;
import com.ruoyi.common.core.domain.BasePermission;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.Entity.*;
import com.ruoyi.system.domain.dto.BizProjectRecordAddDto;
import com.ruoyi.system.domain.dto.BizProjectRecordDto;
import com.ruoyi.system.domain.dto.BizProjectRecordDto1;
import com.ruoyi.system.domain.excel.BizProjectDayRecordExcel;
import com.ruoyi.system.domain.excel.BizProjectRecordExcel;
import com.ruoyi.system.domain.vo.*;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.IBizProjectRecordService;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 工程填报记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Service
public class BizProjectRecordServiceImpl extends MPJBaseServiceImpl<BizProjectRecordMapper, BizProjectRecord> implements IBizProjectRecordService
{
    @Autowired
    private BizProjectRecordMapper bizProjectRecordMapper;

    @Autowired
    private BizProjectAuditMapper bizProjectAuditMapper;

    @Autowired
    private BizVideoMapper bizVideoMapper;

    @Autowired
    private SysDictDataMapper dictDataMapper;

    @Autowired
    private BizDrillRecordMapper bizDrillRecordMapper;

    @Autowired
    private TunnelMapper tunnelMapper; ;

    @Autowired
    private  BizWorkfaceMapper bizWorkfaceMapper;

    @Autowired
    private SysDeptMapper sysDeptMapper;

    @Autowired
    private MiningFootageMapper miningFootageMapper;

//    @Autowired
//    private MinioClient getMinioClient;



    @DataScopeSelf
    public MPage<BizProjectRecordListVo> getlist(BasePermission permission, BizProjectRecordDto dto, Pagination pagination){

        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        MPJLambdaWrapper<BizProjectRecord> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper
                .selectAll(BizProjectRecordListVo.class)
                .and(permission.getDeptIds() != null && permission.getDeptIds().size()>0 ,i->i.in(permission.getDeptIds() != null && permission.getDeptIds().size()>0 , BizProjectRecord::getDeptId,permission.getDeptIds())
                        .or().eq(permission.getDateScopeSelf() == 5,BizProjectRecord::getCreateBy,currentUser.getUserName()))
                .eq(dto.getConstructUnitId()!=null,BizProjectRecord::getConstructUnitId,dto.getConstructUnitId())
                .eq(dto.getTunnelId() != null,BizProjectRecord::getTunnelId,dto.getTunnelId())
                .eq(dto.getDrillType()!=null,BizProjectRecord::getDrillType,dto.getDrillType())
                .eq(dto.getConstructShiftId()!=null,BizProjectRecord::getConstructShiftId,dto.getConstructShiftId())
                .eq(dto.getStatus()!=null,BizProjectRecord::getStatus,dto.getStatus())
                .between(StrUtil.isNotEmpty(dto.getStartTime()),BizProjectRecord::getConstructTime,DateUtils.parseDate(dto.getStartTime()),DateUtils.parseDate(dto.getEndTime()))
                .eq(dto.getStatus()!=null,BizProjectRecord::getStatus,dto.getStatus());
        IPage<BizProjectRecordListVo> sss = this.pageDeep(pagination , queryWrapper);
        return new MPage<>(sss);
    }


    @Override
    public MPage<BizProjectRecordListVo> selectproList(BasePermission permission, BizProjectRecordDto dto , Pagination pagination) {

        Date currentDate = null;
        Date startDate = null;
        if(dto.getDayNum() != null ){
            currentDate = new Date();
            startDate = DateUtil.offsetDay(currentDate, -dto.getDayNum());
        }else {
            currentDate = DateUtils.parseDate(dto.getStartTime());
            startDate = DateUtils.parseDate(dto.getEndTime());
        }
        MPJLambdaWrapper<BizProjectRecord> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper
                .selectAll(BizProjectRecord.class)
                .between(BizProjectRecord::getConstructTime,startDate,currentDate);
        IPage<BizProjectRecordListVo> sss = this.pageDeep(pagination, queryWrapper);
        return new MPage<>(sss);
    }


    @Override
    public BizProStatsVo statsProject(BasePermission permission, BizProjectRecordDto dto) {
        Date currentDate = null;
        Date startDate = null;
        if(dto.getDayNum() != null ){
            currentDate = new Date();
            startDate = DateUtil.offsetDay(currentDate, -dto.getDayNum());
        }else {
            currentDate = DateUtils.parseDate(dto.getStartTime());
            startDate = DateUtils.parseDate(dto.getEndTime());
        }

        MPJLambdaWrapper<BizProjectRecord> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper
//                .selectAs(::getDeptName,"name")
                .selectCount(BizProjectRecord::getProjectId,"value")
                .between(BizProjectRecord::getConstructTime,startDate,currentDate)
                .leftJoin(SysDept.class,SysDept::getDeptId,BizProjectRecord::getDeptId)
//                .leftJoin(BizProjectRecord::getConstructLocationId)
                .groupBy(BizProjectRecord::getDeptId);
        Map<String,Object> locationMap = bizProjectRecordMapper.selectJoinMap(queryWrapper);

        queryWrapper.clear();
        queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper
                .selectAs(SysDictData::getDictLabel,"name")
                .selectCount(BizProjectRecord::getProjectId,"value")
                .leftJoin(SysDictData.class,SysDictData::getDictValue,BizProjectRecord::getDrillType)
                .between(BizProjectRecord::getConstructTime,startDate,currentDate)
                .groupBy(BizProjectRecord::getDrillType);
        Map<String,Object> drillTypeMap = bizProjectRecordMapper.selectJoinMap(queryWrapper);

        queryWrapper.clear();
        queryWrapper = new MPJLambdaWrapper<>();

        queryWrapper
//                .selectAs(SysDictData::getDictLabel,"name")
                .selectCount(BizProjectRecord::getProjectId,"value")
//                .leftJoin(.class,,BizProjectRecord::getConstructUnitId)
                .between(BizProjectRecord::getConstructTime,startDate,currentDate)
                .groupBy(BizProjectRecord::getConstructUnitId);
        Map<String,Object> unitMap = bizProjectRecordMapper.selectJoinMap(queryWrapper);

        BizProStatsVo vo = new BizProStatsVo();
        vo.setLocationMap(unitMap).setTypeMap(drillTypeMap).setUnitMap(unitMap);
        return vo;
    }

    @Override
    public BizProjectRecordVo selectById(Long bizProjectRecordId) {

        MPJLambdaWrapper<BizProjectRecord> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper
                .selectAll(BizProjectRecord.class)
//todo                .leftJoin(,BizProjectRecord::getConstructUnitId)
//               .leftJoin(,BizProjectRecord::getConstructLocationId)
//               .leftJoin(,BizProjectRecord::getConstructShiftId)
//                .select(BizProjectRecord::getProjectId,BizProjectRecordVo::getConstructLocationName)
//                .select(BizProjectRecord::getProjectId,BizProjectRecordVo::getConstructUnitName)
//                .select(BizProjectRecord::getProjectId,BizProjectRecordVo::getConstructShiftName)
                .leftJoin(BizMine.class,BizMine::getMineId,BizProjectRecord::getDeptId)
                .eq(BizProjectRecord::getProjectId,bizProjectRecordId);
        return  bizProjectRecordMapper.selectJoinOne(BizProjectRecordVo.class, queryWrapper);
    }

    @Override
    public List<BizProjectRecordListVo> auditList(BizProjectRecord bizProjectRecord) {
        return null;
    }

    @Override
    public int saveRecord(BizProjectRecordAddDto dto) {
        BizProjectRecord entity = new BizProjectRecord();
        BeanUtil.copyProperties(dto, entity);
        entity.setStatus(BizBaseConstant.FILL_STATUS_PEND).setIsRead(0);

        long  projectId =  this.getBaseMapper().insert(entity);
        dto.getDrillRecords().forEach(drillRecord -> {
            drillRecord.setProjectId(projectId);
            bizDrillRecordMapper.insert(drillRecord);
        });
        dto.getVideos().forEach(bizVideo -> {
            bizVideo.setProjectId(projectId);
            bizVideoMapper.insert(bizVideo);
        });
        BizProjectAudit audit = new BizProjectAudit();
        audit.setProjectId(dto.getProjectId())
                .setMsg("提交")
                .setLevel("AUTHOR")
                .setStatus(0);
        bizProjectAuditMapper.insert(audit);
        return 1;
    }


    @Override
    public int updateRecord(BizProjectRecordAddDto dto) {
        this.updateById(dto);
        UpdateWrapper<BizDrillRecord> drillUpdateWrapper= new UpdateWrapper<>();
        drillUpdateWrapper.lambda().eq(BizDrillRecord::getProjectId,dto.getProjectId()).set(BizDrillRecord::getDelFlag,BizBaseConstant.DELFLAG_Y);
        bizDrillRecordMapper.update(null,drillUpdateWrapper);
        UpdateWrapper<BizVideo> videoUpdateWrapper= new UpdateWrapper<>();
        videoUpdateWrapper.lambda().eq(BizVideo::getProjectId,dto.getProjectId()).set(BizVideo::getDelFlag,BizBaseConstant.DELFLAG_Y);
        bizVideoMapper.update(null,videoUpdateWrapper);

        if(dto.getDrillRecords() != null && dto.getDrillRecords().size() > 0){
            dto.getDrillRecords().forEach(drillRecord -> {
                drillRecord.setDrillRecordId(null);
                drillRecord.setProjectId(dto.getProjectId());
                bizDrillRecordMapper.insert(drillRecord);
            });
        }
        if(dto.getVideos() != null && dto.getVideos().size() > 0){
            dto.getVideos().forEach(bizVideo -> {
                bizVideo.setVideoId(null);
                bizVideo.setProjectId(dto.getProjectId());
                bizVideoMapper.insert(bizVideo);
            });
        }

        return 1;
    }

    @Override
    public int updateRecordById(BizProjectRecordAddDto dto) {
        BizProjectRecord entity = new BizProjectRecord();
        BeanUtil.copyProperties(dto, entity);
        entity.setStatus(BizBaseConstant.FILL_STATUS_PEND).setIsRead(0);
        bizProjectRecordMapper.updateById(entity);
        BizProjectAudit audit = new BizProjectAudit();
        audit.setProjectId(dto.getProjectId())
                .setMsg("提交")
                .setLevel("AUTHOR")
                .setStatus(0);
        bizProjectAuditMapper.insert(audit);
        return 1;
    }

    @Override
    public int firstAudit(BizProjectRecordDto dto) {
        BizProjectAudit audit = new BizProjectAudit();
        audit.setProjectId(dto.getProjectId())
                .setMsg(dto.getMsg())
                .setLevel("TEAM")
                .setStatus(dto.getAudit());
        bizProjectAuditMapper.insert(audit);
        BizProjectRecord entity = new BizProjectRecord();
        entity.setProjectId(dto.getProjectId())
                        .setStatus(dto.getStatus() == 1 ? BizBaseConstant.FILL_STATUS_TEAM_PASS:BizBaseConstant.FILL_STATUS_TEAM_BACK);
        bizProjectRecordMapper.updateById(entity);
        return 1;
    }


    @Override
    public int secondAudit(BizProjectRecordDto dto) {
        BizProjectAudit audit = new BizProjectAudit();
        audit.setProjectId(dto.getProjectId())
                .setMsg(dto.getMsg())
                .setLevel("DEPT")
                .setStatus(dto.getAudit());
        bizProjectAuditMapper.insert(audit);
        BizProjectRecord entity = new BizProjectRecord();
        entity.setProjectId(dto.getProjectId())
                .setStatus(dto.getStatus() == 1 ? BizBaseConstant.FILL_STATUS_DEPART_PASS:BizBaseConstant.FILL_STATUS_DEPART_BACK);
        bizProjectRecordMapper.updateById(entity);
        return 1;
    }


    @Override
    public void getReport(BizProjectRecordDto1 dto, HttpServletResponse response) {

        Date oneTime = new Date();
        Date startTime = new Date();
        Date endTime = new Date();
        if (StrUtil.isNotBlank(dto.getOneTime())){
            oneTime = DateUtil.parseDate(dto.getOneTime());
        }
        if (StrUtil.isNotBlank(dto.getStartTime())){
             startTime = DateUtil.parseDate(dto.getStartTime());
        }
        if (StrUtil.isNotBlank(dto.getEndTime())){
             endTime = DateUtil.parseDate(dto.getEndTime());
        }

        MPJLambdaWrapper<BizProjectRecord> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper
                .selectAs(BizProjectRecord::getConstructTime,BizProjectRecordMap::getConstructTime)
                .selectAs(ClassesEntity::getClassesName,BizProjectRecordMap::getConstructShiftName)
                .selectAs(BizProjectRecord::getDrillNum,BizProjectRecordMap::getDrillNum)
                .selectAs(BizProjectRecord::getConstructType,BizProjectRecordMap::getConstructType)
                .selectAs(BizWorkface::getWorkfaceName,BizProjectRecordMap::getWorkFaceName)
                .selectAs(BizTravePoint::getPointName,BizProjectRecordMap::getPointName)
                .selectAs(BizProjectRecord::getConstructRange,BizProjectRecordMap::getConstructRange)
                .selectAs(BizDrillRecord::getRealDeep,BizProjectRecordMap::getRealDeep)
                .selectAs(BizDrillRecord::getDiameter,BizProjectRecordMap::getDiameter)
                .selectAs("a",ConstructionPersonnelEntity::getName,BizProjectRecordMap::getProjecrHeader)
                .selectAs("b",ConstructionPersonnelEntity::getName,BizProjectRecordMap::getSecurityer)
                .selectAs("c",ConstructionPersonnelEntity::getName,BizProjectRecordMap::getWorker)
                .selectAs(BizProjectRecord::getRemark, BizProjectRecordMap::getRemark)
                .leftJoin(ClassesEntity.class,ClassesEntity::getClassesId,BizProjectRecord::getConstructShiftId)
                .leftJoin(ConstructionPersonnelEntity.class,"a",ConstructionPersonnelEntity::getConstructionPersonnelId,BizProjectRecord::getProjecrHeader )
                .leftJoin(ConstructionPersonnelEntity.class,"b",ConstructionPersonnelEntity::getConstructionPersonnelId,BizProjectRecord::getSecurityer )
                .leftJoin(ConstructionPersonnelEntity.class,"c",ConstructionPersonnelEntity::getConstructionPersonnelId,BizProjectRecord::getWorker )
                .innerJoin(BizDrillRecord.class,BizDrillRecord::getProjectId,BizProjectRecord::getProjectId)
                .leftJoin(BizTravePoint.class,BizTravePoint::getPointId,BizProjectRecord::getTravePointId)
                .leftJoin(BizWorkface.class,BizWorkface::getWorkfaceId,BizProjectRecord::getTunnelId)
                .eq(StrUtil.isNotEmpty(dto.getDirection()),BizDrillRecord::getDirection,dto.getDirection())
                .eq(StrUtil.isNotEmpty(dto.getDrillType()),BizProjectRecord::getDrillType,dto.getDrillType())
                .eq(dto.getTunnelId() != null,BizProjectRecord::getTunnelId,dto.getTunnelId())
                .eq(dto.getConstructUnitId() != null, BizProjectRecord::getConstructUnitId,dto.getConstructUnitId())
                .eq(dto.getShiftId() != null,BizProjectRecord::getConstructShiftId,dto.getShiftId())
                .between(StrUtil.isNotBlank(dto.getOneTime()),BizProjectRecord::getConstructTime, DateUtil.beginOfDay(oneTime),DateUtil.endOfDay(oneTime))
                .between(StrUtil.isNotBlank(dto.getStartTime()), BizProjectRecord::getConstructTime,DateUtils.parseDate(startTime),DateUtils.parseDate(endTime));
        List<BizProjectRecordMap> data = bizProjectRecordMapper.selectJoinList(BizProjectRecordMap.class,queryWrapper);

        List<BizProjectRecordExcel> excelList = BeanUtil.copyToList(data,BizProjectRecordExcel.class);


        String title="";
        if( dto.getTunnelId() != null){
            QueryWrapper<TunnelEntity> tunnelEntityQueryWrapper = new QueryWrapper<>();
            tunnelEntityQueryWrapper.lambda().eq(TunnelEntity::getTunnelId,dto.getTunnelId());
            TunnelEntity entity = tunnelMapper.selectOne(tunnelEntityQueryWrapper);
            title =  entity.getTunnelName();
        }
        if(StrUtil.isNotEmpty(dto.getDirection()) ){
            QueryWrapper<SysDictData> sysDictDataQueryWrapper = new QueryWrapper<>();
            sysDictDataQueryWrapper.lambda().eq(SysDictData::getDictValue,dto.getDirection()).eq(SysDictData::getStatus,0);
            SysDictData dictData = dictDataMapper.selectOne(sysDictDataQueryWrapper);
            title = dictData.getDictLabel() + title;
        }
        title = title + "卸压孔施工台账";
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode(title, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            EasyExcel.write(response.getOutputStream())
                    .autoCloseStream(true)

                    .registerWriteHandler(new CellWriteHandler() {
                        @Override
                        public void afterCellDispose(CellWriteHandlerContext context) {
                            Cell cell = context.getCell();
                            WriteCellData<?> cellData = context.getFirstCellData();
                            WriteCellStyle writeCellStyle = cellData.getOrCreateStyle();
                            // 设置单元格的黑色边框
                            writeCellStyle.setBorderTop(BorderStyle.THIN);
                            writeCellStyle.setBorderBottom(BorderStyle.THIN);
                            writeCellStyle.setBorderLeft(BorderStyle.THIN);
                            writeCellStyle.setBorderRight(BorderStyle.THIN);
                            // 设置边框颜色为黑色
                            writeCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
                            writeCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
                            writeCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
                            writeCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
                            writeCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
                            writeCellStyle.setFillBackgroundColor((short) 1);
                            if (cell.getRowIndex() == 0 && cell.getColumnIndex() == 0) {
                                SXSSFSheet sheet = (SXSSFSheet) cell.getSheet();
                                sheet.setColumnWidth(0, 15 * 300);
//                                sheet.createDrawingPatriarch();
//                                XSSFDrawing drawingPatriarch = sheet.getDrawingPatriarch();
//                                XSSFClientAnchor anchor = drawingPatriarch.createAnchor(0, 0, 0, 0, 4, 1, 5, 2);
//                                XSSFSimpleShape simpleShape = drawingPatriarch.createSimpleShape(anchor);
//                                simpleShape.setShapeType(ShapeTypes.LINE);
//                                simpleShape.setLineWidth(0.5);
//                                simpleShape.setLineStyle(0);
//                                simpleShape.setLineStyleColor(0, 0, 0);
                            }
                        }
                    }).head(head(title)).sheet("sheet1").doWrite(excelList);
        }catch (Exception e){

        }

    }
    /**
     * 多重施法
     * @return
     */
    private List<List<String>> head(String title) {
        List<List<String>> headList2D = new ArrayList<>();

        headList2D.add(Arrays.asList( title,"施工日期")); // 一列
        headList2D.add(Arrays.asList( title,"班次"));
        headList2D.add(Arrays.asList(title,"钻孔编号"));
        headList2D.add(Arrays.asList( title,"导线点/工作面 + 距离"));
//        headList2D.add(Arrays.asList( title,"距离"));
//        headList2D.add(Arrays.asList( title,"   钻孔高度/m\n钻孔角度/°"));
        headList2D.add(Arrays.asList(title,"孔深（m）"));
        headList2D.add(Arrays.asList( title,"孔,径"));
//        headList2D.add(Arrays.asList( title,"揭露煤岩性(m)"));
//        headList2D.add(Arrays.asList( title,"终孔煤（岩）性"));
        headList2D.add(Arrays.asList( title,"施工负责人"));
        headList2D.add(Arrays.asList( title,"施工员"));
        headList2D.add(Arrays.asList(title,"安检员"));
        headList2D.add(Arrays.asList( title,"施工工具"));
        headList2D.add(Arrays.asList( title,"备注"));
        return headList2D;

    }


    @Override
    public void getDayReport(Long mineId, String statsDate, Long deptId, HttpServletResponse response) throws UnsupportedEncodingException {
        Date date = DateUtil.parseDate(statsDate);

        QueryWrapper<BizWorkface> faceQueryWrapper = new QueryWrapper<>();
        faceQueryWrapper.lambda()
                .select(BizWorkface::getWorkfaceId,BizWorkface::getWorkfaceName)
                .eq(BizWorkface::getMineId,mineId)
                .eq(BizWorkface::getStatus,BizBaseConstant.WORKFACE_STATUS_KCZ);
        List<BizWorkface> workfaces = bizWorkfaceMapper.selectList(faceQueryWrapper);
//        Assert.isTrue((workfaces != null && workfaces.size() >0), "没有正在开采的工作面");


        QueryWrapper<SysDept> deptQueryWrapper = new QueryWrapper<>();
        deptQueryWrapper.lambda()
                .select(SysDept::getDeptId)
                .apply("FIND_IN_SET ('" + deptId + "',ancestors)")
                .eq(SysDept::getDelFlag,BizBaseConstant.DELFLAG_N);
        List<SysDept> deptList= sysDeptMapper.selectList(deptQueryWrapper);
        List<Long> deptIds = new ArrayList<>();
        if(deptList!=null && deptList.size()>0){
            deptIds = deptList.stream().map(SysDept::getDeptId).collect(Collectors.toList());
        }
        deptIds.add(deptId);

        MPJLambdaWrapper<BizProjectRecord> projectQueryWrapper = getDirectionWrapper("采帮");
//        projectQueryWrapper
//                .in(BizProjectRecord::getDeptId,deptIds)
//                .eq(BizProjectRecord::getConstructType,"回踩")
//                .between(BizProjectRecord::getConstructTime,DateUtil.beginOfDay(date),DateUtil.endOfDay(date));
        List<BizProjectDayRecordMap> list=   bizProjectRecordMapper.selectJoinList(BizProjectDayRecordMap.class ,projectQueryWrapper);


        Map<Long, List<BizProjectDayRecordMap>> groupedByTunnelId = list.stream()
                .collect(Collectors.groupingBy(BizProjectDayRecordMap::getTunnelId));


        List<BizProjectDayRecordExcel> excelList = new ArrayList<>();

        groupedByTunnelId.forEach((tunnelId, records) -> {
            BizProjectDayRecordExcel excel = new BizProjectDayRecordExcel();
            QueryWrapper<MiningFootageEntity> footageQueryWrapper = new QueryWrapper<>();
            footageQueryWrapper.lambda()
                    .select(MiningFootageEntity::getMiningPace);
//                    .eq(MiningFootageEntity::getWorkfaceId,records.get(0).getTunnelId())
//                    .eq(MiningFootageEntity::getDelFlag,BizBaseConstant.DELFLAG_N)
//                    .between(MiningFootageEntity::getMiningTime,DateUtil.beginOfDay(date).getTime(),DateUtil.endOfDay(date).getTime());
            List<MiningFootageEntity> miningFootageEntityList = miningFootageMapper.selectList(footageQueryWrapper);
            BigDecimal sum =  miningFootageEntityList.stream()
                    .map(MiningFootageEntity::getMiningPace).filter(miningPace -> miningPace != null)  // 过滤掉 null 值（如果有的话）
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            excel.setMinePace(sum);
            List<BizProjectDayRecordMap> caibang = records.stream().filter(record -> "caibang".equals(record.getDirection())).collect(Collectors.toList());
            List<BizProjectDayRecordMap> caibang1 =  caibang.stream()
                    .collect(Collectors.toMap( BizProjectDayRecordMap::getProjectId, record  -> record,  (existing, replacement) -> existing)) .values() .stream()  .collect(Collectors.toList());
            excel.setCaibang(caibang1.size()+"");

            List<BizProjectDayRecordMap> feicaibang = records.stream().filter(record -> "feicaibang".equals(record.getDirection())).collect(Collectors.toList());

            List<BizProjectDayRecordMap> feicaibang1 =  feicaibang.stream()
                    .collect(Collectors.toMap( BizProjectDayRecordMap::getProjectId, record  -> record,  (existing, replacement) -> existing)) .values() .stream()  .collect(Collectors.toList());

            excel.setFeicaibang(feicaibang1.size()+"");
            List<BizProjectDayRecordMap> zhengyingtou = records.stream().filter(record -> "zhengyingtou".equals(record.getDirection())).collect(Collectors.toList());

            List<BizProjectDayRecordMap> zhengyingtou1 =  zhengyingtou.stream()
                    .collect(Collectors.toMap( BizProjectDayRecordMap::getProjectId, record  -> record,  (existing, replacement) -> existing)) .values() .stream()  .collect(Collectors.toList());
            excel.setZhengyingtiou(zhengyingtou1.size()+"");

            excel.setGeshu(caibang1.size()+feicaibang1.size()+zhengyingtou1.size() +"");
            BigDecimal sumRealDeep = records.stream()
                    .map(BizProjectDayRecordMap::getRealDeep) // 映射为 realDeep 字段值
                    .filter(realDeep -> realDeep != null) // 过滤掉 null 值
                    .reduce(BigDecimal.ZERO, BigDecimal::add); // 求和
            excel.setZongshen(sumRealDeep+"");


            String caibangLocation = "";
            String feicaibangLocation = "";
            String zhengyingtou1Location = "";
            for (BizProjectDayRecordMap bizProjectDayRecordMap : caibang1) {
                caibangLocation =  caibangLocation + bizProjectDayRecordMap.getPointName()+"("+bizProjectDayRecordMap.getConstructRange()+")" + '\n';
            }
            for (BizProjectDayRecordMap bizProjectDayRecordMap : feicaibang1) {
                feicaibangLocation =  feicaibangLocation + bizProjectDayRecordMap.getPointName()+"("+bizProjectDayRecordMap.getConstructRange()+")" + '\n';
            }
            for (BizProjectDayRecordMap bizProjectDayRecordMap : zhengyingtou1) {
                zhengyingtou1Location =  zhengyingtou1Location + bizProjectDayRecordMap.getPointName()+"("+bizProjectDayRecordMap.getConstructRange()+")" + '\n';
            }
            excel.setCaibanglocation(caibangLocation);
            excel.setFeicaibanglocation(feicaibangLocation);
            excel.setZhengyingtioulocation(zhengyingtou1Location);

            excel.setWorker(records.size()+"");
            excel.setConstructUnitName(records.get(0).getConstructUnitName());
            records.forEach(record -> System.out.println(record));
            excelList.add(excel);
            excelList.add(excel);
            excelList.add(excel);
            excelList.add(excel);
            excelList.add(excel);
        });

        String templateFileName ="excel/moban.xlsx";

        String fileName = URLEncoder.encode("测试", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        // 方案1
        try (ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).withTemplate(templateFileName).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            // 这里注意 入参用了forceNewRow 代表在写入list的时候不管list下面有没有空行 都会创建一行，然后下面的数据往后移动。默认 是false，会直接使用下一行，如果没有则创建。
            // forceNewRow 如果设置了true,有个缺点 就是他会把所有的数据都放到内存了，所以慎用
            // 简单的说 如果你的模板有list,且list不是最后一行，下面还有数据需要填充 就必须设置 forceNewRow=true 但是这个就会把所有数据放到内存 会很耗内存
            // 如果数据量大 list不是最后一行 参照下一个
            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
            excelWriter.fill(excelList, fillConfig, writeSheet);
//            excelWriter.fill(excelList, fillConfig, writeSheet);
            Map<String, Object> map = MapUtils.newHashMap();
            map.put("statsDate", "2019年10月9日13:28:28");
            map.put("deptName", 1000);
            excelWriter.fill(map, writeSheet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        System.out.println("list = " + list);
    }

    public static void main(String[] args) {
       List<BizProjectDayRecordMap> list = new ArrayList<>();
       BizProjectDayRecordMap bizProjectDayRecordMap1 = new BizProjectDayRecordMap();
       bizProjectDayRecordMap1.setProjectId(1L).setNo(1);
        BizProjectDayRecordMap bizProjectDayRecordMap2 = new BizProjectDayRecordMap();
        bizProjectDayRecordMap2.setProjectId(2L).setNo(2);
        BizProjectDayRecordMap bizProjectDayRecordMap3 = new BizProjectDayRecordMap();
        bizProjectDayRecordMap3.setProjectId(3L).setNo(3);
        BizProjectDayRecordMap bizProjectDayRecordMap4 = new BizProjectDayRecordMap();
        bizProjectDayRecordMap4.setProjectId(4L).setNo(4);
        BizProjectDayRecordMap bizProjectDayRecordMap5 = new BizProjectDayRecordMap();
        bizProjectDayRecordMap5.setProjectId(1L).setNo(5);

        list.add(bizProjectDayRecordMap1);
        list.add(bizProjectDayRecordMap2);
        list.add(bizProjectDayRecordMap3);
        list.add(bizProjectDayRecordMap4);
        list.add(bizProjectDayRecordMap5);
         List<BizProjectDayRecordMap> s  = list.stream()
                 .collect(Collectors.toMap( BizProjectDayRecordMap::getProjectId, record  -> record,  (existing, replacement) -> existing)) .values() .stream()  .collect(Collectors.toList());
        System.out.println("s = " + s);
    }

    private MPJLambdaWrapper<BizProjectRecord> getDirectionWrapper(String direction){
        MPJLambdaWrapper<BizProjectRecord> projectQueryWrapper = new MPJLambdaWrapper<>();
        projectQueryWrapper
                .select(ConstructionUnitEntity::getConstructionUnitName)
                .select(BizWorkface::getWorkfaceName)
                .select(BizDrillRecord::getDirection,BizDrillRecord::getRealDeep)
                .select(BizProjectRecord::getConstructUnitId,BizProjectRecord::getTunnelId,BizProjectRecord::getRemark)
                .innerJoin(BizDrillRecord.class,BizDrillRecord::getProjectId,BizProjectRecord::getProjectId)
                .leftJoin(ConstructionUnitEntity.class,ConstructionUnitEntity::getConstructionUnitId,BizProjectRecord::getConstructUnitId)
                .innerJoin(BizWorkface.class,BizWorkface::getWorkfaceId,BizProjectRecord::getTunnelId);
//                .eq(BizDrillRecord::getDirection,direction)
//                .eq(BizProjectRecord::getDrillType,BizBaseConstant.FILL_TYPE_LDPR)
//                .eq(BizProjectRecord::getDelFlag,BizBaseConstant.DELFLAG_N);
        return projectQueryWrapper;

    }

}







