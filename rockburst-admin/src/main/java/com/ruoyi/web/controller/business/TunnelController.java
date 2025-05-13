package com.ruoyi.web.controller.business;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.BizTunnelBar;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.ParameterValidationAdd;
import com.ruoyi.system.domain.Entity.ParameterValidationOther;
import com.ruoyi.system.domain.Entity.ParameterValidationUpdate;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.Segment;
import com.ruoyi.system.domain.dto.SelectTunnelDTO;
import com.ruoyi.system.domain.dto.TunnelChoiceListDTO;
import com.ruoyi.system.domain.dto.TunnelDTO;
import com.ruoyi.system.domain.dto.TunnelDTOCAD;
import com.ruoyi.system.domain.utils.GeometryUtil;
import com.ruoyi.system.mapper.BizTunnelBarMapper;
import com.ruoyi.system.mapper.BizWorkfaceMapper;
import com.ruoyi.system.service.TunnelService;
import io.swagger.annotations.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/21
 * @description:
 */

@Api(tags = "巷道管理")
@RestController
@RequestMapping(value = "/tunnel")
public class TunnelController {

    @Resource
    private TunnelService tunnelService;
    @Autowired
    private BizTunnelBarMapper bizTunnelBarMapper;
    @Autowired
    private BizWorkfaceMapper bizWorkfaceMapper;


    /**
     * 获取工作面管理详细信息
     */
    @ApiOperation("获取工作面管理详细信息根据名称")
//    @PreAuthorize("@ss.hasPermi('basicInfo:workface:query')")
    @GetMapping(value = "detail/{tunnelName}")
    public R getInfo1(@PathVariable("tunnelName") String tunnelName)
    {
        QueryWrapper<TunnelEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TunnelEntity::getTunnelName,tunnelName);
        List<TunnelEntity> vos =  tunnelService.getBaseMapper().selectList(queryWrapper);
        if(vos != null && vos.size()>0){
            return R.ok(vos.get(0));
        }
        return R.ok();
    }


    @ApiOperation(value = "新增巷道cad", notes = "新增巷道")
    @PostMapping("/add-cad")
    public R<Object> addTunnelcad(@RequestBody TunnelDTOCAD dto) {

        QueryWrapper<BizWorkface> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BizWorkface::getWorkfaceName,dto.getWorkFaceName());
        List<BizWorkface> workfaces =  bizWorkfaceMapper.selectList(queryWrapper);
        BizWorkface bizWorkface = null;
        if(workfaces == null || workfaces.size() == 0){
            return R.fail("没有找到"+dto.getWorkFaceName());
        }
        dto.setWorkFaceId(workfaces.get(0).getWorkfaceId());

        TunnelDTO tunnelDTO = new TunnelDTO();
        BeanUtils.copyProperties(dto,tunnelDTO);
        Long tunnelId =  tunnelService.insertTunnel(tunnelDTO);

        if(StringUtils.isNotEmpty(dto.getPointsList()) && StringUtils.isNotEmpty(dto.getWorkFaceCenter())){
            List<BigDecimal[]> bigDecimals = GeometryUtil.parseToBigDecimalArrayList(dto.getPointsList());
            List<Segment> segments = GeometryUtil.findLongestTwoSegments(bigDecimals);
            String centerstr = dto.getWorkFaceCenter();
            BigDecimal[] mn =  GeometryUtil.parsePoint(centerstr);
            Segment minnear = GeometryUtil.findNearestSegment(segments,mn);
//            List<BigDecimal[]> bars  = GeometryUtil.findTwoClosestPoints(bigDecimals,mn[0],mn[1]);
//
//            List<BigDecimal[]> remaining = new ArrayList<>();
//            for (BigDecimal[] arr : bigDecimals) {
//                boolean found = false;
//                for (BigDecimal[] bar : bars) {
//                    if (Arrays.equals(arr, bar)) {
//                        found = true;
//                        break;
//                    }
//                }
//                if (!found) {
//                    remaining.add(arr);
//                }
//            }

            segments.remove(minnear);
            if(segments != null && segments.size() > 0){
                Segment maxnear = segments.get(0);
                BizTunnelBar bar = new BizTunnelBar();
                bar.setBarName(dto.getTunnelName()+"非生产帮")
                        .setStartx(maxnear.getStart().getX().toString())
                        .setStarty(maxnear.getStart().getY().toString())
                        .setEndx(maxnear.getEnd().getX().toString())
                        .setEndy(maxnear.getEnd().getY().toString())
                        .setType("fscb")
                        .setTunnelId(tunnelId);
                bizTunnelBarMapper.insert(bar);
            }


            if(minnear != null ){
                BizTunnelBar bar = new BizTunnelBar();
                bar.setBarName(dto.getTunnelName()+"生产帮")
                        .setStartx(minnear.getStart().getX().toString())
                        .setStarty(minnear.getStart().getY().toString())
                        .setEndx(minnear.getEnd().getX().toString())
                        .setEndy(minnear.getEnd().getY().toString())
                        .setType("scb")
                        .setTunnelId(tunnelId);
                bizTunnelBarMapper.insert(bar);
            }
        }
        return R.ok();
    }


    @ApiOperation(value = "修改巷道cad", notes = "修改巷道cad")
    @PutMapping("/add-cad")
    public R<Object> putTunnelcad(@RequestBody TunnelDTOCAD dto) {

        TunnelEntity tunnel = new TunnelEntity();
        BeanUtils.copyProperties(dto,tunnel);
        return R.ok(tunnelService.updateById(tunnel));
    }

    @ApiOperation(value = "新增巷道", notes = "新增巷道")
    @PostMapping("/add")
    public R<Object> addTunnel(@RequestBody @Validated({ParameterValidationAdd.class, ParameterValidationOther.class}) TunnelDTO tunnelDTO) {
        return R.ok(tunnelService.insertTunnel(tunnelDTO));
    }

    @ApiOperation(value = "巷道编辑", notes = "巷道编辑")
    @PutMapping(value = "/update")
    public R<Object> updateTunnel(@RequestBody @Validated({ParameterValidationUpdate.class, ParameterValidationOther.class})TunnelDTO tunnelDTO) {
        return R.ok(tunnelService.updateTunnel(tunnelDTO));
    }

    @ApiOperation(value = "巷道详情", notes = "巷道详情")
    @GetMapping("/detail")
    public R<TunnelDTO> detail(@ApiParam(name = "tunnelId", value = "巷道id", required = true) @RequestParam Long tunnelId) {
        return R.ok(tunnelService.detail(tunnelId));
    }

    @ApiOperation(value = "根据条件参数分页查询数据列表", notes = "根据条件参数分页查询数据列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", defaultValue = "10", dataType = "Integer")
    })
    @PostMapping(value = "/pageQueryList")
    public R<Object> pageQueryList(@RequestBody SelectTunnelDTO selectTunnelDTO,
                                   @ApiParam(name = "pageNum", value = "页码", required = true) @RequestParam Integer pageNum,
                                   @ApiParam(name = "pageSize", value = "每页数量", required = true) @RequestParam Integer pageSize) {
        return R.ok(tunnelService.pageQueryList(selectTunnelDTO, pageNum, pageSize));
    }

    @ApiOperation(value = "巷道删除", notes = "巷道删除")
    @RequestMapping(value = "/delete")
    public R<Object> deleteTunnel(@ApiParam(name = "tunnelIds", value = "巷道id数组", required = true) @RequestParam Long[] tunnelIds) {
        return R.ok(tunnelService.deleteByIds(tunnelIds));
    }

    @ApiOperation(value = "获取巷道下拉框", notes = "获取巷道下拉框")
    @GetMapping(value = "/getTunnelChoiceList")
    public R<List<TunnelChoiceListDTO>> getTunnelChoiceList(@ApiParam(name = "faceId", value = "面id", required = true)
                                                            @RequestParam Long faceId){
        return R.ok(tunnelService.getTunnelChoiceList(faceId));
    }

    @ApiOperation(value = "获取所有巷道列表", notes = "获取所有巷道列表")
    @GetMapping(value = "/getAllTunnelList")
    public R<List<TunnelChoiceListDTO>> getChoiceListFitterRule(){
        return R.ok(tunnelService.getChoiceListFitterRule());
    }
}