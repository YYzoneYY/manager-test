package com.ruoyi.web.controller.basicInfo;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.constant.GroupUpdate;
import com.ruoyi.system.constant.MapConfigConstant;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.BizDangerAreaDto;
import com.ruoyi.system.domain.utils.*;
import com.ruoyi.system.domain.vo.BizDangerAreaVo;
import com.ruoyi.system.mapper.BizTunnelBarMapper;
import com.ruoyi.system.mapper.BizWorkfaceMapper;
import com.ruoyi.system.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 危险区管理Controller
 *
 * @author ruoyi
 * @date 2024-11-11
 */
@Api(tags = "basic-危险区")
//@Tag(description = "危险区管理Controller", name = "危险区管理Controller")
@RestController
@RequestMapping("/basicInfo/dangerArea")
public class BizDangerAreaController extends BaseController
{
    @Autowired
    private IBizDangerAreaService bizDangerAreaService;

    @Autowired
    private IBizTunnelBarService bizTunnelBarService;
    @Autowired
    private IBizDangerLevelService bizDangerLevelService;

    @Autowired
    private TunnelService tunnelService;

    @Autowired
    private IBizTravePointService bizTravePointService;
    @Autowired
    private IBizPresetPointService bizPresetPointService;
    @Autowired
    private BizTunnelBarMapper bizTunnelBarMapper;
    @Autowired
    private ISysConfigService configService;
    @Autowired
    private BizWorkfaceMapper bizWorkfaceMapper;

    @Autowired
    private IPolylineObjectService polylineObjectService;

    /**
     *
     */
    @ApiOperation("查询危险区管理列表")
    @PreAuthorize("@ss.hasPermi('basicInfo:dangerArea:list')")
    @GetMapping("/list")
    public R<MPage<BizDangerAreaVo>> list(@ParameterObject BizDangerAreaDto dto, @ParameterObject Pagination pagination)
    {
        if(StrUtil.isNotEmpty(dto.getWorkfaceName())){
            QueryWrapper<BizWorkface> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(BizWorkface::getWorkfaceName, dto.getWorkfaceName());
            List<BizWorkface> workfaces = bizWorkfaceMapper.selectList(queryWrapper);
            if(workfaces != null &&  workfaces.size() > 0){
                dto.setWorkfaceId(workfaces.get(0).getWorkfaceId());
            }
        }
        return R.ok(bizDangerAreaService.selectEntityList(dto, pagination));
    }


    /**
     * 获取危险区管理详细信息
     */
    @ApiOperation("获取危险区管理详细信息")
    @PreAuthorize("@ss.hasPermi('basicInfo:dangerArea:query')")
    @GetMapping(value = "/{dangerAreaId}")
    public R<BizDangerAreaVo> getInfo(@PathVariable("dangerAreaId") Long dangerAreaId)
    {
        return R.ok(bizDangerAreaService.selectEntityById(dangerAreaId));
    }


//    /**
//     * 新增危险区管理
//     */
//    @ApiOperation("新增危险区管理cad")
//    @PreAuthorize("@ss.hasPermi('basicInfo:dangerArea:add')")
//    @Log(title = "危险区管理", businessType = BusinessType.INSERT)
//    @PostMapping("add-cad")
//    public R addcad(@RequestBody  List<Segment> pointList)
//    {
//        List<TunnelEntity> tunnelEntities = tunnelService.list();
//        List<List<Point2D>> list = new ArrayList<>();
//        for (Segment segment : pointList) {
//            List<Point2D> point2DS = new ArrayList<>();
//            for (TunnelEntity tunnelEntity : tunnelEntities) {
//                QueryWrapper<BizTunnelBar> queryWrapper = new QueryWrapper<>();
//                queryWrapper.lambda().eq(BizTunnelBar::getTunnelId,tunnelEntity);
//                List<BizTunnelBar> bars = bizTunnelBarService.list(queryWrapper);
//                if(bars != null && bars.size() > 0){
//                    for (BizTunnelBar bar : bars) {
//                        Point2D jiaodian =  GeometryUtil.getIntersection(new Point2D(segment.getStart().getX(),segment.getEnd().getY()),
//                                new Point2D(segment.getEnd().getX(),segment.getEnd().getY()),
//                                new Point2D(new BigDecimal(bar.getStartx()),new BigDecimal(bar.getStarty())),
//                                new Point2D(new BigDecimal(bar.getEndx()),new BigDecimal(bar.getEndy())));
//                        if(jiaodian != null){
//                            point2DS.add(jiaodian);
//                        }
//                    }
//                }
//            }
//            list.add(point2DS);
//        }
//
//        return R.ok(bizDangerAreaService.insertEntity(dto));
//    }

    /**
     * 新增危险区管理
     */
    @ApiOperation("新增危险区管理cad")
    @PreAuthorize("@ss.hasPermi('basicInfo:dangerArea:add')")
    @Log(title = "危险区管理", businessType = BusinessType.INSERT)
    @PostMapping("add-cadsss")
    public R addcsssad(@RequestBody  List<PolylineObject> polylineObjects)
    {
        if(polylineObjects != null &&  polylineObjects.size() > 0){
            for (PolylineObject polylineObject : polylineObjects) {
                if(polylineObject.getStart() != null && polylineObject.getEnd() != null){
                    polylineObject.setStartx(polylineObject.getStart().getX()+"")
                            .setStarty(polylineObject.getStart().getY()+"")
                            .setEndx(polylineObject.getEnd().getX()+"")
                            .setEndy(polylineObject.getEnd().getY()+"");
                }
                QueryWrapper<PolylineObject> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(PolylineObject::getId, polylineObject.getId());
                long count  = polylineObjectService.getBaseMapper().selectCount(queryWrapper);
                if(count > 0){
                    continue;
                }
                polylineObjectService.save(polylineObject);
            }
        }
        return R.ok();

    }


    /**
     * 新增危险区管理
     */
    @ApiOperation("新增危险区管理cad")
    @PreAuthorize("@ss.hasPermi('basicInfo:dangerArea:add')")
    @Log(title = "危险区管理", businessType = BusinessType.INSERT)
    @PostMapping("add-cad")
    public R addcsssad1(@RequestBody List<PolylineObject> polylineObjects)
    {

        List<Segment> pointList = new ArrayList<>();

        List<BizDangerArea> areas = new ArrayList<>();

        for (PolylineObject polylineObject : polylineObjects) {
            Segment segment = new Segment();
            BeanUtils.copyProperties(polylineObject, segment);
            pointList.add(segment);
            if(polylineObject.getStart() != null && polylineObject.getEnd() != null){
                polylineObject.setStartx(polylineObject.getStart().getX()+"")
                        .setStarty(polylineObject.getStart().getY()+"")
                        .setEndx(polylineObject.getEnd().getX()+"")
                        .setEndy(polylineObject.getEnd().getY()+"");
            }
            QueryWrapper<PolylineObject> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(PolylineObject::getId, polylineObject.getId());
            long count  = polylineObjectService.getBaseMapper().selectCount(queryWrapper);
            if(count > 0){
                continue;
            }
            polylineObjectService.save(polylineObject);
        }

        List<TunnelEntity> tunnelEntities = tunnelService.list();
        if(tunnelEntities == null || tunnelEntities.size() == 0){
            return R.ok();
        }

        for (TunnelEntity tunnelEntity : tunnelEntities) {
            QueryWrapper<BizTunnelBar> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(BizTunnelBar::getTunnelId,tunnelEntity.getTunnelId());
            List<BizTunnelBar> bars = bizTunnelBarService.list(queryWrapper);
            if(bars == null || bars.size() == 0){
                continue;
            }
            if(bars.get(0).getTowardAngle() != null && bars.get(0).getTowardAngle() > 0){
                continue;
            }
//            List<List<Point2D>> lines = new ArrayList<>();
//            for (BizTunnelBar bar : bars) {
//                List<Point2D> line = new ArrayList<>();
//                Point2D start = new Point2D();
//                start.setX(new BigDecimal(bar.getStartx()));
//                start.setY(new BigDecimal(bar.getStarty()));
//                Point2D end = new Point2D();
//                end.setX(new BigDecimal(bar.getEndx()));
//                end.setY(new BigDecimal(bar.getEndy()));
//                line.add(start);
//                line.add(end);
//                lines.add(line);
//            }
            List<List<Point2D>> barPoints = new ArrayList<>();
            List<List<Point2D>> list = new ArrayList<>();
            for (Segment segment : pointList) {
                List<Point2D> point2DS = new ArrayList<>();
                if(bars != null && bars.size() > 0){
                    for (BizTunnelBar bar : bars) {
                        Point2D jiaodian =  SegmentIntersection.getIntersection(new Point2D(segment.getStart().getX(),segment.getStart().getY()),
                                new Point2D(segment.getEnd().getX(),segment.getEnd().getY()),
                                new Point2D(new BigDecimal(bar.getStartx()),new BigDecimal(bar.getStarty())),
                                new Point2D(new BigDecimal(bar.getEndx()),new BigDecimal(bar.getEndy())));
                        if(jiaodian != null){
                            jiaodian.setBarType(bar.getType());
                            point2DS.add(jiaodian);
                        }
                    }
                    list.add(point2DS);
                }
            }
//            List<BigDecimal[]> bigDecimals = getSegments(bars);

//            Segment segment = GeometryUtil.findShortestTwoSegments(bigDecimals);

            Segment segment = new Segment();
            Point2D start2D = new Point2D();
            Point2D end2D = new Point2D();
            start2D.setX(new BigDecimal(bars.get(0).getStartx() ) );
            start2D.setY(new BigDecimal(bars.get(0).getStarty() ) );
            end2D.setX(new BigDecimal(bars.get(1).getStarty() ) );
            end2D.setY(new BigDecimal(bars.get(1).getStarty() ) );


            List<Segment> sorted = GeometryUtil.findNearRectangleRegions(list, segment);

            List<List<Point2D>> quyus = GeometryUtil.buildRegionsFromSortedSegments(sorted);

            for (List<Point2D> quyu : quyus) {
                if(quyu == null || quyu.size() == 0){
                    continue;
                }
                Point2D center = GeometryUtil.getCenterPoint(quyu);
                BizDangerArea area = new BizDangerArea();

                area.setTunnelId(tunnelEntity.getTunnelId())
                                .setWorkfaceId(tunnelEntity.getWorkFaceId());
                area.setCenter(center.getX()+","+center.getY());
                Point2D scb1 = getscb(quyu);
                quyu.remove(scb1);
                area.setScbStartx(scb1.getX()+"")
                        .setScbStarty(scb1.getY()+"");
                Point2D scb2 = getscb(quyu);
                area.setScbEndx(scb2.getX()+"")
                        .setScbEndy(scb2.getY()+"");

                BigDecimal dance1 = GeometryUtil.pointToSegmentDistance(scb1,segment.getStart(),segment.getEnd());
                BigDecimal dance2 = GeometryUtil.pointToSegmentDistance(scb2,segment.getStart(),segment.getEnd());
                if(dance1.compareTo(dance2) >=  0){
                    area.setScbStartx(scb2.getX()+"")
                            .setScbStarty(scb2.getY()+"");
                    area.setScbEndx(scb1.getX()+"")
                            .setScbEndy(scb1.getY()+"");
                }

                Point2D fscb1 = getfscb(quyu);
                area.setFscbStartx(fscb1.getX()+"")
                        .setFscbStarty(fscb1.getY()+"");
                quyu.remove(fscb1);
                Point2D fscb2 = getfscb(quyu);
                area.setFscbEndx(fscb2.getX()+"")
                        .setFscbEndy(fscb2.getY()+"");

                 dance1 = GeometryUtil.pointToSegmentDistance(fscb1,segment.getStart(),segment.getEnd());
                 dance2 = GeometryUtil.pointToSegmentDistance(fscb2,segment.getStart(),segment.getEnd());
                if(dance1.compareTo(dance2) >=  0){
                    area.setFscbStartx(fscb2.getX()+"")
                            .setFscbStarty(fscb2.getY()+"");
                    area.setFscbEndx(fscb1.getX()+"")
                            .setFscbEndy(fscb1.getY()+"");
                }
                BizDangerAreaDto dto = new BizDangerAreaDto();
                BeanUtils.copyProperties(area,dto);
                areas.add(area);

            }
        }

        List<BizDangerArea> distinctAreas = new ArrayList<>();
        Set<Set<String>> seen = new HashSet<>();

        for (BizDangerArea area : areas) {
            List<String> sdsd = getsssssss(area);
            Set<String> aset = new HashSet<>(sdsd); // 转成 HashSet 去除顺序影响

            if (seen.add(aset)) { // 如果未出现过，就加入
                distinctAreas.add(area);
            }
        }
        areas = distinctAreas;

        List<BizDangerArea> areasources = bizDangerAreaService.list(new QueryWrapper<>());

        List<BizDangerArea> instes = DeepCopyUtil.deepCopyList(areas);

        Iterator<BizDangerArea> it = instes.iterator();
        while (it.hasNext()) {
            BizDangerArea insteArea = it.next();
            List<String> sdsd = getsssssss(insteArea);
            for (BizDangerArea areasource : areasources) {
                List<String> sdd = getsssssss(areasource);
                if (new HashSet<>(sdsd).equals(new HashSet<>(sdd))) {
                    it.remove();
                    break; // 避免多次删除
                }
            }
        }

//        List<BizDangerArea> instes = DeepCopyUtil.deepCopyList(areas);
//        for (BizDangerArea area : areas) {
//            List<String> sdsd =  getsssssss(area);
//            for (BizDangerArea areasource : areasources) {
//                List<String> sdd = getsssssss(areasource);
//                boolean isEqual = new HashSet<>(sdsd).equals(new HashSet<>(sdd));
//                if(isEqual){
//                    boolean s =  instes.remove(area);
//                    System.out.println("s = " + s);
//
//                }
//            }
//        }
        areas = instes;
        int n = 1 ;
        for (BizDangerArea area : areas) {
            BizDangerAreaDto ssss = new BizDangerAreaDto();
            BeanUtils.copyProperties(area,ssss);
            TunnelEntity tunnel =  tunnelService.getById(area.getTunnelId());
            ssss.setStatus(1).setName(tunnel.getTunnelName()+"-"+n+"危险区").setPrePointStatus(0);
            bizDangerAreaService.insertEntity(ssss);
            n++;
        }
        return R.ok();
    }


    /**
     * 从 List<BigDecimal[]> 创建线段，并返回长度最短的两条
     * @param bigDecimals 共4个元素，每个元素是长度为2的数组 [x, y]
     * @return 最长的两个 Segment（降序排列）
     */
    public void toward() {
        List<TunnelEntity> tunnelEntities = tunnelService.list();
        if(tunnelEntities == null || tunnelEntities.size() == 0){
            return;
        }
        for (TunnelEntity tunnelEntity : tunnelEntities) {
            QueryWrapper<BizDangerArea> qw = new QueryWrapper<>();
            qw.lambda().eq(BizDangerArea::getTunnelId,tunnelEntity.getTunnelId());
            List<BizDangerArea> areas = bizDangerAreaService.list(qw);
            for (BizDangerArea area : areas) {
                area.getFscbStartx();
            }
        }

    }


    /**
     * 获取两条线段
     * @param bars
     * @return
     */
    public static List<BigDecimal[]> getSegments(List<BizTunnelBar> bars) {
        List<BigDecimal[]> segments = new ArrayList<>();
        for (BizTunnelBar bar : bars) {
            BigDecimal[] sge = new BigDecimal[2];
            sge[0] = new BigDecimal(bar.getStartx());
            sge[1] = new BigDecimal(bar.getStarty());

            BigDecimal[] sger = new BigDecimal[2];
            sger[0] = new BigDecimal(bar.getEndx());
            sger[1] = new BigDecimal(bar.getEndy());

            segments.add(sge);
            segments.add(sger);
        }
        return segments;

    }
    public  List<String> getsssssss(BizDangerArea area){
        List<String> sssssss = new ArrayList<>();
        sssssss.add(area.getFscbStartx());
        sssssss.add(area.getFscbEndx());
        sssssss.add(area.getScbStarty());
        sssssss.add(area.getScbEndy());
        return sssssss;
    }
    public Point2D getscb(List<Point2D> pointList){
        for (Point2D point2D : pointList) {
            if (point2D.getBarType().equals("scb")) {
                return point2D;
            }
        }
        return null;
    }

    public Point2D getfscb(List<Point2D> pointList){
        for (Point2D point2D : pointList) {
            if (point2D.getBarType().equals("fscb")) {
                return point2D;
            }
        }
        return null;
    }
    /**
     * 新增危险区管理
     */
    @ApiOperation("新增危险区管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:dangerArea:add')")
    @Log(title = "危险区管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody  BizDangerAreaDto dto)
    {

        return R.ok(bizDangerAreaService.insertEntity(dto));
    }


    @ApiOperation("生成预设点")
//    @PreAuthorize("@ss.hasPermi('basicInfo:dangerArea:sss')")
    @Log(title = "危险区管理", businessType = BusinessType.INSERT)
    @PostMapping("/addpre")
    public R addpre( String workfaceName,String drillType)
    {
        Long workfaceId;
        if(StrUtil.isEmpty(workfaceName)){
            return R.ok();
        }
        QueryWrapper<BizWorkface> qw = new QueryWrapper<>();
        qw.lambda().eq(BizWorkface::getWorkfaceName,workfaceName);
        List<BizWorkface> workfaces =  bizWorkfaceMapper.selectList(qw);
        if(workfaces != null && workfaces.size() > 0){
            workfaceId = workfaces.get(0).getWorkfaceId();
        } else {
            workfaceId = 0l;
        }

        QueryWrapper<BizDangerArea> qw1 = new QueryWrapper<>();
        qw1.lambda().eq(BizDangerArea::getWorkfaceId,workfaceId).eq(BizDangerArea::getPrePointStatus,0);
        long count =  bizDangerAreaService.count(qw1);
        if(count <= 0){
            return R.ok();
        }

        if(StrUtil.isEmpty(drillType)){
            drillType = BizBaseConstant.FILL_TYPE_LDPR;
        }
        QueryWrapper<TunnelEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TunnelEntity::getWorkFaceId, workfaceId);
        List<TunnelEntity> tunnelEntities  =  tunnelService.list(queryWrapper);
        for (TunnelEntity tunnelEntity : tunnelEntities) {
            QueryWrapper<BizDangerArea> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.lambda().eq(BizDangerArea::getPrePointStatus,0).eq(BizDangerArea::getTunnelId,tunnelEntity.getTunnelId()).orderByAsc(BizDangerArea::getNo);
            List<BizDangerArea> areas = bizDangerAreaService.list(queryWrapper1);
            List<BizDangerLevel> levels = bizDangerLevelService.list();
            //非生产帮
            List<Segment> fsegments = new ArrayList<>();
            //生产帮
            List<Segment> ssegments = new ArrayList<>();
            for (BizDangerArea area : areas) {
                Double spaced = new Double("1");
                for (BizDangerLevel level : levels) {
                    if(area.getLevel().equals(level.getLevel())){
                        spaced = level.getSpaced();
                    }
                }
                Segment segmentf = GeometryUtil.getSegment(area.getFscbStartx(),area.getFscbStarty(),area.getFscbEndx(),area.getFscbEndy(),spaced.toString(),area.getDangerAreaId());
                fsegments.add(segmentf);

                Segment segments = GeometryUtil.getSegment(area.getScbStartx(),area.getScbStarty(),area.getScbEndx(),area.getScbEndy(),spaced.toString(),area.getDangerAreaId());
                ssegments.add(segments);
            }
            List<Point2D> fpoint2DS =  samplePoints(fsegments);
            QueryWrapper<BizTunnelBar> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.lambda().eq(BizTunnelBar::getTunnelId,tunnelEntity.getTunnelId()).eq(BizTunnelBar::getType,"scb");
            List<BizTunnelBar> bars =  bizTunnelBarMapper.selectList(queryWrapper2);
            BizTunnelBar scbbar = bars.get(0);
            queryWrapper2.clear();
            queryWrapper2.lambda().eq(BizTunnelBar::getTunnelId,tunnelEntity.getTunnelId()).eq(BizTunnelBar::getType,"fscb");
            List<BizTunnelBar> bars1 =  bizTunnelBarMapper.selectList(queryWrapper2);
            BizTunnelBar fsbbar = bars1.get(0);


            String uploadUrl = configService.selectConfigByKey(MapConfigConstant.map_bili);
            String meter = configService.selectConfigByKey(MapConfigConstant.pre_drill);


            ExecutorService executorService = Executors.newFixedThreadPool(8);
            List<Future<BizPresetPoint>> futures = new ArrayList<>();

// 处理 fpoint2DS
            for (Point2D point2D : fpoint2DS) {
                String finalDrillType = drillType;
                futures.add(executorService.submit(() -> {
                    BigDecimal[] biaisai = bizPresetPointService.getExtendedPoint(
                            point2D.getX().toString(),
                            point2D.getY().toString(),
                            fsbbar.getDirectAngle(),
                            Double.parseDouble(meter),
                            Double.parseDouble(uploadUrl)
                    );

                    List<Map<String, Object>> list = new ArrayList<>();
                    Map<String, Object> map = new HashMap<>();
                    map.put("x", point2D.getX());
                    map.put("y", point2D.getY());
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("x", biaisai[0]);
                    map1.put("y", biaisai[1]);
                    list.add(map);
                    list.add(map1);

                    BizPresetPoint bp = new BizPresetPoint();
                    bp.setTunnelId(tunnelEntity.getTunnelId())
                            .setDangerAreaId(point2D.getAreaId())
                            .setWorkfaceId(workfaceId)
                            .setTunnelBarId(fsbbar.getBarId())
                            .setDrillType(finalDrillType)
                            .setAxiss(JSONUtil.toJsonStr(list));
                    return bp;
                }));
            }

// 处理 spoint2DS
            List<Point2D> spoint2DS = samplePoints(ssegments);
            for (Point2D point2D : spoint2DS) {
                String finalDrillType1 = drillType;
                futures.add(executorService.submit(() -> {
                    BigDecimal[] biaisai = bizPresetPointService.getExtendedPoint(
                            point2D.getX().toString(),
                            point2D.getY().toString(),
                            scbbar.getDirectAngle(),
                            Double.parseDouble(meter),
                            Double.parseDouble(uploadUrl)
                    );

                    List<Map<String, Object>> list = new ArrayList<>();
                    Map<String, Object> map = new HashMap<>();
                    map.put("x", point2D.getX());
                    map.put("y", point2D.getY());
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("x", biaisai[0]);
                    map1.put("y", biaisai[1]);
                    list.add(map);
                    list.add(map1);

                    BizPresetPoint bp = new BizPresetPoint();
                    bp.setTunnelId(tunnelEntity.getTunnelId())
                            .setDangerAreaId(point2D.getAreaId())
                            .setWorkfaceId(workfaceId)
                            .setTunnelBarId(scbbar.getBarId())
                            .setDrillType(finalDrillType1)
                            .setAxiss(JSONUtil.toJsonStr(list));
                    return bp;
                }));
            }

// 收集结果
            List<BizPresetPoint> resultList = new ArrayList<>();
            for (Future<BizPresetPoint> future : futures) {
                try {
                    BizPresetPoint bp = future.get();
                    if (bp != null) {
                        resultList.add(bp);
                    }
                } catch (Exception e) {
                    e.printStackTrace(); // 或使用 log.error("线程异常", e);
                }
            }

// 批量保存
            bizPresetPointService.saveBatch(resultList);

// 关闭线程池
            executorService.shutdown();

            for (BizDangerArea area : areas) {
                area.setPrePointStatus(1);
                bizDangerAreaService.updateById(area);

            }

//            for (Point2D point2D : fpoint2DS) {
//                BigDecimal[] biaisai  =  bizPresetPointService.getExtendedPoint(point2D.getX().toString(),point2D.getY().toString(),fsbbar.getDirectAngle(),Double.parseDouble(meter),Double.parseDouble(uploadUrl));
//
//                List<Map<String,Object>> list = new ArrayList<>();
//                Map<String,Object> map = new HashMap<>();
//                map.put("x",point2D.getX());
//                map.put("y",point2D.getY());
//                Map<String,Object> map1 = new HashMap<>();
//                map1.put("x",biaisai[0]);
//                map1.put("y",biaisai[1]);
//                list.add(map);
//                list.add(map1);
//                BizPresetPoint bp = new BizPresetPoint();
//                bp.setTunnelId(tunnelEntity.getTunnelId())
//                        .setDangerAreaId(point2D.getAreaId())
//                        .setWorkfaceId(workfaceId)
//                        .setTunnelBarId(fsbbar.getBarId())
//                        .setDrillType(drillType)
//                        .setAxiss(JSONUtil.toJsonStr(list));
//                bizPresetPointService.save(bp);
//            }
//
//            List<Point2D> spoint2DS =  samplePoints(ssegments);
//            for (Point2D point2D : spoint2DS) {
//                BigDecimal[] biaisai  =  bizPresetPointService.getExtendedPoint(point2D.getX().toString(),point2D.getY().toString(),scbbar.getDirectAngle(),3,Double.parseDouble(uploadUrl));
//
//                List<Map<String,Object>> list = new ArrayList<>();
//                Map<String,Object> map = new HashMap<>();
//                map.put("x",point2D.getX());
//                map.put("y",point2D.getY());
//                Map<String,Object> map1 = new HashMap<>();
//                map1.put("x",biaisai[0]);
//                map1.put("y",biaisai[1]);
//                list.add(map);
//                list.add(map1);
//                BizPresetPoint bp = new BizPresetPoint();
//                bp.setTunnelId(tunnelEntity.getTunnelId())
//                        .setDangerAreaId(point2D.getAreaId())
//                        .setWorkfaceId(workfaceId)
//                        .setTunnelBarId(scbbar.getBarId())
//                        .setDrillType(drillType)
//                        .setAxiss(JSONUtil.toJsonStr(list));
//
//                bizPresetPointService.save(bp);
//            }
            
        }


        return R.ok();
    }


    @Anonymous
    @ApiOperation("获取区域内,所有导线点")
//    @PreAuthorize("@ss.hasPermi('basicInfo:dangerArea:edit')")
    @Log(title = "dddd", businessType = BusinessType.UPDATE)
    @GetMapping("getInPoint")
    public R getInPoint(Long startPointId,Double startMeter,Long endPointId,Double endMeter)
    {
        return R.ok(bizTravePointService.getInPointList(startPointId,startMeter,endPointId,endMeter));
    }



    /**
     * 修改危险区管理
     */
    @ApiOperation("修改危险区管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:dangerArea:edit')")
    @Log(title = "危险区管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody @Validated(value = {GroupUpdate.class}) BizDangerAreaDto dto)
    {
        return R.ok(bizDangerAreaService.updateEntity(dto));
    }

    /**
     * 删除危险区管理
     */
    @ApiOperation("删除危险区管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:dangerArea:remove')")
    @Log(title = "危险区管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dangerAreaIds}")
    public R remove(@PathVariable Long[] dangerAreaIds)
    {

        UpdateWrapper<BizDangerArea> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().in(BizDangerArea::getDangerAreaId, dangerAreaIds).set(BizDangerArea::getDelFlag,BizBaseConstant.DELFLAG_Y);
        return R.ok(bizDangerAreaService.update(updateWrapper));
    }


    /**
     * 删除危险区管理
     */
    @ApiOperation("删除危险区管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:dangerArea:remove')")
    @Log(title = "危险区管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/one/{dangerAreaId}")
    public R remove(@PathVariable("dangerAreaId") Long dangerAreaId)
    {

        BizDangerArea entity = new BizDangerArea();
        entity.setDangerAreaId(dangerAreaId).setDelFlag(BizBaseConstant.DELFLAG_Y);
        return R.ok(bizDangerAreaService.updateById(entity));
    }


    public List<Point2D> getPoint2Ds(List<Segment> segments){
        List<Point2D> sampledPoints = samplePoints(segments);
        // 输出所有点
        return sampledPoints;
    }



//    static List<Point2D> samplePoints(List<Segment> segments) {
//        List<Point2D> points = new ArrayList<>();
//        for (int i = 0; i < segments.size(); i++) {
//            Segment current = segments.get(i);
//            Segment next = (i + 1 < segments.size()) ? segments.get(i + 1) : null;
//
//            BigDecimal dx = current.getEnd().getX().subtract(current.getStart().getX());
//            BigDecimal dy = current.getEnd().getY().subtract(current.getStart().getY());
//            BigDecimal length = hypot(dx, dy, 20); // 保留20位精度
//
//            BigDecimal interval = current.getInterval();
//            if (next != null) {
//                interval = Math.min(current.getInterval(), next.getInterval());
//            }
//
//            int steps = (int) Math.floor(length / interval);
//            for (int j = 0; j < steps; j++) {
//                double ratio = (j * interval) / length;
//                double x = current.getStart().getX() + dx * ratio;
//                double y = current.getStart().getY() + dy * ratio;
//                points.add(new Point2D.Double(x, y));
//            }
//        }
//
//        // 加上最后一个点
//        points.add(segments.get(segments.size() - 1).getEnd());
//        return points;
//    }



    static List<Point2D> samplePoints(List<Segment> segments) {
        List<Point2D> points = new ArrayList<>();
        int precision = 20;

        for (int i = 0; i < segments.size(); i++) {
            Segment current = segments.get(i);
            Segment next = (i + 1 < segments.size()) ? segments.get(i + 1) : null;

            BigDecimal dx = current.getEnd().getX().subtract(current.getStart().getX());
            BigDecimal dy = current.getEnd().getY().subtract(current.getStart().getY());
            BigDecimal length = GeometryUtil.hypot(dx, dy, precision);

            BigDecimal interval = current.getInterval();
//            if (next != null) {
//                interval = interval.min(next.getInterval());
//            }

            if (interval.compareTo(BigDecimal.ZERO) <= 0) continue;

            int steps = length.divide(interval, RoundingMode.FLOOR).intValue();

            for (int j = 0; j < steps; j++) {
                BigDecimal ratio = interval.multiply(BigDecimal.valueOf(j))
                        .divide(length, precision, RoundingMode.HALF_UP);
                BigDecimal x = current.getStart().getX().add(dx.multiply(ratio));
                BigDecimal y = current.getStart().getY().add(dy.multiply(ratio));
                points.add(new Point2D(x, y,current.getAreaId()));
            }
        }

        // 添加最后一个点
        if (!segments.isEmpty()) {
            Point2D end = segments.get(segments.size() - 1).getEnd();
            end.setAreaId(segments.get(segments.size() - 1).getAreaId());
            points.add(end);
        }

        return points;
    }



}
