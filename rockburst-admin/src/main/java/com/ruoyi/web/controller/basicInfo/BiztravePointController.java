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
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.constant.GroupAdd;
import com.ruoyi.system.constant.GroupUpdate;
import com.ruoyi.system.domain.BizTravePoint;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.Point;
import com.ruoyi.system.domain.dto.BizTravePointDto;
import com.ruoyi.system.domain.vo.BizTravePointVo;
import com.ruoyi.system.service.IBizMineService;
import com.ruoyi.system.service.IBizMiningAreaService;
import com.ruoyi.system.service.IBizTravePointService;
import com.ruoyi.system.service.TunnelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private TunnelService tunnelService;

    /**
     * 查询矿井管理列表
     */
    @ApiOperation("查询导线点管理列表")
    @PreAuthorize("@ss.hasPermi('basicInfo:mine:list')")
    @GetMapping("/list")
    public R<MPage<BizTravePoint>> list(@ParameterObject BizTravePointDto dto, @ParameterObject Pagination pagination)
    {
        QueryWrapper<BizTravePoint> queryWrapper = new QueryWrapper<BizTravePoint>();
        queryWrapper.lambda()
                .like(StrUtil.isNotEmpty(dto.getAxisx()),BizTravePoint::getAxisx,dto.getAxisx())
                .like(StrUtil.isNotEmpty(dto.getAxisy()),BizTravePoint::getAxisx,dto.getAxisy())
                .like(StrUtil.isNotEmpty(dto.getAxisz()),BizTravePoint::getAxisx,dto.getAxisz())
                .like(StrUtil.isNotEmpty( dto.getPointName()), BizTravePoint::getPointName, dto.getPointName())
                .eq(dto.getStatus() != null , BizTravePoint::getStatus,dto.getStatus())
                .eq(dto.getWorkfaceId() != null , BizTravePoint::getWorkfaceId,dto.getWorkfaceId())
                .eq(dto.getTunnelId() != null , BizTravePoint::getTunnelId,dto.getTunnelId())
                .eq(BizTravePoint::getDelFlag, BizBaseConstant.DELFLAG_N);
        IPage<BizTravePoint> list = bizTravePointService.getBaseMapper().selectPage(pagination,queryWrapper);
        return R.ok(new MPage<>(list));
    }


    @ApiOperation("下拉导线点列表-根据状态,工作面 查询导线点")
    @PreAuthorize("@ss.hasPermi('basicInfo:mine:list')")
    @GetMapping("/checkList")
    public R<List<BizTravePoint>> checkList(@RequestParam(value = "状态合集", required = false) Long[] statuss,
                                            @RequestParam(value = "工作面合集", required = false) Long[] workfaceIds,
                                            @RequestParam(value = "巷道合集", required = false) Long[] tunnelIds)
    {
        QueryWrapper<BizTravePoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .in(statuss != null && statuss.length > 0, BizTravePoint::getStatus, statuss)
                .in(tunnelIds != null && tunnelIds.length > 0, BizTravePoint::getTunnelId, tunnelIds)
                .in(workfaceIds != null && workfaceIds.length > 0, BizTravePoint::getWorkfaceId, workfaceIds)
                .eq(BizTravePoint::getDelFlag,BizBaseConstant.DELFLAG_N);
        List<BizTravePoint> list = bizTravePointService.getBaseMapper().selectList(queryWrapper);
        return R.ok(list);
    }

    @ApiOperation("下拉导线点列表-根据状态,工作面 查询导线点")
    @PreAuthorize("@ss.hasPermi('basicInfo:mine:list')")
    @GetMapping("/checkList1")
    public R checkList1(
                                            @RequestParam(value = "工作面", required = false) Long workfaceId)
    {
        QueryWrapper<TunnelEntity> tunnelEntityQueryWrapper = new QueryWrapper<>();
        tunnelEntityQueryWrapper.lambda().eq(TunnelEntity::getWorkFaceId,workfaceId);
        long count =  tunnelService.count(tunnelEntityQueryWrapper);
        Assert.isTrue(count  > 2, "至少需要两个平行巷,一个切眼");

        tunnelEntityQueryWrapper.clear();
        tunnelEntityQueryWrapper.lambda().select(TunnelEntity::getTunnelId,TunnelEntity::getTunnelType)
                .eq(TunnelEntity::getWorkFaceId,workfaceId)
                .in(TunnelEntity::getTunnelType,"QY","XH","SH");
        List<TunnelEntity> tunnelEntities = tunnelService.list(tunnelEntityQueryWrapper);

        Long qyId = null;
        Long shId = null;
        Long xhId = null;
        for (TunnelEntity tunnelEntity : tunnelEntities) {
            if("QY".equals(tunnelEntity.getTunnelType())){
                qyId = tunnelEntity.getTunnelId();
            }
            if("SH".equals(tunnelEntity.getTunnelType())){
                shId = tunnelEntity.getTunnelId();
            }
            if("XH".equals(tunnelEntity.getTunnelType())){
                xhId = tunnelEntity.getTunnelId();
            }
        }
        Assert.isTrue(qyId != null,"此工作面下缺少 切眼巷道");
        Assert.isTrue(shId != null,"此工作面下缺少 SH巷道");
        Assert.isTrue(xhId != null,"此工作面下缺少 XH巷道");

        QueryWrapper<BizTravePoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(BizTravePoint::getIsVertex,true)
                .eq( BizTravePoint::getTunnelId, qyId)
                .eq(BizTravePoint::getDelFlag,BizBaseConstant.DELFLAG_N);
        List<BizTravePoint> qylist = bizTravePointService.getBaseMapper().selectList(queryWrapper);

        Assert.isTrue(qylist != null && qylist.size() == 2 ,"该巷道下顶点个数不正确");

        //上巷
        queryWrapper.clear();
        queryWrapper.lambda().eq(BizTravePoint::getTunnelId,shId)
                .eq(BizTravePoint::getIsVertex,true)
                .orderBy(true,true,BizTravePoint::getDistance);
        List<BizTravePoint> shVertex = bizTravePointService.getBaseMapper().selectList(queryWrapper);


        //下巷
        queryWrapper.clear();
        queryWrapper.lambda().eq(BizTravePoint::getTunnelId,xhId)
                .orderBy(true,true,BizTravePoint::getDistance);
        List<BizTravePoint> xhlist = bizTravePointService.getBaseMapper().selectList(queryWrapper);


        Map<String,List<BizTravePoint>> map = new HashMap<>();
        map.put("SH",shVertex);
        map.put("XH",xhlist);
        return R.ok(map);
    }

    private Double getMath(Point a, Point n){
        BigDecimal ax = a.getX().subtract(n.getX());
        BigDecimal ay = a.getY().subtract(n.getY());
        BigDecimal az = a.getZ().subtract(n.getZ());

        ax = ax.multiply(ax);
        ay = ay.multiply(ay);
        az = az.multiply(az);

        Double an = Math.sqrt(ax.add(ay).add(az).doubleValue());
        return an;
    }

    private Point zhuanhuan(BizTravePoint bizTravePoint){
        Point point = new Point();
        point.setX(new BigDecimal(bizTravePoint.getAxisx()));
        point.setY(new BigDecimal(bizTravePoint.getAxisy()));
        point.setZ(new BigDecimal(bizTravePoint.getAxisz()));
        return point;
    }


    @ApiOperation("尺子")
    @PreAuthorize("@ss.hasPermi('basicInfo:mine:list')")
    @GetMapping("/rule")
    public R<MPage<BizTravePointVo>> rule(@RequestParam Long locationId,@RequestParam String constructType,@ParameterObject Pagination pagination)
    {
        return R.ok(bizTravePointService.geRuleList(locationId,constructType,pagination));
    }







    /**
     * 获取矿井管理详细信息
     */
    @ApiOperation("获取导线点管理详细信息")
    @PreAuthorize("@ss.hasPermi('basicInfo:mine:query')")
    @GetMapping(value = "/{pointId}")
    public R<BizTravePoint> getInfo(@PathVariable("pointId") Long pointId)
    {
        return R.ok(bizTravePointService.getBaseMapper().selectById(pointId));
    }

    /**
     * 新增矿井管理
     */
    @ApiOperation("新增导线点管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:mine:add')")
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
        Assert.isTrue(i <= 0 , "编号重复");

        TunnelEntity tunnelEntity = tunnelService.getById(dto.getTunnelId());
        if("SH".equals(tunnelEntity.getTunnelType()) || "XH".equals(tunnelEntity.getTunnelType())){
            List<BizTravePoint> qypoints = bizTravePointService.getQyPoint(dto.getWorkfaceId());
            if(qypoints != null && qypoints.size() == 2){
                Double q1 = getMath(zhuanhuan(entity),zhuanhuan(qypoints.get(0)));
                Double q2 = getMath(zhuanhuan(entity),zhuanhuan(qypoints.get(1)));
                if(q1 != null && q2 != null && q1 < q2){
                    entity.setBestNearPointId(qypoints.get(0).getTunnelId()).setDistance(q1);
                }else {
                    entity.setBestNearPointId(qypoints.get(1).getTunnelId()).setDistance(q2);
                }
            }
        } else if ("QY".equals(tunnelEntity.getTunnelType())  ) {
            if(dto.getIsVertex()){
                queryWrapper.clear();
                queryWrapper.lambda()
                        .notIn(dto.getPointId() != null, BizTravePoint::getPointId,dto.getPointId())
                        .eq(BizTravePoint::getTunnelId, dto.getTunnelId())
                        .eq(BizTravePoint::getIsVertex,true);
                long counted = bizTravePointService.count(queryWrapper);
                Assert.isTrue(counted <= 2 , "最多有两个顶点");
                bizTravePointService.updateById(entity);
                bizTravePointService.doit(entity);
                return R.ok(1);
            }
        }

        return R.ok(bizTravePointService.getBaseMapper().insert(entity));
    }

    /**
     * 修改矿井管理
     */
    @ApiOperation("修改导线点管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:mine:edit')")
    @Log(title = "修改导线点管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody @Validated(value = {GroupUpdate.class}) BizTravePointDto dto)
    {
        BizTravePoint entity = new BizTravePoint();
        BeanUtil.copyProperties(dto, entity);
        QueryWrapper<BizTravePoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BizTravePoint::getPointName,dto.getPointName())
                .ne(BizTravePoint::getPointId,dto.getPointId())
                .eq(BizTravePoint::getTunnelId, dto.getTunnelId());
        long i = bizTravePointService.count(queryWrapper);
        Assert.isTrue(i <= 0 , "编号重复");

        TunnelEntity tunnelEntity = tunnelService.getById(dto.getTunnelId());
        if("SH".equals(tunnelEntity.getTunnelType()) || "XH".equals(tunnelEntity.getTunnelType())){
            List<BizTravePoint> qypoints = bizTravePointService.getQyPoint(dto.getWorkfaceId());
            if(qypoints != null && qypoints.size() == 2){
                Double q1 = getMath(zhuanhuan(entity),zhuanhuan(qypoints.get(0)));
                Double q2 = getMath(zhuanhuan(entity),zhuanhuan(qypoints.get(1)));
                if(q1 != null && q2 != null && q1 < q2){
                    entity.setBestNearPointId(qypoints.get(0).getTunnelId()).setDistance(q1);
                }else {
                    entity.setBestNearPointId(qypoints.get(1).getTunnelId()).setDistance(q2);
                }
            }
        } else if ("QY".equals(tunnelEntity.getTunnelType())  ) {
            if(dto.getIsVertex()){
                queryWrapper.clear();
                queryWrapper.lambda()
                        .notIn(dto.getPointId() != null, BizTravePoint::getPointId,dto.getPointId())
                        .eq(BizTravePoint::getTunnelId, dto.getTunnelId())
                        .eq(BizTravePoint::getIsVertex,true);
                long counted = bizTravePointService.count(queryWrapper);
                Assert.isTrue(counted <= 2 , "最多有两个顶点");
                bizTravePointService.updateById(entity);
                bizTravePointService.doit(entity);
                return R.ok(1);
            }
        }
        return R.ok(bizTravePointService.updateById(entity));
    }

    /**
     * 删除矿井管理
     */
    @ApiOperation("删除导线点管理批量")
    @PreAuthorize("@ss.hasPermi('basicInfo:mine:remove')")
    @Log(title = "删除导线点管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{pointIds}")
    public R remove(@PathVariable Long[] pointIds)
    {
        UpdateWrapper<BizTravePoint> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().in(BizTravePoint::getPointId, pointIds).set(BizTravePoint::getDelFlag,BizBaseConstant.DELFLAG_Y);
        return R.ok(bizTravePointService.update(updateWrapper));
    }


    /**
     * 删除矿井管理
     */
    @ApiOperation("删除导线点管理")
//    @PreAuthorize("@ss.hasPermi('basicInfo:mine:remove')")
    @Log(title = "删除导线点管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/one/{pointId}")
    public R remove(@PathVariable("pointId") Long pointId)
    {
        BizTravePoint entity = new BizTravePoint();
        entity.setPointId(pointId).setDelFlag(BizBaseConstant.DELFLAG_Y);
        return R.ok(bizTravePointService.updateById(entity));
    }
}
