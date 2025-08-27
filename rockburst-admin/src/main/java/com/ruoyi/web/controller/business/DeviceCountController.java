package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.service.DeviceCountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: shikai
 * @date: 2025/8/27
 * @description:
 */

@Api(tags = "设备统计")
@RestController
@RequestMapping(value = "/deviceCount")
public class DeviceCountController {

    @Resource
    private DeviceCountService deviceCountService;

    @Autowired
    private TokenService tokenService;

    @ApiOperation(value = "测点总体概览", notes = "测点总体概览")
    @GetMapping(value = "/obtainDeviceCount")
    public R<Object> obtainDeviceCount() {
        String token = tokenService.getToken(ServletUtils.getRequest());
        Long mineId = tokenService.getMineIdFromToken(token);
        return R.ok(this.deviceCountService.obtainDeviceCount(mineId));
    }

    @ApiOperation(value = "各类数量统计", notes = "各类数量统计")
    @GetMapping(value = "/obtainDeviceCountByType")
    public R<Object> obtainDeviceCountByType() {
        String token = tokenService.getToken(ServletUtils.getRequest());
        Long mineId = tokenService.getMineIdFromToken(token);
        return R.ok(this.deviceCountService.obtainDeviceCountByType(mineId));
    }

    @ApiOperation(value = "各类数量占比", notes = "各类数量占比")
    @GetMapping(value = "/obtainSenSorCount")
    public R<Object> obtainSenSorCount() {
        String token = tokenService.getToken(ServletUtils.getRequest());
        Long mineId = tokenService.getMineIdFromToken(token);
        return R.ok(this.deviceCountService.obtainSenSorCount(mineId));
    }
}