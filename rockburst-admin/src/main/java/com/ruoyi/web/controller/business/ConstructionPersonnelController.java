package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.common.enums.ProfessionEnums;
import com.ruoyi.system.domain.Entity.ParameterValidationAdd;
import com.ruoyi.system.domain.Entity.ParameterValidationOther;
import com.ruoyi.system.domain.Entity.ParameterValidationUpdate;
import com.ruoyi.system.domain.dto.ConstructPersonnelDTO;
import com.ruoyi.system.domain.dto.PersonnelChoiceListDTO;
import com.ruoyi.system.domain.dto.PersonnelSelectDTO;
import com.ruoyi.system.service.ConstructionPersonnelService;
import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: shikai
 * @date: 2024/11/16
 * @description:
 */

@Api(tags = "施工人员管理")
@RestController
@RequestMapping(value = "/constructionPersonnel")
public class ConstructionPersonnelController {

    @Resource
    private ConstructionPersonnelService personnelService;

    @ApiOperation(value = "新增施工人员", notes = "新增施工人员")
    @PostMapping(value = "/add")
    public R<ConstructPersonnelDTO> add(@RequestBody @Validated({ParameterValidationAdd.class, ParameterValidationOther.class}) ConstructPersonnelDTO constructPersonnelDTO) {
        return R.ok(personnelService.insertConstructionPersonnel(constructPersonnelDTO));
    }

    @ApiOperation(value = "施工人员编辑", notes = "施工人员编辑")
    @PutMapping(value = "/update")
    public R<ConstructPersonnelDTO> update(@RequestBody @Validated({ParameterValidationUpdate.class, ParameterValidationOther.class})
                                           ConstructPersonnelDTO constructPersonnelDTO) {
        return R.ok(personnelService.updateConstructionPersonnel(constructPersonnelDTO));
    }

    @ApiOperation(value = "根据id查询", notes = "根据id查询")
    @GetMapping(value = "/getById/{constructionPersonnelId}")
    public R<ConstructPersonnelDTO> getConstructionPersonnelById(@ApiParam(name = "constructionPersonnelId", value = "施工人员id", required = true)
                                                                 @PathVariable Long constructionPersonnelId) {
        return R.ok(personnelService.getConstructionPersonnelById(constructionPersonnelId));
    }

    @ApiOperation(value = "分页查询列表", notes = "分页查询列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", defaultValue = "10", dataType = "Integer")
    })
    @PostMapping(value = "/pageQueryList")
    public R<TableData> pageQueryList(@RequestBody PersonnelSelectDTO personnelSelectDTO,
                                      @RequestParam(required = false) Integer pageNum, @RequestParam(required = false) Integer pageSize) {
        return R.ok(personnelService.pageQueryList(personnelSelectDTO, pageNum, pageSize));
    }

    @ApiOperation(value = "删除施工人员", notes = "删除施工人员")
    @DeleteMapping(value = "/delete")
    public R<Boolean> delete(@ApiParam(name = "personnelIds", value = "施工人员id数组", required = true) @RequestParam Long[] personnelIds) {
        return R.ok(personnelService.deleteConstructionPersonnel(personnelIds));
    }

    @ApiOperation(value = "获取施工人员下拉列表", notes = "获取施工人员下拉列表")
    @GetMapping(value = "/getPersonnelChoiceList")
    public R<List<PersonnelChoiceListDTO>> getPersonnelChoiceList(@ApiParam(name = "constructionUnitId", value = "施工单位id", required = true)
                                                                      @RequestParam Long constructionUnitId,
                                                                  @ApiParam(name = "profession", value = "工种", required = true) @RequestParam String profession) {
        return R.ok(personnelService.getPersonnelChoiceList(constructionUnitId, profession));
    }

    @ApiOperation(value = "获取工种下拉列表", notes = "获取工种下拉列表")
    @GetMapping(value = "/getProfessionList")
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
}