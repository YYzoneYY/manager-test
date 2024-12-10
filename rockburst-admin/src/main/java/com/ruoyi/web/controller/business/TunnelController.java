package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.Entity.ParameterValidationAdd;
import com.ruoyi.system.domain.Entity.ParameterValidationOther;
import com.ruoyi.system.domain.Entity.ParameterValidationUpdate;
import com.ruoyi.system.domain.dto.SelectTunnelDTO;
import com.ruoyi.system.domain.dto.TunnelDTO;
import com.ruoyi.system.service.TunnelService;
import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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



    @ApiOperation(value = "新增巷道", notes = "新增巷道")
    @GetMapping("/add")
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
    public R<TunnelDTO> detail(@RequestBody TunnelDTO tunnelDTO) {
        return R.ok(tunnelService.detail(tunnelDTO.getTunnelId()));
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
}