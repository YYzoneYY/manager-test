package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.dto.ProjectWarnSchemeDTO;
import com.ruoyi.system.domain.dto.SelectPlanDTO;
import com.ruoyi.system.domain.dto.SelectProjectWarnDTO;
import com.ruoyi.system.service.ProjectWarnSchemeService;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: shikai
 * @date: 2024/12/12
 * @description:
 */

@Api(tags = "工程预警方案管理")
@RestController
@RequestMapping("/projectWarnScheme")
public class ProjectWarnSchemeController {

    @Resource
    private ProjectWarnSchemeService projectWarnSchemeService;

    @ApiOperation(value = "预警方案新增", notes = "预警方案新增")
    @PostMapping("/addProjectWarnScheme")
    public R<Object> add(@RequestBody ProjectWarnSchemeDTO projectWarnSchemeDTO) {
        return R.ok(this.projectWarnSchemeService.insert(projectWarnSchemeDTO));
    }

    @ApiOperation(value = "预警方案修改", notes = "预警方案修改")
    @PostMapping("/updateProjectWarnScheme")
    public R<Object> update(@RequestBody ProjectWarnSchemeDTO projectWarnSchemeDTO) {
        return R.ok(this.projectWarnSchemeService.update(projectWarnSchemeDTO));
    }

    @ApiOperation(value = "根据id查询", notes = "根据id查询")
    @GetMapping(value = "/getById")
    public R<Object> detail(@ApiParam(name = "projectWarnSchemeId", value = "预警方案id", required = true)
                            @RequestParam Long projectWarnSchemeId) {
        return R.ok(this.projectWarnSchemeService.detail(projectWarnSchemeId));
    }

    @ApiOperation(value = "分页查询", notes = "分页查询")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", defaultValue = "10", dataType = "Integer")
    })
    @GetMapping(value = "/queryPage")
    public R<Object> queryPage(@RequestBody SelectProjectWarnDTO selectProjectWarnDTO,
                               @ApiParam(name = "pageNum", value = "页码", required = true) @RequestParam Integer pageNum,
                               @ApiParam(name = "pageSize", value = "页数", required = true) @RequestParam Integer pageSize){
        return R.ok(this.projectWarnSchemeService.queryByPage(selectProjectWarnDTO, pageNum, pageSize));
    }

    @ApiOperation(value = "删除预警方案", notes = "删除预警方案")
    @DeleteMapping(value = "/delete")
    public R<Object> delete(@ApiParam(name = "projectWarnSchemeIds", value = "预警方案id数组", required = true)
                            @RequestParam Long[] projectWarnSchemeIds) {
        return R.ok(this.projectWarnSchemeService.deleteById(projectWarnSchemeIds));
    }
}