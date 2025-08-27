package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.dto.MeasureSelectDTO;
import com.ruoyi.system.domain.dto.TagDropDownListDTO;
import com.ruoyi.system.domain.dto.WarnSchemeDTO;
import com.ruoyi.system.domain.dto.WarnSchemeSelectDTO;
import com.ruoyi.system.service.WarnSchemeService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: shikai
 * @date: 2025/8/12
 * @description:
 */

@Api(tags = "预警方案配置")
@RestController
@RequestMapping(value = "/warnScheme")
public class WarnSchemeController {

    @Resource
    private WarnSchemeService warnSchemeService;

    @Autowired
    private TokenService tokenService;


    @ApiOperation(value = "新增预警方案配置", notes = "新增预警方案配置")
    @PostMapping(value = "/add")
    public R<Object> addWarnScheme(@RequestBody WarnSchemeDTO warnSchemeDTO) {
        String token = tokenService.getToken(ServletUtils.getRequest());
        Long mineId = tokenService.getMineIdFromToken(token);
        return R.ok(warnSchemeService.addWarnScheme(warnSchemeDTO, mineId));
    }

    @ApiOperation(value = "修改预警方案配置", notes = "修改预警方案配置")
    @PutMapping(value = "/update")
    public R<Object> editWarnScheme(@RequestBody WarnSchemeDTO warnSchemeDTO) {
        return R.ok(warnSchemeService.updateWarnScheme(warnSchemeDTO));
    }

    @ApiOperation(value = "详情", notes = "详情")
    @GetMapping(value = "/detail")
    public R<Object> detail(@ApiParam(name = "warnSchemeId", value = "预警方案配置ID", required = true) @RequestParam Long warnSchemeId) {
        return R.ok(warnSchemeService.detail(warnSchemeId));
    }

    @ApiOperation(value = "分页查询数据列表接口", notes = "分页查询数据列表接口")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", defaultValue = "10", dataType = "Integer")
    })
    @PostMapping(value = "/pageQueryList")
    public R<Object> pageQueryList(@RequestBody WarnSchemeSelectDTO measureSelectDTO,
                                   @ApiParam(name = "pageNum", value = "页码", required = true) @RequestParam Integer pageNum,
                                   @ApiParam(name = "pageSize", value = "每页数量", required = true) @RequestParam Integer pageSize) {
        String token = tokenService.getToken(ServletUtils.getRequest());
        Long mineId = tokenService.getMineIdFromToken(token);
        return R.ok(this.warnSchemeService.pageQueryList(measureSelectDTO, mineId, pageNum, pageSize));
    }

    @ApiOperation(value = "删除预警方案配置", notes = "删除预警方案配置")
    @DeleteMapping(value = "/delete")
    public R<Object> deleteWarnScheme(@ApiParam(name = "warnSchemeIds", value = "预警方案配置ID数组", required = true)
                                          @RequestParam Long[] warnSchemeIds) {
        return R.ok(warnSchemeService.deleteByIds(warnSchemeIds));
    }

    @ApiOperation(value = "批量启用禁用方案", notes = "批量启用禁用方案")
    @PutMapping(value = "/batchEnableDisable")
    public R<Object> batchEnableDisable(@ApiParam(name = "warnSchemeIds", value = "预警方案配置ID数组", required = true)
                                            @RequestParam Long[] warnSchemeIds) {
        return R.ok(warnSchemeService.batchEnableDisable(warnSchemeIds));
    }

    @ApiOperation(value = "根据场景类型获取标志下拉列表", notes = "根据场景类型获取标志下拉列表")
    @GetMapping(value = "/getTagDropDownList")
    public R<List<TagDropDownListDTO>> getTagDropDownList(@ApiParam(name = "sceneType", value = "场景类型", required = true) @RequestParam String sceneType) {
        String token = tokenService.getToken(ServletUtils.getRequest());
        Long mineId = tokenService.getMineIdFromToken(token);
        return R.ok(this.warnSchemeService.getTagDropDownList(sceneType, mineId));
    }


}