package com.ruoyi.app.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.domain.BizTravePoint;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.SysProjectType;
import com.ruoyi.system.domain.dto.BizProjectRecordAddDto;
import com.ruoyi.system.domain.vo.BizTunnelVo;
import com.ruoyi.system.domain.vo.BizWorkfaceVo;
import com.ruoyi.system.mapper.SysProjectTypeMapper;
import com.ruoyi.system.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 工程填报记录Controller
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Api(tags = "app-工程填报记录")
@RestController
@RequestMapping("/app/project/record")
public class BizProjectAppController extends BaseController
{
    @Autowired
    private IBizProjectRecordService bizProjectRecordService;

    @Autowired
    private IBizWorkfaceService bizWorkfaceService;

    @Autowired
    private TunnelService tunnelService;

    @Autowired
    private IBizTravePointService bizTravePointService;

    @Autowired
    private IBizDrillRecordService bizDrillRecordService;
    @Autowired
    private IBizVideoService bizVideoService;

    @Autowired
    private PlanService planService;

    @Autowired
    private SysProjectTypeMapper sysProjectTypeMapper;




    /**
     * 新增工程填报记录(无登录验证)
     */
    @Anonymous
    @ApiOperation("新增工程填报记录")
    @Log(title = "工程填报记录", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public R<?> add(@RequestBody BizProjectRecordAddDto dto)
    {
        return R.ok(bizProjectRecordService.saveRecordApp(dto));
    }

    @Anonymous
    @ApiOperation("获取工作面all")
    @Log(title = "获取工作面all", businessType = BusinessType.INSERT)
    @PostMapping("/workfaceAll")
    public R<?> getWorkfaceListAll()
    {
        List<BizWorkfaceVo> cos = bizWorkfaceService.selectWorkfaceVoList();
        return R.ok(cos);
    }
    /**
     * 新增工程填报记录(无登录验证)
     */
    @Anonymous
    @ApiOperation("获取工作面")
    @Log(title = "获取工作面", businessType = BusinessType.INSERT)
    @PostMapping("/workfaces")
    public R<?> getWorkfaceList(@RequestParam(value = "状态合集", required = false) Long[] statuss,
                                @RequestParam(value = "矿区集合", required = false) Long[] mineIds,
                                @RequestParam(value = "采区集合", required = false) Long[] miningAreaIds)
    {
        QueryWrapper<BizWorkface> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .select(BizWorkface::getWorkfaceId,BizWorkface::getWorkfaceName)
                .in(statuss != null && statuss.length > 0, BizWorkface::getStatus, statuss)
                .in(mineIds != null && mineIds.length > 0, BizWorkface::getMineId, mineIds)
                .in(miningAreaIds != null && miningAreaIds.length > 0, BizWorkface::getMiningAreaId, miningAreaIds)
                .eq(BizWorkface::getDelFlag, BizBaseConstant.DELFLAG_N);
        List<BizWorkface> list = bizWorkfaceService.getBaseMapper().selectList(queryWrapper);
        return R.ok(list);
    }

    @Anonymous
    @ApiOperation("获取巷道")
    @Log(title = "获取巷道", businessType = BusinessType.INSERT)
    @PostMapping("/tunnels")
    public R<?> getTunnelList(@RequestParam(value = "工作面集合", required = false) Long[] workfaceIds)
    {
        QueryWrapper<TunnelEntity> queryWrapper = new QueryWrapper<TunnelEntity>();
        queryWrapper.lambda()
                .in(workfaceIds != null && workfaceIds.length > 0,TunnelEntity::getWorkFaceId,workfaceIds);
        List<TunnelEntity> list = tunnelService.list(queryWrapper);
        List<BizTunnelVo> bizTunnelVoList = new ArrayList<>();
        for (TunnelEntity tunnelEntity : list) {
            BizTunnelVo vo = new BizTunnelVo();
            BeanUtil.copyProperties(tunnelEntity,vo);
            QueryWrapper<BizTravePoint> pointQueryWrapper = new QueryWrapper<>();
            pointQueryWrapper.lambda()
                    .eq(BizTravePoint::getTunnelId,tunnelEntity.getTunnelId())
                    .orderByAsc(BizTravePoint::getNo);
            List<BizTravePoint> points = bizTravePointService.list(pointQueryWrapper);
            vo.setBizTravePoints(points);
            bizTunnelVoList.add(vo);
        }
        return R.ok(bizTunnelVoList);
    }

    @Anonymous
    @ApiOperation("获取导线点")
    @Log(title = "获取导线点", businessType = BusinessType.INSERT)
    @PostMapping("/points")
    public R<?> getPointList(@RequestParam(value = "巷道id", required = false) Long tunnelId)
    {
        QueryWrapper<BizTravePoint> pointQueryWrapper = new QueryWrapper<>();
        pointQueryWrapper.lambda().eq(tunnelId != null, BizTravePoint::getTunnelId, tunnelId);
        return R.ok(bizTravePointService.list(pointQueryWrapper));
    }

    @Anonymous
    @ApiOperation("获取填报类型")
    @Log(title = "获取填报类型", businessType = BusinessType.INSERT)
    @PostMapping("/types")
    public R<?> gettypes()
    {
        return R.ok(sysProjectTypeMapper.selectList(new QueryWrapper<SysProjectType>().lambda().orderBy(true,true,SysProjectType::getSort)));
    }



}
