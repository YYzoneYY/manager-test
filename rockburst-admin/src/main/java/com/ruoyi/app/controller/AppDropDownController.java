package com.ruoyi.app.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.enums.ProfessionEnums;
import com.ruoyi.system.domain.dto.PersonnelChoiceListDTO;
import com.ruoyi.system.domain.dto.TunnelChoiceListDTO;
import com.ruoyi.system.domain.dto.UnitChoiceListDTO;
import com.ruoyi.system.service.ConstructionPersonnelService;
import com.ruoyi.system.service.ConstructionUnitService;
import com.ruoyi.system.service.TunnelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: shikai
 * @date: 2025/1/6
 * @description:
 */

@Api(tags = "app下拉列表")
@RestController
@RequestMapping("/app")
public class AppDropDownController {

    @Resource
    private ConstructionUnitService constructionUnitService;

    @Resource
    private ConstructionPersonnelService personnelService;

    @Resource
    private TunnelService tunnelService;

    @ApiOperation(value = "获取施工单位下拉列表", notes = "获取施工单位下拉列表")
    @GetMapping(value = "/unitChoiceList")
    public R<List<UnitChoiceListDTO>> getUnitChoiceList() {
        return R.ok(constructionUnitService.getUnitChoiceList());
    }


    @ApiOperation(value = "获取施工人员下拉列表", notes = "获取施工人员下拉列表")
    @GetMapping(value = "/personnelChoiceList")
    public R<List<PersonnelChoiceListDTO>> getPersonnelChoiceList(@ApiParam(name = "constructionUnitId", value = "施工单位id", required = true) @RequestParam Long constructionUnitId,
                                                                  @ApiParam(name = "profession", value = "工种", required = true) @RequestParam(required = false) String profession) {
        return R.ok(personnelService.getPersonnelChoiceList(constructionUnitId, profession));
    }

    @ApiOperation(value = "获取工种下拉列表", notes = "获取工种下拉列表")
    @GetMapping(value = "/professionList")
    public R<?> getProfessionList() {
        List<Map<String, String>> list = new ArrayList<>();
        for (ProfessionEnums enums : ProfessionEnums.values()) {
            Map<String, String> map = new HashMap<>();
            map.put("label", enums.getCode());
            map.put("value", enums.getInfo());
            list.add(map);
        }
        return R.ok(list);
    }

    @ApiOperation(value = "获取巷道下拉框", notes = "获取巷道下拉框")
    @GetMapping(value = "/tunnelChoiceList")
    public R<List<TunnelChoiceListDTO>> getTunnelChoiceList(@ApiParam(name = "faceId", value = "面id", required = true) @RequestParam Long faceId){
        return R.ok(tunnelService.getTunnelChoiceList(faceId));
    }

}