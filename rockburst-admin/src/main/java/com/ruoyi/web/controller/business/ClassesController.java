package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.Entity.ClassesEntity;
import com.ruoyi.system.domain.Entity.ParameterValidationAdd;
import com.ruoyi.system.domain.Entity.ParameterValidationOther;
import com.ruoyi.system.domain.Entity.ParameterValidationUpdate;
import com.ruoyi.system.domain.dto.ClassesChoiceListDTO;
import com.ruoyi.system.domain.dto.ClassesSelectDTO;
import com.ruoyi.system.service.ClassesService;
import io.swagger.annotations.*;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/16
 * @description:
 */

@Api(tags = "班次管理")
@RestController
@RequestMapping(value = "/classes")
public class ClassesController {

    @Resource
    private ClassesService classesService;

    @ApiOperation(value = "新增班次", notes = "新增班次")
    @PostMapping(value = "/add")
    public R<Integer> addClasses(@RequestBody @Validated({ParameterValidationAdd.class, ParameterValidationOther.class}) ClassesEntity classesEntity){
        return R.ok(classesService.insertClasses(classesEntity));
    }

    @ApiOperation(value = "班次修改", notes = "班次修改")
    @PutMapping(value = "/update")
    public R<Integer> updateClasses(@RequestBody @Validated({ParameterValidationUpdate.class, ParameterValidationOther.class}) ClassesEntity classesEntity){
        return R.ok(classesService.updateClasses(classesEntity));
    }

    @ApiOperation(value = "根据主键查询", notes = "根据主键查询")
    @GetMapping(value = "/getById")
    public R<ClassesEntity> getClassesById(@ApiParam(name = "classesId", value = "班次id", required = true) @RequestParam Long classesId){
        return R.ok(classesService.getClassesById(classesId));
    }

    @ApiOperation(value = "根据条件参数分页查询数据列表接口", notes = "根据条件参数分页查询数据列表接口")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", defaultValue = "10", dataType = "Integer")
    })
    @GetMapping(value = "/pageQueryList")
    public R<TableData> pageQueryList(@RequestBody ClassesSelectDTO classesSelectDTO,
                                       @ApiParam(name = "pageNum", value = "页码", required = true) @RequestParam Integer pageNum,
                                       @ApiParam(name = "pageSize", value = "每页数量", required = true) @RequestParam Integer pageSize){
        return R.ok(classesService.pageQueryList(classesSelectDTO, pageNum, pageSize));
    }

    @ApiOperation(value = "删除班次", notes = "删除班次")
    @RequestMapping(value = "/delete")
    public R<Boolean> delete(@ApiParam(name = "classesIds", value = "id数组", readOnly = true) @RequestParam Long[] classesIds){
        return R.ok(classesService.deleteClasses(classesIds));
    }

    @ApiOperation(value = "获取班次下拉列表", notes = "获取班次下拉列表")
    @GetMapping(value = "/getClassesChoiceList")
    public R<List<ClassesChoiceListDTO>> getClassesChoiceList(){
        return R.ok(classesService.getClassesChoiceList());
    }
}