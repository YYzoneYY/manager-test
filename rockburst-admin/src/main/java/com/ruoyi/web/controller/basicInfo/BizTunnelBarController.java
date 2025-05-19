package com.ruoyi.web.controller.basicInfo;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.constant.GroupUpdate;
import com.ruoyi.system.domain.BizDangerArea;
import com.ruoyi.system.domain.BizTravePoint;
import com.ruoyi.system.domain.BizTunnelBar;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.BizTunnelBarDto;
import com.ruoyi.system.domain.utils.GeometryUtil;
import com.ruoyi.system.domain.vo.BizTunnelBarVo;
import com.ruoyi.system.mapper.BizDangerAreaMapper;
import com.ruoyi.system.mapper.BizTravePointMapper;
import com.ruoyi.system.mapper.BizWorkfaceMapper;
import com.ruoyi.system.mapper.TunnelMapper;
import com.ruoyi.system.service.IBizTunnelBarService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * 巷道帮管理Controller
 *
 * @author ruoyi
 * @date 2024-11-11
 */
@Api(tags = "basic-巷道帮管理")
//@Tag(description = "巷道帮管理Controller", name = "巷道帮管理Controller")
@RestController
@RequestMapping("/basicInfo/bar")
public class BizTunnelBarController extends BaseController
{
    @Autowired
    private IBizTunnelBarService bizTunnelBarService;
    @Autowired
    private TunnelMapper tunnelMapper;
    @Autowired
    private BizTravePointMapper bizTravePointMapper;
    @Autowired
    private BizDangerAreaMapper bizDangerAreaMapper;
    @Autowired
    private BizWorkfaceMapper bizWorkfaceMapper;


//    /**
//     * 钻孔信息 比例
//     * 传入两个点 ,和距离 计算实际距离与坐标的比例保存在所有帮内
//     * @param startLat
//     * @param startLon
//     * @param endLat
//     * @param endLon
//     * @param drillrange
//     * @return
//     */
//    @ApiOperation("调整钻孔信息比例")
////    @PreAuthorize("@ss.hasPermi('basicInfo:bar:list')")
//    @PostMapping("/barRange")
//    public R barRange(@RequestParam(required = false) String startLat,
//                      @RequestParam(required = false) String startLon,
//                      @RequestParam(required = false) String endLat,
//                      @RequestParam(required = false) String endLon,
//                      @RequestParam(required = false) String drillrange)
//    {
//        BigDecimal startLatBd = new BigDecimal(startLat);
//        BigDecimal startLonBd = new BigDecimal(startLon);
//        BigDecimal endLatBd = new BigDecimal(endLat);
//        BigDecimal endLonBd = new BigDecimal(endLon);
//        BigDecimal drillrangeBd = new BigDecimal(drillrange);
//        BigDecimal latMove = startLatBd.subtract(endLatBd).abs();
//        BigDecimal lonMove = startLonBd.subtract(endLonBd).abs();
//
//        // dx^2
//        BigDecimal dxSquare = latMove.pow(2);
//        // dy^2
//        BigDecimal dySquare = lonMove.pow(2);
//        // dx^2 + dy^2
//        BigDecimal sum = dxSquare.add(dySquare);
//
//        BigDecimal lonlatrange = new BigDecimal(Math.sqrt(sum.doubleValue())).abs().setScale(10, RoundingMode.HALF_DOWN);
//
//        BigDecimal bili = lonlatrange.divide(drillrangeBd, 10, RoundingMode.HALF_DOWN);
//
//        List<BizTunnelBar> bizTunnelBars =  bizTunnelBarService.list();
//        for (BizTunnelBar bizTunnelBar : bizTunnelBars) {
//            UpdateWrapper<BizTunnelBar> updateWrapper = new UpdateWrapper<>();
//            updateWrapper.lambda().set(BizTunnelBar::getDirectRange,bili).eq(BizTunnelBar::getBarId,bizTunnelBar.getBarId()).eq(BizTunnelBar::getDelFlag,BizBaseConstant.DELFLAG_N);
//            bizTunnelBarService.update(updateWrapper);
//        }
//
//        return R.ok();
//    }


    /**
     * 根据导线点序号大小设置巷道帮的走向
     * @return
     */
    @ApiOperation("设置走向")
        @PostMapping("/set_toward_angle")
    public R set_toward_angle()
    {
        List<TunnelEntity> tunnels = tunnelMapper.selectList(new QueryWrapper<>());
        for (TunnelEntity tunnel : tunnels) {
            QueryWrapper<BizTravePoint> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(BizTravePoint::getTunnelId,tunnel.getTunnelId()).orderByAsc(BizTravePoint::getNo);
            List<BizTravePoint> points  = bizTravePointMapper.selectList(queryWrapper);
            if(points == null || points.size() == 0){
                continue;
            }
            Optional<BizTravePoint> minPoint = points.stream()
                    .min(Comparator.comparing(BizTravePoint::getNo));
            Optional<BizTravePoint> maxPoint = points.stream()
                    .max(Comparator.comparing(BizTravePoint::getNo));
            BigDecimal ll = GeometryUtil.calculateAngleFromYAxis(minPoint.get().getAxisx(), minPoint.get().getAxisy(),maxPoint.get().getAxisx(), maxPoint.get().getAxisy());
            QueryWrapper<BizTunnelBar> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.lambda().eq(BizTunnelBar::getTunnelId,tunnel.getTunnelId());
            List<BizTunnelBar> bars =  bizTunnelBarService.getBaseMapper().selectList(queryWrapper2);

            BigDecimal[][] pss = new BigDecimal[4][2];


            BizWorkface workface =  bizWorkfaceMapper.selectById(tunnel.getWorkFaceId());


            BigDecimal[] lll = GeometryUtil.parsePoint(workface.getCenter());
            for (int i = 0; i < bars.size(); i++) {
                bars.get(i).setTowardAngle(ll.doubleValue());
                if(bars.get(i).getType().equals("fscb")){
                    BigDecimal[] aa = new BigDecimal[2];
                    aa[0] = new BigDecimal(bars.get(i).getStartx());
                    aa[1] = new BigDecimal(bars.get(i).getStarty());

                    BigDecimal[] ab = new BigDecimal[2];
                    ab[0] = new BigDecimal(bars.get(i).getEndx());
                    ab[1] = new BigDecimal(bars.get(i).getEndy());
                    double sssas =  GeometryUtil.angleWithYAxisOfPerpendicular(aa,ab,lll);
                    bars.get(i).setDirectAngle((int)sssas)
                            .setYtAngle((int)ll.doubleValue());
                }

                if(bars.get(i).getType().equals("scb")){
                    BigDecimal[] aa = new BigDecimal[2];
                    aa[0] = new BigDecimal(bars.get(i).getStartx());
                    aa[1] = new BigDecimal(bars.get(i).getStarty());

                    BigDecimal[] ab = new BigDecimal[2];
                    ab[0] = new BigDecimal(bars.get(i).getEndx());
                    ab[1] = new BigDecimal(bars.get(i).getEndy());
                    double sssas =  GeometryUtil.angleWithYAxisOfPerpendicular(aa,ab,lll);
                    bars.get(i).setDirectAngle((int)sssas -180)
                            .setYtAngle((int)ll.doubleValue());
                }
                bizTunnelBarService.updateById(bars.get(i));
                BigDecimal[] ps = new BigDecimal[2];
                ps[0] = new BigDecimal(bars.get(i).getStartx());
                ps[1] = new BigDecimal(bars.get(i).getStarty());
                pss[i*2] =  ps;
                BigDecimal[] ps1 = new BigDecimal[2];
                ps1[0] = new BigDecimal(bars.get(i).getEndx());
                ps1[1] = new BigDecimal(bars.get(i).getEndy());
                pss[i*2+1] = ps1;
            }

            BigDecimal[] mn = new BigDecimal[2];
            mn[0] = new BigDecimal(minPoint.get().getAxisx());
            mn[1] = new BigDecimal(minPoint.get().getAxisy());
            BigDecimal[] min =  GeometryUtil.findNearestPoint(pss,mn);


            QueryWrapper<BizDangerArea> queryWrapper3 = new QueryWrapper<>();
            queryWrapper3.lambda().eq(BizDangerArea::getTunnelId,tunnel.getTunnelId());
            List<BizDangerArea> areas = bizDangerAreaMapper.selectList(queryWrapper3);
            if(areas != null && areas.size() > 0){

                for (BizDangerArea area : areas) {
                    BigDecimal[] s1 = new BigDecimal[2];
                    s1[0] = new BigDecimal(area.getScbStartx());
                    s1[1] = new BigDecimal(area.getScbStarty());

                    BigDecimal[] s2 = new BigDecimal[2];
                    s2[0] = new BigDecimal(area.getScbEndx());
                    s2[1] = new BigDecimal(area.getScbEndy());
                    BigDecimal dance1 = GeometryUtil.calculateDistance(s1,min);
                    BigDecimal dance2 = GeometryUtil.calculateDistance(s2,min);
                    if(dance1.compareTo(dance2) > 0){
                        area.setScbStartx(area.getScbEndx())
                                .setScbStarty(area.getScbEndy())
                                .setScbEndx(area.getScbStartx())
                                .setScbEndy(area.getScbStarty());
                    }

                    s1[0] = new BigDecimal(area.getFscbStartx());
                    s1[1] = new BigDecimal(area.getFscbStarty());

                    s2[0] = new BigDecimal(area.getFscbEndx());
                    s2[1] = new BigDecimal(area.getFscbEndy());
                     dance1 = GeometryUtil.calculateDistance(s1,min);
                     dance2 = GeometryUtil.calculateDistance(s2,min);

                    if(dance1.compareTo(dance2) > 0){
                        area.setFscbStartx(area.getFscbEndx())
                                .setFscbStarty(area.getFscbEndy())
                                .setFscbEndx(area.getFscbStartx())
                                .setFscbEndy(area.getFscbStarty());
                    }
                }

                List<BizDangerArea> areas1 = sortByDistance(areas,min);
                for (int i = 0; i < areas1.size(); i++) {
                    areas1.get(i).setNo(i+1);
                    areas1.get(i).setStatus(1);
                    areas1.get(i).setName(tunnel.getTunnelName()+"-"+(i+1)+"-"+"危险区");
                }
                for (BizDangerArea area : areas1) {
                    bizDangerAreaMapper.updateById(area);
                }
            }
        }
        return R.ok();
    }


    public List<BizDangerArea>  sortByDistance(List<BizDangerArea> areas, BigDecimal[] m) {
        areas.sort(Comparator.comparing(area -> {
            BigDecimal[] centerCoords = GeometryUtil.parsePoints(area.getCenter());
            return GeometryUtil.calculateDistance(centerCoords, m);
        }));
        return areas;
    }

//
//    /**
//     * 钻孔信息 比例
//     * 传入两个点 ,和距离 计算实际距离与坐标的比例保存在所有帮内
//     * @param startLat
//     * @param startLon
//     * @param endLat
//     * @param endLon
//     * @param drillrange
//     * @return
//     */
//    @ApiOperation("调整迎头钻孔帮间距信息比例")
////    @PreAuthorize("@ss.hasPermi('basicInfo:bar:list')")
//    @PostMapping("/barPrpoRange")
//    public R barPrpoRange(@RequestParam(required = false) String startLat,
//                      @RequestParam(required = false) String startLon,
//                      @RequestParam(required = false) String endLat,
//                      @RequestParam(required = false) String endLon,
//                      @RequestParam(required = false) String drillrange)
//    {
//        BigDecimal startLatBd = new BigDecimal(startLat);
//        BigDecimal startLonBd = new BigDecimal(startLon);
//        BigDecimal endLatBd = new BigDecimal(endLat);
//        BigDecimal endLonBd = new BigDecimal(endLon);
//        BigDecimal drillrangeBd = new BigDecimal(drillrange);
//        BigDecimal latMove = startLatBd.subtract(endLatBd).abs();
//        BigDecimal lonMove = startLonBd.subtract(endLonBd).abs();
//
//        // dx^2
//        BigDecimal dxSquare = latMove.pow(2);
//        // dy^2
//        BigDecimal dySquare = lonMove.pow(2);
//        // dx^2 + dy^2
//        BigDecimal sum = dxSquare.add(dySquare);
//
//        BigDecimal lonlatrange = new BigDecimal(Math.sqrt(sum.doubleValue())).abs().setScale(10, RoundingMode.HALF_DOWN);
//
//        BigDecimal bili = lonlatrange.divide(drillrangeBd, 10, RoundingMode.HALF_DOWN);
//
//        List<BizTunnelBar> bizTunnelBars =  bizTunnelBarService.list();
//        for (BizTunnelBar bizTunnelBar : bizTunnelBars) {
//            UpdateWrapper<BizTunnelBar> updateWrapper = new UpdateWrapper<>();
//            updateWrapper.lambda().set(BizTunnelBar::getPrpo,bili).eq(BizTunnelBar::getBarId,bizTunnelBar.getBarId()).eq(BizTunnelBar::getDelFlag,BizBaseConstant.DELFLAG_N);
//            bizTunnelBarService.update(updateWrapper);
//        }
//
//        return R.ok();
//    }

    /**
     * 查询巷道帮管理列表
     */
    @ApiOperation("查询巷道帮管理列表")
    @PreAuthorize("@ss.hasPermi('basicInfo:bar:list')")
    @GetMapping("/list")
    public R<MPage<BizTunnelBarVo>> list(@ParameterObject BizTunnelBarDto dto, @ParameterObject Pagination pagination)
    {
        MPage<BizTunnelBarVo> list = bizTunnelBarService.selectEntityList(dto,pagination);
        return R.ok(list);
    }

    /**
     * 查询巷道帮管理列表
     */
    @ApiOperation("查询巷道帮下拉")
    @GetMapping("/checkList")
    public R<List<BizTunnelBar>> checkList(@ParameterObject BizTunnelBarDto dto)
    {
        QueryWrapper<BizTunnelBar> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StrUtil.isNotEmpty(dto.getType()), BizTunnelBar::getType,dto.getType())
                .eq(StrUtil.isNotEmpty(dto.getBarName()),BizTunnelBar::getBarName,dto.getBarName())
                .eq(dto.getTunnelId() != null,BizTunnelBar::getTunnelId, dto.getTunnelId());
        List<BizTunnelBar> list = bizTunnelBarService.list(queryWrapper);
        return R.ok(list);
    }





    /**
     * 获取巷道帮管理详细信息
     */
    @ApiOperation("获取巷道帮管理详细信息")
    @PreAuthorize("@ss.hasPermi('basicInfo:bar:query')")
    @GetMapping(value = "/{barId}")
    public R<BizTunnelBar> getInfo(@PathVariable("barId") Long barId)
    {
        return R.ok(bizTunnelBarService.selectEntityById(barId));
    }

    /**
     * 新增巷道帮管理
     */
    @ApiOperation("新增巷道帮管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:bar:add')")
    @Log(title = "巷道帮管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody  BizTunnelBarDto dto)
    {
        return R.ok(bizTunnelBarService.insertEntity(dto));
    }

    /**
     * 修改巷道帮管理
     */
    @ApiOperation("修改巷道帮管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:bar:edit')")
    @Log(title = "巷道帮管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody @Validated(value = {GroupUpdate.class}) BizTunnelBarDto dto)
    {

        return R.ok(bizTunnelBarService.updateEntity(dto));
    }

    /**
     * 删除巷道帮管理
     */
    @ApiOperation("删除巷道帮管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:bar:remove')")
    @Log(title = "巷道帮管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{barIds}")
    public R remove(@PathVariable Long[] barIds)
    {
//        QueryWrapper<BizTunnelBar> queryWrapper = new QueryWrapper<>();
//        queryWrapper.lambda().in(BizTunnelBar::getBarId, barIds).eq(BizTunnelBar::getDelFlag, BizBaseConstant.DELFLAG_N);
//        Long count = bizMiningAreaService.getBaseMapper().selectCount(queryWrapper);
//        Assert.isTrue(count == 0, "选择的巷道帮下还有采区");
        UpdateWrapper<BizTunnelBar> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().in(BizTunnelBar::getBarId, barIds).set(BizTunnelBar::getDelFlag,BizBaseConstant.DELFLAG_Y);
        return R.ok(bizTunnelBarService.update(updateWrapper));
    }


    /**
     * 删除巷道帮管理
     */
    @ApiOperation("删除巷道帮管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:bar:remove')")
    @Log(title = "巷道帮管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/one/{barId}")
    public R remove(@PathVariable("barId") Long barId)
    {
//        QueryWrapper<BizMiningArea> queryWrapper = new QueryWrapper<>();
//        queryWrapper.lambda().eq(BizMiningArea::getbarId, barId).eq(BizMiningArea::getDelFlag, BizBaseConstant.DELFLAG_N);
//        Long count = bizMiningAreaService.getBaseMapper().selectCount(queryWrapper);
//        Assert.isTrue(count == 0, "选择的巷道帮下还有采区");
        BizTunnelBar entity = new BizTunnelBar();
        entity.setBarId(barId).setDelFlag(BizBaseConstant.DELFLAG_Y);
        return R.ok(bizTunnelBarService.updateById(entity));
    }


}
