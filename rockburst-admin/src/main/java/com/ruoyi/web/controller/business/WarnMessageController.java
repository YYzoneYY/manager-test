package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.dto.actual.ActualSelectDTO;
import com.ruoyi.system.domain.dto.actual.WarnSelectDTO;
import com.ruoyi.system.service.MeasureActualService;
import com.ruoyi.system.service.WarnMessageService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: shikai
 * @date: 2025/8/18
 * @description:
 */

@Api(tags = "预警信息")
@RestController
@RequestMapping(value = "/warnMessage")
public class WarnMessageController {

    @Resource
    private WarnMessageService warnMessageService;

    @Autowired
    private TokenService tokenService;



    @ApiOperation(value = "预警信息列表", notes = "预警信息列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", defaultValue = "10", dataType = "Integer")
    })
    @PostMapping(value = "/warnDataList")
    public R<Object> ActualDataPage(@RequestBody WarnSelectDTO warnSelectDTO,
                                    @ApiParam(name = "pageNum", value = "页码", required = true) @RequestParam Integer pageNum,
                                    @ApiParam(name = "pageSize", value = "每页数量", required = true) @RequestParam Integer pageSize) {
        String token = tokenService.getToken(ServletUtils.getRequest());
        Long mineId = tokenService.getMineIdFromToken(token);
        return R.ok(this.warnMessageService.warnMessagePage(warnSelectDTO, mineId, pageNum, pageSize));
    }

    @ApiOperation(value = "预警信息详情", notes = "预警信息详情")
    @GetMapping(value = "/detail")
    public R<Object> detail(@ApiParam(name = "warnInstanceNum", value = "警情编号", required = true) @RequestParam String warnInstanceNum) {
        String token = tokenService.getToken(ServletUtils.getRequest());
        Long mineId = tokenService.getMineIdFromToken(token);
        return R.ok(this.warnMessageService.detail(warnInstanceNum, mineId));
    }

    @ApiOperation(value = "多参量选择列表", notes = "多参量选择列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "type", value = "类型", required = true, dataType = "String"),
            @ApiImplicitParam(name = "keyword", value = "关键字", dataType = "String", required = false),
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", defaultValue = "10", dataType = "Integer")
    })
    @PostMapping(value = "/referenceQuantityPage")
    public R<Object> referenceQuantityPage(@ApiParam(name = "type", value = "类型", required = true) @RequestParam String type,
                                    @ApiParam(name = "keyword", value = "关键字") @RequestParam(required = false) String keyword,
                                    @ApiParam(name = "pageNum", value = "页码", required = true) @RequestParam Integer pageNum,
                                    @ApiParam(name = "pageSize", value = "每页数量", required = true) @RequestParam Integer pageSize) {
        String token = tokenService.getToken(ServletUtils.getRequest());
        Long mineId = tokenService.getMineIdFromToken(token);
        return R.ok(this.warnMessageService.referenceQuantityPage(type, keyword, mineId, pageNum, pageSize));
    }
}