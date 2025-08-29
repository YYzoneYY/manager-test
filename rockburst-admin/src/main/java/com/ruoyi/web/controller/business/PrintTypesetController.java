package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.service.PrintTypesetService;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author: shikai
 * @date: 2025/8/28
 * @description:
 */
@Api(tags = "打印排版")
@RestController
@RequestMapping(value = "/printTypeset")
public class PrintTypesetController {

    @Resource
    private PrintTypesetService printTypesetService;

    @Resource
    private TokenService tokenService;

    @ApiOperation(value = "分页查询", notes = "分页查询")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", defaultValue = "10", dataType = "Integer")
    })
    @GetMapping("/queryPage")
    public R<Object> queryPage(@ApiParam(name = "startTime", value = "开始时间", required = true) @RequestParam Long startTime,
                               @ApiParam(name = "endTime", value = "结束时间", required = true) @RequestParam Long endTime,
                               @ApiParam(name = "drillNum", value = "钻孔编号") @RequestParam(required = false) String drillNum,
                               @ApiParam(name = "pageNum", value = "页码", required = true) @RequestParam Integer pageNum,
                               @ApiParam(name = "pageSize", value = "页数", required = true) @RequestParam Integer pageSize) {
        String token = tokenService.getToken(ServletUtils.getRequest());
        Long mineId = tokenService.getMineIdFromToken(token);
        return R.ok(printTypesetService.queryPage(new Date(startTime), new Date(endTime), drillNum, mineId, pageNum, pageSize));
    }
}