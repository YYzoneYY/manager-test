package com.ruoyi.web.controller.basicInfo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.constant.GroupAdd;
import com.ruoyi.system.constant.GroupUpdate;
import com.ruoyi.system.domain.BizProjectRecord;
import com.ruoyi.system.domain.BizTravePoint;
import com.ruoyi.system.domain.BizTunnelBar;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.BizTravePointDto;
import com.ruoyi.system.domain.excel.BiztravePointExcel;
import com.ruoyi.system.domain.utils.GeometryUtil;
import com.ruoyi.system.domain.vo.BizTravePointVo;
import com.ruoyi.system.mapper.BizTunnelBarMapper;
import com.ruoyi.system.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 矿井管理Controller
 *
 * @author ruoyi
 * @date 2024-11-11
 */
@Api(tags = "basic-导线点管理")
@RestController
@RequestMapping("/basicInfo/point")
public class BiztravePointController extends BaseController
{
    @Autowired
    private IBizMineService bizMineService;

    @Autowired
    private IBizTravePointService bizTravePointService;

    @Autowired
    private IBizMiningAreaService   bizMiningAreaService;

    @Autowired
    private IBizProjectRecordService bizProjectRecordService;

    @Autowired
    private TunnelService tunnelService;
    @Autowired
    private BizTunnelBarController bizTunnelBarController;
    @Autowired
    private BizTunnelBarMapper bizTunnelBarMapper;

    /**
     * 查询矿井管理列表
     */
    @ApiOperation("查询导线点管理列表")
    @PreAuthorize("@ss.hasPermi('basicInfo:point:list')")
    @GetMapping("/list")
    public R<MPage<BizTravePoint>> list(@ApiParam(name = "workfaceId", value = "工作面id") @RequestParam(required = false) Long workfaceId,
                                        @ApiParam(name = "tunnelId", value = "巷道id") @RequestParam( required = false) Long tunnelId,
                                        @ApiParam(name = "pointName", value = "导线点名称") @RequestParam( required = false) String pointName,
                                        @ParameterObject Pagination pagination)
    {
        QueryWrapper<BizTravePoint> queryWrapper = new QueryWrapper<BizTravePoint>();
        queryWrapper.lambda()
                .like(StrUtil.isNotEmpty( pointName), BizTravePoint::getPointName,pointName)
                .eq(workfaceId != null , BizTravePoint::getWorkfaceId,workfaceId)
                .eq(tunnelId != null , BizTravePoint::getTunnelId,tunnelId)
                .eq(BizTravePoint::getDelFlag, BizBaseConstant.DELFLAG_N);
        IPage<BizTravePoint> list = bizTravePointService.getBaseMapper().selectPage(pagination,queryWrapper);
        return R.ok(new MPage<>(list));
    }


//    @ApiOperation("判断输入导线点加距离是否合法")
////    @PreAuthorize("@ss.hasPermi('basicInfo:point:list')")
//    @GetMapping("/checkJudge")
//    public R<Boolean> checkList(@RequestParam(name = "导线点id", required = false) Long pointId,
//                                            @RequestParam(name = "距离", required = false) Double meter)
//    {
//        if(meter  == 0){
//            return R.ok(true);
//        }
//        if(meter < 0){
//            BizTravePoint prePoint = bizTravePointService.getPrePoint(pointId);
//            if(prePoint == null || prePoint.getPointId() == null){
//                Assert.isTrue(true,"此导线点为第一个导线点,前方向距离为0");
//            }
//            if(Double.parseDouble(prePoint.getPrePointDistance()) ){
//
//            }
//
//        }
//        return R.ok(list);
//    }

    @ApiOperation("下拉导线点列表-根据状态,工作面 查询导线点")
//    @PreAuthorize("@ss.hasPermi('basicInfo:point:list')")
    @GetMapping("/checkList")
    public R<List<BizTravePoint>> checkList(
                                            @ApiParam(name = "workfaceIds", value = "工作面集合") @RequestParam(required = false) Long[] workfaceIds,
                                            @ApiParam(name = "tunnelIds", value = "巷道集合") @RequestParam( required = false) Long[] tunnelIds)
    {
        QueryWrapper<BizTravePoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
//                .in(statuss != null && statuss.length > 0, BizTravePoint::getStatus, statuss)
                .in(tunnelIds != null && tunnelIds.length > 0, BizTravePoint::getTunnelId, tunnelIds)
                .in(workfaceIds != null && workfaceIds.length > 0, BizTravePoint::getWorkfaceId, workfaceIds)
                .eq(BizTravePoint::getDelFlag,BizBaseConstant.DELFLAG_N);
        List<BizTravePoint> list = bizTravePointService.getBaseMapper().selectList(queryWrapper);
        return R.ok(list);
    }

//    @ApiOperation("下拉导线点列表-根据状态,工作面 查询导线点")
////    @PreAuthorize("@ss.hasPermi('basicInfo:point:list')")
//    @GetMapping("/checkList1")
//    public R checkList1(
//                                            @RequestParam(value = "工作面", required = false) Long workfaceId)
//    {
//        QueryWrapper<TunnelEntity> tunnelEntityQueryWrapper = new QueryWrapper<>();
//        tunnelEntityQueryWrapper.lambda().eq(TunnelEntity::getWorkFaceId,workfaceId);
//        long count =  tunnelService.count(tunnelEntityQueryWrapper);
//        Assert.isTrue(count  > 2, "至少需要两个平行巷,一个切眼");
//
//        tunnelEntityQueryWrapper.clear();
//        tunnelEntityQueryWrapper.lambda().select(TunnelEntity::getTunnelId,TunnelEntity::getTunnelType)
//                .eq(TunnelEntity::getWorkFaceId,workfaceId)
//                .in(TunnelEntity::getTunnelType,"QY","XH","SH");
//        List<TunnelEntity> tunnelEntities = tunnelService.list(tunnelEntityQueryWrapper);
//
//        Long qyId = null;
//        Long shId = null;
//        Long xhId = null;
//        for (TunnelEntity tunnelEntity : tunnelEntities) {
//            if("QY".equals(tunnelEntity.getTunnelType())){
//                qyId = tunnelEntity.getTunnelId();
//            }
//            if("SH".equals(tunnelEntity.getTunnelType())){
//                shId = tunnelEntity.getTunnelId();
//            }
//            if("XH".equals(tunnelEntity.getTunnelType())){
//                xhId = tunnelEntity.getTunnelId();
//            }
//        }
//        Assert.isTrue(qyId != null,"此工作面下缺少 切眼巷道");
//        Assert.isTrue(shId != null,"此工作面下缺少 SH巷道");
//        Assert.isTrue(xhId != null,"此工作面下缺少 XH巷道");
//
//        QueryWrapper<BizTravePoint> queryWrapper = new QueryWrapper<>();
//        queryWrapper.lambda()
//                .eq(BizTravePoint::getIsVertex,true)
//                .eq( BizTravePoint::getTunnelId, qyId)
//                .eq(BizTravePoint::getDelFlag,BizBaseConstant.DELFLAG_N);
//        List<BizTravePoint> qylist = bizTravePointService.getBaseMapper().selectList(queryWrapper);
//
//        Assert.isTrue(qylist != null && qylist.size() == 2 ,"该巷道下顶点个数不正确");
//
//        //上巷
//        queryWrapper.clear();
//        queryWrapper.lambda().eq(BizTravePoint::getTunnelId,shId)
//                .eq(BizTravePoint::getIsVertex,true)
//                .orderBy(true,true,BizTravePoint::getDistance);
//        List<BizTravePoint> shVertex = bizTravePointService.getBaseMapper().selectList(queryWrapper);
//
//
//        //下巷
//        queryWrapper.clear();
//        queryWrapper.lambda().eq(BizTravePoint::getTunnelId,xhId)
//                .orderBy(true,true,BizTravePoint::getDistance);
//        List<BizTravePoint> xhlist = bizTravePointService.getBaseMapper().selectList(queryWrapper);
//
//
//        Map<String,List<BizTravePoint>> map = new HashMap<>();
//        map.put("SH",shVertex);
//        map.put("XH",xhlist);
//        return R.ok(map);
//    }
//
//    private Double getMath(Point a, Point n){
//        BigDecimal ax = a.getX().subtract(n.getX());
//        BigDecimal ay = a.getY().subtract(n.getY());
//        BigDecimal az = a.getZ().subtract(n.getZ());
//
//        ax = ax.multiply(ax);
//        ay = ay.multiply(ay);
//        az = az.multiply(az);
//
//        Double an = Math.sqrt(ax.add(ay).add(az).doubleValue());
//        return an;
//    }
//
//    private Point zhuanhuan(BizTravePoint bizTravePoint){
//        Point point = new Point();
//        point.setX(new BigDecimal(bizTravePoint.getAxisx()));
//        point.setY(new BigDecimal(bizTravePoint.getAxisy()));
//        point.setZ(new BigDecimal(bizTravePoint.getAxisz()));
//        return point;
//    }


    @ApiOperation("尺子")
//    @PreAuthorize("@ss.hasPermi('basicInfo:point:list')")
    @GetMapping("/rule")
    public R<MPage<BizTravePointVo>> rule(@RequestParam Long locationId,@RequestParam String constructType,@ParameterObject Pagination pagination)
    {
        return R.ok(bizTravePointService.geRuleList(locationId,constructType,pagination));
    }


    @Log(title = "导线点导入", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('basicInfo:point:import')")
    @ApiOperation("导线点导入")
    @PostMapping("/importData")
    public R importData(@RequestPart("file") MultipartFile file) throws Exception
    {
        ExcelUtil<BiztravePointExcel> util = new ExcelUtil<BiztravePointExcel>(BiztravePointExcel.class);
        List<BiztravePointExcel> excels = util.importExcel(file.getInputStream());

        Map<String, List<BiztravePointExcel>> groupbyTunnel = excels
                .stream().collect(Collectors.groupingBy(BiztravePointExcel::getTunnelName));
        String username =  getUsername();
        //检查
        groupbyTunnel.forEach((tunnelName, group) -> {
            QueryWrapper<TunnelEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(TunnelEntity::getTunnelName,tunnelName);
            Long count = tunnelService.getBaseMapper().selectCount(queryWrapper);
            if(count != 1l){
                Assert.isTrue(true,"请检查<"+tunnelName+">是否正确");
            }
            TunnelEntity tunnel =  tunnelService.getBaseMapper().selectOne(queryWrapper);
            List<String> pointNames =   group.stream().map(BiztravePointExcel::getPointName).collect(Collectors.toList());
            Set<String> uniquePoints = new HashSet<>(pointNames);
            if (uniquePoints.size() < pointNames.size()) {
                Assert.isTrue(true,"请检查<"+tunnelName+">中导线点名称是否重复");
            }

            QueryWrapper<BizTravePoint> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.lambda().select(BizTravePoint::getPointName).eq(BizTravePoint::getTunnelId,tunnel.getTunnelId());
            List<BizTravePoint> points =  this.bizTravePointService.getBaseMapper().selectList(queryWrapper1);
            List<String> sourcePoints = points.stream().map(BizTravePoint::getPointName).collect(Collectors.toList());
            if(sourcePoints.containsAll(pointNames)){
                Assert.isTrue(true,"请检查<"+tunnelName+">中导线点名称与数据库中数据是否重复");
            }


        });

        groupbyTunnel.forEach((tunnelName, group) -> {
            QueryWrapper<TunnelEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(TunnelEntity::getTunnelName,tunnelName);
            TunnelEntity tunnel =  tunnelService.getBaseMapper().selectOne(queryWrapper);

            group.sort(Comparator.comparing(BiztravePointExcel::getNo));

            for (BiztravePointExcel biztravePointExcel : group) {
                BizTravePoint bizTravePoint = new BizTravePoint();
                BeanUtils.copyProperties(biztravePointExcel,bizTravePoint);
                bizTravePoint.setTunnelId(tunnel.getTunnelId()).setWorkfaceId(tunnel.getWorkFaceId()).setCreateBy(username);
                if(bizTravePoint.getNo() != 1){
                    QueryWrapper<BizTravePoint> queryWrapper1 = new QueryWrapper<>();
                    queryWrapper1.lambda().eq(BizTravePoint::getTunnelId,tunnel.getTunnelId())
                            .eq(BizTravePoint::getNo,bizTravePoint.getNo()-1);
                    BizTravePoint pre = bizTravePointService.getBaseMapper().selectOne(queryWrapper1);
                    bizTravePoint.setPrePointId(pre.getPointId());
                }
                this.bizTravePointService.getBaseMapper().insert(bizTravePoint);
            }
        });
        return R.ok();



//        String operName = getUsername();
//        List<BizTravePoint> list = new ArrayList<BizTravePoint>();
//        for (BiztravePointExcel biztravePointExcel : userList) {
//            BizTravePoint bizTravePoint = new BizTravePoint();
//            BeanUtils.copyProperties(biztravePointExcel, bizTravePoint);
//            QueryWrapper<BizTravePoint> queryWrapper = new QueryWrapper<>();
//            queryWrapper.lambda().eq()
//            bizTravePointService.getBaseMapper().selectOne()
//            list.add(bizTravePoint);
//
//
//        }

//
//        String message = userService.importUser(userList, updateSupport, operName);
//        return success(message);
    }

    @ApiOperation("模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<BiztravePointExcel> util = new ExcelUtil<BiztravePointExcel>(BiztravePointExcel.class);
        util.importTemplateExcel(response, "模板");
    }




    /**
     * 获取矿井管理详细信息
     */
    @ApiOperation("获取前导线点管理详细信息")
//    @PreAuthorize("@ss.hasPermi('basicInfo:point:query')")
    @GetMapping(value = "/prePoint")
    public R<BizTravePoint> getPrePointDistance(BizTravePointDto dto) {
        Assert.isTrue(dto.getTunnelId() != null,"请填写巷道");
        Assert.isTrue(StrUtil.isNotEmpty(dto.getAxisx()) && StrUtil.isNotEmpty(dto.getAxisy()) && StrUtil.isNotEmpty(dto.getAxisz()),"请填写巷道");
        return R.ok(bizTravePointService.getPrePointDistance(dto));
    }

    @ApiOperation("获取导线点管理详细信息")
    @PreAuthorize("@ss.hasPermi('basicInfo:point:query')")
    @GetMapping(value = "/{pointId}")
    public R<BizTravePoint> getInfo(@PathVariable("pointId") Long pointId)
    {
        return R.ok(bizTravePointService.getBaseMapper().selectById(pointId));
    }





//    @ApiOperation("获取钻孔经纬度")
////    @PreAuthorize("@ss.hasPermi('basicInfo:mine:query')")
//    @GetMapping(value = "/getlatlon")
//    public R getDillLatLon(@RequestParam Long pointId,@RequestParam String distance)
//    {
//
//        BizTravePoint point =  bizTravePointService.getById(pointId);
//        Double.parseDouble(distance.substring(1));
//        BizTravePoint nearPoint = bizTravePointService.getNearPoint(point);
//        String direction = distance.substring(0, 1);
//        Double directionnum = null;
//        if(direction.equals("-")){
//            directionnum = Double.parseDouble(distance);
//        }else if(direction.equals("+")){
//            String s = distance.substring(1);
//            directionnum = Double.parseDouble(s);
//        }
//        BizTravePoint npoint = bizTravePointService.getPoint(point,nearPoint,directionnum);
//
//        double[] s = MathUtil.mapCartesianToGeodetic(7796.1399,194.9874,0,36.1977,117.206,
//                113.5023,7222.6087,0, 36.2486,117.137 ,
//                Double.parseDouble(npoint.getAxisx()),Double.parseDouble(npoint.getAxisy()),Double.parseDouble(npoint.getAxisz()));
//
//        return R.ok(s);
//    }

//    @ApiOperation("获取钻孔坐标")
////    @PreAuthorize("@ss.hasPermi('basicInfo:mine:query')")
//    @GetMapping(value = "/getAxis")
//    public R<BizTravePoint> getDillAxis(@RequestParam Long pointId,@RequestParam String distance)
//    {
//        BizTravePoint point =  bizTravePointService.getById(pointId);
//        String direction = distance.substring(0, 1);
//        Double directionnum = null;
//        if(direction.equals("-")){
//            directionnum = Double.parseDouble(distance);
//        }else if(direction.equals("+")){
//            String s = distance.substring(1);
//            directionnum = Double.parseDouble(s);
//        }
//        BizTravePoint nearPoint = bizTravePointService.getNearPoint(point);
//
//        return R.ok(bizTravePointService.getPoint(point,nearPoint,directionnum));
//    }
//
//
//    /**
//     * 新增矿井管理 先维护上下巷道,再维护切眼
//     */
//    @ApiOperation("新增导线点管理-CAD")
//    @PreAuthorize("@ss.hasPermi('basicInfo:point:add')")
//    @Log(title = "新增导线点管理", businessType = BusinessType.INSERT)
//    @PostMapping("/cad")
//    public R addcad(@RequestBody  List<BizTravePointDto> dtos)
//    {
//        if(dtos != null && dtos.size() > 0){
//            List<BizTravePoint> points = new ArrayList<>(dtos.size());
//            List<String> uniqueNames = dtos.stream()
//                    .map(BizTravePointDto::getTunnelName)
//                    .distinct()
//                    .collect(Collectors.toList());
//
//            QueryWrapper<TunnelEntity> queryWrapper = new QueryWrapper<>();
//            queryWrapper.lambda().in(TunnelEntity::getTunnelName,uniqueNames);
//            List<TunnelEntity> tunnelEntityList =  tunnelService.getBaseMapper().selectList(queryWrapper);
//            if(tunnelEntityList == null || tunnelEntityList.size() == 0){
//                return R.fail("没有添加巷道");
//            }else if(tunnelEntityList != null && tunnelEntityList.size() != uniqueNames.size()){
//                return R.fail("巷道没有添加全");
//            }
//            int no = 1;
//            for (BizTravePointDto dto : dtos) {
//                BizTravePoint bizTravePoint = new BizTravePoint();
//                BeanUtils.copyProperties(dto,bizTravePoint);
//                bizTravePoint.setNo(Long.parseLong(no+""));
//                for (TunnelEntity tunnel : tunnelEntityList) {
//                    if(tunnel.getTunnelName().equals(dto.getTunnelName())){
//                        dto.setTunnelId(tunnel.getTunnelId());
//                        points.add(bizTravePoint);
//                    }
//                }
//                no++;
//            }
//            bizTravePointService.saveBatch(points);
//            return R.ok();
//        }
//        return R.fail("空数据");
//    }

    /**
     * 新增矿井管理 先维护上下巷道,再维护切眼
     */
    @ApiOperation("新增导线点管理-CAD")
    @PreAuthorize("@ss.hasPermi('basicInfo:point:add')")
    @Log(title = "新增导线点管理", businessType = BusinessType.INSERT)
    @PostMapping("/cad")
    public R sssssaa(@RequestBody  List<BizTravePointDto> dtos)
    {
        if(dtos != null && dtos.size() > 0){
            List<BizTravePoint> points = new ArrayList<>(dtos.size());
            List<String> uniqueNames = dtos.stream()
                    .map(BizTravePointDto::getTunnelName)
                    .distinct()
                    .collect(Collectors.toList());

            QueryWrapper<TunnelEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(TunnelEntity::getTunnelName,uniqueNames);
            List<TunnelEntity> tunnelEntityList =  tunnelService.getBaseMapper().selectList(queryWrapper);
            if(tunnelEntityList == null || tunnelEntityList.size() == 0){
                return R.fail("没有添加巷道");
            }else if(tunnelEntityList != null && tunnelEntityList.size() != uniqueNames.size()){
                return R.fail("巷道没有添加全");
            }
            long no = 1l;
            for (BizTravePointDto dto : dtos) {
                BizTravePoint bizTravePoint = new BizTravePoint();
                BeanUtils.copyProperties(dto,bizTravePoint);
                bizTravePoint.setNo(Long.parseLong(no+""));
                for (TunnelEntity tunnel : tunnelEntityList) {
                    if(tunnel.getTunnelName().equals(dto.getTunnelName())){
                        bizTravePoint.setTunnelId(tunnel.getTunnelId());
                        points.add(bizTravePoint);
                    }
                }
                no++;
            }
            bizTravePointService.saveBatch(points);

            for (TunnelEntity tunnel : tunnelEntityList) {
                QueryWrapper<BizTravePoint> wrapper = new QueryWrapper<>();
                wrapper.lambda().in(BizTravePoint::getTunnelId,tunnel.getTunnelId()).orderByDesc(BizTravePoint::getNo);
                List<BizTravePoint> pointList = bizTravePointService.list(wrapper);
                for (BizTravePoint point : pointList) {
                    //获取帮
                    QueryWrapper<BizTunnelBar> queryWrapper1 = new QueryWrapper<>();
                    queryWrapper1.lambda().eq(BizTunnelBar::getTunnelId,tunnel.getTunnelId());
                    List<BizTunnelBar> bars = bizTunnelBarMapper.selectList(queryWrapper1);
                    BizTunnelBar bizTunnelBar = new BizTunnelBar();
                    if(bars != null && bars.size()>0){
                        bizTunnelBar = bars.get(0);
                    }
                    //巷道帮上的点
                    BigDecimal[] dians =  GeometryUtil.getClosestPointOnSegment(point.getAxisx(),point.getAxisy(),bizTunnelBar.getStartx(),bizTunnelBar.getStarty(),bizTunnelBar.getEndx(),bizTunnelBar.getEndy());
                    QueryWrapper<BizTravePoint> queryWrapper2 = new QueryWrapper<>();
                    queryWrapper2.lambda().eq(BizTravePoint::getNo,point.getNo()-1);
                    List<BizTravePoint> travePoints = bizTravePointService.getBaseMapper().selectList(queryWrapper2);
                    if(travePoints != null && travePoints.size()>0){
                        String prex = travePoints.get(0).getAxisx();
                        String prey = travePoints.get(0).getAxisy();
                        BigDecimal[] dianss =  GeometryUtil.getClosestPointOnSegment(prex,prey,bizTunnelBar.getStartx(),bizTunnelBar.getStarty(),bizTunnelBar.getEndx(),bizTunnelBar.getEndy());
                        BigDecimal distance = GeometryUtil.getDistance(dians[0],dians[1],dianss[0],dianss[1]);
                        point.setPrePointDistance(distance.doubleValue()).setPrePointId(travePoints.get(0).getPointId());
                        bizTravePointService.updateById(point);
                    }
                }
            }

            return R.ok();
        }
        return R.fail("空数据");
    }



    /**
     * 新增矿井管理 先维护上下巷道,再维护切眼
     */
    @ApiOperation("新增导线点管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:point:add')")
    @Log(title = "新增导线点管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody @Validated(value = {GroupAdd.class}) BizTravePointDto dto)
    {
        BizTravePoint entity = new BizTravePoint();
        BeanUtil.copyProperties(dto, entity);
        QueryWrapper<BizTravePoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BizTravePoint::getPointName,dto.getPointName())
                .eq(BizTravePoint::getTunnelId, dto.getTunnelId());
        long i = bizTravePointService.count(queryWrapper);
        Assert.isTrue(i <= 0 , "名称重复");
        queryWrapper.clear();
        //获取帮
        QueryWrapper<BizTunnelBar> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.lambda().eq(BizTunnelBar::getTunnelId,dto.getTunnelId());
        List<BizTunnelBar> bars = bizTunnelBarMapper.selectList(queryWrapper1);
        BizTunnelBar bizTunnelBar = new BizTunnelBar();
        if(bars != null && bars.size()>0){
            bizTunnelBar = bars.get(0);
        }
        //巷道帮上的点
        BigDecimal[] dians =  GeometryUtil.getClosestPointOnSegment(dto.getAxisx(),dto.getAxisy(),bizTunnelBar.getStartx(),bizTunnelBar.getStarty(),bizTunnelBar.getEndx(),bizTunnelBar.getEndy());
        QueryWrapper<BizTravePoint> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.lambda().eq(BizTravePoint::getNo,dto.getNo()-1);
        List<BizTravePoint> points = bizTravePointService.getBaseMapper().selectList(queryWrapper2);
        if(points != null && points.size()>0){
           String prex = points.get(0).getAxisx();
           String prey = points.get(0).getAxisy();
            BigDecimal[] dianss =  GeometryUtil.getClosestPointOnSegment(prex,prey,bizTunnelBar.getStartx(),bizTunnelBar.getStarty(),bizTunnelBar.getEndx(),bizTunnelBar.getEndy());
            BigDecimal distance = GeometryUtil.getDistance(dians[0],dians[1],dianss[0],dianss[1]);
            entity.setPrePointDistance(distance.doubleValue()).setPrePointId(points.get(0).getPointId());
        }
        return R.ok(bizTravePointService.getBaseMapper().insert(entity));
    }

    public static void ssss(BizTravePointDto dto,BizTunnelBar bizTunnelBar,BizTravePointDto predto ){
        //当前点在巷道帮上的坐标
        BigDecimal[] dians =  GeometryUtil.getClosestPointOnSegment(dto.getAxisx(),dto.getAxisy(),bizTunnelBar.getStartx(),bizTunnelBar.getStarty(),bizTunnelBar.getEndx(),bizTunnelBar.getEndy());

        //前一个点在巷道帮上的坐标
        BigDecimal[] dianqs =  GeometryUtil.getClosestPointOnSegment(predto.getAxisx(),predto.getAxisy(),bizTunnelBar.getStartx(),bizTunnelBar.getStarty(),bizTunnelBar.getEndx(),bizTunnelBar.getEndy());


    }


    /**
     * 修改矿井管理
     */
    @ApiOperation("修改导线点管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:point:edit')")
    @Log(title = "修改导线点管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody @Validated(value = {GroupUpdate.class}) BizTravePointDto dto)
    {
        BizTravePoint source = this.bizTravePointService.getById(dto.getPointId());
//        if(!source.getDistance().equals(dto.getDistance()) ){
//            QueryWrapper<BizProjectRecord> queryWrapper = new QueryWrapper<>();
//            queryWrapper.lambda().eq(BizProjectRecord::getTravePoint,dto.getPointId());
//            long count = bizProjectRecordService.getBaseMapper().selectCount(queryWrapper);
//            Assert.isTrue(count<=0,"已经填报过,不能修改距上一个导线点的距离");
//        }
        //todo 填报和计划里有的 , 不能修改 距前一个点距离



        BizTravePoint entity = new BizTravePoint();
        entity.setPointId(dto.getPointId())
                .setNo(dto.getNo())
//                .setDistance(dto.getDistance())
                .setPrePointDistance(dto.getPrePointDistance());
        QueryWrapper<BizTravePoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BizTravePoint::getPointName,dto.getPointName())
                .ne(BizTravePoint::getPointId,dto.getPointId())
                .eq(BizTravePoint::getTunnelId, dto.getTunnelId());
        long i = bizTravePointService.count(queryWrapper);
        Assert.isTrue(i <= 0 , "名称重复");
        return R.ok(bizTravePointService.updateById(entity));
    }


//    /**
//     * 新增矿井管理 先维护上下巷道,再维护切眼
//     */
//    @ApiOperation("新增导线点管理")
//    @PreAuthorize("@ss.hasPermi('basicInfo:point:add')")
//    @Log(title = "新增导线点管理", businessType = BusinessType.INSERT)
//    @PostMapping
//    public R add(@RequestBody @Validated(value = {GroupAdd.class}) BizTravePointDto dto)
//    {
//        BizTravePoint entity = new BizTravePoint();
//        BeanUtil.copyProperties(dto, entity);
//        QueryWrapper<BizTravePoint> queryWrapper = new QueryWrapper<>();
//        queryWrapper.lambda().eq(BizTravePoint::getPointName,dto.getPointName())
//                .eq(BizTravePoint::getTunnelId, dto.getTunnelId());
//        long i = bizTravePointService.count(queryWrapper);
//        Assert.isTrue(i <= 0 , "名称重复");
//
//        TunnelEntity tunnelEntity = tunnelService.getById(dto.getTunnelId());
//        if(BizBaseConstant.TUNNEL_SH.equals(tunnelEntity.getTunnelType()) || BizBaseConstant.TUNNEL_XH.equals(tunnelEntity.getTunnelType())){
//            List<BizTravePoint> qypoints = bizTravePointService.getQyPoint(dto.getWorkfaceId());
//            if(qypoints != null && qypoints.size() == 2){
//                Double q0 = MathUtil.getMinDistance(
//                        entity.getAxisx(),entity.getAxisy(),entity.getAxisz(),
//                        qypoints.get(0).getAxisx(),qypoints.get(0).getAxisy(),qypoints.get(0).getAxisz());
//
//                Double q1 = MathUtil.getMinDistance(
//                        entity.getAxisx(),entity.getAxisy(),entity.getAxisz(),
//                        qypoints.get(1).getAxisx(),qypoints.get(1).getAxisy(),qypoints.get(1).getAxisz());
//                if(q0 != null && q1 != null && q0 < q1){
//                    entity.setBestNearPointId(qypoints.get(0).getPointId()).setDistance(q0);
//                }else {
//                    entity.setBestNearPointId(qypoints.get(1).getPointId()).setDistance(q1);
//                }
//            }
//        } else if (BizBaseConstant.TUNNEL_QY.equals(tunnelEntity.getTunnelType()) ) {
//            if(dto.getIsVertex() != null && !dto.getIsVertex()){
//                long counted = bizTravePointService.getVertexCount(dto.getPointId(),dto.getTunnelId(),true);
//                Assert.isTrue(counted <= 2 , "最多有两个顶点");
//                bizTravePointService.doit(entity);
//                return R.ok(bizTravePointService.getBaseMapper().insert(entity));
//            }
//        }
//
//        return R.ok(bizTravePointService.getBaseMapper().insert(entity));
//    }




//    /**
//     * 修改矿井管理
//     */
//    @ApiOperation("修改导线点管理")
//    @PreAuthorize("@ss.hasPermi('basicInfo:point:edit')")
//    @Log(title = "修改导线点管理", businessType = BusinessType.UPDATE)
//    @PutMapping
//    public R edit(@RequestBody @Validated(value = {GroupUpdate.class}) BizTravePointDto dto)
//    {
//        BizTravePoint entity = new BizTravePoint();
//        BeanUtil.copyProperties(dto, entity);
//        QueryWrapper<BizTravePoint> queryWrapper = new QueryWrapper<>();
//        queryWrapper.lambda().eq(BizTravePoint::getPointName,dto.getPointName())
//                .ne(BizTravePoint::getPointId,dto.getPointId())
//                .eq(BizTravePoint::getTunnelId, dto.getTunnelId());
//        long i = bizTravePointService.count(queryWrapper);
//        Assert.isTrue(i <= 0 , "名称重复");
//
//        TunnelEntity tunnelEntity = tunnelService.getById(dto.getTunnelId());
//        if(BizBaseConstant.TUNNEL_SH.equals(tunnelEntity.getTunnelType()) || BizBaseConstant.TUNNEL_XH.equals(tunnelEntity.getTunnelType())){
//            List<BizTravePoint> qypoints = bizTravePointService.getQyPoint(dto.getWorkfaceId());
//            if(qypoints != null && qypoints.size() == 2){
//                Double q0 = MathUtil.getMinDistance(
//                        entity.getAxisx(),entity.getAxisy(),entity.getAxisz(),
//                        qypoints.get(0).getAxisx(),qypoints.get(0).getAxisy(),qypoints.get(0).getAxisz());
//
//                Double q1 = MathUtil.getMinDistance(
//                        entity.getAxisx(),entity.getAxisy(),entity.getAxisz(),
//                        qypoints.get(1).getAxisx(),qypoints.get(1).getAxisy(),qypoints.get(1).getAxisz());
//                if(q0 != null && q1 != null && q0 < q1){
//                    entity.setBestNearPointId(qypoints.get(0).getPointId()).setDistance(q0);
//                }else {
//                    entity.setBestNearPointId(qypoints.get(1).getPointId()).setDistance(q1);
//                }
//            }
//        } else if (BizBaseConstant.TUNNEL_QY.equals(tunnelEntity.getTunnelType())  ) {
//            if(dto.getIsVertex()){
//                long counted = bizTravePointService.getVertexCount(dto.getPointId(),dto.getTunnelId(),true);
//                Assert.isTrue(counted <= 2 , "最多有两个顶点");
//                bizTravePointService.updateById(entity);
//                bizTravePointService.doit(entity);
//                return R.ok(1);
//            }
//        }
//        return R.ok(bizTravePointService.updateById(entity));
//    }

    /**
     * 删除矿井管理
     */
    @ApiOperation("删除导线点管理批量")
    @PreAuthorize("@ss.hasPermi('basicInfo:point:remove')")
    @Log(title = "删除导线点管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{pointIds}")
    public R remove(@PathVariable Long[] pointIds)
    {
        //todo 需要校验 是否在计划内
        QueryWrapper<BizProjectRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(BizProjectRecord::getTravePointId,pointIds);
        long count = bizProjectRecordService.getBaseMapper().selectCount(queryWrapper);
        Assert.isTrue(count<=0,"已经填报过,不能删除");
        UpdateWrapper<BizTravePoint> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().in(BizTravePoint::getPointId, pointIds).set(BizTravePoint::getDelFlag,BizBaseConstant.DELFLAG_Y);
        return R.ok(bizTravePointService.update(updateWrapper));
    }


    /**
     * 删除矿井管理
     */
    @ApiOperation("删除导线点管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:point:remove')")
    @Log(title = "删除导线点管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/one/{pointId}")
    public R remove(@PathVariable("pointId") Long pointId)
    {
        //todo 需要校验 是否在计划内
        QueryWrapper<BizProjectRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BizProjectRecord::getTravePointId,pointId);
        long count = bizProjectRecordService.getBaseMapper().selectCount(queryWrapper);
        Assert.isTrue(count<=0,"已经填报过,不能删除");
        BizTravePoint entity = new BizTravePoint();
        entity.setPointId(pointId).setDelFlag(BizBaseConstant.DELFLAG_Y);
        return R.ok(bizTravePointService.updateById(entity));
    }



}
