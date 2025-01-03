package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.Entity.ParameterValidationOther;
import com.ruoyi.system.domain.dto.DepartAuditDTO;
import com.ruoyi.system.domain.dto.SelectDeptAuditDTO;
import com.ruoyi.system.domain.dto.TeamAuditDTO;
import com.ruoyi.system.domain.dto.project.DepartmentAuditDTO;
import com.ruoyi.system.domain.vo.BizProjectRecordDetailVo;
import com.ruoyi.system.service.DepartmentAuditService;
import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: shikai
 * @date: 2024/11/26
 * @description:
 */

@Api(tags = "科室审核")
@RestController
@RequestMapping("/departAudit")
public class DepartAuditController {

    @Resource
    private DepartmentAuditService departmentAuditService;

    @ApiOperation(value = "点击审核按钮", notes = "点击审核按钮")
    @GetMapping(value = "/clickAudit")
    public R<DepartmentAuditDTO> audit(@ApiParam(name = "projectId", value = "项目填报id", required = true) @RequestParam Long projectId) {
        return R.ok(this.departmentAuditService.clickAudit(projectId));
    }

    @ApiOperation(value = "审核", notes = "审核")
    @PostMapping(value = "/addAudit")
    public R<Object> addTeamAudit(@RequestBody @Validated(ParameterValidationOther.class) DepartAuditDTO departAuditDTO) {
        return R.ok(this.departmentAuditService.departmentAudit(departAuditDTO));
    }

    @ApiOperation(value = "分页查询", notes = "分页查询")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", defaultValue = "10", dataType = "Integer")
    })
    @PostMapping(value = "/queryPage")
    public R<Object> queryByPage(@RequestBody SelectDeptAuditDTO selectDeptAuditDTO,
                                 @ApiParam(name = "pageNum", value = "页码", required = true) @RequestParam Integer pageNum,
                                 @ApiParam(name = "pageSize", value = "每页显示条数", required = true) @RequestParam Integer pageSize) {
        return R.ok(this.departmentAuditService.queryByPage(selectDeptAuditDTO, pageNum, pageSize));
    }


}