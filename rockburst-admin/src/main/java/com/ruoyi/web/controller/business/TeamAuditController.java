package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.Entity.ParameterValidationOther;
import com.ruoyi.system.domain.dto.SelectProjectDTO;
import com.ruoyi.system.domain.dto.TeamAuditDTO;
import com.ruoyi.system.service.TeamAuditService;
import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: shikai
 * @date: 2024/11/26
 * @description:
 */

@Api(tags = "区队审核")
@RestController
@RequestMapping("/teamAudit")
public class TeamAuditController {

    @Resource
    private TeamAuditService teamAuditService;

    @ApiOperation(value = "点击审核按钮", notes = "点击审核按钮")
    @RequestMapping("/clickAudit")
    public R<Object> audit(@ApiParam(name = "projectId", value = "项目填报id", required = true) @RequestParam Long projectId) {
        return R.ok(this.teamAuditService.audit(projectId));
    }

    @ApiOperation(value = "审核", notes = "审核")
    @RequestMapping("/addAudit")
    public R<Object> addTeamAudit(@RequestBody @Validated(ParameterValidationOther.class) TeamAuditDTO teamAuditDTO) {
        return R.ok(this.teamAuditService.addTeamAudit(teamAuditDTO));
    }

    @ApiOperation(value = "分页查询", notes = "分页查询")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", defaultValue = "10", dataType = "Integer")
    })
    @RequestMapping("/queryPage")
    public R<Object> queryPage(@RequestBody SelectProjectDTO selectProjectDTO,
                               @ApiParam(name = "pageNum", value = "页码", required = true) @RequestParam Integer pageNum,
                               @ApiParam(name = "pageSize", value = "页数", required = true) @RequestParam Integer pageSize) {
        return R.ok(this.teamAuditService.queryByPage(selectProjectDTO, pageNum, pageSize));
    }
}